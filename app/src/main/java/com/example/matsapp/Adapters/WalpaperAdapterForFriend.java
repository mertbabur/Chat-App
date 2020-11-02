package com.example.matsapp.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matsapp.Fragments.ChatFragment;
import com.example.matsapp.Models.FriendWalpaper;
import com.example.matsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class WalpaperAdapterForFriend extends RecyclerView.Adapter<WalpaperAdapterForFriend.CardHolder>{

    private Context mContext;
    private List<Integer> walpaperList;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private String USER_PHONE_NUMBER;
    private String FRIEND_PHONE_NUMBER;
    private String FRIEND_NAME;

    public WalpaperAdapterForFriend(Context mContext, List<Integer> walpaperList, String FRIEND_PHONE_NUMBER, String FRIEND_NAME) {
        this.mContext = mContext;
        this.walpaperList = walpaperList;
        this.FRIEND_PHONE_NUMBER = FRIEND_PHONE_NUMBER;
        this.FRIEND_NAME = FRIEND_NAME;

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        USER_PHONE_NUMBER = mContext.getSharedPreferences("userInfo",mContext.MODE_PRIVATE).getString("phoneNum","numara yok");


    }

    public class CardHolder extends RecyclerView.ViewHolder{

        private CardView cardView_walpaper;
        private ImageView imageView_cardView_walpaper, imageView_cardView_walpaper_selectedWall;

        public CardHolder(@NonNull View itemView) {
            super(itemView);

            cardView_walpaper = itemView.findViewById(R.id.cardView_walpaper);
            imageView_cardView_walpaper = itemView.findViewById(R.id.imageView_cardView_walpaper);
            imageView_cardView_walpaper_selectedWall = itemView.findViewById(R.id.imageView_cardView_walpaper_selectedWall);


        }
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_walpaper_desing, parent, false);

        return new CardHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {

        int walpaperId = walpaperList.get(position);

        holder.imageView_cardView_walpaper.setImageDrawable(mContext.getResources().getDrawable(walpaperId));

        isSelectedWallpaper(holder, String.valueOf(walpaperId));

        holder.cardView_walpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveWalpaperKeyToDBForHasApp(walpaperId);

                ChatFragment chatFragment = new ChatFragment();
                Bundle bundle = new Bundle();
                bundle.putString("friendNum",FRIEND_PHONE_NUMBER);
                bundle.putString("friendName", FRIEND_NAME);
                chatFragment.setArguments(bundle);
                ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity, chatFragment).commit();


            }
        });




    }

    @Override
    public int getItemCount() {
        return walpaperList.size();
    }


    /**
     * Secilen walpaperin keyini database e kaydeder .
     */
    public void saveWalpaperKeyToDBForHasApp(int walpaperId){

        FriendWalpaper friendWalpaper = new FriendWalpaper(String.valueOf(walpaperId), "fromApp");
        myRef.child("FriendsWallpaper").child(USER_PHONE_NUMBER).child(FRIEND_PHONE_NUMBER).setValue(friendWalpaper);

    }


    /**
     * walpaper secili ise gorsel nesne visible hale getirilir .
     */
    public void isSelectedWallpaper(CardHolder holder, String wallpaperKey) {

            myRef.child("FriendsWallpaper").child(USER_PHONE_NUMBER).child(FRIEND_PHONE_NUMBER).child("friendWalpaperKey").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    // eger wallpaper kaydi yoksa null doner catch e duser .
                    try { // ozel sectigimizi secili hale getirir .

                        String key = snapshot.getValue().toString();

                        if (key.equals(wallpaperKey)) {

                            holder.imageView_cardView_walpaper_selectedWall.setVisibility(View.VISIBLE);

                        }

                    } catch (Exception e) {// genel wallpaper secili ...

                        Log.e("exceptn isSelectedWall", e.getMessage());

                        myRef.child("Users").child(USER_PHONE_NUMBER).child("userWallpaper").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String defaultWallpaperKey = snapshot.getValue().toString();

                                if (defaultWallpaperKey.equals(wallpaperKey)){

                                    holder.imageView_cardView_walpaper_selectedWall.setVisibility(View.VISIBLE);

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





}
