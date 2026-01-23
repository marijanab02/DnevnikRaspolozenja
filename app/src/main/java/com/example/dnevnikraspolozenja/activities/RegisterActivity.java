package com.example.dnevnikraspolozenja.activities;

import static com.example.dnevnikraspolozenja.utils.Constants.BASE_URL;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.dnevnikraspolozenja.R;
import com.example.dnevnikraspolozenja.adapters.AvatarAdapter;
import com.example.dnevnikraspolozenja.api.ApiCallback;
import com.example.dnevnikraspolozenja.api.RetrofitClient;
import com.example.dnevnikraspolozenja.models.request.ProfileUpdateRequest;
import com.example.dnevnikraspolozenja.models.request.RegisterRequest;
import com.example.dnevnikraspolozenja.models.response.AuthResponse;
import com.example.dnevnikraspolozenja.models.response.ProfileResponse;
import com.example.dnevnikraspolozenja.utils.AuthManager;
import android.widget.DatePicker;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private EditText fullNameInput, dobInput; // Dodaj polja
    private Button registerBtn;
    private TextView backToLoginText;

    private ProgressBar progressBar;
    private AuthManager authManager;
    private ViewPager2 avatarViewPager;
    private AvatarAdapter avatarAdapter;

    // Hardkodirani avatar URL-ovi iz Supabase bucket-a
    private List<String> avatarUrls = Arrays.asList(
            BASE_URL + "storage/v1/object/public/avatars/avatar_1.png",
            BASE_URL + "storage/v1/object/public/avatars/avatar_2.png",
            BASE_URL + "storage/v1/object/public/avatars/avatar_3.png",
            BASE_URL + "storage/v1/object/public/avatars/avatar_4.png",
            BASE_URL + "storage/v1/object/public/avatars/avatar_5.png",
            BASE_URL + "storage/v1/object/public/avatars/avatar_6.png",
            BASE_URL + "storage/v1/object/public/avatars/avatar_7.png",
            BASE_URL + "storage/v1/object/public/avatars/avatar_8.png"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authManager = new AuthManager(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerBtn = findViewById(R.id.registerBtn);
        backToLoginText = findViewById(R.id.backToLoginText);
        progressBar = findViewById(R.id.ProgressBar);
        fullNameInput = findViewById(R.id.fullNameInput);
        dobInput = findViewById(R.id.dobInput);

        avatarViewPager = findViewById(R.id.avatarViewPager);

        avatarAdapter = new AvatarAdapter(this, avatarUrls);
        avatarViewPager.setAdapter(avatarAdapter);
        avatarViewPager.setOffscreenPageLimit(3);

        avatarViewPager.setPageTransformer((page, position) -> {
            float absPos = Math.abs(position);

            // scaling
            float scale = 1 - (0.25f * absPos);   // središnji = 1.0, susjed = 0.75
            page.setScaleY(scale);
            page.setScaleX(scale);

            // opacity
            float alpha = 1 - (0.5f * absPos);    // središnji = 1.0, susjed = 0.5
            page.setAlpha(alpha);

            // optional translation to enhance peeking
            page.setTranslationX(-40 * position);
        });

        avatarViewPager.setCurrentItem(0, false);
        avatarViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                avatarAdapter.selectedPosition = position;
                avatarAdapter.notifyDataSetChanged();
            }
        });



    }
    private void setupListeners() {
        registerBtn.setOnClickListener(v -> registerUser());
        backToLoginText.setOnClickListener(v -> finish());
        dobInput.setOnClickListener(v -> showDatePicker());

    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        String fullName = fullNameInput.getText().toString().trim();

        String dob = dobInput.getText().toString().trim();
        if (!validateInput(email, password, confirmPassword, fullName, dob)) return;

        setLoading(true);

        RegisterRequest request = new RegisterRequest(email, password);

        RetrofitClient.getInstance()
                .getApi()
                .signup(request)
                .enqueue(new ApiCallback<AuthResponse>() {
                    @Override
                    public void onSuccess(AuthResponse response) {
                        setLoading(false);
                        handleRegisterSuccess(response);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setLoading(false);
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInput(String email, String password, String confirmPassword, String dob, String fullName) {

        if (email.isEmpty()) {
            emailInput.setError("Unesite email");
            emailInput.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Neispravan email");
            emailInput.requestFocus();
            return false;
        }
        if (fullName.isEmpty()) {
            passwordInput.setError("Unesite lozinku");
            passwordInput.requestFocus();
            return false;
        }
        if (dob.isEmpty()) {
            passwordInput.setError("Unesite lozinku");
            passwordInput.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            passwordInput.setError("Unesite lozinku");
            passwordInput.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordInput.setError("Lozinka mora imati barem 6 znakova");
            passwordInput.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Lozinke se ne podudaraju");
            confirmPasswordInput.requestFocus();
            return false;
        }

        return true;
    }

    private void handleRegisterSuccess(AuthResponse response) {

        String token = response.getAccessToken();
        String email = response.getEmail();
        String userId = response.getId();

        if (email == null || userId == null) {
            Toast.makeText(this, "Korisnički podaci nisu vraćeni!", Toast.LENGTH_SHORT).show();
            return;
        }

        authManager.saveToken(token);
        authManager.saveEmail(email);
        authManager.saveUserId(userId);

        createProfile(userId);

        Toast.makeText(this, "Registracija uspješna!", Toast.LENGTH_SHORT).show();
    }



    private void createProfile(String userId) {
        String fullName = fullNameInput.getText().toString().trim();
        String dob = dobInput.getText().toString().trim();
        String selectedAvatarUrl = avatarAdapter.getSelectedAvatarUrl();



        if (fullName.isEmpty() || dob.isEmpty()) {
            Toast.makeText(this, "Unesite ime i datum rođenja", Toast.LENGTH_SHORT).show();
            return;
        }
        ProfileUpdateRequest profileRequest = new ProfileUpdateRequest(
                userId, fullName, dob, selectedAvatarUrl
        );

        RetrofitClient.getInstance()
                .getApi()
                .insertProfile(profileRequest)
                .enqueue(new ApiCallback<Void>() {
                    @Override
                    public void onSuccess(Void response) {
                        // Profil uspješno kreiran
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(RegisterActivity.this,
                                "Profil nije kreiran: " + errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                });


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

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? ProgressBar.VISIBLE : ProgressBar.GONE);
        registerBtn.setEnabled(!isLoading);
        backToLoginText.setEnabled(!isLoading);
        emailInput.setEnabled(!isLoading);
        passwordInput.setEnabled(!isLoading);
        confirmPasswordInput.setEnabled(!isLoading);
    }
}