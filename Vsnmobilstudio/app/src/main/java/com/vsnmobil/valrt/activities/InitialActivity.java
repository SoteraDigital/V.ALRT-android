package com.vsnmobil.valrt.activities;

import android.app.Activity;
//import
import android.content.Intent;
//import
import android.graphics.drawable.Drawable;
import android.os.Bundle;
//import
import android.view.View;
//import
import android.view.View.OnClickListener;
//import
import android.webkit.WebView;
//import
import android.widget.Button;
//import
import android.widget.ImageView;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
//import
import com.vsnmobil.valrt.fragment.TourSlideFragmentActivity;
//import
import com.vsnmobil.valrt.utils.Utils;
/**
 * InitialActivity.java
 * <p/>
 * This class is used to show the animation of the puck and also having the button to navigation to
 * to the tour slide and setup the app.
 */
public class InitialActivity extends Activity implements OnClickListener {
    /** The device web view. */
    private WebView deviceWebView;
    /** The device image view. */
    private ImageView deviceImageView;
    /** The take tour button. */
    private Button takeTourButton;
    /** The setup device button. */
    private Button setupDeviceButton;
    /** The intent. */
    private Intent intent;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        // Accepted terms and conditions true
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.TERMSNCONDITION) == true) {
            // Accepted the agreement
            if (VALRTApplication.getPrefBoolean(this, VALRTApplication.AGREEMENT) == true) {
                // Done congratulation
                if (VALRTApplication.getPrefBoolean(this, VALRTApplication.CONGRATULATION) == false) {
                    startActivity(new Intent(InitialActivity.this, PersonalInfoActivity.class));
                    finish();
                }
            } else {
                //Navigate to agreement screen.
                startActivity(new Intent(InitialActivity.this, AgreementActivity.class));
                finish();
            }
        }
        deviceWebView = (WebView) findViewById(R.id.initial_device_webview);
        deviceImageView = (ImageView) findViewById(R.id.initial_device_imageview);
        takeTourButton = (Button) findViewById(R.id.initial_take_tour_button);
        takeTourButton.setOnClickListener(this);
        setupDeviceButton = (Button) findViewById(R.id.initial_setup_mydevice_button);
        setupDeviceButton.setOnClickListener(this);
        //To check the VSN
        if (Utils.getScreenSize(this) <= 3) {
            deviceWebView.setVisibility(View.GONE);
            deviceImageView.setVisibility(View.VISIBLE);
        } else {
            deviceWebView.loadUrl("file:///android_asset/animated_puck.html");  // changed from loading .gif to .html file because of Galaxy S6 transparency issue
            deviceWebView.setBackgroundColor(0x00000000);
            deviceWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        }
    }
    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //Take a tour
            case R.id.initial_take_tour_button:
                intent = new Intent(InitialActivity.this, TourSlideFragmentActivity.class);
                startActivity(intent);
                break;
            //set up the Application.
            case R.id.initial_setup_mydevice_button:
                intent = new Intent(InitialActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
