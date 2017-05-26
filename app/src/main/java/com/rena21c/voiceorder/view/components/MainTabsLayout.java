package com.rena21c.voiceorder.view.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.view.widgets.UnderLineButton;

public class MainTabsLayout extends FrameLayout implements View.OnClickListener {

    public enum MainTabs {
        RECOMMEND, VOICE_ORDER, MYPARTNER
    }

    private UnderLineButton btnRecommend;
    private UnderLineButton btnVoiceOrder;
    private UnderLineButton btnMyPartner;

    public interface MainTabClickListener {
        void onClickMainTab(MainTabs tab);
    }

    public MainTabsLayout(@NonNull Context context) {
        super(context, null);
    }

    public MainTabsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.layout_main_tabs, this);

        btnRecommend = (UnderLineButton) findViewById(R.id.btnRecommend);
        btnVoiceOrder = (UnderLineButton) findViewById(R.id.btnVoiceOrder);
        btnMyPartner = (UnderLineButton) findViewById(R.id.btnMyPartner);

        btnRecommend.setOnClickListener(this);
        btnVoiceOrder.setOnClickListener(this);
        btnMyPartner.setOnClickListener(this);
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
//                listener.onClickMainTab(MainTabs.VOICE_ORDER);
                break;
            case R.id.btnMyPartner:
                btnMyPartner.setSelected(true);
                break;
        }
    }

    public void showInitialTab(MainTabs tab) {
        btnVoiceOrder.callOnClick();
    }
}
