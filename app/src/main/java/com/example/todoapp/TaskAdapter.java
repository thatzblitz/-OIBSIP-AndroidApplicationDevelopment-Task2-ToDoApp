package com.example.todoapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private TaskListener listener;

    // 1. We create an interface so the Adapter can talk to MainActivity when a card is clicked
    public interface TaskListener {
        void onTaskClick(int position);
        void onTaskChecked(int position, boolean isChecked);
    }
    public TaskAdapter(List<Task> taskList, TaskListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = taskList.get(position);

        holder.textTaskName.setText(currentTask.getName());
        holder.textDateTime.setText(currentTask.getDateTime());
        holder.switchCompletion.setOnCheckedChangeListener(null);

        // Set the state from the database
        holder.switchCompletion.setChecked(currentTask.isCompleted());
        // Handle Optional Description logic
        if (currentTask.getDescription() == null || currentTask.getDescription().trim().isEmpty()) {
            holder.textDescription.setVisibility(View.GONE);
            holder.textShowMore.setVisibility(View.GONE);
        } else {
            holder.textDescription.setVisibility(View.VISIBLE);
            holder.textDescription.setText(currentTask.getDescription());

            // Always reset to 1 line when the list scrolls and recycles views
            holder.textDescription.setMaxLines(1);
            holder.textShowMore.setText("...show more");

            // Hide the button by default
            holder.textShowMore.setVisibility(View.GONE);

            // Wait for Android to lay out the text on the screen, then measure it
            holder.textDescription.post(() -> {
                if (holder.textDescription.getLayout() != null) {
                    // getEllipsisCount checks if the text had to be truncated (cut off) on line 0
                    if (holder.textDescription.getLayout().getEllipsisCount(0) > 0) {
                        holder.textShowMore.setVisibility(View.VISIBLE); // Text is too long, show the button
                    }
                }
            });
        }

        holder.switchCompletion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                int safePosition = holder.getAdapterPosition();
                // Ensure the view hasn't been destroyed while we are clicking it
                if (safePosition != RecyclerView.NO_POSITION) {
                    listener.onTaskChecked(safePosition, isChecked);
                }
            }
        });

        holder.textShowMore.setOnClickListener(v -> {
            if (holder.textDescription.getMaxLines() == 1) {
                holder.textDescription.setMaxLines(Integer.MAX_VALUE);
                holder.textShowMore.setText("...show less");
            } else {
                holder.textDescription.setMaxLines(1);
                holder.textShowMore.setText("...show more");
            }
        });

        // 4. Trigger the click listener when the card is tapped
        holder.itemView.setOnClickListener(v -> listener.onTaskClick(position));
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textTaskName, textDateTime, textDescription, textShowMore;
        SwitchMaterial switchCompletion;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textTaskName = itemView.findViewById(R.id.textTaskName);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            textDescription = itemView.findViewById(R.id.textDescription);
            textShowMore = itemView.findViewById(R.id.textShowMore);
            switchCompletion = itemView.findViewById(R.id.switchCompletion);
        }
    }
}