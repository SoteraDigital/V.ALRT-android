package com.vsnmobil.valrt.activities;

import android.annotation.SuppressLint;
//import
import android.app.Activity;
//import
import android.app.Dialog;
//import
import android.content.Intent;
//import
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
//import
import android.os.Bundle;
//import
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.view.View;
//import
import android.view.View.OnClickListener;
//import
import android.view.Window;
//import
import android.webkit.WebView;
//import
import android.webkit.WebViewClient;
//import
import android.widget.Button;
//import
import android.widget.CheckBox;
//import
import android.widget.ProgressBar;
//import
import android.widget.TextView;
//import
import com.vsnmobil.valrt.BuildConfig;
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
//import
import com.vsnmobil.valrt.utils.LogUtils;
import com.vsnmobil.valrt.utils.Utils;
//import
import java.io.InputStream;

/**
 * WelcomeActivity.java This class load the terms and conditions content in a
 * web view and request the user to accept the terms and condition proceed
 * further.
 */
public class WelcomeActivity extends Activity {
    /** The accept check box. */
    private CheckBox acceptCheckBox;
    /** The next button. */
    private Button nextButton;
    /** The webview. */
    private WebView webview;
    /** The no internet text view. */
    private TextView noInternetTextView;
    /** The progress bar. */
    private ProgressBar progressBar;
    /** The is term accepted. */
    private boolean isTermAccepted = false;
    /** The no internet connection dialog. */
    private Dialog noInternetConnectionDialog;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        acceptCheckBox = (CheckBox) findViewById(R.id.welcome_accept_checkbox);
        noInternetTextView = (TextView) findViewById(R.id.welcome_nointernet_textview);
        nextButton = (Button) findViewById(R.id.welcome_next_button);
        webview = (WebView) findViewById(R.id.welcome_webview_content);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTermAccepted) {
                    // if terms and condition is accepted it will navigate to
                    // agreement screen.
                    VALRTApplication.setPrefBoolean(WelcomeActivity.this, VALRTApplication.TERMSNCONDITION, true);
                    Intent i = new Intent(WelcomeActivity.this, AgreementActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
        // Check box user have to tap to accept the t&c.
        acceptCheckBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acceptCheckBox.isChecked()) {
                    isTermAccepted = true;
                    nextButton.setTextColor(getResources().getColor(R.color.violet_color));
                    nextButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_next_enable_arrow, 0);
                } else {
                    isTermAccepted = false;
                    nextButton.setTextColor(getResources().getColor(R.color.gray_color));
                    nextButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_next_disable_arrow, 0);
                }
            }
        });
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        acceptCheckBox.setChecked(false);
        nextButton.setTextColor(getResources().getColor(R.color.gray_color));
        nextButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_next_disable_arrow, 0);
        isTermAccepted = false;
        loadData();
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (noInternetConnectionDialog != null && noInternetConnectionDialog.isShowing()) {
            noInternetConnectionDialog.dismiss();
        }
    }
    /**
     * Load data.
     */
    // Load the content in the web view.
    @SuppressLint("SetJavaScriptEnabled")
    public void loadData() {
        isTermAccepted = false;
        // If HelpNowAlert build variant then load t&c from local copy
        if (BuildConfig.FLAVOR.equals("HelpNowAlert")) {
            acceptCheckBox.setEnabled(true);
            noInternetTextView.setVisibility(View.INVISIBLE);
            webview.setWebViewClient(new WelcomewebViewClient());
            webview.getSettings().setJavaScriptEnabled(true);
            webview.loadUrl(getString(R.string.agreement_url));
        } else {
            if (Utils.isNetConnected(this)) {
                // if Internet connection is there it will load the content in web
                // view.
                acceptCheckBox.setEnabled(true);
                noInternetTextView.setVisibility(View.INVISIBLE);
                webview.setWebViewClient(new WelcomewebViewClient());
                webview.getSettings().setJavaScriptEnabled(true);
                webview.loadUrl(getString(R.string.agreement_url));
            } else {
                // A pop will notify the user that no Internet connect is there.
                alertDialogShow(getResources()
                        .getString(R.string.not_internet_connection));
                noInternetTextView.setVisibility(View.VISIBLE);
                acceptCheckBox.setEnabled(false);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }
    /**
     * Share to email.
     *
     * @param view the view
     */
    public void shareToEmail(View view) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("plain/text");
        //sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " : " + getString(R.string.terms_conditions));
        // If HelpNowAlert build variant then load t&c from local copy
        if (BuildConfig.FLAVOR.equals("HelpNowAlert")) {
            try {
                InputStream tc = getAssets().open("valert_terms_conditions_english.html");
                byte[] buffer = new byte[tc.available()];
                tc.read(buffer);
                tc.close();
                sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(new String(buffer)).toString() );
            } catch (Exception e) {
                LogUtils.LOGI("shareToEmail", e.getMessage());
            }
        } else {
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.agreement_url));
        }
        startActivity(sendIntent);
    }
    /**
     * To show the alert message that no Internet connection in dialog.
     *
     * @param message which want to show the user.
     */
    public void alertDialogShow(String message) {
        noInternetConnectionDialog = new Dialog(this, R.style.ThemeWithCorners);
        noInternetConnectionDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        noInternetConnectionDialog.setContentView(R.layout.dialog_info);
        noInternetConnectionDialog.setCancelable(true);

        TextView contentTextView = (TextView) noInternetConnectionDialog.findViewById(R.id.info_title_textview);
        Button btn_silentmode = (Button) noInternetConnectionDialog.findViewById(R.id.info_ok_button);
        contentTextView.setText(message);
        noInternetConnectionDialog.show();
        btn_silentmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noInternetConnectionDialog.dismiss();
            }
        });
    }
    /**
     * The Class WelcomewebViewClient.
     */
    // To listen the loading process of content in the web view.
    private class WelcomewebViewClient extends WebViewClient {
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
            webview.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            super.onPageFinished(view, url);
        }
    }
}