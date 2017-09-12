package com.rena21c.voiceorder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.model.Reply;
import com.rena21c.voiceorder.model.RequestedEstimateItem;
import com.rena21c.voiceorder.pojo.MyPartner;
import com.rena21c.voiceorder.util.DpToPxConverter;
import com.rena21c.voiceorder.util.TimeUtil;
import com.rena21c.voiceorder.util.TransformDataUtil;
import com.rena21c.voiceorder.view.adapters.EstimateViewPagerAdapter;
import com.rena21c.voiceorder.view.widgets.FinishEstimateDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestEstimateActivity extends HasTabActivity implements FinishEstimateDialogFragment.FinishRequestEstimateListener{

    private static final int REQUEST = 0;
    private static final int MODIFY = 1;

    private FirebaseDbManager dbManager;
    private AppPreferenceManager appPreferenceManager;

    private Intent modifyIntent;

    private ValueEventListener estimateItemListener;
    private ChildEventListener replyListener;

    private View modifyView;
    private Button btnModifyEstimate;
    private TextView tvItems;


    private View estimatesView;
    private ViewPager vpEstimate;
    private EstimateViewPagerAdapter estimateReplyAdapter;

    private String estimateKey;

    private HashMap<String, Reply> replyHashMap;
    private FinishEstimateDialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        replyHashMap = new HashMap<>();

        appPreferenceManager = App.getApplication(getApplicationContext()).getPreferenceManager();

        dbManager = App.getApplication(getApplicationContext()).getDbMangaer();

        modifyIntent = new Intent(RequestEstimateActivity.this, InputEstimateActivity.class);

        estimateItemListener = new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) return;

                String estimateKey = dataSnapshot.getRef().getParent().getKey();

                GenericTypeIndicator objectListType = new GenericTypeIndicator<ArrayList<RequestedEstimateItem>>() {};
                ArrayList<RequestedEstimateItem> estimateItems = (ArrayList) dataSnapshot.getValue(objectListType);

                initModifyView();

                String items = TransformDataUtil.makeRequestedEstimateItemsInLine(estimateItems);
                tvItems.setText(items);

                setModifyIntentExtra(estimateKey, estimateItems);
            }

            @Override public void onCancelled(DatabaseError databaseError) {}
        };

        replyListener = new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(!dataSnapshot.exists()) {
                    dbManager.subscribeEstimateItem(estimateKey, estimateItemListener);
                } else {

                    Reply reply = dataSnapshot.getValue(Reply.class);

                    initReplyView();

                    replyHashMap.put(dataSnapshot.getKey(), reply);
                    int position = estimateReplyAdapter.addReply(dataSnapshot.getKey(), reply);
                    vpEstimate.setVerticalScrollbarPosition(position);
                }
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(!dataSnapshot.exists()) return;

                Reply reply = dataSnapshot.getValue(Reply.class);

                int position = estimateReplyAdapter.changeReply(dataSnapshot.getKey(), reply);
                vpEstimate.setVerticalScrollbarPosition(position);
            }

            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        };

        estimateKey = appPreferenceManager.getEstimateKey();
        if(estimateKey != null) {
            String estimateTime = estimateKey.split("_")[1];
            long estimateTimeMillis = TimeUtil.convertStringToMillis(estimateTime);
            if(TimeUtil.isOverDueDate(System.currentTimeMillis(), estimateTimeMillis)) {
                //시간 마감
            } else {
                dbManager.subscribeReply(estimateKey, replyListener);
            }
        } else {
            initRequestView();
        }
    }

    @Override protected void onDestroy() {
        dbManager.cancelSubscriptionReply(estimateKey, replyListener);
        dbManager.cancelSubscriptionEstimateItem(estimateKey, estimateItemListener);
        super.onDestroy();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST :
                if(resultCode != RESULT_OK){
                    Toast.makeText(this, "견적요청에 실패하였습니다. 잠시후에 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    if(estimateKey != null) {
                        dbManager.cancelSubscriptionReply(estimateKey, replyListener);
                        dbManager.cancelSubscriptionEstimateItem(estimateKey, estimateItemListener);
                    }

                    estimateKey = data.getStringExtra("estimateKey");
                    if(estimateKey == null) return;

                    appPreferenceManager.setEstimateKey(estimateKey);
                    dbManager.subscribeReply(estimateKey, replyListener);
                    dbManager.subscribeEstimateItem(estimateKey, estimateItemListener);
                }
                break;

            case MODIFY :
                if(resultCode != RESULT_OK)
                    Toast.makeText(this, "견적 요청 수정에 실패하였습니다. 잠시후에 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void initRequestView() {
        setContentView(R.layout.activity_request_estimate_request);

        Button btnRequestEstimate = (Button) findViewById(R.id.btnRequestEstimate);

        btnRequestEstimate.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(RequestEstimateActivity.this, InputEstimateActivity.class);
                startActivityForResult(intent, REQUEST);
            }
        });
    }

    private void initReplyView() {
        if(estimatesView != null) return;

        estimatesView = getLayoutInflater().inflate(R.layout.activity_request_estimate_reply, null);
        estimatesView.setTag("reply");
        setContentView(estimatesView);

        vpEstimate = (ViewPager) findViewById(R.id.vpEstimate);
        vpEstimate.setClipToPadding(false);

        int padding = DpToPxConverter.convertDpToPx(20,getResources().getDisplayMetrics());
        vpEstimate.setPadding(padding, 0, padding, 0);

        int margin = DpToPxConverter.convertDpToPx(10, getResources().getDisplayMetrics());
        vpEstimate.setPageMargin(margin);

        estimateReplyAdapter = new EstimateViewPagerAdapter(new EstimateViewPagerAdapter.ClickFinishButtonListener(){
            @Override public void onClickFinish(String pageKey) {
                dialogFragment = FinishEstimateDialogFragment.newInstance(pageKey);
                dialogFragment.show(getSupportFragmentManager(),"finish");
            }
        });

        vpEstimate.setAdapter(estimateReplyAdapter);
    }

    private void initModifyView() {
        if(modifyView != null) return;

        modifyView = getLayoutInflater().inflate(R.layout.activity_request_estimate_modify,null);
        modifyView.setTag("modify");
        setContentView(modifyView);

        btnModifyEstimate = (Button) findViewById(R.id.btnModifyEstimate);
        btnModifyEstimate.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivityForResult(modifyIntent, MODIFY);
            }
        });

        tvItems = (TextView) findViewById(R.id.tvItems);
    }

    private void setModifyIntentExtra(String estimateKey, ArrayList<RequestedEstimateItem> estimateItems) {
        ArrayList<String> itemNames = new ArrayList<>();
        ArrayList<String> itemNums = new ArrayList<>();
        for(int i = 0; i<estimateItems.size(); i++) {
            itemNames.add(estimateItems.get(i).itemName);
            itemNums.add(estimateItems.get(i).itemNum);
        }

        modifyIntent.putExtra("modify", true);
        modifyIntent.putExtra("estimateKey", estimateKey);
        modifyIntent.putExtra("itemNames", itemNames);
        modifyIntent.putExtra("itemNums", itemNums);
    }

    @Override public void onFinish(String key) {
        dialogFragment.dismiss();
        Reply reply = replyHashMap.get(key);

        dbManager.setEstimateFinish(estimateKey, key);

        String phoneNumber = appPreferenceManager.getPhoneNumber();

        MyPartner myPartner = new MyPartner();
        myPartner.name = reply.vendorName;
        myPartner.items = reply.getRepliedItemNameMapList();

        HashMap<String, MyPartner> myPartnerHashMap = new HashMap<>();
        myPartnerHashMap.put(key, myPartner);

        dbManager.uploadMyPartner(phoneNumber, myPartnerHashMap, null);
    }
}
