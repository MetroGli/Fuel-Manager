package co.edu.unipiloto.fuelmanager.sales;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
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
import co.edu.unipiloto.fuelmanager.data.model.Receipt;
import co.edu.unipiloto.fuelmanager.data.model.Station;
import co.edu.unipiloto.fuelmanager.data.model.Subsidy;
import co.edu.unipiloto.fuelmanager.data.model.User;
import co.edu.unipiloto.fuelmanager.inventory.InventoryRepository;
import co.edu.unipiloto.fuelmanager.utils.Roles;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;

public class SalesActivity extends AppCompatActivity {

    private Spinner           spinnerFuel;
    private Spinner           spinnerClient;
    private TextInputEditText etVolume, etPricePerGal, etPlate;
    private TextView          tvTotal, tvStockInfo, tvSubsidyInfo;
    private MaterialButton    btnRegistrar;
    private RecyclerView      recyclerSales;
    private RecyclerView      recyclerReceipts;
    private SalesAdapter      adapter;
    private ReceiptListAdapter receiptAdapter;

    private DatabaseHelper      db;
    private InventoryRepository inventoryRepo;
    private SessionManager      session;
    private int                 stationId;

    private List<User> clientUsers   = new ArrayList<>();
    private User       selectedClient = null;
    // CLAVE: activeSubsidy como campo de clase para que registrarVenta() lo use
    private Subsidy    activeSubsidy  = null;

    private static final NumberFormat COP =
            NumberFormat.getInstance(new Locale("es", "CO"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        session       = new SessionManager(this);
        db            = DatabaseHelper.getInstance(this);
        inventoryRepo = new InventoryRepository(this);

        int userId = session.getUserId();
        stationId  = db.getStationIdByUserId(userId);
        if (stationId < 0) stationId = userId;

        bindViews();
        setupFuelSpinner();
        setupClientSpinner();
        setupRecycler();
        setupPriceCalculator();
        loadSales();
        loadReceipts();

        btnRegistrar.setOnClickListener(v -> registrarVenta());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        MaterialButton btnAllPdf = findViewById(R.id.btnAllReceiptsPdf);
        if (btnAllPdf != null) btnAllPdf.setOnClickListener(v ->
                startActivity(new Intent(this, ReceiptPdfActivity.class)));
    }

    // ── Vistas ──────────────────────────────────────────────────────────────

    private void bindViews() {
        spinnerFuel      = findViewById(R.id.spinnerFuelSale);
        spinnerClient    = findViewById(R.id.spinnerClient);
        etVolume         = findViewById(R.id.etSaleVolume);
        etPricePerGal    = findViewById(R.id.etSalePrice);
        etPlate          = findViewById(R.id.etSalePlate);
        tvTotal          = findViewById(R.id.tvSaleTotal);
        tvStockInfo      = findViewById(R.id.tvStockInfo);
        tvSubsidyInfo    = findViewById(R.id.tvSubsidyInfo);   // id correcto del XML
        btnRegistrar     = findViewById(R.id.btnRegistrarVenta);
        recyclerSales    = findViewById(R.id.recyclerSales);
        recyclerReceipts = findViewById(R.id.recyclerStationReceipts);
    }

    // ── Spinner combustible ──────────────────────────────────────────────────

    private void setupFuelSpinner() {
        String[] fuels = {
                InventoryMovement.FUEL_CORRIENTE,
                InventoryMovement.FUEL_EXTRA,
                InventoryMovement.FUEL_ACPM
        };
        ArrayAdapter<String> fa = new ArrayAdapter<>(this, R.layout.spinner_item, fuels);
        fa.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerFuel.setAdapter(fa);
        spinnerFuel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                updateStockInfo();
                refreshSubsidy();
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    // ── Spinner cliente ──────────────────────────────────────────────────────

    private void setupClientSpinner() {
        new Thread(() -> {
            clientUsers = db.getUsersByRole(Roles.CLIENTE);

            List<String> names = new ArrayList<>();
            names.add("— Venta directa (sin usuario) —");
            for (User u : clientUsers) {
                String vt = (u.getVehicleType() != null && !u.getVehicleType().isEmpty())
                        ? "  [" + u.getVehicleType() + "]" : "";
                names.add(u.getName() + vt);
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> ca = new ArrayAdapter<>(this, R.layout.spinner_item, names);
                ca.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinnerClient.setAdapter(ca);
                spinnerClient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                        selectedClient = (pos == 0) ? null : clientUsers.get(pos - 1);
                        refreshSubsidy();
                    }
                    @Override public void onNothingSelected(AdapterView<?> p) {}
                });
            });
        }).start();
    }

    // ── Subsidio en tiempo real ──────────────────────────────────────────────

    /**
     * Busca subsidio activo y lo guarda en activeSubsidy (campo de clase).
     * Prioridad: userId → vehicleType → zona de la estación.
     * Muestra badge verde si hay subsidio, lo oculta si no.
     */
    private void refreshSubsidy() {
        String fuel = spinnerFuel.getSelectedItem() != null
                ? spinnerFuel.getSelectedItem().toString() : InventoryMovement.FUEL_CORRIENTE;

        new Thread(() -> {
            activeSubsidy = null;

            if (selectedClient != null) {
                // 1. Por userId exacto
                activeSubsidy = db.getActiveSubsidyForUser(
                        String.valueOf(selectedClient.getId()), fuel);
                // 2. Por tipo de vehículo (Carro / Moto / Camión)
                if (activeSubsidy == null
                        && selectedClient.getVehicleType() != null
                        && !selectedClient.getVehicleType().isEmpty()) {
                    activeSubsidy = db.getActiveSubsidyForUser(
                            selectedClient.getVehicleType(), fuel);
                }
            }

            // 3. Por zona de la estación (subsidio regional)
            if (activeSubsidy == null) {
                Station st = db.getStationById(stationId);
                if (st != null && st.getZone() != null && !st.getZone().isEmpty()) {
                    activeSubsidy = db.getActiveSubsidyByZone(st.getZone(), fuel);
                }
            }

            final Subsidy found = activeSubsidy;
            runOnUiThread(() -> {
                if (tvSubsidyInfo == null) return;
                if (found != null) {
                    tvSubsidyInfo.setVisibility(View.VISIBLE);
                    tvSubsidyInfo.setText(
                            "🏷  Subsidio activo: -" + (int) found.getDiscountPct() +
                                    "%  ·  " + found.getTargetType() + ": " + found.getTargetValue());
                } else {
                    tvSubsidyInfo.setVisibility(View.GONE);
                }
                // Actualizar total si ya hay precio ingresado
                recalcTotal();
            });
        }).start();
    }

    // ── Recycler ─────────────────────────────────────────────────────────────

    private void setupRecycler() {
        adapter = new SalesAdapter(new ArrayList<>());
        recyclerSales.setLayoutManager(new LinearLayoutManager(this));
        recyclerSales.setAdapter(adapter);

        // ReceiptListAdapter: botón PDF por ítem abre ReceiptPdfActivity
        receiptAdapter = new ReceiptListAdapter(new ArrayList<>(), receipt -> {
            Intent i = new Intent(this, ReceiptPdfActivity.class);
            i.putExtra(ReceiptPdfActivity.EXTRA_RECEIPT_ID, (int) receipt.getId());
            startActivity(i);
        });
        recyclerReceipts.setLayoutManager(new LinearLayoutManager(this));
        recyclerReceipts.setAdapter(receiptAdapter);
    }

    // ── Calculadora de total ──────────────────────────────────────────────────

    private void setupPriceCalculator() {
        android.text.TextWatcher w = new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int af) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { recalcTotal(); }
            @Override public void afterTextChanged(android.text.Editable s) {}
        };
        etVolume.addTextChangedListener(w);
        etPricePerGal.addTextChangedListener(w);
    }

    private void recalcTotal() {
        try {
            double vol   = Double.parseDouble(etVolume.getText().toString().trim());
            double price = Double.parseDouble(etPricePerGal.getText().toString().trim());
            // Refleja el descuento en el total mostrado
            double precioFinal = (activeSubsidy != null)
                    ? price * (1.0 - activeSubsidy.getDiscountPct() / 100.0)
                    : price;
            tvTotal.setText("Total: $" + COP.format(vol * precioFinal));
        } catch (NumberFormatException e) {
            tvTotal.setText("Total: $0");
        }
    }

    private void updateStockInfo() {
        new Thread(() -> {
            InventoryStock stock = inventoryRepo.getCurrentStock(stationId);
            String fuel = spinnerFuel.getSelectedItem().toString();
            double avail = stock.getStock(fuel);
            runOnUiThread(() -> tvStockInfo.setText("Stock disponible: " +
                    String.format(Locale.getDefault(), "%.1f gal", avail)));
        }).start();
    }

    // ── Registrar venta ───────────────────────────────────────────────────────

    private void registrarVenta() {
        String volStr   = etVolume.getText()      != null ? etVolume.getText().toString().trim() : "";
        String priceStr = etPricePerGal.getText() != null ? etPricePerGal.getText().toString().trim() : "";
        String plate    = etPlate.getText()       != null ? etPlate.getText().toString().trim() : "";
        String fuel     = spinnerFuel.getSelectedItem().toString();

        if (TextUtils.isEmpty(volStr))   { etVolume.setError("Ingresa el volumen"); return; }
        if (TextUtils.isEmpty(priceStr)) { etPricePerGal.setError("Ingresa el precio"); return; }

        double volume, pricePerGal;
        try {
            volume      = Double.parseDouble(volStr);
            pricePerGal = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) { etVolume.setError("Número inválido"); return; }

        if (volume <= 0)      { etVolume.setError("Debe ser mayor a 0"); return; }
        if (pricePerGal <= 0) { etPricePerGal.setError("Debe ser mayor a 0"); return; }

        // Aplicar descuento si hay subsidio activo
        final double precioFinal = (activeSubsidy != null)
                ? pricePerGal * (1.0 - activeSubsidy.getDiscountPct() / 100.0)
                : pricePerGal;

        if (activeSubsidy != null) {
            Toast.makeText(this,
                    "✓ Subsidio -" + (int) activeSubsidy.getDiscountPct() +
                            "% aplicado  →  $" + COP.format(precioFinal) + "/gal",
                    Toast.LENGTH_SHORT).show();
        }

        new Thread(() -> {
            InventoryStock stock = inventoryRepo.getCurrentStock(stationId);
            if (volume > stock.getStock(fuel)) {
                runOnUiThread(() -> Toast.makeText(this,
                        "⚠ Stock insuficiente de " + fuel + ": solo hay " +
                                String.format(Locale.getDefault(), "%.1f gal", stock.getStock(fuel)),
                        Toast.LENGTH_LONG).show());
                return;
            }

            String now   = new Date().toString();
            double total = volume * precioFinal;

            // Guardar venta con precio ya descontado
            FuelSale sale = new FuelSale(fuel, volume, precioFinal, plate, now, stationId);
            long saleId   = db.insertSale(sale);

            db.insertInventoryMovement(fuel, InventoryMovement.TYPE_SALIDA, volume,
                    "Venta #" + saleId + (plate.isEmpty() ? "" : " · " + plate), now, stationId);

            // Recibo con precio descontado
            db.insertReceipt(saleId, fuel, volume, precioFinal, total, plate, now, stationId);

            loadSales();
            loadReceipts();
            runOnUiThread(() -> {
                Toast.makeText(this,
                        "Venta #" + saleId + " registrada ✓  $" + COP.format(total),
                        Toast.LENGTH_LONG).show();
                etVolume.setText(""); etPricePerGal.setText(""); etPlate.setText("");
                tvTotal.setText("Total: $0");
                updateStockInfo();
            });
        }).start();
    }

    // ── Carga de datos ────────────────────────────────────────────────────────

    private void loadSales() {
        new Thread(() -> {
            List<FuelSale> sales = db.getSales(stationId);
            runOnUiThread(() -> adapter.updateData(sales));
        }).start();
    }

    private void loadReceipts() {
        new Thread(() -> {
            List<Receipt> receipts = db.getReceiptsByStation(stationId);
            runOnUiThread(() -> receiptAdapter.updateData(receipts));
        }).start();
    }
}