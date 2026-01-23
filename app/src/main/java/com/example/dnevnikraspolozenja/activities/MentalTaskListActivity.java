package com.example.dnevnikraspolozenja.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

// Sakrij default title (naziv aplikacije)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
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
                            MentalTaskAdapter adapter = new MentalTaskAdapter(
                                    response.body(),
                                    (task, position) -> deleteTask(task, position)
                            );
                            recyclerView.setAdapter(adapter);
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
    private void deleteTask(MentalTask task, int position) {
        String token = "Bearer " + authManager.getToken();

        RetrofitClient.getInstance()
                .getApi()
                .deleteMentalTask(token, "eq." + task.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MentalTaskListActivity.this,
                                    "Task obrisan", Toast.LENGTH_SHORT).show();
                            // Ukloni iz RecyclerView
                            ((MentalTaskAdapter) recyclerView.getAdapter())
                                    .tasks.remove(position);
                            recyclerView.getAdapter().notifyItemRemoved(position);
                        } else {
                            Toast.makeText(MentalTaskListActivity.this,
                                    "Greška pri brisanju: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(MentalTaskListActivity.this,
                                "Greška: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
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
