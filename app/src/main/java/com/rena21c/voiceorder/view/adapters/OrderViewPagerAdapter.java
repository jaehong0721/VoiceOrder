package com.rena21c.voiceorder.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rena21c.voiceorder.activities.OrderDetailActivity;
import com.rena21c.voiceorder.etc.RecordedFileManager;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.model.VendorInfo;
import com.rena21c.voiceorder.model.VoiceRecord;
import com.rena21c.voiceorder.util.FileNameUtil;
import com.rena21c.voiceorder.viewmodel.AcceptedOrderPage;
import com.rena21c.voiceorder.viewmodel.EmptyOrderPage;
import com.rena21c.voiceorder.viewmodel.OrderPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OrderViewPagerAdapter extends PagerAdapter {

    private final FirebaseDbManager dbManager;
    private final RecordedFileManager recordedFileManager;

    private Context context;
    private LayoutInflater layoutInflater;

    private ArrayList<String> timeStampList;
    private Map<String, OrderPage> orderPageMap;

    private ItemCountChangedListener itemCountChangedListener;

    OrderPage.OnClickDetailsOrderPageListener onClickDetailsOrderPageListener;

    public interface ItemCountChangedListener {
        void itemCountChange(int count);
    }

    public OrderViewPagerAdapter(final Context context,
                                 FirebaseDbManager dbManager,
                                 RecordedFileManager recordedFileManager,
                                 ItemCountChangedListener itemCountChangedListener) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.context = context;
        this.timeStampList = new ArrayList<>();
        this.orderPageMap = new HashMap<>();

        this.dbManager = dbManager;
        this.recordedFileManager = recordedFileManager;

        this.itemCountChangedListener = itemCountChangedListener;

        onClickDetailsOrderPageListener = new OrderPage.OnClickDetailsOrderPageListener() {
            @Override public void onClickDetailsOrderPage(String timeStamp, HashMap<String, VoiceRecord> itemHashMap) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("timeStamp", timeStamp);
                intent.putExtra("itemHashMap", itemHashMap);
                context.startActivity(intent);
            }
        };
        itemCountChangedListener.itemCountChange(timeStampList.size());
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
        return timeStampList.size();
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

    public void addTimeStamp(String fileName) {
        String timeStamp = FileNameUtil.getTimeFromFileName(fileName);
        timeStampList.add(timeStamp);
        Collections.sort(timeStampList, Collections.<String>reverseOrder());
        if (orderPageMap.containsKey(timeStamp)) {
            // Do Nothing
        } else {
            orderPageMap.put(timeStamp, new EmptyOrderPage(timeStamp, fileName, recordedFileManager.isStored(fileName)));
        }
        notifyDataSetChanged();
        itemCountChangedListener.itemCountChange(timeStampList.size());
    }


    public int addOrder(String timeStamp, HashMap<String, VoiceRecord> newItemHashMap) {
        int position = getPosition(timeStamp);
        replaceNumberKeyToVendorNameKey(newItemHashMap);
        orderPageMap.put(timeStamp, new AcceptedOrderPage(timeStamp, newItemHashMap));

        Log.d("", "orderMap: " + newItemHashMap);

        notifyDataSetChanged();
        itemCountChangedListener.itemCountChange(timeStampList.size());
        return position;
    }

    public void remove(String timeStamp) {
        int position = getPosition(timeStamp);
        timeStampList.remove(position);
        orderPageMap.remove(timeStamp);

        notifyDataSetChanged();
        itemCountChangedListener.itemCountChange(timeStampList.size());
    }


    public View getView(int position) {
        String timeStamp = timeStampList.get(position);
        return orderPageMap.get(timeStamp).getView(layoutInflater, onClickDetailsOrderPageListener);
    }

    public int replaceToAcceptedOrder(String timeStamp, HashMap<String, VoiceRecord> newItemHashMap) {
        replaceNumberKeyToVendorNameKey(newItemHashMap);

        int position = getPosition(timeStamp);

        orderPageMap.put(timeStamp, new AcceptedOrderPage(timeStamp, newItemHashMap));

        notifyDataSetChanged();
        return position;
    }

    private void replaceNumberKeyToVendorNameKey(final HashMap<String, VoiceRecord> itemHashMap) {
        for (final String vendorPhoneNumber : itemHashMap.keySet()) {
            dbManager.getVendorInfo(vendorPhoneNumber, new ValueEventListener() {
                @Override public void onDataChange(DataSnapshot dataSnapshot) {
                    VendorInfo vendorInfo = dataSnapshot.getValue(VendorInfo.class);
                    itemHashMap.put(vendorInfo.vendorName, itemHashMap.remove(vendorPhoneNumber));
                    notifyDataSetChanged();
                }

                @Override public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(context, databaseError.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private int getPosition(String timeStamp) {
        for (int i = 0; i < timeStampList.size(); i++) {
            if (timeStampList.get(i).equals(timeStamp)) {
                return i;
            }
        }
        return -1;
    }
}
