package co.edu.unipiloto.fuelmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.fuelmanager.admin.AdminDashBoard;
import co.edu.unipiloto.fuelmanager.auth.LoginActivity;
import co.edu.unipiloto.fuelmanager.clients.AuthorityHome;
import co.edu.unipiloto.fuelmanager.clients.ClientHome;
import co.edu.unipiloto.fuelmanager.clients.DistributorHome;
import co.edu.unipiloto.fuelmanager.utils.Roles;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;

public class RouterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> {
            SessionManager session = new SessionManager(this);
            if (session.isLoggedIn()) {
                startActivity(getIntentForRole(this, session.getUserRole()));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 1500);
    }


    public static Intent getIntentForRole(Context context, String role) {
        if (role == null) return new Intent(context, LoginActivity.class);
        switch (role) {
            case Roles.ADMIN:
                return new Intent(context, AdminDashBoard.class);
            case Roles.DISTRIBUIDOR:
                return new Intent(context, DistributorHome.class);
            case Roles.AUTORIDAD:
                return new Intent(context, AuthorityHome.class);
            case Roles.ESTACION:
            case Roles.CLIENTE:
            default:
                return new Intent(context, ClientHome.class);
        }
    }
}