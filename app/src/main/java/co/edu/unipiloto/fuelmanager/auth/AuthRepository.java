package co.edu.unipiloto.fuelmanager.auth;

import android.content.Context;

import co.edu.unipiloto.fuelmanager.data.model.User;
import co.edu.unipiloto.fuelmanager.utils.ApiClient;

public class AuthRepository {

    public AuthRepository(Context context) {
    }

    public enum LoginResult { SUCCESS, INVALID_CREDENTIALS }
    public enum RegisterResult { SUCCESS, EMAIL_TAKEN, ERROR }

    public LoginResult login(String email, String password) {
        User user = ApiClient.loginUser(email, password);
        return user != null ? LoginResult.SUCCESS : LoginResult.INVALID_CREDENTIALS;
    }

    public User getUser(String email, String password) {
        return ApiClient.loginUser(email, password);
    }

    public RegisterResult register(String name, String email, String password, String role) {
        if (ApiClient.emailExists(email)) return RegisterResult.EMAIL_TAKEN;
        User user = new User(name, email, password, role);
        long id = ApiClient.insertUser(user);
        return id != -1 ? RegisterResult.SUCCESS : RegisterResult.ERROR;
    }
}