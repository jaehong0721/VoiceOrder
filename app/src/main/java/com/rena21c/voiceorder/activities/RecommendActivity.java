package com.rena21c.voiceorder.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.view.DividerItemDecoration;
import com.rena21c.voiceorder.view.adapters.VendorsRecyclerViewAdapter;


public class RecommendActivity extends HasTabActivity {

    private RecyclerView rvVendors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        rvVendors = (RecyclerView) findViewById(R.id.rvVendors);
        VendorsRecyclerViewAdapter adapter = new VendorsRecyclerViewAdapter();
        rvVendors.setLayoutManager(new LinearLayoutManager(this));
        rvVendors.addItemDecoration(new DividerItemDecoration(getApplicationContext(), R.drawable.shape_divider_recycler_view));
        rvVendors.setAdapter(adapter);
    }
}
