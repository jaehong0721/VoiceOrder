package com.rena21c.voiceorder.view.actionbar;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rena21c.voiceorder.R;

public class ActionBarOnDetail implements ActionBarInterface{

    private Context context;
    private ActionBar actionBar;

    private ImageView ibBack;
    private TextView tvTimeStampInActionBar;

    @Override public void setUp() {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_on_detail);
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context,android.R.color.white)));

        View view = actionBar.getCustomView();
        ibBack = (ImageView)view.findViewById(R.id.ibBack);
        tvTimeStampInActionBar = (TextView) view.findViewById(R.id.tvTimeStampInActionBar);
    }

    private ActionBarOnDetail(Context context, ActionBar actionBar) {
        this.context = context;
        this.actionBar = actionBar;
    }

    public static ActionBarOnDetail createWithActionBar(Context context, ActionBar supportActionBar) {
        ActionBarOnDetail instance = new ActionBarOnDetail(context, supportActionBar);
        instance.setUp();
        return instance;
    }

    public ActionBarOnDetail setBackButtonClickListener(View.OnClickListener backButtonClickListener) {
        ibBack.setOnClickListener(backButtonClickListener);
        return this;
    }

    public ActionBarOnDetail setTimeStamp(String timeStamp) {
        tvTimeStampInActionBar.setText(timeStamp);
        return this;
    }

}
