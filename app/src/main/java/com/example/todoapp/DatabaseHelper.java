package com.example.todoapp;

import android.content.ContentValues;
import android.content.Context;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "EncryptedTasks.db";
    private static final int DATABASE_VERSION = 1;

    // In a real production app, this password should be generated securely and stored in the Android Keystore.
    // For this build, we are using a static passphrase to initialize the SQLCipher encryption.
    public static final String DB_PASSWORD = "SuperSecretPassword123!";

    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DATETIME = "datetime";
    private static final String COLUMN_DESC = "description";
    private static final String COLUMN_COMPLETED = "completed";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // Initialize the SQLCipher library
        System.loadLibrary("sqlcipher");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DATETIME + " TEXT, " +
                COLUMN_DESC + " TEXT, " +
                COLUMN_COMPLETED + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    // --- C.R.U.D. Operations ---

    public void addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase(DB_PASSWORD);
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, task.getName());
        values.put(COLUMN_DATETIME, task.getDateTime());
        values.put(COLUMN_DESC, task.getDescription());
        values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0); // SQLite doesn't have booleans, so we use 1 and 0

        db.insert(TABLE_TASKS, null, values);
        db.close();
    }

    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase(DB_PASSWORD);
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS, null);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATETIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESC)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1
                );
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return taskList;
    }

    public void updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase(DB_PASSWORD);
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, task.getName());
        values.put(COLUMN_DATETIME, task.getDateTime());
        values.put(COLUMN_DESC, task.getDescription());
        values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);

        db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
    }

    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase(DB_PASSWORD);
        db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteCompletedTasks() {
        SQLiteDatabase db = this.getWritableDatabase(DB_PASSWORD);
        // Deletes all rows where the completed column equals 1 (true)
        db.delete(TABLE_TASKS, COLUMN_COMPLETED + " = ?", new String[]{"1"});
        db.close();
    }

    public void deleteAllTasks() {
        SQLiteDatabase db = this.getWritableDatabase(DB_PASSWORD);
        // Passing null deletes every row in the table
        db.delete(TABLE_TASKS, null, null);
        db.close();
    }
}