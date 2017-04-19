package com.rena21c.voiceorder.view.actionbar;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;

import com.rena21c.voiceorder.R;

public class ActionBarOnMain implements ActionBarInterface{

    private final Context context;
    private final ActionBar actionBar;

    @Override public void setUp() {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_on_main);
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context,android.R.color.white)));
        actionBar.setElevation(0);
    }

    private ActionBarOnMain(Context context, ActionBar actionBar) {
        this.context = context;
        this.actionBar = actionBar;
    }

    public static ActionBarOnMain createWithActionBar(Context context, ActionBar supportActionBar) {
        ActionBarOnMain instance = new ActionBarOnMain(context, supportActionBar);
        instance.setUp();
        return instance;
    }
}
