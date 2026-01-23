package com.example.dnevnikraspolozenja.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.dnevnikraspolozenja.R;
import com.example.dnevnikraspolozenja.api.ApiCallback;
import com.example.dnevnikraspolozenja.api.RetrofitClient;
import com.example.dnevnikraspolozenja.models.MentalTask;
import com.example.dnevnikraspolozenja.models.request.CreateMoodRequest;
import com.example.dnevnikraspolozenja.models.request.UserTaskRequest;
import com.example.dnevnikraspolozenja.utils.AuthManager;
import java.util.List;
import java.util.Random;


public class CreateMoodActivity extends AppCompatActivity {

    private Button btnMood1, btnMood2, btnMood3, btnMood4, btnMood5;
    private TextView tvSelectedMood;
    private EditText etNote;
    private Button btnSaveMood;
    private ProgressBar progressBar;
    private int selectedMoodScore = 0;
    private String userId;
    private String token;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mood);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

// Sakrij default title (naziv aplikacije)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        initViews();
        loadUserData();
        setupMoodButtons();
        setupSaveButton();
    }

    private void initViews() {
        btnMood1 = findViewById(R.id.btnMood1);
        btnMood2 = findViewById(R.id.btnMood2);
        btnMood3 = findViewById(R.id.btnMood3);
        btnMood4 = findViewById(R.id.btnMood4);
        btnMood5 = findViewById(R.id.btnMood5);
        tvSelectedMood = findViewById(R.id.tvSelectedMood);
        etNote = findViewById(R.id.etNote);
        btnSaveMood = findViewById(R.id.btnSaveMood);
        progressBar = findViewById(R.id.progressBar);
        authManager = new AuthManager(this);
    }

    private void loadUserData() {
        userId = authManager.getUserId();
        token = authManager.getToken();

        if (userId == null || token == null) {
            Toast.makeText(this, "Niste prijavljeni!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupMoodButtons() {
        View.OnClickListener moodClickListener = v -> {
            resetMoodButtons();

            if (v.getId() == R.id.btnMood1) {
                selectedMoodScore = 1;
                btnMood1.setAlpha(1.0f);
                tvSelectedMood.setText("Odabrano: Vrlo loše (1)");
            } else if (v.getId() == R.id.btnMood2) {
                selectedMoodScore = 2;
                btnMood2.setAlpha(1.0f);
                tvSelectedMood.setText("Odabrano: Loše (2)");
            } else if (v.getId() == R.id.btnMood3) {
                selectedMoodScore = 3;
                btnMood3.setAlpha(1.0f);
                tvSelectedMood.setText("Odabrano: Neutralno (3)");
            } else if (v.getId() == R.id.btnMood4) {
                selectedMoodScore = 4;
                btnMood4.setAlpha(1.0f);
                tvSelectedMood.setText("Odabrano: Dobro (4)");
            } else if (v.getId() == R.id.btnMood5) {
                selectedMoodScore = 5;
                btnMood5.setAlpha(1.0f);
                tvSelectedMood.setText("Odabrano: Odlično (5)");
            }
        };

        btnMood1.setOnClickListener(moodClickListener);
        btnMood2.setOnClickListener(moodClickListener);
        btnMood3.setOnClickListener(moodClickListener);
        btnMood4.setOnClickListener(moodClickListener);
        btnMood5.setOnClickListener(moodClickListener);

        resetMoodButtons();
    }

    private void resetMoodButtons() {
        btnMood1.setAlpha(0.5f);
        btnMood2.setAlpha(0.5f);
        btnMood3.setAlpha(0.5f);
        btnMood4.setAlpha(0.5f);
        btnMood5.setAlpha(0.5f);
    }

    private void setupSaveButton() {
        btnSaveMood.setOnClickListener(v -> {
            if (selectedMoodScore == 0) {
                Toast.makeText(this, "Molimo odaberite raspoloženje!", Toast.LENGTH_SHORT).show();
                return;
            }
            saveMoodEntry();
        });
    }

    private void saveMoodEntry() {
        String note = etNote.getText().toString().trim();
        if (note.isEmpty()) note = null;

        CreateMoodRequest request = new CreateMoodRequest(userId, selectedMoodScore, note);

        progressBar.setVisibility(View.VISIBLE);
        btnSaveMood.setEnabled(false);

        RetrofitClient.getInstance()
                .getApi()
                .createMoodEntry("Bearer " + token, request)
                .enqueue(new ApiCallback<Void>() {
                    @Override
                    public void onSuccess(Void response) {
                        SharedPreferences prefs = getSharedPreferences("mood_prefs", MODE_PRIVATE);
                        prefs.edit()
                                .putString(
                                        "last_mood_date",
                                        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                                .format(new Date())
                                )
                                .apply();
                        // Nakon što je mood spremljen, dohvat random taska
                        fetchRandomTaskAndInsert();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        progressBar.setVisibility(View.GONE);
                        btnSaveMood.setEnabled(true);
                        Toast.makeText(CreateMoodActivity.this,
                                "Greška: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void insertUserTask(long taskId, MentalTask randomTask) {
        UserTaskRequest request = new UserTaskRequest(userId, taskId);
        request.setCompleted(false); // boolean postavljen preko settera

        RetrofitClient.getInstance()
                .getApi()
                .insertUserTask("Bearer " + token, request)
                .enqueue(new ApiCallback<Void>() {
                    @Override
                    public void onSuccess(Void response) {
                        goToDashboard(randomTask); // šaljemo task direktno
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(CreateMoodActivity.this,
                                "Greška pri dodjeljivanju taska: " + errorMessage, Toast.LENGTH_LONG).show();
                        goToDashboard(randomTask);
                    }
                });
    }

    private void fetchRandomTaskAndInsert() {
        String moodFilter = "cs.{" + selectedMoodScore + "}";

        RetrofitClient.getInstance()
                .getApi()
                .getTasksForMood("Bearer " + token, moodFilter, "id.asc", 100)
                .enqueue(new ApiCallback<List<MentalTask>>() {
                    @Override
                    public void onSuccess(List<MentalTask> tasks) {
                        Toast.makeText(CreateMoodActivity.this,
                                "Pronađeno " + tasks.size() + " taskova za raspoloženje " + selectedMoodScore,
                                Toast.LENGTH_SHORT).show();

                        if (tasks.isEmpty()) {
                            goToDashboard(null);
                            return;
                        }

                        // random odabir
                        MentalTask randomTask = tasks.get(new Random().nextInt(tasks.size()));
                        insertUserTask(randomTask.getId(), randomTask);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(CreateMoodActivity.this,
                                "Greška pri dohvaćanju taskova: " + errorMessage,
                                Toast.LENGTH_LONG).show();
                        goToDashboard(null);
                    }
                });
    }


    private void goToDashboard(MentalTask task) {
        progressBar.setVisibility(View.GONE);
        btnSaveMood.setEnabled(true);

        Toast.makeText(CreateMoodActivity.this,
                "Mood i task uspješno spremljeni!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(CreateMoodActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // šaljemo task u Dashboard ako postoji
        if (task != null) {
            intent.putExtra("task_title", task.getTitle());
            intent.putExtra("task_description", task.getDescription());
        }

        startActivity(intent);
        finish();
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
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
