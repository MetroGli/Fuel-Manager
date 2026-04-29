package co.edu.unipiloto.fuelmanager.clients;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.auth.LoginActivity;
import co.edu.unipiloto.fuelmanager.distribution.DeliveryActivity;
import co.edu.unipiloto.fuelmanager.distribution.PriceUpdateActivity;
import co.edu.unipiloto.fuelmanager.distribution.WholesalePriceActivity;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;

public class DistributorHome extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_home);

        session = new SessionManager(this);

        TextView tvName = findViewById(R.id.tvUserName);
        if (tvName != null) tvName.setText("Hola, " + session.getUserName());

        // HU-09: Registrar entrega
        findViewById(R.id.cardDelivery).setOnClickListener(v ->
                startActivity(new Intent(this, DeliveryActivity.class)));

        // HU-04: Actualizar precios al consumidor
        findViewById(R.id.cardPriceUpdate).setOnClickListener(v ->
                startActivity(new Intent(this, PriceUpdateActivity.class)));

        // HU-13: Precios mayoristas (distribuidor → estación)
        if (findViewById(R.id.cardWholesale) != null) {
            findViewById(R.id.cardWholesale).setOnClickListener(v ->
                    startActivity(new Intent(this, WholesalePriceActivity.class)));
        }

        findViewById(R.id.btnLogout).setOnClickListener(v -> confirmLogout());
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