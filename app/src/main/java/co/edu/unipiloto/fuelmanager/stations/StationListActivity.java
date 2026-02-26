package co.edu.unipiloto.fuelmanager.stations;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.model.Station;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;

public class StationListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StationAdapter adapter;
    private StationRepository repository;
    private Spinner spinnerZone;
    private TextView tvCount;

    private final List<Station> stationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);

        repository = new StationRepository(this);

        recyclerView = findViewById(R.id.recyclerStations);
        spinnerZone  = findViewById(R.id.spinnerZone);
        tvCount      = findViewById(R.id.tvCount);

        // Saludo con nombre del usuario
        SessionManager session = new SessionManager(this);
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("Hola, " + session.getUserName());

        // RecyclerView
        adapter = new StationAdapter(stationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setupZoneFilter();
        loadStations(null);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void setupZoneFilter() {
        String[] zones = {"Todas las zonas", "Bogotá Norte", "Bogotá Sur",
                "Bogotá Centro", "Bogotá Occidente", "Bogotá Oriente"};
        ArrayAdapter<String> za = new ArrayAdapter<>(this,
                R.layout.spinner_item, zones);
        za.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerZone.setAdapter(za);

        spinnerZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                if (pos == 0) loadStations(null);
                else loadStations(zones[pos]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void loadStations(String zone) {
        new Thread(() -> {
            List<Station> result = (zone == null)
                    ? repository.getAllOrderedByPrice()
                    : repository.getByZone(zone);

            runOnUiThread(() -> {
                adapter.updateData(result);
                tvCount.setText(result.size() + " estaciones encontradas");
            });
        }).start();
    }
}