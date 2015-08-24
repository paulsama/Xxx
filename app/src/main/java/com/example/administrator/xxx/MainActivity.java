package com.example.administrator.xxx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    public final static String BOOK = "com.example.xxx.Book";
    private static final String LOG_TAG = "lllllllllllog";
    private TextView textView;
    private String INPUT_STRING;

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

    public void scanBook(View view){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String re = scanResult.getContents();
            Log.d("code", re);
            EditText editText = (EditText) findViewById(R.id.edit_message);
            editText.setText(re);
        }
    }

    public void searchText(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String isbn = editText.getText().toString();
        INPUT_STRING = isbn;
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            Log.d(LOG_TAG, "Fetching data...");
            textView.setText("Fetching data...");
            new DownloadWebpageTask().execute(isbn);
        } else {
            // display error
            Log.d(LOG_TAG, "No network connection available");
            textView.setText("No network connection available");
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... isbn) {

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
        protected void onPostExecute(String result) {
            if (result == null) {
                textView.setText("Can't find book for you input: " + INPUT_STRING);
                return;
            }
            Log.d(LOG_TAG, "rrrrrrrrrrrrrrrrrrrrrrr The response body is:");
            Log.d(LOG_TAG, result);
            Log.d(LOG_TAG, "rrrrrrrrrrrrrrrrrrrrrrr");
            Log.d(LOG_TAG, "Fetching data finished!");
            Intent intent = new Intent(MainActivity.this, DisplayBookActivity.class);
            intent.putExtra(BOOK, result);
            startActivity(intent);
        }
    }

    private String downloadUrl(String isbn) throws IOException {
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
            Log.d(LOG_TAG, "The response status is: " + response);
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

    public String readIt(InputStream stream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte [] buffer = new byte[1024];
        int len;
        while ((len=stream.read(buffer))!=-1){
            baos.write(buffer, 0, len);
        }
        String jsonString = baos.toString();
        baos.close();
        return jsonString;
    }

}
