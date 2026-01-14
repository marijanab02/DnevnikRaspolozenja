package com.example.dnevnikraspolozenja.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dnevnikraspolozenja.R;
import com.example.dnevnikraspolozenja.adapters.MentalTaskAdapter;
import com.example.dnevnikraspolozenja.api.RetrofitClient;
import com.example.dnevnikraspolozenja.models.MentalTask;
import com.example.dnevnikraspolozenja.utils.AuthManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MentalTaskListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mental_task_list);

        authManager = new AuthManager(this);

        recyclerView = findViewById(R.id.recyclerMentalTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadMentalTasks();
    }

    private void loadMentalTasks() {
        String token = "Bearer " + authManager.getToken();

        RetrofitClient.getInstance()
                .getApi()
                .getMentalTasks(token)
                .enqueue(new Callback<List<MentalTask>>() {
                    @Override
                    public void onResponse(Call<List<MentalTask>> call,
                                           Response<List<MentalTask>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            recyclerView.setAdapter(
                                    new MentalTaskAdapter(response.body()));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MentalTask>> call, Throwable t) {
                        Toast.makeText(MentalTaskListActivity.this,
                                "Greška pri dohvaćanju taskova",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
