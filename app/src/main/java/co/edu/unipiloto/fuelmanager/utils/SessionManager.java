package co.edu.unipiloto.fuelmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

import co.edu.unipiloto.fuelmanager.data.model.User;

public class SessionManager {

    private static final String PREF_NAME    = "FuelManagerSession";
    private static final String KEY_ID       = "user_id";
    private static final String KEY_NAME     = "user_name";
    private static final String KEY_EMAIL    = "user_email";
    private static final String KEY_ROLE     = "user_role";
    private static final String KEY_LOGGED   = "is_logged";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(User user) {
        editor.putInt(KEY_ID,     user.getId());
        editor.putString(KEY_NAME,  user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_ROLE,  user.getRole());
        editor.putBoolean(KEY_LOGGED, true);
        editor.apply();
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn()    { return prefs.getBoolean(KEY_LOGGED, false); }
    public int    getUserId()      { return prefs.getInt(KEY_ID, 1); }
    public String getUserName()    { return prefs.getString(KEY_NAME,  ""); }
    public String getUserEmail()   { return prefs.getString(KEY_EMAIL, ""); }
    public String getUserRole()    { return prefs.getString(KEY_ROLE,  ""); }
}