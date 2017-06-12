package com.rena21c.voiceorder.view.actionbar;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rena21c.voiceorder.R;

public class NavigateBackActionBar implements ActionBarInterface{

    private Context context;
    private ActionBar actionBar;

    private ImageView ibBack;
    private TextView tvTitle;

    @Override public void setUp() {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_navigate_back);
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context,android.R.color.white)));

        View view = actionBar.getCustomView();
        ibBack = (ImageView)view.findViewById(R.id.ibBack);
        tvTitle = (TextView) view.findViewById(R.id.tvTitleInActionBar);
    }

    private NavigateBackActionBar(Context context, ActionBar actionBar) {
        this.context = context;
        this.actionBar = actionBar;
    }

    public static NavigateBackActionBar createWithActionBar(Context context, ActionBar supportActionBar) {
        NavigateBackActionBar instance = new NavigateBackActionBar(context, supportActionBar);
        instance.setUp();
        return instance;
    }

    public NavigateBackActionBar setBackButtonClickListener(View.OnClickListener backButtonClickListener) {
        ibBack.setOnClickListener(backButtonClickListener);
        return this;
    }

    public NavigateBackActionBar setTitle(String timeStamp) {
        tvTitle.setText(timeStamp);
        return this;
    }

}
