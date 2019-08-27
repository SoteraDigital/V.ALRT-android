package com.vsnmobil.valrt.activities;

import java.util.List;
//import
import android.app.Activity;
//import
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
//import
import android.os.Bundle;
//import
import android.text.Html;
import android.view.View;
//import
import android.view.View.OnClickListener;
//import
import android.widget.Button;
//import
import android.widget.ProgressBar;
//import
import android.widget.TextView;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.storage.DatabaseHelper;
/**
 * HistoryLogActivity.java This Activity will show the last 50 records of the
 * history log events like key press, fall detect connect, disconnect and low
 * battery etc,.
 */
public class HistoryLogActivity extends Activity {
    /** The no history log text view. */
    private TextView noHistoryLogTextView;
    /** The content text view. */
    private TextView contentTextView;
    /** The progress bar. */
    private ProgressBar progressBar;
    /** The back button. */
    private Button backButton;
    private Button emailButton;
    /** The history content. */
    private String historyContent = "";
    /** The result. */
    String result = null;
    /** The db helper. */
    private DatabaseHelper dbHelper;
    /** The device history. */
    private List<String> deviceHistory;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historylog);

        backButton = (Button) findViewById(R.id.history_log_back_button);
        emailButton = (Button) findViewById(R.id.shareButton);
        contentTextView = (TextView) findViewById(R.id.history_log_content_textview);
        progressBar = (ProgressBar) findViewById(R.id.history_log_progressbar);
        noHistoryLogTextView = (TextView) findViewById(R.id.history_log_nohistory_textview);

        emailButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLog();
            }
        });
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dbHelper = new DatabaseHelper(this);
        // call the AsyncTask to fetch the record from database.
        HistoryLogTask task = new HistoryLogTask();
        task.execute();
    }
    /**
     * The Class HistoryLogTask.
     */
    // To asynchronously fetch the records from the database.
    private class HistoryLogTask extends AsyncTask<String, Void, String> {
        /* (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
         */
        @Override
        protected String doInBackground(String... arg0) {
            // To retrieve the History Log table content
            deviceHistory = dbHelper.getDeviceHistory();
            return result;
        }
        /* (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            if (deviceHistory.size() == 0) {
                //If no history available invisible the progress bar
                progressBar.setVisibility(View.INVISIBLE);
                // Visible the no history found text view.
                noHistoryLogTextView.setVisibility(View.VISIBLE);
            } else {
                // Fetch all the history record from database.
                for (int i = 0; i < deviceHistory.size(); i++) {
                    historyContent += deviceHistory.get(i) + " \n \n";
                }
                // invisible the progress.
                progressBar.setVisibility(View.INVISIBLE);
                // load the content to text view.
                contentTextView.setText(historyContent);
            }
            super.onPostExecute(result);
        }
    }

    public void emailLog(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@vsnmobil.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "VALRT Debug Log");
        intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(""));
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/file.ext"));
        startActivity(intent);


    }

    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
