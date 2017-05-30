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

        actionBarOnMain = ActionBarOnMain.createWithActionBar(getApplicationContext(), getSupportActionBar());
    }

    @Override protected void onStart() {
        super.onStart();

        actionBarOnMain.setTabClickListener(new ActionBarOnMain.TabClickListener() {
            @Override public void OnTabClicked(ActionBarOnMain.Tab tab) {

                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                switch (tab) {
                    case RECOMMEND:
                        Log.d("HasTabActivity", "추천탭");
                        intent.setComponent(new ComponentName(HasTabActivity.this, Main2Activity.class));
                        startActivity(intent);
                        break;

                    case VOICE_ORDER:
                        Log.d("HasTabActivity", "주문탭");
                        intent.setComponent(new ComponentName(HasTabActivity.this, MainActivity.class));
                        startActivity(intent);
                        break;

                    case MY_PARTNER:
                        Log.d("HasTabActivity", "거래처탭");
                        intent.setComponent(new ComponentName(HasTabActivity.this, Main3Activity.class));
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    @Override protected void onStop() {
        super.onStop();

        actionBarOnMain.removeTabClickListener();
    }
}
