package com.example.matsapp.Adapters;

import android.content.Context;
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
import com.example.matsapp.Models.UserContacs;
import com.example.matsapp.Models.UserFriend;
import com.example.matsapp.R;
import com.example.matsapp.Utils.TimeClass;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShareContactsAdapter extends RecyclerView.Adapter<ShareContactsAdapter.CardHolder>{

    private Context mContext;
    private List<UserContacs> userContacsList;
    private String USER_PHONE_NUMBER;
    private String FRIEND_PHONE_NUMBER;
    private String FRIEND_NAME;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private FirebaseStorage storage;
    private StorageReference stRef;

    public ShareContactsAdapter(Context mContext, List<UserContacs> userContacsList, String USER_PHONE_NUMBER, String FRIEND_PHONE_NUMBER, String FRIEND_NAME) {

        this.mContext = mContext;
        this.userContacsList = userContacsList;
        this.USER_PHONE_NUMBER = USER_PHONE_NUMBER;
        this.FRIEND_PHONE_NUMBER = FRIEND_PHONE_NUMBER;
        this.FRIEND_NAME = FRIEND_NAME;

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        storage = FirebaseStorage.getInstance();
        stRef = storage.getReference();

    }

    public class CardHolder extends RecyclerView.ViewHolder {

        private ImageView shareContactsAdapter_contactPP;
        private TextView shareContactsAdapter_contactName, shareContactsAdapter_contactAbout;
        private CardView shareContactsAdapter_cardClick;


        public CardHolder(@NonNull View itemView) {
            super(itemView);

            shareContactsAdapter_contactPP = itemView.findViewById(R.id.imageView_cardView_shareContactForList_contactPP);
            shareContactsAdapter_contactName = itemView.findViewById(R.id.textView_cardView_shareContactForList_contactName);
            shareContactsAdapter_contactAbout = itemView.findViewById(R.id.textView_cardView_shareContactForList_contactAbout);
            shareContactsAdapter_cardClick = itemView.findViewById(R.id.cardView_shareContactForList_cardClick);


        }


    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_sharecontact_forlist_design, parent, false);



        return new CardHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {

        UserContacs userContacs = userContacsList.get(position);

        setContactInfoToAttributies(holder, userContacs);

        // once mesaji db ye yazar ve sonra chat fragment a gonderir .
        holder.shareContactsAdapter_cardClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChatFragment chatFragment = new ChatFragment();
                Bundle bundle = new Bundle();
                bundle.putString("friendNum", FRIEND_PHONE_NUMBER);
                bundle.putString("friendName", FRIEND_NAME);

                String messageText = userContacs.getContactName() + "," + userContacs.getContactNum();
                sendShareContact(USER_PHONE_NUMBER, FRIEND_PHONE_NUMBER, "contact", TimeClass.getClock(), "false", messageText);

                chatFragment.setArguments(bundle);
                ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity,chatFragment).commit();

            }
        });


    }

    @Override
    public int getItemCount() {
        return userContacsList.size();
    }


    /**
     * contact bilgilerini gorsel nesnelere yerlestirir .
     * @param holder
     * @param contact
     */
    public void setContactInfoToAttributies(CardHolder holder, UserContacs contact){

        if (contact.getContactNum().contains("*")){
            return;
        }

        myRef.child("UserFriends").child(USER_PHONE_NUMBER).child(contact.getContactNum()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {




                try{ // eger arkadas listesinde var ise .

                    UserFriend userFriend = snapshot.getValue(UserFriend.class);

                    setRegisteredUserInApp(holder, userFriend);

                }
                catch (Exception e){ // eger arkadas listesinde yok ise .

                    Log.e("excptn setContactInfo", e.getMessage());

                    holder.shareContactsAdapter_contactName.setText(contact.getContactName());
                    holder.shareContactsAdapter_contactAbout.setText("");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    /**
     * eger contact uygulamaya kayitli ise ona gore gorsel nesnelere yerlestirme yapilir .
     * @param holder
     * @param friend
     */
    public void setRegisteredUserInApp(CardHolder  holder, UserFriend friend){

        myRef.child("Users").child(friend.getFriendPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                String contactPPKey = user.getUserPP();
                Log.e("num",user.getUserPP());

                if (contactPPKey.equals("null")){
                    return;
                }

                stRef.child("UsersPictures").child(contactPPKey).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {


                        Picasso.get().load(uri).into(holder.shareContactsAdapter_contactPP);


                    }
                });



                holder.shareContactsAdapter_contactName.setText(friend.getFriendName());

                holder.shareContactsAdapter_contactAbout.setText(user.getUserState());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    public void sendShareContact(String userNumber, String friendNumber, String messageType, String messageDate, String messageSeen,String messageText){

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



}
