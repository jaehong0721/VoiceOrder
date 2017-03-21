package com.rena21c.voiceorder.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.activities.OrderDetailActivity;
import com.rena21c.voiceorder.model.Order;

import java.util.ArrayList;

public class OrderViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Order> orders;

    public OrderViewPagerAdapter(Context context) {
        super();
        this.context = context;
        this.orders = App.getApplication(context.getApplicationContext()).orders;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    //This way, when you call notifyDataSetChanged(), the view pager will remove all views and reload them all. As so the reload effect is obtained.
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    //=============================================
    public View getView(int position) {
        if (orders.get(position).itemHashMap == null)
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
                intent.putExtra("timeStamp", orders.get(position).timeStamp);
                intent.putExtra("itemHashMap", orders.get(position).itemHashMap);
                context.startActivity(intent);
            }
        });
        return view;
    }

    public void add(String timeStamp) {
        orders.add(0, new Order(timeStamp));
        notifyDataSetChanged();
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }
}
