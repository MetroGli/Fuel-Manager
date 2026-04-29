package co.edu.unipiloto.fuelmanager.distribution;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.local.DatabaseHelper;
import co.edu.unipiloto.fuelmanager.data.model.PriceUpdate;
import co.edu.unipiloto.fuelmanager.data.model.Station;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;

public class PriceUpdateActivity extends AppCompatActivity {

    private Spinner         spinnerStation;
    private EditText        etCorriente, etExtra, etAcpm;
    private TextView        tvCurrentCorriente, tvCurrentExtra, tvCurrentAcpm;
    private ImageButton btnBack;
    private Button          btnUpdate;
    private RecyclerView    recyclerHistory;

    private DatabaseHelper  db;
    private SessionManager  session;
    private List<Station>   stations = new ArrayList<>();
    private Station         selectedStation;
    private PriceUpdateHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_update);

        db      = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);

        bindViews();
        loadStations();
        loadHistory();

        btnBack.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> doUpdate());
    }

    private void bindViews() {
        spinnerStation      = findViewById(R.id.spinnerStation);
        etCorriente         = findViewById(R.id.etCorriente);
        etExtra             = findViewById(R.id.etExtra);
        etAcpm              = findViewById(R.id.etAcpm);
        tvCurrentCorriente  = findViewById(R.id.tvCurrentCorriente);
        tvCurrentExtra      = findViewById(R.id.tvCurrentExtra);
        tvCurrentAcpm       = findViewById(R.id.tvCurrentAcpm);
        btnUpdate           = findViewById(R.id.btnUpdate);
        btnBack             = findViewById(R.id.btnBack);
        recyclerHistory     = findViewById(R.id.recyclerHistory);

        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PriceUpdateHistoryAdapter(new ArrayList<>());
        recyclerHistory.setAdapter(adapter);
    }

    private void loadStations() {
        stations = db.getAllStationsSimple();

        List<String> names = new ArrayList<>();
        for (Station s : stations) names.add(s.getName());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, names);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStation.setAdapter(spinnerAdapter);

        spinnerStation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                selectedStation = stations.get(pos);
                showCurrentPrices(selectedStation);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        if (!stations.isEmpty()) {
            selectedStation = stations.get(0);
            showCurrentPrices(selectedStation);
        }
    }

    private void showCurrentPrices(Station s) {
        tvCurrentCorriente.setText(String.format("Actual: $%.0f/gal", s.getPriceCorriente()));
        tvCurrentExtra.setText(String.format("Actual: $%.0f/gal", s.getPriceExtra()));
        tvCurrentAcpm.setText(String.format("Actual: $%.0f/gal", s.getPriceAcpm()));

        // Pre-llenar con precios actuales
        etCorriente.setText(String.valueOf((int) s.getPriceCorriente()));
        etExtra.setText(String.valueOf((int) s.getPriceExtra()));
        etAcpm.setText(String.valueOf((int) s.getPriceAcpm()));
    }

    private void doUpdate() {
        if (selectedStation == null) return;

        String sCor = etCorriente.getText().toString().trim();
        String sExt = etExtra.getText().toString().trim();
        String sAcp = etAcpm.getText().toString().trim();

        if (sCor.isEmpty() || sExt.isEmpty() || sAcp.isEmpty()) {
            Toast.makeText(this, "Completa todos los precios", Toast.LENGTH_SHORT).show();
            return;
        }

        double newCor, newExt, newAcp;
        try {
            newCor = Double.parseDouble(sCor);
            newExt = Double.parseDouble(sExt);
            newAcp = Double.parseDouble(sAcp);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Precios inválidos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newCor <= 0 || newExt <= 0 || newAcp <= 0) {
            Toast.makeText(this, "Los precios deben ser mayores a 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardar registro antes de actualizar
        PriceUpdate pu = new PriceUpdate(
                selectedStation.getId(),
                selectedStation.getName(),
                selectedStation.getPriceCorriente(), newCor,
                selectedStation.getPriceExtra(),     newExt,
                selectedStation.getPriceAcpm(),      newAcp,
                new Date().toString(),
                session.getUserId()
        );
        db.insertPriceUpdate(pu);

        // Actualizar precios en la tabla de estaciones
        boolean ok = db.updateStationPrices(selectedStation.getId(), newCor, newExt, newAcp);

        if (ok) {
            Toast.makeText(this, "✓ Precios actualizados", Toast.LENGTH_SHORT).show();
            // Recargar estación con nuevos precios
            selectedStation = db.getStationById(selectedStation.getId());
            showCurrentPrices(selectedStation);
            loadHistory();
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadHistory() {
        List<PriceUpdate> history = db.getAllPriceUpdates();
        adapter.updateData(history);
    }
}