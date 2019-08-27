package com.vsnmobil.valrt.activities;

import android.app.Activity;
//import
import android.content.Intent;
//import
import android.net.Uri;
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
import com.vsnmobil.valrt.BuildConfig;
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.utils.Utils;
/**
 * TermsAndConditionsActivity.java
 * <p/>
 * This class load the terms and conditions content in a web view .
 */
public class TermsAndConditionsActivity extends Activity {
    /** The back button. */
    private Button backButton;
    /** The webview. */
    private WebView webview;
    /** The no internet text view. */
    private TextView noInternetTextView;
    /** The title text view. */
    private TextView titleTextView;
    /** The progress bar. */
    private ProgressBar progressBar;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termscondition);

        noInternetTextView = (TextView) findViewById(R.id.termsandcondition_nointernet_textview);
        titleTextView = (TextView) findViewById(R.id.termsandcondition_title_textview);
        backButton = (Button) findViewById(R.id.termsandcondition_back_button);
        webview = (WebView) findViewById(R.id.termsandcondition_webview_content);
        progressBar = (ProgressBar) findViewById(R.id.termsandcondition_progressbar);
        //Heading to support the v.35 mobile
        if (Utils.getScreenSize(this) <= 3) {
            titleTextView.setText(getString(R.string.terms_conditions_heading));
        }
        //To navigate back to the previous screen.
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
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
    //Load the content in the web view.
    public void loadData() {
        if (Utils.isNetConnected(this)) {
            // if Internet connection is there it will load the content in web view.
            noInternetTextView.setVisibility(View.INVISIBLE);
            webview.setWebViewClient(new TermsAndConditionswebViewClient());
            webview.loadUrl(getString(R.string.agreement_url));
        } else {
            noInternetTextView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
    /**
     * The Class TermsAndConditionswebViewClient.
     */
    // To listen the loading process of content in the web view.
    private class TermsAndConditionswebViewClient extends WebViewClient {

        /* (non-Javadoc)
         * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("play.google.com") || url.contains("www.apple.com")) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            } else {
                view.loadUrl(url);
                return false;
            }
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
