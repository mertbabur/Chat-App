package com.example.matsapp.Utils;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.matsapp.Models.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FirebaseUtils {



    /**
     * Firebase Storage ye kaydetme isini yapar .
     * @param filepath --> seçtiğimiz resmin uri sini tutar .
     * @param stRef
     * @param myRef
     * @param USER_PHONE_NUMBER
     */
    public static void savePhotoFirebaseStorage(Uri filepath, StorageReference stRef, DatabaseReference myRef, String USER_PHONE_NUMBER){

        String randomKey = UUID.randomUUID().toString();

        final StorageReference ref = stRef.child("UsersPictures").child(randomKey);
        ref.putFile(filepath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()){

                    myRef.child("Users").child(USER_PHONE_NUMBER).child("userPP").setValue(randomKey);


                }
                else{
                    Log.e("savePhotoFirebaseStr : ", task.getException()+"");
                }

            }
        });


    }

    /**
     * resim icin messageText e mesaj yazisi ve resim keyi eklenecek .
     * @param imageUri
     * @param stRef
     * @param myRef
     * @param USER_PHONE_NUMBER
     * @param FRIEND_PHONE_NUMBER
     */
    public static void saveMessagePhotoFirebaseStorageAndDB(Uri imageUri, String messageText, String messageType, String messageSeen, String messageDate, StorageReference stRef, DatabaseReference myRef, String USER_PHONE_NUMBER, String FRIEND_PHONE_NUMBER){

        String randomKey = UUID.randomUUID().toString();

        final StorageReference ref = stRef.child("UsersMessagesPictures").child(randomKey);
        ref.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()){

                    String messageId = myRef.child("Messages").child(USER_PHONE_NUMBER).child(FRIEND_PHONE_NUMBER).push().getKey();

                    Map map = new HashMap();
                    map.put("messageType", messageType);
                    map.put("messageSeen", messageSeen);
                    map.put("messageDate", messageDate);
                    map.put("messageFrom", USER_PHONE_NUMBER);

                    if (messageType.equals("image")){ // eger sadece image gonderilmisse ...
                        map.put("messageText", randomKey);
                    }
                    else { // eger image ile birlikte text message i gonderilmisse ...
                        map.put("messageText", randomKey + "," + messageText);
                    }

                    myRef.child("Messages").child(USER_PHONE_NUMBER).child(FRIEND_PHONE_NUMBER).child(messageId).setValue(map);
                    myRef.child("Messages").child(FRIEND_PHONE_NUMBER).child(USER_PHONE_NUMBER).child(messageId).setValue(map);



                }
                else{
                    Log.e("savePhotoFirebaseStr : ", task.getException()+"");
                }

            }
        });
    }


    public static void saveStoryFirebaseStorage(Uri filepath, StorageReference stRef, DatabaseReference myRef, String USER_PHONE_NUMBER, String storyTime){

        String randomKey = UUID.randomUUID().toString();

        final StorageReference ref = stRef.child("UserStories").child(randomKey);
        ref.putFile(filepath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()){

                    DatabaseReference ref = myRef.child("UserStories").child(USER_PHONE_NUMBER);

                    String storyId = ref.push().getKey();
                    long timeEnd = System.currentTimeMillis() + 86400000; // 1 day

                    Map map = new HashMap();
                    map.put("storyKey", randomKey);
                    map.put("timeStart", ServerValue.TIMESTAMP);
                    map.put("timeEnd", timeEnd);
                    map.put("storyId", storyId);
                    map.put("userPhone", USER_PHONE_NUMBER);
                    map.put("storyTime", storyTime);

                    ref.child(storyId).setValue(map);



                }
                else{
                    Log.e("savePhotoFirebaseStr : ", task.getException()+"");
                }

            }
        });


    }

}
