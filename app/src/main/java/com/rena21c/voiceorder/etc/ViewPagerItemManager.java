package com.rena21c.voiceorder.etc;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.rena21c.voiceorder.R;

import java.util.ArrayList;

public class ViewPagerItemManager {

    public static final int BEFORE_ACCEPT = 0;
    public static final int AFTER_ACCEPT = 1;

    private LayoutInflater layoutInflater;
    private ArrayList<Integer> items = new ArrayList<>();

    public ViewPagerItemManager(Context context) {
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        items.add(0);
        items.add(1);
        items.add(1);
        items.add(1);
        items.add(0);
    }

    public View getView(int type) {
        if(type == BEFORE_ACCEPT)
            return getBeforeAcceptOrderView();
        else if(type == AFTER_ACCEPT)
            return getAfterAcceptOrderView();
        else
            return null;
    }

    private View getBeforeAcceptOrderView() {
        //todo timestamp binding
        View view = layoutInflater.inflate(R.layout.before_accept_order_view, null, false);
        return view;
    }

    private View getAfterAcceptOrderView() {
        //todo data binding
        View view = layoutInflater.inflate(R.layout.after_accept_order_view, null, false);
        return view;
    }

    public ArrayList<Integer> getItems() {
        return items;
    }
}
