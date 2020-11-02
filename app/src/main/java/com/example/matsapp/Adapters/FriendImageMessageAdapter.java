package com.example.matsapp.Adapters;

import android.content.Context;
import android.net.Uri;
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

import com.example.matsapp.Fragments.SentImageShowFragment;
import com.example.matsapp.Models.Message;
import com.example.matsapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendImageMessageAdapter extends RecyclerView.Adapter<FriendImageMessageAdapter.CardHolder>{

    private Context mContext;
    private List<Message> messageList;
    private String friendName;
    private String friendNum;
    private int whichFromFragment;

    private FirebaseStorage storage;
    private StorageReference stRef;

    public FriendImageMessageAdapter(Context mContext, List<Message> messageList, String friendName, String friendNum, int whichFromFragment) {
        this.mContext = mContext;
        this.messageList = messageList;
        this.friendName = friendName;
        this.friendNum = friendNum;
        this.whichFromFragment = whichFromFragment;

        storage = FirebaseStorage.getInstance();
        stRef = storage.getReference();

    }


    public class CardHolder extends RecyclerView.ViewHolder{

        CardView cardView_cardview_sentImageList;
        ImageView imageView_cardview_sentImageList_image;


        public CardHolder(@NonNull View itemView) {
            super(itemView);

            cardView_cardview_sentImageList = itemView.findViewById(R.id.cardView_cardview_sentImageList);
            imageView_cardview_sentImageList_image = itemView.findViewById(R.id.imageView_cardview_sentImageList_image);

        }
    }


    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_sent_image_list_design, parent, false);

        return new CardHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {

        Message message = messageList.get(position);

        setImageToAttribute(holder, message);

        holder.cardView_cardview_sentImageList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SentImageShowFragment sentImageShowFragment = new SentImageShowFragment();
                Bundle bundle = new Bundle();
                bundle.putString("friendNum", friendNum);
                bundle.putString("friendName", friendName);
                bundle.putString("messageType", message.getMessageType());
                bundle.putString("messageText", message.getMessageText());
                bundle.putString("whoSent", message.getMessageFrom());
                bundle.putInt("whichFromFragment", whichFromFragment);
                sentImageShowFragment.setArguments(bundle);
                ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity, sentImageShowFragment).commit();


            }
        });



    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    /**
     * resimleri gerekli gorsel nesnelere yerlestirir .
     */
    public void setImageToAttribute(CardHolder holder, Message message){

        String ppKey = message.getMessageText();
        String messageType = message.getMessageType();


        if (messageType.equals("image")){ // resim ile birlikte text message gonderildi  ise ...

            String[] arr = ppKey.trim().split(",");

            ppKey = arr[0]; // resim keyi 0. indiste .

        }

        Log.e("key::", ppKey);

        stRef.child("UsersMessagesPictures").child(ppKey).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri).into(holder.imageView_cardview_sentImageList_image);

            }
        });



    }





}
