package com.vsnmobil.valrt.activities;

import android.app.Activity;
//import
import android.bluetooth.BluetoothAdapter;
//import
import android.content.Intent;
//import
import android.os.Bundle;
//import
import android.widget.ProgressBar;
//import
import android.widget.ImageView;
//import
import com.vsnmobil.valrt.BuildConfig;
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
//import
import com.vsnmobil.valrt.utils.Utils;
/**
 * SplashActivity.java 
 *
 * This class is to show the splash image at the first time of launching the APP.
 * It has a loading horizontal progress it will show for 5 seconds.
 */
public class SplashActivity extends Activity {
    /** The splash time out. */
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 400;
    /** The Constant TIMER_RUNTIME. */
    protected static final int TIMER_RUNTIME = 10000;
    /** The is active. */
    boolean isActive = false;
    /** The progress bar. */
    protected ProgressBar progressBar;
    /** The logo. */
    protected ImageView splashLogo;
    /** The bluetooth adapter. */
    BluetoothAdapter bluetoothAdapter;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = (ProgressBar) findViewById(R.id.splash_progressBar);
        splashLogo = (ImageView) findViewById(R.id.splash_logo);
        // If HelpNowAlert build variant then don't display VSN logo at bottom of screen
        if (BuildConfig.FLAVOR.equals("HelpNowAlert")) {
            splashLogo.setVisibility(ImageView.GONE);
        }
        // Check BLE Device support is there or not.
        if (!Utils.isBLESupported(this)) {
            Utils.showToast(this, getString(R.string.ble_not_supported));
            finish();
            return;
        }
        // Accepted terms and conditions true
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.TERMSNCONDITION) == true) {
            // Accepted the agreement
            if (VALRTApplication.getPrefBoolean(this, VALRTApplication.AGREEMENT) == true) {
                // Done congratulation
                if (VALRTApplication.getPrefBoolean(this, VALRTApplication.CONGRATULATION) == false) {
                    startActivity(new Intent(SplashActivity.this, PersonalInfoActivity.class));
                    finish();
                } else {
                    //Navigate to home screen.
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                }
            } else {
                //Navigate to agreement screen.
                startActivity(new Intent(SplashActivity.this, AgreementActivity.class));
                finish();
            }
        }
        //Thread to show the progress in horizontal view.
        final Thread timerThread = new Thread() {
            @Override
            public void run() {
                isActive = true;
                try {
                    int waited = 0;
                    while (isActive && (waited < TIMER_RUNTIME)) {
                        sleep(200);
                        if (isActive) {
                            waited += SPLASH_TIME_OUT;
                            updateProgress(waited);
                        }
                    }
                } catch (InterruptedException e) {
                } finally {
                    //navigate to welcome screen.
                    if (VALRTApplication.getPrefBoolean(SplashActivity.this, VALRTApplication.TERMSNCONDITION) == false) {
                        startActivity(new Intent(SplashActivity.this, InitialActivity.class));
                        finish();
                    }
                }
            }
        };
        timerThread.start();
    }
    /**
     * To update the value to the horizontal progress bar.
     *
     * @param timePassed the time passed
     */
    public void updateProgress(final int timePassed) {
        if (null != progressBar) {
            // Ignore rounding error here
            final int progress = progressBar.getMax() * timePassed / TIMER_RUNTIME;
            progressBar.setProgress(progress);
        }
    }
}
