package com.example.matsapp.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matsapp.Adapters.ChatFragmentAdapter;
import com.example.matsapp.Adapters.ShareContactsAdapter;
import com.example.matsapp.Models.FriendWalpaper;
import com.example.matsapp.Models.Message;
import com.example.matsapp.Models.User;
import com.example.matsapp.R;
import com.example.matsapp.Utils.FirebaseUtils;
import com.example.matsapp.Utils.TimeClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatFragment extends Fragment {

    private ImageView chatFragment_back_click, chatFragment_friendPP, chatFragment_menu, chatFragment_camera, chatFragment_attachFile_menu;
    private EditText chatFragment_messageField;
    private RecyclerView chatFragment_recyclerView;
    private CardView cardView_fragmentChat_click;
    private TextView chatFragment_friendName, chatFragment_friendLastSeen;
    private FloatingActionButton chatFragment_cardView_sendMessage_click, chatFragment_floatinActionButton_sendRecord_click;

    private ChatFragmentAdapter adapter;

    private String FRIEND_PHONE_NUMBER;
    private String USER_PHONE_NUMBER;
    private String FRIEND_NAME;

    private static final int PHOTO_TAKEN_FROM_CAMERA = 1;
    private static final int PHOTO_SELECTED_FROM_GALLERY = 2;

    private String currentPhotoPath;

    private Bitmap bitmap_Photo;
    private Uri photo_selected_from_galleryOrTakenCamera_uri;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private List<Message> messageList;
    private List<String> messageKey;

    private FirebaseStorage storage;
    private StorageReference stRef;

    private MediaPlayer mediaPlayerSendMessage, mediaPlayerReceivedMessage;

    private MediaRecorder recorder;
    private String mFileName = null;

    private MediaPlayer player;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat_design,container,false);

        defineAttributies(rootView);
        blockFromFriendControl(USER_PHONE_NUMBER, FRIEND_PHONE_NUMBER, "Arkadaşınızı engellediniz .");
        blockFromFriendControl(FRIEND_PHONE_NUMBER, USER_PHONE_NUMBER, "Arkadaşınız tarafından engellendiniz .");
        actionAttributies();
        getFriendInfoFromDB();
        getWalpaperForRecycBackground();
        loadMessage();
        defineRecyclerView();
        controlMessageForAudio();

        return rootView;

    }


    public void defineAttributies(View rootView){

        chatFragment_back_click = rootView.findViewById(R.id.chatFragment_back_click);
        chatFragment_friendPP = rootView.findViewById(R.id.fragmentChat_friendPP);
        chatFragment_menu = rootView.findViewById(R.id.chatFragment_menu);
        chatFragment_camera = rootView.findViewById(R.id.chatFragment_camera);
        chatFragment_attachFile_menu = rootView.findViewById(R.id.chatFragment_attachFile_menu);
        chatFragment_messageField = rootView.findViewById(R.id.chatFragment_messageField);
        chatFragment_recyclerView = rootView.findViewById(R.id.chatFragment_recyclerView);
        cardView_fragmentChat_click = rootView.findViewById(R.id.cardView_fragmentChat_click);
        chatFragment_friendName = rootView.findViewById(R.id.chatFragment_friendName);
        chatFragment_friendLastSeen = rootView.findViewById(R.id.chatFragment_friendLastSeen);
        chatFragment_cardView_sendMessage_click = rootView.findViewById(R.id.chatFragment_floatinActionButton_sendMessage_click);
        chatFragment_floatinActionButton_sendRecord_click = rootView.findViewById(R.id.chatFragment_floatinActionButton_sendRecord_click);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        storage = FirebaseStorage.getInstance();
        stRef = storage.getReference();

        USER_PHONE_NUMBER = getActivity().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE).getString("phoneNum", "1");
        FRIEND_PHONE_NUMBER = getArguments().getString("friendNum", "2");
        FRIEND_NAME = getArguments().getString("friendName", "null");

        messageList = new ArrayList<>();
        messageKey = new ArrayList<>();

        mediaPlayerSendMessage = MediaPlayer.create(getActivity(), R.raw.send_message);
        mediaPlayerReceivedMessage = MediaPlayer.create(getActivity(), R.raw.received_message);

        mFileName = getActivity().getExternalCacheDir().getAbsolutePath();
        mFileName += "/recorded_audio.3gp";



    }


    public void actionAttributies(){

        // geri tusu ...
        chatFragment_back_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainUserPageFragment mainUserPageFragment = new MainUserPageFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity,mainUserPageFragment).commit();

            }
        });

        // kamera ile resim gonderme .
        chatFragment_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PhotoSendFragment photoSendFragment = new PhotoSendFragment();
                Bundle bundle = new Bundle();
                bundle.putString("friendNum", FRIEND_PHONE_NUMBER);
                bundle.putString("friendName", FRIEND_NAME);
                bundle.putInt("which",11);
                photoSendFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity,photoSendFragment).commit();

            }
        });

        // yazi yazma durumuna gore ses veya mesaj gonderme butonu aktiflesiyor ...
        chatFragment_messageField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().equals("")) { // eger mesaj yazildi ise send text tusu aktif .
                    chatFragment_floatinActionButton_sendRecord_click.setVisibility(View.INVISIBLE);
                    chatFragment_cardView_sendMessage_click.setVisibility(View.VISIBLE);
                }
                else { // eger mesaj yazilmadi ise record tusu aktif .
                    chatFragment_floatinActionButton_sendRecord_click.setVisibility(View.VISIBLE);
                    chatFragment_cardView_sendMessage_click.setVisibility(View.INVISIBLE);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        /*chatFragment_floatinActionButton_sendRecord_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ses kaydi atilmasi kismi yapilacak .
                Log.e("send record","click");


                myRef.child("BannedUsers").child(FRIEND_PHONE_NUMBER).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // eger null deger donuyorsa arkadas banli listede degil demek .
                        try {


                            String temp = snapshot.getValue().toString();
                            Log.e("mesaj","girdi");
                            Toast.makeText(getActivity(), "Kullanici maalesef banlandı. Bu yüzden mesaj gönderemezsiniz .", Toast.LENGTH_LONG).show();

                        }
                        catch (Exception e){
                            Log.e("exception sendMessage", e.getMessage());

                            sendRecord();


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });*/


        // Burada arkadas banlı degilse mesaj gonderilir . Text mesajı gonderilir .
        chatFragment_cardView_sendMessage_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chatFragment_cardView_sendMessage_click.setVisibility(View.INVISIBLE);
                chatFragment_floatinActionButton_sendRecord_click.setVisibility(View.VISIBLE);

                myRef.child("BannedUsers").child(FRIEND_PHONE_NUMBER).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // eger null deger donuyorsa arkadas banli listede degil demek .
                        try {

                            String temp = snapshot.getValue().toString();
                            Log.e("mesaj","girdi");
                            Toast.makeText(getActivity(), "Kullanici maalesef banlandı. Bu yüzden mesaj gönderemezsiniz .", Toast.LENGTH_LONG).show();

                        }
                        catch (Exception e){
                            Log.e("exception sendMessage", e.getMessage());

                            String messageText = chatFragment_messageField.getText().toString();

                            sendMessage(USER_PHONE_NUMBER, FRIEND_PHONE_NUMBER, "text", TimeClass.getClock(), "false", messageText);

                            mediaPlayerSendMessage.start();

                            chatFragment_messageField.setText("");

                            chatFragment_cardView_sendMessage_click.setVisibility(View.INVISIBLE);
                            chatFragment_floatinActionButton_sendRecord_click.setVisibility(View.VISIBLE);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        // arkadas profilini acar ...
        cardView_fragmentChat_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openFriendProfileFragment();

            }
        });


        chatFragment_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setPopupMenuForCollapse();


            }
        });


        chatFragment_attachFile_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myRef.child("BannedUsers").child(FRIEND_PHONE_NUMBER).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // eger null deger donuyorsa arkadas banli listede degil demek .
                        try {


                            String temp = snapshot.getValue().toString();
                            Toast.makeText(getActivity(), "Kullanici maalesef banlandı. Bu yüzden mesaj gönderemezsiniz .", Toast.LENGTH_LONG).show();

                        }
                        catch (Exception e){
                            Log.e("exception sendMessage", e.getMessage());

                            setPopupMenuForAttachFile();
                            
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        // ses gonderme ...
        chatFragment_floatinActionButton_sendRecord_click.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.RECORD_AUDIO}, 102 );

                }
                else {

                    if (event.getAction() == MotionEvent.ACTION_DOWN){

                        startRecording();

                    }
                    else if (event.getAction() == MotionEvent.ACTION_UP){
                        Log.e("tik","bitti");
                        stopRecording();
                        uploadRecord();

                    }

                }




                return false;
            }
        });

    }


    public void uploadRecord(){

        String randomKey = UUID.randomUUID().toString();

        StorageReference filepath = stRef.child("Audio").child(randomKey);
        Uri uri = Uri.fromFile(new File(mFileName));
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e("uri -->",String.valueOf(uri));

                        sendMessage(USER_PHONE_NUMBER, FRIEND_PHONE_NUMBER, "voice", TimeClass.getClock(), "false", String.valueOf(uri));

                    }
                });
            }
        });

    }


    /*public void playAudio(){

        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC );

        try {

            mediaPlayer.setDataSource("https://firebasestorage.googleapis.com/v0/b/matsapp-b7cc8.appspot.com/o/Audio%2Fnew_audio.3gp?alt=media&token=4106b69e-4f9b-41e6-8f4b-90e6775dc0c5");
            *//*mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    mp.start();

                }
            });*//*

            mediaPlayer.prepare();
            mediaPlayer.start();

        }
        catch (Exception e){

        }
    }*/


    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(mFileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("start Recording exceptn", "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    /*private void startPlaying() {
        Log.e("girdi","girdi");
        player = new MediaPlayer();
        try {

            Log.e("file",mFileName);
            player.setDataSource(mFileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e("LOG_TAG", "prepare() failed");
        }
    }*/

    private void stopPlaying() {
        player.release();
        player = null;
    }

    /**
     * popup menuyu gerekli gorsel nesneye yerlestirir .
     * ve menu itemlarin calisinca ne yapacaklarini tayin eder .
     */
    public void setPopupMenuForCollapse(){

        PopupMenu popupMenu = new PopupMenu(getActivity(),chatFragment_menu);
        popupMenu.getMenuInflater().inflate(R.menu.popmenu_fragmentchat_design, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){

                    case R.id.action_viewContact:
                        openFriendProfileFragment();
                        return true;

                    case R.id.action_mediaLinkDocs:

                        SharedImageFragment sharedImageFragment = new SharedImageFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("friendNum",FRIEND_PHONE_NUMBER);
                        bundle1.putString("friendName",FRIEND_NAME);
                        bundle1.putInt("whichFromFragment",1);
                        sharedImageFragment.setArguments(bundle1);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity, sharedImageFragment).commit();


                        return true;

                    case R.id.action_walpaper:

                        WalpaperFragment walpaperFragment = new WalpaperFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("friendNum",FRIEND_PHONE_NUMBER);
                        bundle.putString("friendName",FRIEND_NAME);
                        walpaperFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity, walpaperFragment).commit();


                        return true;
                    default:
                        return false;
                }



            }
        });

    }


    /**
     * popup menuyu gerekli gorsel nesneye yerlestirir .
     * ve menu itemlarin calisinca ne yapacaklarini tayin eder .
     */
    public void setPopupMenuForAttachFile(){

        PopupMenu popupMenu = new PopupMenu(getActivity(),chatFragment_attachFile_menu);
        popupMenu.getMenuInflater().inflate(R.menu.popupmenu_fragmentchat_forattachfile_design, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                PhotoSendFragment photoSendFragment = new PhotoSendFragment();
                Bundle bundle = new Bundle();

                switch (item.getItemId()){

                    case R.id.action_attachFile_camera:

                        bundle.putString("friendNum", FRIEND_PHONE_NUMBER);
                        bundle.putString("friendName", FRIEND_NAME);
                        bundle.putInt("which",11);
                        photoSendFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity,photoSendFragment).commit();

                        return true;

                    case R.id.action_attachFile__photoGallery: // galeriyi acar ve resim gonderme ekranini acar .

                        bundle.putString("friendNum", FRIEND_PHONE_NUMBER);
                        bundle.putString("friendName", FRIEND_NAME);
                        bundle.putInt("which",22);
                        photoSendFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity,photoSendFragment).commit();

                        return true;

                    case R.id.action_attachFile_contact:

                        ShareContactFragment shareContactFragment = new ShareContactFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("friendNum", FRIEND_PHONE_NUMBER);
                        bundle1.putString("friendName", FRIEND_NAME);
                        shareContactFragment.setArguments(bundle1);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity, shareContactFragment).commit();
                        return true;

                    default:
                        return false;
                }



            }
        });

    }




    /**
     * friendProfile fragment i acar .
     */
    public void openFriendProfileFragment(){

        FriendProfileFragment friendProfileFragment = new FriendProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("friendNum",FRIEND_PHONE_NUMBER);
        bundle.putString("friendName",FRIEND_NAME);
        friendProfileFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity,friendProfileFragment).commit();

    }


    /**
     * recyceler view u tanimlar .
     */
    public void defineRecyclerView(){

        chatFragment_recyclerView.setHasFixedSize(true);
        chatFragment_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new ChatFragmentAdapter(getActivity(),messageList, FRIEND_PHONE_NUMBER);
        chatFragment_recyclerView.setAdapter(adapter);


    }


    /**
     * DB den arkadas ile ilgili bilgileri alir . Ve gerekli gorsel nesnelere yerlestirir .
     */
    public void getFriendInfoFromDB(){

        myRef.child("Users").child(FRIEND_PHONE_NUMBER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                Log.e("friendName", user.toString());

                chatFragment_friendName.setText(FRIEND_NAME);
                placeOnViusalObjectLastSeen(user.getLastSeen());

                String userPPKey = user.getUserPP();

                if (!userPPKey.equals("null")){

                    stRef.child("UsersPictures").child(userPPKey).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Picasso.get().load(uri).into(chatFragment_friendPP);

                        }
                    });

                }
                else{

                    chatFragment_friendPP.setImageDrawable(getResources().getDrawable(R.drawable.default_user));

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


    /**
     * gorsel nesneye son gorulmeyi yerlestirir .
     * @param lastSeen
     */
    public void placeOnViusalObjectLastSeen(String lastSeen){

        String [] arr = lastSeen.split(",");

        if (arr[3].equals("Çevrimiçi")){

            chatFragment_friendLastSeen.setText("Çevrimiçi");

        }
        else{


            chatFragment_friendLastSeen.setText(arr[1] + " " + arr[0]);

        }


    }


    /**
     * kullanici mesaj gonderir .
     * @param userNumber --> kullanici numarasi .
     * @param friendNumber --> arkadas numarasi .
     * @param messageType --> ses mi, resim mi, text mi ?
     * @param messageDate --> mesaj atilma zamani ?
     * @param messageSeen --> ??
     * @param messageText --> mesaj kismi .
     */
    public void sendMessage(String userNumber, String friendNumber, String messageType, String messageDate, String messageSeen,String messageText){

        String messageId = myRef.child("Messages").child(userNumber).child(friendNumber).push().getKey();

        Map map = new HashMap();
        map.put("messageType", messageType);
        map.put("messageSeen", messageSeen);
        map.put("messageDate", messageDate);
        map.put("messageText", messageText);
        map.put("messageFrom", userNumber);

        myRef.child("Messages").child(userNumber).child(friendNumber).child(messageId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    myRef.child("Messages").child(friendNumber).child(userNumber).child(messageId).setValue(map);

                }

            }
        });


    }


    /**
     * atilan mesajlari listeler .
     */
    public void loadMessage(){

        myRef.child("Messages").child(USER_PHONE_NUMBER).child(FRIEND_PHONE_NUMBER).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Message message = dataSnapshot.getValue(Message.class);
                messageList.add(message);
                adapter.notifyDataSetChanged();
                messageKey.add(dataSnapshot.getKey());
                chatFragment_recyclerView.scrollToPosition(messageList.size()-1); // bu satir ile son mesaja dogru kaydirmis olcaz ...


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * kullanici engelli ise kim engellemişse ona gore mesaj yazilir ve gorsel nesneler invisible yapilir .
     * @param userPhone
     * @param friendPhone
     * @param message --> verilmek istenen mesaj .
     */
    public void blockFromFriendControl(String userPhone, String friendPhone, String message){

        myRef.child("BlockFromFriend").child(userPhone).child(friendPhone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {

                    String temp = snapshot.getValue().toString();

                    chatFragment_messageField.setVisibility(View.INVISIBLE);
                    chatFragment_floatinActionButton_sendRecord_click.setClickable(false);
                    chatFragment_camera.setVisibility(View.INVISIBLE);
                    chatFragment_attachFile_menu.setVisibility(View.INVISIBLE);

                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                }
                catch (Exception e){

                    Log.e("exception bloFrm contrl", e.getMessage());

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    /**
     * kullanici walpaper secmisse onu recy arkaplanina yerlestirir.
     * secmemisse default walpaper i yerlestirir .
     */
    public void getWalpaperForRecycBackground(){

        myRef.child("FriendsWallpaper").child(USER_PHONE_NUMBER).child(FRIEND_PHONE_NUMBER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                FriendWalpaper friendWalpaper = snapshot.getValue(FriendWalpaper.class);

                // eger db de kayit yoksa null doner. Yani default arkaplan uygulanir .
                try {

                    String walpaperKey = friendWalpaper.getFriendWalpaperKey();
                    String fromWalpaper = friendWalpaper.getFromWalpaper();

                    if (fromWalpaper.equals("fromApp")){ // app in icinde olan walpaper i secerse ...

                        chatFragment_recyclerView.setBackground(getActivity().getResources().getDrawable(Integer.valueOf(walpaperKey)));

                    }
                    else { // galeriden secerse ...

                    }

                }
                catch (Exception e){ // default arkaplan kismi .

                    Log.e("exception getWallpaper", e.getMessage());

                    myRef.child("Users").child(USER_PHONE_NUMBER).child("userWallpaper").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String wallpaperKey = snapshot.getValue().toString();

                            chatFragment_recyclerView.setBackgroundResource(Integer.valueOf(wallpaperKey));

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });






                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    /**
     * arkadastan mesaj geldiginde gelen bildirim sesini oynatir .
     */
    public void controlMessageForAudio(){
        Log.e("listener :::", "calisti");
        myRef.child("Messages").child(USER_PHONE_NUMBER).child(FRIEND_PHONE_NUMBER).limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("listener :::", "sdfsfsdfsdsdfsdf");
                for (DataSnapshot postSnaphot : snapshot.getChildren()){

                    String fromMessage = postSnaphot.child("messageFrom").getValue().toString();

                    if (fromMessage.equals(FRIEND_PHONE_NUMBER)){

                        mediaPlayerReceivedMessage.start();

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }












}
