package com.example.dnevnikraspolozenja.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dnevnikraspolozenja.R;
import com.example.dnevnikraspolozenja.api.ApiCallback;
import com.example.dnevnikraspolozenja.api.RetrofitClient;
import com.example.dnevnikraspolozenja.models.request.ProfileUpdateRequest;
import com.example.dnevnikraspolozenja.models.response.ProfileResponse;
import com.example.dnevnikraspolozenja.utils.AuthManager;
import com.example.dnevnikraspolozenja.utils.Constants;

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

        loadProfile();

        saveProfileBtn.setOnClickListener(v -> updateProfile());
        cancelBtn.setOnClickListener(v -> finish());
    }

    private void loadProfile() {
        setLoading(true);
        String token = "Bearer " + authManager.getToken();
        String userId = authManager.getUserId();
        String filter = "eq." + userId;

        Log.d("EditProfile", "Loading profile");
        Log.d("EditProfile", "Token: " + token);
        Log.d("EditProfile", "UserId: " + userId);
        Log.d("EditProfile", "Filter: " + filter);

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
                            Log.d("EditProfile", "Profile loaded successfully");
                        } else {
                            Log.d("EditProfile", "No profile found");
                            Toast.makeText(EditProfileActivity.this,
                                    "Profil ne postoji",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setLoading(false);
                        Log.e("EditProfile", "Error: " + errorMessage);
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

        Log.d("EditProfile", "Updating profile");

        RetrofitClient.getInstance()
                .getApi()
                .updateProfile(token, filter, request)
                .enqueue(new ApiCallback<ProfileResponse>() {
                    @Override
                    public void onSuccess(ProfileResponse response) {
                        setLoading(false);
                        Log.d("EditProfile", "Profile updated successfully");
                        Toast.makeText(EditProfileActivity.this,
                                "Profil uspješno ažuriran!",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setLoading(false);
                        Log.e("EditProfile", "Update error: " + errorMessage);
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
}
