package com.rena21c.voiceorder.activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.view.actionbar.ActionBarViewModel;
import com.rena21c.voiceorder.view.components.OrderViewPagerLayout;
import com.rena21c.voiceorder.view.components.RecordGuideLayout;
import com.rena21c.voiceorder.view.components.RecordingLayout;
import com.rena21c.voiceorder.view.components.ReplaceableLayout;
import com.rena21c.voiceorder.view.widgets.RecordAndStopButton;

public class MainActivity extends BaseActivity implements RecordAndStopButton.activateRecorderListener{

    private ReplaceableLayout replaceableLayout;
    private RecordAndStopButton recordAndStopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        if(checkFirstRun()) {
            recordAndStopButton.setInitHeight(recordAndStopButton.HEIGHT_WITH_GUIDE_LAYOUT);
            replaceableLayout.replaceChildView(RecordGuideLayout.getInstance(this, replaceableLayout).getView());
        }
        else {
            recordAndStopButton.setInitHeight(recordAndStopButton.HEIGHT_WITH_ORDER_LIST_LAYOUT);
            replaceableLayout.replaceChildView(OrderViewPagerLayout.getInstance(this, replaceableLayout).getView());
        }
    }

    private boolean checkFirstRun() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        boolean isFirst = sharedPreferences.getBoolean("isFirst", true);
        if(isFirst) {
            sharedPreferences.edit().putBoolean("isFirst", false).commit();
            return true;
        }
        else {
            return false;
        }
    }

    private void init() {
        ActionBarViewModel.createWithActionBar(getApplicationContext(), getSupportActionBar());

        replaceableLayout = (ReplaceableLayout)findViewById(R.id.replaceableLayout);
        recordAndStopButton = (RecordAndStopButton)findViewById(R.id.btnRecordAndStop);
        recordAndStopButton.setListener(this);
    }

    @Override
    public void record() {
        replaceableLayout.replaceChildView(RecordingLayout.getInstance(this, replaceableLayout).getView());
    }

    @Override
    public void stop() {
        replaceableLayout.replaceChildView(OrderViewPagerLayout.getInstance(this, replaceableLayout).getView());
    }
}
