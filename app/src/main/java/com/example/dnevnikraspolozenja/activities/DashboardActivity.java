package com.example.dnevnikraspolozenja.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.dnevnikraspolozenja.R;
import com.example.dnevnikraspolozenja.utils.AuthManager;

public class DashboardActivity extends AppCompatActivity {

    private AuthManager authManager;
    private TextView welcomeText;
    private Button logoutBtn;
    private Button editProfileBtn;
    private Button addMoodBtn;
    private Button viewMoodHistoryBtn;
    private ConstraintLayout dashboardRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        authManager = new AuthManager(this);

        welcomeText = findViewById(R.id.welcomeText);
        logoutBtn = findViewById(R.id.logoutBtn);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        addMoodBtn = findViewById(R.id.addMoodBtn);
        viewMoodHistoryBtn = findViewById(R.id.viewMoodHistoryBtn);
        dashboardRoot = findViewById(R.id.dashboardRoot);

        // Dohvati email i prikaži korisničko ime
        String email = authManager.getEmail();
        String username = "";
        if (email != null && email.contains("@")) {
            username = email.substring(0, email.indexOf("@"));
        }
        welcomeText.setText("Dobrodošli, " + username);

        checkMoodAndUpdateBackground();

        addMoodBtn.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, CreateMoodActivity.class);
            startActivity(intent);
        });
        editProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
        viewMoodHistoryBtn.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, MoodListActivity.class);
            startActivity(intent);
        });


        // Logout
        logoutBtn.setOnClickListener(v -> {
            authManager.logout();
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkMoodAndUpdateBackground();
    }

    private void checkMoodAndUpdateBackground() {
        int lastMoodScore = getSharedPreferences("MoodPrefs", MODE_PRIVATE)
                .getInt("last_mood_score", 0);

        if (lastMoodScore == 0) {
            dashboardRoot.setBackgroundColor(Color.parseColor("#B3E5FC"));
        } else if (lastMoodScore <= 2) {
            dashboardRoot.setBackgroundColor(Color.parseColor("#BDBDBD"));
        } else {
            dashboardRoot.setBackgroundColor(Color.parseColor("#B3E5FC"));
        }
    }
}

