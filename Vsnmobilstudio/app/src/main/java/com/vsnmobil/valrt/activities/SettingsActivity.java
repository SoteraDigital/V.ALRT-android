package com.vsnmobil.valrt.activities;

import android.app.Activity;
//import
import android.app.AlertDialog;
//import
import android.app.Dialog;
//import
import android.content.ContentResolver;
//import
import android.content.Context;
//import
import android.content.DialogInterface;
//import
import android.content.DialogInterface.OnClickListener;
//import
import android.content.Intent;
//import
import android.database.Cursor;
//import
import android.os.Bundle;
//import
import android.provider.ContactsContract;
//import
import android.provider.ContactsContract.Contacts;
//import
import android.text.TextUtils;
//import
import android.util.Log;
import android.view.ContextThemeWrapper;
//import
import android.view.KeyEvent;
//import
import android.view.View;
//import
import android.view.Window;
//import
import android.view.WindowManager;
//import
import android.view.inputmethod.EditorInfo;
//import
import android.widget.Button;
//import
import android.widget.CheckBox;
//import
import android.widget.EditText;
//import
import android.widget.LinearLayout;
//import
import android.widget.TextView;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
//import
import com.vsnmobil.valrt.utils.Utils;
/**
 * The Class SettingsActivity.
 */
public class SettingsActivity extends Activity implements View.OnClickListener {
    /** The context. */
    final Context context = this;
    /** The Constant CONTACT_PICKER_RESULT. */
    private static final int CONTACT_PICKER_RESULT = 1001;
    /** The dialog. */
    private Dialog dialog;
    /** The first contact textview. */
    private TextView firstContactTextview;
    /** The second contact text view. */
    private TextView secondContactTextView;
    /** The third contact text view. */
    private TextView thirdContactTextView;
    /** The personal info text view. */
    private TextView personalInfoTextView;
    /** The alert message text view. */
    private TextView alertMessageTextView;
    /** The tracker status text view. */
    private TextView trackerStatusTextView;
    /** The back button. */
    private Button backButton;
    /** The first contact button. */
    private Button firstContactButton;
    /** The second contact button. */
    private Button secondContactButton;
    /** The third contact button. */
    private Button thirdContactButton;
    /** The first contact sms check box. */
    private CheckBox firstContactSmsCheckBox;
    /** The first contact call check box. */
    private CheckBox firstContactCallCheckBox;
    /** The second contact sms check box. */
    private CheckBox secondContactSmsCheckBox;
    /** The second contact call check box. */
    private CheckBox secondContactCallCheckBox;
    /** The third contact sms check box. */
    private CheckBox thirdContactSmsCheckBox;
    /** The third contact call check box. */
    private CheckBox thirdContactCallCheckBox;
    /** The contact name. */
    private String contactName;
    /** The contact number. */
    private String contactNumber;
    /** The intent. */
    private Intent intent;
    /** The is contact one. */
    private boolean isContactOne = false;
    /** The is contact two. */
    private boolean isContactTwo = false;
    /** The is contact three. */
    private boolean isContactThree = false;
    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        trackerStatusTextView = (TextView) findViewById(R.id.settings_tracker_on_off_textview);
        alertMessageTextView = (TextView) findViewById(R.id.settings_alertmessage_textview);
        (firstContactTextview = (TextView) findViewById(R.id.settings_first_contact_textview))
                .setOnClickListener(this);
        (secondContactTextView = (TextView) findViewById(R.id.settings_second_contact_textview))
                .setOnClickListener(this);
        (thirdContactTextView = (TextView) findViewById(R.id.settings_third_contact_textview))
                .setOnClickListener(this);
        (backButton = (Button) findViewById(R.id.settings_back_button)).setOnClickListener(this);
        (firstContactButton = (Button) findViewById(R.id.settings_first_contact_button))
                .setOnClickListener(this);
        (secondContactButton = (Button) findViewById(R.id.settings_second_contact_button))
                .setOnClickListener(this);
        (thirdContactButton = (Button) findViewById(R.id.settings_third_contact_button))
                .setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.settings_personal_info_layout)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.settings_alertmsg_container_layout)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.settings_advance_settings_layout)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.settings_tracker_container_layout)).setOnClickListener(this);
        (firstContactSmsCheckBox = (CheckBox) findViewById(R.id.settings_first_contact_sms_checkbox))
                .setOnClickListener(this);
        (firstContactCallCheckBox = (CheckBox) findViewById(R.id.settings_first_contact_call_checkbox))
                .setOnClickListener(this);
        (secondContactSmsCheckBox = (CheckBox) findViewById(R.id.settings_second_contact_sms_checkbox))
                .setOnClickListener(this);
        (secondContactCallCheckBox = (CheckBox) findViewById(R.id.settings_second_contact_call_checkbox))
                .setOnClickListener(this);
        (thirdContactSmsCheckBox = (CheckBox) findViewById(R.id.settings_third_contact_sms_checkbox))
                .setOnClickListener(this);
        (thirdContactCallCheckBox = (CheckBox) findViewById(R.id.settings_third_contact_call_checkbox))
                .setOnClickListener(this);
        personalInfoTextView = (TextView) findViewById(R.id.settings_contacts_arrow_textview);
    }
    protected void onStart() {
        super.onStart();
        String country = VALRTApplication.getPrefString(SettingsActivity.this, VALRTApplication.PERSONAL_INFO_COUNTRY_CODE);
        if(!country.equalsIgnoreCase("US")){
            Log.e("SettingsActivity","Settings Activity onstart country:"+country);
            VALRTApplication.setPrefString(SettingsActivity.this,VALRTApplication.PERSONAL_INFO_COUNTRY_CODE,"MX");
        }
    }
    /**
     * On resume.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(VALRTApplication.getPrefString(this,
                VALRTApplication.PERSONAL_INFO_NAME))
                && TextUtils.isEmpty(VALRTApplication.getPrefString(this,
                VALRTApplication.PERSONAL_INFO_PHONE))) {
            personalInfoTextView.setText(getResources().getString(R.string.personal_info_add));
        } else {
            personalInfoTextView.setText(getResources().getString(R.string.personal_info_edit));
        }

        if (TextUtils.isEmpty(VALRTApplication.getPrefString(this, VALRTApplication.ALERTMSG))) {
            alertMessageTextView.setText(R.string.tap_to_message);
        } else {
            alertMessageTextView.setText(VALRTApplication.getPrefString(this,
                    VALRTApplication.ALERTMSG));
        }
        firstContactSmsCheckBox.setChecked(VALRTApplication.getPrefBoolean(this,
                VALRTApplication.C1TCBX));
        firstContactCallCheckBox.setChecked(VALRTApplication.getPrefBoolean(this,
                VALRTApplication.C1CCBX));
        secondContactSmsCheckBox.setChecked(VALRTApplication.getPrefBoolean(this,
                VALRTApplication.C2TCBX));
        secondContactCallCheckBox.setChecked(VALRTApplication.getPrefBoolean(this,
                VALRTApplication.C2CCBX));
        thirdContactSmsCheckBox.setChecked(VALRTApplication.getPrefBoolean(this,
                VALRTApplication.C3TCBX));
        thirdContactCallCheckBox.setChecked(VALRTApplication.getPrefBoolean(this,
                VALRTApplication.C3CCBX));
        if (TextUtils
                .isEmpty(VALRTApplication.getPrefString(this, VALRTApplication.CONTACTONENAME))) {
            firstContactTextview.setText(R.string.tap_to_add_contact);
            firstContactButton.setBackgroundResource(R.drawable.img_plus_checkbox);
        } else {
            firstContactTextview.setText(VALRTApplication.getPrefString(this,
                    VALRTApplication.CONTACTONENAME));
            firstContactButton.setBackgroundResource(R.drawable.bg_minus_checkbox);
        }
        if (TextUtils
                .isEmpty(VALRTApplication.getPrefString(this, VALRTApplication.CONTACTTWONAME))) {
            secondContactTextView.setText(R.string.tap_to_add_contact);
            secondContactButton.setBackgroundResource(R.drawable.img_plus_checkbox);
        } else {
            secondContactTextView.setText(VALRTApplication.getPrefString(this,
                    VALRTApplication.CONTACTTWONAME));
            secondContactButton.setBackgroundResource(R.drawable.bg_minus_checkbox);
        }
        if (TextUtils.isEmpty(VALRTApplication.getPrefString(this,
                VALRTApplication.CONTACTTHREENAME))) {
            thirdContactTextView.setText(R.string.tap_to_add_contact);
            thirdContactButton.setBackgroundResource(R.drawable.img_plus_checkbox);
        } else {
            thirdContactTextView.setText(VALRTApplication.getPrefString(this,
                    VALRTApplication.CONTACTTHREENAME));
            thirdContactButton.setBackgroundResource(R.drawable.bg_minus_checkbox);
        }
        // Device Tracker On / off status
        if (VALRTApplication.getPrefBoolean(this, VALRTApplication.DEVICE_TRACKER_ALERT_TONE_STATUS)
                || VALRTApplication.getPrefBoolean(this, VALRTApplication.DEVICE_TRACKER_VIBRATION_STATUS)) {
            trackerStatusTextView.setText(R.string.tracker_on);
        } else {

            trackerStatusTextView.setText(R.string.tracker_off);
        }
    }
    /**
     * On click.
     *
     * @param v the v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_tracker_container_layout:
                intent = new Intent(SettingsActivity.this, DeviceTrackerActivity.class);
                startActivity(intent);
                break;
            case R.id.settings_personal_info_layout:
                intent = new Intent(SettingsActivity.this, PersonalInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.settings_back_button:
                if (VALRTApplication.getPrefBoolean(this, VALRTApplication.CONGRATULATION) == false) {
                    VALRTApplication.setPrefBoolean(this, VALRTApplication.AGREEMENT, false);
                    finish();
                } else {
                    if (backButtonStatus()) {
                        onBackPressed();
                    }
                }
                break;
            case R.id.settings_advance_settings_layout:
                intent = new Intent(SettingsActivity.this, AdvanceSettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.settings_first_contact_sms_checkbox:
                // Check box to send SMS for contact #1
                if (firstContactSmsCheckBox.isChecked()) {
                    if (TextUtils.isEmpty(VALRTApplication.getPrefString(this,
                            VALRTApplication.CONTACTONENAME))) {
                        alertDialogShow(getResources().getString(R.string.no_contatct_added));
                        firstContactSmsCheckBox.setChecked(false);
                    } else
                        VALRTApplication.setPrefBoolean(this, VALRTApplication.C1TCBX, true);
                } else {
                    VALRTApplication.setPrefBoolean(this, VALRTApplication.C1TCBX, false);
                }
                break;
            case R.id.settings_first_contact_call_checkbox:
                // Check box to make call for contact #1
                if (firstContactCallCheckBox.isChecked()) {
                    if (TextUtils.isEmpty(VALRTApplication.getPrefString(this,
                            VALRTApplication.CONTACTONENAME))) {
                        alertDialogShow(getResources().getString(R.string.no_contatct_added));
                        firstContactCallCheckBox.setChecked(false);
                    } else
                        VALRTApplication.setPrefBoolean(this, VALRTApplication.C1CCBX, true);
                } else {
                    VALRTApplication.setPrefBoolean(this, VALRTApplication.C1CCBX, false);
                }
                break;
            case R.id.settings_second_contact_sms_checkbox:
                // Check box to send SMS for contact #2
                if (secondContactSmsCheckBox.isChecked()) {
                    if (TextUtils.isEmpty(VALRTApplication.getPrefString(this,
                            VALRTApplication.CONTACTTWONAME))) {
                        alertDialogShow(getResources().getString(R.string.no_contatct_added));
                        secondContactSmsCheckBox.setChecked(false);
                    } else
                        VALRTApplication.setPrefBoolean(this, VALRTApplication.C2TCBX, true);
                } else {
                    VALRTApplication.setPrefBoolean(this, VALRTApplication.C2TCBX, false);
                }
                break;
            case R.id.settings_second_contact_call_checkbox:
                // Check box to make call for contact #2
                if (secondContactCallCheckBox.isChecked()) {
                    if (TextUtils.isEmpty(VALRTApplication.getPrefString(this,
                            VALRTApplication.CONTACTTWONAME))) {
                        alertDialogShow(getResources().getString(R.string.no_contatct_added));
                        secondContactCallCheckBox.setChecked(false);
                    } else
                        VALRTApplication.setPrefBoolean(this, VALRTApplication.C2CCBX, true);
                } else {
                    VALRTApplication.setPrefBoolean(this, VALRTApplication.C2CCBX, false);
                }
                break;
            case R.id.settings_third_contact_sms_checkbox:
                // Check box to send SMS for contact #3
                if (thirdContactSmsCheckBox.isChecked()) {
                    if (TextUtils.isEmpty(VALRTApplication.getPrefString(this,
                            VALRTApplication.CONTACTTHREENAME))) {
                        alertDialogShow(getResources().getString(R.string.no_contatct_added));
                        thirdContactSmsCheckBox.setChecked(false);
                    } else
                        VALRTApplication.setPrefBoolean(this, VALRTApplication.C3TCBX, true);
                } else {
                    VALRTApplication.setPrefBoolean(this, VALRTApplication.C3TCBX, false);
                }
                break;
            case R.id.settings_third_contact_call_checkbox:
                // Check box to make call for contact #3
                if (thirdContactCallCheckBox.isChecked()) {
                    if (TextUtils.isEmpty(VALRTApplication.getPrefString(this,
                            VALRTApplication.CONTACTTHREENAME))) {
                        alertDialogShow(getResources().getString(R.string.no_contatct_added));
                        thirdContactCallCheckBox.setChecked(false);
                    } else
                        VALRTApplication.setPrefBoolean(this, VALRTApplication.C3CCBX, true);
                } else {
                    VALRTApplication.setPrefBoolean(this, VALRTApplication.C3CCBX, false);
                }
                break;

            case R.id.settings_first_contact_button:
            case R.id.settings_first_contact_textview:
                if (TextUtils.isEmpty(VALRTApplication.getPrefString(this,
                        VALRTApplication.CONTACTONENAME))) {
                    isContactOne = true;
                    if (!VALRTApplication.getPrefString(SettingsActivity.this,
                            VALRTApplication.PERSONAL_INFO_COUNTRY_CODE).equalsIgnoreCase("US")) {
                        alertDialogShow(getResources().getString(R.string.dialing_format));
                    } else
                        doLaunchContactPicker();
                } else {
                    deleteContact(1);
                }
                break;
            case R.id.settings_second_contact_button:
            case R.id.settings_second_contact_textview:
                if (TextUtils.isEmpty(VALRTApplication.getPrefString(this,
                        VALRTApplication.CONTACTTWONAME))) {
                    isContactTwo = true;
                    if (!VALRTApplication.getPrefString(SettingsActivity.this,
                            VALRTApplication.PERSONAL_INFO_COUNTRY_CODE).equalsIgnoreCase("US")) {
                        alertDialogShow(getResources().getString(R.string.dialing_format));
                    } else
                        doLaunchContactPicker();
                } else {
                    deleteContact(2);
                }
                break;
            case R.id.settings_third_contact_button:
            case R.id.settings_third_contact_textview:
                if (TextUtils.isEmpty(VALRTApplication.getPrefString(this,
                        VALRTApplication.CONTACTTHREENAME))) {
                    isContactThree = true;
                    if (!VALRTApplication.getPrefString(SettingsActivity.this, VALRTApplication.PERSONAL_INFO_COUNTRY_CODE).equalsIgnoreCase("US")) {
                        alertDialogShow(getResources().getString(R.string.dialing_format));
                    } else
                        doLaunchContactPicker();
                } else {
                    deleteContact(3);
                }
                break;
            case R.id.settings_alertmsg_container_layout:
                intent = new Intent(SettingsActivity.this, AlertMessageActivity.class);
                startActivity(intent);
                break;

        }
    }
    /**
     * On back pressed.
     */
    @Override
    public void onBackPressed() {
        if (backButtonStatus()) {
            super.onBackPressed();
        }
    }
    /**
     * Do launch contact picker.
     */
    // Contact picker
    public void doLaunchContactPicker() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }
    /**
     * On activity result.
     *
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data the data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case (CONTACT_PICKER_RESULT):
                    int i = 0;
                    String phoneNumber = null;
                    String[] numbers = null;
                    String id = null;
                    ContentResolver cr = getContentResolver();
                    Cursor cur = cr.query(data.getData(), null, null, null, null);
                    if (cur.getCount() > 0) {
                        while (cur.moveToNext()) {
                            id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                            contactName = cur.getString(cur
                                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            if (Integer.parseInt(cur.getString(cur
                                    .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                                Cursor pCur = cr.query(
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                        new String[]{
                                                id
                                        }, null);
                                numbers = new String[pCur.getCount()];
                                while (pCur.moveToNext()) {
                                    phoneNumber = pCur
                                            .getString(pCur
                                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    numbers[i] = phoneNumber;
                                    i++;
                                }
                                pCur.close();

                                if (VALRTApplication.getPrefString(SettingsActivity.this, VALRTApplication.PERSONAL_INFO_COUNTRY_CODE).equalsIgnoreCase("US")) {
                                    if (numbers.length == 1) {
                                        editContactPopUp(removeSpecialCharacter(numbers[0]));
                                    } else {
                                        contactPopUpUS(numbers);
                                    }
                                } else {
                                    if (numbers.length == 1) {
                                        editContactPopUp(removeSpecialCharacter(numbers[0]));
                                    } else {
                                        contactPopUp(numbers);
                                    }
                                }
                            } else {
                                // No phone number in the contact.
                                alertDialogShow(getString(R.string.no_phonenumber_incontacts));
                            }
                        }
                    }
                    cur.close();
                    break;
            }
        }
    }
    /**
     * Back button status.
     *
     * @return true, if successful
     */
    public boolean backButtonStatus() {

        if ((VALRTApplication.getPrefBoolean(this, VALRTApplication.C1TCBX))
                || (VALRTApplication.getPrefBoolean(this, VALRTApplication.C2TCBX))
                || (VALRTApplication.getPrefBoolean(this, VALRTApplication.C3TCBX))
                || (VALRTApplication.getPrefBoolean(this, VALRTApplication.C1CCBX))
                || (VALRTApplication.getPrefBoolean(this, VALRTApplication.C2CCBX))
                || (VALRTApplication.getPrefBoolean(this, VALRTApplication.C3CCBX))) {
            backButton.setTextColor(getResources().getColor(R.color.violet_color));

            return true;
        } else {
            alertDialogShow(getString(R.string.valrt_settings), getString(R.string.alert_setting_info_required));
            return false;
        }
    }
    /**
     * To remove the special character.
     *
     * @param editPhoneNumber the edit phone number
     * @return the string
     */
    public String removeSpecialCharacter(String editPhoneNumber) {
        return editPhoneNumber = editPhoneNumber.replaceAll("[\\s\\-()]", "");
    }
    /**
     * Pop up picker to choose a contact number from multiple number if the country is US.
     *
     * @param phoneNumbers array of phone numbers.
     */
    public void contactPopUp(final String[] phoneNumbers) {
        AlertDialog.Builder ad = new AlertDialog.Builder(new ContextThemeWrapper(this,
                android.R.style.Theme_Holo_Light_Dialog));
        ad.setTitle(contactName);
        ad.setItems(phoneNumbers, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                contactNumber = phoneNumbers[which];
                contactNumber = removeSpecialCharacter(contactNumber);
                editContactPopUp(contactNumber);
            }
        });
        AlertDialog adb = ad.create();
        adb.show();
    }
    /**
     * Pop up picker to choose a contact number from multiple number.
     *
     * @param phoneNumbers array of phone numbers.
     */
    public void contactPopUpUS(final String[] phoneNumbers) {
        AlertDialog.Builder ad = new AlertDialog.Builder(new ContextThemeWrapper(this,
                android.R.style.Theme_Holo_Light_Dialog));
        ad.setTitle(contactName);
        ad.setItems(phoneNumbers, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                contactNumber = phoneNumbers[which];
                contactNumber = removeSpecialCharacter(contactNumber);
                editContactPopUp(contactNumber);
            }
        });
        AlertDialog adb = ad.create();
        adb.show();
    }
    /**
     * To save the selected connected in preference.
     *
     * @param selectedContactNumber the new contact
     */
    public void setContact(String selectedContactNumber) {
        if (isContactOne == true) {
            firstContactTextview.setText(contactName);
            VALRTApplication.setPrefString(SettingsActivity.this,
                    VALRTApplication.CONTACTONENAME, contactName);
            VALRTApplication.setPrefString(SettingsActivity.this,
                    VALRTApplication.CONTACTONENUMBER, selectedContactNumber);
            isContactOne = false;
            firstContactButton.setBackgroundResource(R.drawable.bg_minus_checkbox);
            VALRTApplication.setPrefBoolean(SettingsActivity.this, VALRTApplication.C1TCBX, true);
            firstContactSmsCheckBox.setChecked(true);
            firstContactButton.setBackgroundResource(R.drawable.bg_minus_checkbox);
            VALRTApplication.setPrefBoolean(SettingsActivity.this, VALRTApplication.C1CCBX, true);
            firstContactCallCheckBox.setChecked(true);
        } else if (isContactTwo == true) {
            secondContactTextView.setText(contactName);
            VALRTApplication.setPrefString(SettingsActivity.this,
                    VALRTApplication.CONTACTTWONAME, contactName);
            VALRTApplication.setPrefString(SettingsActivity.this,
                    VALRTApplication.CONTACTTWONUMBER, selectedContactNumber);
            isContactTwo = false;
            secondContactButton.setBackgroundResource(R.drawable.bg_minus_checkbox);
            VALRTApplication.setPrefBoolean(SettingsActivity.this, VALRTApplication.C2TCBX, true);
            secondContactSmsCheckBox.setChecked(true);
            secondContactButton.setBackgroundResource(R.drawable.bg_minus_checkbox);
            VALRTApplication.setPrefBoolean(SettingsActivity.this, VALRTApplication.C2CCBX, true);
            secondContactCallCheckBox.setChecked(true);

        } else if (isContactThree == true) {
            thirdContactTextView.setText(contactName);
            VALRTApplication.setPrefString(SettingsActivity.this, VALRTApplication.CONTACTTHREENAME, contactName);
            VALRTApplication.setPrefString(SettingsActivity.this, VALRTApplication.CONTACTTHREENUMBER, selectedContactNumber);
            isContactThree = false;
            thirdContactButton.setBackgroundResource(R.drawable.bg_minus_checkbox);
            VALRTApplication.setPrefBoolean(SettingsActivity.this, VALRTApplication.C3TCBX, true);
            thirdContactSmsCheckBox.setChecked(true);
            thirdContactButton.setBackgroundResource(R.drawable.bg_minus_checkbox);
            VALRTApplication.setPrefBoolean(SettingsActivity.this, VALRTApplication.C3CCBX, true);
            thirdContactCallCheckBox.setChecked(true);

        }
    }
    /**
     * To show the selected contact in editable pop up to add the country code.
     *
     * @param newPhoneNumber the new phone number
     */
    public void editContactPopUp(final String newPhoneNumber) {

        final Dialog dialog = new Dialog(this, R.style.ThemeWithCorners);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_contact);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        final EditText newNumbereEditText = (EditText) dialog
                .findViewById(R.id.edit_contact_text_edittext);

        if (VALRTApplication.getPrefString(SettingsActivity.this,
                VALRTApplication.PERSONAL_INFO_COUNTRY_CODE).equalsIgnoreCase("US")) {
            newNumbereEditText.setText(newPhoneNumber);
        } else {
            if (newPhoneNumber.startsWith(getString(R.string.plus_symbol))) {
                newNumbereEditText.setText(newPhoneNumber);
            } else {
                newNumbereEditText.setText(getString(R.string.plus_symbol) + newPhoneNumber);
            }
        }

        if (VALRTApplication.getPrefString(SettingsActivity.this,
                VALRTApplication.PERSONAL_INFO_COUNTRY_CODE).equalsIgnoreCase("US")) {
            View dividerView = (View) dialog.findViewById(R.id.edit_contact_divider);
            dividerView.setVisibility(View.VISIBLE);
            TextView messageTextView = (TextView) dialog.findViewById(R.id.edit_contact_message_textview);
            messageTextView.setVisibility(View.INVISIBLE);
        }

        newNumbereEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String editedPhoneNumber = newNumbereEditText.getText().toString().trim();
                    if (Utils.validateNumber(SettingsActivity.this, editedPhoneNumber)) {
                        setContact(editedPhoneNumber);
                        dialog.dismiss();
                        return true;
                    } else {
                        newNumbereEditText.setError(getString(R.string.edit_number_validation));
                    }
                }
                return false;
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();
    }
    /**
     * On destroy.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    /**
     * Alert dialog show.
     *
     * @param title the title
     * @param message the message
     */
    public void alertDialogShow(String title, String message) {
        final Dialog dialog = new Dialog(context, R.style.ThemeWithCorners);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_settingsinfo);
        dialog.setCancelable(false);
        TextView titleTextView = (TextView) dialog.findViewById(R.id.settingsinfo_title_textview);
        TextView messageTextView = (TextView) dialog
                .findViewById(R.id.settingsinfo_message_textview);
        Button okButton = (Button) dialog.findViewById(R.id.settingsinfo_ok_button);
        titleTextView.setText(title);
        messageTextView.setText(message);
        dialog.show();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    /**
     * Alert dialog show.
     *
     * @param message the message
     */
    // To show the alert info to the user.
    public void alertDialogShow(String message) {
        dialog = new Dialog(this, R.style.ThemeWithCorners);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_info);
        dialog.setCancelable(false);
        TextView messageTextView = (TextView) dialog.findViewById(R.id.info_title_textview);
        Button okButton = (Button) dialog.findViewById(R.id.info_ok_button);
        messageTextView.setText(message);
        dialog.show();
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!VALRTApplication.getPrefString(SettingsActivity.this,
                        VALRTApplication.PERSONAL_INFO_COUNTRY_CODE).equalsIgnoreCase("US")) {
                    if (isContactOne == true || isContactTwo == true || isContactThree == true) {
                        doLaunchContactPicker();
                    }
                }
            }
        });
        dialog.show();
    }
    /**
     * Delete contact.
     *
     * @param whichContact the which contact
     */
    // To remove the contacts from the emergency list
    public void deleteContact(final int whichContact) {
        dialog = new Dialog(this, R.style.ThemeWithCorners);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_alert_yes_no);
        dialog.setCancelable(false);

        TextView forgetTitleTextView = (TextView) dialog.findViewById(R.id.alert_title_textview);
        TextView forgetMessageTextView = (TextView) dialog
                .findViewById(R.id.alert_content_textview);
        forgetMessageTextView.setVisibility(View.GONE);
        Button forgetYesButton = (Button) dialog.findViewById(R.id.alert_yes_button);
        Button forgetNoButton = (Button) dialog.findViewById(R.id.alert_no_button);

        forgetTitleTextView.setText(getString(R.string.sure_remove_contact));
        forgetYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (whichContact == 1) {
                    VALRTApplication.setPrefString(SettingsActivity.this,
                            VALRTApplication.CONTACTONENAME, "");
                    VALRTApplication.setPrefString(SettingsActivity.this,
                            VALRTApplication.CONTACTONENUMBER, "");
                    firstContactButton.setBackgroundResource(R.drawable.img_plus_checkbox);
                    firstContactTextview.setText(R.string.tap_to_add_contact);

                    VALRTApplication.setPrefBoolean(SettingsActivity.this, VALRTApplication.C1TCBX,
                            false);
                    firstContactSmsCheckBox.setChecked(false);
                    VALRTApplication.setPrefBoolean(SettingsActivity.this, VALRTApplication.C1CCBX,
                            false);
                    firstContactCallCheckBox.setChecked(false);
                } else if (whichContact == 2) {
                    VALRTApplication.setPrefString(SettingsActivity.this,
                            VALRTApplication.CONTACTTWONAME, "");
                    VALRTApplication.setPrefString(SettingsActivity.this,
                            VALRTApplication.CONTACTTWONUMBER, "");
                    secondContactButton.setBackgroundResource(R.drawable.img_plus_checkbox);
                    secondContactTextView.setText(R.string.tap_to_add_contact);

                    VALRTApplication.setPrefBoolean(SettingsActivity.this, VALRTApplication.C2TCBX,
                            false);
                    secondContactSmsCheckBox.setChecked(false);
                    VALRTApplication.setPrefBoolean(SettingsActivity.this, VALRTApplication.C2CCBX,
                            false);
                    secondContactCallCheckBox.setChecked(false);
                } else if (whichContact == 3) {
                    VALRTApplication.setPrefString(
                            SettingsActivity.this, VALRTApplication.CONTACTTHREENAME, "");
                    VALRTApplication.setPrefString(
                            SettingsActivity.this, VALRTApplication.CONTACTTHREENUMBER, "");
                    thirdContactButton.setBackgroundResource(R.drawable.img_plus_checkbox);
                    thirdContactTextView.setText(R.string.tap_to_add_contact);

                    VALRTApplication.setPrefBoolean(SettingsActivity.this, VALRTApplication.C3TCBX, false);
                    thirdContactSmsCheckBox.setChecked(false);
                    VALRTApplication.setPrefBoolean(SettingsActivity.this, VALRTApplication.C3CCBX, false);
                    thirdContactCallCheckBox.setChecked(false);
                }
                dialog.dismiss();
            }
        });
        forgetNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
