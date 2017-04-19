package com.rena21c.voiceorder.view.adapters;

import android.support.v4.view.ViewPager;

public abstract class SimpleViewPagerSelectedListener implements ViewPager.OnPageChangeListener{
    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override public void onPageScrollStateChanged(int state) {

    }

    @Override abstract public void onPageSelected(int position);
}
