package com.example.matsapp.Utils;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TimeClass {

    private static DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    public TimeClass() {
    }


    /**
     * Kullanici app e giris cikis zamanlarini DB ye kaydeder .
     * @param userPhone --> kullanici telefon numarasi .
     * @param userState --> kullanici durumu(online, offline ?) .
     */
    public static void updateUserStatus(String userPhone, String userState){

        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calendar.getTime());

        String onlineState = saveCurrentTime+","+saveCurrentDate+","+userState;

        myRef.child("Users").child(userPhone).child("lastSeen").setValue(onlineState);


    }

    public static String getClock(){

        String saveCurrentTime;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calendar.getTime());

        return saveCurrentTime;


    }

    public static String getDate(){

        String saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        return saveCurrentDate;

    }


}
