package ba.sum.fsre.dnevnikraspolozenja.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ba.sum.fsre.dnevnikraspolozenja.R;
import ba.sum.fsre.dnevnikraspolozenja.models.CalendarDay;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private List<CalendarDay> days;

    public CalendarAdapter(List<CalendarDay> days) {
        this.days = days;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CalendarDay day = days.get(position);

        if (day.getDate() == null) {
            holder.tvDay.setText("");
            holder.tvEmoji.setText("");
            return;
        }

        holder.tvDay.setText(String.valueOf(day.getDate().getDayOfMonth()));

        if (day.hasMood()) {
            holder.tvEmoji.setText(emojiForMood(day.getMoodScore()));
        } else {
            holder.tvEmoji.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvEmoji;

        ViewHolder(View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvEmoji = itemView.findViewById(R.id.tvEmoji);
        }
    }
}