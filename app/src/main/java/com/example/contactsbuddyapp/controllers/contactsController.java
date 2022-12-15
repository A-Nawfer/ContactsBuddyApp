package com.example.contactsbuddyapp.controllers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactsbuddyapp.R;
import com.example.contactsbuddyapp.helpers.searchFilter;
import com.example.contactsbuddyapp.models.contacts;
import com.example.contactsbuddyapp.selectContact;

import java.util.ArrayList;

public class contactsController  extends RecyclerView.Adapter<contactsController.HolderContact> implements Filterable {

    private Context context;
    public ArrayList<contacts> contactsList, filterList;
    private searchFilter contactSearchFilter;

    public contactsController(Context context, ArrayList<contacts> contactList) {
        this.context = context;
        this.contactsList = contactList;
        this.filterList = contactList;
    }

    @NonNull
    @Override
    public contactsController.HolderContact onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.contact_layout, parent, false);

        return new HolderContact(view);
    }

    @Override
    public void onBindViewHolder(@NonNull contactsController.HolderContact holder, int position) {

        contacts contacts = contactsList.get(position);
        String id = contacts.getContactId();
        String name = contacts.getContactName();
        String image = contacts.getContactImage();

        if (image.equals("") || image.equals("null")) {
            holder.IV_contactImage.setImageResource(R.drawable.placeholder);
        } else {
            holder.IV_contactImage.setImageURI(Uri.parse(image));
        }

        holder.TV_contactName.setText(name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, selectContact.class);
                intent.putExtra("ID", id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    @Override
    public Filter getFilter() {
        if (contactSearchFilter == null) {
            contactSearchFilter = new searchFilter(this, filterList);
        }
        return contactSearchFilter;
    }

    class HolderContact extends RecyclerView.ViewHolder {

        ImageView IV_contactImage;
        TextView TV_contactName;

        public HolderContact (@Nullable View itemView) {
            super(itemView);

            IV_contactImage = itemView.findViewById(R.id.IV_contactImage);
            TV_contactName = itemView.findViewById(R.id.TV_contactName);
        }
    }
}
