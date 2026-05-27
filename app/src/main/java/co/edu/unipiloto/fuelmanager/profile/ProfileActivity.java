package co.edu.unipiloto.fuelmanager.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.utils.ApiClient;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;
import co.edu.unipiloto.fuelmanager.utils.Roles;

public class ProfileActivity extends AppCompatActivity {

    private SessionManager    session;

    private TextView          tvRoleBadge;
    private TextView          tvInitials;
    private TextInputEditText etName;
    private TextView          tvEmail;
    private TextView          tvRole;
    private MaterialButton    btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        session = new SessionManager(this);

        tvRoleBadge = findViewById(R.id.tvRoleBadge);
        tvInitials  = findViewById(R.id.tvInitials);
        etName      = findViewById(R.id.etName);
        tvEmail     = findViewById(R.id.tvEmail);
        tvRole      = findViewById(R.id.tvRole);
        btnSave     = findViewById(R.id.btnSave);

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        populateFields();

        btnSave.setOnClickListener(v -> saveName());
    }

    private void populateFields() {
        String name  = session.getUserName();
        String email = session.getUserEmail();
        String role  = session.getUserRole();

        tvRoleBadge.setText(getRoleLabel(role));
        tvInitials.setText(getInitials(name));
        etName.setText(name);
        tvEmail.setText(email);
        tvRole.setText(getRoleLabel(role));
    }

    private void saveName() {
        String newName = etName.getText() == null ? ""
                : etName.getText().toString().trim();

        if (TextUtils.isEmpty(newName)) {
            etName.setError("El nombre no puede estar vacío");
            etName.requestFocus();
            return;
        }

        int userId = session.getUserId();


        new Thread(() -> {
            boolean ok = ApiClient.updateUserName(userId, newName);
            runOnUiThread(() -> {
                if (ok) {
                    session.updateUserName(newName);
                    tvInitials.setText(getInitials(newName));
                    Toast.makeText(this, "✔ Nombre actualizado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error al guardar. Inténtalo de nuevo.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    /* ── Helpers ─────────────────────────────────────────── */
    private String getRoleLabel(String role) {
        if (role == null) return "Usuario";
        switch (role) {
            case Roles.ESTACION:     return "Estación de Servicio";
            case Roles.DISTRIBUIDOR: return "Distribuidor";
            case Roles.AUTORIDAD:    return "Autoridad Reguladora";
            case Roles.ADMIN:        return "Administrador";
            default:                 return "Conductor";
        }
    }

    private String getInitials(String name) {
        if (TextUtils.isEmpty(name)) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
    }
}