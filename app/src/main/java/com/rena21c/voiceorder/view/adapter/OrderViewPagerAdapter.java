package com.rena21c.voiceorder.view.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.rena21c.voiceorder.etc.ViewPagerItemManager;

public class OrderViewPagerAdapter extends PagerAdapter{

    private ViewPagerItemManager viewPagerItemManager;

    public OrderViewPagerAdapter(ViewPagerItemManager viewPagerItemManager) {
        super();
        this.viewPagerItemManager = viewPagerItemManager;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = viewPagerItemManager.getView(viewPagerItemManager.getItems().get(position));
        if(view != null) {
            container.addView(view);
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View)object;
        container.removeView(view);
    }

    @Override
    public int getCount() {
        return viewPagerItemManager.getItems().size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
