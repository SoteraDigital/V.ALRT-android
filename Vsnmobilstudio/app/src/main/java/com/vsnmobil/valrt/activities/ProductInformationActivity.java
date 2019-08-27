package com.vsnmobil.valrt.activities;

import java.util.ArrayList;
//import
import java.util.HashMap;
//import
import android.app.Activity;
//import
import android.content.pm.PackageManager.NameNotFoundException;
//import
import android.os.Bundle;
//import
import android.view.View;
//import
import android.view.View.OnClickListener;
//import
import android.widget.Button;
//import
import android.widget.ListView;
//import
import android.widget.TextView;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.adapters.ProductInformationAdapter;
//import
import com.vsnmobil.valrt.storage.DatabaseHelper;

/**
 * ProductInformationActivity.java
 * <p/>
 * This activity will show the APP version and information about the product like
 * model number,serial number and puck's MAC address.
 */
public class ProductInformationActivity extends Activity {
    /** The product information list. */
    private ListView productInformationList;
    /** The product information adapter. */
    private ProductInformationAdapter productInformationAdapter;
    /** The device product info list. */
    private ArrayList<HashMap<String, String>> deviceProductInfoList;
    /** The db helper. */
    private DatabaseHelper dbHelper;
    /** The back button. */
    private Button backButton;
    /** The app version text view. */
    private TextView appVersionTextView;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productinformation);
        dbHelper = new DatabaseHelper(this);
        productInformationList = (ListView) findViewById(R.id.productinfo_listview);
        backButton = (Button) findViewById(R.id.productinfo_back_button);
        appVersionTextView = (TextView) findViewById(R.id.productinfo_appversion_textview);
        //To disable the scroll of the listview.
        productInformationList.setScrollContainer(false);
        // To read the current APP version and set in text view.
        try {
            String versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            appVersionTextView.setText(versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        //Call the adapter to set the product information list.
        setProductInformation();
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //To go back to previous screen.
                finish();

            }
        });
    }
    /**
     * Sets the product information.
     */
    // To set the product information in a list view.
    private void setProductInformation() {
        //Check the paired device count
        deviceProductInfoList = dbHelper.getProductInformation();
        if (deviceProductInfoList.size() != 0) {
            //Load the device serial number and MAC address of the connected device from database.
            productInformationAdapter = new ProductInformationAdapter(ProductInformationActivity.this, deviceProductInfoList);
            //set the values to the product information adapter.
            productInformationList.setAdapter(productInformationAdapter);
        }
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
