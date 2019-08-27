package com.vsnmobil.valrt.fragment;

import java.util.ArrayList;
//import
import java.util.HashMap;
//import
import android.os.Bundle;
//import
import android.support.v4.app.FragmentActivity;
//import
import android.support.v4.view.ViewPager;
//import
import android.view.View;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.adapters.DashboardFPAdapter;
//import
import com.vsnmobil.valrt.storage.DatabaseHelper;
//import
import com.vsnmobil.valrt.views.CirclePageIndicator;
/**
 * DashboardFPAdapter.java
 *
 * This class is used to load the fragments in to a single fragment activity
 * with swipe action.
 *
 */
public class DashboardFragmentActivity extends FragmentActivity {
    /** The view pager. */
    private ViewPager viewPager;
    /** The dashboard adapter. */
    private DashboardFPAdapter dashboardAdapter;
    /** The db helper. */
    private DatabaseHelper dbHelper;
    /** The paired device. */
    private ArrayList<HashMap<String, String>> pairedDevice;
    /** The indicator. */
    private CirclePageIndicator indicator;
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity_dashboard);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        //Database helper instance.
        dbHelper = new DatabaseHelper(this);
        pairedDevice = dbHelper.getPairedConnectedDeviceList();
        //Adapter to load the fragment in swipe view.
        dashboardAdapter = new DashboardFPAdapter(getSupportFragmentManager(), pairedDevice);
        viewPager.setAdapter(dashboardAdapter);
        indicator.setViewPager(viewPager);
        // If we have only one device hide the indicator.
        if (pairedDevice.size() == 1)
            indicator.setVisibility(View.GONE);
    }

}
