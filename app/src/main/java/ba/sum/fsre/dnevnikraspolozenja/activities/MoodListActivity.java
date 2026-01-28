package ba.sum.fsre.dnevnikraspolozenja.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import ba.sum.fsre.dnevnikraspolozenja.R;
import ba.sum.fsre.dnevnikraspolozenja.adapters.MoodAdapter;
import ba.sum.fsre.dnevnikraspolozenja.api.ApiCallback;
import ba.sum.fsre.dnevnikraspolozenja.api.RetrofitClient;
import ba.sum.fsre.dnevnikraspolozenja.models.response.MoodEntryResponse;
import ba.sum.fsre.dnevnikraspolozenja.utils.AuthManager;

import java.util.Arrays;
import java.util.List;

public class MoodListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_list);

        recyclerView = findViewById(R.id.recyclerViewMoods);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

        loadMoodEntries();
    }

    private void loadMoodEntries() {
        String token = authManager.getToken();
        String userId = authManager.getUserId();

        RetrofitClient.getInstance()
                .getApi()
                .getMoodEntries(
                        "Bearer " + token,
                        "eq." + userId,
                        "created_at.desc",
                        "eq.false"
                )
                .enqueue(new ApiCallback<MoodEntryResponse[]>() {
                    @Override
                    public void onSuccess(MoodEntryResponse[] response) {
                        List<MoodEntryResponse> moodList = Arrays.asList(response);

                        recyclerView.setAdapter(
                                new MoodAdapter(moodList, token)
                        );
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(MoodListActivity.this, "Greška pri učitavanju ", Toast.LENGTH_LONG).show();
                    }
                });
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