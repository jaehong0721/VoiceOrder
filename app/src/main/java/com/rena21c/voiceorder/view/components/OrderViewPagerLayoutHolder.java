package com.rena21c.voiceorder.view.components;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.view.adapters.OrderViewPagerAdapter;
import com.rena21c.voiceorder.view.widgets.ViewPagerIndicator;


public class OrderViewPagerLayoutHolder {

    private OrderViewPagerAdapter orderViewPagerAdapter;
    private ViewPager orderViewPager;
    private ViewPagerIndicator viewPagerIndicator;
    private View view;

    public interface ReplaceOrderFinishedListener {
        void onFinish(int position);
    }

    public OrderViewPagerLayoutHolder(Context context, ViewGroup rootView) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.layout_component_order_view_pager, rootView, false);

        viewPagerIndicator = (ViewPagerIndicator) view.findViewById(R.id.viewPagerIndicator);
        orderViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        orderViewPagerAdapter = new OrderViewPagerAdapter(context);

        viewPagerIndicator.createDot(orderViewPagerAdapter.getCount());
        viewPagerIndicator.selectDot(0);

        orderViewPager.setAdapter(orderViewPagerAdapter);
        orderViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

    public View getView() {
        return view;
    }

    public void addOrder(String fileName) {
        orderViewPagerAdapter.add(fileName);
        viewPagerIndicator.addDot();
        orderViewPager.setCurrentItem(0);
    }

    public void replaceToAcceptedOrder(DataSnapshot dataSnapshot) {
        orderViewPagerAdapter.replaceToAcceptedOrder(dataSnapshot, new ReplaceOrderFinishedListener() {
            @Override
            public void onFinish(int position) {
                orderViewPager.setCurrentItem(position, false);
            }
        });
    }

    public void replaceToFailedOrder(String fileName) {
        orderViewPagerAdapter.replaceToFailedOrder(fileName, new ReplaceOrderFinishedListener() {
            @Override
            public void onFinish(int position) {
                orderViewPager.setCurrentItem(position);
            }
        });
    }

}
