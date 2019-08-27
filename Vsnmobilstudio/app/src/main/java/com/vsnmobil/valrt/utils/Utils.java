package com.vsnmobil.valrt.utils;

import java.text.DecimalFormat;
//import
import java.text.SimpleDateFormat;
//import
import java.util.Date;
//import
import java.util.List;
//import
import java.util.Locale;
//import
import java.util.regex.Matcher;
//import
import java.util.regex.Pattern;
//import
import org.apache.http.HttpResponse;
//import
import org.apache.http.NameValuePair;
//import
import org.apache.http.StatusLine;
//import
import org.apache.http.client.HttpClient;
//import
import org.apache.http.client.entity.UrlEncodedFormEntity;
//import
import org.apache.http.client.methods.HttpPost;
//import
import org.apache.http.impl.client.DefaultHttpClient;
//import
import org.apache.http.message.BasicNameValuePair;
//import
import android.app.Activity;
//import
import android.app.ActivityManager;
//import
import android.app.ActivityManager.RunningServiceInfo;
//import
import android.app.NotificationManager;
//import
import android.bluetooth.BluetoothAdapter;
//import
import android.bluetooth.BluetoothManager;
//import
import android.content.Context;
//import
import android.content.pm.PackageManager;
//import
import android.location.LocationManager;
//import
import android.media.AudioManager;
//import
import android.media.RingtoneManager;
//import
import android.net.ConnectivityManager;
//import
import android.net.NetworkInfo;
//import
import android.net.Uri;
//import
import android.os.Build;
//import
import android.provider.Settings;
//import
import android.text.TextUtils;
//import
import android.util.DisplayMetrics;
//import
import android.view.View;
//import
import android.view.ViewGroup;
//import
import android.widget.Toast;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
/**
 * Utils.java
 * <p/>
 * This class contains the Utils methods like check Internet connection, GPS
 * status, current time stamp etc.
 */
public class Utils {
    /** The Constant TAG. */
    private final static String TAG = Utils.class.getSimpleName();
    /**
     * To check the Internet connection is there are not.
     *
     * @param context the context
     * @return boolean Internet connection is there are not.
     */
    public static boolean isNetConnected(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = conMgr.getAllNetworkInfo();
        // Checking for all possible Internet providers
        if (info == null)
        {
            LogUtils.LOGE(TAG,"Null getAllNetworkInfo");
            return false;
        }
        for (NetworkInfo anInfo : info)
        {
            LogUtils.LOGE(TAG, "Checking Network: " + anInfo.getDetailedState().toString());
            if (anInfo.getState() == NetworkInfo.State.CONNECTED)
                return true;
        }

        return false;
    }
    /**
     * To check the GPS is enabled or not.
     *
     * @param context the context
     * @return boolean GPS is on or not
     */
    public static boolean isGPSon(Context context) {
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
    }
    /**
     * To get the current date and time stamp.
     *
     * @return string current date with the time.
     */
    public static String currentTimeStamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd hh:mm:ss a", Locale.getDefault());
        String date = dateFormat.format(new Date());
        return date;
    }
    /**
     * To post the data to the server.
     *
     * @param urlParameters the url parameters
     * @return int result code of the request.
     */
    public static int serverRequest(List<BasicNameValuePair> urlParameters) {
        int resultCode = 0;
        try {
            // Initialize HttpClient and HttpPost
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(
                    VALRTApplication.VOIP_SMS_WEBSERVICE_URL);
            httppost.setEntity(new UrlEncodedFormEntity(
                    (List<? extends NameValuePair>) urlParameters));
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            StatusLine statusLine = response.getStatusLine();
            resultCode = statusLine.getStatusCode();
        } catch (Exception e) {
        }
        return resultCode;
    }
    /**
     * Checks whether given service is running.
     *
     * @param context the context
     * @param serviceName the service name
     * @return boolean value running return true. Not running return false.
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    /**
     * To validate the phone number with country code.
     *
     * @param context the context
     * @param phoneNumber the phone number
     * @return phone number is valid or not
     */
    public static boolean validateNumber(Context context, String phoneNumber) {
        String countryCode = VALRTApplication.getPrefString(context,
                VALRTApplication.PERSONAL_INFO_COUNTRY_CODE);
        // United states.
        if (countryCode.equalsIgnoreCase("US")) {
            String replacedNumber;
            if (phoneNumber.length() >= 10 && validCellPhone(phoneNumber) == true) {
                replacedNumber = phoneNumber.replaceAll("\\+", "");
                return (replacedNumber.length() >= 10);
            } else {
                return false;
            }
        }
        // if selected country is Mexico
        if (countryCode.equalsIgnoreCase("MX") && phoneNumber.length() >= 10) {
            // First digit must be +
            if (phoneNumber.substring(0, 1).equals("+")
                    && validCellPhone(phoneNumber) == true) {
                return true;
            } else
                return false;
        }
        // if selected country is India
        if (countryCode.equalsIgnoreCase("IN") && phoneNumber.length() >= 12) {
            // First three digit must be +91
            if (phoneNumber.substring(0, 3).equals("+91")
                    && validCellPhone(phoneNumber) == true) {
                return true;
            } else
                return false;
        }
        // if selected country is Brazil
        if (countryCode.equalsIgnoreCase("BR") && phoneNumber.length() >= 12) {
            // First three digit must be +55
            if (phoneNumber.substring(0, 3).equals("+55")
                    && validCellPhone(phoneNumber) == true) {
                return true;
            } else
                return false;
        }
        return false;
    }
    /**
     * To validate the phone number.
     *
     * @param number the number
     * @return phone number is valid or not
     */
    public static boolean validCellPhone(String number) {
        Pattern pattern = Pattern.compile("^(?:[1-9-+]\\d*)(?:\\.\\d*)?$");
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }
    /**
     * To cancel the already generated notification.
     *
     * @param context      of the class.
     * @param id the id
     */
    public static void cancelNotify(Context context, int id) {
        try {
            NotificationManager nMgr = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancel(id);
        } catch (Exception e) {
            LogUtils.LOGI(TAG, e.getMessage());
        }
    }
    /**
     * To show the toast message.
     *
     * @param context the context
     * @param message the message
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
    /**
     * To check the tracker option (alert tone/vibration) is enabled /disabled.
     *
     * @param context the context
     * @return status of the tracker enabled / disabled.
     */
    public static boolean isTrackerEnabled(Context context) {
        if (VALRTApplication.getPrefBoolean(context,
                VALRTApplication.DEVICE_TRACKER_ALERT_TONE_STATUS)
                || VALRTApplication.getPrefBoolean(context,
                VALRTApplication.DEVICE_TRACKER_VIBRATION_STATUS)) {
            return true;
        }
        return false;
    }
    /**
     * To check the Airplane mode is enabled /disabled.
     *
     * @param context the context
     * @return status of the airplane mode enabled / disabled.
     */
    public static boolean isAirplaneModeEnabled(Context context) {
        if ((Settings.Global.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) == 0)) {
            return true;
        }
        return false;
    }
    /**
     * To check the device bluetooth is enabled or not.
     *
     * @param context the context
     * @return boolean Bluetooth is enabled / disabled.
     */
    public static boolean isBluetoothEnabled(Context context) {
        BluetoothManager bluetoothManager = (BluetoothManager) context
                .getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        return bluetoothAdapter.isEnabled();
    }
    /**
     * To check the device have the Bluetooth low energy support.
     *
     * @param context the context
     * @return boolean value of the corresponding key passed as parameter.
     */
    public static boolean isBLESupported(Context context) {
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE);
    }
    /**
     * To check the phone is in silent mode or not.
     *
     * @param context the context
     * @return boolean value silent or not.
     */
    public static boolean isSilentMode(Context context) {
        AudioManager am = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        if (am.getRingerMode() == AudioManager.RINGER_MODE_SILENT
                || am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)
            return true;
        else
            return false;
    }
    /**
     * To get alert tone uri based on the user preference.
     *
     * @param context of the application
     * @return Uri of the alert tone
     */
    public static Uri getAlertToneUri(Context context) {
        String path = VALRTApplication.getPrefString(context,
                VALRTApplication.ALERTTONEPATH);
        if (VALRTApplication.getPrefBoolean(context,
                VALRTApplication.PANICTONECBX))
            return Uri.parse("android.resource://" + context.getPackageName()
                    + "/" + R.raw.siren);
        if (TextUtils.isEmpty(path)) {
            return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        } else {
            return Uri.parse(path);
        }
    }
    /**
     * Enable/Disable the layout including it's child views.
     *
     * @param view the view
     * @param enabled the enabled
     */
    public static void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int idx = 0; idx < group.getChildCount(); idx++) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }
    /**
     * To fetch the screen size mobile device.
     *
     * @param activity the activity
     * @return int screen size value.
     */
    public static int getScreenSize(Activity activity) {
        DisplayMetrics met = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(met);// get
        // display
        // metrics
        // object
        String screenSize = new DecimalFormat("##.##")
                .format(Math
                        .sqrt(((met.widthPixels / met.xdpi) * (met.widthPixels / met.xdpi))
                                + ((met.heightPixels / met.ydpi) * (met.heightPixels / met.ydpi))));
        screenSize = screenSize.substring(0, 1);
        return Integer.parseInt(screenSize);
    }
    /**
     * Checks whether the scanned device is VSN device or not.
     *
     * @param deviceName the device name
     * @param deviceAddress the device address
     * @return boolean value VSN device or not.
     */
    public static boolean isVSNDevice(String deviceName, String deviceAddress) {
        if (deviceName != null && deviceAddress != null) {
            if (deviceName.equals("V.ALRT " + deviceAddress.substring(9))
                    || deviceName.startsWith("V-Alert")
                    || deviceName.startsWith("VALRT")
                    || deviceName.startsWith("Valert"))
                return true;
            else
                return false;
        }
        return false;
    }
    /**
     * To check the mobile mode accept the Adjust connection control value.
     *
     * @return boolean True if Adjust connection control value is acceptable
     * else false.
     */
    public static boolean isAdjustControlAcceptable() {
        // To check the Samsung S3 device.
        if (Build.MODEL.equalsIgnoreCase("SAMSUNG-SGH-I747"))
            return false;
        else
            return true;
    }
}
