package com.rena21c.voiceorder.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.View;
import android.widget.ImageButton;

import com.rena21c.voiceorder.R;

public class SearchVendorActivity extends BaseActivity {

    AppCompatAutoCompleteTextView actvSearch;;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_search_vendor);
        actvSearch = (AppCompatAutoCompleteTextView) findViewById(R.id.actvSearch);
        findViewById(R.id.ibClose).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                finish();
            }
        });
    }
}
