package com.vsnmobil.valrt.activities;

import android.app.Activity;
//import
import android.app.Dialog;
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
import android.util.Log;
import android.view.KeyEvent;
//import
import android.view.View;
//import
import android.view.View.OnClickListener;
//import
import android.view.Window;
//import
import android.view.inputmethod.EditorInfo;
//import
import android.view.inputmethod.InputMethodManager;
//import
import android.widget.AdapterView;
//import
import android.widget.ArrayAdapter;
//import
import android.widget.Button;
//import
import android.widget.EditText;
//import
import android.widget.ListView;
//import
import android.widget.TextView;
//import
import com.crashlytics.android.Crashlytics;
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
//import
import com.vsnmobil.valrt.utils.Utils;
/**
 * PersonalInfoActivity.java
 * <p/>
 * This class is to get the personal information like name, phone number and country code.
 */
public class PersonalInfoActivity extends Activity {
    /** The next button. */
    private Button nextButton;
    /** The back button. */
    private Button backButton;
    /** The country code button. */
    private Button countryCodeButton;
    /** The name edit text. */
    private EditText nameEditText;
    /** The phone number edit text. */
    private EditText phoneNumberEditText;
    /** The intent. */
    private Intent intent;
    /** The dialog. */
    private Dialog dialog;
    /** The name. */
    private String name;
    /** The phone number. */
    private String phoneNumber;
    /** The country. */
    private String country;
    /** The country array. */
    private String[] countryArray;
    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        countryArray = getResources().getStringArray(R.array.country_arrays);
        nextButton = (Button) findViewById(R.id.personal_info_next_button);
        backButton = (Button) findViewById(R.id.personal_info_back_button);
        countryCodeButton = (Button) findViewById(R.id.personal_country_code_button);
        nameEditText = (EditText) findViewById(R.id.personal_name_editview);
        phoneNumberEditText = (EditText) findViewById(R.id.personal_phonenumber_editview);
        String country = VALRTApplication.getPrefString(PersonalInfoActivity.this, VALRTApplication.PERSONAL_INFO_COUNTRY_CODE);
        if(country.equalsIgnoreCase("MX")){
            VALRTApplication.setPrefString(PersonalInfoActivity.this,VALRTApplication.PERSONAL_INFO_COUNTRY_CODE,"MX");
        }
        else if(country.equalsIgnoreCase("IN")){
            VALRTApplication.setPrefString(PersonalInfoActivity.this,VALRTApplication.PERSONAL_INFO_COUNTRY_CODE,"MX");
        }
        else if(country.equalsIgnoreCase("BR")){
            VALRTApplication.setPrefString(PersonalInfoActivity.this,VALRTApplication.PERSONAL_INFO_COUNTRY_CODE,"MX");
        }
        // Back button listener
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //To check the user is first time to application or not.
                if (VALRTApplication.getPrefBoolean(PersonalInfoActivity.this, VALRTApplication.CONGRATULATION) == true) {
                    //To validate the entered information.
                    if (validateInfo()) {
                        //Navigate to setting screen.
                        finish();
                    }
                } else {
                    //Navigate to agreement screen
                    intent = new Intent(PersonalInfoActivity.this, AgreementActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        // next button listener
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //To validate the entered information.
                if (validateInfo()) {
                    //Navigate to add contact screen.
                    intent = new Intent(PersonalInfoActivity.this, AddContactActivity.class);
                    startActivity(intent);
                }
            }
        });
        //Country code button listener.
        countryCodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VALRTApplication.getPrefBoolean(PersonalInfoActivity.this, VALRTApplication.COUNTRY_CODE_SELECTED)) {
                    alertDialogShow();
                } else {
                    showCountryListDialog();
                }
            }
        });
        //Phone number edit text listener to soft keyboard done button press event.
        phoneNumberEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    validateInfo();
                    return true;
                }
                return false;
            }
        });
        //Phone number edit text listener to track the user action.
        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if user enter any character in edit text enable the color of next button.
                if (count >= 1) {
                    nextButton.setTextColor(getResources().getColor(R.color.violet_color));
                    nextButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_next_enable_arrow, 0);
                    nextButton.setEnabled(true);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //user name edit text listener to track the user action.
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count >= 3) {
                    nameEditText.setError(null);
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
    /**
     * On resume.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // set the previously stored values in the corresponding edit text .
        if (!TextUtils.isEmpty(VALRTApplication.getPrefString(this, VALRTApplication.PERSONAL_INFO_NAME))) {
            nameEditText.setText(VALRTApplication.getPrefString(this, VALRTApplication.PERSONAL_INFO_NAME));
            nextButton.setEnabled(true);
        } else {
            nextButton.setEnabled(false);
        }

        if (!TextUtils.isEmpty(VALRTApplication.getPrefString(this, VALRTApplication.PERSONAL_INFO_PHONE))) {
            phoneNumberEditText.setText(VALRTApplication.getPrefString(this, VALRTApplication.PERSONAL_INFO_PHONE));
            nextButton.setEnabled(true);
        } else {
            nextButton.setEnabled(false);
        }

        country = VALRTApplication.getPrefString(this, VALRTApplication.PERSONAL_INFO_COUNTRY_CODE);
        if (country.equalsIgnoreCase("MX")) {
            countryCodeButton.setText(countryArray[1]);
        } /*else if (country.equalsIgnoreCase("BR")) {
            countryCodeButton.setText(countryArray[2]);
        } else if (country.equalsIgnoreCase("IN")) {
            countryCodeButton.setText(countryArray[3]);
        }*/ else {
            countryCodeButton.setText(countryArray[0]);
            country = "US";
        }

        //To move the cursor to the end of the last digit.
        int numberLength = phoneNumberEditText.getText().length();
        phoneNumberEditText.setSelection(numberLength, numberLength);

        int textLength = nameEditText.getText().length();
        nameEditText.setSelection(textLength, textLength);

        //If it's not first time to app invisible the back button.
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.CONGRATULATION) == true) {
            nextButton.setVisibility(View.INVISIBLE);
        }
    }
    /**
     * On back pressed.
     */
    @Override
    public void onBackPressed() {
        // If it's not first time to app validate and navigate to previous screen.
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.CONGRATULATION) == true) {
            if (validateInfo()) {
                super.onBackPressed();
            }
        } else {
            //Navigate to agreement screen.
            intent = new Intent(PersonalInfoActivity.this, AgreementActivity.class);
            startActivity(intent);
            finish();
        }
    }
    /**
     * To validate the personal info entered by user and enable / disable the next button.
     *
     * @return true, if successful
     */
    private boolean validateInfo() {
        name = nameEditText.getText().toString().trim();
        phoneNumber = phoneNumberEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || name.length() < 3) {
            nameEditText.setError(getString(R.string.enter_valid_name));
            nameEditText.requestFocus();
            return false;
        } else if (Utils.validCellPhone(phoneNumber) == false || phoneNumber.length() <= 7) {
            phoneNumberEditText.setError(getString(R.string.valid_contact));
            phoneNumberEditText.requestFocus();
            return false;
        } else {
            VALRTApplication.setPrefString(PersonalInfoActivity.this, VALRTApplication.PERSONAL_INFO_NAME, name);
            VALRTApplication.setPrefString(PersonalInfoActivity.this, VALRTApplication.PERSONAL_INFO_PHONE, phoneNumber);
            VALRTApplication.setPrefString(PersonalInfoActivity.this, VALRTApplication.PERSONAL_INFO_COUNTRY_CODE, country);
            VALRTApplication.setPrefBoolean(PersonalInfoActivity.this, VALRTApplication.COUNTRY_CODE_SELECTED, true);
            Crashlytics.setUserIdentifier(phoneNumber);
            Crashlytics.setUserName(name);

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(phoneNumberEditText.getWindowToken(), 0);
            return true;
        }
    }
    /**
     * To show the country list pop up.
     */
    private void showCountryListDialog() {
        dialog = new Dialog(this, R.style.ThemeWithCorners);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_country_popup);
        dialog.setCancelable(false);
        Button cancelButton = (Button) dialog.findViewById(R.id.country_cancel_button);
        ListView countyListView = (ListView) dialog.findViewById(R.id.country_listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.listitem_country, getResources().getStringArray(
                R.array.country_arrays));
        countyListView.setAdapter(adapter);

        countyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                if (position == 0) {
                    country = "US";
                } else {
                    country = "MX";
                }
                countryCodeButton.setText(countryArray[position]);

                if (!VALRTApplication.getPrefString(PersonalInfoActivity.this, VALRTApplication.PERSONAL_INFO_COUNTRY_CODE).equalsIgnoreCase(country)) {
                    clearContactConfig();
                }
                //Set the country code.
                VALRTApplication.setPrefString(PersonalInfoActivity.this, VALRTApplication.PERSONAL_INFO_COUNTRY_CODE, country);
                dialog.dismiss();

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    /**
     * To show the warning message to the user about the selected contact lost.
     */
    public void alertDialogShow() {

        dialog = new Dialog(this, R.style.ThemeWithCorners);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_info);

        dialog.setCancelable(false);
        TextView messageTextView = (TextView) dialog.findViewById(R.id.info_title_textview);
        Button okButton = (Button) dialog.findViewById(R.id.info_ok_button);
        messageTextView.setText(getString(R.string.edit_contact_warning));
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showCountryListDialog();
            }
        });
        dialog.show();
    }
    /**
     * To clear the previously selected Contact configuration.
     */
    private void clearContactConfig() {
        VALRTApplication.setPrefBoolean(this, VALRTApplication.C1TCBX, false);
        VALRTApplication.setPrefBoolean(this, VALRTApplication.C2TCBX, false);
        VALRTApplication.setPrefBoolean(this, VALRTApplication.C3TCBX, false);
        VALRTApplication.setPrefBoolean(this, VALRTApplication.C1CCBX, false);
        VALRTApplication.setPrefBoolean(this, VALRTApplication.C2CCBX, false);
        VALRTApplication.setPrefBoolean(this, VALRTApplication.C3CCBX, false);
        VALRTApplication.setPrefString(this, VALRTApplication.CONTACTONENAME, "");
        VALRTApplication.setPrefString(this, VALRTApplication.CONTACTONENUMBER, "");
        VALRTApplication.setPrefString(this, VALRTApplication.CONTACTTWONAME, "");
        VALRTApplication.setPrefString(this, VALRTApplication.CONTACTTWONUMBER, "");
        VALRTApplication.setPrefString(this, VALRTApplication.CONTACTTHREENAME, "");
        VALRTApplication.setPrefString(this, VALRTApplication.CONTACTTHREENUMBER, "");
    }
}
