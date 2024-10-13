package com.example.calculator;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.SimpleCursorAdapter;

public class HistoryActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ListView historyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyListView = findViewById(R.id.history_list_view);
        databaseHelper = new DatabaseHelper(this);

        loadHistory();
    }

    private void loadHistory() {
        Cursor cursor = databaseHelper.getAllHistory();

        String[] fromColumns = {"expression", "result"};
        int[] toViews = {R.id.text_expression, R.id.text_result};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.history_list_item,
                cursor,
                fromColumns,
                toViews,
                0);
        historyListView.setAdapter(adapter);
    }
}
