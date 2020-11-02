package com.example.matsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matsapp.Activities.StoriesActivity;
import com.example.matsapp.Models.Story;
import com.example.matsapp.Models.UserFriend;
import com.example.matsapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendStatusAdapter extends RecyclerView.Adapter<FriendStatusAdapter.CardHolder>{

    private Context mContext;
    private List<UserFriend> userFriendList;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private FirebaseStorage storage;
    private StorageReference stRef;

    private String USER_PHONE_NUMBER;


    public FriendStatusAdapter(Context mContext, List<UserFriend> userFriendList) {
        this.mContext = mContext;
        this.userFriendList = userFriendList;

        USER_PHONE_NUMBER = mContext.getSharedPreferences("userInfo",mContext.MODE_PRIVATE).getString("phoneNum", "bos num");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        storage = FirebaseStorage.getInstance();
        stRef = storage.getReference();
    }

    public class CardHolder extends RecyclerView.ViewHolder{

        ImageView imageView_cardView_friendStatusList_userPP;
        TextView textView_cardView_friendStatusList_userName;
        CardView cardView_friendStatusList;


        public CardHolder(@NonNull View itemView) {
            super(itemView);

            imageView_cardView_friendStatusList_userPP = itemView.findViewById(R.id.imageView_cardView_friendStatusList_userPP);
            textView_cardView_friendStatusList_userName = itemView.findViewById(R.id.textView_cardView_friendStatusList_userName);
            cardView_friendStatusList = itemView.findViewById(R.id.cardView_friendStatusList);

        }

    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_friend_status_list_design, parent, false);

        return new CardHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {

        UserFriend  userFriend = userFriendList.get(position);

        setUserInfoToAttributes(holder, userFriend);

        holder.cardView_friendStatusList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, StoriesActivity.class);
                intent.putExtra("phoneNum", userFriend.getFriendPhone());
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return userFriendList.size();
    }


    /**
     * gerekli gorsel nesnelere bilgileri yerlestirir .
     */
    public void setUserInfoToAttributes(CardHolder holder, UserFriend userFriend){

        String friendName = userFriend.getFriendName();
        String phoneNumber = userFriend.getFriendPhone();

        holder.textView_cardView_friendStatusList_userName.setText(friendName);

        // kullanici pp sini yerlestir .
        myRef.child("Users").child(phoneNumber).child("userPP").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String ppKey = snapshot.getValue().toString();

                stRef.child("UsersPictures").child(ppKey).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Picasso.get().load(uri).into(holder.imageView_cardView_friendStatusList_userPP);

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}
