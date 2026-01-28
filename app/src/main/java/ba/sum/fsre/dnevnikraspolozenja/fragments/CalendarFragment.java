package ba.sum.fsre.dnevnikraspolozenja.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ba.sum.fsre.dnevnikraspolozenja.R;
import ba.sum.fsre.dnevnikraspolozenja.adapters.CalendarAdapter;
import ba.sum.fsre.dnevnikraspolozenja.api.ApiCallback;
import ba.sum.fsre.dnevnikraspolozenja.api.RetrofitClient;
import ba.sum.fsre.dnevnikraspolozenja.models.CalendarDay;
import ba.sum.fsre.dnevnikraspolozenja.models.response.MoodEntryResponse;
import ba.sum.fsre.dnevnikraspolozenja.utils.AuthManager;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarFragment extends Fragment {

    private static final String TAG = "CalendarFragment";

    private RecyclerView recyclerView;
    private CalendarAdapter adapter;
    private AuthManager authManager;
    private YearMonth currentMonth;
    private TextView tvMonthTitle;
    private Button btnPrev, btnNext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCalendar);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));

        tvMonthTitle = view.findViewById(R.id.tvMonthTitle);
        btnPrev = view.findViewById(R.id.btnPrev);
        btnNext = view.findViewById(R.id.btnNext);

        authManager = new AuthManager(requireContext());
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

        loadMoodForMonth();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called - refreshing calendar");
        loadMoodForMonth();
    }

    private void updateMonthTitle() {
        String[] mjeseci = {
                "Siječanj", "Veljača", "Ožujak", "Travanj", "Svibanj", "Lipanj",
                "Srpanj", "Kolovoz", "Rujan", "Listopad", "Studeni", "Prosinac"
        };

        int monthIndex = currentMonth.getMonthValue() - 1;
        String monthName = mjeseci[monthIndex];

        tvMonthTitle.setText(monthName + " " + currentMonth.getYear());
        Log.d(TAG, "Updated month title: " + monthName + " " + currentMonth.getYear());
    }

    private void loadMoodForMonth() {
        String token = "Bearer " + authManager.getToken();
        String userId = authManager.getUserId();

        String startDate = currentMonth.atDay(1).toString();
        String endDate = currentMonth.plusMonths(1).atDay(1).toString();

        Log.d(TAG, "Loading moods for user: " + userId);
        Log.d(TAG, "Date range: " + startDate + " to " + endDate);

        Map<String, String> filters = new HashMap<>();
        filters.put("user_id", "eq." + userId);
        filters.put("deleted", "eq.false");
        filters.put("select", "id,created_at,mood_score");
        filters.put("created_at", "gte." + startDate);
        filters.put("and", "(created_at.lt." + endDate + ")");

        RetrofitClient.getInstance()
                .getApi()
                .getMoodEntriesForMonth(token, filters)
                .enqueue(new ApiCallback<MoodEntryResponse[]>() {
                    @Override
                    public void onSuccess(MoodEntryResponse[] response) {


                        buildCalendar(response);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "Error loading moods: " + errorMessage);
                        if (getContext() != null) {
                            Toast.makeText(getContext(),
                                    "Greška pri učitavanju kalendara: " + errorMessage,
                                    Toast.LENGTH_SHORT).show();
                        }
                        // Prikazi prazan kalendar u slučaju greške
                        buildCalendar(new MoodEntryResponse[0]);
                    }
                });
    }

    private void buildCalendar(MoodEntryResponse[] moods) {

        // Mapiramo svaki datum na listu raspoloženja tog dana
        Map<LocalDate, List<Integer>> moodByDate = new HashMap<>();

        for (MoodEntryResponse mood : moods) {
            try {
                LocalDate date = LocalDate.parse(mood.getCreatedAt().substring(0, 10));
                moodByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(mood.getMoodScore());
            } catch (Exception e) {
                Log.e(TAG, "Error parsing date: " + mood.getCreatedAt(), e);
            }
        }

        List<CalendarDay> days = new ArrayList<>();

        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue(); // 1 = PON, 7 = NED

        // Prazni dani prije prvog dana mjeseca
        for (int i = 1; i < dayOfWeek; i++) {
            days.add(new CalendarDay(null, null));
        }

        int lengthOfMonth = currentMonth.lengthOfMonth();

        for (int day = 1; day <= lengthOfMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            List<Integer> scores = moodByDate.get(date);
            Integer moodScore = null;

            if (scores != null && !scores.isEmpty()) {
                // računamo prosjek raspoloženja i zaokružujemo
                int sum = 0;
                for (int s : scores) sum += s;
                moodScore = Math.round((float) sum / scores.size());
            }

            days.add(new CalendarDay(date, moodScore));
        }

        adapter = new CalendarAdapter(days);
        recyclerView.setAdapter(adapter);
    }


    private String emojiForMood(int mood) {
        switch (mood) {
            case 1: return "😢";
            case 2: return "😟";
            case 3: return "😐";
            case 4: return "🙂";
            case 5: return "😄";
            default: return "";
        }
    }


    public void refreshCalendar() {
        loadMoodForMonth();
    }
}