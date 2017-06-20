package com.rena21c.voiceorder.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.etc.IsCheckedComparator;
import com.rena21c.voiceorder.model.Contact;
import com.rena21c.voiceorder.util.ContactsLoader;
import com.rena21c.voiceorder.util.DpToPxConverter;
import com.rena21c.voiceorder.view.DividerItemDecoration;
import com.rena21c.voiceorder.view.actionbar.NavigateBackActionBar;
import com.rena21c.voiceorder.view.adapters.ContactsRecyclerViewAdapter;
import com.rena21c.voiceorder.view.animation.ShowHeightChangeAnimation;
import com.rena21c.voiceorder.viewholder.ContactInfoViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class AddPartnerActivity extends BaseActivity implements ContactInfoViewHolder.CheckContactListener,
                                                                ContactsLoader.LoadFinishedListener {

    private ContactsLoader contactsLoader;

    private AppPreferenceManager appPreferenceManager;

    private HashMap<String, String> myPartnerMap;

    private ContactsRecyclerViewAdapter contactsAdapter;

    private RecyclerView rvContacts;
    private Button btnRegister;
    private AutoCompleteTextView actvSearch;
    private ImageView ivDelete;

    private boolean isInitialAdd;

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

        appPreferenceManager = App.getApplication(getApplicationContext()).getPreferenceManager();

        myPartnerMap = appPreferenceManager.getMyPartners();
        isInitialAdd = myPartnerMap.size() == 0;

        rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        contactsAdapter = new ContactsRecyclerViewAdapter(this);

        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.addItemDecoration(new DividerItemDecoration(getApplicationContext(), R.drawable.shape_divider_recycler_view));
        rvContacts.setAdapter(contactsAdapter);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                appPreferenceManager.setMyPartners(myPartnerMap);
                finish();
            }
        });

        actvSearch = (AutoCompleteTextView) findViewById(R.id.actvSearch);
        actvSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                contactsAdapter.getFilter().filter(s);
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        ivDelete = (ImageView) findViewById(R.id.ivDelete);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                actvSearch.setText("");
                actvSearch.clearFocus();
            }
        });

        actvSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if(v != actvSearch) return;

                if(hasFocus) {
                    ivDelete.setVisibility(View.VISIBLE);
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(actvSearch.getWindowToken(), 0);

                    ivDelete.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override protected void onStart() {
        super.onStart();
        contactsLoader.startToLoadContacts();
    }

    @Override public void onLoadFinished(ArrayList<Contact> contacts) {
        if(myPartnerMap.size() != 0) {

            for (Contact contact : contacts) {
                if (myPartnerMap.containsKey(contact.phoneNumber)) contact.isChecked = true;
            }

            IsCheckedComparator isCheckedComparator = new IsCheckedComparator();
            Collections.sort(contacts, isCheckedComparator);

            contactsAdapter.setOriginContacts(contacts);
        } else {
            contactsAdapter.setOriginContacts(contacts);
        }
    }

    @Override public void onCheck(Contact contact) {
        contact.isChecked = !contact.isChecked;

        if(contact.isChecked) {
            myPartnerMap.put(contact.phoneNumber, contact.name);
        } else {
            myPartnerMap.remove(contact.phoneNumber);
        }

        showBtnRegister();
    }

    private void showBtnRegister() {
        if(btnRegister.getHeight() == 0) {
            Animation heightChangeAnimation = new ShowHeightChangeAnimation(btnRegister, DpToPxConverter.convertDpToPx(54, getResources().getDisplayMetrics()));
            heightChangeAnimation.setDuration(200);
            btnRegister.startAnimation(heightChangeAnimation);
        }
        if(myPartnerMap.size() != 0) {
            btnRegister.setText("거래처로 등록" + " (" + myPartnerMap.size() + ")");
        } else {
            btnRegister.setText(isInitialAdd ? "취소" : "모든 거래처 삭제");
        }
    }
}
