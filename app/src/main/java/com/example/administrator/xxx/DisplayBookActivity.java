package com.example.administrator.xxx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class DisplayBookActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setTextSize(20);
        Intent intent = getIntent();
        String book_info = intent.getStringExtra(MainActivity.BOOK);
        Gson gson = new GsonBuilder().create();
        Book book = gson.fromJson(book_info, Book.class);
        textView.setText(
                book.title + " by " + book.author + "\n" +
                book.subtitle + "\n" +
                book.rating.average + " (" + book.rating.numRaters + "人评价）");
        Log.d("xxx", book.alt);
        Log.d("xxx", book.rating.average);
        setContentView(textView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class Rating {
        private String average;
        private Integer max;
        private Integer min;
        private Integer numRaters;
    }

    public class Book {
        private String alt;
        private List author;
        private String title;
        private String subtitle;
        private Rating rating;
    }
}
