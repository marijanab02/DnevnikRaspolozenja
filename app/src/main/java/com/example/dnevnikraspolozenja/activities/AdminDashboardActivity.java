package com.example.dnevnikraspolozenja.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dnevnikraspolozenja.R;
import com.example.dnevnikraspolozenja.utils.AuthManager;

public class AdminDashboardActivity extends AppCompatActivity {

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        authManager = new AuthManager(this);

        findViewById(R.id.btnAddMentalTask)
                .setOnClickListener(v ->
                        startActivity(new Intent(this, AddMentalTaskActivity.class)));

        findViewById(R.id.btnViewMentalTasks)
                .setOnClickListener(v ->
                        startActivity(new Intent(this, MentalTaskListActivity.class)));

        findViewById(R.id.logoutBtn)
                .setOnClickListener(v -> {
                    authManager.logout();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                });
    }
}
