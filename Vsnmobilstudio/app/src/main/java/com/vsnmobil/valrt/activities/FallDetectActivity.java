package com.vsnmobil.valrt.activities;

import java.util.Timer;
//import
import java.util.TimerTask;
//import
import android.app.Activity;
//import
import android.bluetooth.BluetoothGatt;
//import
import android.content.ComponentName;
//import
import android.content.Context;
//import
import android.content.Intent;
//import
import android.content.ServiceConnection;
//import
import android.os.Bundle;
//import
import android.os.CountDownTimer;
//import
import android.os.IBinder;
//import
import android.util.Log;
//import
import android.view.View;
//import
import android.view.View.OnClickListener;
//import
import android.view.Window;
//import
import android.view.WindowManager;
//import
import android.widget.Button;
//import
import android.widget.TextView;
//import
import com.vsnmobil.valrt.GattConstant;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
//import
import com.vsnmobil.valrt.services.BluetoothLeService;
//import
import com.vsnmobil.valrt.utils.LogUtils;
//import
import com.vsnmobil.valrt.utils.NotificationUtils;
//import
import com.vsnmobil.valrt.utils.PlaySound;
//import
import com.vsnmobil.valrt.utils.Utils;
/**
 * FallDetectActivity.java
 * <p/>
 * This class will be pop up when ever the free fall occurred in the puck.It will
 * show a count down timer for one minute.If user cancel then this it will write
 * a value to puck to cancel the alert. If user didn't press the cancel  in  one minute
 * then it will start the AlertProgressActivity class.
 */
public class FallDetectActivity extends Activity implements OnClickListener {
    /** The tag. */
    private String TAG = LogUtils.makeLogTag(FallDetectActivity.class);
    /** The alert progress gatt. */
    public static BluetoothGatt alertProgressGatt;
    /** The bluetooth le service. */
    private BluetoothLeService bluetoothLeService;
    /** The count timer. */
    private CountDownTimer countTimer;
    /** The play sound. */
    private PlaySound playSound;
    /** The fall detect message. */
    private TextView fallDetectMessage;
    /** The cancel button. */
    private Button cancelButton;
    /** The totalcountervalue. */
    private int TOTALCOUNTERVALUE = 60000;
    /** The singleunit. */
    private int SINGLEUNIT = 1000;
    /** The music status. */
    boolean musicStatus = false;
    /** The is time complete. */
    boolean isTimeComplete = false;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_falldetect);
        // Keeps Screen On while Alert in Progress
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setFinishOnTouchOutside(false);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);
        fallDetectMessage = (TextView) findViewById(R.id.falldetect_content_textview);
        Intent i = new Intent(this, BluetoothLeService.class);
        startService(i);
        bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
        startCountDownTimer();
        Utils.cancelNotify(this, VALRTApplication.ALERTINPROGRESS_NOTIFY_ID);
        NotificationUtils.postNotification(this, getString(R.string.fall_detected_notification), VALRTApplication.FALL_DETECT_NOTIFY_ID);
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.PHONESILENTCBX) == false && Utils.isSilentMode(this) == false) {
            playSound = new PlaySound(this, false);
            playSound.start();
        }
    }
    /**
     * Start count down timer.
     */
    public void startCountDownTimer() {
        countTimer = new CountDownTimer(TOTALCOUNTERVALUE, SINGLEUNIT) {
            public void onTick(final long millisUntilFinished) {
                FallDetectActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        fallDetectMessage.setText(millisUntilFinished / SINGLEUNIT + getString(R.string.seconds));
                    }
                });
            }
            public void onFinish() {
                isTimeComplete = true;
                if (playSound != null) {
                    playSound.stop();
                    playSound = null;
                }
                // need to start alert progress
                Intent i = new Intent(FallDetectActivity.this, AlertProgressActivity.class);
                i.putExtra(VALRTApplication.VALRT_STATUS, "FALL");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        };
        countTimer.start();
    }
    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_button:
                countTimer.cancel();
                if (bluetoothLeService != null) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            bluetoothLeService.ackDevice(alertProgressGatt, GattConstant.CANCEL_ACK);
                        }
                    }, 2000);
                }
                finish();
                break;
        }
    }
    /** The m service connection. */
    // To manage Service class life cycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!bluetoothLeService.initialize()) {
                finish();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // Don't kill the BluetoothLeService instance
            // while disconnecting the service.
        }
    };
    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        VALRTApplication.isFallDetectInProgress = false;
        // Clears the flag for Keep Screen On
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (isTimeComplete == false) {
            bluetoothLeService.ackDevice(alertProgressGatt, GattConstant.CANCEL_ACK);
        }
        unbindService(mServiceConnection);
        Utils.cancelNotify(this, VALRTApplication.FALL_DETECT_NOTIFY_ID);

        if (countTimer != null)
            countTimer.cancel();

        if (playSound != null) {
            playSound.stop();
            playSound = null;
        }

    }
    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        //	super.onBackPressed();
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
    }

}
