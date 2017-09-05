package com.rena21c.voiceorder.firebase;


import android.content.Context;

import com.google.firebase.database.DataSnapshot;

public abstract class HasDbListener extends ToastErrorHandlingListener {

    public HasDbListener(Context context) {
        super(context);
    }

    @Override public void onDataChange(DataSnapshot dataSnapshot) {
        if(dataSnapshot.exists()) {
            hasDb();
        } else {
            hasNone();
        }
    }

    protected abstract void hasDb();
    protected abstract void hasNone();
}
