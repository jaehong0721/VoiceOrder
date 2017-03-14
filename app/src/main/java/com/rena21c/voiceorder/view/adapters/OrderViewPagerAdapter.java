package com.rena21c.voiceorder.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.activities.OrderDetailActivity;
import com.rena21c.voiceorder.etc.PreferenceManager;
import com.rena21c.voiceorder.model.Item;
import com.rena21c.voiceorder.model.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Order> orders = new ArrayList<>();

    public OrderViewPagerAdapter(Context context) {
        super();
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        initBeforeAcceptOrderData();
        initAfterAcceptOrderData();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = getView(position);
        if (view != null) {
            container.addView(view);
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    //=============================================
    public View getView(int position) {
        if (orders.get(position).items == null)
            return getBeforeAcceptOrderView(position);
        else
            return getAfterAcceptOrderView(position);
    }

    private View getBeforeAcceptOrderView(int position) {

        View view = layoutInflater.inflate(R.layout.before_accept_order_view, null, false);
        TextView tvTimeStamp = (TextView) view.findViewById(R.id.tvTimeStamp);
        tvTimeStamp.setText(orders.get(position).timeStamp);
        return view;
    }

    private View getAfterAcceptOrderView(final int position) {

        View view = layoutInflater.inflate(R.layout.after_accept_order_view, null, false);
        TextView tvTimeStamp = (TextView) view.findViewById(R.id.tvTimeStamp);
        TextView tvItemList = (TextView) view.findViewById(R.id.tvItemList);
        TextView tvVendorList = (TextView) view.findViewById(R.id.tvVendorList);

        tvTimeStamp.setText(orders.get(position).timeStamp);
        tvItemList.setText(orders.get(position).makeItemList());
        tvVendorList.setText(orders.get(position).makeVendorList());

        TextView tvDetail = (TextView) view.findViewById(R.id.tvDetail);
        tvDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("time", orders.get(position).timeStamp);
                intent.putExtra("items", orders.get(position).items);
                context.startActivity(intent);
            }
        });
        return view;
    }

    private void initAfterAcceptOrderData() {
        //server or db에서 데이터 바인딩(지금은 더미데이터)
        Item item1 = new Item("양파", "단", "송군식품", 3);
        Item item2 = new Item("고구마", "포대", "미리네", 1);
        Item item3 = new Item("감자", "포대", "유현통상", 2);
        Item item4 = new Item("된장", "박스", "금자야채가게", 6);

        ArrayList<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);


        orders.add(new Order("00:01  03.12", null));
        orders.add(new Order("00:02  03.12", items));
        orders.add(new Order("00:03  03.12", items));
        orders.add(new Order("00:04  03.12", items));
        orders.add(new Order("00:05  03.12", null));
    }

    private void initBeforeAcceptOrderData() {
        //오퍼레이터가 접수 전 주문들
        ArrayList<String> timeList = PreferenceManager.retriveTimeList(context);
        if (timeList != null) {
            for (String time : timeList) {
                long time2 = Long.parseLong(time);
                SimpleDateFormat dayTime = new SimpleDateFormat("MM.dd  HH:mm");
                String timeStamp = dayTime.format(new Date(time2));
                orders.add(new Order(timeStamp, null));
            }
        }
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void add(long time) {
        SimpleDateFormat dayTime = new SimpleDateFormat("MM.dd  HH:mm");
        String timeStamp = dayTime.format(new Date(time));
        orders.add(0, new Order(timeStamp, null));
        notifyDataSetChanged();
    }

    //This way, when you call notifyDataSetChanged(), the view pager will remove all views and reload them all. As so the reload effect is obtained.
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
