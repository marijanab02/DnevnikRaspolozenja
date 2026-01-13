package com.example.dnevnikraspolozenja.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dnevnikraspolozenja.R;
import com.example.dnevnikraspolozenja.utils.AuthManager;

public class DashboardActivity extends AppCompatActivity {

    private AuthManager authManager;
    private TextView welcomeText;
    private Button logoutBtn;
    private Button editProfileBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        authManager = new AuthManager(this);

        welcomeText = findViewById(R.id.welcomeText);
        logoutBtn = findViewById(R.id.logoutBtn);
        editProfileBtn = findViewById(R.id.editProfileBtn);

        // Dohvati email i prikaži korisničko ime
        String email = authManager.getEmail();
        String username = "";
        if (email != null && email.contains("@")) {
            username = email.substring(0, email.indexOf("@"));
        }
        welcomeText.setText("Dobrodošli, " + username);


        editProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
        // Logout
        logoutBtn.setOnClickListener(v -> {
            authManager.logout();
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // da korisnik ne može vratiti Back
        });
    }
}
