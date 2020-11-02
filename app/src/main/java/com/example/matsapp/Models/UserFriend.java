package com.example.matsapp.Models;

public class UserFriend {

    private String friendName;
    private String friendPhone;

    public UserFriend() {
    }

    public UserFriend(String friendName, String friendPhone) {
        this.friendName = friendName;
        this.friendPhone = friendPhone;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendPhone() {
        return friendPhone;
    }

    public void setFriendPhone(String friendPhone) {
        this.friendPhone = friendPhone;
    }


}
