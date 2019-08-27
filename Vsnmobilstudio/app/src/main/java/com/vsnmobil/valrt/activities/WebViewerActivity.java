package com.vsnmobil.valrt.activities;

import android.annotation.SuppressLint;
//import
import android.app.Activity;
//import
import android.content.Intent;
//import
import android.os.Bundle;
//import
import android.view.View;
//import
import android.view.View.OnClickListener;
//import
import android.webkit.WebView;
//import
import android.webkit.WebViewClient;
//import
import android.widget.Button;
//import
import android.widget.ProgressBar;
//import
import android.widget.TextView;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
//import
import com.vsnmobil.valrt.utils.Utils;
/**
 * WebViewerActivity.java To View the quick start guide PDF document using the
 * Google Docs service inside web view.
 */
public class WebViewerActivity extends Activity {
    /** The webview. */
    private WebView webview;
    /** The no interne text view. */
    private TextView noInterneTextView;
    /** The title text view. */
    private TextView titleTextView;
    /** The progress bar. */
    private ProgressBar progressBar;
    /** The back button. */
    private Button backButton;
    /** The data intent. */
    private Intent dataIntent;
    /** The url. */
    private String url;
    /** The is faq. */
    private boolean isFAQ = false;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webviewer);
        webview = (WebView) findViewById(R.id.webviewer_webview);
        backButton = (Button) findViewById(R.id.webviewer_back_button);
        progressBar = (ProgressBar) findViewById(R.id.webviewer_progressbar);
        titleTextView = (TextView) findViewById(R.id.webviewer_title_textview);
        noInterneTextView = (TextView) findViewById(R.id.webviewer_nointernet_textview);
        dataIntent = getIntent();
        if (dataIntent.hasExtra(VALRTApplication.WEBLINK))
            isFAQ = dataIntent.getBooleanExtra(VALRTApplication.WEBLINK, false);
        if (isFAQ) {
            if (Utils.getScreenSize(this) <= 3) {
                titleTextView.setText(getString(R.string.faq_heading));
            } else {
                titleTextView.setText(getString(R.string.faq));
            }
            url = getString(R.string.faq_url);
        } else {
            titleTextView.setText(getString(R.string.quick_start_guide));
            // Changed the Google service URL from Google Docs to google drive to open pdf
            url = "https://drive.google.com/viewerng/viewer?embedded=true&url=" + getString(R.string.quick_start_link);
        }

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Navigate back to the previous screen
                finish();
            }
        });

    }
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
    /**
     * Load data.
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void loadData() {
        if (Utils.isNetConnected(this)) {
            noInterneTextView.setVisibility(View.INVISIBLE);
            webview.setWebViewClient(new WebViewerWebViewClient());
            webview.getSettings().setJavaScriptEnabled(true);
            // Open the PDF document in Google Docs inside web view.
            webview.loadUrl(url);
            webview.setVisibility(View.VISIBLE);
        } else {
            noInterneTextView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
    /**
     * The Class WebViewerWebViewClient.
     */
    // To listen the loading process of the content in the web view.
    private class WebViewerWebViewClient extends WebViewClient {

        /* (non-Javadoc)
         * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }
        /* (non-Javadoc)
         * @see android.webkit.WebViewClient#onPageFinished(android.webkit.WebView, java.lang.String)
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.INVISIBLE);
            super.onPageFinished(view, url);
        }
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
