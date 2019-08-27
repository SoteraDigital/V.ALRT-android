package com.vsnmobil.valrt;

import java.util.Timer;
//import
import java.util.TimerTask;
//import
import android.bluetooth.BluetoothAdapter;
//import
import android.content.BroadcastReceiver;
//import
import android.content.Context;
//import
import android.content.Intent;
//import
import android.location.LocationManager;
//import
import android.os.Handler;
//import
import android.os.Looper;
import android.support.v4.content.ContextCompat;
//import
import com.vsnmobil.valrt.services.BluetoothLeService;
//import
import com.vsnmobil.valrt.services.ReconnectService;
//import
import com.vsnmobil.valrt.storage.DatabaseHelper;
//import
import com.vsnmobil.valrt.utils.LogUtils;
//import
import com.vsnmobil.valrt.utils.NotificationUtils;
//import
import com.vsnmobil.valrt.utils.Utils;
/**
 * VALRTReceiver.java
 *
 * This class will be receive values whenever the following event occur. Events like Bluetooth
 * status on / off,Location provider on / off and mobile device power up. Based on the received
 * value we will trigger the corresponding functions.
 *
 */
public class VALRTReceiver extends BroadcastReceiver {
    /** The Constant TAG. */
    private final static String TAG = VALRTReceiver.class.getSimpleName();
    /** The boot completed action. */
    private final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";
    /** The quickboot poweron. */
    private final String QUICKBOOT_POWERON ="com.htc.intent.action.QUICKBOOT_POWERON";
    /** The htc boot completed. */
    private final String HTC_BOOT_COMPLETED ="com.htc.intent.action.BOOT_COMPLETED";
    /** The Constant ACTION_NETWORK_STATE_CHANGED. */
    public final static String ACTION_NETWORK_STATE_CHANGED = "android.net.conn.CONNECTIVITY_CHANGE";
    /** The Constant DELAY. */
    private static final long DELAY = 15000;
    /** The mcontext. */
    private Context mcontext;
    /** The db helper. */
    private DatabaseHelper dbHelper;
    /** The network timer. */
    private Timer networkTimer;
    /* (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    public void onReceive(Context context, Intent intent) {
        this.mcontext = context;
        dbHelper = new DatabaseHelper(mcontext);
        final String action = intent.getAction();
        if(!VALRTApplication.getPrefBoolean(context, VALRTApplication.VALRT_SWITCH_OFF)&& dbHelper.getPairedDeviceCount() != 0){
            // To Listen the Bluetooth switch off / switch on
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);
                switch (state) {
                    // If Bluetooth switch off stop all the services running in background.
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        mcontext.stopService(new Intent(mcontext,ReconnectService.class));
                        mcontext.stopService(new Intent(mcontext,BluetoothLeService.class));
                        dbHelper.updateConnectionStatus();
                        NotificationUtils.postNotification(mcontext,mcontext.getString(R.string.bluetooth_off),VALRTApplication.BLUETOOTH_NOTIFY_ID);
                        break;
                    //If Bluetooth switch on start the reconnect service to scan and connect the paired device.
                    case BluetoothAdapter.STATE_ON:
                        // Turning on the blue tooth.
                        if (dbHelper.getPairedDeviceCount() != 0)
                        {
                            if(!VALRTApplication.isScanActivityRunning)
                            {
                                ContextCompat.startForegroundService(mcontext,new Intent(mcontext, ReconnectService.class));
                            }
                        }
                        Utils.cancelNotify(mcontext,VALRTApplication.BLUETOOTH_NOTIFY_ID);
                        break;
                }
            } else if (action.equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                if (!Utils.isGPSon(mcontext)) {
                    // Generate notification in notification bar that the location is disabled.
                    NotificationUtils.postNotification(mcontext,mcontext.getString(R.string.location_disabled),VALRTApplication.LOCATION_NOTIFY_ID);
                } else if (Utils.isGPSon(mcontext)) {
                    // Remove the previously generated notification.
                    Utils.cancelNotify(mcontext,VALRTApplication.LOCATION_NOTIFY_ID);
                }
                // Mobile device received the on boot complete event.
            } else if (action.equals(BOOT_COMPLETED_ACTION)) {
                //If Bluetooth is switched off post a local notification.
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mBluetoothAdapter.isEnabled()) {
                    NotificationUtils.postNotification(mcontext,mcontext.getString(R.string.bluetooth_off),VALRTApplication.BLUETOOTH_NOTIFY_ID);
                }
                // If we update with new version this action will be called.
            } else if (action.equals(ACTION_NETWORK_STATE_CHANGED)) {
                networkChangeNotification();
            } else if (action.equals(QUICKBOOT_POWERON) || action.equals(HTC_BOOT_COMPLETED)){
                //If Bluetooth is switched off post a local notification.
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mBluetoothAdapter.isEnabled()) {
                    NotificationUtils.postNotification(mcontext,mcontext.getString(R.string.bluetooth_off),VALRTApplication.BLUETOOTH_NOTIFY_ID);
                }
            }
        }
    }
    /**
     * This timer function will wait for 15 second after it has been called
     * then it will check for the Internet connection is available or net.
     * If not it will post a local notification.
     */
    private void networkChangeNotification(){
        try{
            if(networkTimer==null){
                networkTimer = new Timer();
                networkTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        final Handler h = new Handler(Looper.getMainLooper());
                        h.post(new Runnable() {
                            public void run() {
                                if (!Utils.isNetConnected(mcontext)) {
                                    NotificationUtils.postNotification(mcontext,mcontext.getString(R.string.need_data_connection), VALRTApplication.DATA_CONNECTION_NOTIFY_ID);
                                }else{
                                    Utils.cancelNotify(mcontext,VALRTApplication.DATA_CONNECTION_NOTIFY_ID);
                                }
                            }
                        });
                    }
                }, DELAY);
            }
        }catch(Exception e){
            LogUtils.i(TAG, e);
        }
    }
}
