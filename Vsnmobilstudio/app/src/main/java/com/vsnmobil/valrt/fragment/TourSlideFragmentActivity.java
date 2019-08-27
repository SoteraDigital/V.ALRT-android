package com.vsnmobil.valrt.fragment;

import java.util.List;
//import
import java.util.Vector;
//import
import android.os.Bundle;
//import
import android.support.v4.app.Fragment;
//import
import android.support.v4.app.FragmentActivity;
//import
import android.support.v4.view.ViewPager;
//import
import com.vsnmobil.valrt.BuildConfig;
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.adapters.PagerAdapter;
//import
import com.vsnmobil.valrt.views.CirclePageIndicator;
/**
 * TourSlideFragmentActivity.java
 * This is the main fragment activity to load the tour fragments in it.
 *
 */
public class TourSlideFragmentActivity extends FragmentActivity {
    /**
     * maintains the pager adapter.
     */
    private PagerAdapter mPagerAdapter;
    /**
     * The indicator.
     */
    private CirclePageIndicator indicator;
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.fragment_activity_tourslide);
        indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        // Initialize the pager
        this.initialisePaging();
    }
    /**
     * Initialize the fragments to be paged.
     */
    private void initialisePaging() {
        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, SlideOneFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, SlideTwoFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, SlideThreeFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, SlideFourFragment.class.getName()));
        // If V.ALRT build variant then display video fragment
        if (BuildConfig.FLAVOR.equals("VALRT")) {
            fragments.add(Fragment.instantiate(this, SlideVideoFragment.class.getName()));
        }
        fragments.add(Fragment.instantiate(this, SlideLetsStartFragment.class.getName()));
        this.mPagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);
        // ViewPager to load the fragment and to swipe.
        ViewPager pager = (ViewPager) super.findViewById(R.id.tourslide_fragment_viewpager);
        pager.setAdapter(this.mPagerAdapter);
        indicator.setViewPager(pager);
    }
}
