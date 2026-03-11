package co.edu.unipiloto.fuelmanager.stations;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.alerts.AlertsActivity;
import co.edu.unipiloto.fuelmanager.alerts.PriceAlertNotifier;
import co.edu.unipiloto.fuelmanager.data.local.DatabaseHelper;
import co.edu.unipiloto.fuelmanager.data.model.PriceAlert;
import co.edu.unipiloto.fuelmanager.data.model.Station;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;

public class StationListActivity extends AppCompatActivity {

    private Spinner          spinnerZone;
    private RecyclerView     recycler;
    private StationAdapter   adapter;
    private TextView         tvCount;

    private StationRepository  repo;
    private DatabaseHelper     db;
    private SessionManager     session;
    private PriceAlertNotifier notifier;

    private List<Station> allStations = new ArrayList<>();
    private static final NumberFormat COP = NumberFormat.getInstance(new Locale("es", "CO"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);

        repo     = new StationRepository(this);
        db       = DatabaseHelper.getInstance(this);
        session  = new SessionManager(this);
        notifier = new PriceAlertNotifier(this);

        spinnerZone = findViewById(R.id.spinnerZone);
        recycler    = findViewById(R.id.recyclerStations);
        tvCount     = findViewById(R.id.tvStationCount);

        setupRecycler();
        setupZoneFilter();

        MaterialButton btnAlerts = findViewById(R.id.btnAlerts);
        if (btnAlerts != null) {
            btnAlerts.setOnClickListener(v ->
                    startActivity(new Intent(this, AlertsActivity.class)));
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        checkPriceChangesAndNotify();
    }

    private void setupRecycler() {
        adapter = new StationAdapter(new ArrayList<>(), this::showAlertDialog);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
        loadStations("Todas");
    }

    private void setupZoneFilter() {
        String[] zones = {"Todas", "Bogotá Norte", "Bogotá Sur",
                "Bogotá Centro", "Bogotá Occidente"};
        ArrayAdapter<String> za = new ArrayAdapter<>(this,
                R.layout.spinner_item, zones);
        za.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerZone.setAdapter(za);
        spinnerZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                loadStations(zones[pos]);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void loadStations(String zone) {
        new Thread(() -> {
            allStations = repo.getStationsSortedByPrice(zone);
            runOnUiThread(() -> {
                adapter.updateData(allStations);
                tvCount.setText(allStations.size() + " estaciones");
            });
        }).start();
    }

    private void showAlertDialog(Station station) {
        int userId = session.getUserId();

        // Los precios van dentro de los ítems — setMessage y setItems no pueden coexistir
        final String[] fuelLabels = {
                "🟠 Corriente  $" + COP.format(station.getPriceCorriente()) + "/gal",
                "🟡 Extra      $" + COP.format(station.getPriceExtra())     + "/gal",
                "🔵 ACPM       $" + COP.format(station.getPriceAcpm())      + "/gal"
        };
        final String[] fuelNames = {"Corriente", "Extra", "ACPM"};

        new AlertDialog.Builder(this, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle("🔔 " + station.getName() + "\nSelecciona el combustible a vigilar")
                .setItems(fuelLabels, (dialog, which) -> {
                    String fuel = fuelNames[which];
                    boolean active = db.isAlertActive(station.getId(), fuel, userId);

                    if (active) {
                        new AlertDialog.Builder(this,
                                androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert)
                                .setTitle("Alerta activa")
                                .setMessage("Ya tienes una alerta para " + fuel
                                        + " en " + station.getName()
                                        + ".\n\n¿Deseas desactivarla?")
                                .setPositiveButton("Desactivar", (d2, w2) -> {
                                    db.deactivateAlert(station.getId(), fuel, userId);
                                    Toast.makeText(this,
                                            "Alerta desactivada", Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("Mantener", null)
                                .show();
                    } else {
                        double refPrice;
                        switch (fuel) {
                            case "Extra": refPrice = station.getPriceExtra(); break;
                            case "ACPM":  refPrice = station.getPriceAcpm();  break;
                            default:      refPrice = station.getPriceCorriente(); break;
                        }
                        PriceAlert alert = new PriceAlert(
                                station.getId(), station.getName(),
                                fuel, refPrice, userId);
                        db.upsertAlert(alert);
                        Toast.makeText(this,
                                "✔ Alerta activada para " + fuel + " en " + station.getName(),
                                Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void checkPriceChangesAndNotify() {
        int userId = session.getUserId();
        new Thread(() -> notifier.checkAndNotify(userId)).start();
    }
}