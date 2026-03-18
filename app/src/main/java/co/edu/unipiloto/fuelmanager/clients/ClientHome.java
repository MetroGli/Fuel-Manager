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
import co.edu.unipiloto.fuelmanager.normative.NormativePriceActivity;
import co.edu.unipiloto.fuelmanager.sales.SalesActivity;
import co.edu.unipiloto.fuelmanager.stations.StationListActivity;
import co.edu.unipiloto.fuelmanager.utils.Roles;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;

public class ClientHome extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);

        session = new SessionManager(this);
        String role = session.getUserRole();

        TextView tvWelcome   = findViewById(R.id.tvWelcome);
        TextView tvRoleBadge = findViewById(R.id.tvRoleBadge);
        tvWelcome.setText("Hola, " + session.getUserName());
        tvRoleBadge.setText(getRoleLabel(role));

        setupCards(role);

        findViewById(R.id.btnLogout).setOnClickListener(v -> confirmLogout());
    }

    private void setupCards(String role) {

        // ── HU-01: solo CLIENTE ──────────────────────────────
        CardView cardPrices = findViewById(R.id.cardPrices);
        if (role.equals(Roles.CLIENTE)) {
            cardPrices.setVisibility(View.VISIBLE);
            cardPrices.setOnClickListener(v ->
                    startActivity(new Intent(this, StationListActivity.class)));
        } else {
            cardPrices.setVisibility(View.GONE);
        }

        // ── HU-07: ESTACION y ADMIN ──────────────────────────
        CardView cardInventory = findViewById(R.id.cardInventory);
        if (role.equals(Roles.ESTACION) || role.equals(Roles.ADMIN)) {
            cardInventory.setVisibility(View.VISIBLE);
            cardInventory.setOnClickListener(v ->
                    startActivity(new Intent(this, InventoryActivity.class)));
        } else {
            cardInventory.setVisibility(View.GONE);
        }

        // ── HU-03: ESTACION y ADMIN ──────────────────────────
        CardView cardSales = findViewById(R.id.cardSales);
        if (role.equals(Roles.ESTACION) || role.equals(Roles.ADMIN)) {
            cardSales.setVisibility(View.VISIBLE);
            cardSales.setOnClickListener(v ->
                    startActivity(new Intent(this, SalesActivity.class)));
        } else {
            cardSales.setVisibility(View.GONE);
        }

        // ── HU-08: solo ADMIN ────────────────────────────────
        CardView cardNormative = findViewById(R.id.cardNormative);
        if (role.equals(Roles.ADMIN)) {
            cardNormative.setVisibility(View.VISIBLE);
            cardNormative.setOnClickListener(v ->
                    startActivity(new Intent(this, NormativePriceActivity.class)));
        } else {
            cardNormative.setVisibility(View.GONE);
        }
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
                    session.clearSession();
                    Intent i = new Intent(this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}