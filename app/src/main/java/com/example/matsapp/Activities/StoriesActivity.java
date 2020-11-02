package com.example.matsapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matsapp.Models.Story;
import com.example.matsapp.Models.UserFriend;
import com.example.matsapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.connection.ConnectionAuthTokenProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoriesActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener{

    private ImageView imageView_activityStories_userPP, imageView_activityStories_storyContent, imageView_activityStories_closeStory;
    private TextView textView_activityStories_userName, textView_activityStories_storyTime;
    private StoriesProgressView storyView_activityStories;
    private View reverse;
    private View skip;


    private int counter = 0;
    private long pressTime = 0L;
    private long limit = 500L;

    private List<String> imageList;
    private List<String> storyTime;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private FirebaseStorage storage;
    private StorageReference stRef;

    private String USER_PHONE_NUMBER;

    private String RECEIVED_FROM_INTENT_NUM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);

        defineAttributes();
        setUserInfo(RECEIVED_FROM_INTENT_NUM);
        getStories(RECEIVED_FROM_INTENT_NUM);
        actionAttributes();

    }


    public void defineAttributes(){

        imageView_activityStories_userPP = findViewById(R.id.imageView_activityStories_userPP);
        imageView_activityStories_storyContent = findViewById(R.id.imageView_activityStories_storyContent);
        textView_activityStories_userName = findViewById(R.id.textView_activityStories_userName);
        textView_activityStories_storyTime = findViewById(R.id.textView_activityStories_storyTime);
        imageView_activityStories_closeStory =findViewById(R.id.imageView_activityStories_closeStory);
        storyView_activityStories = findViewById(R.id.storyView_activityStories);
        reverse = findViewById(R.id.reverse);
        skip = findViewById(R.id.skip);


        USER_PHONE_NUMBER = getSharedPreferences("userInfo",MODE_PRIVATE).getString("phoneNum","bos num");
        RECEIVED_FROM_INTENT_NUM = getIntent().getStringExtra("phoneNum");

        imageList = new ArrayList<>();
        storyTime = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        storage = FirebaseStorage.getInstance();
        stRef = storage.getReference();

        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storyView_activityStories.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storyView_activityStories.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);

    }


    public void actionAttributes(){

        // story i kapatir .
        imageView_activityStories_closeStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


    }


    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()){

                case MotionEvent.ACTION_DOWN:

                    pressTime = System.currentTimeMillis();
                    storyView_activityStories.pause();

                    return false;

                case MotionEvent.ACTION_UP:

                    long now = System.currentTimeMillis();
                    storyView_activityStories.resume();

                    return limit < now - pressTime;

            }

            return false;
        }
    };


    @Override
    public void onNext() {

        stRef.child("UserStories").child(imageList.get(++counter)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri).into(imageView_activityStories_storyContent);
                //textView_activityStories_storyTime.setText(storyTime.get(++counter));



            }
        });



    }

    @Override
    public void onPrev() {

        if ((counter - 1) < 0)
            return;

        stRef.child("UserStories").child(imageList.get(--counter)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri).into(imageView_activityStories_storyContent);
                //textView_activityStories_storyTime.setText(storyTime.get(--counter));

            }
        });





    }

    @Override
    public void onComplete() {

        finish();

    }

    @Override
    protected void onDestroy() {
        storyView_activityStories.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storyView_activityStories.pause();
        super.onPause();
    }

    @Override
    protected void onRestart() {
        storyView_activityStories.resume();
        super.onRestart();
    }

    public void getStories(String userPhone){

        myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("UserStories").child(userPhone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                imageList.clear();
                storyTime.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()){

                    Story story = postSnapshot.getValue(Story.class);
                    long timecurrent = System.currentTimeMillis();

                    if (timecurrent > story.getTimeStart() && timecurrent < story.getTimeEnd()){

                        imageList.add(story.getStoryKey());
                        storyTime.add(story.getStoryTime());

                    }
                    textView_activityStories_storyTime.setText(story.getStoryTime());

                    stRef.child("UserStories").child(story.getStoryKey()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(imageView_activityStories_storyContent);
                        }
                    });

                }

                    storyView_activityStories.setStoriesCount(imageList.size());
                    storyView_activityStories.setStoryDuration(5000L);
                    storyView_activityStories.setStoriesListener(StoriesActivity.this);
                    storyView_activityStories.startStories(counter);





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void setUserInfo(String phone){

        // arkadas adini almak icin .
        myRef.child("UserFriends").child(USER_PHONE_NUMBER).child(phone).child("friendName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                try { // arkadas story atti ise .

                    String friendName = snapshot.getValue().toString();
                    textView_activityStories_userName.setText(friendName);


                }
                catch (Exception e){ // kullanicinin kendi story si ise .
                    Log.e("excptn setUserInfo", e.getMessage());

                    textView_activityStories_userName.setText("My Story");


                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // kullanici pp sini almak icin .
        myRef.child("Users").child(phone).child("userPP").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String ppKey = snapshot.getValue().toString();

                stRef.child("UsersPictures").child(ppKey).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Picasso.get().load(uri).into(imageView_activityStories_userPP);

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });








    }






}