package com.rena21c.voiceorder.view.actionbar;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.view.widgets.UnderLineButton;

public class TabActionBar implements ActionBarInterface, View.OnClickListener {

    public enum Tab {
        RECOMMEND(R.id.btnRecommend),
        VOICE_ORDER(R.id.btnVoiceOrder),
        MY_PARTNER(R.id.btnMyPartner);

        public int viewIdOfTab;

        Tab(int viewIdOfTab) {
            this.viewIdOfTab = viewIdOfTab;
        }
    }

    private TabClickListener listener;

    private final Context context;
    private final ActionBar actionBar;

    private View tabView;

    private UnderLineButton btnRecommend;
    private UnderLineButton btnVoiceOrder;
    private UnderLineButton btnMyPartner;

    public interface TabClickListener {
        void onTabClicked(Tab tab);
    }

    @Override public void setUp() {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_on_main);

        tabView = actionBar.getCustomView();

        Toolbar toolbar = (Toolbar) tabView.getParent();
        toolbar.setContentInsetsAbsolute(0,0);

        btnRecommend = (UnderLineButton) tabView.findViewById(R.id.btnRecommend);
        btnVoiceOrder = (UnderLineButton) tabView.findViewById(R.id.btnVoiceOrder);
        btnMyPartner = (UnderLineButton) tabView.findViewById(R.id.btnMyPartner);

        btnRecommend.setSelected(false);
        btnVoiceOrder.setSelected(false);
        btnMyPartner.setSelected(false);

        btnRecommend.setOnClickListener(this);
        btnVoiceOrder.setOnClickListener(this);
        btnMyPartner.setOnClickListener(this);
    }

    private TabActionBar(Context context, ActionBar actionBar) {
        this.context = context;
        this.actionBar = actionBar;
    }

    public static TabActionBar createWithActionBar(Context context, ActionBar supportActionBar) {
        TabActionBar instance = new TabActionBar(context, supportActionBar);
        instance.setUp();
        return instance;
    }

    public void setTab(Tab tab) {
        (tabView.findViewById(tab.viewIdOfTab)).setSelected(true);
    }

    public void moveTab(Tab tab) {
        (tabView.findViewById(tab.viewIdOfTab)).callOnClick();
    }

    public void setTabClickListener(TabClickListener listener) {
        this.listener = listener;
    }

    public void removeTabClickListener() {
        this.listener = null;
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnRecommend:
                App.getApplication(context).getPreferenceManager().setClickedTab(Tab.RECOMMEND.toString());
                listener.onTabClicked(Tab.RECOMMEND);
                break;

            case R.id.btnVoiceOrder:
                App.getApplication(context).getPreferenceManager().setClickedTab(Tab.VOICE_ORDER.toString());
                listener.onTabClicked(Tab.VOICE_ORDER);
                break;

            case R.id.btnMyPartner:
                App.getApplication(context).getPreferenceManager().setClickedTab(Tab.MY_PARTNER.toString());
                listener.onTabClicked(Tab.MY_PARTNER);
        }
    }
}
