package com.rena21c.voiceorder.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.etc.IsCheckedComparator;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.firebase.ToastErrorHandlingListener;
import com.rena21c.voiceorder.model.Contact;
import com.rena21c.voiceorder.pojo.MyPartner;
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
                                                                ContactsLoader.LoadFinishedListener,
                                                                OnCompleteListener{

    private ContactsLoader contactsLoader;

    private AppPreferenceManager appPreferenceManager;

    private FirebaseDbManager dbManager;

    private HashMap<String,MyPartner> myPartnerMap;
    private HashMap<String,MyPartner> removedMyPartnerMap;

    private ContactsRecyclerViewAdapter contactsAdapter;

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

        myPartnerMap = new HashMap<>();
        removedMyPartnerMap = new HashMap<>();

        contactsLoader = new ContactsLoader(getLoaderManager(), getApplicationContext());
        contactsLoader.setLoadFinishedListener(this);

        appPreferenceManager = App.getApplication(getApplicationContext()).getPreferenceManager();

        dbManager = new FirebaseDbManager(FirebaseDatabase.getInstance());

        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        contactsAdapter = new ContactsRecyclerViewAdapter(this);

        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.addItemDecoration(new DividerItemDecoration(getApplicationContext(), R.drawable.shape_divider_recycler_view));
        rvContacts.setAdapter(contactsAdapter);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                dbManager.uploadMyPartner(appPreferenceManager.getPhoneNumber(), myPartnerMap, AddPartnerActivity.this);
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

        dbManager.getMyPartnersOnce(appPreferenceManager.getPhoneNumber(), new ToastErrorHandlingListener(this) {

            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    GenericTypeIndicator partnerMapType = new GenericTypeIndicator<HashMap<String, MyPartner>>() {};
                    myPartnerMap = (HashMap)dataSnapshot.getValue(partnerMapType);
                }
                isInitialAdd = myPartnerMap.size() == 0;
                contactsLoader.startToLoadContacts();
            }
        });
    }

    @Override public void onLoadFinished(ArrayList<Contact> contacts) {
        if(myPartnerMap.size() != 0) {

            for (Contact contact : contacts) {
                if (myPartnerMap.containsKey(contact.phoneNumber)) contact.isChecked = true;
            }

            IsCheckedComparator isCheckedComparator = new IsCheckedComparator();
            Collections.sort(contacts, isCheckedComparator);
        }

        contactsAdapter.setOriginContacts(contacts);
    }

    @Override public void onCheck(Contact contact) {
        contact.isChecked = !contact.isChecked;

        if(contact.isChecked) {
            if(removedMyPartnerMap.containsKey(contact.phoneNumber)) {
                MyPartner restoredMyPartner = removedMyPartnerMap.get(contact.phoneNumber);
                restoredMyPartner.name = contact.name;
                myPartnerMap.put(contact.phoneNumber, restoredMyPartner);
            } else {
                myPartnerMap.put(contact.phoneNumber, new MyPartner(contact.name));
            }
        } else {
            MyPartner removedMyPartner = myPartnerMap.remove(contact.phoneNumber);
            removedMyPartnerMap.put(contact.phoneNumber, removedMyPartner);
        }

        showBtnRegister();
    }

    @Override public void onComplete(@NonNull Task task) {
        if(!task.isSuccessful()) Toast.makeText(this, "거래처 등록에 실패하였습니다", Toast.LENGTH_SHORT).show();
        finish();
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
