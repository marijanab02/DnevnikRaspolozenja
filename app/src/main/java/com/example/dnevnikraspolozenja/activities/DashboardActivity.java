package com.example.dnevnikraspolozenja.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.dnevnikraspolozenja.R;
import com.example.dnevnikraspolozenja.api.ApiCallback;
import com.example.dnevnikraspolozenja.api.RetrofitClient;
import com.example.dnevnikraspolozenja.models.request.UpdateUserTaskRequest;
import com.example.dnevnikraspolozenja.models.request.UserTaskRequest;
import com.example.dnevnikraspolozenja.models.response.ProfileResponse;
import com.example.dnevnikraspolozenja.models.response.UserTaskStatusResponse;
import com.example.dnevnikraspolozenja.utils.AuthManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private AuthManager authManager;
    private TextView welcomeText, tvTaskTitle, tvTaskDescription;
    private Button logoutBtn, editProfileBtn, addMoodBtn, viewMoodHistoryBtn;
    private CheckBox cbCompleted;
    private ConstraintLayout dashboardRoot;
    private String userId, token;
    private long currentStatusId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

// Sakrij default title (naziv aplikacije)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        initViews();

        // Uvijek dohvat zadnjeg taska iz baze
        fetchLastTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_add_mood) {
            startActivity(new android.content.Intent(this, CreateMoodActivity.class));
            return true;
        }

        if (id == R.id.menu_mood_history) {
            startActivity(new android.content.Intent(this, MoodListActivity.class));
            return true;
        }

        if (id == R.id.menu_edit_profile) {
            startActivity(new android.content.Intent(this, EditProfileActivity.class));
            return true;
        }
        if (id == R.id.menu_logout) {
            authManager.logout();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void initViews() {
        authManager = new AuthManager(this);
        userId = authManager.getUserId();
        token = authManager.getToken();

        welcomeText = findViewById(R.id.welcomeText);
        logoutBtn = findViewById(R.id.logoutBtn);
        dashboardRoot = findViewById(R.id.dashboardRoot);
        tvTaskTitle = findViewById(R.id.tvTaskTitle);
        tvTaskDescription = findViewById(R.id.tvTaskDescription);
        cbCompleted = findViewById(R.id.cbCompleted);

        fetchUserProfile();

    }
    private void fetchUserProfile() {
        RetrofitClient.getInstance()
                .getApi()
                .getProfile(
                        "Bearer " + token,
                        "eq." + userId
                )
                .enqueue(new ApiCallback<ProfileResponse[]>() {
                    @Override
                    public void onSuccess(ProfileResponse[] response) {
                        if (response != null && response.length > 0) {
                            String fullName = response[0].getFull_name();

                            if (fullName != null && !fullName.isEmpty()) {
                                welcomeText.setText("Dobrodošli, " + fullName);
                            } else {
                                setFallbackUsername();
                            }
                        } else {
                            setFallbackUsername();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setFallbackUsername();
                    }
                });
    }
    private void setFallbackUsername() {
        String email = authManager.getEmail();
        String username = email != null && email.contains("@")
                ? email.split("@")[0]
                : "";

        welcomeText.setText("Dobrodošli, " + username);
    }




    private void fetchLastTask() {
        RetrofitClient.getInstance()
                .getApi()
                .getLastUserTask(
                        "Bearer " + token,
                        "eq." + userId,
                        "*,task:mental_tasks(*)",
                        "id.desc",
                        1
                )
                .enqueue(new ApiCallback<UserTaskStatusResponse[]>() {
                    @Override
                    public void onSuccess(UserTaskStatusResponse[] response) {
                        if (response == null || response.length == 0 || response[0].getTask() == null) {
                            tvTaskTitle.setText("Nema zadnjeg taska");
                            tvTaskDescription.setText("");
                            cbCompleted.setVisibility(View.GONE);
                            return;
                        }

                        UserTaskStatusResponse lastTask = response[0];
                        tvTaskTitle.setText(lastTask.getTask().getTitle());
                        tvTaskDescription.setText(lastTask.getTask().getDescription());
                        cbCompleted.setChecked(lastTask.isCompleted());
                        cbCompleted.setEnabled(!lastTask.isCompleted());
                        currentStatusId = lastTask.getId();
                        cbCompleted.setVisibility(View.VISIBLE);

                        if (!lastTask.isCompleted()) {
                            cbCompleted.setOnClickListener(v -> {
                                cbCompleted.setEnabled(false);
                                cbCompleted.setChecked(true);
                                markTaskCompleted();
                            });
                        } else {
                            cbCompleted.setOnClickListener(null);
                        }
                        cbCompleted.setText(
                                lastTask.isCompleted() ? "Bravo, završili ste zadatak!" : "Završite zadatak!"
                        );


                    }

                    @Override
                    public void onError(String errorMessage) {
                        cbCompleted.setText("Završite zadatak!");
                        Toast.makeText(DashboardActivity.this, "Greška: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void markTaskCompleted() {
        String completedAt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                .format(new Date());

        UpdateUserTaskRequest updateRequest =
                new UpdateUserTaskRequest(true, completedAt);

        RetrofitClient.getInstance()
                .getApi()
                .updateUserTask(
                        "Bearer " + token,
                        "eq." + currentStatusId,
                        updateRequest
                )
                .enqueue(new ApiCallback<Void>() {
                    @Override
                    public void onSuccess(Void response) {
                        Toast.makeText(DashboardActivity.this, "Task označen kao urađen!", Toast.LENGTH_SHORT).show();
                        cbCompleted.setChecked(true);
                        cbCompleted.setEnabled(false);
                        cbCompleted.setText("Bravo, završili ste zadatak!");

                    }

                    @Override
                    public void onError(String errorMessage) {
                        cbCompleted.setChecked(false);
                        cbCompleted.setEnabled(true);
                        Toast.makeText(DashboardActivity.this, "Greška pri updateu: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

}
