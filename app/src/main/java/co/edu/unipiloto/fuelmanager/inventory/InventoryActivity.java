package co.edu.unipiloto.fuelmanager.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import co.edu.unipiloto.fuelmanager.data.model.InventoryMovement;
import co.edu.unipiloto.fuelmanager.data.model.InventoryStock;
import co.edu.unipiloto.fuelmanager.normative.NormativePriceActivity;
import co.edu.unipiloto.fuelmanager.normative.NormativePriceRepository;
import co.edu.unipiloto.fuelmanager.utils.ApiClient;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;

public class InventoryActivity extends AppCompatActivity {

    private NormativePriceRepository normativeRepo;
    private TextView tvStockCorriente, tvStockExtra, tvStockAcpm;
    private TextView tvAlertCorriente, tvAlertExtra, tvAlertAcpm;
    private Spinner spinnerFuel, spinnerMovType;
    private TextInputEditText etVolume, etNote;
    private MaterialButton btnEntrada, btnSalida, btnRegistrar;
    private RecyclerView recyclerMovements;
    private InventoryMovAdapter adapter;

    private InventoryRepository repository;
    private int stationId;
    private String selectedMovType = InventoryMovement.TYPE_ENTRADA;

    private static final double STOCK_MIN_GAL = 50.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        SessionManager session = new SessionManager(this);
        int userId = session.getUserId();

        // getStationIdByUserId ahora retorna userId como fallback (ver ApiClient)
        stationId = ApiClient.getStationIdByUserId(userId);

        repository = new InventoryRepository(this);

        bindViews();
        setupSpinners();
        setupMovTypeToggle();
        setupRecycler();
        loadData();

        btnRegistrar.setOnClickListener(v -> registrarMovimiento());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnIrAPrecios).setOnClickListener(v ->
                startActivity(new Intent(this, NormativePriceActivity.class)));
    }

    private void bindViews() {
        tvStockCorriente  = findViewById(R.id.tvStockCorriente);
        tvStockExtra      = findViewById(R.id.tvStockExtra);
        tvStockAcpm       = findViewById(R.id.tvStockAcpm);
        tvAlertCorriente  = findViewById(R.id.tvAlertCorriente);
        tvAlertExtra      = findViewById(R.id.tvAlertExtra);
        tvAlertAcpm       = findViewById(R.id.tvAlertAcpm);
        spinnerFuel       = findViewById(R.id.spinnerFuel);
        spinnerMovType    = findViewById(R.id.spinnerMovType);
        etVolume          = findViewById(R.id.etVolume);
        etNote            = findViewById(R.id.etNote);
        btnEntrada        = findViewById(R.id.btnTypeEntrada);
        btnSalida         = findViewById(R.id.btnTypeSalida);
        btnRegistrar      = findViewById(R.id.btnRegistrar);
        recyclerMovements = findViewById(R.id.recyclerMovements);
        normativeRepo     = new NormativePriceRepository(this);
    }

    private void setupSpinners() {
        String[] fuels = {
                InventoryMovement.FUEL_CORRIENTE,
                InventoryMovement.FUEL_EXTRA,
                InventoryMovement.FUEL_ACPM
        };
        ArrayAdapter<String> fa = new ArrayAdapter<>(this, R.layout.spinner_item, fuels);
        fa.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerFuel.setAdapter(fa);
    }

    private void setupMovTypeToggle() {
        setMovType(InventoryMovement.TYPE_ENTRADA);
        btnEntrada.setOnClickListener(v -> setMovType(InventoryMovement.TYPE_ENTRADA));
        btnSalida.setOnClickListener(v  -> setMovType(InventoryMovement.TYPE_SALIDA));
    }

    private void setMovType(String type) {
        selectedMovType = type;
        boolean isEntrada = type.equals(InventoryMovement.TYPE_ENTRADA);

        btnEntrada.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(isEntrada ? 0xFF2E7D32 : 0xFF1E2A3A));
        btnEntrada.setTextColor(isEntrada ? 0xFFFFFFFF : 0xFF78909C);

        btnSalida.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(!isEntrada ? 0xFFC62828 : 0xFF1E2A3A));
        btnSalida.setTextColor(!isEntrada ? 0xFFFFFFFF : 0xFF78909C);
    }

    private void setupRecycler() {
        adapter = new InventoryMovAdapter(new ArrayList<>());
        recyclerMovements.setLayoutManager(new LinearLayoutManager(this));
        recyclerMovements.setAdapter(adapter);
    }

    private void loadData() {
        new Thread(() -> {
            InventoryStock stock = repository.getCurrentStock(stationId);
            List<InventoryMovement> movs = repository.getMovements(stationId);
            runOnUiThread(() -> {
                updateStockCards(stock);
                adapter.updateData(movs);
            });
        }).start();
    }

    private void updateStockCards(InventoryStock stock) {
        tvStockCorriente.setText(formatGal(stock.getCorrienteGal()));
        tvStockExtra.setText(formatGal(stock.getExtraGal()));
        tvStockAcpm.setText(formatGal(stock.getAcpmGal()));
        setAlert(tvAlertCorriente, stock.getCorrienteGal());
        setAlert(tvAlertExtra,     stock.getExtraGal());
        setAlert(tvAlertAcpm,      stock.getAcpmGal());
    }

    private void setAlert(TextView tvAlert, double stock) {
        if (stock < STOCK_MIN_GAL) {
            tvAlert.setVisibility(View.VISIBLE);
            tvAlert.setText("⚠ Stock bajo");
        } else {
            tvAlert.setVisibility(View.GONE);
        }
    }

    private void registrarMovimiento() {
        String volumeStr = etVolume.getText() != null ? etVolume.getText().toString().trim() : "";
        String note      = etNote.getText()   != null ? etNote.getText().toString().trim()   : "";
        String fuel      = spinnerFuel.getSelectedItem().toString();

        if (TextUtils.isEmpty(volumeStr)) { etVolume.setError("Ingresa el volumen"); return; }

        double volume;
        try {
            volume = Double.parseDouble(volumeStr);
        } catch (NumberFormatException e) { etVolume.setError("Número inválido"); return; }

        if (volume <= 0) { etVolume.setError("El volumen debe ser mayor a 0"); return; }

        // Verificar stock antes de salida — necesita hilo
        if (selectedMovType.equals(InventoryMovement.TYPE_SALIDA)) {
            new Thread(() -> {
                InventoryStock stock = repository.getCurrentStock(stationId);
                if (volume > stock.getStock(fuel)) {
                    runOnUiThread(() -> Toast.makeText(this,
                            "⚠ Stock insuficiente de " + fuel + ": solo hay " +
                                    formatGal(stock.getStock(fuel)),
                            Toast.LENGTH_LONG).show());
                } else {
                    insertarMovimiento(fuel, volume, note);
                }
            }).start();
        } else {
            insertarMovimiento(fuel, volume, note);
        }
    }

    private void insertarMovimiento(String fuel, double volume, String note) {
        InventoryMovement mov = new InventoryMovement(
                fuel, selectedMovType, volume, note,
                new Date().toString(), stationId);

        new Thread(() -> {
            long id = repository.insertMovement(mov);
            runOnUiThread(() -> {
                if (id > 0) {
                    Toast.makeText(this,
                            selectedMovType + " de " + volume + " gal registrada ✓",
                            Toast.LENGTH_SHORT).show();
                    etVolume.setText("");
                    etNote.setText("");
                    loadData();
                } else {
                    Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private String formatGal(double gal) {
        return String.format(Locale.getDefault(), "%.1f gal", gal);
    }
}