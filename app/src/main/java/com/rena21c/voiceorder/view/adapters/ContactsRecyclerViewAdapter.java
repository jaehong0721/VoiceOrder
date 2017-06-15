package com.rena21c.voiceorder.view.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.model.Contact;
import com.rena21c.voiceorder.viewholder.ContactInfoViewHolder;

import java.util.ArrayList;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactInfoViewHolder> {

    private ContactInfoViewHolder.CheckContactListener listener;

    private ArrayList<Contact> contacts = new ArrayList<>();

    public ContactsRecyclerViewAdapter(ContactInfoViewHolder.CheckContactListener listener) {
        this.listener = listener;
    }

    @Override public ContactInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacts, parent, false);
        return new ContactInfoViewHolder(view, listener);
    }

    @Override public void onBindViewHolder(ContactInfoViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.bind(contact);
    }

    @Override public int getItemCount() {
        return contacts.size();
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }
}
