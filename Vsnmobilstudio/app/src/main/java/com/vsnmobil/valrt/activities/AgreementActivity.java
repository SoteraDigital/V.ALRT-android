package com.vsnmobil.valrt.activities;

import android.app.Activity;
//import
import android.app.Dialog;
//import
import android.content.Intent;
//import
import android.os.Bundle;
//import
import android.provider.Settings;
//import
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
 * AgreementActivity.java
 * <p/>
 * This class load the agreement content in a web view.
 */
public class AgreementActivity extends Activity {
    /** The no internet text view. */
    private TextView noInternetTextView;
    /** The next button. */
    private Button nextButton;
    /** The location setting dialog. */
    private Dialog locationSettingDialog;
    /** The web view. */
    private WebView webView;
    /** The progress bar. */
    private ProgressBar progressBar;
    /** The valrt application. */
    private VALRTApplication valrtApplication;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        valrtApplication = (VALRTApplication) getApplicationContext();
        valrtApplication.pingService();
        // set the panic tone.
        VALRTApplication.setPrefBoolean(AgreementActivity.this, VALRTApplication.PANICTONECBX, true);
        noInternetTextView = (TextView) findViewById(R.id.agreement_nointernet_textview);
        webView = (WebView) findViewById(R.id.agreement_webview);
        progressBar = (ProgressBar) findViewById(R.id.agreement_progressbar);
        //if GPS is disabled show a pop up message to user.
        if (!Utils.isGPSon(this)) {
            alertDialogShow(getString(R.string.locationservice_agreement));
        }
        nextButton = (Button) findViewById(R.id.agreement_back_button);
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //if next button is clicked set preference as true and navigate to settings activity.
                VALRTApplication.setPrefBoolean(AgreementActivity.this, VALRTApplication.AGREEMENT, true);
                Intent i = new Intent(AgreementActivity.this, PersonalInfoActivity.class);
                startActivity(i);
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
        // Load the data in the web view.
        loadData();
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        finish();
    }
    /**
     * Load data.
     */
    //To load the content of the agreement into web view.
    public void loadData() {
        noInternetTextView.setVisibility(View.INVISIBLE);
        webView.setWebViewClient(new AgreementwebViewClient());
        webView.loadUrl(getResources().getString(R.string.agreement_note_url));
        webView.setVisibility(View.VISIBLE);
    }
    /**
     * The Class AgreementwebViewClient.
     */
    // To listen the loading process of content in the web view.
    private class AgreementwebViewClient extends WebViewClient {
        /* (non-Javadoc)
         * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
        /* (non-Javadoc)
         * @see android.webkit.WebViewClient#onPageFinished(android.webkit.WebView, java.lang.String)
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            // after finished the loading of content invisible the progress bar.
            webView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            nextButton.setEnabled(true);
            super.onPageFinished(view, url);
        }
    }
    /**
     * To show the pop up message if GPS provider is disabled.It have two buttons.
     * settings button navigate to default location provider setting screen.
     * cancel button close the dialog.
     *
     * @param message the message
     */
    public void alertDialogShow(String message) {

        locationSettingDialog = new Dialog(this, R.style.ThemeWithCorners);
        locationSettingDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        locationSettingDialog.setContentView(R.layout.dialog_location_setting_cancel);
        locationSettingDialog.setCancelable(false);
        TextView silentMessageTextView = (TextView) locationSettingDialog.findViewById(R.id.txt_msg);
        Button settingButton = (Button) locationSettingDialog.findViewById(R.id.location_settings_button);
        Button cancelButton = (Button) locationSettingDialog.findViewById(R.id.location_cancel_button);
        silentMessageTextView.setText(message);
        locationSettingDialog.show();
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationSettingDialog.dismiss();
            }
        });
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to default mobile GPS settings screen.
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 1);
                locationSettingDialog.dismiss();
            }
        });
    }
}
