package com.example.matsapp.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matsapp.Adapters.UserAboutAdapter;
import com.example.matsapp.R;
import com.example.matsapp.Utils.TimeClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserAboutFragment extends Fragment {

    private CardView cardView_fragmentUserAbout;
    private EditText editText_fragmentUserAbout_about;
    private RecyclerView recyclerView_fragmentUserAbout;
    private ImageView imageView_fragmentUserAbout_back_click, imageView_fragmentUserAbout_save_click;

    private List<String> userAboutList;

    private UserAboutAdapter adapter;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private String USERS_PHONE_NUMBER;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_user_about_design,container,false);

        defineAttributies(rootView);
        defineRecyclerView();
        getUserAboutDB();
        actionAttributies();

        return rootView;
    }


    public void defineAttributies(View rootView){

        cardView_fragmentUserAbout = rootView.findViewById(R.id.cardView_fragmentUserAbout);
        editText_fragmentUserAbout_about = rootView.findViewById(R.id.editText_fragmentUserAbout_about);
        recyclerView_fragmentUserAbout = rootView.findViewById(R.id.recyclerView_fragmentUserAbout);
        imageView_fragmentUserAbout_back_click = rootView.findViewById(R.id.imageView_fragmentUserAbout_back_click);
        imageView_fragmentUserAbout_save_click = rootView.findViewById(R.id.imageView_fragmentUserAbout_save_click);

        userAboutList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        USERS_PHONE_NUMBER = getActivity().getSharedPreferences("userInfo",getActivity().MODE_PRIVATE).getString("phoneNum","1");


    }

    public void actionAttributies(){


        editText_fragmentUserAbout_about.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                imageView_fragmentUserAbout_save_click.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imageView_fragmentUserAbout_save_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveUserAboutDB();


            }
        });

        imageView_fragmentUserAbout_back_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainUserPageFragment mainUserPageFragment = new MainUserPageFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity,mainUserPageFragment).commit();

            }
        });


    }


    /**
     * Sabit kullanici icin hakkimdalari liste atar .
     * RecyclerView i tanimlar .
     */
    public void defineRecyclerView(){

        userAboutList.add("Müsait");
        userAboutList.add("Meşgul");
        userAboutList.add("Okulda");
        userAboutList.add("Sinemada");
        userAboutList.add("İşte");
        userAboutList.add("Pil bitmek üzere");
        userAboutList.add("Konuşmam, yalnızca MhatsApp");
        userAboutList.add("Toplantıda");
        userAboutList.add("Spor Salonunda");
        userAboutList.add("Uyuyor");
        userAboutList.add("Sadece Acil Çağrılar");

        recyclerView_fragmentUserAbout.setHasFixedSize(true);
        recyclerView_fragmentUserAbout.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new UserAboutAdapter(getActivity(),userAboutList);
        recyclerView_fragmentUserAbout.setAdapter(adapter);


    }


    /**
     * Dbden kullanici hakkindasini alir ve görsel nesneye yerletirir .
     * Bilgiyi getirdikten sonra save butonunu gorunmez yapar . (kayitldan sonra db degistigi icin getUserAtboutDB tekrar tekrar calisiyor (    *** tam emin degilim ??? ***    ))
     */
    public void getUserAboutDB(){

        myRef.child("Users").child(USERS_PHONE_NUMBER).child("userState").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String userAbout = dataSnapshot.getValue().toString();

                editText_fragmentUserAbout_about.setText(userAbout);

                imageView_fragmentUserAbout_save_click.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    /**
     * Yeni yazilan kullanici hakkimdasini DB ye kaydeder .
     */
    public void saveUserAboutDB(){

        String userAbout = editText_fragmentUserAbout_about.getText().toString();

        myRef.child("Users").child(USERS_PHONE_NUMBER).child("userState").setValue(userAbout).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    imageView_fragmentUserAbout_save_click.setVisibility(View.INVISIBLE);

                    String userAboutDate = TimeClass.getDate();

                    myRef.child("Users").child(USERS_PHONE_NUMBER).child("userStateDate").setValue(userAboutDate);

                }

            }
        });


    }

}
