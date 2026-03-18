package co.edu.unipiloto.fuelmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.fuelmanager.admin.AdminDashBoard;
import co.edu.unipiloto.fuelmanager.auth.LoginActivity;
import co.edu.unipiloto.fuelmanager.clients.ClientHome;
import co.edu.unipiloto.fuelmanager.clients.DistributorHome;
import co.edu.unipiloto.fuelmanager.utils.Roles;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;

public class RouterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Intent next = getIntentForRole(this, session.getUserRole());
        next.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(next);
        finish();
    }

    public static Intent getIntentForRole(Context context, String role) {
        switch (role) {
            case Roles.ADMIN:
                return new Intent(context, AdminDashBoard.class);
            case Roles.DISTRIBUIDOR:
                return new Intent(context, DistributorHome.class);
            default:
                return new Intent(context, ClientHome.class);
        }
    }
}