package com.rena21c.voiceorder.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.activities.OrderDetailActivity;
import com.rena21c.voiceorder.model.Order;
import com.rena21c.voiceorder.model.VendorInfo;
import com.rena21c.voiceorder.model.VoiceRecord;
import com.rena21c.voiceorder.util.FileNameUtil;
import com.rena21c.voiceorder.view.components.OrderViewPagerLayoutHolder;

import java.util.ArrayList;
import java.util.HashMap;

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
        if (orders.get(position).orderState == Order.IN_PROGRESS) {
            return getBeforeAcceptOrderView(position);
        } else if(orders.get(position).orderState == Order.ACCEPTED) {
            return getAfterAcceptOrderView(position);
        } else
            return getFailedOrderView(position);
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

    private View getFailedOrderView(int position) {
        View view = layoutInflater.inflate(R.layout.failed_order_view, null, false);
        TextView tvTimeStamp = (TextView) view.findViewById(R.id.tvTimeStamp);
        tvTimeStamp.setText(orders.get(position).timeStamp);
        return view;
    }

    public void add(String timeStamp) {
        orders.add(0, new Order(Order.IN_PROGRESS, timeStamp, null));
        notifyDataSetChanged();
    }

    public void replaceToAcceptedOrder(DataSnapshot dataSnapshot, OrderViewPagerLayoutHolder.ReplaceOrderFinishedListener listener) {
        GenericTypeIndicator objectMapType = new GenericTypeIndicator<HashMap<String, VoiceRecord>>() {};
        HashMap<String, VoiceRecord> objectMap = (HashMap) dataSnapshot.getValue(objectMapType);

        String timeStamp = FileNameUtil.getTimeFromFileName(dataSnapshot.getKey());
        int position = 0;

        for(int i=0; i<orders.size(); i++) {
            if(orders.get(i).timeStamp.equals(timeStamp) && orders.get(i).orderState == Order.IN_PROGRESS) {
                getVendorName(objectMap);
                orders.get(i).itemHashMap = objectMap;
                orders.get(i).orderState = Order.ACCEPTED;
                position = i;
                break;
            }
        }
        notifyDataSetChanged();
        listener.onFinish(position);
    }

    public void replaceToFailedOrder(String fileName, OrderViewPagerLayoutHolder.ReplaceOrderFinishedListener listener) {
        String timeStamp = FileNameUtil.getTimeFromFileName(fileName);
        int position = 0;

        for(int i=0; i<orders.size(); i++) {
            if(orders.get(i).timeStamp.equals(timeStamp) && orders.get(i).orderState == Order.IN_PROGRESS) {
                orders.get(i).orderState = Order.FAILED;
                position = i;
                break;
            }
        }
        notifyDataSetChanged();
        listener.onFinish(position);
    }

    private HashMap getVendorName(final HashMap<String, VoiceRecord> itemHashMap) {
        for (final String vendorPhoneNumber : itemHashMap.keySet()) {
            FirebaseDatabase.getInstance().getReference().child("vendors")
                    .child(vendorPhoneNumber)
                    .child("info")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override public void onDataChange(DataSnapshot dataSnapshot) {
                            VendorInfo vendorInfo = dataSnapshot.getValue(VendorInfo.class);
                            itemHashMap.put(vendorInfo.vendorName, itemHashMap.remove(vendorPhoneNumber));
                        }

                        @Override public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(context, databaseError.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        return itemHashMap;
    }
}
