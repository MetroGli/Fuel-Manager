package co.edu.unipiloto.fuelmanager.alerts;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.local.DatabaseHelper;
import co.edu.unipiloto.fuelmanager.data.model.PriceAlert;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;

public class AlertsActivity extends AppCompatActivity {

    private RecyclerView   recycler;
    private AlertsAdapter  adapter;
    private TextView       tvEmpty;
    private DatabaseHelper db;
    private int            userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        db     = DatabaseHelper.getInstance(this);
        userId = new SessionManager(this).getUserId();

        recycler = findViewById(R.id.recyclerAlerts);
        tvEmpty  = findViewById(R.id.tvAlertsEmpty);

        adapter = new AlertsAdapter(new ArrayList<>(), (alert, activate) -> {
            if (activate) {
                alert.setActive(true);
                db.upsertAlert(alert);
            } else {
                db.deactivateAlert(alert.getStationId(), alert.getFuelType(), userId);
            }
            loadAlerts();
        });

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAlerts();
    }

    private void loadAlerts() {
        new Thread(() -> {
            List<PriceAlert> alerts = db.getActiveAlerts(userId);
            runOnUiThread(() -> {
                adapter.updateData(alerts);
                tvEmpty.setVisibility(alerts.isEmpty() ? View.VISIBLE : View.GONE);
                recycler.setVisibility(alerts.isEmpty() ? View.GONE : View.VISIBLE);
            });
        }).start();
    }
}