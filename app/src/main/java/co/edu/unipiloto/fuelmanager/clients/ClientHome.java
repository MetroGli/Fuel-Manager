package co.edu.unipiloto.fuelmanager.clients;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.auth.LoginActivity;
import co.edu.unipiloto.fuelmanager.inventory.InventoryActivity;
import co.edu.unipiloto.fuelmanager.stations.StationListActivity;
import co.edu.unipiloto.fuelmanager.utils.Roles;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;

public class ClientHome extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);

        sessionManager = new SessionManager(this);
        String role = sessionManager.getUserRole();

        TextView tvWelcome   = findViewById(R.id.tvWelcome);
        TextView tvRoleBadge = findViewById(R.id.tvRoleBadge);
        tvWelcome.setText("Hola, " + sessionManager.getUserName());
        tvRoleBadge.setText(getRoleLabel(role));

        CardView cardPrices = findViewById(R.id.cardPrices);
        if (role.equals(Roles.CLIENTE)) {
            cardPrices.setVisibility(View.VISIBLE);
            cardPrices.setOnClickListener(v ->
                    startActivity(new Intent(this, StationListActivity.class)));
        } else {
            cardPrices.setVisibility(View.GONE);
        }

        CardView cardInventory       = findViewById(R.id.cardInventory);
        TextView tvInventoryBadge    = findViewById(R.id.tvInventoryBadge);
        if (role.equals(Roles.ESTACION)) {
            cardInventory.setAlpha(1.0f);
            tvInventoryBadge.setVisibility(View.GONE);
            cardInventory.setOnClickListener(v ->
                    startActivity(new Intent(this, InventoryActivity.class)));
        } else if (role.equals(Roles.ADMIN)) {
            cardInventory.setAlpha(1.0f);
            tvInventoryBadge.setVisibility(View.GONE);
            cardInventory.setOnClickListener(v ->
                    startActivity(new Intent(this, InventoryActivity.class)));
        }

        // Logout
        findViewById(R.id.btnLogout).setOnClickListener(v -> confirmLogout());
    }

    private String getRoleLabel(String role) {
        switch (role) {
            case Roles.ESTACION:     return "Estación de Servicio";
            case Roles.DISTRIBUIDOR: return "Distribuidor";
            case Roles.AUTORIDAD:    return "Autoridad Reguladora";
            case Roles.ADMIN:        return "Administrador";
            default:                 return "Conductor";
        }
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Deseas salir de la aplicación?")
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