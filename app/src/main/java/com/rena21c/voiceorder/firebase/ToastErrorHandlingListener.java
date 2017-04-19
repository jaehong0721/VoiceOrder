package com.rena21c.voiceorder.firebase;


import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public abstract class ToastErrorHandlingListener implements ValueEventListener {

    private final Context context;

    public ToastErrorHandlingListener(Context context) {
        this.context = context;
    }

    @Override public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(context, databaseError.toString(), Toast.LENGTH_SHORT).show();
    }
}
