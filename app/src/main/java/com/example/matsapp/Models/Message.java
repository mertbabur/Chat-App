package com.example.matsapp.Models;

public class Message {

    private String userPhone, friendPhone, messageDate, messageFrom, messageText, messageType;
    private String messageSeen;

    public Message(){

    }

    public Message(String userPhone, String friendPhone, String messageDate, String messageFrom, String messageText, String messageType, String messageSeen) {
        this.userPhone = userPhone;
        this.friendPhone = friendPhone;
        this.messageDate = messageDate;
        this.messageFrom = messageFrom;
        this.messageText = messageText;
        this.messageType = messageType;
        this.messageSeen = messageSeen;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getFriendPhone() {
        return friendPhone;
    }

    public void setFriendPhone(String friendPhone) {
        this.friendPhone = friendPhone;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageSeen() {
        return messageSeen;
    }

    public void setMessageSeen(String messageSeen) {
        this.messageSeen = messageSeen;
    }
}
