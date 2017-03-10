package com.rena21c.voiceorder.view.actionbar;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;

import com.rena21c.voiceorder.R;


public class ActionBarViewModel {

    private Context context;

    public static ActionBarViewModel createWithActionBar(Context context, ActionBar supportActionBar) {
        ActionBarViewModel instance = new ActionBarViewModel(context, supportActionBar);
        instance.setup();
        return instance;
    }

    private ActionBar actionBar;

    private ActionBarViewModel(Context context, ActionBar actionBar) {
        this.actionBar = actionBar;
        this.context = context;
    }


    private void setup() {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar);
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context,android.R.color.transparent)));
        actionBar.setElevation(0);
    }
}
