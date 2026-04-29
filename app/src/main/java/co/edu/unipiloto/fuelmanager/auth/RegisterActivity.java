package co.edu.unipiloto.fuelmanager.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.local.DatabaseHelper;
import co.edu.unipiloto.fuelmanager.data.model.Station;
import co.edu.unipiloto.fuelmanager.data.model.User;
import co.edu.unipiloto.fuelmanager.utils.Roles;

public class RegisterActivity extends AppCompatActivity {

    // Campos base
    private TextInputEditText etName, etEmail, etPassword, etConfirm;
    private Spinner           spinnerRole;
    private MaterialButton    btnRegister;

    // Sección vehículo — solo para CLIENTE
    private View              sectionVehicle;
    private Spinner           spinnerVehicle;

    // Sección estación — solo para ESTACION
    private View              sectionStation;
    private TextInputEditText etAddress, etZone;
    private TextInputEditText etPriceCorriente, etPriceExtra, etPriceAcpm;

    private DatabaseHelper db;
    private String selectedRole    = Roles.CLIENTE;
    private String selectedVehicle = DatabaseHelper.VEHICLE_CARRO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = DatabaseHelper.getInstance(this);
        bindViews();
        setupRoleSpinner();
        setupVehicleSpinner();

        btnRegister.setOnClickListener(v -> attemptRegister());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void bindViews() {
        etName           = findViewById(R.id.etName);
        etEmail          = findViewById(R.id.etEmail);
        etPassword       = findViewById(R.id.etPassword);
        etConfirm        = findViewById(R.id.etConfirmPassword);
        spinnerRole      = findViewById(R.id.spinnerRole);
        btnRegister      = findViewById(R.id.btnRegister);
        sectionVehicle   = findViewById(R.id.sectionVehicle);
        spinnerVehicle   = findViewById(R.id.spinnerVehicle);
        sectionStation   = findViewById(R.id.sectionStation);
        etAddress        = findViewById(R.id.etStationAddress);
        etZone           = findViewById(R.id.etStationZone);
        etPriceCorriente = findViewById(R.id.etPriceCorriente);
        etPriceExtra     = findViewById(R.id.etPriceExtra);
        etPriceAcpm      = findViewById(R.id.etPriceAcpm);
    }

    private void setupRoleSpinner() {
        String[] roles = {
                Roles.CLIENTE,
                Roles.ESTACION,
                Roles.DISTRIBUIDOR,
                Roles.AUTORIDAD
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, roles);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                selectedRole = roles[pos];
                sectionVehicle.setVisibility(selectedRole.equals(Roles.CLIENTE) ? View.VISIBLE : View.GONE);
                sectionStation.setVisibility(selectedRole.equals(Roles.ESTACION) ? View.VISIBLE : View.GONE);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void setupVehicleSpinner() {
        String[] vehicles = {
                DatabaseHelper.VEHICLE_CARRO,
                DatabaseHelper.VEHICLE_MOTO,
                DatabaseHelper.VEHICLE_CAMION
        };
        ArrayAdapter<String> va = new ArrayAdapter<>(this, R.layout.spinner_item, vehicles);
        va.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerVehicle.setAdapter(va);
        spinnerVehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                selectedVehicle = vehicles[pos];
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void attemptRegister() {
        String name     = text(etName);
        String email    = text(etEmail);
        String password = text(etPassword);
        String confirm  = text(etConfirm);

        if (TextUtils.isEmpty(name))     { etName.setError("Ingresa tu nombre"); return; }
        if (TextUtils.isEmpty(email))    { etEmail.setError("Ingresa tu correo"); return; }
        if (TextUtils.isEmpty(password)) { etPassword.setError("Ingresa una contraseña"); return; }
        if (password.length() < 6)       { etPassword.setError("Mínimo 6 caracteres"); return; }
        if (!password.equals(confirm))   { etConfirm.setError("Las contraseñas no coinciden"); return; }

        if (db.emailExists(email)) { etEmail.setError("Este correo ya está registrado"); return; }

        User user = new User(0, name, email, password, selectedRole);

        if (selectedRole.equals(Roles.CLIENTE)) {
            // Guardar tipo de vehículo
            user.setVehicleType(selectedVehicle);
            new Thread(() -> {
                long id = db.insertUser(user);
                runOnUiThread(() -> onResult(id));
            }).start();

        } else if (selectedRole.equals(Roles.ESTACION)) {
            String address      = text(etAddress);
            String zone         = text(etZone);
            String corrienteStr = text(etPriceCorriente);
            String extraStr     = text(etPriceExtra);
            String acpmStr      = text(etPriceAcpm);

            if (TextUtils.isEmpty(address))     { etAddress.setError("Ingresa la dirección"); return; }
            if (TextUtils.isEmpty(zone))         { etZone.setError("Ingresa la zona"); return; }
            if (TextUtils.isEmpty(corrienteStr)) { etPriceCorriente.setError("Ingresa el precio"); return; }
            if (TextUtils.isEmpty(extraStr))     { etPriceExtra.setError("Ingresa el precio"); return; }
            if (TextUtils.isEmpty(acpmStr))      { etPriceAcpm.setError("Ingresa el precio"); return; }

            double corriente, extra, acpm;
            try {
                corriente = Double.parseDouble(corrienteStr);
                extra     = Double.parseDouble(extraStr);
                acpm      = Double.parseDouble(acpmStr);
            } catch (NumberFormatException e) { etPriceCorriente.setError("Precio inválido"); return; }

            Station station = new Station();
            station.setName(name); station.setAddress(address); station.setZone(zone);
            station.setPriceCorriente(corriente); station.setPriceExtra(extra); station.setPriceAcpm(acpm);

            new Thread(() -> {
                long id = db.insertUserWithStation(user, station);
                runOnUiThread(() -> onResult(id));
            }).start();

        } else {
            // DISTRIBUIDOR, AUTORIDAD — sin campos extra
            new Thread(() -> {
                long id = db.insertUser(user);
                runOnUiThread(() -> onResult(id));
            }).start();
        }
    }

    private void onResult(long id) {
        if (id > 0) {
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        } else {
            Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
        }
    }

    private String text(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}