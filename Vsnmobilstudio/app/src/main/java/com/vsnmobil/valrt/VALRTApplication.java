package com.vsnmobil.valrt;


import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.util.Calendar;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.vsnmobil.valrt.services.BluetoothLeService;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * VALRTApplication.java
 *
 * This class contains all the constants which are used thought out the application.
 * like preference constants, database constants, web service URL and local notification function
 * to access static from all the class of application.
 */
public class VALRTApplication extends Application {
    private FirebaseAnalytics mFirebaseAnalytics;
    /** The context. */
    private Context context;
    /** The Constant BLUETOOTH_NOTIFY_ID. */
    // Notification ID Constants.
    public static final int BLUETOOTH_NOTIFY_ID = 12001;
    /** The Constant LOCATION_NOTIFY_ID. */
    public static final int LOCATION_NOTIFY_ID = 12002;
    /** The Constant CONNECT_DISCONNECT_NOTIFY_ID. */
    public static final int CONNECT_DISCONNECT_NOTIFY_ID = 12003;
    /** The Constant DATA_CONNECTION_NOTIFY_ID. */
    public static final int DATA_CONNECTION_NOTIFY_ID = 12004;
    /** The Constant ALERTINPROGRESS_NOTIFY_ID. */
    public static final int ALERTINPROGRESS_NOTIFY_ID = 12005;
    /** The Constant FALL_DETECT_NOTIFY_ID. */
    public static final int FALL_DETECT_NOTIFY_ID = 12006;
    /** The Constant BLUETOOTHLESERVICE_NOTIFY_ID. */
    public static final int BLUETOOTHLESERVICE_NOTIFY_ID = 12007;
    /** The Constant BLUETOOTH_CONNECT_DISCONNECT_NOTIFY_ID. */
    public static final int BLUETOOTH_CONNECT_DISCONNECT_NOTIFY_ID = 12009;
    /** The Constant TRACKER_CONNECT_DISCONNECT_NOTIFY_ID. */
    //public static final int UPGRADE_NOTIFY_ID = 12010;
    public static final int TRACKER_CONNECT_DISCONNECT_NOTIFY_ID = 12011;
    /** The is alert in progress. */
    // Alert in progress flag.
    static public boolean isAlertInProgress= false;
    /** The is fall detect in progress. */
    static public boolean isFallDetectInProgress= false;
    /** The is device track in progress. */
    static public boolean isDeviceTrackInProgress= false;
    /** The is forget me clicked. */
    static public boolean isForgetMeClicked = false;
    /** The is upgraded. */
    static public boolean isUpgraded = false;
    /** The is scan activity running. */
    // Blue tooth LE scanner flag.
    static public boolean isScanActivityRunning= false;
    /** The Constant EXTRA_BLUETOOTH_DEVICE_ADDRESS. */
    //  Preference Constants.
    public static final String EXTRA_BLUETOOTH_DEVICE_ADDRESS = "BT_Address";
    /** The Constant EXTRA_BLUETOOTH_DEVICE_NAME. */
    public static final String EXTRA_BLUETOOTH_DEVICE_NAME = "BT_Name";
    /** The Constant DATABASE_NAME. */
    // Database Constants
    public static final String DATABASE_NAME = "valert.db";
    /** The Constant DATABASE_VERSION. */
    public static final int DATABASE_VERSION = 1;
    /** The Constant PREFERENCEVARIABLE. */
    //Preference Constants
    public static final String PREFERENCEVARIABLE = "valertpref";
    /** The device table. */
    // Device table Constants
    public static String DEVICE_TABLE = "device_table";
    /** The device address. */
    public static String DEVICE_ADDRESS = "device_address";
    /** The device name. */
    public static String DEVICE_NAME = "device_name";
    /** The device status. */
    public static String DEVICE_STATUS = "device_status";
    /** The battery status. */
    public static String BATTERY_STATUS = "battery_status";
    /** The falldetection status. */
    public static String FALLDETECTION_STATUS = "falldetection_status";
    /** The device software version. */
    public static String DEVICE_SOFTWARE_VERSION = "device_software_version";
    /** The device serial number. */
    public static String DEVICE_SERIAL_NUMBER = "device_serial_number";
    /** The Constant HISTORY_LOG_TABLE. */
    // History Log table Constants
    public static final String HISTORY_LOG_TABLE = "history_log";
    /** The history log status. */
    public static String HISTORY_LOG_STATUS = "history_log_status";
    /** The history log count. */
    public static int HISTORY_LOG_COUNT = 0;
    /** The alertmsg. */
    // V.ALRT Settings screen preference Constants.
    static public String ALERTMSG = "alertmsg";
    /** The alerttonename. */
    static public String ALERTTONENAME = "alerttonename";
    /** The alerttonepath. */
    static public String ALERTTONEPATH = "alerttonepath";
    /** The panictonecbx. */
    static public String PANICTONECBX = "panictonecbx";
    /** The C1 ccbx. */
    static public String C1CCBX = "c1callcbx"; // First contact call check box
    /** The C2 ccbx. */
    static public String C2CCBX = "c2callcbx"; // Second contact call check box
    /** The C3 ccbx. */
    static public String C3CCBX = "c3callcbx"; // Third contact call check box
    /** The C1 tcbx. */
    static public String C1TCBX = "cltextcbx"; // First contact message check box
    /** The C2 tcbx. */
    static public String C2TCBX = "c2textcbx"; // Second contact message check box
    /** The C3 tcbx. */
    static public String C3TCBX = "c3textcbx"; // Third contact message check box
    /** The devicesilentcbx. */
    static public String DEVICESILENTCBX = "devicesilentcbx"; //  V.ALRT device silent check box
    /** The phonesilentcbx. */
    static public String PHONESILENTCBX = "phonesilentcbx";   // V.ALRT Application silent check box
    /** The contactonename. */
    static public String CONTACTONENAME = "conename"; // first contact name.
    /** The contacttwoname. */
    static public String CONTACTTWONAME = "ctwoname"; // second contact name
    /** The contactthreename. */
    static public String CONTACTTHREENAME = "cthreename"; //third contact name
    /** The contactonenumber. */
    static public String CONTACTONENUMBER = "conenumber"; //first contact number
    /** The contacttwonumber. */
    static public String CONTACTTWONUMBER = "ctwonumber"; //second contact number
    /** The contactthreenumber. */
    static public String CONTACTTHREENUMBER = "cthreenumber"; //third contact name
    /** The termsncondition. */
    //terms and conditions preference Constants.
    static public String TERMSNCONDITION = "termsncondition";
    /** The congratulation. */
    static public String CONGRATULATION = "congratulation";
    /** The agreement. */
    static public String AGREEMENT = "agreement";
    /** The personal info name. */
    //Personal information preference Constants.
    static public String PERSONAL_INFO_NAME="personalinfoname";
    /** The personal info phone. */
    static public String PERSONAL_INFO_PHONE="personalinfophone";
    /** The personal info country code. */
    static public String PERSONAL_INFO_COUNTRY_CODE = "personalinfo_country_code";
    /** The country code selected. */
    static public String COUNTRY_CODE_SELECTED = "country_code_selected";
    /** The valrt switch off. */
    //V.LART Switch on /off
    static public String VALRT_SWITCH_OFF = "valrt_switch_off";
    /** The device tracker alert tone status. */
    //Device tracker constants.
    static public String DEVICE_TRACKER_ALERT_TONE_STATUS = "device_track_alert_tone"; // device tracker alert tone
    /** The device tracker vibration status. */
    static public String DEVICE_TRACKER_VIBRATION_STATUS = "device_track_vibration";  // device tracker vibration status
    /** The valrt status. */
    // Alert in progress functionality constants.
    static public String VALRT_STATUS = "valrt_status";
    /** The tabposition. */
    static public String TABPOSITION = "tabposition";
    /** The notification id. */
    static public String NOTIFICATION_ID = "notification_id";
    /** The finemeheading. */
    static public String FINEMEHEADING = "findmeheading";
    /** The intent device address. */
    static public String INTENT_DEVICE_ADDRESS = "intent_device_address";
    /** The weblink. */
    static public String WEBLINK = "weblink";
    /** The connected. */
    // Connection status constants
    static public String CONNECTED = "2";
    /** The connecting. */
    static public String CONNECTING = "1";
    /** The disconnected. */
    static public String DISCONNECTED = "0";
    /** The tag. */
    public static final String TAG = "tag";
    /**
     * Web service API production URL.
     */
    //SMS
    public static final String VOIP_SMS_WEBSERVICE_URL = "https://example.com";
    /** The Constant VOIP_CALL_WEBSERVICE_URL. */
    //VOIP Call Access token.
    public static final String VOIP_CALL_WEBSERVICE_URL = "https://example.com";
    /* (non-Javadoc)
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        context = getApplicationContext();
    }
    /**
     * To store the string values in the preference.
     *
     * @param context the context
     * @param key the key
     * @param value the value
     */
    public static void setPrefString(Context context,String key, String value) {
        SharedPreferences sp =  context.getSharedPreferences(PREFERENCEVARIABLE, MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }
    /**
     * To retrieve the string values in the preference using key.
     *
     * @param context the context
     * @param key the key
     * @return String value of the corresponding key passed as parameter.
     */
    public static String getPrefString(Context context,String key) {
        SharedPreferences sp =  context.getSharedPreferences(PREFERENCEVARIABLE, MODE_PRIVATE);
        return sp.getString(key, "");
    }
    /**
     * To store the boolean values in the preference.
     *
     * @param context the context
     * @param key the key
     * @param value the value
     */
    public static void setPrefBoolean(Context context,String key, boolean value) {
        SharedPreferences sp =  context.getSharedPreferences(PREFERENCEVARIABLE, MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }
    /**
     * To retrieve the boolean values in the preference using key.
     *
     * @param context the context
     * @param key the key
     * @return boolean value of the corresponding key passed as parameter.
     */
    public static boolean getPrefBoolean(Context context,String key) {
        SharedPreferences sp =  context.getSharedPreferences(PREFERENCEVARIABLE, MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }
    /**
     * To get the Blue tooth Manager instance.
     *
     * @param context the context
     * @return BluetoothManager.
     */
    public static BluetoothManager getManager(Context context) {
        return (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }
    /**
     * It will hit the BluetoothLeService every 15 minutes to make user that the service is running.
     */
    public void pingService() {
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(context, BluetoothLeService.class);
        PendingIntent pintent = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Start every 15 minutes
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),900000,pintent);
    }
    /**
     * If user cleared the recent task. This method will be called.It will start the BluetoothLeService
     * service class.
     *
     * @param context the context
     */
    public static  void startService(Context context) {
        Intent intent = new Intent(context, BluetoothLeService.class);
        PendingIntent pintent = PendingIntent.getService(context, 0, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 30);
        AlarmManager alarm = (AlarmManager)context. getSystemService(Context.ALARM_SERVICE);
        // Start after 30 seconds
        alarm.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pintent);
    }
}
