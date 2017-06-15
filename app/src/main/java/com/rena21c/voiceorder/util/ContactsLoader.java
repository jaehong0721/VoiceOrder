package com.rena21c.voiceorder.util;


import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.rena21c.voiceorder.model.Contact;

import java.util.ArrayList;

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

        if(listener != null) {
            listener.onLoadFinished(contacts);
        } else {
            throw new RuntimeException("you must set the loadFinishedListener");
        }
    }

    @Override public void onLoaderReset(Loader loader) {}

}
