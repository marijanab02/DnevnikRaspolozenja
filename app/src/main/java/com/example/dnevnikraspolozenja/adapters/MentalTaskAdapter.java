package com.example.dnevnikraspolozenja.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dnevnikraspolozenja.R;
import com.example.dnevnikraspolozenja.models.MentalTask;

import java.util.List;

public class MentalTaskAdapter
        extends RecyclerView.Adapter<MentalTaskAdapter.ViewHolder> {

    public List<MentalTask> tasks;
    private OnDeleteClickListener deleteClickListener;

    // Interface za callback
    public interface OnDeleteClickListener {
        void onDelete(MentalTask task, int position);
    }
    public MentalTaskAdapter(List<MentalTask> tasks, OnDeleteClickListener listener) {
        this.tasks = tasks;
        this.deleteClickListener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mental_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MentalTask task = tasks.get(position);

        holder.title.setText(task.getTitle());
        holder.description.setText(task.getDescription());
        holder.moodLevel.setText(
                "Mood levels: " + task.getMoodLevel().toString()
        );
        holder.moodLevel.setText(getMoodEmojis(task.getMoodLevel()));
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDelete(task, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, moodLevel;
        Button btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.taskTitle);
            description = itemView.findViewById(R.id.taskDescription);
            moodLevel = itemView.findViewById(R.id.taskMoodLevel);
            btnDelete = itemView.findViewById(R.id.btnDeleteTask);
        }
    }
    private String getMoodEmojis(List<Integer> moodLevels) {
        StringBuilder sb = new StringBuilder();

        for (int level : moodLevels) {
            switch (level) {
                case 1: sb.append("😢 "); break;
                case 2: sb.append("😟 "); break;
                case 3: sb.append("😐 "); break;
                case 4: sb.append("🙂 "); break;
                case 5: sb.append("😄 "); break;
            }
        }

        return sb.toString().trim();
    }

}
