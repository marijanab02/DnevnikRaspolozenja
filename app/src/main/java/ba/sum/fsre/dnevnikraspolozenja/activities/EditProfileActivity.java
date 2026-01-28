package ba.sum.fsre.dnevnikraspolozenja.activities;

import static ba.sum.fsre.dnevnikraspolozenja.utils.Constants.BASE_URL;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import ba.sum.fsre.dnevnikraspolozenja.R;
import ba.sum.fsre.dnevnikraspolozenja.adapters.AvatarAdapter;
import ba.sum.fsre.dnevnikraspolozenja.api.ApiCallback;
import ba.sum.fsre.dnevnikraspolozenja.api.RetrofitClient;
import ba.sum.fsre.dnevnikraspolozenja.models.request.ProfileUpdateRequest;
import ba.sum.fsre.dnevnikraspolozenja.models.response.ProfileResponse;
import ba.sum.fsre.dnevnikraspolozenja.utils.AuthManager;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;

public class EditProfileActivity extends AppCompatActivity {

    private EditText fullNameInput, dobInput;
    private Button saveProfileBtn, cancelBtn;
    private ProgressBar progressBar;
    private AuthManager authManager;
    private ViewPager2 avatarViewPager;
    private AvatarAdapter avatarAdapter;
    private Button deleteProfileBtn;
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
        setContentView(R.layout.activity_edit_profile);

        authManager = new AuthManager(this);

        fullNameInput = findViewById(R.id.fullNameInput);
        dobInput = findViewById(R.id.dobInput);
        saveProfileBtn = findViewById(R.id.saveProfileBtn);
        progressBar = findViewById(R.id.progressBar);
        cancelBtn = findViewById(R.id.cancelBtn);
        deleteProfileBtn = findViewById(R.id.deleteProfileBtn);
        deleteProfileBtn.setOnClickListener(v -> confirmDeleteProfile());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

// Sakrij default title (naziv aplikacije)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        avatarViewPager = findViewById(R.id.avatarViewPager);

        avatarAdapter = new AvatarAdapter(this, avatarUrls);
        avatarViewPager.setAdapter(avatarAdapter);
        avatarViewPager.setOffscreenPageLimit(3);

        avatarViewPager.setPageTransformer((page, position) -> {
            float absPos = Math.abs(position);
            page.setScaleX(1 - (0.25f * absPos));
            page.setScaleY(1 - (0.25f * absPos));
            page.setAlpha(1 - (0.5f * absPos));
            page.setTranslationX(-100 * position);
        });

        avatarViewPager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        avatarAdapter.selectedPosition = position;
                        avatarAdapter.notifyDataSetChanged();
                    }
                }
        );
        ImageView imgAvatar = findViewById(R.id.imgAvatar);

        String avatarUrl = authManager.getAvatarUrl();
        if (avatarUrl != null) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.avatar_border)
                    .error(R.drawable.avatar_border)
                    .circleCrop()
                    .into(imgAvatar);
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
                        String currentAvatar = response[0].getAvatarUrl();

                        if (currentAvatar != null) {
                            int index = avatarUrls.indexOf(currentAvatar);
                            if (index != -1) {
                                avatarViewPager.setCurrentItem(index, false);
                                avatarAdapter.selectedPosition = index;
                            }
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setLoading(false);
                        Toast.makeText(EditProfileActivity.this,
                                "Greška prilikom učitavanja profila ",
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
        String selectedAvatarUrl = avatarAdapter.getSelectedAvatarUrl();

        setLoading(true);
        String token = "Bearer " + authManager.getToken();
        String userId = authManager.getUserId();
        String filter = "eq." + userId;
        ProfileUpdateRequest request = new ProfileUpdateRequest(userId, fullName, dob, selectedAvatarUrl);

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
                                "Greška prilikom ažuriranja profila ",
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
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void confirmDeleteProfile() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Brisanje profila")
                .setMessage("Jeste li sigurni? Ova radnja je nepovratna.")
                .setPositiveButton("Obriši", (dialog, which) -> deleteProfile())
                .setNegativeButton("Odustani", null)
                .show();
    }
    private void deleteProfile() {
        setLoading(true);

        String token = "Bearer " + authManager.getToken();
        Log.d("JWT", authManager.getToken());

        RetrofitClient.getInstance()
                .getApi()
                .deleteAuthUser(token)
                .enqueue(new ApiCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody response) {
                        setLoading(false);
                        authManager.logout();

                        Toast.makeText(
                                EditProfileActivity.this,
                                "Račun je trajno obrisan",
                                Toast.LENGTH_LONG
                        ).show();

                        Intent intent = new Intent(
                                EditProfileActivity.this,
                                LoginActivity.class
                        );
                        intent.setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                        );
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setLoading(false);
                        Toast.makeText(
                                EditProfileActivity.this,
                                "Greška prilikom brisanja računa",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }


}