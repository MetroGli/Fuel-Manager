package co.edu.unipiloto.fuelmanager.authority;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
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
import co.edu.unipiloto.fuelmanager.data.model.Subsidy;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;

public class SubsidyActivity extends AppCompatActivity {

    private Spinner spinnerTargetType, spinnerFuel;
    private TextInputEditText etTargetValue, etDiscount, etNotes, etEndDate;
    private MaterialButton btnRegistrar;
    private RecyclerView recyclerSubsidies;
    private SubsidyAdapter adapter;
    private DatabaseHelper db;
    private int authorityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subsidy);

        db = DatabaseHelper.getInstance(this);
        authorityId = new SessionManager(this).getUserId();

        spinnerTargetType   = findViewById(R.id.spinnerTargetType);
        spinnerFuel         = findViewById(R.id.spinnerSubsidyFuel);
        etTargetValue       = findViewById(R.id.etTargetValue);
        etDiscount          = findViewById(R.id.etDiscountPct);
        etNotes             = findViewById(R.id.etSubsidyNotes);
        etEndDate           = findViewById(R.id.etEndDate);
        btnRegistrar        = findViewById(R.id.btnRegistrarSubsidy);
        recyclerSubsidies   = findViewById(R.id.recyclerSubsidies);

        setupSpinners();
        setupRecycler();
        loadSubsidies();

        btnRegistrar.setOnClickListener(v -> registrarSubsidio());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void setupSpinners() {
        String[] types = {"REGION", "USER"};
        ArrayAdapter<String> ta = new ArrayAdapter<>(this, R.layout.spinner_item, types);
        ta.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerTargetType.setAdapter(ta);

        spinnerTargetType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                TextView tvHint = findViewById(R.id.tvTargetHint);
                if (tvHint != null) tvHint.setText(pos == 0 ? "Ej: Bogotá Sur" : "Ej: ID del usuario");
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        String[] fuels = {"Corriente", "Extra", "ACPM", "TODOS"};
        ArrayAdapter<String> fa = new ArrayAdapter<>(this, R.layout.spinner_item, fuels);
        fa.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerFuel.setAdapter(fa);
    }

    private void setupRecycler() {
        adapter = new SubsidyAdapter(new ArrayList<>(), subsidyId -> {
            db.deactivateSubsidy(subsidyId);
            loadSubsidies();
            Toast.makeText(this, "Subsidio desactivado", Toast.LENGTH_SHORT).show();
        });
        recyclerSubsidies.setLayoutManager(new LinearLayoutManager(this));
        recyclerSubsidies.setAdapter(adapter);
    }

    private void registrarSubsidio() {
        String targetValue = etTargetValue.getText() != null ? etTargetValue.getText().toString().trim() : "";
        String discountStr = etDiscount.getText()    != null ? etDiscount.getText().toString().trim()    : "";
        String notes       = etNotes.getText()       != null ? etNotes.getText().toString().trim()       : "";
        String endDate     = etEndDate.getText()      != null ? etEndDate.getText().toString().trim()      : "";

        if (TextUtils.isEmpty(targetValue)) { etTargetValue.setError("Ingresa el destino"); return; }
        if (TextUtils.isEmpty(discountStr)) { etDiscount.setError("Ingresa el descuento"); return; }
        if (TextUtils.isEmpty(endDate))     { etEndDate.setError("Ingresa fecha fin"); return; }

        double discount;
        try { discount = Double.parseDouble(discountStr); }
        catch (NumberFormatException e) { etDiscount.setError("Número inválido"); return; }

        if (discount <= 0 || discount > 100) { etDiscount.setError("Debe ser entre 1 y 100"); return; }

        String targetType = spinnerTargetType.getSelectedItem().toString();
        String fuel       = spinnerFuel.getSelectedItem().toString();
        String startDate  = new Date().toString();

        new Thread(() -> {
            Subsidy subsidy = new Subsidy(targetType, targetValue, fuel, discount,
                    startDate, endDate, notes, authorityId);
            long id = db.insertSubsidy(subsidy);
            loadSubsidies();
            runOnUiThread(() -> {
                Toast.makeText(this, "Subsidio #" + id + " registrado ✓", Toast.LENGTH_SHORT).show();
                etTargetValue.setText(""); etDiscount.setText(""); etNotes.setText(""); etEndDate.setText("");
            });
        }).start();
    }

    private void loadSubsidies() {
        new Thread(() -> {
            List<Subsidy> list = db.getAllSubsidies();
            runOnUiThread(() -> adapter.updateData(list));
        }).start();
    }
}
