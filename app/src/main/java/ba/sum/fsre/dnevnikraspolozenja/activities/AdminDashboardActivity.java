package ba.sum.fsre.dnevnikraspolozenja.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import ba.sum.fsre.dnevnikraspolozenja.R;
import ba.sum.fsre.dnevnikraspolozenja.api.ApiCallback;
import ba.sum.fsre.dnevnikraspolozenja.api.RetrofitClient;
import ba.sum.fsre.dnevnikraspolozenja.models.response.ProfileResponse;
import ba.sum.fsre.dnevnikraspolozenja.utils.AuthManager;

public class AdminDashboardActivity extends AppCompatActivity {

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        authManager = new AuthManager(this);
        ImageView imgAvatar = findViewById(R.id.imgAvatar);

        String token = "Bearer " + authManager.getToken();
        String userId = authManager.getUserId();

        loadAvatarFromApi(token, userId);
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
    private void loadAvatarFromApi(String token, String userId) {
        RetrofitClient.getInstance()
                .getApi()
                .getProfile(token, "eq." + userId)
                .enqueue(new ApiCallback<ProfileResponse[]>() {
                    @Override
                    public void onSuccess(ProfileResponse[] response) {
                        if (response != null && response.length > 0) {
                            String avatarUrl = response[0].getAvatarUrl();

                            authManager.saveAvatarUrl(avatarUrl);

                            loadAvatar(avatarUrl);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // optional fallback
                    }
                });
    }
    private void loadAvatar(String avatarUrl) {
        ImageView imgAvatar = findViewById(R.id.imgAvatar);
        Log.d("AVATAR_URL", "URL: " + avatarUrl);
        Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.avatar_border)
                .error(R.drawable.avatar_border)
                .circleCrop()
                .into(imgAvatar);
    }
}