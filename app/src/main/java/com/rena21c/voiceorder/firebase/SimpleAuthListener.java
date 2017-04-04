package com.rena21c.voiceorder.firebase;


import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public abstract class SimpleAuthListener implements OnSuccessListener, OnFailureListener {

    private final Context context;

    public SimpleAuthListener(Context context) {
        this.context = context;
    }

    @Override public void onFailure(@NonNull Exception e) {
        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
    }

}
