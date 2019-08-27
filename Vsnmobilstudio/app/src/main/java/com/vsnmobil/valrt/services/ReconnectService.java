package com.vsnmobil.valrt.services;

import java.util.ArrayList;
//import
import java.util.List;
//import
import java.util.Timer;
//import
import java.util.TimerTask;
//import
import android.app.Service;
//import
import android.bluetooth.BluetoothAdapter;
//import
import android.bluetooth.BluetoothDevice;
//import
import android.bluetooth.BluetoothManager;
//import
import android.content.ComponentName;
//import
import android.content.Intent;
//import
import android.content.ServiceConnection;
//import
import android.os.Handler;
//import
import android.os.IBinder;
//import
import android.os.Looper;
//import
import com.vsnmobil.valrt.VALRTApplication;
//import
import com.vsnmobil.valrt.storage.DatabaseHelper;
//import
import com.vsnmobil.valrt.utils.LogUtils;
//import
import com.vsnmobil.valrt.utils.Utils;
/**
 * The Class ReconnectService.
 */
public class ReconnectService extends Service implements BluetoothAdapter.LeScanCallback {
    /** The tag. */
    private String TAG = LogUtils.makeLogTag(ReconnectService.class);
    /** The already requested. */
    private List<String> alreadyRequested = new ArrayList<String>();
    /** The bluetooth le service. */
    private BluetoothLeService bluetoothLeService;
    /** The bluetooth adapter. */
    private BluetoothAdapter bluetoothAdapter;
    /** The device address list. */
    private List<String> deviceAddressList;
    /** The db helper. */
    private DatabaseHelper dbHelper;
    /** The timer delay connect. */
    private Timer timerDelayConnect = null;
    /** The timer stop service. */
    private Timer timerStopService = null;
    /** The timer interval scanner. */
    private Timer timerIntervalScanner = null;
    /** The is scanning. */
    private boolean isScanning = false;
    /** The timeout. */
    private long TIMEOUT = 180000;
    /** The scan timeout. */
    private long SCAN_TIMEOUT = 30000;
    /** The delay. */
    private long DELAY = 5000;
    /** The device address. */
    private String deviceAddress = null;
    /* (non-Javadoc)
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper =  DatabaseHelper.getInstance(this);
        BluetoothManager manager = VALRTApplication.getManager(this);
        if (manager != null) {
            bluetoothAdapter = manager.getAdapter();
            if (bluetoothAdapter != null && Utils.isBluetoothEnabled(this)==true) {
                scanDevice();
            } else {
                stopSelf();
            }
        } else {
            stopSelf();
        }
        // to stop the Reconnect Service after two minutes.
        stopService();
        Intent i = new Intent(this,BluetoothLeService.class);
        startService(i);
        bindService(i, mServiceConnection, BIND_AUTO_CREATE);
    }
    /** The m service connection. */
    // To manage Service class life cycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName,IBinder service) {
            bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!bluetoothLeService.initialize()) {
                stopService();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // Don't kill the BluetoothLeService instance
            // while disconnecting the service.
        }
    };
    /* (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    /* (non-Javadoc)
     * @see android.bluetooth.BluetoothAdapter.LeScanCallback#onLeScan(android.bluetooth.BluetoothDevice, int, byte[])
     */
    @Override
    public void onLeScan(final BluetoothDevice newDeivce, final int newRssi,final byte[] newScanRecord) {
        deviceAddressList = dbHelper.getAllDeviceAddress();
        if(deviceAddressList.size()==0){
            stopSelf();
        }
        deviceAddress = newDeivce.getAddress();
        final Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            public void run() {
                if (deviceAddressList.contains(deviceAddress)) {
                    if (bluetoothLeService != null) {
                        connectDevice(deviceAddress);
                    }
                }
            }
        });
    }
    /* (non-Javadoc)
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        try{
            // Stop scanning
            stopScan();
            // UNBIND the BluetoothLeService connection
            unbindService(mServiceConnection);

            if (timerStopService != null) {
                timerStopService.cancel();
            }

            if (timerDelayConnect != null) {
                timerDelayConnect.cancel();
            }

            if (timerIntervalScanner != null) {
                timerIntervalScanner.cancel();
            }
        }catch(Exception e){
            LogUtils.LOGI(TAG,e.getMessage()) ;
        }
        super.onDestroy();
    }
    /**
     * Start scan.
     */
    // Start scan
    private void startScan() {
        try{
            if ((bluetoothAdapter != null) && (!isScanning)) {
                bluetoothAdapter.startLeScan(this);
                isScanning = true;
            }
        }catch(Exception e)
        {
            LogUtils.LOGI(TAG,e.getMessage()) ;
        }
    }
    /**
     * Stop scan.
     */
    // Stop scan
    private void stopScan() {
        try{
            if (bluetoothAdapter != null) {
                bluetoothAdapter.stopLeScan(this);
            }
            isScanning = false;
        }catch(Exception e){
            LogUtils.LOGI(TAG,e.getMessage()) ;
        }
    }
    /**
     * Stop service.
     */
    public void stopService() {
        try{
            if(timerStopService==null){
                timerStopService = new Timer();
                timerStopService.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ReconnectService.this.stopSelf();
                    }
                }, TIMEOUT);
            }
        }catch(Exception e){
            LogUtils.LOGI(TAG,e.getMessage()) ;
        }
    }
    /**
     * Scan device.
     */
    // Runs the LeScan for 30seconds balanced mode and Scan Available Devices
    public void scanDevice() {
        try{
            if(timerIntervalScanner==null){
                timerIntervalScanner = new Timer();
                timerIntervalScanner.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (!isScanning) {
                            startScan();
                            if(alreadyRequested!=null)
                                alreadyRequested.clear();
                        } else {
                            stopScan();
                        }
                    }
                }, 0, SCAN_TIMEOUT);
            }
        }catch(Exception e){
            LogUtils.LOGI(TAG,e.getMessage()) ;
        }
    }
    /**
     * Connect device.
     *
     * @param deviceMac the device mac
     */
    public void connectDevice(final String deviceMac) {
        try{
            if(timerDelayConnect==null){
                timerDelayConnect = new Timer();
                timerDelayConnect.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        final Handler h = new Handler(Looper.getMainLooper());
                        h.post(new Runnable() {
                            public void run() {
                                if (!alreadyRequested.contains(deviceMac)) {
                                    bluetoothLeService.connect(deviceMac);
                                    alreadyRequested.add(deviceMac);
                                }
                            }
                        });
                    }
                }, DELAY);
            }
        }catch(Exception e){
            LogUtils.LOGI(TAG,e.getMessage()) ;
        }
    }
}
