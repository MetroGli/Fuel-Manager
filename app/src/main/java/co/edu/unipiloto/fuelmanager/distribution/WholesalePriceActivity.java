package co.edu.unipiloto.fuelmanager.distribution;

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
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.local.DatabaseHelper;
import co.edu.unipiloto.fuelmanager.data.model.NormativePrice;
import co.edu.unipiloto.fuelmanager.data.model.Station;
import co.edu.unipiloto.fuelmanager.data.model.WholesalePrice;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;

public class WholesalePriceActivity extends AppCompatActivity {

    private Spinner           spinnerStation, spinnerFuel;
    private TextInputEditText etPrice;
    private TextView          tvNormativeRef, tvMarginInfo;
    private MaterialButton    btnGuardar;
    private RecyclerView      recyclerHistory;
    private WholesaleAdapter  adapter;

    private DatabaseHelper    db;
    private SessionManager    session;
    private List<Station>     stations = new ArrayList<>();
    private Station           selectedStation;
    private String            selectedFuel = "Corriente";

    private static final NumberFormat COP = NumberFormat.getInstance(new Locale("es", "CO"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wholesale_price);

        db      = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);

        bindViews();
        loadStationsSpinner();
        setupFuelSpinner();
        setupRecycler();
        loadHistory();

        btnGuardar.setOnClickListener(v -> guardarPrecio());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void bindViews() {
        spinnerStation = findViewById(R.id.spinnerWholesaleStation);
        spinnerFuel    = findViewById(R.id.spinnerWholesaleFuel);
        etPrice        = findViewById(R.id.etWholesalePrice);
        tvNormativeRef = findViewById(R.id.tvNormativeRef);
        tvMarginInfo   = findViewById(R.id.tvMarginInfo);
        btnGuardar     = findViewById(R.id.btnGuardarWholesale);
        recyclerHistory= findViewById(R.id.recyclerWholesale);
    }

    private void loadStationsSpinner() {
        new Thread(() -> {
            stations = db.getAllStations();
            List<String> names = new ArrayList<>();
            for (Station s : stations) names.add(s.getName() + " · " + s.getZone());
            runOnUiThread(() -> {
                ArrayAdapter<String> sa = new ArrayAdapter<>(this, R.layout.spinner_item, names);
                sa.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinnerStation.setAdapter(sa);
                spinnerStation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                        selectedStation = stations.get(pos);
                        updateNormativeRef();
                    }
                    @Override public void onNothingSelected(AdapterView<?> p) {}
                });
                if (!stations.isEmpty()) { selectedStation = stations.get(0); updateNormativeRef(); }
            });
        }).start();
    }

    private void setupFuelSpinner() {
        String[] fuels = {"Corriente", "Extra", "ACPM"};
        ArrayAdapter<String> fa = new ArrayAdapter<>(this, R.layout.spinner_item, fuels);
        fa.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerFuel.setAdapter(fa);
        spinnerFuel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                selectedFuel = fuels[pos];
                updateNormativeRef();
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    /** Muestra el precio normativo de referencia y el margen respecto al precio al consumidor. */
    private void updateNormativeRef() {
        new Thread(() -> {
            List<NormativePrice> normative = db.getNormativePrices();
            double normRef = 0;
            for (NormativePrice p : normative) {
                if (p.getFuelType().equalsIgnoreCase(selectedFuel) ||
                    p.getFuelType().toUpperCase().contains(selectedFuel.toUpperCase())) {
                    normRef = p.getPricePerGallon();
                    break;
                }
            }

            // Precio al consumidor de la estación seleccionada
            double consumerPrice = 0;
            if (selectedStation != null) {
                switch (selectedFuel) {
                    case "Extra": consumerPrice = selectedStation.getPriceExtra(); break;
                    case "ACPM":  consumerPrice = selectedStation.getPriceAcpm();  break;
                    default:      consumerPrice = selectedStation.getPriceCorriente(); break;
                }
            }

            final double refFinal = normRef;
            final double consFinal = consumerPrice;

            runOnUiThread(() -> {
                if (refFinal > 0)
                    tvNormativeRef.setText("Referencia MINMINAS: $" + COP.format(refFinal) + "/gal");
                else
                    tvNormativeRef.setText("Sin precio normativo registrado");

                if (consFinal > 0)
                    tvMarginInfo.setText("Precio al consumidor en esta estación: $" +
                            COP.format(consFinal) + "/gal\n" +
                            "El precio mayorista debe ser menor a este valor.");
                else
                    tvMarginInfo.setText("");
            });
        }).start();
    }

    private void setupRecycler() {
        adapter = new WholesaleAdapter(new ArrayList<>());
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerHistory.setAdapter(adapter);
    }

    private void guardarPrecio() {
        if (selectedStation == null) { Toast.makeText(this, "Selecciona una estación", Toast.LENGTH_SHORT).show(); return; }

        String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";
        if (TextUtils.isEmpty(priceStr)) { etPrice.setError("Ingresa el precio mayorista"); return; }

        double price;
        try { price = Double.parseDouble(priceStr); }
        catch (NumberFormatException e) { etPrice.setError("Número inválido"); return; }

        if (price <= 0) { etPrice.setError("Debe ser mayor a 0"); return; }

        // Advertir si el precio mayorista es mayor al precio al consumidor
        double consumerPrice;
        switch (selectedFuel) {
            case "Extra": consumerPrice = selectedStation.getPriceExtra(); break;
            case "ACPM":  consumerPrice = selectedStation.getPriceAcpm();  break;
            default:      consumerPrice = selectedStation.getPriceCorriente(); break;
        }

        if (consumerPrice > 0 && price >= consumerPrice) {
            Toast.makeText(this,
                    "⚠ El precio mayorista ($" + COP.format(price) +
                    ") es igual o mayor al precio al consumidor ($" + COP.format(consumerPrice) + ")",
                    Toast.LENGTH_LONG).show();
        }

        WholesalePrice wp = new WholesalePrice(
                selectedStation.getId(), selectedStation.getName(),
                selectedFuel, price, new Date().toString(), session.getUserId());

        new Thread(() -> {
            db.insertWholesalePrice(wp);
            loadHistory();
            runOnUiThread(() -> {
                Toast.makeText(this, "Precio mayorista registrado ✓", Toast.LENGTH_SHORT).show();
                etPrice.setText("");
            });
        }).start();
    }

    private void loadHistory() {
        new Thread(() -> {
            List<WholesalePrice> list = db.getWholesalePricesByDistributor(session.getUserId());
            runOnUiThread(() -> adapter.updateData(list));
        }).start();
    }
}
