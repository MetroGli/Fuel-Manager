package co.edu.unipiloto.fuelmanager.authority;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.local.DatabaseHelper;
import co.edu.unipiloto.fuelmanager.data.model.Station;
import co.edu.unipiloto.fuelmanager.stations.StationAdapter;

public class PriceMonitorActivity extends AppCompatActivity {

    private Spinner spinnerZone;
    private RecyclerView recycler;
    private StationAdapter adapter;
    private TextView tvSummary;
    private DatabaseHelper db;
    private static final NumberFormat COP = NumberFormat.getInstance(new Locale("es", "CO"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_monitor);

        db = DatabaseHelper.getInstance(this);
        spinnerZone = findViewById(R.id.spinnerMonitorZone);
        recycler    = findViewById(R.id.recyclerMonitor);
        tvSummary   = findViewById(R.id.tvMonitorSummary);

        adapter = new StationAdapter(new ArrayList<>(), null);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        setupZoneSpinner();
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void setupZoneSpinner() {
        String[] zones = {"Todas", "Bogotá Norte", "Bogotá Sur", "Bogotá Centro", "Bogotá Occidente"};
        ArrayAdapter<String> za = new ArrayAdapter<>(this, R.layout.spinner_item, zones);
        za.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerZone.setAdapter(za);
        spinnerZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                loadStations(zones[pos]);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void loadStations(String zone) {
        new Thread(() -> {
            List<Station> stations = zone.equals("Todas")
                    ? db.getAllStations()
                    : db.getStationsByZone(zone);
            // Calcular estadísticas
            if (!stations.isEmpty()) {
                double minC=Double.MAX_VALUE, maxC=0, sumC=0;
                double minE=Double.MAX_VALUE, maxE=0;
                double minA=Double.MAX_VALUE, maxA=0;
                for (Station s : stations) {
                    minC = Math.min(minC, s.getPriceCorriente()); maxC = Math.max(maxC, s.getPriceCorriente()); sumC+=s.getPriceCorriente();
                    minE = Math.min(minE, s.getPriceExtra());     maxE = Math.max(maxE, s.getPriceExtra());
                    minA = Math.min(minA, s.getPriceAcpm());      maxA = Math.max(maxA, s.getPriceAcpm());
                }
                double avgC = sumC / stations.size();
                String summary = stations.size() + " estaciones · Corriente: $" + COP.format(minC) +
                        " – $" + COP.format(maxC) + " (prom $" + COP.format(avgC) + ")\n" +
                        "Extra: $" + COP.format(minE) + " – $" + COP.format(maxE) +
                        "  ·  ACPM: $" + COP.format(minA) + " – $" + COP.format(maxA);
                runOnUiThread(() -> { adapter.updateData(stations); tvSummary.setText(summary); });
            } else {
                runOnUiThread(() -> { adapter.updateData(stations); tvSummary.setText("Sin estaciones en esta zona"); });
            }
        }).start();
    }
}
