package com.vsnmobil.valrt.fragment;

import android.app.Dialog;
//import
import android.bluetooth.BluetoothGatt;
//import
import android.bluetooth.BluetoothGattCharacteristic;
//import
import android.content.BroadcastReceiver;
//import
import android.content.ComponentName;
//import
import android.content.Context;
//import
import android.content.Intent;
//import
import android.content.IntentFilter;
//import
import android.content.ServiceConnection;
//import
import android.os.AsyncTask;
//import
import android.os.Bundle;
//import
import android.os.Handler;
//import
import android.os.IBinder;
//import
import android.support.v4.app.Fragment;
//import
import android.text.Editable;
//import
import android.text.TextUtils;
//import
import android.text.TextWatcher;
//import
import android.util.Log;
import android.view.LayoutInflater;
//import
import android.view.View;
//import
import android.view.View.OnClickListener;
//import
import android.view.ViewGroup;
//import
import android.view.Window;
//import
import android.view.WindowManager;
//import
import android.widget.Button;
//import
import android.widget.EditText;
//import
import android.widget.ImageView;
//import
import android.widget.RelativeLayout;
//import
import android.widget.TextView;
//import
import com.vsnmobil.valrt.GattConstant;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
//import
import com.vsnmobil.valrt.activities.FindMeActivity;
//import
import com.vsnmobil.valrt.services.BluetoothLeService;
//import
import com.vsnmobil.valrt.storage.DatabaseHelper;
//import
import com.vsnmobil.valrt.utils.LogUtils;
//import
import com.vsnmobil.valrt.utils.Utils;
/**
 * DashboardFragment.java 
 *
 * This class is fragment class which have the view like find me button, signal strength imageview
 * battery status image view and fall enable / disable button and forget device button.
 */
public class DashboardFragment extends Fragment implements OnClickListener {
    /** The tag. */
    private String TAG = LogUtils.makeLogTag(DashboardFragment.class);
    /**
     * New instance.
     *
     * @param deviceName the device name
     * @param deviceAddress the device address
     * @param batteryStatus the battery status
     * @param falldetectionStatus the falldetection status
     * @param position the position
     * @return the dashboard fragment
     */
    public static DashboardFragment newInstance(String deviceName, String deviceAddress,String batteryStatus, String falldetectionStatus, int position) {
        DashboardFragment pageFragment = new DashboardFragment();
        Bundle bundle = new Bundle();
        bundle.putString(VALRTApplication.DEVICE_NAME, deviceName);
        bundle.putString(VALRTApplication.DEVICE_ADDRESS, deviceAddress);
        bundle.putString(VALRTApplication.BATTERY_STATUS, batteryStatus);
        bundle.putString(VALRTApplication.FALLDETECTION_STATUS, batteryStatus);
        bundle.putInt(VALRTApplication.TABPOSITION, position);
        pageFragment.setArguments(bundle);
        return pageFragment;
    }
    /** The bluetooth le service. */
    private BluetoothLeService bluetoothLeService;
    /** The db helper. */
    private DatabaseHelper dbHelper;
    /** The back button. */
    private Button backButton;
    /** The find me relative layout. */
    private RelativeLayout findMeRelativeLayout;
    /** The change name relative layout. */
    private RelativeLayout changeNameRelativeLayout;
    /** The fall detection relative layout. */
    private RelativeLayout fallDetectionRelativeLayout;
    /** The forget me relative layout. */
    private RelativeLayout forgetMeRelativeLayout;
    /** The battery image view. */
    private ImageView batteryImageView;
    /** The signal image view. */
    private ImageView signalImageView;
    /** The device name text view. */
    private TextView deviceNameTextView;
    /** The fall detect status text view. */
    private TextView fallDetectStatusTextView;
    /** The device name. */
    private String deviceName;
    /** The device address. */
    private String deviceAddress;
    /** The device battery. */
    private String deviceBattery;
    /** The delay. */
    private long DELAY = 2000;
    /** The handler. */
    private Handler handler = new Handler();
    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //database helper instance.
        dbHelper = DatabaseHelper.getInstance(getActivity());
        //Bind with the blue tooth le service connection.
        Intent i = new Intent(getActivity(), BluetoothLeService.class);
        getActivity().startService(i);
        getActivity().bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
    }
    /**
     * On create view.
     *
     * @param inflater the inflater
     * @param container the container
     * @param savedInstanceState the saved instance state
     * @return the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        deviceName = getArguments().getString(VALRTApplication.DEVICE_NAME);
        deviceAddress = getArguments().getString(VALRTApplication.DEVICE_ADDRESS);
        deviceBattery = getArguments().getString(VALRTApplication.BATTERY_STATUS);

        deviceNameTextView = (TextView) view.findViewById(R.id.dashboard_device_name_textview);
        fallDetectStatusTextView = (TextView) view.findViewById(R.id.fall_detect_status_textview);

        batteryImageView = (ImageView) view.findViewById(R.id.battery_imageview);
        signalImageView = (ImageView) view.findViewById(R.id.signal_imageview);

        findMeRelativeLayout = (RelativeLayout) view.findViewById(R.id.dashboard_find_me_layout);
        changeNameRelativeLayout = (RelativeLayout) view.findViewById(R.id.dashboard_change_name_layout);
        fallDetectionRelativeLayout = (RelativeLayout) view.findViewById(R.id.dashboard_fall_detection_layout);
        forgetMeRelativeLayout = (RelativeLayout) view.findViewById(R.id.dashboard_forget_layout);
        backButton = (Button) view.findViewById(R.id.dashboard_back_button);
        findMeRelativeLayout.setOnClickListener(this);
        forgetMeRelativeLayout.setOnClickListener(this);
        fallDetectionRelativeLayout.setOnClickListener(this);
        changeNameRelativeLayout.setOnClickListener(this);
        backButton.setOnClickListener(this);
        // Update the battery status.
        if (!deviceBattery.equalsIgnoreCase("-1"))
            bateryStatusUpdate(Integer.parseInt(deviceBattery));
        if (dbHelper.getFalldetectionStatus(deviceAddress).equalsIgnoreCase("1")) {
            //fall detect enabled.
            fallDetectStatusTextView.setText(getString(R.string.enabled));
        } else {
            //fall detect disabled.
            fallDetectStatusTextView.setText(getString(R.string.disabled));
        }
        return view;
    }
    /**
     * On resume.
     */
    @Override
    public void onResume() {
        super.onResume();
        //register the broadcast receiver.
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        //start the handler.
        handler.postDelayed(runnable, DELAY);
    }
    /**
     * On pause.
     */
    @Override
    public void onPause() {
        super.onPause();
        // Remove the handler.
        if (handler != null)
            handler.removeCallbacks(runnable);
        //unregister the broadcast receiver.
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }
    /**
     * On destroy.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null)
            handler.removeCallbacks(runnable);
        //un-bind the service connection with the blue tooth le service.
        getActivity().unbindService(mServiceConnection);
    }
    /** The m service connection. */
    // To manage Service class life cycle of Blue tooth le service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!bluetoothLeService.initialize()) {
                getActivity().finish();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // Don't kill the BluetoothLeService instance
            // while disconnecting the service.
        }
    };
    /** The m gatt update receiver. */
    // Broadcast receiver to receive the update from the Bluetooth le service class.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            String receivedDeviceAddress = intent.getStringExtra(BluetoothLeService.EXTRA_ADDRESS);
            //puck is connected.
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                //Start the handler to read the signal strength
                if (handler != null)
                    handler.postDelayed(runnable, DELAY);
                // set the device name.
                deviceNameTextView.setText(getString(R.string.device_name));
                //enable the fall detect and change name button.
                fallDetectionRelativeLayout.setEnabled(true);
                changeNameRelativeLayout.setEnabled(true);
                bateryStatusUpdate(Integer.parseInt(dbHelper.getBatteryStatus(deviceAddress)));
                //puck disconnected.
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                if (receivedDeviceAddress.equalsIgnoreCase(deviceAddress)) {
                    // change the status to disconnected.
                    deviceNameTextView.setText(R.string.disconnected);
                    // remove the signal  requesting handler.
                    if (handler != null)
                        handler.removeCallbacks(runnable);
                    //disable the fall detect and change name button.
                    fallDetectionRelativeLayout.setEnabled(false);
                    changeNameRelativeLayout.setEnabled(false);
                    signalStatusUpdate(Integer.parseInt("-107"));
                    batteryImageView.setBackgroundResource(R.drawable.img_battery);
                }
                //puck signal strength
            } else if (BluetoothLeService.ACTION_RSSI_STATUS.equals(action)) {
                // Notification
                String value = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                signalStatusUpdate(Integer.parseInt(value));
                //puck battery status.
            } else if (BluetoothLeService.ACTION_BATTERY_STATUS.equals(action)) {
                bateryStatusUpdate(Integer.parseInt(dbHelper.getBatteryStatus(deviceAddress)));
            }
        }
    };
    /**
     * Make gatt update intent filter.
     *
     * @return the intent filter
     */
    //Register the intent filters to receive the updates from service class.
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_RSSI_STATUS);
        intentFilter.addAction(BluetoothLeService.ACTION_BATTERY_STATUS);
        return intentFilter;
    }
    /**
     * On click.
     *
     * @param v the v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // To call the find me functionality in the puck.
            case R.id.dashboard_find_me_layout:
                BluetoothGatt bluetoothGatt = bluetoothLeService.getGatt(deviceAddress);
                deviceName = dbHelper.getDeviceName(deviceAddress);
                if (bluetoothGatt != null) {
                    if (bluetoothLeService.checkConnectionState(bluetoothGatt)) {
                        if (bluetoothLeService.processQueueExecutor.getSize() == 0) {
                            FindMeTask task = new FindMeTask();
                            task.execute();
                            Intent i = new Intent(getActivity(), FindMeActivity.class);
                            i.putExtra(VALRTApplication.FINEMEHEADING, deviceName);
                            startActivity(i);
                        } else {
                            Utils.showToast(getActivity(),getString(R.string.please_wait_a_moment));
                        }
                    } else {
                        Utils.showToast(getActivity(),getString(R.string.disconnected));
                    }
                } else {
                    Utils.showToast(getActivity(),getString(R.string.disconnected));
                }
                break;
            //To trigger the pop up for change name.
            case R.id.dashboard_change_name_layout:
                try {
                    changeName();
                } catch (Exception e) {}
                break;
            // To enable / disable the fall detect.
            case R.id.dashboard_fall_detection_layout:
                try {
                    fallDetect();
                } catch (Exception e) {}
                break;
            //To forget the paired device.
            case R.id.dashboard_forget_layout:
                try {
                    forgetDevice(deviceAddress);
                } catch (Exception e) {
                }
                break;
            //To navigate back to previous screen.
            case R.id.dashboard_back_button:
                getActivity().finish();
                break;
        }
    }
    /** The runnable. */
    // Runnable to request the RSSI value from the connected puck every 2 second.
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (bluetoothLeService != null) {
                    BluetoothGatt bluetoothGatt = bluetoothLeService.getGatt(deviceAddress);
                    if (bluetoothGatt != null) {
                        // read the RSSI value from the puck
                        bluetoothGatt.readRemoteRssi();
                    }
                }
                handler.postDelayed(runnable, DELAY);
            } catch (Exception e) {
                LogUtils.LOGI(TAG, e.getMessage());
            }
        }
    };
    /**
     * To update the battery status to the battery image view based on the value from the
     * service.
     *
     * @param status the status
     */
    // Battery Status Update to UI
    public void bateryStatusUpdate(int status) {
        if (status <= 10){
            batteryImageView.setBackgroundResource(R.drawable.img_battery_one);
        }
        else if (status <= 20){
            batteryImageView.setBackgroundResource(R.drawable.img_battery_two);
        }
        else if (status > 20){
            batteryImageView.setBackgroundResource(R.drawable.img_battery_three);
        }
    }
    /**
     * To update the signal status to the puck in image view based on the value from the
     * service.
     *
     * @param status the status
     */
    public void signalStatusUpdate(int status) {

        if (status >= -63) {
            signalImageView.setBackgroundResource(R.drawable.img_signal_four);
        } else if (status >= -72) {
            signalImageView.setBackgroundResource(R.drawable.img_signal_three);
        } else if (status >= -80) {
            signalImageView.setBackgroundResource(R.drawable.img_signal_two);
        } else if (status >= -87) {
            signalImageView.setBackgroundResource(R.drawable.img_signal_one);
        } else if (status < -100) {
            signalImageView.setBackgroundResource(R.drawable.img_signal);
        }
    }
    /**
     * Write and set notify the fall detect enable / disable to puck through Bluetooth le sevice class.
     *
     * @param status the status
     */
    public void fallStatusUpdate(boolean status) {

        try {
            byte[] writeValue = GattConstant.ENABLE_KEY_DETECTION_VALUE;
            if (status == true) {
                writeValue = GattConstant.ENABLE_FALL_KEY_DETECTION_VALUE;
            }
            BluetoothGatt bluetoothGatt = bluetoothLeService.getGatt(deviceAddress);
            if (bluetoothGatt != null) {
                // first step
                BluetoothGattCharacteristic chr = bluetoothLeService
                        .getGattChar(bluetoothGatt, GattConstant.SERVICE_VSN_SIMPLE_SERVICE,
                                GattConstant.CHAR_DETECTION_CONFIG);
                bluetoothLeService.enableForDetect(bluetoothGatt, chr, writeValue);
            }
        } catch (Exception e) {
            dbHelper.updateDeviceConnectionStatus(deviceAddress, VALRTApplication.DISCONNECTED);
        }
    }
    /**
     * To rename the paired device and update the status to the user.
     *
     * @param newDeviceName the new device name
     * @param deviceAddress the device address
     */
    public void renameDevice(String newDeviceName, String deviceAddress) {
        if (dbHelper.renameDevice(deviceAddress, newDeviceName) == 1) {
            deviceName = newDeviceName;
            Utils.showToast(getActivity(),getString(R.string.device_name_changed_successfully));
        } else if (dbHelper.renameDevice(deviceAddress, newDeviceName) == 0) {
            Utils.showToast(getActivity(),getString(R.string.unable_to_change_name));
        }
    }
    /**
     * The Class FindMeTask.
     */
    // To write the value to the puck to make it beep.
    private class FindMeTask extends AsyncTask<String, Void, String> {
        /**
         * Do in background.
         *
         * @param arg0 the arg0
         * @return the string
         */
        @Override
        protected String doInBackground(String... arg0) {
            try {
                if (bluetoothLeService != null) {
                    BluetoothGatt bluetoothGatt = bluetoothLeService.getGatt(deviceAddress);
                    if (bluetoothGatt != null) {
                        bluetoothLeService.findMEDevice(bluetoothGatt,GattConstant.ENABLE_IMMEDIATE_ALERT_VALUE);
                    }
                }
            } catch (Exception e) {
                dbHelper.updateDeviceConnectionStatus(deviceAddress, VALRTApplication.DISCONNECTED);
            }
            return null;
        }
    }
    /**
     * To enable and disable the fall detect functionality.
     */
    public void fallDetect() {

        final Dialog fallDetectDialog = new Dialog(getActivity(), R.style.ThemeWithCorners);
        fallDetectDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        fallDetectDialog.setContentView(R.layout.dialog_alert_yes_no);
        fallDetectDialog.setCancelable(false);

        TextView fallDetecttitleTextView = (TextView) fallDetectDialog.findViewById(R.id.alert_title_textview);
        TextView fallDetectMessageTextView = (TextView) fallDetectDialog.findViewById(R.id.alert_content_textview);

        Button fallDetectYesButton = (Button) fallDetectDialog.findViewById(R.id.alert_yes_button);
        Button fallDetectNoButton = (Button) fallDetectDialog.findViewById(R.id.alert_no_button);

        if (dbHelper.getFalldetectionStatus(deviceAddress).equalsIgnoreCase("1")) {
            fallDetecttitleTextView.setText(getString(R.string.disable_falldetect_header));
            fallDetectMessageTextView.setVisibility(View.GONE);
        } else {
            fallDetecttitleTextView.setText(getString(R.string.enable_falldetect_header));
            fallDetectMessageTextView.setText(getString(R.string.are_you_sure_enable_falldetection));
        }
        fallDetectYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dbHelper.getFalldetectionStatus(deviceAddress).equalsIgnoreCase("1")) {
                    fallStatusUpdate(false);
                    fallDetectStatusTextView.setText(R.string.disabled);
                    dbHelper.updateDeviceFalldetectionStatus(deviceAddress, "0");
                    dbHelper.insertDeviceHistory(deviceName + "," + deviceAddress + ","
                            + getResources().getString(R.string.history_fall_disable));
                } else {
                    fallStatusUpdate(true);
                    dbHelper.updateDeviceFalldetectionStatus(deviceAddress, "1");
                    fallDetectStatusTextView.setText(R.string.enabled);
                    dbHelper.insertDeviceHistory(deviceName + "," + deviceAddress + ","
                            + getResources().getString(R.string.history_fall_enable));
                }
                fallDetectDialog.dismiss();
            }
        });
        fallDetectNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fallDetectDialog.dismiss();
            }
        });
        fallDetectDialog.show();
    }
    /**
     * To disconnect and delete the paired device from the database.
     *
     * @param deviceAddress the device address
     */
    public void forgetDevice(final String deviceAddress) {

        final Dialog  forgetDialog = new Dialog(getActivity(), R.style.ThemeWithCorners);
        forgetDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        forgetDialog.setContentView(R.layout.dialog_alert_yes_no);

        forgetDialog.setCancelable(false);
        TextView forgettitleTextView = (TextView) forgetDialog.findViewById(R.id.alert_title_textview);
        TextView forgetMessagetextView = (TextView) forgetDialog.findViewById(R.id.alert_content_textview);
        forgetMessagetextView.setVisibility(View.GONE);
        Button forgetYesButton = (Button) forgetDialog.findViewById(R.id.alert_yes_button);
        Button forgetNoButton = (Button) forgetDialog.findViewById(R.id.alert_no_button);
        forgettitleTextView.setText(getString(R.string.are_you_sure_forget_device));
        forgetYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothGatt bGatt = null;
                VALRTApplication.isForgetMeClicked=true;
                try {
                    if (bluetoothLeService != null) {
                        bGatt = bluetoothLeService.getGatt(deviceAddress);
                        if (bGatt != null) {
                            bluetoothLeService.disconnect(bGatt,true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dbHelper.deleteDevice(deviceAddress);
                getActivity().finish();
                forgetDialog.dismiss();
            }
        });
        forgetNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetDialog.dismiss();
            }
        });
        forgetDialog.show();
    }
    /**
     * To change the name of the paired device.
     *
     */
    public void changeName() {
        final Dialog changeNameDialog = new Dialog(getActivity(), R.style.ThemeWithCorners);
        changeNameDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        changeNameDialog.setContentView(R.layout.dialog_rename);
        final TextView titleTextView = (TextView) changeNameDialog.findViewById(R.id.rename_title_textview);
        final EditText newNameEditView = (EditText) changeNameDialog.findViewById(R.id.rename_text_edittext);
        final Button changeNameYesButton = (Button) changeNameDialog.findViewById(R.id.rename_save_button);
        Button changeNameNoButton = (Button) changeNameDialog.findViewById(R.id.rename_cancel_button);
        titleTextView.setText(getString(R.string.change_name));
        newNameEditView.setText(deviceName);
        newNameEditView.setSelectAllOnFocus(true);
        newNameEditView.setSelection(newNameEditView.length());
        newNameEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(newNameEditView.getText().toString().trim())) {
                    newNameEditView.setError(getString(R.string.changename_length_warning));
                } else if (dbHelper.checkDeviceNameAvailability(newNameEditView.getText().toString()) != 0) {
                    newNameEditView.setError(getString(R.string.changename_device_exists));
                } else if (dbHelper.checkDeviceNameAvailability(newNameEditView.getText().toString()) == 0) {
                    newNameEditView.setError(null);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        changeNameYesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(newNameEditView.getText().toString().trim())) {
                    if(dbHelper.checkDeviceNameAvailability(newNameEditView.getText().toString()) != 0){
                        newNameEditView.setError(getString(R.string.changename_device_exists));
                    }else{
                        renameDevice(newNameEditView.getText().toString().trim(), deviceAddress);
                        changeNameDialog.dismiss();
                    }
                }else{
                    newNameEditView.setError(getString(R.string.changename_length_warning));
                }
            }
        });
        changeNameNoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNameDialog.dismiss();
            }
        });
        changeNameDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        changeNameDialog.show();
    }
}
