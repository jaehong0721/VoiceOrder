package com.rena21c.voiceorder.util;


import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.rena21c.voiceorder.etc.NameAscComparator;
import com.rena21c.voiceorder.model.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ContactsLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface LoadFinishedListener {
        void onLoadFinished(ArrayList<Contact> contacts);
    }

    private LoaderManager loaderManager;

    private Context context;

    private Uri contactUri;

    private LoadFinishedListener listener;

    public ContactsLoader(LoaderManager loaderManager, Context context) {
        this.loaderManager = loaderManager;
        this.context = context;
    }

    public void setLoadFinishedListener(LoadFinishedListener listener) {
        this.listener = listener;
    }

    public void startToLoadContacts() {
        loaderManager.initLoader(0, null, this);
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
                context,
                contactUri,
                projection,
                selection,
                null,
                null);
    }

    @Override public void onLoadFinished(Loader loader, Cursor cursor) {

        cursor.moveToFirst();

        ArrayList<Contact> contacts = new ArrayList<>();

        HashMap<String, String> nameMap = new HashMap<>();
        HashMap<String, String> phoneNumberMap = new HashMap<>();

        do{
            String id;
            String name;
            String phoneNumber;

            id = cursor.getString(0);

            if(cursor.getString(1).equals(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)) {
                name = cursor.getString(2);
                nameMap.put(id, name);
            } else {
                phoneNumber = cursor.getString(2);
                phoneNumberMap.put(id, phoneNumber);
            }

        } while(cursor.moveToNext());

        for(Map.Entry entry : phoneNumberMap.entrySet()) {
            String name = nameMap.get(entry.getKey());
            String phoneNumber = StringUtil.removeSpecialLetter((String) entry.getValue());
            if(phoneNumber.equals("")) phoneNumber = "01000000000";

            if(name != null && phoneNumber != null)
                contacts.add(new Contact(phoneNumber, name));
        }

        NameAscComparator nameAscComparator = new NameAscComparator();

        Collections.sort(contacts, nameAscComparator);

        if(listener != null) {
            listener.onLoadFinished(contacts);
        } else {
            throw new RuntimeException("you must set the loadFinishedListener");
        }
    }

    @Override public void onLoaderReset(Loader loader) {}

}
