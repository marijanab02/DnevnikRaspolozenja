package com.example.dnevnikraspolozenja.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dnevnikraspolozenja.R;
import com.example.dnevnikraspolozenja.api.ApiCallback;
import com.example.dnevnikraspolozenja.api.RetrofitClient;
import com.example.dnevnikraspolozenja.models.request.CreateMentalTaskRequest;
import com.example.dnevnikraspolozenja.utils.AuthManager;

import java.util.ArrayList;
import java.util.List;

public class AddMentalTaskActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etMoodLevel;
    private Button btnSaveTask;
    private CheckBox cbMood1, cbMood2, cbMood3, cbMood4, cbMood5;
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
        btnSaveTask = findViewById(R.id.btnSaveTask);
        progressBar = findViewById(R.id.progressBar);
        cbMood1 = findViewById(R.id.cbMood1);
        cbMood2 = findViewById(R.id.cbMood2);
        cbMood3 = findViewById(R.id.cbMood3);
        cbMood4 = findViewById(R.id.cbMood4);
        cbMood5 = findViewById(R.id.cbMood5);


    }
    private List<Integer> getSelectedMoodLevels() {

        List<Integer> moodLevels = new ArrayList<>();
        if (cbMood1.isChecked()) moodLevels.add(1);
        if (cbMood2.isChecked()) moodLevels.add(2);
        if (cbMood3.isChecked()) moodLevels.add(3);
        if (cbMood4.isChecked()) moodLevels.add(4);
        if (cbMood5.isChecked()) moodLevels.add(5);
        return moodLevels;
    }

    private void setupSaveButton() {
        btnSaveTask.setOnClickListener(v -> createMentalTask());
    }

    private void createMentalTask() {

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        List<Integer> moodLevels = getSelectedMoodLevels();

        if (title.isEmpty() || moodLevels.isEmpty()) {
            Toast.makeText(this, "Sva polja su obavezna", Toast.LENGTH_SHORT).show();
            return;
        }

        CreateMentalTaskRequest request =
                new CreateMentalTaskRequest(title, description, moodLevels);

        setLoading(true);

        RetrofitClient.getInstance()
                .getApi()
                .createMentalTask(
                        "Bearer " + authManager.getToken(),
                        request
                )
                .enqueue(new ApiCallback<Void>() {

                    @Override
                    public void onSuccess(Void response) {
                        setLoading(false);
                        Toast.makeText(
                                AddMentalTaskActivity.this,
                                "Mentalni task dodan",
                                Toast.LENGTH_SHORT
                        ).show();
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setLoading(false);
                        Toast.makeText(
                                AddMentalTaskActivity.this,
                                errorMessage,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }


    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSaveTask.setEnabled(!loading);
    }
}
