package com.rena21c.voiceorder.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.activities.OrderDetailActivity;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.model.Order;
import com.rena21c.voiceorder.model.Order.OrderState;
import com.rena21c.voiceorder.model.VendorInfo;
import com.rena21c.voiceorder.model.VoiceRecord;
import com.rena21c.voiceorder.util.FileNameUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class OrderViewPagerAdapter extends PagerAdapter {

    private final FirebaseDbManager dbManager;
    private Context context;
    private LayoutInflater layoutInflater;

    // TODO: Order 대신 TimeStamp(String)으로 변경
    private ArrayList<Order> orders;

    private Map<String, Order> orderMap;

    private ItemCountChangedListener itemCountChangedListener;

    public interface ItemCountChangedListener {
        void itemCountChange(int count);
    }

    public OrderViewPagerAdapter(Context context, FirebaseDbManager dbManager, ItemCountChangedListener itemCountChangedListener) {
        this.context = context;
        this.orders = new ArrayList<>();
        this.orderMap = new HashMap<>();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dbManager = dbManager;
        this.itemCountChangedListener = itemCountChangedListener;
        itemCountChangedListener.itemCountChange(orders.size());
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = getView(position);
        container.addView(view);
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

    public View getView(int position) {
        String timeStamp = orders.get(position).timeStamp;
        if (!orderMap.containsKey(timeStamp)) {
            return getEmptyOrderView(position);
        } else if (orderMap.get(timeStamp).orderState == OrderState.ACCEPTED) {
            Order order = orderMap.get(timeStamp);
            return getAfterAcceptOrderView(order);
        } else
            return getFailedOrderView(position);
    }

    private View getEmptyOrderView(int position) {
        View view = layoutInflater.inflate(R.layout.before_accept_order_view, null, false);
        TextView tvTimeStamp = (TextView) view.findViewById(R.id.tvTimeStamp);
        tvTimeStamp.setText(orders.get(position).timeStamp);
        return view;
    }

    private View getAfterAcceptOrderView(final Order order) {
        View view = layoutInflater.inflate(R.layout.after_accept_order_view, null, false);
        TextView tvTimeStamp = (TextView) view.findViewById(R.id.tvTimeStamp);
        TextView tvItemList = (TextView) view.findViewById(R.id.tvItemList);
        TextView tvVendorList = (TextView) view.findViewById(R.id.tvVendorList);

        tvTimeStamp.setText(order.timeStamp);
        tvItemList.setText(order.makeItemList());
        tvVendorList.setText(order.makeVendorList());

        TextView tvDetail = (TextView) view.findViewById(R.id.tvDetail);
        tvDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("timeStamp", order.timeStamp);
                intent.putExtra("itemHashMap", order.itemHashMap);
                context.startActivity(intent);
            }
        });
        return view;
    }

    private View getFailedOrderView(int position) {
        View view = layoutInflater.inflate(R.layout.failed_order_view, null, false);
        TextView tvTimeStamp = (TextView) view.findViewById(R.id.tvTimeStamp);
        tvTimeStamp.setText(orders.get(position).timeStamp);
        return view;
    }

    public void addEmptyOrderView(Order emptyOrder) {
        orders.add(0, emptyOrder);
        Collections.sort(orders, new Comparator<Order>() {
            @Override public int compare(Order o1, Order o2) {
                return o2.timeStamp.compareTo(o1.timeStamp);
            }
        });
        itemCountChangedListener.itemCountChange(orders.size());
        notifyDataSetChanged();
    }

    public int addOrder(String fileName, HashMap<String, VoiceRecord> newItemHashMap) {
        String timeStamp = FileNameUtil.getTimeFromFileName(fileName);
        replaceNumberKeyToVendorNameKey(newItemHashMap);

        int position = getPosition(timeStamp);

        orderMap.put(timeStamp, new Order(OrderState.ACCEPTED, timeStamp, newItemHashMap));

        Log.d("", "orderMap: " + orderMap);

        notifyDataSetChanged();
        return position;
    }

    public int replaceToAcceptedOrder(String fileName, HashMap<String, VoiceRecord> newItemHashMap) {
        String timeStamp = FileNameUtil.getTimeFromFileName(fileName);
        replaceNumberKeyToVendorNameKey(newItemHashMap);

        int position = getPosition(timeStamp);

        if (position != -1) {
            Order order = orders.get(position);
            order.itemHashMap = newItemHashMap;
            order.orderState = OrderState.ACCEPTED;
        }

        notifyDataSetChanged();
        return position;
    }

    public int replaceToFailedOrder(String fileName) {
        String timeStamp = FileNameUtil.getTimeFromFileName(fileName);

        int position = getPosition(timeStamp);
        Order order = orders.get(position);
        order.orderState = OrderState.FAILED;

        notifyDataSetChanged();
        return position;
    }

    private void replaceNumberKeyToVendorNameKey(final HashMap<String, VoiceRecord> itemHashMap) {
        for (final String vendorPhoneNumber : itemHashMap.keySet()) {
            dbManager.getVendorInfo(vendorPhoneNumber, new ValueEventListener() {
                @Override public void onDataChange(DataSnapshot dataSnapshot) {
                    VendorInfo vendorInfo = dataSnapshot.getValue(VendorInfo.class);
                    itemHashMap.put(vendorInfo.vendorName, itemHashMap.remove(vendorPhoneNumber));
                }

                @Override public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(context, databaseError.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private int getPosition(String timeStamp) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            order.match(timeStamp);
            return i;
        }
        return -1;
    }
}
