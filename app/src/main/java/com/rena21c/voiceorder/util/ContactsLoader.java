package com.rena21c.voiceorder.util;


import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

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

        contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

        return new CursorLoader(
                context,
                contactUri,
                projection,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
    }

    @Override public void onLoadFinished(Loader loader, Cursor cursor) {

        cursor.moveToFirst();

        ArrayList<Contact> contacts = new ArrayList<>();

        do {
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String number = StringUtil.removeSpecialLetter(cursor.getString(column).trim());
            Log.d("test", "phoneNumber = " + number);

            if(number.equals("")) continue;

            int column_name = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String name = cursor.getString(column_name);
            Log.d("test", "name = " + name);

            contacts.add(new Contact(number, name));
        } while (cursor.moveToNext());

        if(listener != null) {
            listener.onLoadFinished(contacts);
        } else {
            throw new RuntimeException("you must set the loadFinishedListener");
        }
    }

    @Override public void onLoaderReset(Loader loader) {}

}
