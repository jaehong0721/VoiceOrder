package com.rena21c.voiceorder.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.model.Contact;
import com.rena21c.voiceorder.view.DividerItemDecoration;
import com.rena21c.voiceorder.view.actionbar.NavigateBackActionBar;
import com.rena21c.voiceorder.view.adapters.ContactsRecyclerViewAdapter;
import com.rena21c.voiceorder.viewholder.ContactInfoViewHolder;

import java.util.ArrayList;
import java.util.HashMap;


public class AddPartnerActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri contactUri;

    private ContactsRecyclerViewAdapter contactsAdapter;

    private RecyclerView rvContacts;

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

        rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        contactsAdapter = new ContactsRecyclerViewAdapter();

        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.addItemDecoration(new DividerItemDecoration(getApplicationContext(), R.drawable.shape_divider_recycler_view));
        rvContacts.setAdapter(contactsAdapter);
    }

    @Override protected void onStart() {
        super.onStart();

        getLoaderManager().initLoader(0, null, this);
    }


    @Override public Loader onCreateLoader(int id, Bundle args) {

        contactUri = Uri.withAppendedPath(
                ContactsContract.Contacts.CONTENT_URI,
                ContactsContract.Contacts.Entity.CONTENT_DIRECTORY);

        String[] projection =
                {
                        ContactsContract.Contacts.Entity.RAW_CONTACT_ID,
                        ContactsContract.Contacts.Entity.MIMETYPE,
                        ContactsContract.Contacts.Entity.DATA1
                };

        String selection = "(" + ContactsContract.Contacts.Entity.MIMETYPE + " = " + "'" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'" + " OR "
                        + ContactsContract.Contacts.Entity.MIMETYPE + " = " + "'" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "')" + " AND "
                        + ContactsContract.Contacts.Entity.DATA1 + " NOT NULL";

        return new CursorLoader(
                getApplicationContext(),
                contactUri,
                projection,
                selection,
                null,
                ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC");
    }

    @Override public void onLoadFinished(Loader loader, Cursor cursor) {

        cursor.moveToFirst();

        ArrayList<Contact> contacts = new ArrayList<>();

        String id = null;

        do{
            int lastIndex = contacts.size() - 1;

            if(cursor.getString(1).equals(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)) {

                if(contacts.size() != 0 && contacts.get(lastIndex).phoneNumber == null)
                    contacts.remove(lastIndex);

                id = cursor.getString(0);

                Contact contact = new Contact();
                contact.name = cursor.getString(2);
                contacts.add(contact);
            }

            if(cursor.getString(1).equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {

                if(cursor.getString(0).equals(id))
                    contacts.get(lastIndex).phoneNumber = cursor.getString(2);
            }

        } while(cursor.moveToNext());

        contactsAdapter.setContacts(contacts);

    }

    @Override public void onLoaderReset(Loader loader) {}

}
