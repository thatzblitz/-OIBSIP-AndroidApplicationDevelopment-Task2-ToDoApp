package com.example.todoapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private FloatingActionButton fabAddTask;
    private TextView textEmptyState;
    private TextView textTaskProgress;
    private DatabaseHelper dbHelper;
    private String selectedDate = "";
    private String selectedTime = "";
    private android.widget.ImageButton fabDeleteTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        fabAddTask = findViewById(R.id.fabAddTask);
        textEmptyState = findViewById(R.id.textEmptyState);

        textTaskProgress = findViewById(R.id.textTaskProgress);

        // Initialize the encrypted database helper
        dbHelper = new DatabaseHelper(this);

        // Load all saved tasks from the database instead of creating an empty list
        taskList = dbHelper.getAllTasks();

        fabDeleteTasks = findViewById(R.id.fabDeleteTasks);

        // Initialize adapter with both Click and Check listeners
        taskAdapter = new TaskAdapter(taskList, new TaskAdapter.TaskListener() {
            @Override
            public void onTaskClick(int position) {
                showTaskDialog(position);
            }
            @Override
            public void onTaskChecked(int position, boolean isChecked) {
                // Update the task state and save immediately to the database
                Task task = taskList.get(position);
                task.setCompleted(isChecked);
                dbHelper.updateTask(task);

                updateUIState();
            }
        });

        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setAdapter(taskAdapter);

        updateUIState();

        // 1. The Plus Icon Logic (Make sure this line exists!)
        fabAddTask.setOnClickListener(v -> showTaskDialog(null));

        // Bulk Delete Dialog Logic
        // 2. The Custom Bulk Delete Dialog Logic
        fabDeleteTasks.setOnClickListener(v -> {
            if (taskList.isEmpty()) return;

            // Inflate your custom layout
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete, null);

            AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                    .setView(dialogView)
                    .create();

            Button btnDeleteCompleted = dialogView.findViewById(R.id.btnDeleteCompleted);
            Button btnDeleteAll = dialogView.findViewById(R.id.btnDeleteAll);

            btnDeleteCompleted.setOnClickListener(view -> {
                dbHelper.deleteCompletedTasks();

                taskList.clear();
                taskList.addAll(dbHelper.getAllTasks());
                taskAdapter.notifyDataSetChanged();
                updateUIState();

                dialog.dismiss();
            });

            btnDeleteAll.setOnClickListener(view -> {
                dbHelper.deleteAllTasks();

                taskList.clear();
                taskList.addAll(dbHelper.getAllTasks());
                taskAdapter.notifyDataSetChanged();
                updateUIState();

                dialog.dismiss();
            });

            dialog.show();

            // CRITICAL: Makes the default square window transparent so your rounded background is visible
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
        });
    }

    private void updateUIState() {
        if (taskList.isEmpty()) {
            textEmptyState.setVisibility(View.VISIBLE);
            recyclerViewTasks.setVisibility(View.GONE);
            textTaskProgress.setVisibility(View.GONE); // Hide if no tasks exist
        } else {
            textEmptyState.setVisibility(View.GONE);
            recyclerViewTasks.setVisibility(View.VISIBLE);
            textTaskProgress.setVisibility(View.VISIBLE);

            // Calculate completed tasks
            int completedCount = 0;
            for (Task task : taskList) {
                if (task.isCompleted()) {
                    completedCount++;
                }
            }

            // Format and display the string
            textTaskProgress.setText(completedCount + "/" + taskList.size() + " tasks completed");
        }
    }

    private void showTaskDialog(Integer position) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);

        EditText editTaskName = dialogView.findViewById(R.id.editTaskName);
        EditText editDescription = dialogView.findViewById(R.id.editDescription);
        Button btnDate = dialogView.findViewById(R.id.btnDate);
        Button btnTime = dialogView.findViewById(R.id.btnTime);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        selectedDate = "";
        selectedTime = "";

        if (position != null) {
            Task existingTask = taskList.get(position);
            editTaskName.setText(existingTask.getName());
            editDescription.setText(existingTask.getDescription());

            String[] dateTimeParts = existingTask.getDateTime().split(", ");
            if(dateTimeParts.length == 2) {
                selectedDate = dateTimeParts[0];
                selectedTime = dateTimeParts[1];
                btnDate.setText(selectedDate);
                btnTime.setText(selectedTime);
            }

            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setText("Delete");
            btnDelete.setTextColor(android.graphics.Color.parseColor("#FF453A"));
        } else {
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setText("Cancel");
            btnDelete.setTextColor(android.graphics.Color.parseColor("#8E8E93"));
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .create();

        btnDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                btnDate.setText(selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                String amPm = hourOfDay >= 12 ? "PM" : "AM";
                int displayHour = hourOfDay > 12 ? hourOfDay - 12 : (hourOfDay == 0 ? 12 : hourOfDay);
                String displayMin = minute < 10 ? "0" + minute : String.valueOf(minute);

                selectedTime = displayHour + ":" + displayMin + " " + amPm;
                btnTime.setText(selectedTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        });

        btnSave.setOnClickListener(v -> {
            String name = editTaskName.getText().toString().trim();
            String desc = editDescription.getText().toString().trim();

            if (name.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
                Toast.makeText(this, "Please enter a name, date, and time.", Toast.LENGTH_SHORT).show();
                return;
            }

            String finalDateTime = selectedDate + ", " + selectedTime;

            if (position == null) {
                // Save NEW task to database
                dbHelper.addTask(new Task(0, name, finalDateTime, desc, false));

                // Fetch the newly updated list
                taskList.clear();
                taskList.addAll(dbHelper.getAllTasks());

                // Scalpel command: Tell the adapter exactly ONE item was added to the bottom
                taskAdapter.notifyItemInserted(taskList.size() - 1);
            } else {
                // Update EXISTING task in database
                Task task = taskList.get(position);
                task.setName(name);
                task.setDateTime(finalDateTime);
                task.setDescription(desc);
                dbHelper.updateTask(task);

                // Fetch the newly updated list
                taskList.clear();
                taskList.addAll(dbHelper.getAllTasks());

                // Scalpel command: Tell the adapter exactly ONE item was updated
                taskAdapter.notifyItemChanged(position);
            }

            // These two lines should already be right below your block
            updateUIState();
            dialog.dismiss();

            updateUIState();
            dialog.dismiss();
        });

        btnDelete.setOnClickListener(v -> {
            if (position != null) {
                // Delete task from database using its unique ID
                Task taskToDelete = taskList.get(position);
                dbHelper.deleteTask(taskToDelete.getId());

                taskList.remove((int) position);
                taskAdapter.notifyItemRemoved(position);
                updateUIState();
            }
            dialog.dismiss();
        });

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
    }
}