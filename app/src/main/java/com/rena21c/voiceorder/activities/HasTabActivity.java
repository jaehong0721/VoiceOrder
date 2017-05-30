package com.rena21c.voiceorder.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.rena21c.voiceorder.view.actionbar.TabActionBar;

public class HasTabActivity extends AppCompatActivity {

    private TabActionBar tabActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String tab = getIntent().getStringExtra("tab");

        tabActionBar = TabActionBar.createWithActionBar(getApplicationContext(), getSupportActionBar());
        tabActionBar.setInitialTab(TabActionBar.Tab.valueOf(tab));
    }

    @Override protected void onStart() {
        super.onStart();

        tabActionBar.setTabClickListener(new TabActionBar.TabClickListener() {
            @Override public void OnTabClicked(TabActionBar.Tab tab) {

                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("tab", tab.toString());

                switch (tab) {
                    case RECOMMEND:
                        Log.d("HasTabActivity", "추천탭");
                        intent.setComponent(new ComponentName(HasTabActivity.this, RecommendActivity.class));
                        break;

                    case VOICE_ORDER:
                        Log.d("HasTabActivity", "주문탭");
                        intent.setComponent(new ComponentName(HasTabActivity.this, VoiceOrderActivity.class));
                        break;

                    case MY_PARTNER:
                        Log.d("HasTabActivity", "거래처탭");
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
}
