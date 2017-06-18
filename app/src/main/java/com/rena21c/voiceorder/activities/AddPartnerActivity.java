package com.rena21c.voiceorder.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.model.Contact;
import com.rena21c.voiceorder.util.ContactsLoader;
import com.rena21c.voiceorder.util.DpToPxConverter;
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

    private AppPreferenceManager appPreferenceManager;

    private HashMap<String, String> checkedContactMap;

    private ContactsRecyclerViewAdapter contactsAdapter;

    private RecyclerView rvContacts;
    private Button btnRegister;
    private AutoCompleteTextView actvSearch;

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

        checkedContactMap = appPreferenceManager.getMyPartners();
        isInitialAdd = checkedContactMap.size() == 0;

        rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        contactsAdapter = new ContactsRecyclerViewAdapter(this);

        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.addItemDecoration(new DividerItemDecoration(getApplicationContext(), R.drawable.shape_divider_recycler_view));
        rvContacts.setAdapter(contactsAdapter);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                appPreferenceManager.setMyPartners(checkedContactMap);
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
    }

    @Override protected void onStart() {
        super.onStart();
        contactsLoader.startToLoadContacts();
    }

    @Override public void onLoadFinished(ArrayList<Contact> contacts) {
        if(checkedContactMap.size() != 0) {
            contactsAdapter.setOriginContacts(sortByIsChecked(contacts));
        } else {
            contactsAdapter.setOriginContacts(contacts);
        }
    }

    @Override public void onCheck(Contact contact) {
        contact.isChecked = !contact.isChecked;

        if(contact.isChecked) {
            checkedContactMap.put(contact.phoneNumber, contact.name);
        } else {
            checkedContactMap.remove(contact.phoneNumber);
        }

        showBtnRegister();
    }

    private ArrayList<Contact> sortByIsChecked(ArrayList<Contact> contacts) {
        ArrayList<Contact> sortedContacts = new ArrayList<>();
        int i = 0;

        for (Contact contact : contacts) {
            if (checkedContactMap.containsKey(contact.phoneNumber)) {
                contact.isChecked = true;
                sortedContacts.add(i, contact);
                i += 1;
            } else {
                sortedContacts.add(contact);
            }
        }
        return sortedContacts;
    }

    private void showBtnRegister() {
        if(btnRegister.getHeight() == 0) {
            Animation heightChangeAnimation = new ShowHeightChangeAnimation(btnRegister, DpToPxConverter.convertDpToPx(54, getResources().getDisplayMetrics()));
            heightChangeAnimation.setDuration(200);
            btnRegister.startAnimation(heightChangeAnimation);
        }
        if(checkedContactMap.size() != 0) {
            btnRegister.setText("거래처로 등록" + " (" + checkedContactMap.size() + ")");
        } else {
            btnRegister.setText(isInitialAdd ? "취소" : "모든 거래처 삭제");
        }
    }
}
