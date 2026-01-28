package ba.sum.fsre.dnevnikraspolozenja.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.dnevnikraspolozenja.R;

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
