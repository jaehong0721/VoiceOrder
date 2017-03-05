package com.rena21c.voiceorder.view.actionbar;

import android.support.v7.app.ActionBar;

import com.rena21c.voiceorder.R;


public class ActionBarViewModel {

    public static ActionBarViewModel createWithActionBar(ActionBar supportActionBar) {
        ActionBarViewModel instance = new ActionBarViewModel(supportActionBar);
        instance.setup();
        return instance;
    }

    private ActionBar actionBar;

    private ActionBarViewModel(ActionBar actionBar) {this.actionBar = actionBar;}

    private void setup() {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar);
        actionBar.setElevation(0);
    }

}
