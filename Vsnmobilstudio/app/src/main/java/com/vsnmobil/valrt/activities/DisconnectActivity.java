package com.vsnmobil.valrt.activities;

import android.app.ActionBar.LayoutParams;
//import
import android.app.Activity;
//import
import android.content.BroadcastReceiver;
//import
import android.content.Context;
//import
import android.content.Intent;
//import
import android.content.IntentFilter;
//import
import android.os.Bundle;
//import
import android.os.Vibrator;
//import
import android.view.View;
//import
import android.view.WindowManager;
//import
import android.widget.Button;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
//import
import com.vsnmobil.valrt.storage.DatabaseHelper;
//import
import com.vsnmobil.valrt.utils.LogUtils;
import com.vsnmobil.valrt.utils.PlaySound;
//import
import com.vsnmobil.valrt.utils.Utils;
/**
 * DisconnectActivity.java
 * <p/>
 * DisconnectDialog get displayed when BLE device out of range.
 */
public class DisconnectActivity extends Activity {
    /** The activity. */
    private static Activity activity;
    /** The vibrator. */
    private static Vibrator vibrator;
    /** The db helper. */
    private static DatabaseHelper dbHelper;
    /** The cancel button. */
    private Button cancelButton;
    /** The play sound. */
    private PlaySound playSound;
    /** The dot. */
    static int dot = 200; // Length of a Morse Code "dot" in milliseconds
    /** The dash. */
    static int dash = 500; // Length of a Morse Code "dash" in milliseconds
    /** The short_gap. */
    static int short_gap = 200; // Length of Gap Between dots/dashes
    /** The medium_gap. */
    static int medium_gap = 500; // Length of Gap Between Letters
    /** The long_gap. */
    static int long_gap = 1000; // Length of Gap Between Words
    /** The pattern. */
    static long[] pattern = {0, dot, short_gap, dot, short_gap, dot, medium_gap, dash, short_gap, dash, short_gap, dash,
            medium_gap, dot, short_gap, dot, short_gap, dot, long_gap};
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disconnect);
        // Keeps Screen On while Tracker in Progress
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        // Keeps Screen On while Tracker in Progress
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setFinishOnTouchOutside(false);
        activity = DisconnectActivity.this;
        // Database helper to fetch data from local database
        dbHelper = DatabaseHelper.getInstance(this);
        cancelButton = (Button) findViewById(R.id.disconnect_cancel_all_button);
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(vibrateReceiver, filter);
        if (VALRTApplication.getPrefBoolean(DisconnectActivity.this, VALRTApplication.DEVICE_TRACKER_VIBRATION_STATUS)) {
            vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            // The "0" means to repeat the pattern starting at the beginning
            vibrator.vibrate(pattern, 0);
        }
        if (VALRTApplication.getPrefBoolean(DisconnectActivity.this, VALRTApplication.DEVICE_TRACKER_ALERT_TONE_STATUS)) {
            if (VALRTApplication.getPrefBoolean(this, VALRTApplication.PHONESILENTCBX) == false && Utils.isSilentMode(this) == false) {
                playSound = new PlaySound(this, true);
                playSound.start();
            }
        }
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // logging in history table
                String historyLog = getResources().getString(R.string.tracker_Canceled);
                dbHelper.insertDeviceHistory(historyLog);
                if (vibrator != null)
                    vibrator.cancel();

                if (playSound != null)
                    playSound.stop();
                VALRTApplication.isDeviceTrackInProgress = false;
                finish();
            }
        });

    }
    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        // do nothing
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (vibrator != null)
                vibrator.cancel();
            if (playSound != null) {
                playSound.stop();
                playSound = null;
            }
            VALRTApplication.isDeviceTrackInProgress = false;
            unregisterReceiver(vibrateReceiver);
        } catch (Exception e) {
            LogUtils.i("TAG", e); //logException
            finish();
        }
    }
    /**
     * Cancelthis activity.
     */
    public static void cancelthisActivity() {
        if (vibrator != null)
            vibrator.cancel();
        VALRTApplication.isDeviceTrackInProgress = false;
        activity.finish();
    }
    /** The vibrate receiver. */
    // screen off vibration still on
    public static BroadcastReceiver vibrateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // Vibration on
                if (VALRTApplication.getPrefBoolean(activity, VALRTApplication.DEVICE_TRACKER_VIBRATION_STATUS)) {
                    vibrator.cancel();
                    vibrator.vibrate(pattern, 0);
                }
            }
        }
    };
}
