package com.example.dnevnikraspolozenja.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dnevnikraspolozenja.R;
import com.example.dnevnikraspolozenja.models.MentalTask;

import java.util.List;

public class MentalTaskAdapter
        extends RecyclerView.Adapter<MentalTaskAdapter.ViewHolder> {

    private List<MentalTask> tasks;

    public MentalTaskAdapter(List<MentalTask> tasks) {
        this.tasks = tasks;
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
        holder.difficulty.setText(task.getDifficulty());
        holder.category.setText(task.getCategory());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, difficulty, category;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.taskTitle);
            description = itemView.findViewById(R.id.taskDescription);
            difficulty = itemView.findViewById(R.id.taskDifficulty);
            category = itemView.findViewById(R.id.taskCategory);
        }
    }
}
