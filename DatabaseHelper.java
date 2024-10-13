package com.example.calculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "calculator_history.db";
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_HISTORY = "history";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_EXPRESSION = "expression";
    public static final String COLUMN_RESULT = "result";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_HISTORY + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_EXPRESSION + " TEXT, " +
                    COLUMN_RESULT + " TEXT, " +
                    COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_HISTORY + " ADD COLUMN " + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP");
        }
    }

    // Method to insert calculation history into the database
    public void addHistory(String expression, String result) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXPRESSION, expression);
        values.put(COLUMN_RESULT, result);
        db.insert(TABLE_HISTORY, null, values);
        db.close();
    }

    // Method to get all calculation history (if needed)
    public Cursor getAllHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_HISTORY + " ORDER BY " + COLUMN_TIMESTAMP + " DESC LIMIT 5", null);
    }
}
