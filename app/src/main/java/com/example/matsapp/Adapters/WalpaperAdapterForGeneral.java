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
import com.example.matsapp.Fragments.MainUserPageFragment;
import com.example.matsapp.Fragments.WalpaperFragment;
import com.example.matsapp.Models.FriendWalpaper;
import com.example.matsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class WalpaperAdapterForGeneral extends RecyclerView.Adapter<WalpaperAdapterForGeneral.CardHolder>{

    private Context mContext;
    private List<Integer> walpaperList;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private String USER_PHONE_NUMBER;

    public WalpaperAdapterForGeneral(Context mContext, List<Integer> walpaperList) {
        this.mContext = mContext;
        this.walpaperList = walpaperList;

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

                int walpaperId = walpaperList.get(position);

                holder.imageView_cardView_walpaper.setImageDrawable(mContext.getResources().getDrawable(walpaperId));

                holder.cardView_walpaper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        saveWalpaperKeyToDBForHasApp(walpaperId);

                        MainUserPageFragment mainUserPageFragment = new MainUserPageFragment();
                        ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity, mainUserPageFragment).commit();


                    }
                });


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

        myRef.child("Users").child(USER_PHONE_NUMBER).child("userWallpaper").setValue(String.valueOf(walpaperId));

    }


    /**
     * walpaper secili ise gorsel nesne visible hale getirilir .
     */
    public void isSelectedWallpaper(CardHolder holder, String wallpaperKey) {

        myRef.child("Users").child(USER_PHONE_NUMBER).child("userWallpaper").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                    String key = snapshot.getValue().toString();

                    if (key.equals(wallpaperKey)) {

                        holder.imageView_cardView_walpaper_selectedWall.setVisibility(View.VISIBLE);

                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }




}
