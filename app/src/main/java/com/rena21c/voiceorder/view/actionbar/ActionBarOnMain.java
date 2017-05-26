package com.rena21c.voiceorder.view.actionbar;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.view.widgets.UnderLineButton;

public class ActionBarOnMain implements ActionBarInterface, View.OnClickListener {

    public enum MainTabs {
        RECOMMEND, VOICE_ORDER, MYPARTNER
    }

    private final Context context;
    private final ActionBar actionBar;

    private UnderLineButton btnRecommend;
    private UnderLineButton btnVoiceOrder;
    private UnderLineButton btnMyPartner;

    @Override public void setUp() {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_on_main);

        View tab = actionBar.getCustomView();

        Toolbar toolbar = (Toolbar) tab.getParent();
        toolbar.setContentInsetsAbsolute(0,0);

        btnRecommend = (UnderLineButton) tab.findViewById(R.id.btnRecommend);
        btnVoiceOrder = (UnderLineButton) tab.findViewById(R.id.btnVoiceOrder);
        btnMyPartner = (UnderLineButton) tab.findViewById(R.id.btnMyPartner);

        btnRecommend.setOnClickListener(this);
        btnVoiceOrder.setOnClickListener(this);
        btnMyPartner.setOnClickListener(this);
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


    public void setInitialTab() {
        btnVoiceOrder.callOnClick();
    }

    @Override public void onClick(View v) {
        btnRecommend.setSelected(false);
        btnVoiceOrder.setSelected(false);
        btnMyPartner.setSelected(false);

        switch (v.getId()) {
            case R.id.btnRecommend:
                btnRecommend.setSelected(true);
                break;
            case R.id.btnVoiceOrder:
                btnVoiceOrder.setSelected(true);
                break;
            case R.id.btnMyPartner:
                btnMyPartner.setSelected(true);
                break;
        }
    }

}
