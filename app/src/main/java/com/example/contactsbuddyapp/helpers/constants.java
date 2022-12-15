package com.example.contactsbuddyapp.helpers;

public class constants {
    public static final int version = 1;
    public static final String db = "DB_CONTACTS_BUDDY";
    public static final String table = "TBL_CONTACTS";
    public static final String contactId = "CONTACTS_ID";
    public static final String contactName = "CONTACTS_NAME";
    public static final String contactNumber = "CONTACT_NUMBER";
    public static final String email = "EMAIL";
    public static final String contactImage = "CONTACT_IMAGE";
    public static final String createdDate = "CREATED_DATE";
    public static final String lastUpdateDate = "LAST_UPDATE_DATE";

    public static final String queryCreateTable = "CREATE TABLE " + table + "(" +
            contactId + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            contactName + " TEXT," +
            contactNumber + " TEXT," +
            email + " TEXT," +
            contactImage + " TEXT," +
            createdDate + " TEXT," +
            lastUpdateDate + " TEXT" + ")";
}