package com.example.matsapp.Utils;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.matsapp.Models.UserContacs;
import com.example.matsapp.Models.UserFriend;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param -- > isUserFriendPhoneList --> rehberdeki numaralari tutar .
 */
public class FriendNumberControl {

    private static List<String> isUserFriendPhoneList = new ArrayList<>();

    private static DatabaseReference myRef;

    private static int counter = -1;

    public FriendNumberControl() {
    }


    /**
     * Once rehberden numaralari al ve kontrol edip firebase e kaydet .
     * @param activity --> ?
     * @param phoneNumber --> kullanici numarasi .
     */
    public static void useFriendNumberControlClass(FragmentActivity activity, String phoneNumber){

        getPhoneNumbersFromGuideBook(activity);

        isUserFriendRegistered(phoneNumber, isUserFriendPhoneList);

    }



    /**
     * Rehberden telefon numaralarini ceker .
     */
    public static List<UserContacs> getPhoneNumbersFromGuideBook(FragmentActivity activity){

        List<UserContacs> list = new ArrayList<>();

        Cursor guideBook = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);

        while (guideBook.moveToNext()) {

            String phoneNumber = guideBook.getString(guideBook.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            String phoneNumberName = guideBook.getString(guideBook.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            /*if (!phoneNumber.contains("+90")){
                phoneNumber = "+90" + phoneNumber;
            }*/

            //Log.e("phone",phoneNumber.replaceAll("\\s","").toString() + " " + phoneNumberName);

            isUserFriendPhoneList.add(phoneNumber.replaceAll("\\s","") + "," + phoneNumberName);

            //userFriendSavedName.add(phoneNumberName);

            list.add(new UserContacs(phoneNumberName, phoneNumber.replaceAll("\\s","")));

        }

        return list;

    }


    /**
     * Kullanicinin arkadasi app e kayitli mi . Kayitli ise arkadas numarasini firebase e UserFriends tablosu adi altinda kaydeder .
     * @param phoneNumber --> user telefon numarasi .
     * @param userContactNums --> rehberdeki numaralar .
     */
    public static void isUserFriendRegistered(String phoneNumber, List<String> userContactNums){

        myRef = FirebaseDatabase.getInstance().getReference();

        for (int i = 0; i < userContactNums.size(); i++) {

            try {

                String[] arr = userContactNums.get(i).split(",");
                String num = arr[0]; // friend numarasi .
                String userFriendName = arr[1]; // rehberdeki friend adi .
                //Log.e("a:",num+userFriendName);

                myRef.child("Users").child(num).child("userPhone").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String friendNum = "";

                        try {

                            friendNum = dataSnapshot.getValue().toString();

                        } catch (Exception e) {
                            Log.e("hata exception : ", e.getMessage());
                        }

                        if (!friendNum.equals("")) {

                            /*myRef.child("UserFriends").child(phoneNumber).child(friendNum).setValue(friendNum).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.e("sonuc : ", "basarili");
                                    }
                                }
                            });*/



                            UserFriend friend = new UserFriend(userFriendName,friendNum);

                            myRef.child("UserFriends").child(phoneNumber).child(friendNum).setValue(friend);

                            //Log.e(counter+"",userFriendSavedName.get(counter).toString()+" "+friendNum.toString());



                        } else {
                            //Log.e("isUserFriendReg : ", "arkadas db ye kaydedilmedi .");

                        }

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
            catch (Exception e){
                //Log.e("Exception : ",e.getMessage());
            }


        }



    }





}
