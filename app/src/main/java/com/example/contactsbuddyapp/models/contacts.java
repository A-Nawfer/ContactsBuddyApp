package com.example.contactsbuddyapp.models;

public class contacts {
    String contactId, contactName, email, contactNumber, createdDate, lastUpdatedDate, contactImage;

    public contacts(String contactId, String contactName, String contactNumber, String email, String contactImage, String createdDate, String lastUpdatedDate) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.createdDate = createdDate;
        this.lastUpdatedDate = lastUpdatedDate;
        this.contactImage = contactImage;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getContactImage() {
        return contactImage;
    }

    public void setContactImage(String contactImage) {
        this.contactImage = contactImage;
    }
}
