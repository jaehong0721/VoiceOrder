package com.rena21c.voiceorder.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.model.Contact;
import com.rena21c.voiceorder.util.ContactsLoader;
import com.rena21c.voiceorder.view.DividerItemDecoration;
import com.rena21c.voiceorder.view.actionbar.NavigateBackActionBar;
import com.rena21c.voiceorder.view.adapters.ContactsRecyclerViewAdapter;
import com.rena21c.voiceorder.view.animation.ShowHeightChangeAnimation;
import com.rena21c.voiceorder.viewholder.ContactInfoViewHolder;

import java.util.ArrayList;
import java.util.HashMap;


public class AddPartnerActivity extends BaseActivity implements ContactInfoViewHolder.CheckContactListener,
                                                                ContactsLoader.LoadFinishedListener {

    private ContactsLoader contactsLoader;

    private HashMap<String, String> checkedContactMap;

    private ContactsRecyclerViewAdapter contactsAdapter;

    private RecyclerView rvContacts;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_partner);

        NavigateBackActionBar.createWithActionBar(this, getSupportActionBar())
                .setBackButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setTitle("거래처 등록");

        contactsLoader = new ContactsLoader(getLoaderManager(), getApplicationContext());
        contactsLoader.setLoadFinishedListener(this);

        checkedContactMap = new HashMap<>();

        rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        contactsAdapter = new ContactsRecyclerViewAdapter(this);

        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.addItemDecoration(new DividerItemDecoration(getApplicationContext(), R.drawable.shape_divider_recycler_view));
        rvContacts.setAdapter(contactsAdapter);

        btnRegister = (Button) findViewById(R.id.btnRegister);
    }

    @Override protected void onStart() {
        super.onStart();
        contactsLoader.startToLoadContacts();
    }

    @Override public void onLoadFinished(ArrayList<Contact> contacts) {
        contactsAdapter.setContacts(contacts);
    }

    @Override public void onCheck(Contact contact) {
        contact.isChecked = !contact.isChecked;

        if(contact.isChecked) {
            checkedContactMap.put(contact.phoneNumber, contact.name);

            if(checkedContactMap.size() == 1) {
                Animation longHeightAni = new ShowHeightChangeAnimation(btnRegister, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 54, getResources().getDisplayMetrics()));
                longHeightAni.setDuration(200);
                btnRegister.startAnimation(longHeightAni);
            }

        } else {
            checkedContactMap.remove(contact.phoneNumber);

            if(checkedContactMap.size() == 0) {
                Animation longHeightAni = new ShowHeightChangeAnimation(btnRegister, 0);
                longHeightAni.setDuration(200);
                btnRegister.startAnimation(longHeightAni);
            }
        }
    }
}
