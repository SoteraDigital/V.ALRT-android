package com.vsnmobil.valrt.voip;

import android.content.Context;
//import
import android.content.Intent;
//import
import com.crashlytics.android.Crashlytics;
import com.vsnmobil.valrt.utils.LogUtils;
import com.vsnmobil.valrt.utils.Utils;
//import
import java.util.ArrayList;
//import
import java.util.List;

import io.fabric.sdk.android.services.common.Crash;

/**
 * CallQueueExecutor.java
 * This class handle the execution of the VOIP call and broadcast the status to the UI
 */
public class CallQueueExecutor implements VoipPhone.CallBackInterface {
    private final static String TAG = CallQueueExecutor.class.getSimpleName();

    /** The phone number list. */
    private static List<String> phoneNumberList = new ArrayList();
    /** The phone. */
    public static VoipPhone phone;
    /** The m context. */
    private Context mContext;
    /** The Constant ACTION_CALL_CONNECTED. */
    public final static String ACTION_CALL_CONNECTED = "com.vsnmobil.valrt.ACTION_CALL_CONNECTED";
    /** The Constant ACTION_CALL_DISCONNECTED. */
    public final static String ACTION_CALL_DISCONNECTED = "com.vsnmobil.valrt.ACTION_CALL_DISCONNECTED";
    /** The Constant ACTION_CALL_QUEUE_COMPLETED. */
    public final static String ACTION_CALL_QUEUE_COMPLETED = "com.vsnmobil.valrt.ACTION_CALL_QUEUE_COMPLETED";
    /** The Constant EXTRA_CALL_NUMBER. */
    public final static String EXTRA_CALL_NUMBER = "com.vsnmobil.valrt.EXTRA_CALL_NUMBER";
    /** The Constant EXTRA_CALL_STATUS. */
    public final static String EXTRA_CALL_STATUS = "com.vsnmobil.valrt.EXTRA_CALL_STATUS";
    /** The Constant CALL_STATUS_SUCCESS. */
    public final static int CALL_STATUS_SUCCESS = 1;
    /** The Constant CALL_STATUS_FAIL. */
    public final static int CALL_STATUS_FAIL = 2;
    /** The Constant CALL_STATUS_RETRY. */
    public final static int CALL_STATUS_RETRY = 3;
    /**
     * Instantiates a new call queue executor.
     *
     * @param context the context
     */
    public CallQueueExecutor(Context context) {
        mContext = context;
        phone = VoipPhone.getInstance(mContext, this);
        phoneNumberList.clear();
    }
    /**
     * Add PhoneNumber to Call List.
     *
     * @param phoneNumber the phone number
     */
    public void addCall(String phoneNumber) {
        phoneNumberList.add(phoneNumber);
    }
    /**
     * Remove PhoneNumber from Call List.
     *
     * @param phoneNumber the phone number
     */
    public void removeCall(String phoneNumber) {
        if (phoneNumberList.contains(phoneNumber)) {
            phoneNumberList.remove(phoneNumber);
        }
    }
    /**
     * Makes a call to the listed phone numbers.
     */
    public void makeCall() {
        try {
            if (!phoneNumberList.isEmpty()) {
                if (Utils.isNetConnected(mContext)) {
                    phone.connect(phoneNumberList.get(0));
                } else {
                    this.onRequestCompleted(CALL_STATUS_FAIL);
                }
            } else {
                Intent intent = new Intent(ACTION_CALL_QUEUE_COMPLETED);
                mContext.getApplicationContext().sendBroadcast(intent);
                stopAll();
            }
        } catch (Exception e) {
            LogUtils.LOGE(TAG,"Crash sending call broadcast");
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }
    /* (non-Javadoc)
     * @see com.example.testapp.VoipPhone.CallBackInterface#onRequestCompleted(int)
     */
    @Override
    public void onRequestCompleted(int connectionStatus)
    {
        if (!phoneNumberList.isEmpty())
        {
            broadcastUpdate(ACTION_CALL_DISCONNECTED, phoneNumberList.get(0), connectionStatus);
            if (CALL_STATUS_RETRY != connectionStatus)
            {
                phoneNumberList.remove(phoneNumberList.get(0));
            }
            makeCall();
        }
    }
    /**
     * Sends the status of call being activated to its registered broadcast receiver.
     *
     * @param action the action
     * @param phoneNumber the phone number
     * @param status the status
     */
    public void broadcastUpdate(String action, String phoneNumber, int status) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_CALL_NUMBER, phoneNumber);
        intent.putExtra(EXTRA_CALL_STATUS, status);
        mContext.getApplicationContext().sendBroadcast(intent);
    }
    /**
     * Stops all pending and ongoing calls.
     */
    public void stopAll() {
        phone.disconnect();
        phone.stopAsyncTask();
        phoneNumberList.clear();

    }
    /* (non-Javadoc)
     * @see com.example.testapp.VoipPhone.CallBackInterface#onInitiateCompleted()
     */
    @Override
    public void onInitiateCompleted() {
        makeCall();
    }
}
