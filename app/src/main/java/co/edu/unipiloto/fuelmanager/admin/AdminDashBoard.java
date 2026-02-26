package co.edu.unipiloto.fuelmanager.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.auth.LoginActivity;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;

public class AdminDashBoard extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        sessionManager = new SessionManager(this);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("Bienvenido, " + sessionManager.getUserName());

        findViewById(R.id.btnLogout).setOnClickListener(v -> confirmLogout());
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Deseas salir de la plataforma?")
                .setPositiveButton("Salir", (d, w) -> {
                    sessionManager.clearSession();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}