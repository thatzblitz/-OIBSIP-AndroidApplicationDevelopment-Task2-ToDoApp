package com.example.todoapp;
public class Task {
    private int id;
    private String name;
    private String dateTime;
    private String description;
    private boolean isCompleted;

    // Constructor
    public Task(int id, String name, String dateTime, String description, boolean isCompleted) {
        this.id = id;
        this.name = name;
        this.dateTime = dateTime;
        this.description = description;
        this.isCompleted = isCompleted;
    }

    // New Getter and Setter for ID
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}