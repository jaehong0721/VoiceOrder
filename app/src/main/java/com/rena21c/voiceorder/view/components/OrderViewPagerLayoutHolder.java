package com.rena21c.voiceorder.view.components;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.model.VoiceRecord;
import com.rena21c.voiceorder.view.adapters.OrderViewPagerAdapter;
import com.rena21c.voiceorder.view.widgets.ViewPagerIndicator;

import java.util.HashMap;


public class OrderViewPagerLayoutHolder {

    private OrderViewPagerAdapter orderViewPagerAdapter;
    private ViewPager orderViewPager;
    private ViewPagerIndicator viewPagerIndicator;
    private View view;

    public interface ReplaceOrderFinishedListener {
        void onFinish(int position);
    }

    public OrderViewPagerLayoutHolder(Context context, ViewGroup rootView, FirebaseDbManager dbManager) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.layout_component_order_view_pager, rootView, false);

        viewPagerIndicator = (ViewPagerIndicator) view.findViewById(R.id.viewPagerIndicator);
        orderViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        orderViewPagerAdapter = new OrderViewPagerAdapter(context, App.getApplication(context.getApplicationContext()).orders, dbManager);

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
        orderViewPagerAdapter.addEmptyRecordView(fileName);
        viewPagerIndicator.addDot();
        orderViewPager.setCurrentItem(0);
    }

    public void replaceToAcceptedOrder(DataSnapshot dataSnapshot) {
        GenericTypeIndicator objectMapType = new GenericTypeIndicator<HashMap<String, VoiceRecord>>() {};
        HashMap<String, VoiceRecord> objectMap = (HashMap) dataSnapshot.getValue(objectMapType);
        String key = dataSnapshot.getKey();
        orderViewPagerAdapter.replaceToAcceptedOrder(key, objectMap, new ReplaceOrderFinishedListener() {
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
