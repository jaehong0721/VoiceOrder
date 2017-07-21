package com.rena21c.voiceorder.view.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.model.Contact;
import com.rena21c.voiceorder.viewholder.ContactInfoViewHolder;

import java.util.ArrayList;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactInfoViewHolder> implements Filterable {

    private ContactInfoViewHolder.CheckContactListener listener;

    private ArrayList<Contact> originContacts;
    private ArrayList<Contact> displayedContacts;

    private Filter contactFilter;

    public ContactsRecyclerViewAdapter(ContactInfoViewHolder.CheckContactListener listener) {
        this.listener = listener;
        originContacts = new ArrayList<>();
        displayedContacts = new ArrayList<>();
    }

    @Override public ContactInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacts, parent, false);
        return new ContactInfoViewHolder(view, listener);
    }

    @Override public void onBindViewHolder(ContactInfoViewHolder holder, int position) {
        Contact contact = displayedContacts.get(position);
        holder.bind(contact);
    }

    @Override public int getItemCount() {
        return displayedContacts.size();
    }

    public void setOriginContacts(ArrayList<Contact> originContacts) {
        this.originContacts = originContacts;
        setDisplayedContacts(this.originContacts);
    }

    private void setDisplayedContacts(ArrayList<Contact> displayedContacts) {
        this.displayedContacts = displayedContacts;
        notifyDataSetChanged();
    }

    @Override public Filter getFilter() {
        return contactFilter != null ? contactFilter : new ContactFilter();
    }

    private class ContactFilter extends Filter {

        @Override protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();

            if(constraint == null || constraint.length() == 0) {
                filterResults.values = originContacts;
                filterResults.count = originContacts.size();
            } else {
                ArrayList<Contact> filteredContacts = new ArrayList<>();

                for(Contact contact : originContacts) {

                    if(contact.name.trim().contains(constraint.toString().trim())) {
                        filteredContacts.add(contact);
                    }
                }

                filterResults.values = filteredContacts;
                filterResults.count = filteredContacts.size();
            }
            return filterResults;
        }

        @Override protected void publishResults(CharSequence constraint, FilterResults results) {
            try {
                setDisplayedContacts((ArrayList<Contact>)results.values);
            } catch (ClassCastException e) {
                throw new RuntimeException("The item on ContactInfo RecyclerView must be Contact.class");
            }
        }
    }
}
