package ba.sum.fsre.dnevnikraspolozenja.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import ba.sum.fsre.dnevnikraspolozenja.R;
import ba.sum.fsre.dnevnikraspolozenja.api.ApiCallback;
import ba.sum.fsre.dnevnikraspolozenja.api.RetrofitClient;
import ba.sum.fsre.dnevnikraspolozenja.models.request.CreateMentalTaskRequest;
import ba.sum.fsre.dnevnikraspolozenja.utils.AuthManager;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

// Sakrij default title (naziv aplikacije)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
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

        authManager = new AuthManager(this);


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
                                "Pogreška prilikom dodavanja zadatka",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }


    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSaveTask.setEnabled(!loading);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_add_task) {
            startActivity(new Intent(this, AddMentalTaskActivity.class));
            return true;
        }

        if (id == R.id.menu_list_tasks) {
            startActivity(new Intent(this, MentalTaskListActivity.class));
            return true;
        }

        if (id == R.id.menu_admin_logout) {
            authManager.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}