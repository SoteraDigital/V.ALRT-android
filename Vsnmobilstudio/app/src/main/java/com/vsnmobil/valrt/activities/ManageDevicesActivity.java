package com.vsnmobil.valrt.activities;

import android.Manifest;
import android.app.Activity;
//import
import android.app.Dialog;
//import
import android.bluetooth.BluetoothAdapter;
//import
import android.bluetooth.BluetoothDevice;
//import
import android.bluetooth.BluetoothGatt;
//import
import android.bluetooth.BluetoothManager;
//import
import android.content.ComponentName;
//import
import android.content.Intent;
//import
import android.content.ServiceConnection;
//import
import android.content.pm.PackageManager;
import android.os.Bundle;
//import
import android.os.Handler;
//import
import android.os.IBinder;
//import
import android.os.Looper;
//import
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
//import
import android.view.View.OnClickListener;
//import
import android.view.Window;
//import
import android.view.animation.Animation;
//import
import android.view.animation.LinearInterpolator;
//import
import android.view.animation.RotateAnimation;
//import
import android.widget.AdapterView;
//import
import android.widget.AdapterView.OnItemClickListener;
//import
import android.widget.Button;
//import
import android.widget.ImageView;
//import
import android.widget.ListView;
//import
import android.widget.RelativeLayout;
//import
import android.widget.TextView;
//import
import com.crashlytics.android.Crashlytics;
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
//import
import com.vsnmobil.valrt.adapters.PairedDeviceAdapter;
//import
import com.vsnmobil.valrt.adapters.ScannedDeviceAdapter;
//import
import com.vsnmobil.valrt.model.ScannedDevice;
//import
import com.vsnmobil.valrt.services.BluetoothLeService;
//import
import com.vsnmobil.valrt.services.ReconnectService;
//import
import com.vsnmobil.valrt.storage.DatabaseHelper;
//import
import com.vsnmobil.valrt.utils.LogUtils;
//import
import com.vsnmobil.valrt.utils.Utils;
//import
import java.util.ArrayList;
//import
import java.util.HashMap;
//import
import java.util.List;
//import
import java.util.Timer;
//import
import java.util.TimerTask;

/**
 * The Class ManageDevicesActivity.
 */
public class ManageDevicesActivity extends Activity implements BluetoothAdapter.LeScanCallback, AdapterView.OnItemClickListener {
    /** The tag. */
    private String TAG = LogUtils.makeLogTag(ManageDevicesActivity.class);
    /** The Constant REQUEST_ENABLE_BT. */
    public static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ENABLE_FINE_LOCATION = 2;
    /** The handler. */
    private Handler handler = new Handler();
    /** The bluetooth adapter. */
    private BluetoothAdapter bluetoothAdapter;
    /** The bluetooth le service. */
    private BluetoothLeService bluetoothLeService;
    /** The scanned device adapter. */
    private ScannedDeviceAdapter scannedDeviceAdapter;
    /** The paired device adapter. */
    private PairedDeviceAdapter pairedDeviceAdapter;
    /** The db helper. */
    private DatabaseHelper dbHelper;
    /** The device list. */
    private HashMap<String, String> deviceList;
    /** The paired device. */
    private ArrayList<HashMap<String, String>> pairedDevice;
    /** The device address list. */
    private List<String> deviceAddressList;
    /** The scanning device list view. */
    private ListView scanningDeviceListView;
    /** The paied device list view. */
    private ListView paiedDeviceListView;
    /** The dialog. */
    private Dialog dialog;
    /** The delay. */
    private long DELAY = 2000;
    /** The device address. */
    private String deviceAddress;
    /** The is scanning. */
    private boolean isScanning;
    /** The is spinning. */
    private boolean isSpinning = false;
    /** The tutor layout. */
    private RelativeLayout tutorLayout;
    /** The available device layout. */
    private RelativeLayout availableDeviceLayout;
    /** The paired device layout. */
    private RelativeLayout pairedDeviceLayout;
    /** The loading layout. */
    private RelativeLayout loadingLayout;
    /** The large loading image view. */
    private ImageView largeLoadingImageView;
    /** The no device available text view. */
    private TextView noDeviceAvailableTextView;
    /** The no paired device available text view. */
    private TextView noPairedDeviceAvailableTextView;
    /** The loading image view. */
    private ImageView loadingImageView;
    /** The back button. */
    private Button backButton;
    /** The next button. */
    private Button nextButton;
    /** The anim. */
    private RotateAnimation anim;
    /** The anim large. */
    private RotateAnimation animLarge;
    /** The timer delay connect. */
    private Timer timerDelayConnect = null;
    /** The already sent request. */
    private List<String> alreadyRequested = new ArrayList<String>();
    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managedevices);
        //Database helper instance.
        dbHelper = DatabaseHelper.getInstance(this);
        tutorLayout = (RelativeLayout) findViewById(R.id.managedevices_tutor_layout);
        availableDeviceLayout = (RelativeLayout) findViewById(R.id.managedevice_available_devices_layout);
        pairedDeviceLayout = (RelativeLayout) findViewById(R.id.managedevices_paired_devices_layout);
        loadingLayout = (RelativeLayout) findViewById(R.id.managedevice_loading_layout);
        noPairedDeviceAvailableTextView = (TextView) findViewById(R.id.managedevices_no_paired_device_textview);
        noDeviceAvailableTextView = (TextView) findViewById(R.id.managedevices_no_device_avilable_textview);
        scanningDeviceListView = (ListView) findViewById(R.id.managedevices_scanning_device_listview);
        loadingImageView = (ImageView) findViewById(R.id.managedevices_loading_imageview);
        largeLoadingImageView = (ImageView) findViewById(R.id.managedevice_large_loading_imageview);
        paiedDeviceListView = (ListView) findViewById(R.id.managedevices_paired_listview);
        backButton = (Button) findViewById(R.id.managedevices_back_button);
        nextButton = (Button) findViewById(R.id.managedevices_next_button);
        nextButton.setEnabled(false);
        // loading animation
        anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(3000);
        loadingImageView.startAnimation(anim);
        largeLoadingImageView.startAnimation(anim);
        animLarge = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animLarge.setInterpolator(new LinearInterpolator());
        animLarge.setRepeatCount(Animation.INFINITE);
        animLarge.setDuration(2000);
        largeLoadingImageView.startAnimation(animLarge);
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.CONGRATULATION) == true) {
            nextButton.setVisibility(View.INVISIBLE);
        }
        //Start the Blue tooth Le service class and bind wit the service.
        Intent i = new Intent(this, BluetoothLeService.class);
        startService(i);
        bindService(i, mServiceConnection, BIND_AUTO_CREATE);
        //Adapter to load the Scanning Device in list view.
        scannedDeviceAdapter = new ScannedDeviceAdapter(this, R.layout.listitem_device, new ArrayList<ScannedDevice>());
        scanningDeviceListView.setAdapter(scannedDeviceAdapter);
        // To navigate back to the previous screen.
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSpinning == false)
                    finish();
            }
        });
        // To navigate to the next screen (home screen).
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //if it's first time navigate to congratulations screen
                if (VALRTApplication.getPrefBoolean(ManageDevicesActivity.this, VALRTApplication.CONGRATULATION) == false) {
                    Intent i = new Intent(ManageDevicesActivity.this, CongratulationsActivity.class);
                    i.putExtra(VALRTApplication.INTENT_DEVICE_ADDRESS, deviceAddress);
                    startActivity(i);
                }
            }
        });
        // Check the Blue tooth support is enabled or not.
        BluetoothManager manager = VALRTApplication.getManager(this);
        if (manager != null) {
            bluetoothAdapter = manager.getAdapter();
        }
        if (bluetoothAdapter == null) {
            Utils.showToast(this, getString(R.string.ble_not_supported));
            finish();
            return;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_ENABLE_FINE_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    startScan();
                } else
                {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
    /**
     * Sets the paired device.
     */
    //To show the paired device in list view.
    private void setPairedDevice() {
        pairedDevice = dbHelper.getPairedDeviceList();
        if (pairedDevice.size() != 0) {
            String status = pairedDevice.get(0).get(VALRTApplication.DEVICE_STATUS);
            tutorLayout.setVisibility(View.GONE);
            availableDeviceLayout.setVisibility(View.GONE);
            if (status.equalsIgnoreCase(VALRTApplication.CONNECTING)) {
                pairedDeviceLayout.setVisibility(View.VISIBLE);
                nextButton.setTextColor(getResources().getColor(R.color.gray_color));
                nextButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_next_disable_arrow, 0);
                nextButton.setEnabled(false);
            } else if (status.equalsIgnoreCase(VALRTApplication.CONNECTED)) {
                isSpinning = false;
                pairedDeviceLayout.setVisibility(View.VISIBLE);
                largeLoadingImageView.clearAnimation();
                loadingLayout.setVisibility(View.INVISIBLE);
                nextButton.setEnabled(true);
            } else if (status.equalsIgnoreCase(VALRTApplication.DISCONNECTED)) {
                pairedDeviceLayout.setVisibility(View.VISIBLE);
                largeLoadingImageView.clearAnimation();
                loadingLayout.setVisibility(View.INVISIBLE);
                nextButton.setEnabled(true);
                nextButton.setTextColor(getResources().getColor(R.color.gray_color));
                nextButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_next_disable_arrow, 0);
                VALRTApplication.isUpgraded = true;
                startService(new Intent(this, BluetoothLeService.class));
                stopScan();
                startScan();
            }
            //At least one puck need to be paired with the app.Then only the next button will be shown.
            if (VALRTApplication.getPrefBoolean(this, VALRTApplication.CONGRATULATION) == false) {
                if (pairedDevice.size() != 0) {
                    String connectStatus = pairedDevice.get(0).get(VALRTApplication.DEVICE_STATUS);
                    if (connectStatus.equalsIgnoreCase(VALRTApplication.CONNECTED) || connectStatus.equalsIgnoreCase(VALRTApplication.DISCONNECTED)) {
                        nextButton.setTextColor(getResources().getColor(R.color.violet_color));
                        nextButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_next_enable_arrow, 0);
                    }
                }

            }
            noPairedDeviceAvailableTextView.setVisibility(View.INVISIBLE);
            paiedDeviceListView.setVisibility(View.VISIBLE);
        } else {
            tutorLayout.setVisibility(View.VISIBLE);
            pairedDeviceLayout.setVisibility(View.GONE);
            availableDeviceLayout.setVisibility(View.VISIBLE);
            nextButton.setTextColor(getResources().getColor(R.color.gray_color));
            nextButton.setEnabled(false);
            nextButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_next_disable_arrow, 0);
            paiedDeviceListView.setVisibility(View.INVISIBLE);
            noPairedDeviceAvailableTextView.setVisibility(View.VISIBLE);
        }

        pairedDeviceAdapter = new PairedDeviceAdapter(ManageDevicesActivity.this, pairedDevice);
        paiedDeviceListView.setAdapter(pairedDeviceAdapter);
        paiedDeviceListView.setOnItemClickListener(ManageDevicesActivity.this);
    }
    /**
     * Inits the.
     */
    private void init() {
        // if already reconnect service is running.Just stop the service.Because,we are going
        //scan in foreground here.
        stopService(new Intent(this, ReconnectService.class));
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.CONGRATULATION) == true) {
            nextButton.setVisibility(View.INVISIBLE);
        }
        // Initialize List view and adapter to load the Device list while
        // scanning.
        scannedDeviceAdapter = new ScannedDeviceAdapter(this, R.layout.listitem_device, new ArrayList<ScannedDevice>());
        scanningDeviceListView.setAdapter(scannedDeviceAdapter);
        // Scanned Device item click listener
        scanningDeviceListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {
                ScannedDevice item = scannedDeviceAdapter.getItem(position);
                alreadyRequested.clear();
                if (item != null) {
                    //Restrict that only once device can be connected.
                    if ((dbHelper.getPairedDeviceCount() >= 1)) {
                        alertDialogShow(getString(R.string.pls_forget_device));
                    } else {
                        //Add the device from scanned list to paired device.
                        deviceList = new HashMap<String, String>();
                        deviceList.put(VALRTApplication.DEVICE_ADDRESS, item.getDeviceMac());
                        deviceList.put(VALRTApplication.DEVICE_NAME, item.getDisplayName());
                        deviceList.put(VALRTApplication.DEVICE_STATUS, VALRTApplication.CONNECTING);
                        dbHelper.insertBleDevice(deviceList);
                        deviceAddress = item.getDeviceMac();
                        connectDevice(item.getDeviceMac());
                        isSpinning = true;
                        tutorLayout.setVisibility(View.GONE);
                        availableDeviceLayout.setVisibility(View.GONE);
                        loadingLayout.setVisibility(View.VISIBLE);
                        largeLoadingImageView.startAnimation(animLarge);
                        try {
                            scannedDeviceAdapter.remove(item);
                        } catch (Exception e) {
                            LogUtils.i(TAG, e);
                        }
                        //Stop and start scan again to refresh the device list.
                        stopScan();
                        startScan();
                        if (pairedDeviceAdapter.getCount() == 0) {
                            noDeviceAvailableTextView.setVisibility(View.VISIBLE);
                            scanningDeviceListView.setVisibility(View.INVISIBLE);
                        }
                        updateUI();
                    }
                }
            }
        });
        // Start scanning
        startScan();
    }
    /**
     * Update ui.
     */
    public void updateUI() {
        runOnUiThread(new Runnable() {
            public void run() {
                setPairedDevice();
            }
        });
    }
    /**
     * On resume.
     */
    @Override
    protected void onResume() {
        super.onResume();
        VALRTApplication.isScanActivityRunning = true;
        loadingImageView.setVisibility(View.VISIBLE);
        handler.postDelayed(runnable, DELAY);
        // Check the Blue tooth is enable or not
        // If not request the user to switch on the Blue tooth
        if (!bluetoothAdapter.isEnabled()) {
            final Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }
        // If yes call the init()
        init();
        updateUI();

    }
    /**
     * On pause.
     */
    @Override
    protected void onPause() {
        super.onPause();
        // Stop scanning
        stopScan();
        VALRTApplication.isScanActivityRunning = false;
        handler.removeCallbacks(runnable);
    }
    /**
     * On destroy.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel timer
        if (timerDelayConnect != null) {
            timerDelayConnect.cancel();
        }
        VALRTApplication.isScanActivityRunning = false;
        // Stop scanning
        stopScan();
        // UNBIND the BluetoothLeService connection
        unbindService(mServiceConnection);
        handler.removeCallbacks(runnable);
    }
    /**
     * On le scan.
     *
     * @param newDeivce the new deivce
     * @param newRssi the new rssi
     * @param newScanRecord the new scan record
     */
    // Blue tooth le scanner to scan the Blue tooth low energy device.
    @Override
    public void onLeScan(final BluetoothDevice newDeivce, final int newRssi, final byte[] newScanRecord) {
        // To filter the VSN Device.
        if (Utils.isVSNDevice(newDeivce.getName(), newDeivce.getAddress())) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    deviceAddressList = dbHelper.getAllDeviceAddress();
                    //LogUtils.LOGD("****** Hao: ", "runOnUIThread: Name -> "+newDeivce.getName() + " Address -> "+newDeivce.getAddress());

                    if (!deviceAddressList.contains(newDeivce.getAddress())) {
                        //LogUtils.LOGD("****** Hao: NewDevice ", newDeivce.getAddress());
                        noDeviceAvailableTextView.setVisibility(View.GONE);
                        loadingImageView.clearAnimation();
                        loadingImageView.setVisibility(View.INVISIBLE);
                        scanningDeviceListView.setVisibility(View.VISIBLE);
                        scannedDeviceAdapter.update(newDeivce, newRssi, newScanRecord);
                    } else {
                        connectDevice(newDeivce.getAddress());
                    }
                }
            });
        }
    }
    /**
     * Start scan.
     */
    // Start scan
    private void startScan() {
        try {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
            {
                // Permission is not granted
                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_ENABLE_FINE_LOCATION);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }else {

                if ((bluetoothAdapter != null) && (!isScanning)) {
                    bluetoothAdapter.startLeScan(ManageDevicesActivity.this);
                    isScanning = true;
                }
            }
        } catch (Exception e) {
            LogUtils.i(TAG, e); //logException
        }
    }
    /**
     * Stop scan.
     */
    // Stop scan
    private void stopScan() {
        try {
            if (bluetoothAdapter != null) {
                bluetoothAdapter.stopLeScan(ManageDevicesActivity.this);
            }
            isScanning = false;
        } catch (Exception e) {
            LogUtils.i(TAG, e); //logException
        }
    }
    /**
     * On item click.
     *
     * @param arg0 the arg0
     * @param arg1 the arg1
     * @param position the position
     * @param arg3 the arg3
     */
    // Item click listener for paired device
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        String deviceAddress = pairedDevice.get(position).get(VALRTApplication.DEVICE_ADDRESS);
        try {
            deleteDevice(deviceAddress);
        } catch (Exception e) {
            LogUtils.i(TAG, e); //logException
        }
    }
    /** The m service connection. */
    // To manage Service class life cycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
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
    /**
     * Connect device.
     *
     * @param mDeviceAddress the m device address
     */
    // Connect the device with BluetoothLeService
    public void connectDevice(final String mDeviceAddress)
    {
        Crashlytics.setString("device_address",mDeviceAddress);
        VALRTApplication.isForgetMeClicked = false;
        if (bluetoothLeService != null)
        {
            //LogUtils.LOGD("****** Hao:", "connectDevice");
            if (!alreadyRequested.contains(mDeviceAddress))
            {
                //LogUtils.LOGD("****** Hao:", "connectDevice --> SendConnect");

                bluetoothLeService.connect(mDeviceAddress);
                alreadyRequested.add(mDeviceAddress);
            } else {
                if(timerDelayConnect==null) {
                    timerDelayConnect = new Timer();
                    timerDelayConnect.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            alreadyRequested.clear();
                            timerDelayConnect.purge();
                            timerDelayConnect = null;
                        }
                    }, 10000);
                }
            }
        }
    }
    /** The runnable. */
    // Update the UI dynamically from the database.
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, DELAY);
            updateUI();
        }
    };
    /**
     * Delete device.
     *
     * @param mDeviceAddress the m device address
     */
    // To remove the contacts from the emergency list
    public void deleteDevice(final String mDeviceAddress) {
        if (largeLoadingImageView != null)
            largeLoadingImageView.clearAnimation();
        loadingLayout.setVisibility(View.INVISIBLE);
        dialog = new Dialog(this, R.style.ThemeWithCorners);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_alert_yes_no);
        dialog.setCancelable(false);
        TextView forgetTitle = (TextView) dialog.findViewById(R.id.alert_title_textview);
        TextView forgetMessage = (TextView) dialog.findViewById(R.id.alert_content_textview);
        forgetMessage.setVisibility(View.GONE);
        Button forgetYesButton = (Button) dialog.findViewById(R.id.alert_yes_button);
        Button forgetNoButton = (Button) dialog.findViewById(R.id.alert_no_button);
        forgetTitle.setText(getString(R.string.are_you_sure_forget_device));
        forgetYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothGatt bGatt = null;
                VALRTApplication.isForgetMeClicked = true;
                try {
                    if (bluetoothLeService != null) {
                        bGatt = bluetoothLeService.getGatt(mDeviceAddress);
                        if (bGatt != null) {
                            bluetoothLeService.disconnect(bGatt, true);
                        }
                    }
                } catch (Exception e) {
                    LogUtils.i(TAG, e); //logException
                }
                dbHelper.deleteDevice(mDeviceAddress);
                stopScan();
                startScan();
                dialog.dismiss();
            }
        });
        forgetNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    /**
     * On activity result.
     *
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data the data
     */
    // On Blue tooth Activity Intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Utils.showToast(this, getString(R.string.bluetooth_enabled));
            }
            if (resultCode == RESULT_CANCELED) {
                Utils.showToast(this, getString(R.string.bluetooth_off));
                ManageDevicesActivity.this.finish();
            }
        }
    }
    /**
     * Alert dialog show.
     *
     * @param message the message
     */
    public void alertDialogShow(String message) {
        dialog = new Dialog(this, R.style.ThemeWithCorners);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_info);
        dialog.setCancelable(false);

        TextView messageTextview = (TextView) dialog.findViewById(R.id.info_title_textview);
        Button okButton = (Button) dialog.findViewById(R.id.info_ok_button);
        messageTextview.setText(message);
        dialog.show();
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    /**
     * On back pressed.
     */
    @Override
    public void onBackPressed() {
        if (isSpinning == false) {
            super.onBackPressed();
        }
    }
}
