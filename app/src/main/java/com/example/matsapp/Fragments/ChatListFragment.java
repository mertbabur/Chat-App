package com.example.matsapp.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matsapp.Activities.MainActivity;
import com.example.matsapp.Adapters.UserFriendPhoneListAdapter;
import com.example.matsapp.Models.User;
import com.example.matsapp.Models.UserFriend;
import com.example.matsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {

    private RecyclerView recyclerView_fragmentChatsList;
    private FloatingActionButton floatingActionButton_fragmentChatsList;

    private List<UserFriend> friendList;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private String USER_PHONE_NUMBER;

    private UserFriendPhoneListAdapter adapter;

    private static final int CHAT_LIST_FRAGMENT = 1; // adapterde hangi fragmenttan geldigini anlamamiza yarayacak .


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chats_list_design,container,false);

        defineAttributies(rootView);
        actionAttributies();

        permissionsForStorage();

        defineRecylerView();

        return rootView;

    }


    public void defineAttributies(View rootView){

        recyclerView_fragmentChatsList = rootView.findViewById(R.id.recyclerView_fragmentChatsList);
        floatingActionButton_fragmentChatsList = rootView.findViewById(R.id.floatingActionButton_fragmentChatsList);

        friendList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        USER_PHONE_NUMBER = getActivity().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE).getString("phoneNum","1");


    }


    public void actionAttributies(){

        // arkadas listesini acmak icin ...
        floatingActionButton_fragmentChatsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserFriendPhoneListFragment userFriendPhoneListFragment = new UserFriendPhoneListFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity,userFriendPhoneListFragment).commit();

            }
        });


    }


    /**
     * Messages kismindan friend numlari alir ve userFriend kisminddanda bu num ile bilgileri cekerek list atar .
     * Daha sonra burada recylerView tanimlanir .
     */
    public void defineRecylerView(){


        myRef.child("Messages").child(USER_PHONE_NUMBER).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String friendNum = snapshot.getKey();

                myRef.child("UserFriends").child(USER_PHONE_NUMBER).child(friendNum).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        UserFriend friend = snapshot.getValue(UserFriend.class);


                        try { // arkadastan geldi ise ...

                            String temp = friend.getFriendName();

                            Log.e("aa", friend.getFriendName() + " " + friend.getFriendPhone());

                            friendList.add(friend);
                            //adapter.notifyDataSetChanged();

                            Log.e("num:",friendList.size()+"");


                            recyclerView_fragmentChatsList.setHasFixedSize(true);
                            recyclerView_fragmentChatsList.setLayoutManager(new LinearLayoutManager(getActivity()));

                            adapter = new UserFriendPhoneListAdapter(getActivity(),friendList, CHAT_LIST_FRAGMENT);
                            recyclerView_fragmentChatsList.setAdapter(adapter);



                        }
                        catch (Exception e){ // tanimadigimiz bir numaradan geldi ise ...

                            myRef.child("Users").child(friendNum).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    User user = snapshot.getValue(User.class);
                                    //friend.setFriendName("no");
                                    //friend.setFriendPhone(friendNum);

                                    if (!user.getUserName().equals("null")) // ismi null degilse ...
                                        friendList.add(new UserFriend(user.getUserName(), friendNum));
                                    else // ismi nullsa numara goster ...
                                        friendList.add(new UserFriend(friendNum, friendNum));


                                    recyclerView_fragmentChatsList.setHasFixedSize(true);
                                    recyclerView_fragmentChatsList.setLayoutManager(new LinearLayoutManager(getActivity()));

                                    adapter = new UserFriendPhoneListAdapter(getActivity(),friendList, CHAT_LIST_FRAGMENT);
                                    recyclerView_fragmentChatsList.setAdapter(adapter);

                                    return;

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });// myRef sonu ...



                        }// catch sonu ...

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //myRef sonu ...

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        /*myRef.child("Messages").child(USER_PHONE_NUMBER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    String friendNum = postSnapshot.getKey();

                    //Log.e("num:",friendNum);

                    myRef.child("UserFriends").child(USER_PHONE_NUMBER).child(friendNum).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            UserFriend friend = snapshot.getValue(UserFriend.class);


                            try { // arkadastan geldi ise ...

                                String temp = friend.getFriendName();

                                Log.e("aa", friend.getFriendName() + " " + friend.getFriendPhone());

                                friendList.add(friend);
                                //adapter.notifyDataSetChanged();

                                Log.e("num:",friendList.size()+"");


                                recyclerView_fragmentChatsList.setHasFixedSize(true);
                                recyclerView_fragmentChatsList.setLayoutManager(new LinearLayoutManager(getActivity()));

                                adapter = new UserFriendPhoneListAdapter(getActivity(),friendList, CHAT_LIST_FRAGMENT);
                                recyclerView_fragmentChatsList.setAdapter(adapter);



                            }
                            catch (Exception e){ // tanimadigimiz bir numaradan geldi ise ...

                                myRef.child("Users").child(friendNum).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        User user = snapshot.getValue(User.class);
                                        //friend.setFriendName("no");
                                        //friend.setFriendPhone(friendNum);

                                        if (!user.getUserName().equals("null")) // ismi null degilse ...
                                            friendList.add(new UserFriend(user.getUserName(), friendNum));
                                        else // ismi nullsa numara goster ...
                                            friendList.add(new UserFriend(friendNum, friendNum));


                                        recyclerView_fragmentChatsList.setHasFixedSize(true);
                                        recyclerView_fragmentChatsList.setLayoutManager(new LinearLayoutManager(getActivity()));

                                        adapter = new UserFriendPhoneListAdapter(getActivity(),friendList, CHAT_LIST_FRAGMENT);
                                        recyclerView_fragmentChatsList.setAdapter(adapter);

                                        return;

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });// myRef sonu ...



                            }// catch sonu ...

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    //myRef sonu ...


                }// for sonu ...


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/ // for lu olan ...

    }


    /**
     * rehbere erisim icin izin ister .
     */
    public void permissionsForStorage(){

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 104 );

        }

    }

}
