package ba.sum.fsre.dnevnikraspolozenja.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ba.sum.fsre.dnevnikraspolozenja.R;
import ba.sum.fsre.dnevnikraspolozenja.api.ApiCallback;
import ba.sum.fsre.dnevnikraspolozenja.api.RetrofitClient;
import ba.sum.fsre.dnevnikraspolozenja.models.request.LoginRequest;
import ba.sum.fsre.dnevnikraspolozenja.models.response.AuthResponse;
import ba.sum.fsre.dnevnikraspolozenja.models.response.ProfileResponse;
import ba.sum.fsre.dnevnikraspolozenja.utils.AuthManager;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginBtn;
    private TextView registerText;
    private ProgressBar progressBar;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authManager = new AuthManager(this);

        // Ako je korisnik već prijavljen → preskoči login
        if (authManager.isLoggedIn()) {
            redirectByRole();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.loginBtn);
        registerText = findViewById(R.id.registerText);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        loginBtn.setOnClickListener(v -> loginUser());

        registerText.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (!validateInput(email, password)) return;

        setLoading(true);

        LoginRequest request = new LoginRequest(email, password);

        RetrofitClient.getInstance()
                .getApi()
                .login(request)
                .enqueue(new ApiCallback<AuthResponse>() {
                    @Override
                    public void onSuccess(AuthResponse response) {
                        setLoading(false);
                        handleLoginSuccess(response);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setLoading(false);
                        Toast.makeText(LoginActivity.this, "Netočni korisnički podaci", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            emailInput.setError("Unesite email");
            return false;
        }
        if (password.isEmpty()) {
            passwordInput.setError("Unesite lozinku");
            return false;
        }
        return true;
    }

    private void handleLoginSuccess(AuthResponse response) {
        authManager.saveToken(response.getAccessToken());
        authManager.saveEmail(response.getUser().getEmail());
        authManager.saveUserId(response.getUser().getId());


        Toast.makeText(this, "Uspješna prijava!", Toast.LENGTH_SHORT).show();
        fetchUserRoleAndRedirect();
    }

    private void goToMain() {
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }
    private void fetchUserRoleAndRedirect() {
        String token = "Bearer " + authManager.getToken();
        String userId = authManager.getUserId();
        String filter = "eq." + userId;

        RetrofitClient.getInstance()
                .getApi()
                .getProfile(token, filter)
                .enqueue(new ApiCallback<ProfileResponse[]>() {
                    @Override
                    public void onSuccess(ProfileResponse[] response) {
                        if (response != null && response.length > 0) {
                            String role = response[0].getRole();
                            authManager.saveRole(role);

                            if ("admin".equals(role)) {
                                startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                            } else {
                                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            }
                            finish();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(LoginActivity.this,
                                "Greška pri dohvaćanju role",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        loginBtn.setEnabled(!isLoading);
    }
    private void redirectByRole() {
        String role = authManager.getRole();

        if ("admin".equals(role)) {
            startActivity(new Intent(this, AdminDashboardActivity.class));
        } else {
            startActivity(new Intent(this, DashboardActivity.class));
        }
        finish();
    }
}