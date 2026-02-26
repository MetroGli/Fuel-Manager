package co.edu.unipiloto.fuelmanager;

import android.content.Context;
import android.content.Intent;

import co.edu.unipiloto.fuelmanager.admin.AdminDashBoard;
import co.edu.unipiloto.fuelmanager.clients.ClientHome;
import co.edu.unipiloto.fuelmanager.utils.Roles;

public class RouterActivity {

    private RouterActivity() {}

    public static Intent getIntentForRole(Context context, String role) {
        switch (role) {
            case Roles.ADMIN:
                return new Intent(context, AdminDashBoard.class);
            case Roles.CLIENTE:
            default:
                return new Intent(context, ClientHome.class);
        }
    }
}