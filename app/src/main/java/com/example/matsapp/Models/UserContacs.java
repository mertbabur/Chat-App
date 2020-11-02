package com.example.matsapp.Models;

public class UserContacs {

    private String contactName, contactNum;

    public UserContacs(String contactName, String contactNum) {
        this.contactName = contactName;
        this.contactNum = contactNum;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNum() {
        return contactNum;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }
}
