package com.rena21c.voiceorder.viewholder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.model.Contact;

public class ContactInfoViewHolder extends RecyclerView.ViewHolder{

    private TextView tvNameOnContact;
    private ImageView ivCheckMark;

    public ContactInfoViewHolder(final View itemView) {
        super(itemView);

        tvNameOnContact = (TextView) itemView.findViewById(R.id.tvNameOnContact);
        ivCheckMark = (ImageView) itemView.findViewById(R.id.ivCheckMark);

    }

    public void bind(Contact contact) {

        tvNameOnContact.setText(contact.name);
        
    }
}
