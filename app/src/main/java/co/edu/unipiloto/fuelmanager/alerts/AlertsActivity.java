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
import co.edu.unipiloto.fuelmanager.data.model.PriceAlert;
import co.edu.unipiloto.fuelmanager.utils.ApiClient;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;

public class AlertsActivity extends AppCompatActivity {

    private RecyclerView  recycler;
    private AlertsAdapter adapter;
    private TextView      tvEmpty;
    private int           userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        userId = new SessionManager(this).getUserId();

        recycler = findViewById(R.id.recyclerAlerts);
        tvEmpty  = findViewById(R.id.tvAlertsEmpty);

        adapter = new AlertsAdapter(new ArrayList<>(), (alert, activate) -> {
            new Thread(() -> {
                if (activate) {
                    alert.setActive(true);
                    ApiClient.upsertAlert(alert);
                } else {
                    ApiClient.deactivateAlert(alert.getStationId(), alert.getFuelType(), userId);
                }
                loadAlerts();
            }).start();
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
            List<PriceAlert> alerts = ApiClient.getActiveAlerts(userId);
            runOnUiThread(() -> {
                adapter.updateData(alerts);
                tvEmpty.setVisibility(alerts.isEmpty() ? View.VISIBLE : View.GONE);
                recycler.setVisibility(alerts.isEmpty() ? View.GONE : View.VISIBLE);
            });
        }).start();
    }
}