package com.example.dnevnikraspolozenja.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dnevnikraspolozenja.api.ApiCallback;
import com.example.dnevnikraspolozenja.api.RetrofitClient;
import com.example.dnevnikraspolozenja.models.request.LoginRequest;
import com.example.dnevnikraspolozenja.models.response.AuthResponse;
import com.example.dnevnikraspolozenja.utils.AuthManager;

public class LoginActivity extends AppCompatActivity {

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authManager = new AuthManager(this);

        login();
    }

    private void login() {
        LoginRequest request = new LoginRequest(
                "marijana.bandic@fsre.sum.ba",
                "test1234"
        );

        RetrofitClient.getInstance()
                .getApi()
                .login(request)
                .enqueue(new ApiCallback<AuthResponse>() {
                    @Override
                    public void onSuccess(AuthResponse response) {
                        authManager.saveToken(response.getAccessToken());
                        authManager.saveEmail(response.getUser().getEmail());
                        Log.d("SUPABASE", "LOGIN OK");
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("SUPABASE", errorMessage);
                    }
                });
    }
}

