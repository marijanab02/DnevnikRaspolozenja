package ba.sum.fsre.dnevnikraspolozenja.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dnevnikraspolozenja.R;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ba.sum.fsre.dnevnikraspolozenja.adapters.CalendarAdapter;
import ba.sum.fsre.dnevnikraspolozenja.api.ApiCallback;
import ba.sum.fsre.dnevnikraspolozenja.api.RetrofitClient;
import ba.sum.fsre.dnevnikraspolozenja.models.CalendarDay;
import ba.sum.fsre.dnevnikraspolozenja.models.response.MoodEntryResponse;
import ba.sum.fsre.dnevnikraspolozenja.utils.AuthManager;



public class MoodCalendarActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CalendarAdapter adapter;
    private AuthManager authManager;

    private YearMonth currentMonth;

    private TextView tvMonthTitle;
    private Button btnPrev, btnNext, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_calendar);

        recyclerView = findViewById(R.id.recyclerViewCalendar);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7));

        tvMonthTitle = findViewById(R.id.tvMonthTitle);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);

        authManager = new AuthManager(this);
        currentMonth = YearMonth.now();

        updateMonthTitle();

        btnPrev.setOnClickListener(v -> {
            currentMonth = currentMonth.minusMonths(1);
            updateMonthTitle();
            loadMoodForMonth();
        });

        btnNext.setOnClickListener(v -> {
            currentMonth = currentMonth.plusMonths(1);
            updateMonthTitle();
            loadMoodForMonth();
        });
        btnBack.setOnClickListener(v -> finish());

        loadMoodForMonth();
    }

    private void updateMonthTitle() {
        String monthName = currentMonth.getMonth().name().toLowerCase();
        monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);

        tvMonthTitle.setText(monthName + " " + currentMonth.getYear());
    }

    private void loadMoodForMonth() {
        String token = "Bearer " + authManager.getToken();
        String userId = authManager.getUserId();

        String startDate = currentMonth.atDay(1).toString();
        String endDate = currentMonth.plusMonths(1).atDay(1).toString();

        Map<String, String> filters = new HashMap<>();
        filters.put("user_id", "eq." + userId);
        filters.put("deleted", "eq.false");
        filters.put("select", "id,created_at,mood_score");
        filters.put("created_at", "gte." + startDate);
        filters.put("and", "(created_at.lt." + endDate + ")"); // AND operator za range

        Log.d("MoodCalendar", "Loading moods for: " + currentMonth);
        Log.d("MoodCalendar", "Date range: " + startDate + " to " + endDate);

        RetrofitClient.getInstance()
                .getApi()
                .getMoodEntriesForMonth(token, filters)
                .enqueue(new ApiCallback<MoodEntryResponse[]>() {
                    @Override
                    public void onSuccess(MoodEntryResponse[] response) {
                        Log.d("MoodCalendar", "Response received: " + response.length + " entries");
                        buildCalendar(response);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("MoodCalendar", "Error: " + errorMessage);
                        Toast.makeText(MoodCalendarActivity.this,
                                "Greška: " + errorMessage,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void buildCalendar(MoodEntryResponse[] moods) {

        Map<LocalDate, Integer> moodByDate = new HashMap<>();

        for (MoodEntryResponse mood : moods) {
            LocalDate date = LocalDate.parse(mood.getCreatedAt().substring(0, 10));
            moodByDate.put(date, mood.getMoodScore());
        }

        List<CalendarDay> days = new ArrayList<>();

        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue(); // 1=Mon ... 7=Sun

        for (int i = 1; i < dayOfWeek; i++) {
            days.add(new CalendarDay(null, null));
        }

        int lengthOfMonth = currentMonth.lengthOfMonth();

        for (int day = 1; day <= lengthOfMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            Integer moodScore = moodByDate.get(date);
            days.add(new CalendarDay(date, moodScore));
        }

        adapter = new CalendarAdapter(days);
        recyclerView.setAdapter(adapter);
    }
}
