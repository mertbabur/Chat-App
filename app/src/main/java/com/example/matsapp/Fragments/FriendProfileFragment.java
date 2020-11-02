package com.example.matsapp.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.matsapp.Adapters.FriendImageMessageAdapter;
import com.example.matsapp.Models.Message;
import com.example.matsapp.Models.User;
import com.example.matsapp.R;
import com.example.matsapp.Utils.UserDeleteClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FriendProfileFragment extends Fragment {

    private ImageView imageView_friendProfileFragment_back, imageView_friendProfileFragment_collapse, imageView_friendProfileFragment_informAgain_image;

    private TextView textView_friendProfileFragment_friendName, textView_friendProfileFragment_friendState
            , textView_friendProfileFragment_friendAbout, textView_friendProfileFragment_firendAboutDate
            , textView_friendProfileFragment_friendPhoneNumber, textView_friendProfileFragment_block_click
            , textView_friendProfileFragment_informAgain_click;

    private ImageView imageView_friendProfileFragment_friendPP, imageView_fragmentFriendProfile_goToSentImages;

    private RecyclerView recyclerView_fragmentFriendProfile;
    private FriendImageMessageAdapter adapter;

    private List<Message> messageList;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private String FRIEND_PHONE_NUMBER; // ChatFragmenttan gelen friendNum bundle i ...
    private String FRIEND_NAME; // ChatFragmenttan gelen friendName bundle i ...
    private String USER_PHONE_NUMBER;

    private FirebaseStorage storage;
    private StorageReference stRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_friend_profile_design,container,false);

        defineAttributies(rootView);

        isInformAgainImageControl();

        isBlocFromFriendControl();

        actionAttributies();

        setFriendInfoToAttributies();

        getMedia();

        return rootView;

    }

    public void defineAttributies(View rootView){

        imageView_friendProfileFragment_back = rootView.findViewById(R.id.imageView_friendProfileFragment_back);
        imageView_friendProfileFragment_collapse = rootView.findViewById(R.id.imageView_friendProfileFragment_collapse);
        textView_friendProfileFragment_friendName = rootView.findViewById(R.id.textView_friendProfileFragment_friendName);
        textView_friendProfileFragment_friendState = rootView.findViewById(R.id.textView_friendProfileFragment_friendState);
        textView_friendProfileFragment_friendAbout = rootView.findViewById(R.id.textView_friendProfileFragment_friendAbout);
        textView_friendProfileFragment_firendAboutDate = rootView.findViewById(R.id.textView_friendProfileFragment_firendAboutDate);
        textView_friendProfileFragment_friendPhoneNumber = rootView.findViewById(R.id.textView_friendProfileFragment_friendPhoneNumber);
        textView_friendProfileFragment_block_click = rootView.findViewById(R.id.textView_friendProfileFragment_block_click);
        textView_friendProfileFragment_informAgain_click = rootView.findViewById(R.id.textView_friendProfileFragment_informAgain_click);
        imageView_friendProfileFragment_friendPP = rootView.findViewById(R.id.imageView_friendProfileFragment_friendPP);
        imageView_friendProfileFragment_informAgain_image = rootView.findViewById(R.id.imageView_friendProfileFragment_informAgain_image);
        imageView_fragmentFriendProfile_goToSentImages = rootView.findViewById(R.id.imageView_fragmentFriendProfile_goToSentImages);
        recyclerView_fragmentFriendProfile = rootView.findViewById(R.id.recyclerView_fragmentFriendProfile);

        messageList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        storage = FirebaseStorage.getInstance();
        stRef = storage.getReference();

        FRIEND_PHONE_NUMBER = getArguments().getString("friendNum","1");
        FRIEND_NAME = getArguments().getString("friendName","2");
        USER_PHONE_NUMBER = getActivity().getSharedPreferences("userInfo",getActivity().MODE_PRIVATE).getString("phoneNum","1");

    }


    public void actionAttributies(){

        imageView_friendProfileFragment_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("arkiNum",FRIEND_PHONE_NUMBER);

                ChatFragment chatFragment = new ChatFragment();
                Bundle bundle = new Bundle();
                bundle.putString("friendNum",FRIEND_PHONE_NUMBER);
                bundle.putString("friendName",FRIEND_NAME);
                chatFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity,chatFragment).commit();

            }
        });

        textView_friendProfileFragment_block_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (textView_friendProfileFragment_block_click.getText().toString().equals("Engelle")) {

                    myRef.child("BlockFromFriend").child(USER_PHONE_NUMBER).child(FRIEND_PHONE_NUMBER).setValue(FRIEND_PHONE_NUMBER);

                }
                else {

                    myRef.child("BlockFromFriend").child(USER_PHONE_NUMBER).child(FRIEND_PHONE_NUMBER).removeValue();

                }


            }
        });

        textView_friendProfileFragment_informAgain_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (textView_friendProfileFragment_informAgain_click.getText().toString().trim().equals("Kişiyi şikayet et")) { // db ye sikayet et yaz .

                    myRef.child("Complaints").child(FRIEND_PHONE_NUMBER).child(USER_PHONE_NUMBER).setValue(USER_PHONE_NUMBER);

                }
                else if(textView_friendProfileFragment_informAgain_click.getText().toString().trim().equals("Arkadaşınız maalesef banlandı")){

                    textView_friendProfileFragment_informAgain_click.setClickable(false);

                }
                else { // db den sikayeti kaldir .

                    myRef.child("Complaints").child(FRIEND_PHONE_NUMBER).child(USER_PHONE_NUMBER).removeValue();

                }


                complainingControl();

            }
        });

        imageView_friendProfileFragment_collapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setPopupMenu();

            }
        });

        imageView_fragmentFriendProfile_goToSentImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedImageFragment sharedImageFragment = new SharedImageFragment();
                Bundle bundle = new Bundle();
                bundle.putString("friendNum",FRIEND_PHONE_NUMBER);
                bundle.putString("friendName",FRIEND_NAME);
                bundle.putInt("whichFromFragment",2);
                sharedImageFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity, sharedImageFragment).commit();

            }
        });


    }



    public void complainingControl() {

        myRef.child("Complaints").child(FRIEND_PHONE_NUMBER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getChildrenCount() == 100){

                    Log.e("sınır","sınıra ulasildi");

                    UserDeleteClass.deleteComplaints(myRef, FRIEND_PHONE_NUMBER);

                    myRef.child("BannedUsers").child(FRIEND_PHONE_NUMBER).setValue(FRIEND_PHONE_NUMBER);


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }




    public void setFriendInfoToAttributies(){

        textView_friendProfileFragment_friendName.setText(FRIEND_NAME);
        textView_friendProfileFragment_friendPhoneNumber.setText(FRIEND_PHONE_NUMBER);

        myRef.child("Users").child(FRIEND_PHONE_NUMBER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                textView_friendProfileFragment_friendAbout.setText(user.getUserState());
                textView_friendProfileFragment_firendAboutDate.setText(user.getUserStateDate());

                setToAttributeLastSeen();


                String userPPKey = user.getUserPP();

                if (!userPPKey.equals("null")){

                    stRef.child("UsersPictures").child(userPPKey).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Picasso.get().load(uri).into(imageView_friendProfileFragment_friendPP);

                        }
                    });

                }
                else{

                    imageView_friendProfileFragment_friendPP.setImageDrawable(getResources().getDrawable(R.drawable.default_user));

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void setToAttributeLastSeen() {

        myRef.child("Users").child(FRIEND_PHONE_NUMBER).child("lastSeen").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String lastSeen = snapshot.getValue().toString();
                String[] arr = lastSeen.split(",");

                if (arr[3].equals("Çevrimiçi")){

                    textView_friendProfileFragment_friendState.setText("Çevrimiçi");

                }
                else{

                    textView_friendProfileFragment_friendState.setText(arr[1] + " " + arr[0]);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    /**
     * Sikayet et resmini kontrol eder .
     * Sikayet edilme durumuna gore görsel nesnelere uygun yazi ve resmi yerlestirir .
     */
    public void isInformAgainImageControl(){



            myRef.child("Complaints").child(FRIEND_PHONE_NUMBER).child(USER_PHONE_NUMBER).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    try{

                        String phoneNum = snapshot.getValue().toString();
                        imageView_friendProfileFragment_informAgain_image.setImageDrawable(getResources().getDrawable(R.drawable.not_inform_again_icon));
                        textView_friendProfileFragment_informAgain_click.setText("Şikayeti geri al");
                        Log.e("a","1");

                    }
                    catch (Exception e) {

                        Log.e("exception informAgain", e.getMessage());

                        imageView_friendProfileFragment_informAgain_image.setImageDrawable(getResources().getDrawable(R.drawable.inform_again_icon));

                        //Burada kullanici banli mi buna bakildi .
                        myRef.child("BannedUsers").child(FRIEND_PHONE_NUMBER).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                try{

                                    String temp = snapshot.getValue().toString();

                                    textView_friendProfileFragment_informAgain_click.setText("Arkadaşınız maalesef banlandı");

                                }
                                catch (Exception e){
                                    Log.e("exception bannedUsers",e.getMessage());
                                    textView_friendProfileFragment_informAgain_click.setText("Kişiyi şikayet et");
                                    Log.e("a", "2");

                                }
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
     * kullanici block durumuna gore gorsel nesnelere uygun yazilar yerlestirilir .
     */
    public void isBlocFromFriendControl(){

        // eger kullanici block listesinde degilse null doner ve catch e duser .
        myRef.child("BlockFromFriend").child(USER_PHONE_NUMBER).child(FRIEND_PHONE_NUMBER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {

                    String temp = snapshot.getValue().toString();

                    textView_friendProfileFragment_block_click.setText("Kullanıcının engelini kaldır");

                }
                catch (Exception e){


                    Log.e("exception isBlockFromFr", e.getMessage());

                    textView_friendProfileFragment_block_click.setText("Engelle");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    /**
     * popup menuyu gerekli gorsel nesneye yerlestirir .
     * ve menu itemlarin calisinca ne yapacaklarini tayin eder .
     */
    public void setPopupMenu(){

        PopupMenu popupMenu = new PopupMenu(getActivity(),imageView_friendProfileFragment_collapse);
        popupMenu.getMenuInflater().inflate(R.menu.popupmenu_fragmentfriendprofile_design, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){

                    case R.id.action_deleteChat:
                        deleteChat();
                        return true;

                    default:
                        return false;
                }



            }
        });

    }


    /**
     * Firebaseden gerekli sohbeti siler .
     */
    public void deleteChat() {

        myRef.child("Messages").child(USER_PHONE_NUMBER).child(FRIEND_PHONE_NUMBER).removeValue();


    }


    /**
     * Arkadas ile paylasilan max 10 tane iamge list e alinir.
     */
    public void getMedia(){

        myRef.child("Messages").child(USER_PHONE_NUMBER).child(FRIEND_PHONE_NUMBER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()){

                    Message message = postSnapshot.getValue(Message.class);

                    if (message.getMessageType().equals("image") || message.getMessageType().equals("imageText")){

                        messageList.add(message);

                    }

                    if (messageList.size() == 10) // 10 tane image a ulasti ise ...
                        break;

                }

                recyclerView_fragmentFriendProfile.setHasFixedSize(true);
                recyclerView_fragmentFriendProfile.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
                adapter = new FriendImageMessageAdapter(getActivity(), messageList, FRIEND_NAME, FRIEND_PHONE_NUMBER, 2); // 2 --> bu fragmenttan gidildigini soyler .
                recyclerView_fragmentFriendProfile.setAdapter(adapter);

                Log.e("szeee", messageList.size()+"");



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }





}
