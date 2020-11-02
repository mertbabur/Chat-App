package com.example.matsapp.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matsapp.Fragments.ChatFragment;
import com.example.matsapp.Models.User;
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

public class UserFriendPhoneListAdapter extends RecyclerView.Adapter<UserFriendPhoneListAdapter.CardHolder>{

    private Context mContext;
    private List<UserFriend> friendsPhoneList;
    private int whichFragment;

    private FirebaseStorage storage;
    private StorageReference stRef;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private String USER_PHONE_NUMBER;

    private static final int USER_FRIEND_PHONE_LIST_FRAGMENT = 0;
    private static final int CHAT_LIST_FRAGMENT = 1;

    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor editor;



    public UserFriendPhoneListAdapter(Context mContext, List<UserFriend> friendsPhoneList, int whicFragment) {
        this.mContext = mContext;
        this.friendsPhoneList = friendsPhoneList;
        this.whichFragment = whicFragment;
    }


    // cardView_textView_userName --> arkadas ismi ...
    public class CardHolder extends RecyclerView.ViewHolder{

        //private ImageView cardView_imageView_userPP;
        private TextView cardView_textView_userName, cardView_textView_userStatus, cardView_textView_friendNum;
        private CardView cardView_userFriendsPhoneList;
        private ImageView cardView_imageView_userPP;


        public CardHolder(@NonNull View itemView) {
            super(itemView);

            defineAttributies();

        }

        public void defineAttributies(){

            cardView_imageView_userPP = itemView.findViewById(R.id.fragmentChat_friendPP);
            cardView_textView_userName = itemView.findViewById(R.id.cardView_textView_userName);
            cardView_textView_userStatus = itemView.findViewById(R.id.cardView_textView_userStatus);
            cardView_userFriendsPhoneList = itemView.findViewById(R.id.cardView_userFriendsPhoneList);
            cardView_textView_friendNum = itemView.findViewById(R.id.cardView_textView_friendNum);

            USER_PHONE_NUMBER = mContext.getSharedPreferences("userInfo",mContext.MODE_PRIVATE).getString("phoneNum","1").toString();

            database = FirebaseDatabase.getInstance();
            myRef = database.getReference();

            storage = FirebaseStorage.getInstance();
            stRef = storage.getReference();

        }


    }


    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_user_friends_phone_list_design,parent,false);

        return new CardHolder(itemView);

    }


    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {

        UserFriend userFriend = friendsPhoneList.get(position);

        String friendPhone = userFriend.getFriendPhone();
        String friendName = userFriend.getFriendName();

        holder.cardView_textView_userName.setText(friendName);

        myRef.child("Users").child(friendPhone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = new User();
                user = dataSnapshot.getValue(User.class);
                String friendState = user.getUserState(); // durum ve tarih seklinde gelecek split etmemiz gerekir .
                String friendNum = user.getUserPhone();
                String userPPKey = user.getUserPP();

                if (!userPPKey.equals("null")){

                    stRef.child("UsersPictures").child(userPPKey).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Picasso.get().load(uri).into(holder.cardView_imageView_userPP);

                        }
                    });

                }
                else{

                    holder.cardView_imageView_userPP.setImageDrawable(mContext.getResources().getDrawable(R.drawable.default_user));


                }

                if (whichFragment == USER_FRIEND_PHONE_LIST_FRAGMENT) // eger userFriendPhoneList fragmentindan geldiyse ...
                    holder.cardView_textView_userStatus.setText(friendState);
                else
                    getLastMessage(USER_PHONE_NUMBER,friendNum,holder); // durum icin text e en son mesajı yerlesitirir .

                holder.cardView_textView_friendNum.setText(friendNum);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.cardView_userFriendsPhoneList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String friendNum = holder.cardView_textView_friendNum.getText().toString();
                String friendName = holder.cardView_textView_userName.getText().toString(); // arkadas name ...

                ChatFragment chatFragment = new ChatFragment();

                Bundle bundle = new Bundle();
                bundle.putString("friendNum", friendNum);
                bundle.putString("friendName", friendName);
                chatFragment.setArguments(bundle);

                mSharedPrefs = mContext.getSharedPreferences("friendInfo",mContext.MODE_PRIVATE);
                editor = mSharedPrefs.edit();
                editor.putString("friendNum", friendNum);
                editor.putString("friendName", friendName);
                editor.commit();



                ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity,chatFragment).commit();

            }
        });



    }


    @Override
    public int getItemCount() {
        return friendsPhoneList.size();
    }


    /**
     * Son atilan mesaji durum textine yerlestirir .
     * @param userNum --> telefona giris yapan numara .
     * @param friendNum --> arkadas numara .
     * @param holder
     */
    public void getLastMessage(String userNum, String friendNum, CardHolder holder){


        myRef.child("Messages").child(friendNum).child(userNum).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int messageCount = (int)snapshot.getChildrenCount();
                int counter = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()){
                    counter++;

                    if (counter == messageCount) {

                        String messageType = postSnapshot.child("messageType").getValue().toString();

                        if (messageType.equals("text")){ // eger text mesaji ise direk text i yerlestir .

                            String lastMessage = postSnapshot.child("messageText").getValue().toString();
                            holder.cardView_textView_userStatus.setText(lastMessage);

                        }
                        else if (messageType.equals("imageText")){ // eger image ile text varsa elimde arr olacak .

                            String[] arr = postSnapshot.child("messageText").getValue().toString().split(",");
                            String lastMessage = arr[1];
                            holder.cardView_textView_userStatus.setText(lastMessage);

                        }
                        else if(messageType.equals("image")){ // sadece image gonderildi ise "RESİM" yazisini yerlestir .

                            holder.cardView_textView_userStatus.setText("RESİM");

                        }
                        else if (messageType.equals("voice")){

                            holder.cardView_textView_userStatus.setText("VOICE");

                        }
                        else{ // bu diger paylasim cesitleri icin devam edecek .

                        }


                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }







}
