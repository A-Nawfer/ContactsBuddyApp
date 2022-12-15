package com.example.contactsbuddyapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.contactsbuddyapp.helpers.constants;
import com.example.contactsbuddyapp.helpers.dbHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class selectContact extends AppCompatActivity {

    private ImageView btnBack, btnManageContact, btnDeleteContact, btn_phone1, btn_message1,
            btn_email1, btn_phone2, btn_message2, btn_email2;
    private TextView TV_contactName, TV_phone, TV_email, TV_dates;
    private CircularImageView IV_contactImage;

    private dbHelper dbHelper;

    private String contactId, contactName, contactImage, contactNumber, email, createdDate, lastUpdateDate;

    private SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        btnBack = findViewById(R.id.btnBack);
        btnManageContact = findViewById(R.id.btnManageContact);
        btnDeleteContact = findViewById(R.id.btnDeleteContact);
        btn_phone1 = findViewById(R.id.btn_phone1);
        btn_message1 = findViewById(R.id.btn_message1);
        btn_email1 = findViewById(R.id.btn_email1);
        btn_phone2 = findViewById(R.id.btn_phone2);
        btn_message2 = findViewById(R.id.btn_message2);
        btn_email2 = findViewById(R.id.btn_email2);

        TV_contactName = findViewById(R.id.TV_contactName);
        TV_phone = findViewById(R.id.TV_phone);
        TV_email = findViewById(R.id.TV_email);
        TV_dates = findViewById(R.id.TV_dates);

        dbHelper = new dbHelper(this);

        IV_contactImage = findViewById(R.id.IV_contactImage);

        contactId = getIntent().getStringExtra("ID");

        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        dbHelper = new dbHelper(this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_phone1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(contactNumber))));
            }
        });

        btn_phone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(contactNumber))));
            }
        });

        btn_message1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + Uri.encode(contactNumber))));
            }
        });

        btn_message2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + Uri.encode(contactNumber))));
            }
        });


        btn_email1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + Uri.encode(email))));
            }
        });

        btn_email2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + Uri.encode(email))));
            }
        });

        btnManageContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(selectContact.this, updateContact.class);
                intent.putExtra("ID", contactId);
                startActivity(intent);
            }
        });

        btnDeleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
            }
        });

        getContactDetails();
    }

    private void getContactDetails() {
        String selectQuery = "SELECT * FROM " + constants.table +
                " WHERE " + constants.contactId + "=" + contactId;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String contactId = "" + cursor.getInt(cursor.getColumnIndexOrThrow(constants.contactId));
                contactName = "" + cursor.getString(cursor.getColumnIndexOrThrow(constants.contactName));
                contactNumber = "" + cursor.getString(cursor.getColumnIndexOrThrow(constants.contactNumber));
                email = "" + cursor.getString(cursor.getColumnIndexOrThrow(constants.email));
                contactImage = "" + cursor.getString(cursor.getColumnIndexOrThrow(constants.contactImage));
                createdDate = "" + cursor.getString(cursor.getColumnIndexOrThrow(constants.createdDate));
                lastUpdateDate = "" + cursor.getString(cursor.getColumnIndexOrThrow(constants.lastUpdateDate));

                String date_created = sdf.format(new Date(Long.parseLong(createdDate)));
                String date_last_updated = sdf.format(new Date(Long.parseLong(lastUpdateDate)));

                TV_contactName.setText(contactName);
                TV_phone.setText(PhoneNumberUtils.formatNumber(contactNumber));
                TV_email.setText(email);
                TV_dates.setText("Created on: " + date_created + "\n Last Updated on: " + date_last_updated);

                if (contactImage.equals("") || contactImage.equals("null")) {
                    IV_contactImage.setImageResource(R.drawable.placeholder);
                } else {
                    IV_contactImage.setImageURI(Uri.parse(contactImage));
                }

            } while (cursor.moveToNext());
        }
    }

    private void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(selectContact.this);
        builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete '" + contactName + "' from ContactsBuddy?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbHelper.delete(contactId);
                        Toast.makeText(selectContact.this, "" + contactName + " has been deleted successfully!",
                                Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //dismiss the alert dialog
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    protected void onResume() {
        super.onResume();
        getContactDetails();
    }
}