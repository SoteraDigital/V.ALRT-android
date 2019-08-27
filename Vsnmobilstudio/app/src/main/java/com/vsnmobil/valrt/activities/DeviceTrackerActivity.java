package com.vsnmobil.valrt.activities;

import android.app.Activity;
//import
import android.app.Dialog;
//import
import android.os.Bundle;
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
import com.vsnmobil.valrt.storage.DatabaseHelper;
/**
 * DeviceTrackerActivity.java
 * <p/>
 * This class contains the settings to enable/ disable the device tracking features.
 * Device tracking features: Alert tone and  Vibration.
 */
public class DeviceTrackerActivity extends Activity implements View.OnClickListener {
    /** The alert tone button. */
    private Button alertToneButton;
    /** The vibrate button. */
    private Button vibrateButton;
    /** The db helper. */
    private DatabaseHelper dbHelper;
    /** The dialog. */
    private Dialog dialog;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_tracker);
        ((LinearLayout) findViewById(R.id.tracker_settings_tone_device_layout)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.tracker_settings_vibrate_phone_layout)).setOnClickListener(this);
        (alertToneButton = (Button) findViewById(R.id.tracker_settings_tone_button)).setOnClickListener(this);
        (vibrateButton = (Button) findViewById(R.id.tracker_settings_vibrate_button)).setOnClickListener(this);
        ((Button) findViewById(R.id.tracker_settings_back_button)).setOnClickListener(this);
        dbHelper = new DatabaseHelper(this);
    }
    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tracker_settings_tone_button:
            case R.id.tracker_settings_tone_device_layout:
                // Check box to enable/disable the phone to silent mode
                if (!VALRTApplication.getPrefBoolean(DeviceTrackerActivity.this, VALRTApplication.DEVICE_TRACKER_ALERT_TONE_STATUS)) {
                    if (VALRTApplication.getPrefBoolean(DeviceTrackerActivity.this, VALRTApplication.PHONESILENTCBX)) {
                        dialog = new Dialog(DeviceTrackerActivity.this, R.style.ThemeWithCorners);
                        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_alert_yes_no);
                        dialog.setCancelable(false);
                        TextView phoneTitleTextView = (TextView) dialog.findViewById(R.id.alert_title_textview);
                        TextView phoneMessageTextView = (TextView) dialog.findViewById(R.id.alert_content_textview);
                        phoneMessageTextView.setVisibility(View.GONE);
                        Button phoneYesButton = (Button) dialog.findViewById(R.id.alert_yes_button);
                        Button phoneNoButton = (Button) dialog.findViewById(R.id.alert_no_button);
                        phoneYesButton.setText(getString(R.string.accept));
                        phoneNoButton.setText(getString(R.string.cancel));
                        phoneTitleTextView.setText(getString(R.string.tracker_tone_checked_silentmode_on_message));
                        phoneYesButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                VALRTApplication.setPrefBoolean(DeviceTrackerActivity.this, VALRTApplication.DEVICE_TRACKER_ALERT_TONE_STATUS, true);
                                alertToneButton.setBackground(getResources().getDrawable(R.drawable.bg_enable_checkbox));
                                dbHelper.insertDeviceHistory(getString(R.string.history_tracker_loud_tone_on));
                                if (VALRTApplication.getPrefBoolean(DeviceTrackerActivity.this, VALRTApplication.PHONESILENTCBX)) {
                                    VALRTApplication.setPrefBoolean(DeviceTrackerActivity.this, VALRTApplication.PHONESILENTCBX, false);
                                    dbHelper.insertDeviceHistory(getString(R.string.history_application_silentmode_off));
                                }
                                dialog.dismiss();
                            }
                        });
                        phoneNoButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertToneButton.setBackground(getResources().getDrawable(R.drawable.img_plain_disable_checkbox));
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else {
                        VALRTApplication.setPrefBoolean(DeviceTrackerActivity.this, VALRTApplication.DEVICE_TRACKER_ALERT_TONE_STATUS, true);
                        alertToneButton.setBackground(getResources().getDrawable(R.drawable.bg_enable_checkbox));
                        dbHelper.insertDeviceHistory(getString(R.string.history_tracker_loud_tone_on));
                    }
                } else {
                    VALRTApplication.setPrefBoolean(DeviceTrackerActivity.this, VALRTApplication.DEVICE_TRACKER_ALERT_TONE_STATUS, false);
                    alertToneButton.setBackground(getResources().getDrawable(R.drawable.img_plain_disable_checkbox));
                    dbHelper.insertDeviceHistory(getString(R.string.history_tracker_loud_tone_off));
                }
                break;
            case R.id.tracker_settings_vibrate_button:
            case R.id.tracker_settings_vibrate_phone_layout:
                if (!VALRTApplication.getPrefBoolean(DeviceTrackerActivity.this, VALRTApplication.DEVICE_TRACKER_VIBRATION_STATUS)) {
                    VALRTApplication.setPrefBoolean(this, VALRTApplication.DEVICE_TRACKER_VIBRATION_STATUS, true);
                    dbHelper.insertDeviceHistory(getString(R.string.history_tracker_vibrate_on));
                    vibrateButton.setBackground(getResources().getDrawable(R.drawable.bg_enable_checkbox));
                } else {
                    VALRTApplication.setPrefBoolean(this, VALRTApplication.DEVICE_TRACKER_VIBRATION_STATUS, false);
                    dbHelper.insertDeviceHistory(getString(R.string.history_tracker_vibrate_off));
                    vibrateButton.setBackground(getResources().getDrawable(R.drawable.img_plain_disable_checkbox));
                }
                break;
            case R.id.tracker_settings_back_button:
                finish();
                break;
        }
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        //alert tone check box status
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.DEVICE_TRACKER_ALERT_TONE_STATUS)) {
            alertToneButton.setBackground(getResources().getDrawable(R.drawable.bg_enable_checkbox));
        } else {
            alertToneButton.setBackground(getResources().getDrawable(R.drawable.img_plain_disable_checkbox));
        }
        // Vibrator check box status
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.DEVICE_TRACKER_VIBRATION_STATUS)) {
            vibrateButton.setBackground(getResources().getDrawable(R.drawable.bg_enable_checkbox));
        } else {
            vibrateButton.setBackground(getResources().getDrawable(R.drawable.img_plain_disable_checkbox));
        }
    }
}
