package com.rena21c.voiceorder.view.components;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.view.adapters.OrderViewPagerAdapter;
import com.rena21c.voiceorder.view.widgets.ViewPagerIndicator;


public class OrderViewPagerLayout extends RelativeLayout {

    private static OrderViewPagerLayout INSTANCE;

    private OrderViewPagerAdapter orderViewPagerAdapter;
    private ViewPager viewPager;
    private ViewPagerIndicator viewPagerIndicator;
    private View view;

    private OrderViewPagerLayout(Context context, ViewGroup rootView) {
        super(context);
        init(context, rootView);
    }

    private void init(Context context, ViewGroup rootView) {

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.layout_component_order_view_pager, rootView, false);

        viewPagerIndicator = (ViewPagerIndicator)view.findViewById(R.id.viewPagerIndicator);
        viewPager = (ViewPager)view.findViewById(R.id.viewPager);
        orderViewPagerAdapter = new OrderViewPagerAdapter(context);

        viewPagerIndicator.createDot(orderViewPagerAdapter.getOrders().size());
        viewPagerIndicator.selectDot(0);

        viewPager.setAdapter(orderViewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageScrollStateChanged(int state) {}
            @Override
            public void onPageSelected(int position) {
                viewPagerIndicator.selectDot(position);
            }
        });
    }

    public static OrderViewPagerLayout getInstance(Context context, ViewGroup rootView) {
        if(INSTANCE == null) {
            INSTANCE = new OrderViewPagerLayout(context, rootView);
            return INSTANCE;
        }
        else {
            return INSTANCE;
        }
    }

    public View getView() {
        return view;
    }
}
