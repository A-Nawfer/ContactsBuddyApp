package com.example.contactsbuddyapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.contactsbuddyapp.controllers.contactsController;
import com.example.contactsbuddyapp.helpers.constants;
import com.example.contactsbuddyapp.helpers.dbHelper;

public class listContacts extends AppCompatActivity {

    private EditText ET_contactSearch;
    private TextView TV_addContact;
    private RecyclerView RV_contactList;
    private LinearLayout LL_noDataView;
    private ImageButton IM_sortList;

    private com.example.contactsbuddyapp.helpers.dbHelper dbHelper;
    private com.example.contactsbuddyapp.controllers.contactsController contactsController;

    private String orderBy;
    private final String orderByLastAdded = constants.createdDate + " DESC";
    private final String orderByFirstAdded = constants.createdDate + " ASC";
    private final String orderByContactNameAsc = constants.contactName + " ASC";
    private final String orderByContactNameDesc = constants.contactName + " DESC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contacts);

        ET_contactSearch = findViewById(R.id.ET_contactSearch);
        TV_addContact = findViewById(R.id.TV_addContact);
        RV_contactList = findViewById(R.id.RV_contactList);
        LL_noDataView = findViewById(R.id.LL_noDataView);
        IM_sortList = findViewById(R.id.IM_sortList);

        TV_addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(listContacts.this, addContact.class);
                startActivity(intent);
            }
        });

        dbHelper = new dbHelper(this);

        orderBy = orderByContactNameAsc;

        loadListOfContacts(orderBy);

        ET_contactSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
                try {
                    if (cs != null && cs.length() > 0) {
                        contactsController.getFilter().filter(cs);
                        if (contactsController.getItemCount() == 0) {
                            RV_contactList.setVisibility(View.GONE);
                            LL_noDataView.setVisibility(View.VISIBLE);
                        } else {
                            RV_contactList.setVisibility(View.VISIBLE);
                            LL_noDataView.setVisibility(View.GONE);
                        }
                    } else {
                        loadListOfContacts(orderBy);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        IM_sortList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortContactList();
            }
        });
    }

    private void loadListOfContacts(String orderBy) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RV_contactList.setLayoutManager(linearLayoutManager);
        contactsController = new contactsController(
                listContacts.this, dbHelper.read(orderBy));

        RV_contactList.setAdapter(contactsController);

        if (contactsController.getItemCount() == 0) {
            LL_noDataView.setVisibility(View.VISIBLE);
            RV_contactList.setVisibility(View.GONE);
        } else {
            LL_noDataView.setVisibility(View.GONE);
            RV_contactList.setVisibility(View.VISIBLE);
        }
    }

    private void sortContactList() {
        String[] options = {"Ascending", "Descending", "Latest", "Oldest"};

        AlertDialog.Builder builder = new AlertDialog.Builder(listContacts.this);
        builder.setTitle("Sort By")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (i == 0) {
                            loadListOfContacts(orderByContactNameAsc);

                        } else if (i == 1) {
                            loadListOfContacts(orderByContactNameDesc);

                        } else if (i == 2) {
                            loadListOfContacts(orderByLastAdded);

                        } else if (i == 3) {
                            loadListOfContacts(orderByFirstAdded);
                        }
                    }
                }).show();
    }

    protected void onResume() {
        super.onResume();
        loadListOfContacts(orderBy);
    }
}