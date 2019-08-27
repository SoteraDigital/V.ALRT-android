package com.vsnmobil.valrt.activities;

import java.util.ArrayList;
//import
import java.util.HashMap;
//import
import android.Manifest;
import android.app.Activity;
//import
import android.app.Dialog;
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
import android.support.v4.app.ActivityCompat;
import android.util.Log;
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
import android.widget.Button;
//import
import android.widget.ImageView;
//import
import android.widget.LinearLayout;
//import
import android.widget.RelativeLayout;
//import
import android.widget.TextView;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
//import
import com.vsnmobil.valrt.fragment.DashboardFragmentActivity;
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
/**
 * The Class HomeActivity.
 */
public class HomeActivity extends Activity implements View.OnClickListener {
    private static final int MIC_PERMISSION_REQUEST_CODE = 135;
    /** The tag. */
    private String TAG = LogUtils.makeLogTag(HomeActivity.class);
    /** The silent mode text view. */
    private TextView silentModeTextView;
    /** The device status image view. */
    private ImageView deviceStatusImageView;
    /** The phone status image view. */
    private ImageView phoneStatusImageView;
    /** The loading image view. */
    private ImageView loadingImageView;
    /** The footer linear layout. */
    private LinearLayout footerLinearLayout;
    /** The container one relative layout. */
    private RelativeLayout containerOneRelativeLayout;
    /** The loading relative layout. */
    private RelativeLayout loadingRelativeLayout;
    /** The containertwo linear layout. */
    private LinearLayout containertwoLinearLayout;
    /** The app on off toggle button. */
    private Button appOnOffToggleButton;
    /** The separator. */
    private View separator;
    /** The paired device count. */
    private ArrayList<HashMap<String, String>> pairedDeviceCount;
    /** The bluetooth le service. */
    private BluetoothLeService bluetoothLeService;
    /** The db helper. */
    private DatabaseHelper dbHelper;
    /** The alert dialog. */
    private Dialog alertDialog;
    /** The intent. */
    private Intent intent;
    /** The is available. */
    private boolean isAvailable = false;

    @Override
    protected void onStart() {
        super.onStart();
        String country = VALRTApplication.getPrefString(HomeActivity.this, VALRTApplication.PERSONAL_INFO_COUNTRY_CODE);
        if(!country.equalsIgnoreCase("US")){
            Log.e("hello","hello");
            VALRTApplication.setPrefString(HomeActivity.this,"MX","1");
        }
    }

    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // If alert in progress activity is alive start it
        if (VALRTApplication.isAlertInProgress) {
            intent = new Intent(this, AlertProgressActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            // If fall detect activity is alive start it
        } else if (VALRTApplication.isFallDetectInProgress) {
            intent = new Intent(this, FallDetectActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (VALRTApplication.isDeviceTrackInProgress) {
            intent = new Intent(this, DisconnectActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        dbHelper = new DatabaseHelper(this);
        containerOneRelativeLayout = (RelativeLayout) findViewById(R.id.home_container_one_layout);
        loadingRelativeLayout = (RelativeLayout) findViewById(R.id.home_loading_layout);
        containertwoLinearLayout = (LinearLayout) findViewById(R.id.home_container_two_layout);
        appOnOffToggleButton = (Button) findViewById(R.id.home_app_on_off_toggle_button);
        ((RelativeLayout) findViewById(R.id.home_mydevice_layout)).setOnClickListener(this);
        ((RelativeLayout) findViewById(R.id.home_mysettings_layout)).setOnClickListener(this);
        ((Button) findViewById(R.id.home_manage_devices_button)).setOnClickListener(this);
        ((Button) findViewById(R.id.home_about_vsn_button)).setOnClickListener(this);
        loadingImageView = (ImageView) findViewById(R.id.home_loading_imageview);
        footerLinearLayout = (LinearLayout) findViewById(R.id.home_footer_layout);
        separator = (View) findViewById(R.id.home_separator);
        silentModeTextView = (TextView) findViewById(R.id.home_silentmode_textview);
        deviceStatusImageView = (ImageView) findViewById(R.id.home_device_silent_imageview);
        phoneStatusImageView = (ImageView) findViewById(R.id.home_mobile_silent_imageview);
        // If Bluetooth Le service is not running start the service
        if (!Utils.isServiceRunning(this, BluetoothLeService.class.getName())) {
            dbHelper.updateConnectionStatus();
            // If paired device count is not equal to zero.
            if (dbHelper.getPairedDeviceCount() != 0 && VALRTApplication.getPrefBoolean(this, VALRTApplication.VALRT_SWITCH_OFF) == false) {
                VALRTApplication.isUpgraded = true;
                startService(new Intent(this, ReconnectService.class));
            }
        }
        //loading animation
        RotateAnimation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO}, MIC_PERMISSION_REQUEST_CODE);

        anim.setDuration(2000);
        loadingImageView.startAnimation(anim);
        //Toggle button action.
        appOnOffToggleButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (VALRTApplication.getPrefBoolean(HomeActivity.this, VALRTApplication.VALRT_SWITCH_OFF) == false) {
                    turnOFFApp();
                } else {
                    loadingRelativeLayout.setVisibility(View.VISIBLE);
                    Intent i = new Intent(HomeActivity.this, BluetoothLeService.class);
                    startService(i);
                    bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
                    isAvailable = true;
                    dbHelper.insertDeviceHistory(getString(R.string.valrt_turn_on));
                    loadingView();
                    appOnOffToggleButton.setBackground(getResources().getDrawable(R.drawable.shape_rounded_corner_gray_with_green_storke));
                    appOnOffToggleButton.setText(getString(R.string.toogle_on));
                    appOnOffToggleButton.setTextColor(getResources().getColor(R.color.violet_color));

                }
            }
        });
    }
    /**
     * On click.
     *
     * @param v the v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_mydevice_layout:
                // Check the paired connected device count
                pairedDeviceCount = dbHelper.getPairedConnectedDeviceList();
                if (pairedDeviceCount.size() != 0) {
                    // count is not equal to zero navigate to dash board
                    // fragment.
                    intent = new Intent(HomeActivity.this, DashboardFragmentActivity.class);
                    startActivity(intent);
                } else {
                    // count is equal to zero show the alert message to user.
                    alertDialogShow(getString(R.string.no_device_connected));
                }
                break;
            case R.id.home_mysettings_layout:
                // Navigate to settings screen.
                intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.home_manage_devices_button:
                // Navigate to manage devices screen.
                intent = new Intent(HomeActivity.this, ManageDevicesActivity.class);
                startActivity(intent);
                break;
            case R.id.home_about_vsn_button:
                // Navigate to help screen.
                intent = new Intent(HomeActivity.this, HelpActivity.class);
                startActivity(intent);
                break;
        }
    }
    /**
     * On resume.
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        // If no paired device found invisible the Toggle switch else show.
        if (dbHelper.getPairedDeviceCount() == 0) {
            appOnOffToggleButton.setVisibility(View.GONE);
        } else {
            appOnOffToggleButton.setVisibility(View.VISIBLE);
        }
        // To set the toggle switch on/off when open the screen.
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.VALRT_SWITCH_OFF)) {
            disableLayout();
            appOnOffToggleButton.setBackground(getResources().getDrawable(R.drawable.shape_rounded_corner_red));
            appOnOffToggleButton.setText(getString(R.string.toogle_off));
            appOnOffToggleButton.setTextColor(getResources().getColor(R.color.white_color));
        } else {
            appOnOffToggleButton.setBackground(getResources().getDrawable(R.drawable.shape_rounded_corner_gray_with_green_storke));
            appOnOffToggleButton.setText(getString(R.string.toogle_on));
            appOnOffToggleButton.setTextColor(getResources().getColor(R.color.violet_color));
        }

        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.VALRT_SWITCH_OFF) == false) {
            Intent i = new Intent(this, BluetoothLeService.class);
            startService(i);
            bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
            isAvailable = true;
        }
        // Silent mode status of puck configured in setting screen.
        if (!VALRTApplication.getPrefBoolean(this, VALRTApplication.DEVICESILENTCBX)) {
            // silent mode off
            deviceStatusImageView.setBackground(getResources().getDrawable(
                    R.drawable.img_valert_device_disable_checkbox));
        } else {
            // silent mode on
            deviceStatusImageView.setBackground(getResources().getDrawable(
                    R.drawable.img_valrt_device_enable_checkbox));
        }

        // Silent mode status of mobile device configured in setting screen.
        if (!VALRTApplication.getPrefBoolean(this, VALRTApplication.PHONESILENTCBX)) {
            // silent mode off
            phoneStatusImageView.setBackground(getResources().getDrawable(
                    R.drawable.img_phone_slient_disable_checkbox));
        } else {
            // silent mode on
            phoneStatusImageView.setBackground(getResources().getDrawable(
                    R.drawable.bg_phone_silent_enable_checkbox));
        }
        // If any one of the silent mode (puck / phone) visible the footer
        // layout else invisible it.
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.PHONESILENTCBX)
                || VALRTApplication.getPrefBoolean(this, VALRTApplication.DEVICESILENTCBX)) {
            footerLinearLayout.setVisibility(View.VISIBLE);
            silentModeTextView.setVisibility(View.VISIBLE);
            deviceStatusImageView.setVisibility(View.VISIBLE);
            phoneStatusImageView.setVisibility(View.VISIBLE);
            separator.setVisibility(View.VISIBLE);
        } else {
            footerLinearLayout.setVisibility(View.GONE);
            silentModeTextView.setVisibility(View.INVISIBLE);
            deviceStatusImageView.setVisibility(View.INVISIBLE);
            phoneStatusImageView.setVisibility(View.INVISIBLE);
            separator.setVisibility(View.INVISIBLE);
        }
    }
    /**
     * Turn off the whole Application functionality.
     */
    public void turnOFFApp() {
        final DeviceSilentTask task = new DeviceSilentTask();
        final Dialog switchOffDialog = new Dialog(this, R.style.ThemeWithCorners);
        switchOffDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        switchOffDialog.setContentView(R.layout.dialog_alert_yes_no);

        switchOffDialog.setCancelable(false);
        TextView switchOfftitleTextView = (TextView) switchOffDialog.findViewById(R.id.alert_title_textview);
        TextView switchOffMessagetextView = (TextView) switchOffDialog.findViewById(R.id.alert_content_textview);
        Button switchOffOnButton = (Button) switchOffDialog.findViewById(R.id.alert_yes_button);
        Button switchOffCancelButton = (Button) switchOffDialog.findViewById(R.id.alert_no_button);
        switchOfftitleTextView.setVisibility(View.GONE);
        switchOffOnButton.setText(getString(R.string.turn_off));
        switchOffCancelButton.setText(getString(R.string.cancel));
        switchOffMessagetextView.setText(getString(R.string.turn_off_alert_message));
        switchOffOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VALRTApplication.setPrefBoolean(HomeActivity.this, VALRTApplication.VALRT_SWITCH_OFF, true);
                appOnOffToggleButton.setBackground(getResources().getDrawable(R.drawable.shape_rounded_corner_red));
                appOnOffToggleButton.setText(getString(R.string.toogle_off));
                appOnOffToggleButton.setTextColor(getResources().getColor(R.color.white_color));
                disableLayout();
                dbHelper.insertDeviceHistory(getString(R.string.valrt_turn_off));
                task.execute();
                stopService(new Intent(HomeActivity.this, ReconnectService.class));
                stopService();
                switchOffDialog.dismiss();
            }
        });
        switchOffCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appOnOffToggleButton.setBackground(getResources().getDrawable(R.drawable.shape_rounded_corner_gray_with_green_storke));
                appOnOffToggleButton.setText(getString(R.string.toogle_on));
                appOnOffToggleButton.setTextColor(getResources().getColor(R.color.violet_color));
                switchOffDialog.dismiss();
            }
        });
        switchOffDialog.show();
    }
    /** The m service connection. */
    // To manage Service class life cycle.
    private ServiceConnection mServiceConnection = new ServiceConnection() {
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
     * The Class DeviceSilentTask.
     */
    private class DeviceSilentTask extends AsyncTask<String, Void, Void> {

        /**
         * Do in background.
         *
         * @param arg0 the arg0
         * @return the void
         */
        @Override
        protected Void doInBackground(String... arg0) {
            try {
                bluetoothLeService.silentAllDevice(true);
            } catch (Exception e) {
                LogUtils.LOGE(TAG, "writing for silent mode", e);
            }
            return null;
        }
    }
    /**
     * Disable layout.
     */
    //To disable the home layout.
    void disableLayout() {
        containerOneRelativeLayout.setAlpha((float) 0.2);
        containertwoLinearLayout.setAlpha((float) 0.2);
        footerLinearLayout.setAlpha((float) 0.2);
        Utils.enableDisableView(containerOneRelativeLayout, false);
        Utils.enableDisableView(containertwoLinearLayout, false);
        Utils.enableDisableView(footerLinearLayout, false);

    }
    /**
     * Enable layout.
     */
    //To enable the previously disabled layout.
    void enableLayout() {
        containerOneRelativeLayout.setAlpha(1);
        containertwoLinearLayout.setAlpha(1);
        footerLinearLayout.setAlpha(1);
        Utils.enableDisableView(containerOneRelativeLayout, true);
        Utils.enableDisableView(containertwoLinearLayout, true);
        Utils.enableDisableView(footerLinearLayout, true);
    }
    /**
     * Stop service.
     */
    //Stop background service
    public void stopService() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (bluetoothLeService != null) {
                    bluetoothLeService.disConnectAllDevice();
                    bluetoothLeService.stopSelf();
                }
                Utils.cancelNotify(HomeActivity.this, VALRTApplication.BLUETOOTH_CONNECT_DISCONNECT_NOTIFY_ID);
                dbHelper.updateConnectionStatus();
            }
            // Wait 4 seconds to put the device is silent.
        }, 4000);
    }
    /**
     * Loading view.
     */
    // To show the loading view for 2 seconds when enable the toggle switch on.
    public void loadingView() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                VALRTApplication.setPrefBoolean(HomeActivity.this, VALRTApplication.VALRT_SWITCH_OFF, false);
                appOnOffToggleButton.setBackground(getResources().getDrawable(R.drawable.shape_rounded_corner_gray_with_green_storke));
                appOnOffToggleButton.setTextColor(getResources().getColor(R.color.violet_color));
                appOnOffToggleButton.setText(getString(R.string.toogle_on));
                loadingRelativeLayout.setVisibility(View.GONE);
                enableLayout();
            }
            // wait 5 seconds to show loading spinner
        }, 5000);
    }
    /**
     * To show the alert info to the user.
     *
     * @param message the message
     */
    public void alertDialogShow(String message) {
        alertDialog = new Dialog(this, R.style.ThemeWithCorners);
        alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_info);

        alertDialog.setCancelable(false);
        TextView messageTextView = (TextView) alertDialog.findViewById(R.id.info_title_textview);
        Button okButton = (Button) alertDialog.findViewById(R.id.info_ok_button);
        messageTextView.setText(message);
        alertDialog.show();
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
    /**
     * On back pressed.
     */
    @Override
    public void onBackPressed() {
        // finish the all the activity and exit the APP.
        this.finishAffinity();
        super.onBackPressed();
    }
    /**
     * On pause.
     */
    @Override
    public void onPause() {
        super.onPause();
        unBindService();
        //unregister the broadcast receiver.
        unregisterReceiver(mGattUpdateReceiver);
    }
    /**
     * On destroy.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        unBindService();
    }
    /**
     * To un-bind the service object from the activity.
     */
    void unBindService() {
        if (isAvailable == true) {
            if (mServiceConnection != null)
                unbindService(mServiceConnection);
            isAvailable = false;
        }
    }
    /** The m gatt update receiver. */
    // Broadcast receiver to receive the update from the Bluetooth le service class.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            //puck is connected.
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
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
        return intentFilter;
    }
}