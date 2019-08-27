package com.vsnmobil.valrt.adapters;

import java.util.List;
//import
import android.support.v4.app.Fragment;
//import
import android.support.v4.app.FragmentManager;
//import
import android.support.v4.app.FragmentPagerAdapter;
/**
 * PagerAdapter.java
 * <p/>
 * This adapter is to load the list of fragment inside a fragment activity and show it
 * in swipe view.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    /** The fragments. */
    private List<Fragment> fragments;
    /**
     * Instantiates a new pager adapter.
     *
     * @param fm the fm
     * @param fragments the fragments
     */
    public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }
    /* (non-Javadoc)
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
        return this.fragments.size();
    }
}
