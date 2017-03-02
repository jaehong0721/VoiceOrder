package com.rena21c.voiceorder.activities;

import android.os.Bundle;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.view.actionbar.ActionBarViewModel;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ActionBarViewModel.createWithActionBar(getSupportActionBar());
    }
}
