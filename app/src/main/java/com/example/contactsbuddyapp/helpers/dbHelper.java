package com.example.contactsbuddyapp.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.contactsbuddyapp.models.contacts;

import java.util.ArrayList;

public class dbHelper extends SQLiteOpenHelper {
    public dbHelper(@Nullable Context context) {
        super(context, constants.db, null, constants.version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(constants.queryCreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + constants.table);
        onCreate(sqLiteDatabase);
    }

    public long create(String contactName, String contactNumber, String email, String contactImage,
                             String createdDate, String lastUpdateDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(constants.contactName, contactName);
        values.put(constants.contactNumber, contactNumber);
        values.put(constants.email, email);
        values.put(constants.contactImage, contactImage);
        values.put(constants.createdDate, createdDate);
        values.put(constants.lastUpdateDate, lastUpdateDate);

        long result = db.insert(constants.table, null, values);

        db.close();

        return result;
    }

    public ArrayList<contacts> read(String orderBy) {
        ArrayList<contacts> contactsList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + constants.table + " ORDER BY " + orderBy;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                contacts contact = new contacts(
                        "" + cursor.getInt(cursor.getColumnIndexOrThrow(constants.contactId)),
                        "" + cursor.getString(cursor.getColumnIndexOrThrow(constants.contactName)),
                        "" + cursor.getString(cursor.getColumnIndexOrThrow(constants.contactNumber)),
                        "" + cursor.getString(cursor.getColumnIndexOrThrow(constants.email)),
                        "" + cursor.getString(cursor.getColumnIndexOrThrow(constants.contactImage)),
                        "" + cursor.getString(cursor.getColumnIndexOrThrow(constants.createdDate)),
                        "" + cursor.getString(cursor.getColumnIndexOrThrow(constants.lastUpdateDate)));

                contactsList.add(contact);

            } while (cursor.moveToNext());
        }

        db.close();

        return contactsList;
    }

    public void update(String contactId, String contactName, String contactNumber, String email, String contactImage, String lastUpdateDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(constants.contactName, contactName);
        values.put(constants.contactNumber, contactNumber);
        values.put(constants.email, email);
        values.put(constants.contactImage, contactImage);
        values.put(constants.lastUpdateDate, lastUpdateDate);

        db.update(constants.table, values, constants.contactId + "=?", new String[]{contactId});

        db.close();
    }

    public void delete(String contactId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(constants.table, constants.contactId + "=?", new String[]{contactId});
        db.close();
    }
}
