package co.edu.unipiloto.fuelmanager.sales;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.local.DatabaseHelper;
import co.edu.unipiloto.fuelmanager.data.model.FuelSale;
import co.edu.unipiloto.fuelmanager.data.model.InventoryMovement;
import co.edu.unipiloto.fuelmanager.data.model.InventoryStock;
import co.edu.unipiloto.fuelmanager.inventory.InventoryRepository;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;

public class SalesActivity extends AppCompatActivity {

    private Spinner            spinnerFuel;
    private TextInputEditText  etVolume, etPricePerGal, etPlate;
    private TextView           tvTotal, tvStockInfo;
    private MaterialButton     btnRegistrar;
    private RecyclerView       recyclerSales;
    private SalesAdapter       adapter;

    private DatabaseHelper     db;
    private InventoryRepository inventoryRepo;
    private SessionManager     session;
    private int                stationId;

    private static final NumberFormat COP =
            NumberFormat.getInstance(new Locale("es", "CO"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        session       = new SessionManager(this);
        stationId     = session.getUserId();
        db            = DatabaseHelper.getInstance(this);
        inventoryRepo = new InventoryRepository(this);

        bindViews();
        setupSpinner();
        setupRecycler();
        setupPriceCalculator();
        loadSales();

        btnRegistrar.setOnClickListener(v -> registrarVenta());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void bindViews() {
        spinnerFuel  = findViewById(R.id.spinnerFuelSale);
        etVolume     = findViewById(R.id.etSaleVolume);
        etPricePerGal= findViewById(R.id.etSalePrice);
        etPlate      = findViewById(R.id.etSalePlate);
        tvTotal      = findViewById(R.id.tvSaleTotal);
        tvStockInfo  = findViewById(R.id.tvStockInfo);
        btnRegistrar = findViewById(R.id.btnRegistrarVenta);
        recyclerSales= findViewById(R.id.recyclerSales);
    }

    private void setupSpinner() {
        String[] fuels = {
                InventoryMovement.FUEL_CORRIENTE,
                InventoryMovement.FUEL_EXTRA,
                InventoryMovement.FUEL_ACPM
        };
        ArrayAdapter<String> fa = new ArrayAdapter<>(this, R.layout.spinner_item, fuels);
        fa.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerFuel.setAdapter(fa);

        // Al cambiar combustible, mostrar stock disponible
        spinnerFuel.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> p, android.view.View v, int pos, long id) {
                updateStockInfo();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> p) {}
        });
    }

    private void setupRecycler() {
        adapter = new SalesAdapter(new ArrayList<>());
        recyclerSales.setLayoutManager(new LinearLayoutManager(this));
        recyclerSales.setAdapter(adapter);
    }

    /** Recalcula el total en tiempo real al cambiar volumen o precio. */
    private void setupPriceCalculator() {
        android.text.TextWatcher watcher = new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int af) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { recalcTotal(); }
            @Override public void afterTextChanged(android.text.Editable s) {}
        };
        etVolume.addTextChangedListener(watcher);
        etPricePerGal.addTextChangedListener(watcher);
    }

    private void recalcTotal() {
        try {
            double vol   = Double.parseDouble(etVolume.getText().toString().trim());
            double price = Double.parseDouble(etPricePerGal.getText().toString().trim());
            tvTotal.setText("Total: $" + COP.format(vol * price));
        } catch (NumberFormatException e) {
            tvTotal.setText("Total: $0");
        }
    }

    private void updateStockInfo() {
        new Thread(() -> {
            InventoryStock stock = inventoryRepo.getCurrentStock(stationId);
            String fuel = spinnerFuel.getSelectedItem().toString();
            double available = stock.getStock(fuel);
            runOnUiThread(() ->
                    tvStockInfo.setText("Stock disponible: " +
                            String.format(Locale.getDefault(), "%.1f gal", available))
            );
        }).start();
    }

    private void registrarVenta() {
        String volStr   = etVolume.getText()     != null ? etVolume.getText().toString().trim()      : "";
        String priceStr = etPricePerGal.getText() != null ? etPricePerGal.getText().toString().trim() : "";
        String plate    = etPlate.getText()       != null ? etPlate.getText().toString().trim()       : "";
        String fuel     = spinnerFuel.getSelectedItem().toString();

        if (TextUtils.isEmpty(volStr)) {
            etVolume.setError("Ingresa el volumen");
            return;
        }
        if (TextUtils.isEmpty(priceStr)) {
            etPricePerGal.setError("Ingresa el precio por galón");
            return;
        }

        double volume, pricePerGal;
        try {
            volume      = Double.parseDouble(volStr);
            pricePerGal = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            etVolume.setError("Número inválido");
            return;
        }

        if (volume <= 0) { etVolume.setError("Debe ser mayor a 0"); return; }
        if (pricePerGal <= 0) { etPricePerGal.setError("Debe ser mayor a 0"); return; }

        new Thread(() -> {
            // 1. Verificar stock
            InventoryStock stock = inventoryRepo.getCurrentStock(stationId);
            if (volume > stock.getStock(fuel)) {
                runOnUiThread(() -> Toast.makeText(this,
                        "⚠ Stock insuficiente de " + fuel + ": solo hay " +
                                String.format(Locale.getDefault(), "%.1f gal", stock.getStock(fuel)),
                        Toast.LENGTH_LONG).show());
                return;
            }

            String now = new Date().toString();

            // 2. Registrar la venta
            FuelSale sale = new FuelSale(fuel, volume, pricePerGal, plate, now, stationId);
            long saleId = db.insertSale(sale);

            // 3. Descontar automáticamente del inventario (SALIDA)
            db.insertInventoryMovement(
                    fuel,
                    InventoryMovement.TYPE_SALIDA,
                    volume,
                    "Venta #" + saleId + (plate.isEmpty() ? "" : " · " + plate),
                    now,
                    stationId
            );

            loadSales();

            runOnUiThread(() -> {
                Toast.makeText(this,
                        "Venta registrada ✓ · Total: $" + COP.format(volume * pricePerGal),
                        Toast.LENGTH_LONG).show();
                etVolume.setText("");
                etPricePerGal.setText("");
                etPlate.setText("");
                tvTotal.setText("Total: $0");
                updateStockInfo();
            });
        }).start();
    }

    private void loadSales() {
        new Thread(() -> {
            List<FuelSale> sales = db.getSales(stationId);
            runOnUiThread(() -> adapter.updateData(sales));
        }).start();
    }
}