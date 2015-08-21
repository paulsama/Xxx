package com.example.administrator.xxx;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MainActivity extends Activity {
    private static final String LOG_TAG = "lllllllllllog";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void searchText(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String isbn = editText.getText().toString();
        Log.d(LOG_TAG, isbn);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            Log.d(LOG_TAG, "fetch data");
            textView.setText("fetching data...");
            new DownloadWebpageTask().execute(isbn);
        } else {
            // display error
            Log.d(LOG_TAG, "No network connection available");
            textView.setText("No network connection available");
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, Book> {
        @Override
        protected Book doInBackground(String... isbn) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(isbn[0]);
            } catch (IOException e) {
                Log.d(LOG_TAG, "Unable to retrieve web page. URL may be invalid.");
                return null;
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Book result) {
            Log.d(LOG_TAG, "rrrrrrrrrrrrrrrrrrrrrrr");
            Log.d(LOG_TAG, result.alt);
            Log.d(LOG_TAG, "rrrrrrrrrrrrrrrrrrrrrrr");
            textView.setText(result.title);
        }
    }

    private Book downloadUrl(String isbn) throws IOException {
        InputStream is = null;

        try {
//            URL url = new URL("https://api.douban.com/v2/book/isbn/9787544713078");
            Log.d(LOG_TAG, "isbn is "+ isbn);
            String string_url = "https://api.douban.com/v2/book/isbn/" + isbn;
            Log.d(LOG_TAG, "url is "+ string_url);
            URL url = new URL(string_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(LOG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return readIt(is);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public class Book {
        private String alt;
        private String title;
        private String subtitle;
    }

    public Book readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte [] buffer = new byte[1024];
        int len;
        while ((len=stream.read(buffer))!=-1){
            baos.write(buffer, 0, len);
        }
        String jsonString = baos.toString();
        Log.d(LOG_TAG, "================================");
        Log.d(LOG_TAG, jsonString);
        Log.d(LOG_TAG, "================================");
        Gson gson = new GsonBuilder().create();
        Book book = gson.fromJson(jsonString, Book.class);
        baos.close();
        return book;
    }

}
