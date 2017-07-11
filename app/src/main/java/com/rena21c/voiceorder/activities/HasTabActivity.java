package com.rena21c.voiceorder.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.firebase.AnalyticsEventManager;
import com.rena21c.voiceorder.view.actionbar.TabActionBar;

public class HasTabActivity extends AppCompatActivity {

    private TabActionBar tabActionBar;
    private AnalyticsEventManager eventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String phoneNumber = App.getApplication(getApplicationContext()).getPreferenceManager().getPhoneNumber();
        eventManager = App.getApplication(getApplicationContext()).getEventManager();

        String tab = getIntent().getStringExtra("tab");

        tabActionBar = TabActionBar.createWithActionBar(getApplicationContext(), getSupportActionBar());
        tabActionBar.setTab(TabActionBar.Tab.valueOf(tab));
    }

    @Override protected void onStart() {
        super.onStart();

        tabActionBar.setTabClickListener(new TabActionBar.TabClickListener() {
            @Override public void onTabClicked(TabActionBar.Tab tab) {

                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("tab", tab.toString());

                switch (tab) {
                    case RECOMMEND:
                        eventManager.setRecommendTabClickEvent();
                        intent.setComponent(new ComponentName(HasTabActivity.this, RecommendActivity.class));
                        break;

                    case VOICE_ORDER:
                        eventManager.setVoiceOrderTabClickEvent();
                        intent.setComponent(new ComponentName(HasTabActivity.this, VoiceOrderActivity.class));
                        break;

                    case MY_PARTNER:
                        eventManager.setMyPartnerTabClickEvent();
                        intent.setComponent(new ComponentName(HasTabActivity.this, MyPartnerActivity.class));
                        break;
                }

                startActivity(intent);
            }
        });
    }

    @Override protected void onStop() {
        super.onStop();

        tabActionBar.removeTabClickListener();
    }

    protected void moveTab(TabActionBar.Tab tab) {
        tabActionBar.moveTab(tab);
    }
}
