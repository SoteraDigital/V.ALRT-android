package com.vsnmobil.valrt.activities;

import java.util.ArrayList;
//import
import java.util.List;
//import
import java.util.Timer;
//import
import java.util.TimerTask;
//import
import org.apache.http.message.BasicNameValuePair;
//import
import android.app.ActionBar.LayoutParams;
//import
import android.app.Activity;
//import
import android.bluetooth.BluetoothGatt;
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
import android.content.IntentSender;
//import
import android.content.ServiceConnection;
//import
import android.location.Location;
//import
import android.os.AsyncTask;
//import
import android.os.Bundle;
//import
import android.os.Handler;
//import
import android.os.IBinder;
//import
import android.text.TextUtils;
//import
import android.util.Log;
//import
import android.view.View;
//import
import android.view.WindowManager;
//import
import android.widget.Button;
//import
import android.widget.ImageView;
//import
import android.widget.LinearLayout;
//import
import android.widget.TextView;
import android.widget.Toast;
//import
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
//import
import com.google.android.gms.common.GooglePlayServicesUtil;
//import
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
//import
import com.google.android.gms.location.LocationRequest;
//import
import com.google.android.gms.location.LocationServices;
import com.vsnmobil.valrt.GattConstant;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
//import
import com.vsnmobil.valrt.VALRTReceiver;
//import
import com.vsnmobil.valrt.services.BluetoothLeService;
//import
import com.vsnmobil.valrt.storage.DatabaseHelper;
//import
import com.vsnmobil.valrt.utils.LogUtils;
import com.vsnmobil.valrt.utils.NotificationUtils;
//import
import com.vsnmobil.valrt.utils.PlaySound;
//import
import com.vsnmobil.valrt.utils.Utils;
//import
import com.vsnmobil.valrt.voip.CallQueueExecutor;
//import
import com.vsnmobil.valrt.voip.VoipPhone;

import io.fabric.sdk.android.services.common.Crash;

/**
 * AlertProgressActivity.java If any emergency event like Emergency key press /
 * fall detect occur. The activity will be triggered It sent out the emergency
 * message and VOIP call to the selected contacts. As soon as this activity
 * trigger it will wait for the accurate location data and sent out the second
 * SMS.
 */
public class AlertProgressActivity extends Activity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    private final static String TAG = VALRTReceiver.class.getSimpleName();
    /** The alert progress gatt. */
    public static BluetoothGatt alertProgressGatt = null;
    /** The bluetooth le service. */
    private BluetoothLeService bluetoothLeService;
    /** The call queue executor. */
    private CallQueueExecutor callQueueExecutor;
    /** The location request. */
    private LocationRequest locationRequest;
    /** The location client. */
    private GoogleApiClient locationClient;
    /** The db helper. */
    private DatabaseHelper dbHelper;
    /** The first message layout. */
    private LinearLayout firstMessageLayout;
    /** The second messgae layout. */
    private LinearLayout secondMessgaeLayout;
    /** The third message layout. */
    private LinearLayout thirdMessageLayout;
    /** The first call layout. */
    private LinearLayout firstCallLayout;
    /** The second call layout. */
    private LinearLayout secondCallLayout;
    /** The third call layout. */
    private LinearLayout thirdCallLayout;
    /** The first message number text view. */
    private TextView firstMessageNumberTextView;
    /** The second message number text view. */
    private TextView secondMessageNumberTextView;
    /** The third message number text view. */
    private TextView thirdMessageNumberTextView;
    /** The first call number text view. */
    private TextView firstCallNumberTextView;
    /** The second call number text view. */
    private TextView secondCallNumberTextView;
    /** The third call number text view. */
    private TextView thirdCallNumberTextView;
    /** The first call status text view. */
    private TextView firstCallStatusTextView;
    /** The second call status text view. */
    private TextView secondCallStatusTextView;
    /** The third call status text view. */
    private TextView thirdCallStatusTextView;
    /** The no contacts text view. */
    private TextView noContactsTextView;
    /** The title text view. */
    private TextView titleTextView;
    /** The first call status image view. */
    private ImageView firstCallStatusImageView;
    /** The second call status image view. */
    private ImageView secondCallStatusImageView;
    /** The third call status image view. */
    private ImageView thirdCallStatusImageView;
    /** The first message status image view. */
    private ImageView firstMessageStatusImageView;
    /** The second message status image view. */
    private ImageView secondMessageStatusImageView;
    /** The third message status image view. */
    private ImageView thirdMessageStatusImageView;
    /** The cancel all button. */
    private Button cancelAllButton;
    /** The message. */
    private String message;
    /** The message type. */
    private String messageType;
    /** The device address. */
    private String deviceAddress;
    /** The device serial number. */
    private String deviceSerialNumber;
    /** The phone number list. */
    private List<String> phoneNumberList;
    /** The play sound. */
    private PlaySound playSound;
    /** The call count. */
    private int callCount = 1;
    /** The result status. */
    private int resultStatus = 1;
    /** The contacts count. */
    private int contactsCount = 1;
    // Define a request code to send to Google Play services. This code is
    /** The Constant CONNECTION_FAILURE_RESOLUTION_REQUEST. */
    // returned in Activity.onActivityResult
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    /** The milliseconds per second. */
    // Milliseconds per second
    int MILLISECONDS_PER_SECOND = 1000;
    /** The update interval in seconds. */
    // Update frequency in seconds
    int UPDATE_INTERVAL_IN_SECONDS = 5;
    /** The update interval. */
    // Update frequency in milliseconds
    long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    /** The fastest interval in seconds. */
    // The fastest update frequency, in seconds
    int FASTEST_INTERVAL_IN_SECONDS = 5;
    /** The fastest interval. */
    // A fast frequency ceiling in milliseconds
    long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    /** The intent. */
    Intent intent = getIntent();
    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertprogress);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        // Keeps Screen On while Alert in Progress
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setFinishOnTouchOutside(false);
        titleTextView = (TextView) findViewById(R.id.alertinprogress_title_textview);
        // SMS Layout
        firstMessageLayout = (LinearLayout) findViewById(R.id.alertinprogress_first_sms_layout);
        secondMessgaeLayout = (LinearLayout) findViewById(R.id.alertinprogress_second_sms_layout);
        thirdMessageLayout = (LinearLayout) findViewById(R.id.alertinprogress_third_sms_layout);
        // CALL Layout
        firstCallLayout = (LinearLayout) findViewById(R.id.alertinprogress_first_call_layout);
        secondCallLayout = (LinearLayout) findViewById(R.id.alertinprogress_second_call_layout);
        thirdCallLayout = (LinearLayout) findViewById(R.id.alertinprogress_third_call_layout);
        // CALL Status TextView
        firstCallStatusImageView = (ImageView) findViewById(R.id.alertinprogress_first_call_imageview);
        secondCallStatusImageView = (ImageView) findViewById(R.id.alertinprogress_second_call_imageview);
        thirdCallStatusImageView = (ImageView) findViewById(R.id.alertinprogress_third_call_imageview);
        // SMS Status ImageView
        firstMessageStatusImageView = (ImageView) findViewById(R.id.alertinprogress_first_sms_imageview);
        secondMessageStatusImageView = (ImageView) findViewById(R.id.alertinprogress_second_sms_imageview);
        thirdMessageStatusImageView = (ImageView) findViewById(R.id.alertinprogress_third_sms_imageview);
        firstMessageNumberTextView = (TextView) findViewById(R.id.alertinprogress_first_sms_textview);
        secondMessageNumberTextView = (TextView) findViewById(R.id.alertinprogress_second_sms_textview);
        thirdMessageNumberTextView = (TextView) findViewById(R.id.alertinprogress_third_sms_textview);
        firstCallNumberTextView = (TextView) findViewById(R.id.alertinprogress_first_call_number_textview);
        secondCallNumberTextView = (TextView) findViewById(R.id.alertinprogress_second_call_number_textview);
        thirdCallNumberTextView = (TextView) findViewById(R.id.alertinprogress_third_call_number_textview);
        firstCallStatusTextView = (TextView) findViewById(R.id.alertinprogress_first_call_textview);
        secondCallStatusTextView = (TextView) findViewById(R.id.alertinprogress_second_call_textview);
        thirdCallStatusTextView = (TextView) findViewById(R.id.alertinprogress_third_call_textview);
        cancelAllButton = (Button) findViewById(R.id.alertinprogress_cancel_all_button);
        noContactsTextView = (TextView) findViewById(R.id.alertinprogress_no_contacts_textview);
        VALRTApplication.isAlertInProgress = true;
        dbHelper = new DatabaseHelper(this);
        deviceAddress = alertProgressGatt.getDevice().getAddress();
        deviceSerialNumber = dbHelper.getDeviceSerial(deviceAddress);
        Intent i = new Intent(this, BluetoothLeService.class);
        startService(i);
        bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
        phoneNumberList = new ArrayList<String>();
        // Add the selected contact numbers in a list.
        // To make SMS for contact one
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.C1TCBX) == true) {
            String c1no = VALRTApplication.getPrefString(this, VALRTApplication.CONTACTONENUMBER);
            phoneNumberList.add(c1no);
        }
        // To make SMS for contact two
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.C2TCBX) == true) {
            String c2no = VALRTApplication.getPrefString(this, VALRTApplication.CONTACTTWONUMBER);
            phoneNumberList.add(c2no);
        }
        // To make SMS for contact two
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.C3TCBX) == true) {
            String c3no = VALRTApplication.getPrefString(this, VALRTApplication.CONTACTTHREENUMBER);
            phoneNumberList.add(c3no);
        }
        messageType = getString(R.string.valrt_emergency);
        intent = this.getIntent();
        if (intent.hasExtra(VALRTApplication.VALRT_STATUS)) {
            if (intent.getStringExtra(VALRTApplication.VALRT_STATUS).equals("FALL"))
                messageType = getString(R.string.valrt_fall);
        }
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.PHONESILENTCBX) == false && Utils.isSilentMode(this) == false) {
            playSound = new PlaySound(this, false);
            playSound.start();
        }
        // Start the location listener
        locationRequest = LocationRequest.create();
        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        locationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        alertProgress();
        // Register the Broadcast Receiver to get status of VOIP call
        IntentFilter filter = new IntentFilter();
        filter.addAction(CallQueueExecutor.ACTION_CALL_CONNECTED);
        filter.addAction(CallQueueExecutor.ACTION_CALL_DISCONNECTED);
        filter.addAction(CallQueueExecutor.ACTION_CALL_QUEUE_COMPLETED);
        filter.addAction(VALRTReceiver.ACTION_NETWORK_STATE_CHANGED);
        registerReceiver(voipCallStatusReceiver, filter);
        // Generate notification in notification bar and play alert tone.
        NotificationUtils.postNotification(this, getString(R.string.sending_sms_to_contact), VALRTApplication.ALERTINPROGRESS_NOTIFY_ID);
        // Checks Network Connection to Trigger Call and SMS
        if (!Utils.isNetConnected(this))
        {
            firstMessageStatusImageView.setImageResource(R.drawable.ic_call_failed);
            firstMessageStatusImageView.setVisibility(View.VISIBLE);

            secondMessageStatusImageView.setImageResource(R.drawable.ic_call_failed);
            secondMessageStatusImageView.setVisibility(View.VISIBLE);

            thirdMessageStatusImageView.setImageResource(R.drawable.ic_call_failed);
            thirdMessageStatusImageView.setVisibility(View.VISIBLE);

            firstCallStatusImageView.setImageResource(R.drawable.ic_call_failed);
            firstCallStatusImageView.setVisibility(View.VISIBLE);

            secondCallStatusImageView.setImageResource(R.drawable.ic_call_failed);
            secondCallStatusImageView.setVisibility(View.VISIBLE);

            thirdCallStatusImageView.setImageResource(R.drawable.ic_call_failed);
            thirdCallStatusImageView.setVisibility(View.VISIBLE);

            firstCallStatusTextView.setText(getString(R.string.network_error));
            secondCallStatusTextView.setText(getString(R.string.network_error));
            thirdCallStatusTextView.setText(getString(R.string.network_error));

            cancelAllButton.setText(getString(R.string.done_findme));
        } else {

            if (VALRTApplication.getPrefBoolean(this, VALRTApplication.C1CCBX) == true
                    || VALRTApplication.getPrefBoolean(this, VALRTApplication.C2CCBX) == true
                    || VALRTApplication.getPrefBoolean(this, VALRTApplication.C3CCBX) == true) {
                callQueueExecutor = new CallQueueExecutor(getApplicationContext());
                triggerCallUpdated();
            }
            sendAlertSMS(null);
        }

        if (Utils.isGPSon(this) && isServicesConnected()) {
            locationClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            locationClient.connect();
        } else { // GPS switched off
            sendAlertSMS(getString(R.string.accurate_location_unavailable));
            // Generate notification in notification bar and
            NotificationUtils.postNotification(this, getString(R.string.location_disabled), VALRTApplication.LOCATION_NOTIFY_ID);
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
     * Alert progress.
     */
    public void alertProgress() {
        // Disable the cancel all button when main box is checked/no other call
        // check box is checked
        cancelAllButton.setVisibility(View.VISIBLE);
        // Setting First Text & Call status
        if (!TextUtils.isEmpty(VALRTApplication
                .getPrefString(this, VALRTApplication.CONTACTONENAME))) {
            if (VALRTApplication.getPrefBoolean(this, VALRTApplication.C1TCBX)) {
                disbleNoContact(true);
                firstMessageLayout.setVisibility(View.VISIBLE);
                firstMessageNumberTextView.setText(VALRTApplication.getPrefString(this, VALRTApplication.CONTACTONENAME));
            }
            if (VALRTApplication.getPrefBoolean(this, VALRTApplication.C1CCBX)) {
                disbleNoContact(true);
                firstCallLayout.setVisibility(View.VISIBLE);

                firstCallNumberTextView.setText(VALRTApplication.getPrefString(this, VALRTApplication.CONTACTONENAME));
                ++contactsCount;
            }
        }
        // Setting second Text & Call status
        if (!TextUtils.isEmpty(VALRTApplication
                .getPrefString(this, VALRTApplication.CONTACTTWONAME))) {
            if (VALRTApplication.getPrefBoolean(this, VALRTApplication.C2TCBX)) {
                disbleNoContact(true);
                secondMessgaeLayout.setVisibility(View.VISIBLE);
                secondMessageNumberTextView.setText(VALRTApplication.getPrefString(this,
                        VALRTApplication.CONTACTTWONAME));
            }
            if (VALRTApplication.getPrefBoolean(this, VALRTApplication.C2CCBX)) {
                disbleNoContact(true);
                if (contactsCount == 1) {
                    firstCallLayout.setVisibility(View.VISIBLE);
                    firstCallNumberTextView.setText(VALRTApplication.getPrefString(this,
                            VALRTApplication.CONTACTTWONAME));
                } else if (contactsCount == 2) {
                    secondCallLayout.setVisibility(View.VISIBLE);
                    secondCallNumberTextView.setText(VALRTApplication.getPrefString(this,
                            VALRTApplication.CONTACTTWONAME));
                }
                ++contactsCount;
            }
        }
        // Setting Third Text & Call status
        if (!TextUtils.isEmpty(VALRTApplication.getPrefString(this,
                VALRTApplication.CONTACTTHREENAME))) {
            if (VALRTApplication.getPrefBoolean(this, VALRTApplication.C3TCBX)) {
                disbleNoContact(true);
                thirdMessageLayout.setVisibility(View.VISIBLE);
                thirdMessageNumberTextView.setText(VALRTApplication.getPrefString(this,
                        VALRTApplication.CONTACTTHREENAME));
            }

            if (VALRTApplication.getPrefBoolean(this, VALRTApplication.C3CCBX)) {
                disbleNoContact(true);
                if (contactsCount == 1) {
                    firstCallLayout.setVisibility(View.VISIBLE);
                    firstCallNumberTextView.setText(VALRTApplication.getPrefString(this,
                            VALRTApplication.CONTACTTHREENAME));
                } else if (contactsCount == 2) {
                    secondCallLayout.setVisibility(View.VISIBLE);
                    secondCallNumberTextView.setText(VALRTApplication.getPrefString(this,
                            VALRTApplication.CONTACTTHREENAME));
                } else if (contactsCount == 3) {
                    thirdCallLayout.setVisibility(View.VISIBLE);
                    thirdCallNumberTextView.setText(VALRTApplication.getPrefString(this,
                            VALRTApplication.CONTACTTHREENAME));
                }
                ++contactsCount;
            }
        }
        // To enable calling to text by default
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.C1CCBX) || VALRTApplication.getPrefBoolean(this, VALRTApplication.C2CCBX) || VALRTApplication.getPrefBoolean(this, VALRTApplication.C3CCBX)) {
            firstCallStatusTextView.setVisibility(View.VISIBLE);
        }
    }
    /**
     * Send alert sms.
     *
     * @param location the location
     */
    // Sending Alert SMS to the contacts.
    public void sendAlertSMS(String location) {
        if (location == null) {
            message = messageType + " " + getString(R.string.from) + ": "
                    + VALRTApplication.getPrefString(this, VALRTApplication.PERSONAL_INFO_NAME)
                    + " "
                    + VALRTApplication.getPrefString(this, VALRTApplication.PERSONAL_INFO_PHONE)
                    + " " + VALRTApplication.getPrefString(this, VALRTApplication.ALERTMSG) + ",  "
                    + getString(R.string.location_to_follow);
            sendSMS(message, null);
        } else {
            message = location + " " + Utils.currentTimeStamp();
            sendSMS(message, "location");
        }
    }
    /**
     * Send sms.
     *
     * @param msg the msg
     * @param type the type
     */
    // Send SMS
    @SuppressWarnings("unchecked")
    public void sendSMS(String msg, String type) {
        try {
            String phoneNumbers = "";
            int i = 0;
            for (String phoneTemp : phoneNumberList) {
                if (i == 0)
                    phoneNumbers = phoneTemp;
                else
                    phoneNumbers = phoneNumbers + "," + phoneTemp;
                i = 1;
            }
            List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("serial_no", deviceSerialNumber));
            nameValuePairs.add(new BasicNameValuePair("numberOfSmsReceverOrEmergencyNumberList", phoneNumbers));
            nameValuePairs.add(new BasicNameValuePair("messageBodyContent", msg));
            nameValuePairs.add(new BasicNameValuePair("macId", deviceAddress));

            if (!TextUtils.isEmpty(phoneNumbers)) {
                if (type == null) {
                    new SendSmsAsync().execute(nameValuePairs);
                } else {
                    new SendLocationSmsAsync().execute(nameValuePairs);
                }
            }
        } catch (Exception e) {
            LogUtils.i("TAG", e); //logException
        }
    }
    /**
     * Trigger call updated.
     */
    public void triggerCallUpdated() {

        String phoneNumber;
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.C1CCBX)) {
            phoneNumber = VALRTApplication.getPrefString(this, VALRTApplication.CONTACTONENUMBER);
            callQueueExecutor.addCall(phoneNumber);
        }
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.C2CCBX)) {
            phoneNumber = VALRTApplication.getPrefString(this, VALRTApplication.CONTACTTWONUMBER);
            callQueueExecutor.addCall(phoneNumber);
        }
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.C3CCBX)) {
            phoneNumber = VALRTApplication.getPrefString(this, VALRTApplication.CONTACTTHREENUMBER);
            callQueueExecutor.addCall(phoneNumber);
        }
        if (VoipPhone.sdkInitiated) {
            callQueueExecutor.makeCall();
        }
    }
    private void setActivityDisplay(int status,
                                    ImageView imageView,
                                    TextView textView)
    {
        if (status == CallQueueExecutor.CALL_STATUS_SUCCESS)
        {
            imageView.setImageResource(R.drawable.img_alerttick);
            imageView.setVisibility(View.VISIBLE);
            textView.setText(getString(R.string.alert_called));
        } else if (status == CallQueueExecutor.CALL_STATUS_FAIL)
        {
            imageView.setImageResource(R.drawable.ic_call_failed);
            imageView.setVisibility(View.VISIBLE);
            textView.setText(getString(R.string.alert_failed));
        } else if (status == CallQueueExecutor.CALL_STATUS_RETRY)
        {
            imageView.setVisibility(View.INVISIBLE);
            textView.setText(getString(R.string.alert_call));
            --callCount;
        }
    }
    /** The voip call status receiver. */
    BroadcastReceiver voipCallStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (VALRTReceiver.ACTION_NETWORK_STATE_CHANGED.equals(action)) {
                if (!Utils.isNetConnected(AlertProgressActivity.this))
                {
                    Toast.makeText(context,R.string.network_error,Toast.LENGTH_LONG);
                    LogUtils.LOGE(TAG,"Failure missing network supposedly...");
                    if (callQueueExecutor != null)
                    {
                        callQueueExecutor.onRequestCompleted(CallQueueExecutor.CALL_STATUS_SUCCESS);
                    }
                }
            } else if (CallQueueExecutor.ACTION_CALL_CONNECTED.equals(action)) {
                if (callCount == 1) {
                    firstCallStatusTextView.setVisibility(View.VISIBLE);
                    titleTextView.setText(getString(R.string.connected));
                } else if (callCount == 2) {
                    secondCallStatusTextView.setVisibility(View.VISIBLE);
                    titleTextView.setText(getString(R.string.connected));
                } else if (callCount == 3) {
                    thirdCallStatusTextView.setVisibility(View.VISIBLE);
                    titleTextView.setText(getString(R.string.connected));
                }

                if (playSound != null)
                    playSound.pause();

            } else if (CallQueueExecutor.ACTION_CALL_DISCONNECTED.equals(action)) {
                if (playSound != null)
                    playSound.resume();
                int status = intent.getIntExtra(CallQueueExecutor.EXTRA_CALL_STATUS, 0);
                if (callCount == 1)
                {
                    setActivityDisplay(status,firstCallStatusImageView,firstCallStatusTextView);
                } else if (callCount == 2)
                {
                    setActivityDisplay(status, secondCallStatusImageView,secondCallStatusTextView);
                } else if (callCount == 3)
                {
                    setActivityDisplay(status, thirdCallStatusImageView,thirdCallStatusTextView);
                }
                ++callCount;
            } else if (CallQueueExecutor.ACTION_CALL_QUEUE_COMPLETED.equals(action)) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.LOGE(TAG,"done calling.");
                    }
                }, 200);
                cancelAllButton.setText(getString(R.string.done_findme));
                titleTextView.setText(getString(R.string.alert_completed));
            }
        }
    };
    /**
     * The Class SendSmsAsync.
     */
    class SendSmsAsync extends AsyncTask<List<BasicNameValuePair>, Void, Void> {

        /**
         * Do in background.
         *
         * @param params the params
         * @return the void
         */
        @Override
        protected Void doInBackground(List<BasicNameValuePair>... params) {
            List<BasicNameValuePair> urlParameters = params[0];
            int resultCode = Utils.serverRequest(urlParameters);
            if (resultCode == 500) {
                resultCode = Utils.serverRequest(urlParameters);
                if (resultCode == 500) {
                    resultCode = Utils.serverRequest(urlParameters);
                }
            }
            return null;
        }
    }
    /**
     * The Class SendLocationSmsAsync.
     */
    class SendLocationSmsAsync extends AsyncTask<List<BasicNameValuePair>, Void, Void> {

        /**
         * Do in background.
         *
         * @param params the params
         * @return the void
         */
        @Override
        protected Void doInBackground(List<BasicNameValuePair>... params) {
            List<BasicNameValuePair> urlParameters = params[0];
            int resultCode = Utils.serverRequest(urlParameters);
            if (resultCode == 500) {
                resultCode = Utils.serverRequest(urlParameters);
                if (resultCode == 500) {
                    resultCode = Utils.serverRequest(urlParameters);
                    resultStatus = resultCode;
                } else {
                    resultStatus = resultCode;
                }
            } else {
                resultStatus = resultCode;
            }
            return null;
        }
        /**
         * On post execute.
         *
         * @param result the result
         */
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (VALRTApplication
                    .getPrefBoolean(AlertProgressActivity.this, VALRTApplication.C1CCBX) == false
                    && VALRTApplication.getPrefBoolean(AlertProgressActivity.this,
                    VALRTApplication.C2CCBX) == false
                    && VALRTApplication.getPrefBoolean(AlertProgressActivity.this,
                    VALRTApplication.C3CCBX) == false) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("done called2","done called2");
                    }
                }, 2000);
                cancelAllButton.setText(getString(R.string.done_findme));
            }

            if (VALRTApplication
                    .getPrefBoolean(AlertProgressActivity.this, VALRTApplication.C1TCBX)) {
                if (resultStatus == 200) {
                    firstMessageStatusImageView.setImageResource(R.drawable.img_alerttick);
                } else {
                    firstMessageStatusImageView.setImageResource(R.drawable.ic_call_failed);
                }
                firstMessageStatusImageView.setVisibility(View.VISIBLE);
            }

            if (VALRTApplication
                    .getPrefBoolean(AlertProgressActivity.this, VALRTApplication.C2TCBX)) {
                if (resultStatus == 200) {
                    secondMessageStatusImageView.setImageResource(R.drawable.img_alerttick);
                } else {
                    secondMessageStatusImageView.setImageResource(R.drawable.ic_call_failed);
                }
                secondMessageStatusImageView.setVisibility(View.VISIBLE);
            }

            if (VALRTApplication
                    .getPrefBoolean(AlertProgressActivity.this, VALRTApplication.C3TCBX)) {
                if (resultStatus == 200) {
                    thirdMessageStatusImageView.setImageResource(R.drawable.img_alerttick);
                } else {
                    thirdMessageStatusImageView.setImageResource(R.drawable.ic_call_failed);
                }
                thirdMessageStatusImageView.setVisibility(View.VISIBLE);
            }
        }
    }
    /**
     * *
     * End.
     *
     * @param v the v
     */

    public void DisableCall(View v) {
        // To stop ongoing call and all pending calls
        if (callQueueExecutor != null)
            callQueueExecutor.stopAll();
        if (bluetoothLeService != null) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    bluetoothLeService.ackDevice(alertProgressGatt, GattConstant.CANCEL_ACK);
                }
            }, 2000);
        }
        finish();
    }
    /**
     * Disble no contact.
     *
     * @param flag the flag
     */
    public void disbleNoContact(boolean flag) {
        if (flag == true)
            noContactsTextView.setVisibility(View.INVISIBLE);
    }
    /**
     * On connection failed.
     *
     * @param connectionResult the connection result
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        LogUtils.LOGE(TAG,"Failure with connection for alert progress");
        /*
         * Google Play services can resolve some errors it detects. If the error
         * has a resolution, try sending an Intent to start a Google Play
         * services activity that can resolve error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                LogUtils.LOGE(TAG,"Failure with connection for alert progress");
                Crashlytics.logException(e)
                ;
            }
        } else {
            // If no resolution is available, display a dialog to the user with
            // the error.
        }
    }
    /**
     * On connected.
     *
     * @param arg0 the arg0
     */
    /*
     * This is overridden method of interface
     * GooglePlayServicesClient.ConnectionCallbacks which is called when
     * locationClient is connected to google service. You can receive GPS
     * reading only when this method is called.So request for location updates
     * from this method rather than onStart()
     */
    @Override
    public void onConnected(Bundle arg0) {
        LocationServices.FusedLocationApi.requestLocationUpdates( locationClient,locationRequest, this);
    }
    @Override
    public void onConnectionSuspended(int i)
    {

    }
    /**
     * On location changed.
     *
     * @param location the location
     */
    /*
     * Overridden method of interface LocationListener called when location of
     * gps device is changed. Location Object is received as a parameter. This
     * method is called when location of GPS device is changed
     */
    @Override
    public void onLocationChanged(Location location) {
        String lanlat = location.getLatitude() + "," + location.getLongitude();
        sendAlertSMS(getString(R.string.my_estimated_location) + " http://maps.google.com/maps?q="
                + lanlat);
        stopGPS();
    }
    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean isServicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(AlertProgressActivity.this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * Stop gps.
     */
    /*
     * Called when Service running in background is stopped. Remove location
     * Update to stop receiving GPS reading
     */
    public void stopGPS() {
        LocationServices.FusedLocationApi.removeLocationUpdates(locationClient,this);
    }
    /**
     * On resume.
     */
    @Override
    public void onResume() {
        super.onResume();
    }
    /**
     * On pause.
     */
    @Override
    protected void onPause() {
        super.onPause();
    }
    /**
     * On back pressed.
     */
    @Override
    public void onBackPressed() {
        // Do nothing
    }
    /**
     * On destroy.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clears the flag for Keep Screen On
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        bluetoothLeService.ackDevice(alertProgressGatt, GattConstant.CANCEL_ACK);
        VALRTApplication.isAlertInProgress = false;
        if (callQueueExecutor != null)
            callQueueExecutor.stopAll();
        unbindService(mServiceConnection);
        unregisterReceiver(voipCallStatusReceiver);
        if (locationClient != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(locationClient,this);
        if (playSound != null) {
            playSound.stop();
            playSound = null;
        }
        Utils.cancelNotify(this, VALRTApplication.ALERTINPROGRESS_NOTIFY_ID);
    }
}
