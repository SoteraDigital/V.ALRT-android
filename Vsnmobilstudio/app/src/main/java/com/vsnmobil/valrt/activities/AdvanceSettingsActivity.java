package com.vsnmobil.valrt.activities;

import android.app.Activity;
//import
import android.app.Dialog;
//import
import android.content.ComponentName;
//import
import android.content.Context;
//import
import android.content.Intent;
//import
import android.content.ServiceConnection;
//import
import android.media.RingtoneManager;
//import
import android.net.Uri;
//import
import android.os.AsyncTask;
//import
import android.os.Bundle;
//import
import android.os.IBinder;
//import
import android.provider.Settings;
//import
import android.text.TextUtils;
//import
import android.view.View;
//import
import android.view.Window;
//import
import android.widget.Button;
//import
import android.widget.LinearLayout;
//import
import android.widget.TextView;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
//import
import com.vsnmobil.valrt.services.BluetoothLeService;
//import
import com.vsnmobil.valrt.storage.DatabaseHelper;
//import
import com.vsnmobil.valrt.utils.LogUtils;
//import
import com.vsnmobil.valrt.utils.Utils;
/**
 * AdvanceSettingsActivity.java
 * <p/>
 * In this class we have a advance options like to choose the alert tone from Ring tone and also
 * having controls to enable and disable the silent mode of Phone application and valert device.
 */
public class AdvanceSettingsActivity extends Activity implements View.OnClickListener { //AdvanceSettingsActivity
    /** The Constant TAG. */
    private final static String TAG = AdvanceSettingsActivity.class.getSimpleName();
    /** The Constant RINGTONE_PICKER_RESULT. */
    private static final int RINGTONE_PICKER_RESULT = 1002;
    /** The bluetooth le service. */
    private BluetoothLeService bluetoothLeService;
    /** The db helper. */
    private DatabaseHelper dbHelper;
    /** The intent. */
    private Intent intent;
    /** The uri. */
    private Uri uri;
    /** The default tone linear layout. */
    private LinearLayout defaultToneLinearLayout;
    /** The alert tone text view. */
    private TextView alertToneTextView;
    /** The title text view. */
    private TextView titleTextView;
    /** The silent device button. */
    private Button silentDeviceButton;
    /** The silent phone button. */
    private Button silentPhoneButton;
    /** The panic tone button. */
    private Button panicToneButton;
    /** The dialog. */
    private Dialog dialog;
    /** The ringtone path. */
    private String ringtonePath;
    /** The ringtone name. */
    private String ringtoneName;
    /** The silenttrue. */
    // silent constants
    private String SILENTTRUE = "1";
    /** The silentfalse. */
    private String SILENTFALSE = "0";
    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {//onCreate
        super.onCreate(savedInstanceState);//super
        setContentView(R.layout.activity_advance_settings);//setContentView
        intent = new Intent(this, BluetoothLeService.class);//intent passing
        startService(intent);//startService
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);//bindService
        titleTextView = (TextView) findViewById(R.id.advance_settings_title_textview); //titleTextView
        (defaultToneLinearLayout = (LinearLayout) findViewById(R.id.advance_settings_default_tone_layout)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.advance_settings_panic_tone_layout)).setOnClickListener(this);//advance_settings_panic_tone_layout
        ((LinearLayout) findViewById(R.id.advance_settings_silent_device_layout)).setOnClickListener(this);//advance_settings_silent_device_layout
        ((LinearLayout) findViewById(R.id.advance_settings_silent_phone_layout)).setOnClickListener(this);//advanceSettingsphoneLayout
        alertToneTextView = (TextView) findViewById(R.id.advance_settings_alert_tone_name_textview);//alertToneTextView
        ((Button) findViewById(R.id.advance_settings_back_button)).setOnClickListener(this);//advanceSettingsBackButton
        silentDeviceButton = (Button) findViewById(R.id.advance_settings_silent_device_button);//silentDeviceButton
        silentDeviceButton.setOnClickListener(this);//silentDeviceButton
        silentPhoneButton = (Button) findViewById(R.id.advance_settings_silent_phone_button); //silentPhoneButton
        silentPhoneButton.setOnClickListener(this); //silentPhoneButton
        panicToneButton = (Button) findViewById(R.id.advance_settings_panic_tone_button); //panicToneButton
        panicToneButton.setOnClickListener(this); //panicToneButton
        //Heading to support the v.35 mobile
        if (Utils.getScreenSize(this) <= 3) {
            titleTextView.setText(getString(R.string.advance_settings_heading));
        }
        dbHelper = new DatabaseHelper(this);
    }
    /**
     * On click.
     *
     * @param v the v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.advance_settings_back_button:
                if (VALRTApplication.getPrefBoolean(AdvanceSettingsActivity.this, VALRTApplication.CONGRATULATION) == true) {
                    onBackPressed();
                    finish();
                } else {
                    finish();
                }
                break;
            case R.id.advance_settings_default_tone_layout:
                doLaunchRingtonePicker();
                break;
            case R.id.advance_settings_panic_tone_layout:
            case R.id.advance_settings_panic_tone_button:
                if (!VALRTApplication.getPrefBoolean(AdvanceSettingsActivity.this, VALRTApplication.PANICTONECBX)) {
                    VALRTApplication.setPrefBoolean(AdvanceSettingsActivity.this, VALRTApplication.PANICTONECBX, true);
                    panicToneButton.setBackground(getResources().getDrawable(R.drawable.bg_enable_checkbox));
                    Utils.enableDisableView(defaultToneLinearLayout, false);
                    defaultToneLinearLayout.setAlpha((float) 0.2);
                } else {
                    VALRTApplication.setPrefBoolean(AdvanceSettingsActivity.this, VALRTApplication.PANICTONECBX, false);
                    panicToneButton.setBackground(getResources().getDrawable(R.drawable.img_plain_disable_checkbox));
                    Utils.enableDisableView(defaultToneLinearLayout, true);
                    defaultToneLinearLayout.setAlpha((float) 1);
                }
                break;
            case R.id.advance_settings_silent_phone_button:
            case R.id.advance_settings_silent_phone_layout:
                // Check box to enable/disable the phone to silent mode
                if (VALRTApplication.getPrefBoolean(this, VALRTApplication.DEVICE_TRACKER_ALERT_TONE_STATUS)) {
                    dialog = new Dialog(AdvanceSettingsActivity.this, R.style.ThemeWithCorners);
                    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_alert_yes_no);
                    dialog.setCancelable(false);
                    TextView phoneTitleTextView = (TextView) dialog.findViewById(R.id.alert_title_textview);
                    TextView phoneMessageTextView = (TextView) dialog.findViewById(R.id.alert_content_textview);
                    phoneMessageTextView.setVisibility(View.GONE);
                    Button phoneYesButton = (Button) dialog.findViewById(R.id.alert_yes_button);
                    Button phoneNoButton = (Button) dialog.findViewById(R.id.alert_no_button);
                    phoneTitleTextView.setText(getString(R.string.phone_checked_device_tracker_message));
                    phoneYesButton.setText(getString(R.string.accept));
                    phoneNoButton.setText(getString(R.string.cancel));
                    phoneYesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!VALRTApplication.getPrefBoolean(AdvanceSettingsActivity.this, VALRTApplication.PHONESILENTCBX)) {
                                VALRTApplication.setPrefBoolean(AdvanceSettingsActivity.this, VALRTApplication.PHONESILENTCBX, true);
                                dbHelper.insertDeviceHistory(getString(R.string.history_application_silentmode_on));
                                silentPhoneButton.setBackground(getResources().getDrawable(R.drawable.bg_enable_checkbox));
                                if (VALRTApplication.getPrefBoolean(AdvanceSettingsActivity.this, VALRTApplication.DEVICE_TRACKER_ALERT_TONE_STATUS)) {
                                    VALRTApplication.setPrefBoolean(AdvanceSettingsActivity.this, VALRTApplication.DEVICE_TRACKER_ALERT_TONE_STATUS, false);
                                    dbHelper.insertDeviceHistory(getString(R.string.history_tracker_loud_tone_off));
                                }
                            } else {
                                VALRTApplication.setPrefBoolean(AdvanceSettingsActivity.this, VALRTApplication.PHONESILENTCBX, false);
                                dbHelper.insertDeviceHistory(getString(R.string.history_application_silentmode_off));
                                silentPhoneButton.setBackground(getResources().getDrawable(R.drawable.img_plain_disable_checkbox));
                            }
                            dialog.dismiss();
                        }
                    });
                    phoneNoButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (VALRTApplication.getPrefBoolean(AdvanceSettingsActivity.this, VALRTApplication.PHONESILENTCBX)) {
                                silentPhoneButton.setBackground(getResources().getDrawable(R.drawable.bg_enable_checkbox));
                            } else {
                                silentPhoneButton.setBackground(getResources().getDrawable(R.drawable.img_plain_disable_checkbox));
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    if (!VALRTApplication.getPrefBoolean(AdvanceSettingsActivity.this, VALRTApplication.PHONESILENTCBX)) {
                        VALRTApplication.setPrefBoolean(AdvanceSettingsActivity.this, VALRTApplication.PHONESILENTCBX, true);
                        dbHelper.insertDeviceHistory(getString(R.string.history_application_silentmode_on));
                        silentPhoneButton.setBackground(getResources().getDrawable(R.drawable.bg_enable_checkbox));
                    } else {
                        VALRTApplication.setPrefBoolean(AdvanceSettingsActivity.this, VALRTApplication.PHONESILENTCBX, false);
                        dbHelper.insertDeviceHistory(getString(R.string.history_application_silentmode_off));
                        silentPhoneButton.setBackground(getResources().getDrawable(R.drawable.img_plain_disable_checkbox));
                    }
                }
                break;
            case R.id.advance_settings_silent_device_button:
            case R.id.advance_settings_silent_device_layout:
                //final DeviceSilentTask task = new DeviceSilentTask();
                if (!VALRTApplication.getPrefBoolean(AdvanceSettingsActivity.this, VALRTApplication.DEVICESILENTCBX)) {
                    //task.execute(SILENTTRUE);
                    bluetoothLeService.silentAllDevice(true);
                    VALRTApplication.setPrefBoolean(AdvanceSettingsActivity.this, VALRTApplication.DEVICESILENTCBX, true);
                    dbHelper.insertDeviceHistory(getString(R.string.history_device_silentmode_on));
                    silentDeviceButton.setBackground(getResources().getDrawable(R.drawable.bg_enable_checkbox));
                } else {
                    bluetoothLeService.silentAllDevice(false);
                    VALRTApplication.setPrefBoolean(AdvanceSettingsActivity.this, VALRTApplication.DEVICESILENTCBX, false);
                    dbHelper.insertDeviceHistory(getString(R.string.history_device_silentmode_off));
                    silentDeviceButton.setBackground(getResources().getDrawable(R.drawable.img_plain_disable_checkbox));
                    //task.execute(SILENTFALSE);
                }
                break;
        }
    }
    /**
     * On resume.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // To set ring tone selected checked in the picker
        if (TextUtils.isEmpty(VALRTApplication.getPrefString(this, VALRTApplication.ALERTTONENAME))) {
            uri = Settings.System.DEFAULT_NOTIFICATION_URI;
        } else {
            uri = Uri.parse(VALRTApplication.getPrefString(this, VALRTApplication.ALERTTONEPATH));
        }
        // Setting Alert Tone Name
        if (TextUtils.isEmpty(VALRTApplication.getPrefString(this, VALRTApplication.ALERTTONENAME))) {
            String rname = RingtoneManager.getRingtone(this, Settings.System.DEFAULT_NOTIFICATION_URI).getTitle(this);
            alertToneTextView.setText(rname);
        } else {
            alertToneTextView.setText(VALRTApplication.getPrefString(this, VALRTApplication.ALERTTONENAME));
        }
        //Device silent mode check box
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.DEVICESILENTCBX)) {
            silentDeviceButton.setBackground(getResources().getDrawable(R.drawable.bg_enable_checkbox));
        } else {
            silentDeviceButton.setBackground(getResources().getDrawable(R.drawable.img_plain_disable_checkbox));
        }
        //Phone silent mode check box
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.PHONESILENTCBX)) {
            silentPhoneButton.setBackground(getResources().getDrawable(R.drawable.bg_enable_checkbox));
        } else {
            silentPhoneButton.setBackground(getResources().getDrawable(R.drawable.img_plain_disable_checkbox));
        }

        //Panic tone check box
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.PANICTONECBX)) {
            panicToneButton.setBackground(getResources().getDrawable(R.drawable.bg_enable_checkbox));
            Utils.enableDisableView(defaultToneLinearLayout, false);
            defaultToneLinearLayout.setAlpha((float) 0.2);
        } else {
            panicToneButton.setBackground(getResources().getDrawable(R.drawable.img_plain_disable_checkbox));
            Utils.enableDisableView(defaultToneLinearLayout, true);
            defaultToneLinearLayout.setAlpha((float) 1);
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
    /**
     * On destroy.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }
    /**
     * Do launch ringtone picker.
     */
    // Ring tone picker
    public void doLaunchRingtonePicker() {
        Intent ringtonePickerIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,
                getString(R.string.available_tones));
        ringtonePickerIntent
                .putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
        if (uri != null)
            ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
        startActivityForResult(ringtonePickerIntent, RINGTONE_PICKER_RESULT);
    }
    /**
     * On activity result.
     *
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data the data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case (RINGTONE_PICKER_RESULT):
                    uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    if (uri != null) {
                        ringtoneName = RingtoneManager.getRingtone(this, uri).getTitle(this);
                        ringtonePath = uri.toString();
                        VALRTApplication.setPrefString(AdvanceSettingsActivity.this, VALRTApplication.ALERTTONENAME, ringtoneName);
                        VALRTApplication.setPrefString(AdvanceSettingsActivity.this, VALRTApplication.ALERTTONEPATH, ringtonePath);
                        alertToneTextView.setText(ringtoneName);
                    }
                    break;
            }
        }
    }
    /**
     * The Class DeviceSilentTask.
     */
    /*private class DeviceSilentTask extends AsyncTask<String, Void, String> {

        *//**
         * Do in background.
         *
         * @param arg0 the arg0
         * @return the string
         *//*
        @Override
        protected String doInBackground(String... arg0) {
            try {
                if (arg0[0].equalsIgnoreCase(SILENTTRUE))
                    bluetoothLeService.silentAllDevice(true);
                else if (arg0[0].equalsIgnoreCase(SILENTFALSE))
                    bluetoothLeService.silentAllDevice(false);
            } catch (Exception e) {
                LogUtils.LOGE(TAG, "writing for silent mode", e);
            }
            return null;
        }
    }*/
}
