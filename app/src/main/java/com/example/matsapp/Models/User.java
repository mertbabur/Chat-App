package com.example.matsapp.Models;

public class User {

    private String lastSeen, userKey, userName, userPP, userState, userPhone, userStateDate, userWallpaper;

    public User() {
    }

    public User(String lastSeen, String userKey, String userName, String userPP, String userState, String userPhone, String userStateDate, String userWallpaper) {
        this.lastSeen = lastSeen;
        this.userKey = userKey;
        this.userName = userName;
        this.userPP = userPP;
        this.userState = userState;
        this.userPhone = userPhone;
        this.userStateDate = userStateDate;
        this.userWallpaper = userWallpaper;

    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPP() {
        return userPP;
    }

    public void setUserPP(String userPP) {
        this.userPP = userPP;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserStateDate() {
        return userStateDate;
    }

    public void setUserStateDate(String userStateDate) {
        this.userStateDate = userStateDate;
    }

    public String getUserWallpaper() {
        return userWallpaper;
    }

    public void setUserWallpaper(String userWallpaper) {
        this.userWallpaper = userWallpaper;
    }
}
