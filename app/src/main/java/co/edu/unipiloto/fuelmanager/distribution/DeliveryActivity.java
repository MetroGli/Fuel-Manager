package co.edu.unipiloto.fuelmanager.distribution;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.local.DatabaseHelper;
import co.edu.unipiloto.fuelmanager.data.model.Delivery;
import co.edu.unipiloto.fuelmanager.data.model.Station;
import co.edu.unipiloto.fuelmanager.data.model.InventoryMovement;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;

public class DeliveryActivity extends AppCompatActivity {

    private Spinner           spinnerStation;
    private Spinner           spinnerFuel;
    private TextInputEditText etVolume, etNotes;
    private MaterialButton    btnRegistrar;
    private RecyclerView      recyclerDeliveries;
    private DeliveryAdapter   adapter;

    private DatabaseHelper    db;
    private SessionManager    session;
    private List<Station> stations = new ArrayList<>(); // [id, name, address]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        db      = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);

        bindViews();
        loadStationsSpinner();
        setupFuelSpinner();
        setupRecycler();
        loadDeliveries();

        btnRegistrar.setOnClickListener(v -> registrarEntrega());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void bindViews() {
        spinnerStation     = findViewById(R.id.spinnerStation);
        spinnerFuel        = findViewById(R.id.spinnerFuelDelivery);
        etVolume           = findViewById(R.id.etDeliveryVolume);
        etNotes            = findViewById(R.id.etDeliveryNotes);
        btnRegistrar       = findViewById(R.id.btnRegistrarEntrega);
        recyclerDeliveries = findViewById(R.id.recyclerDeliveries);
    }

    private void loadStationsSpinner() {
        new Thread(() -> {
            stations = db.getAllStationsSimple();

            List<String> names = new ArrayList<>();
            for (Station s : stations) {
                names.add(s.getName() + " · " + s.getAddress());
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> sa = new ArrayAdapter<>(this,
                        R.layout.spinner_item, names);
                sa.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinnerStation.setAdapter(sa);
            });
        }).start();
    }

    private void setupFuelSpinner() {
        String[] fuels = {
                InventoryMovement.FUEL_CORRIENTE,
                InventoryMovement.FUEL_EXTRA,
                InventoryMovement.FUEL_ACPM
        };
        ArrayAdapter<String> fa = new ArrayAdapter<>(this,
                R.layout.spinner_item, fuels);
        fa.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerFuel.setAdapter(fa);
    }

    private void setupRecycler() {
        adapter = new DeliveryAdapter(new ArrayList<>());
        recyclerDeliveries.setLayoutManager(new LinearLayoutManager(this));
        recyclerDeliveries.setAdapter(adapter);
    }

    private void registrarEntrega() {
        if (stations.isEmpty()) {
            Toast.makeText(this, "No hay estaciones disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        String volStr = etVolume.getText() != null
                ? etVolume.getText().toString().trim() : "";
        String notes  = etNotes.getText() != null
                ? etNotes.getText().toString().trim() : "";

        if (TextUtils.isEmpty(volStr)) {
            etVolume.setError("Ingresa el volumen");
            return;
        }

        double volume;
        try {
            volume = Double.parseDouble(volStr);
        } catch (NumberFormatException e) {
            etVolume.setError("Número inválido");
            return;
        }

        if (volume <= 0) {
            etVolume.setError("Debe ser mayor a 0");
            return;
        }

        int selectedPos = spinnerStation.getSelectedItemPosition();
        if (selectedPos < 0 || selectedPos >= stations.size()) return;

        Station station = stations.get(selectedPos);
        int    stationId   = station.getId();
        String stationName = station.getName();
        String fuel        = spinnerFuel.getSelectedItem().toString();
        int    distId      = session.getUserId();

        new Thread(() -> {
            Delivery delivery = new Delivery(
                    stationId, stationName, fuel,
                    volume, new Date().toString(),
                    notes, distId);

            long id = db.insertDelivery(delivery);
            loadDeliveries();

            runOnUiThread(() -> {
                Toast.makeText(this,
                        "✔ Entrega #" + id + " registrada en " + stationName,
                        Toast.LENGTH_LONG).show();
                etVolume.setText("");
                etNotes.setText("");
            });
        }).start();
    }

    private void loadDeliveries() {
        new Thread(() -> {
            List<Delivery> list = db.getDeliveries(session.getUserId());
            runOnUiThread(() -> adapter.updateData(list));
        }).start();
    }
}