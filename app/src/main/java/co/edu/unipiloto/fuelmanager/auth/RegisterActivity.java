package co.edu.unipiloto.fuelmanager.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.utils.Roles;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private Spinner spinnerRole;
    private Button btnRegister;
    private TextView tvGoLogin;
    private View progressBar;

    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = new AuthRepository(this);

        etName            = findViewById(R.id.etName);
        etEmail           = findViewById(R.id.etEmail);
        etPassword        = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        spinnerRole       = findViewById(R.id.spinnerRole);
        btnRegister       = findViewById(R.id.btnRegister);
        tvGoLogin         = findViewById(R.id.tvGoLogin);
        progressBar       = findViewById(R.id.progressBar);

        setupRoleSpinner();

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvGoLogin.setOnClickListener(v -> finish());
    }

    private void setupRoleSpinner() {
        String[] roles = {
                "Conductor (Particular)",
                "Estación de Servicio",
                "Distribuidor",
                "Autoridad Reguladora"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                roles
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
    }

    private String getSelectedRole() {
        int pos = spinnerRole.getSelectedItemPosition();
        switch (pos) {
            case 1: return Roles.ESTACION;
            case 2: return Roles.DISTRIBUIDOR;
            case 3: return Roles.AUTORIDAD;
            default: return Roles.CLIENTE;
        }
    }

    private void attemptRegister() {
        String name     = etName.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm  = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Ingresa tu nombre"); etName.requestFocus(); return;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Correo no válido"); etEmail.requestFocus(); return;
        }
        if (password.length() < 6) {
            etPassword.setError("Mínimo 6 caracteres"); etPassword.requestFocus(); return;
        }
        if (!password.equals(confirm)) {
            etConfirmPassword.setError("Las contraseñas no coinciden");
            etConfirmPassword.requestFocus(); return;
        }

        setLoading(true);
        String role = getSelectedRole();

        new Thread(() -> {
            AuthRepository.RegisterResult result =
                    authRepository.register(name, email, password, role);

            runOnUiThread(() -> {
                setLoading(false);
                switch (result) {
                    case SUCCESS:
                        Toast.makeText(this,
                                "¡Cuenta creada! Inicia sesión",
                                Toast.LENGTH_LONG).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                        break;
                    case EMAIL_TAKEN:
                        etEmail.setError("Este correo ya está registrado");
                        etEmail.requestFocus();
                        break;
                    case ERROR:
                        Toast.makeText(this,
                                "Error al crear la cuenta. Intenta de nuevo",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            });
        }).start();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!loading);
    }
}