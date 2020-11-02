package com.example.matsapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.matsapp.Adapters.FriendImageMessageAdapter;
import com.example.matsapp.Models.Message;
import com.example.matsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SharedImageFragment extends Fragment {

    private RecyclerView recyclerView_fragmentSharedImage;
    private ImageView imageView_fragmentSharedImage_backClick;
    private TextView textView_fragmentSharedImage_friendName;

    private static final int FROM_CHAT_FRAGMENT = 1;
    private static final int FROM_FRIEND_PROFILE = 2;

    private String friendNum;
    private String friendName;
    private int whichFromFragment;

    private String USER_PHONE_NUMBER;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private List<Message> messageList;
    private FriendImageMessageAdapter adapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sent_image_design, container, false);

        defineAttributes(rootView);
        actionAttributes();
        getMedia();

        return rootView;
    }


    public void defineAttributes(View rootView){

        recyclerView_fragmentSharedImage = rootView.findViewById(R.id.recyclerView_fragmentSharedImage);
        imageView_fragmentSharedImage_backClick = rootView.findViewById(R.id.imageView_fragmentSharedImage_backClick);
        textView_fragmentSharedImage_friendName = rootView.findViewById(R.id.textView_fragmentSharedImage_friendName);

        friendNum = getArguments().getString("friendNum", "bos num");
        friendName = getArguments().getString("friendName", "bos name");
        whichFromFragment = getArguments().getInt("whichFromFragment", 0);

        USER_PHONE_NUMBER = getActivity().getSharedPreferences("userInfo",getActivity().MODE_PRIVATE).getString("phoneNum","bos user num");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        messageList = new ArrayList<>();


    }


    public void actionAttributes(){

        imageView_fragmentSharedImage_backClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (whichFromFragment == FROM_CHAT_FRAGMENT){ // chatFragmenttan geldi ise ...

                    openChatFragment();

                }
                else { // friendProfileFragmenttan geldi ise ...

                    openUserProfileFragment();

                }



            }
        });


    }


    /**
     * UserProfileFragment i acar .
     */
    public void openUserProfileFragment(){

        FriendProfileFragment friendProfileFragment = new FriendProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("friendNum", friendNum);
        bundle.putString("friendName", friendName);
        friendProfileFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity, friendProfileFragment).commit();

    }


    /**
     * ChatFragment i acar .
     */
    public void openChatFragment(){

        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("friendNum", friendNum);
        bundle.putString("friendName", friendName);
        chatFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity, chatFragment).commit();


    }


    /**
     * Arkadas ile mesajlasilan imageleri list e depolar .
     * arkadasin ismini toolbarimsi yapiya yerlestirir .
     */
    public void getMedia(){

        textView_fragmentSharedImage_friendName.setText(friendName);

        myRef.child("Messages").child(USER_PHONE_NUMBER).child(friendNum).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()){

                    Message message = postSnapshot.getValue(Message.class);

                    if (message.getMessageType().equals("image") || message.getMessageType().equals("imageText")){

                        messageList.add(message);

                    }

                }

                recyclerView_fragmentSharedImage.setHasFixedSize(true);
                recyclerView_fragmentSharedImage.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
                adapter = new FriendImageMessageAdapter(getActivity(), messageList, friendName, friendNum, whichFromFragment);
                recyclerView_fragmentSharedImage.setAdapter(adapter);

                Log.e("szeee", messageList.size()+"");



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



}
