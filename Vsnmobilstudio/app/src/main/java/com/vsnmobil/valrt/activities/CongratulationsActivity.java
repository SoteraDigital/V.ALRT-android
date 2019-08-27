package com.vsnmobil.valrt.activities;
/**
 * CongratulationsActivity.java
 * <p/>
 * This class load the Congratulations content in a view.
 */
import java.util.ArrayList;
//import
import java.util.List;
//import
import org.apache.http.message.BasicNameValuePair;
//import
import android.app.Activity;
//import
import android.app.Dialog;
//import
import android.content.Intent;
//import
import android.os.AsyncTask;
//import
import android.os.Bundle;
//import
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
import android.widget.RelativeLayout;
//import
import android.widget.TextView;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
//import
import com.vsnmobil.valrt.storage.DatabaseHelper;
//import
import com.vsnmobil.valrt.utils.LogUtils;
import com.vsnmobil.valrt.utils.Utils;
/**
 * The Class CongratulationsActivity.
 */
public class CongratulationsActivity extends Activity {
    /** The done button. */
    private Button doneButton;
    /** The loading image view. */
    private ImageView loadingImageView;
    /** The loading relative layout. */
    private RelativeLayout loadingRelativeLayout;
    /** The send config sms. */
    private SendConfigSmsAsync sendConfigSms;
    /** The name value pairs. */
    private List<BasicNameValuePair> nameValuePairs;
    /** The phone number list. */
    private List<String> phoneNumberList;
    /** The device address. */
    private String deviceAddress;
    /** The device serial number. */
    private String deviceSerialNumber = "1234567";
    /** The message. */
    private String message;
    /** The db helper. */
    private DatabaseHelper dbHelper;
    /** The data intent. */
    private Intent dataIntent;
    /** The is done clicked. */
    private boolean isDoneClicked = false;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulations);
        dbHelper = new DatabaseHelper(this);
        // To get the value from the previous activity.
        dataIntent = getIntent();
        if (dataIntent.hasExtra(VALRTApplication.INTENT_DEVICE_ADDRESS))
            deviceAddress = dataIntent.getStringExtra(VALRTApplication.INTENT_DEVICE_ADDRESS);
        if (deviceAddress != null)
            deviceSerialNumber = dbHelper.getDeviceSerial(deviceAddress);
        message = getString(R.string.valrt_emergency) + " " + getString(R.string.from) + ": "
                + VALRTApplication.getPrefString(this, VALRTApplication.PERSONAL_INFO_NAME)
                + ", " + getString(R.string.initial_setup_message);
        phoneNumberList = new ArrayList<String>();
        sendConfigSms = new SendConfigSmsAsync();
        loadingRelativeLayout = (RelativeLayout) findViewById(R.id.congratulation_loading_layout);
        loadingImageView = (ImageView) findViewById(R.id.congratulation_loading_imageview);
        doneButton = (Button) findViewById(R.id.congratulation_done_button);
        // loading animation
        RotateAnimation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(2000);
        loadingImageView.startAnimation(anim);
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
        doneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doneButton.setEnabled(false);
                isDoneClicked = true;
                // show the message alert Pop-up to send the first time
                // configured contact SMS.
                if (checkContactSelected()) {
                    showDialog();
                } else {
                    // Navigate to Home activity.
                    goToHome();
                }
            }
        });
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        if (isDoneClicked == false) {
            super.onBackPressed();
        }
    }
    /**
     * Check contact selected.
     *
     * @return true, if successful
     */
    public boolean checkContactSelected() {
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.C1TCBX) == true
                || VALRTApplication.getPrefBoolean(this, VALRTApplication.C2TCBX) == true
                || VALRTApplication.getPrefBoolean(this, VALRTApplication.C3TCBX) == true) {
            return true;
        }
        return false;
    }
    /**
     * Go to home.
     */
    public void goToHome() {
        // set the preference as true for the user clicked the congratulation.
        VALRTApplication.setPrefBoolean(CongratulationsActivity.this,
                VALRTApplication.CONGRATULATION, true);
        Intent i = new Intent(CongratulationsActivity.this, HomeActivity.class);
        startActivity(i);
        finish(); // finish this activity.
    }
    /**
     * Show dialog.
     */
    public void showDialog() {
        final Dialog alertDialog = new Dialog(this, R.style.ThemeWithCorners);//alertDialog
        alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_alert_yes_no);
        alertDialog.setCancelable(false);
        TextView alerttitleTextView = (TextView) alertDialog //alertTitle
                .findViewById(R.id.alert_title_textview);
        TextView alertMessagetextView = (TextView) alertDialog //alertMessage
                .findViewById(R.id.alert_content_textview);
        Button alertSendButton = (Button) alertDialog.findViewById(R.id.alert_yes_button); //alertSendButton
        Button alertSkipButton = (Button) alertDialog.findViewById(R.id.alert_no_button); //alertSkipButton
        alertSendButton.setText(getString(R.string.send));
        alertSkipButton.setText(getString(R.string.skip));
        alerttitleTextView.setText(getString(R.string.tell_your_contact_that_you_are_part_of_valrt_circle));
        alertMessagetextView.setText(getString(R.string.you_are_part_of_valrt_circle));
        alertSendButton.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
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
                    nameValuePairs = new ArrayList<BasicNameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("serial_no", deviceSerialNumber));
                    nameValuePairs.add(new BasicNameValuePair("numberOfSmsReceverOrEmergencyNumberList", phoneNumbers));
                    nameValuePairs.add(new BasicNameValuePair("messageBodyContent", message));
                    nameValuePairs.add(new BasicNameValuePair("macId", deviceAddress));
                    if (Utils.isNetConnected(CongratulationsActivity.this)) {
                        alertDialog.dismiss();
                        sendConfigSms.execute(nameValuePairs);
                        loadingRelativeLayout.setVisibility(View.VISIBLE);
                    } else {
                        dbHelper.insertDeviceHistory(getString(R.string.history_text_message_not_sent));
                        alertDialog.dismiss();
                        // Navigate to Home activity.
                        goToHome();
                    }
                    // set the preference as true for the user clicked the
                    // congratulation.
                    VALRTApplication.setPrefBoolean(CongratulationsActivity.this,
                            VALRTApplication.CONGRATULATION, true);
                } catch (Exception e) {
                    LogUtils.i("TAG", e); //logException
                }
            }
        });
        alertSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                // Navigate to Home activity.
                goToHome();

            }
        });
        alertDialog.show();
    }
    /**
     * The Class SendConfigSmsAsync.
     */
    // To send the SMS Async.
    class SendConfigSmsAsync extends AsyncTask<List<BasicNameValuePair>, Void, Void> {
        /* (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
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
        /* (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dbHelper.insertDeviceHistory(getString(R.string.history_text_message_sent));
            loadingRelativeLayout.setVisibility(View.INVISIBLE);
            goToHome();
        }
    }
}
