package com.rena21c.voiceorder.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.rena21c.voiceorder.view.actionbar.ActionBarOnMain;

public class HasTabActivity extends AppCompatActivity {

    private ActionBarOnMain actionBarOnMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String tab = getIntent().getStringExtra("tab");

        actionBarOnMain = ActionBarOnMain.createWithActionBar(getApplicationContext(), getSupportActionBar());
        actionBarOnMain.setInitialTab(ActionBarOnMain.Tab.valueOf(tab));
    }

    @Override protected void onStart() {
        super.onStart();

        actionBarOnMain.setTabClickListener(new ActionBarOnMain.TabClickListener() {
            @Override public void OnTabClicked(ActionBarOnMain.Tab tab) {

                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("tab", tab.toString());

                switch (tab) {
                    case RECOMMEND:
                        Log.d("HasTabActivity", "추천탭");
                        intent.setComponent(new ComponentName(HasTabActivity.this, Main2Activity.class));
                        break;

                    case VOICE_ORDER:
                        Log.d("HasTabActivity", "주문탭");
                        intent.setComponent(new ComponentName(HasTabActivity.this, VoiceOrderActivity.class));
                        break;

                    case MY_PARTNER:
                        Log.d("HasTabActivity", "거래처탭");
                        intent.setComponent(new ComponentName(HasTabActivity.this, Main3Activity.class));
                        break;
                }

                startActivity(intent);
            }
        });
    }

    @Override protected void onStop() {
        super.onStop();

        actionBarOnMain.removeTabClickListener();
    }
}
