package com.rena21c.voiceorder.view.actionbar;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rena21c.voiceorder.R;


public class ActionBarViewModel {

    private Context context;

    public static ActionBarViewModel createWithActionBar(Context context, ActionBar supportActionBar) {
        ActionBarViewModel instance = new ActionBarViewModel(context, supportActionBar);
        instance.setup();
        return instance;
    }

    private ActionBar actionBar;
    private ImageView ibMenu;
    private ImageView ibBack;
    private TextView tvTimeStampInActionBar;

    private ActionBarViewModel(Context context, ActionBar actionBar) {
        this.actionBar = actionBar;
        this.context = context;
    }

    private void setup() {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar);
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context,android.R.color.white)));
        actionBar.setElevation(0);

        View view = actionBar.getCustomView();
        ibMenu = (ImageView)view.findViewById(R.id.ibMenu);
        ibBack = (ImageView)view.findViewById(R.id.ibBack);
        tvTimeStampInActionBar = (TextView) view.findViewById(R.id.tvTimeStampInActionBar);
    }

    public ActionBarViewModel setBackButtonClickListener(View.OnClickListener backButtonClickListener) {
        ibMenu.setVisibility(View.GONE);
        ibBack.setVisibility(View.VISIBLE);
        ibBack.setOnClickListener(backButtonClickListener);
        return this;
    }

    public void setTimeStamp(String timeStamp) {
        tvTimeStampInActionBar.setVisibility(View.VISIBLE);
        tvTimeStampInActionBar.setText(timeStamp);
    }
}
