package com.mattprecious.otherdevice.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.mattprecious.otherdevice.R;
import com.mattprecious.otherdevice.fragment.MainFragment;
import com.mattprecious.otherdevice.util.Preferences;
import com.viewpagerindicator.TitlePageIndicator;

public class OtherDevice extends SherlockFragmentActivity {
    private final static String TAG = "OtherDevice";
    
    private MyPagerAdapter adapter;
    private ViewPager pager;
    private TitlePageIndicator indicator;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if ("holo_light".equals(Preferences.getTheme(this))) {
            setTheme(R.style.Sherlock___Theme_Light);
        }
        
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.pager);
        
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(1);
        
        indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }
    
    private class MyPagerAdapter extends FragmentPagerAdapter {
        private final int NUM_FRAGMENTS = 1;
        private Fragment[] FRAGMENTS = new Fragment[NUM_FRAGMENTS];
        private String[] TITLES = new String[NUM_FRAGMENTS];

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            
            TITLES[0] = "Main";
            FRAGMENTS[0] = new MainFragment();
        }

        @Override
        public Fragment getItem(int position) {
            if (position >=0 && position < NUM_FRAGMENTS) {
                return FRAGMENTS[position];
            }
            
            Log.e(TAG, "invalid getItem position: " + position);
            return null;
        }
        
        @Override
        public CharSequence getPageTitle(int position) {
            if (position >=0 && position < NUM_FRAGMENTS) {
                return TITLES[position];
            }
            
            Log.e(TAG, "invalid getPageTitle position: " + position);
            return null;
        }

        @Override
        public int getCount() {
            return NUM_FRAGMENTS;
        }

    }
}
