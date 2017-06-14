package com.rena21c.voiceorder.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.model.Contact;
import com.rena21c.voiceorder.view.actionbar.NavigateBackActionBar;

import java.util.ArrayList;


public class AddPartnerActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private Uri contactUri;

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
                        ContactsContract.Contacts.Entity.DATA1,
                        ContactsContract.Contacts.Entity.MIMETYPE,
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

            if(cursor.getString(2).equals(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)) {

                if(contacts.size() != 0 && contacts.get(lastIndex).phoneNumber == null)
                    contacts.remove(lastIndex);

                id = cursor.getString(0);

                Contact contact = new Contact();
                contact.name = cursor.getString(1);
                contacts.add(contact);
            }

            if(cursor.getString(2).equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {

                if(cursor.getString(0).equals(id))
                    contacts.get(lastIndex).phoneNumber = cursor.getString(1);
            }

        } while(cursor.moveToNext());


        for(Contact c : contacts) {
            Log.d("test", "폰번호 : " + c.phoneNumber);
            Log.d("test", "이름 : " + c.name);
        }
    }

    @Override public void onLoaderReset(Loader loader) {}
}
