package com.rena21c.voiceorder.activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.model.VoiceRecord;
import com.rena21c.voiceorder.util.FileNameUtil;
import com.rena21c.voiceorder.view.actionbar.ActionBarOnDetail;
import com.rena21c.voiceorder.view.adapters.OrderDetailRecyclerAdapter;

import java.util.HashMap;

public class OrderDetailActivity extends BaseActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.DetailTheme);
        setContentView(R.layout.activity_order_detail);

        HashMap<String, VoiceRecord> itemHashMap = (HashMap<String, VoiceRecord>) getIntent().getSerializableExtra("itemHashMap");

        String timeStamp = getIntent().getStringExtra("timeStamp");
        String displayTime = FileNameUtil.getDisplayTimeFromfileName(timeStamp);

        ActionBarOnDetail.createWithActionBar(this, getSupportActionBar())
                .setBackButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavUtils.navigateUpFromSameTask(OrderDetailActivity.this);
                    }
                })
                .setTimeStamp(displayTime);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        OrderDetailRecyclerAdapter orderDetailRecyclerAdapter = new OrderDetailRecyclerAdapter(itemHashMap);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderDetailRecyclerAdapter);
    }
}
