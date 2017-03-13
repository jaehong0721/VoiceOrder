package com.rena21c.voiceorder.activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.model.Item;
import com.rena21c.voiceorder.view.actionbar.ActionBarViewModel;
import com.rena21c.voiceorder.view.adapters.OrderDetailRecyclerAdapter;

import java.util.ArrayList;

public class OrderDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvTimeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        ArrayList<Item> items = (ArrayList<Item>) getIntent().getSerializableExtra("items");
        String timestamp = getIntent().getStringExtra("time");

        ActionBarViewModel.createWithActionBar(this, getSupportActionBar())
                .setBackButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavUtils.navigateUpFromSameTask(OrderDetailActivity.this);
                    }
                });

        tvTimeStamp = (TextView)findViewById(R.id.tvTimeStamp);
        tvTimeStamp.setText(timestamp);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        OrderDetailRecyclerAdapter orderDetailRecyclerAdapter = new OrderDetailRecyclerAdapter(items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderDetailRecyclerAdapter);
    }
}
