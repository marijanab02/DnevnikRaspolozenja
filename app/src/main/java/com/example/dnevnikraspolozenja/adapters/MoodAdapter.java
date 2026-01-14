package com.example.dnevnikraspolozenja.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dnevnikraspolozenja.R;
import com.example.dnevnikraspolozenja.api.ApiCallback;
import com.example.dnevnikraspolozenja.api.RetrofitClient;
import com.example.dnevnikraspolozenja.models.request.UpdateMoodRequest;
import com.example.dnevnikraspolozenja.models.response.MoodEntryResponse;

import java.util.ArrayList;
import java.util.List;

public class MoodAdapter extends RecyclerView.Adapter<MoodAdapter.ViewHolder> {

    private List<MoodEntryResponse> moods;
    private String token;

    public MoodAdapter(List<MoodEntryResponse> moods, String token) {
        this.moods = new ArrayList<>(moods);
        this.token = token;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mood, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MoodEntryResponse mood = moods.get(position);

        String moodText;
        switch (mood.getMoodScore()) {
            case 1: moodText = "😢 Vrlo loše (1)"; break;
            case 2: moodText = "😟 Loše (2)"; break;
            case 3: moodText = "😐 Neutralno (3)"; break;
            case 4: moodText = "🙂 Dobro (4)"; break;
            case 5: moodText = "😄 Odlično (5)"; break;
            default: moodText = "Raspoloženje";
        }
        holder.tvMood.setText(moodText);
        holder.tvDate.setText(mood.getCreatedAt());

        if (mood.getNote() == null || mood.getNote().isEmpty()) {
            holder.tvNote.setVisibility(View.GONE);
        } else {
            holder.tvNote.setVisibility(View.VISIBLE);
            holder.tvNote.setText(mood.getNote());
        }

        int bgColor;
        int textColor;
        switch (mood.getMoodScore()) {
            case 1: bgColor = 0xFFE57373; textColor = Color.WHITE; break; // crvena
            case 2: bgColor = 0xFFFFB74D; textColor = Color.BLACK; break; // narančasta
            case 3: bgColor = 0xFFFFF176; textColor = Color.BLACK; break; // žuta
            case 4: bgColor = 0xFFAED581; textColor = Color.BLACK; break; // svijetlozelena
            case 5: bgColor = 0xFF81C784; textColor = Color.WHITE; break; // tamnozelena
            default: bgColor = 0xFFEEEEEE; textColor = Color.BLACK; // siva
        }
        holder.cardMood.setCardBackgroundColor(bgColor);
        holder.tvMood.setTextColor(textColor);
        holder.tvDate.setTextColor(textColor);
        holder.tvNote.setTextColor(textColor);

        holder.btnDelete.setOnClickListener(v -> {
            UpdateMoodRequest request = new UpdateMoodRequest(true);
            RetrofitClient.getInstance()
                    .getApi()
                    .softDeleteMood("Bearer " + token, "eq." + mood.getId(), request)
                    .enqueue(new ApiCallback<Void>() {
                        @Override
                        public void onSuccess(Void response) {
                            Toast.makeText(v.getContext(), "Mood obrisan", Toast.LENGTH_SHORT).show();
                            moods.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, moods.size());
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(v.getContext(), "Greška: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return moods.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMood, tvDate, tvNote;
        Button btnDelete;
        CardView cardMood;

        ViewHolder(View itemView) {
            super(itemView);
            tvMood = itemView.findViewById(R.id.tvMood);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNote = itemView.findViewById(R.id.tvNote);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            cardMood = itemView.findViewById(R.id.cardMood); // važno!
        }
    }
}
