package com.vsnmobil.valrt.activities;

import android.app.Activity;
//import
import android.content.Context;
//import
import android.content.Intent;
//import
import android.os.Bundle;
//import
import android.text.Editable;
//import
import android.text.TextUtils;
//import
import android.text.TextWatcher;
//import
import android.view.KeyEvent;
//import
import android.view.View;
//import
import android.view.View.OnClickListener;
//import
import android.view.inputmethod.EditorInfo;
//import
import android.view.inputmethod.InputMethodManager;
//import
import android.widget.Button;
//import
import android.widget.EditText;
//import
import android.widget.TextView;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
/**
 * AlertMessageActivity.java
 */
public class AlertMessageActivity extends Activity {
    /** The next button. */
    private Button nextButton;
    /** The back button. */
    private Button backButton;
    /** The intent. */
    private Intent intent;
    /** The alert message edit text. */
    private EditText alertMessageEditText;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_message);
        nextButton = (Button) findViewById(R.id.alert_message_next_button);
        backButton = (Button) findViewById(R.id.alert_message_back_button);
        alertMessageEditText = (EditText) findViewById(R.id.alert_message_content_edittext);
        alertMessageEditText.setFocusable(true);

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VALRTApplication.getPrefBoolean(AlertMessageActivity.this, VALRTApplication.CONGRATULATION) == true) {
                    if (validateInfo()) {
                        finish();
                    }
                } else {
                    finish();
                }
            }
        });
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInfo()) {
                    intent = new Intent(AlertMessageActivity.this, ManageDevicesActivity.class);
                    startActivity(intent);
                }
            }
        });
        alertMessageEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    validateInfo();
                    return true;
                }
                return false;
            }
        });
        alertMessageEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count >= 1) {
                    nextButton.setTextColor(getResources().getColor(R.color.violet_color));
                    nextButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_next_enable_arrow, 0);
                    backButton.setTextColor(getResources().getColor(R.color.violet_color));
                    nextButton.setEnabled(true);
                    alertMessageEditText.setError(null);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(VALRTApplication.getPrefString(this, VALRTApplication.ALERTMSG))) {
            alertMessageEditText.setText(VALRTApplication.getPrefString(this, VALRTApplication.ALERTMSG));
            nextButton.setEnabled(true);
        } else {
            nextButton.setEnabled(false);
        }
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.CONGRATULATION) == true) {
            nextButton.setVisibility(View.INVISIBLE);
        }

        int textLength = alertMessageEditText.getText().length();
        alertMessageEditText.setSelection(textLength, textLength);
    }
    /**
     * Validate info.
     *
     * @return true, if successful
     */
    private boolean validateInfo() {
        if (TextUtils.isEmpty(alertMessageEditText.getText().toString().trim())) {
            alertMessageEditText.setError(getString(R.string.alert_msg_empty));
            return false;
        } else {
            VALRTApplication.setPrefString(AlertMessageActivity.this, VALRTApplication.ALERTMSG, alertMessageEditText.getText().toString().trim());
            nextButton.setTextColor(getResources().getColor(R.color.violet_color));
            nextButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_next_enable_arrow, 0);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(alertMessageEditText.getWindowToken(), 0);
            return true;
        }
    }
}
