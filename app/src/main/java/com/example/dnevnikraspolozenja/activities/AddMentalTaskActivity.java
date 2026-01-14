package com.example.dnevnikraspolozenja.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dnevnikraspolozenja.R;
import com.example.dnevnikraspolozenja.api.ApiCallback;
import com.example.dnevnikraspolozenja.api.RetrofitClient;
import com.example.dnevnikraspolozenja.models.request.CreateMentalTaskRequest;
import com.example.dnevnikraspolozenja.utils.AuthManager;

public class AddMentalTaskActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etMoodLevel;
    private Button btnSaveTask;
    private ProgressBar progressBar;

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mental_task);

        authManager = new AuthManager(this);

        initViews();
        setupSaveButton();
    }

    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etMoodLevel = findViewById(R.id.etMoodLevel);
        btnSaveTask = findViewById(R.id.btnSaveTask);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupSaveButton() {
        btnSaveTask.setOnClickListener(v -> createMentalTask());
    }

    private void createMentalTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String moodLevelStr = etMoodLevel.getText().toString().trim();

        if (title.isEmpty() || moodLevelStr.isEmpty()) {
            Toast.makeText(this, "Naslov i mood level su obavezni", Toast.LENGTH_SHORT).show();
            return;
        }

        int moodLevel;
        try {
            moodLevel = Integer.parseInt(moodLevelStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Mood level mora biti broj (1–5)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (moodLevel < 1 || moodLevel > 5) {
            Toast.makeText(this, "Mood level mora biti između 1 i 5", Toast.LENGTH_SHORT).show();
            return;
        }

        CreateMentalTaskRequest request =
                new CreateMentalTaskRequest(title, description, moodLevel);

        setLoading(true);

        RetrofitClient.getInstance()
                .getApi()
                .createMentalTask("Bearer " + authManager.getToken(), request)
                .enqueue(new ApiCallback<Void>() {

                    @Override
                    public void onSuccess(Void response) {
                        setLoading(false);
                        Toast.makeText(AddMentalTaskActivity.this,
                                "Mentalni task uspješno dodan",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setLoading(false);
                        Toast.makeText(AddMentalTaskActivity.this,
                                "Greška: " + errorMessage,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSaveTask.setEnabled(!loading);
    }
}
