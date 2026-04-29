package co.edu.unipiloto.fuelmanager.clients;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.auth.LoginActivity;
import co.edu.unipiloto.fuelmanager.authority.PriceMonitorActivity;
import co.edu.unipiloto.fuelmanager.authority.ReceiptHistoryActivity;
import co.edu.unipiloto.fuelmanager.authority.SubsidyActivity;
import co.edu.unipiloto.fuelmanager.report.ReportActivity;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;

public class AuthorityHome extends AppCompatActivity {
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_home);
        session = new SessionManager(this);
        TextView tvName = findViewById(R.id.tvUserName);
        if (tvName != null) tvName.setText("Hola, " + session.getUserName());

        // HU-10: Reporte PDF
        findViewById(R.id.cardReport).setOnClickListener(v ->
                startActivity(new Intent(this, ReportActivity.class)));
        // HU-05: Monitoreo precios por región
        findViewById(R.id.cardMonitor).setOnClickListener(v ->
                startActivity(new Intent(this, PriceMonitorActivity.class)));
        // HU-11: Subsidios
        findViewById(R.id.cardSubsidy).setOnClickListener(v ->
                startActivity(new Intent(this, SubsidyActivity.class)));
        // HU-12: Historial de recibos
        findViewById(R.id.cardReceipts).setOnClickListener(v ->
                startActivity(new Intent(this, ReceiptHistoryActivity.class)));

        findViewById(R.id.btnLogout).setOnClickListener(v -> confirmLogout());
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión").setMessage("¿Deseas salir?")
                .setPositiveButton("Salir", (d, w) -> {
                    session.clearSession();
                    Intent i = new Intent(this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }).setNegativeButton("Cancelar", null).show();
    }
}