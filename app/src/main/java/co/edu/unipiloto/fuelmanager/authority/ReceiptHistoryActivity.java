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
import co.edu.unipiloto.fuelmanager.data.local.DatabaseHelper;
import co.edu.unipiloto.fuelmanager.data.model.Receipt;
import co.edu.unipiloto.fuelmanager.data.model.Station;
import co.edu.unipiloto.fuelmanager.sales.ReceiptPdfActivity;

public class ReceiptHistoryActivity extends AppCompatActivity {

    private Spinner        spinnerStation;  // NUEVO — filtro por estación
    private RecyclerView   recycler;
    private ReceiptAdapter adapter;
    private DatabaseHelper db;

    private List<Station> stations = new ArrayList<>();
    private int           selectedStationId = -1; // -1 = todas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_history);

        db           = DatabaseHelper.getInstance(this);
        spinnerStation = findViewById(R.id.spinnerFilterStation); // NUEVO id en layout
        recycler     = findViewById(R.id.recyclerReceipts);

        // ReceiptAdapter ahora recibe un listener para abrir el PDF
        adapter = new ReceiptAdapter(new ArrayList<>(), receipt -> openReceiptPdf(receipt));
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        setupStationFilter(); // NUEVO
    }

    // ── Filtro por estación (NUEVO) ─────────────────────────────────────────

    private void setupStationFilter() {
        new Thread(() -> {
            stations = db.getAllStations();

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

                // Carga inicial: todas
                loadReceipts();
            });
        }).start();
    }

    // ── Carga de recibos (filtrada o total) ─────────────────────────────────

    private void loadReceipts() {
        new Thread(() -> {
            List<Receipt> list = (selectedStationId == -1)
                    ? db.getAllReceipts()
                    : db.getReceiptsByStation(selectedStationId);
            runOnUiThread(() -> adapter.updateData(list));
        }).start();
    }

    // ── Abrir PDF desde Autoridad ────────────────────────────────────────────

    private void openReceiptPdf(Receipt receipt) {
        Intent intent = new Intent(this, ReceiptPdfActivity.class);
        // Pasamos el saleId para que ReceiptPdfActivity cargue ese recibo específico
        intent.putExtra(ReceiptPdfActivity.EXTRA_RECEIPT_ID, (int) receipt.getId());
        startActivity(intent);
    }
}