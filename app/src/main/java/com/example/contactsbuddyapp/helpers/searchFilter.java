package com.example.contactsbuddyapp.helpers;

import android.widget.Filter;

import com.example.contactsbuddyapp.controllers.contactsController;
import com.example.contactsbuddyapp.models.contacts;

import java.util.ArrayList;

public class searchFilter extends Filter {
    private contactsController contactsController;
    private ArrayList<contacts> filterList;

    public searchFilter(contactsController contactsListAdapter, ArrayList<contacts> filterList) {
        this.contactsController = contactsListAdapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults filterResults =new FilterResults();

        if (charSequence != null && charSequence.length() > 0) {
            charSequence = charSequence.toString().toUpperCase();

            ArrayList<contacts> contactsList = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).getContactName().toUpperCase().contains(charSequence)) {
                    contactsList.add(filterList.get(i));
                }
            }
            filterResults.count = contactsList.size();
            filterResults.values = contactsList;
        } else {
            filterResults.count = filterList.size();
            filterResults.values = filterList;
        }
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        contactsController.contactsList = (ArrayList<contacts>) filterResults.values;
        contactsController.notifyDataSetChanged();
    }
}
