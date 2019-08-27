package com.vsnmobil.valrt.activities;

import android.app.Activity;
//import
import android.app.Dialog;
//import
import android.content.Intent;
//import
import android.os.Bundle;
//import
import android.view.View;
//import
import android.view.Window;
//import
import android.widget.Button;
//import
import android.widget.RelativeLayout;
//import
import android.widget.TextView;
//import
import com.vsnmobil.valrt.BuildConfig;
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
//import
import com.vsnmobil.valrt.utils.Utils;
/**
 * HelpActivity.java This Activity having the RelativeLayouts to navigate to the
 * following screens HistoryLog,Quick start guide, Product information, Terms
 * and condition and Instructional video.
 */
public class HelpActivity extends Activity implements View.OnClickListener {
    /** The history log relative layout. */
    private RelativeLayout historyLogRelativeLayout;
    /** The quick start guide relative layout. */
    private RelativeLayout quickStartGuideRelativeLayout;
    /** The product information relative layout. */
    private RelativeLayout productInformationRelativeLayout;
    /** The aboutvsn relative layout. */
    private RelativeLayout aboutvsnRelativeLayout;
    /** The terms and conditions relative layout. */
    private RelativeLayout termsAndConditionsRelativeLayout;
    /** The instructional video relative layout. */
    private RelativeLayout instructionalVideoRelativeLayout;
    /** The back button. */
    private Button backButton;
    /** The intent. */
    private Intent intent;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        historyLogRelativeLayout = (RelativeLayout) findViewById(R.id.help_history_layout);
        historyLogRelativeLayout.setOnClickListener(this);
        quickStartGuideRelativeLayout = (RelativeLayout) findViewById(R.id.help_quic_start_guide_layout);
        //quickStartGuideRelativeLayout.setOnClickListener(this);
        productInformationRelativeLayout = (RelativeLayout) findViewById(R.id.help_product_info_layout);
        productInformationRelativeLayout.setOnClickListener(this);
        aboutvsnRelativeLayout = (RelativeLayout) findViewById(R.id.help_faq_layout);
        aboutvsnRelativeLayout.setOnClickListener(this);
        termsAndConditionsRelativeLayout = (RelativeLayout) findViewById(R.id.help_terms_condition_layout);
        termsAndConditionsRelativeLayout.setOnClickListener(this);
        instructionalVideoRelativeLayout = (RelativeLayout) findViewById(R.id.help_instruction_video_layout);
        // If V.ALRT build variant then enable video layout
        if (BuildConfig.FLAVOR.equals("VALRT")) {
            quickStartGuideRelativeLayout.setOnClickListener(this);
            instructionalVideoRelativeLayout.setOnClickListener(this);
        } else {
            quickStartGuideRelativeLayout.setVisibility(View.GONE);
            instructionalVideoRelativeLayout.setVisibility(View.GONE);
        }
        backButton = (Button) findViewById(R.id.help_back_button);
        backButton.setOnClickListener(this);
    }
    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.help_history_layout:
                // Navigate to History log screen.
                intent = new Intent(HelpActivity.this, HistoryLogActivity.class);
                startActivity(intent);
                break;
            case R.id.help_quic_start_guide_layout:
                // Navigate to quick start guide screen
                intent = new Intent(HelpActivity.this, WebViewerActivity.class);
                intent.putExtra(VALRTApplication.WEBLINK, false);
                startActivity(intent);
                break;
            case R.id.help_product_info_layout:
                // Navigate to product information screen
                intent = new Intent(HelpActivity.this, ProductInformationActivity.class);
                startActivity(intent);
                break;
            case R.id.help_faq_layout:
                // Navigate to about VSN screen.
                intent = new Intent(HelpActivity.this, WebViewerActivity.class);
                intent.putExtra(VALRTApplication.WEBLINK, true);
                startActivity(intent);
                break;
            case R.id.help_terms_condition_layout:
                // Navigate to terms and condition screen
                intent = new Intent(HelpActivity.this, TermsAndConditionsActivity.class);
                startActivity(intent);
                break;
            case R.id.help_instruction_video_layout:

                if (Utils.isNetConnected(this)) {
                    // Navigate to instructional video screen
                    intent = new Intent(HelpActivity.this, VideoPlayerActivity.class);
                    startActivity(intent);
                } else {
                    // A pop will notify the user that no Internet connect is there.
                    alertDialogShow(getResources().getString(R.string.not_internet_connection));
                }
                break;
            case R.id.help_back_button:
                // Navigate back to the previous screen.
                finish();
                break;
        }
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    /**
     * To show the alert message that no Internet connection in dialog.
     *
     * @param message which want to show the user.
     */
    public void alertDialogShow(String message) {
        final Dialog noInternetConnectionDialog = new Dialog(this, R.style.ThemeWithCorners);
        noInternetConnectionDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        noInternetConnectionDialog.setContentView(R.layout.dialog_info);
        noInternetConnectionDialog.setCancelable(false);

        TextView contentTextView = (TextView) noInternetConnectionDialog
                .findViewById(R.id.info_title_textview);
        Button btn_silentmode = (Button) noInternetConnectionDialog
                .findViewById(R.id.info_ok_button);
        contentTextView.setText(message);

        noInternetConnectionDialog.show();
        btn_silentmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noInternetConnectionDialog.dismiss();
            }
        });
    }
}
