package com.example.dnevnikraspolozenja.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dnevnikraspolozenja.R;
import com.example.dnevnikraspolozenja.adapters.MoodAdapter;
import com.example.dnevnikraspolozenja.api.ApiCallback;
import com.example.dnevnikraspolozenja.api.RetrofitClient;
import com.example.dnevnikraspolozenja.models.response.MoodEntryResponse;
import com.example.dnevnikraspolozenja.utils.AuthManager;

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
                        Toast.makeText(MoodListActivity.this, "Greška: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
