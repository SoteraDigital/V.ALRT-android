package com.vsnmobil.valrt.voip;

import java.util.HashMap;
//import
import java.util.Map;
//import
import android.content.Context;
//import
import android.content.Intent;
//import
import android.media.AudioManager;
//import
import android.os.AsyncTask;
//import
import com.crashlytics.android.Crashlytics;
import com.twilio.client.Connection;
//import
import com.twilio.client.ConnectionListener;
//import
import com.twilio.client.Device;
//import
import com.twilio.client.Device.Capability;
//import
import com.twilio.client.Twilio;
//import
import com.twilio.client.Twilio.InitListener;
//import
import com.vsnmobil.valrt.VALRTApplication;
import com.vsnmobil.valrt.utils.LogUtils;
import com.vsnmobil.valrt.utils.Utils;

import io.fabric.sdk.android.services.common.Crash;

/**
 * VoipPhone.java
 *
 * This class contains the call back methods for the VOIP call. It will create a device and initiate
 * the call. It will trigger the corresponding call back methods when ever the action trigger
 * like connecting, error occur and disconnected etc.
 *
 */
public class VoipPhone {

    private final static String TAG = VoipPhone.class.getSimpleName();
    /** The device. */
    private static Device device;
    /** The connection. */
    private static Connection connection;
    /** The sdk initiated. */
    public static boolean sdkInitiated = false;
    /** The instance. */
    private static VoipPhone instance;
    /** The capability token. */
    private String capabilityToken;
    /** The m context. */
    private static Context mContext;
    /** The audio manager. */
    private static AudioManager audioManager;
    /** The phone number. */
    private String phoneNumber = "";
    /** The token duration. */
    private String tokenDuration = "tokenDuration" + "=" + 480;
    /** The get token async. */
    private GetTokenAsync getTokenAsync;
    /**
     * The Interface CallBackInterface.
     */
    public interface CallBackInterface {
        /**
         * It call backs whenever the connection disconnected.
         *
         * @param connectionStatus the connection status
         */
        public void onRequestCompleted(int connectionStatus);

        /**
         * Call backs whenever the sdk initialization completed.
         */
        public void onInitiateCompleted();
    }
    /**
     * Gets the single instance of VoipPhone.
     *
     * @param context the context
     * @param callBackInterface the call back interface
     * @return single instance of VoipPhone
     */
    public static final VoipPhone getInstance(Context context,
                                              CallBackInterface callBackInterface) {
        mContext = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (instance == null)
            instance = new VoipPhone(context, callBackInterface);
        return instance;
    }
    /** The m call back interface. */
    private CallBackInterface mCallBackInterface;
    /**
     * Instantiates a new voip phone.
     *
     * @param context the context
     * @param callBackInterface the call back interface
     */
    public VoipPhone(Context context, CallBackInterface callBackInterface) {
        mCallBackInterface = callBackInterface;
        if (!sdkInitiated) {
            Twilio.initialize(context, new InitListener() {
                @Override
                public void onInitialized() {
                    sdkInitiated = true;
                    mCallBackInterface.onInitiateCompleted();
                }

                @Override
                public void onError(Exception e) {
                    mCallBackInterface.onRequestCompleted(CallQueueExecutor.CALL_STATUS_FAIL);
                    LogUtils.LOGE(TAG,"Exception intializaing twilio:"+e.toString());
                    Crashlytics.logException(e);
                }

                @Override
                protected void finalize() throws Throwable {
                    super.finalize();
                    if (connection != null)
                        connection.disconnect();
                    if (device != null) {
                        device.release();
                        device = null;
                    }
                }
            });

        }
    }
    /**
     * Gets AccessToken from Webservice URL.
     *
     * @return the access token
     */
    public void getAccessToken() {
        try {
            capabilityToken = HttpHelper.httpGet(VALRTApplication.VOIP_CALL_WEBSERVICE_URL+ tokenDuration);
        } catch (Exception e) {
            mCallBackInterface.onRequestCompleted(CallQueueExecutor.CALL_STATUS_FAIL);
            LogUtils.LOGE(TAG,"Get access token get failure:"+e.toString());
            Crashlytics.logException(e);
        }
    }
    /**
     * Connects Phone to make Call.
     *
     * @param phoneNumber the phone number
     */
    public void connect(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        try {
            getTokenAsync = new GetTokenAsync();
            getTokenAsync.execute();
        } catch (Exception e) {
            mCallBackInterface.onRequestCompleted(CallQueueExecutor.CALL_STATUS_FAIL);
            LogUtils.LOGE(TAG,"Execute conncet failed getting token:"+e.toString());
            Crashlytics.logException(e);
        }
    }
    /**
     * Disconnects Phone Connection and Close Device Initiated.
     */
    public void disconnect() {
        try
        {
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
            if (device != null) {
                device.release();
                device.disconnectAll();
                device = null;
            }
        }catch(Exception e){
            LogUtils.LOGE(TAG,"disconnect failure:"+e.toString());
            Crashlytics.logException(e);
        }
    }
    /**
     * Stops Running Asynchronous Task.
     */
    public void stopAsyncTask() {
        if (getTokenAsync != null) {
            if (!getTokenAsync.isCancelled()) {
                getTokenAsync.cancel(true);
            }
        }
    }
    /**
     * Indicates outgoing call can make.
     *
     * @return true- can make outgoing call, false - otherwise.
     */
    public boolean canMakeOutgoing() {
        if (device == null)
            return false;
        Map<Capability, Object> caps = device.getCapabilities();
        return caps.containsKey(Capability.OUTGOING)
                && (Boolean) caps.get(Capability.OUTGOING);
    }
    /**
     * Sends the status of call being activated to its registered broadcast
     * receiver.
     *
     * @param action the action
     */
    public void broadcastUpdate(String action) {
        final Intent intent = new Intent(action);
        mContext.getApplicationContext().sendBroadcast(intent);
    }
    private class GetTokenAsync extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            disconnect();
        };

        @Override
        protected Void doInBackground(Void... params) {
            getAccessToken();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Initialize Device for Making Call Connection
            device = Twilio.createDevice(capabilityToken, null /* DeviceListener */);
            device.setOutgoingSoundEnabled(true);
            device.setDisconnectSoundEnabled(true);
            // Create Map values for Calling Phone Numbers
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("emergencyNo", phoneNumber);
            // Initialize Connection for Outgoing
            connection = device.connect(parameters, new ConnectionListener()
            {
                @Override
                public void onDisconnected(Connection connection,int inErrorCode, String inErrorMessage)
                {
                    mCallBackInterface.onRequestCompleted(CallQueueExecutor.CALL_STATUS_FAIL);
                    LogUtils.LOGE(TAG,"Disconnected VOIP Phone:"+ inErrorMessage);
                    Crashlytics.log("Call disconnected with: "+ inErrorMessage);
                    VoipPhone.this.disconnect();
                }
                @Override
                public void onDisconnected(Connection arg0)
                {
                    if (audioManager != null) {
                        audioManager.setSpeakerphoneOn(false);
                    }
                    LogUtils.LOGW(TAG,"Call disconnected due to: "+ arg0.getState().toString());
                    Crashlytics.log("Call disconnected with state: "+ arg0.getState().toString());
                    mCallBackInterface.onRequestCompleted(CallQueueExecutor.CALL_STATUS_SUCCESS);
                    VoipPhone.this.disconnect();
                }
                @Override
                public void onConnecting(Connection arg0)
                {
                    if (audioManager != null) {
                        audioManager.setSpeakerphoneOn(true);
                        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),0);
                    }
                }
                @Override
                public void onConnected(Connection arg0)
                {
                    broadcastUpdate(CallQueueExecutor.ACTION_CALL_CONNECTED);
                }
            } );
        };
    };
}
