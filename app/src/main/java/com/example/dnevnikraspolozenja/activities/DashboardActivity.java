package com.example.dnevnikraspolozenja.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.dnevnikraspolozenja.R;
import com.example.dnevnikraspolozenja.api.ApiCallback;
import com.example.dnevnikraspolozenja.api.RetrofitClient;
import com.example.dnevnikraspolozenja.models.request.UpdateUserTaskRequest;
import com.example.dnevnikraspolozenja.models.request.UserTaskRequest;
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

        initViews();
        setupNavigation();

        // Uvijek dohvat zadnjeg taska iz baze
        fetchLastTask();
    }

    private void initViews() {
        authManager = new AuthManager(this);
        userId = authManager.getUserId();
        token = authManager.getToken();

        welcomeText = findViewById(R.id.welcomeText);
        logoutBtn = findViewById(R.id.logoutBtn);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        addMoodBtn = findViewById(R.id.addMoodBtn);
        viewMoodHistoryBtn = findViewById(R.id.viewMoodHistoryBtn);
        dashboardRoot = findViewById(R.id.dashboardRoot);
        tvTaskTitle = findViewById(R.id.tvTaskTitle);
        tvTaskDescription = findViewById(R.id.tvTaskDescription);
        cbCompleted = findViewById(R.id.cbCompleted);

        // Pozdrav korisniku
        String email = authManager.getEmail();
        String username = email != null && email.contains("@") ? email.split("@")[0] : "";
        welcomeText.setText("Dobrodošli, " + username);
    }

    private void setupNavigation() {
        addMoodBtn.setOnClickListener(v -> startActivity(new android.content.Intent(this, com.example.dnevnikraspolozenja.activities.CreateMoodActivity.class)));
        editProfileBtn.setOnClickListener(v -> startActivity(new android.content.Intent(this, com.example.dnevnikraspolozenja.activities.EditProfileActivity.class)));
        viewMoodHistoryBtn.setOnClickListener(v -> startActivity(new android.content.Intent(this, com.example.dnevnikraspolozenja.activities.MoodListActivity.class)));
        logoutBtn.setOnClickListener(v -> {
            authManager.logout();
            finish();
        });
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
                        currentStatusId = lastTask.getId();
                        cbCompleted.setVisibility(View.VISIBLE);

                        // Checkbox click listener
                        cbCompleted.setOnClickListener(v -> {
                            cbCompleted.setEnabled(false); // sprječava višestruki click
                            markTaskCompleted();
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
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
                        cbCompleted.setEnabled(true);
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
