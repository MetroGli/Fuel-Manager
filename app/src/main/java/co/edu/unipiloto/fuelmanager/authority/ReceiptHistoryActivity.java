package co.edu.unipiloto.fuelmanager.authority;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.model.Receipt;
import co.edu.unipiloto.fuelmanager.data.model.Station;
import co.edu.unipiloto.fuelmanager.sales.ReceiptPdfActivity;
import co.edu.unipiloto.fuelmanager.utils.ApiClient;

public class ReceiptHistoryActivity extends AppCompatActivity {

    private Spinner        spinnerStation;
    private RecyclerView   recycler;
    private ReceiptAdapter adapter;

    private List<Station> stations = new ArrayList<>();
    private int           selectedStationId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_history);

        spinnerStation = findViewById(R.id.spinnerFilterStation);
        recycler       = findViewById(R.id.recyclerReceipts);

        adapter = new ReceiptAdapter(new ArrayList<>(), receipt -> openReceiptPdf(receipt));
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        setupStationFilter();
    }

    private void setupStationFilter() {
        new Thread(() -> {
            stations = ApiClient.getAllStations();

            List<String> names = new ArrayList<>();
            names.add("Todas las estaciones");
            for (Station s : stations) names.add(s.getName() + " · " + s.getZone());

            runOnUiThread(() -> {
                ArrayAdapter<String> sa = new ArrayAdapter<>(this, R.layout.spinner_item, names);
                sa.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinnerStation.setAdapter(sa);

                spinnerStation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                        selectedStationId = (pos == 0) ? -1 : stations.get(pos - 1).getId();
                        loadReceipts();
                    }
                    @Override public void onNothingSelected(AdapterView<?> p) {}
                });

                loadReceipts();
            });
        }).start();
    }

    private void loadReceipts() {
        new Thread(() -> {
            List<Receipt> list = (selectedStationId == -1)
                    ? ApiClient.getAllReceipts()
                    : ApiClient.getReceiptsByStation(selectedStationId);
            runOnUiThread(() -> adapter.updateData(list));
        }).start();
    }

    private void openReceiptPdf(Receipt receipt) {
        Intent intent = new Intent(this, ReceiptPdfActivity.class);
        intent.putExtra(ReceiptPdfActivity.EXTRA_RECEIPT_ID, (int) receipt.getId());
        startActivity(intent);
    }
}