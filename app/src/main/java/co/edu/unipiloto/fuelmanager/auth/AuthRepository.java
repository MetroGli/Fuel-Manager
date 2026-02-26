package co.edu.unipiloto.fuelmanager.auth;

import android.content.Context;

import co.edu.unipiloto.fuelmanager.data.local.DatabaseHelper;
import co.edu.unipiloto.fuelmanager.data.model.User;

public class AuthRepository {

    private final DatabaseHelper db;

    public AuthRepository(Context context) {
        db = DatabaseHelper.getInstance(context);
    }

    public enum LoginResult { SUCCESS, INVALID_CREDENTIALS }
    public enum RegisterResult { SUCCESS, EMAIL_TAKEN, ERROR }

    public LoginResult login(String email, String password) {
        User user = db.loginUser(email, password);
        return user != null ? LoginResult.SUCCESS : LoginResult.INVALID_CREDENTIALS;
    }

    public User getUser(String email, String password) {
        return db.loginUser(email, password);
    }

    public RegisterResult register(String name, String email, String password, String role) {
        if (db.emailExists(email)) return RegisterResult.EMAIL_TAKEN;
        User user = new User(name, email, password, role);
        long id = db.insertUser(user);
        return id != -1 ? RegisterResult.SUCCESS : RegisterResult.ERROR;
    }
}