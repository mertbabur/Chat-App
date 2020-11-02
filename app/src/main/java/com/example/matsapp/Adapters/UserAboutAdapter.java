package com.example.matsapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matsapp.R;
import com.example.matsapp.Utils.TimeClass;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UserAboutAdapter extends RecyclerView.Adapter<UserAboutAdapter.CardHolder>{

    private Context mContext;
    private List<String> userAboutList;

    private FirebaseDatabase database;
    private DatabaseReference myRef;



    public UserAboutAdapter(Context mContext, List<String> userAboutList) {
        this.mContext = mContext;
        this.userAboutList = userAboutList;
    }


    public class CardHolder extends RecyclerView.ViewHolder{

        private TextView textView_cardView_userAbout_adapter;
        private CardView cardView_userAbout;


        public CardHolder(@NonNull View itemView) {
            super(itemView);

            textView_cardView_userAbout_adapter = itemView.findViewById(R.id.textView_cardView_userAbout_adapter);
            cardView_userAbout = itemView.findViewById(R.id.cardView_userAbout);


        }
    }


    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_user_about_design,parent,false);

        return new CardHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {

        String userAbout = userAboutList.get(position);

        holder.textView_cardView_userAbout_adapter.setText(userAbout);

        holder.cardView_userAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userAboutDate = TimeClass.getDate();

                saveUserAboutDB(userAbout, userAboutDate);

                TextView text = ((AppCompatActivity)mContext).findViewById(R.id.editText_fragmentUserAbout_about); // hakkimda kismini  guncellendi. userAboutFragmenttaki gorsel nesneye ulastik .
                text.setText(userAbout);
                

            }
        });


    }

    @Override
    public int getItemCount() {
        return userAboutList.size();
    }


    public void saveUserAboutDB(String userAbout, String userAboutDate){

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        String userPhone = mContext.getSharedPreferences("userInfo",mContext.MODE_PRIVATE).getString("phoneNum","1");

        myRef.child("Users").child(userPhone).child("userState").setValue(userAbout);
        myRef.child("Users").child(userPhone).child("userAboutDate").setValue(userAboutDate);


    }


}
