package com.example.dnevnikraspolozenja.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.dnevnikraspolozenja.R;
import com.example.dnevnikraspolozenja.api.ApiCallback;
import com.example.dnevnikraspolozenja.api.RetrofitClient;
import com.example.dnevnikraspolozenja.models.request.ProfileUpdateRequest;
import com.example.dnevnikraspolozenja.models.response.ProfileResponse;
import com.example.dnevnikraspolozenja.utils.AuthManager;

import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity {

    private EditText fullNameInput, dobInput;
    private Button saveProfileBtn, cancelBtn;
    private ProgressBar progressBar;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        authManager = new AuthManager(this);

        fullNameInput = findViewById(R.id.fullNameInput);
        dobInput = findViewById(R.id.dobInput);
        saveProfileBtn = findViewById(R.id.saveProfileBtn);
        progressBar = findViewById(R.id.progressBar);
        cancelBtn = findViewById(R.id.cancelBtn);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

// Sakrij default title (naziv aplikacije)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Otvori DatePicker kad klikne na dobInput
        dobInput.setFocusable(false);
        dobInput.setClickable(true);
        dobInput.setOnClickListener(v -> showDatePicker());

        loadProfile();

        saveProfileBtn.setOnClickListener(v -> updateProfile());
        cancelBtn.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    dobInput.setText(formattedDate);
                },
                year, month, day
        );

        // Ne dopusti buduće datume
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void loadProfile() {
        setLoading(true);
        String token = "Bearer " + authManager.getToken();
        String userId = authManager.getUserId();
        String filter = "eq." + userId;

        Log.d("EditProfile", "Loading profile");

        RetrofitClient.getInstance()
                .getApi()
                .getProfile(token, filter)
                .enqueue(new ApiCallback<ProfileResponse[]>() {
                    @Override
                    public void onSuccess(ProfileResponse[] response) {
                        setLoading(false);
                        if (response != null && response.length > 0) {
                            fullNameInput.setText(response[0].getFull_name());
                            dobInput.setText(response[0].getDate_of_birth());
                        } else {
                            Toast.makeText(EditProfileActivity.this,
                                    "Profil ne postoji",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setLoading(false);
                        Toast.makeText(EditProfileActivity.this,
                                "Greška: " + errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProfile() {
        String fullName = fullNameInput.getText().toString().trim();
        String dob = dobInput.getText().toString().trim();

        if (fullName.isEmpty() || dob.isEmpty()) {
            Toast.makeText(this, "Popunite sva polja", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        String token = "Bearer " + authManager.getToken();
        String userId = authManager.getUserId();
        String filter = "eq." + userId;
        ProfileUpdateRequest request = new ProfileUpdateRequest(fullName, dob);

        RetrofitClient.getInstance()
                .getApi()
                .updateProfile(token, filter, request)
                .enqueue(new ApiCallback<ProfileResponse>() {
                    @Override
                    public void onSuccess(ProfileResponse response) {
                        setLoading(false);
                        Toast.makeText(EditProfileActivity.this,
                                "Profil uspješno ažuriran!",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setLoading(false);
                        Toast.makeText(EditProfileActivity.this,
                                "Greška: " + errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        saveProfileBtn.setEnabled(!isLoading);
        fullNameInput.setEnabled(!isLoading);
        dobInput.setEnabled(!isLoading);
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
}
