package com.example.matsapp.Utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteClass {

    private static DatabaseReference myRef;
    private static String userPhoneNumber;

    private static boolean control;


    /**
     * Firebase deki child("UserFriends") daki veriyi siler .
     * @param myRef
     * @param userPhoneNumber
     */
    public static void deleteFromUsersFriendsDB(DatabaseReference myRef, String userPhoneNumber){

        myRef.child("UserFriends").child(userPhoneNumber).removeValue();

    }


    /**
     * Firebase deki child("Users") daki veriyi siler .
     * @param myRef
     * @param userPhoneNumber
     */
    public static void deleteFromUsersDB(DatabaseReference myRef, String userPhoneNumber){

        myRef.child("Users").child(userPhoneNumber).removeValue();

    }


    /**
     * sikayet sinira ulasinca kullanici silineceginden sikayetlerde silinecek .
     */
    public static void deleteComplaints(DatabaseReference myRef, String userPhoneNumber){

        myRef.child("Complaints").child(userPhoneNumber).removeValue();

    }









}
