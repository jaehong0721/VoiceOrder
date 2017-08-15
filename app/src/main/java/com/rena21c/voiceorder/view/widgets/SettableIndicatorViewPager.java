package com.rena21c.voiceorder.view.widgets;


import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class SettableIndicatorViewPager extends ViewPager {
    private ViewPagerIndicator indicator;

    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override public void onChanged() {
            PagerAdapter adapter = getAdapter();
            if(adapter != null && indicator != null) {
                indicator.changeDot(adapter.getCount());

                if(adapter.getCount()==1) indicator.selectDot(0);
            }
        }
    };

    public SettableIndicatorViewPager(Context context) {
        this(context, null);
    }

    public SettableIndicatorViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        addOnPageChangeListener(new OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override public void onPageSelected(int position) {
                if(indicator == null) return;
                indicator.selectDot(position);
            }

            @Override public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        adapter.registerDataSetObserver(dataSetObserver);
    }

    public void setIndicator(final ViewPagerIndicator indicator) {
        this.indicator = indicator;
    }
}
