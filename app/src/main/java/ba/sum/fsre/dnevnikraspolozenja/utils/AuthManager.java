package ba.sum.fsre.dnevnikraspolozenja.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    private static final String PREFS_NAME = "auth";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ROLE = "role";

    private SharedPreferences prefs;

    public AuthManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public void saveEmail(String email) {
        prefs.edit().putString(KEY_EMAIL, email).apply();
    }
    public void saveUserId(String userId) { prefs.edit().putString(KEY_USER_ID, userId).apply();}

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getUserId() { return prefs.getString(KEY_USER_ID, null); }
    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
    public void saveAvatarUrl(String url) {
        prefs.edit()
                .putString("avatar_url", url)
                .apply();
    }

    public String getAvatarUrl() {
        return prefs.getString("avatar_url", null);
    }
    public void saveRole(String role) {
        prefs.edit().putString(KEY_ROLE, role).apply();
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }
}