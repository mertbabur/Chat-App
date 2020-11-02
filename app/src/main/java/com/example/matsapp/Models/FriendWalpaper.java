package com.example.matsapp.Models;

public class FriendWalpaper {

    private String friendWalpaperKey;
    private String fromWalpaper;

    public FriendWalpaper() {
    }

    public FriendWalpaper(String friendWalpaperKey, String fromWalpaper) {
        this.friendWalpaperKey = friendWalpaperKey;
        this.fromWalpaper = fromWalpaper;
    }

    public String getFriendWalpaperKey() {
        return friendWalpaperKey;
    }

    public void setFriendWalpaperKey(String friendWalpaperKey) {
        this.friendWalpaperKey = friendWalpaperKey;
    }

    public String getFromWalpaper() {
        return fromWalpaper;
    }

    public void setFromWalpaper(String fromWalpaper) {
        this.fromWalpaper = fromWalpaper;
    }
}
