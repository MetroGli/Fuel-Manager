package co.edu.unipiloto.fuelmanager.normative;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.model.NormativePrice;
import co.edu.unipiloto.fuelmanager.normative.NormativePriceRepository;

public class NormativePriceActivity extends AppCompatActivity {

    private MaterialButton          btnActualizar;
    private RecyclerView            recycler;
    private NormativePriceAdapter   adapter;
    private NormativePriceRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normative_price);

        repo = new NormativePriceRepository(this);

        btnActualizar = findViewById(R.id.btnActualizarPrecios);
        recycler      = findViewById(R.id.recyclerNormative);

        adapter = new NormativePriceAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        // Cargar precios existentes al abrir
        loadPrices();

        btnActualizar.setOnClickListener(v -> actualizarPrecios());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void actualizarPrecios() {
        btnActualizar.setEnabled(false);
        btnActualizar.setText("Actualizando...");

        new Thread(() -> {
            boolean ok = repo.fetchAndSaveFromJson();
            List<NormativePrice> lista = repo.getAll();

            runOnUiThread(() -> {
                btnActualizar.setEnabled(true);
                btnActualizar.setText("Actualizar precios normativos");

                if (ok) {
                    adapter.setData(lista);
                    Toast.makeText(this, "Precios actualizados ✓", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void loadPrices() {
        new Thread(() -> {
            List<NormativePrice> lista = repo.getAll();
            runOnUiThread(() -> adapter.setData(lista));
        }).start();
    }
}