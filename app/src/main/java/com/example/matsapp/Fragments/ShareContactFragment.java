package com.example.matsapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matsapp.Adapters.ChatFragmentAdapter;
import com.example.matsapp.Adapters.ShareContactsAdapter;
import com.example.matsapp.Models.UserContacs;
import com.example.matsapp.R;
import com.example.matsapp.Utils.FriendNumberControl;

import java.util.ArrayList;
import java.util.List;

public class ShareContactFragment extends Fragment {

    private RecyclerView recyclerView_fragmentShareContact;
    private ImageView imageView_fragmentShareContact_backClick;
    private TextView textView_fragmetnShareContact_contactNumber;

    private List<UserContacs> userContacsList;

    private ShareContactsAdapter adapter;

    private String USER_PHONE_NUMBER;
    private String FRIEND_PHONE_NUMBER;
    private String FRIEND_NAME;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_share_contact_design, container, false);

        defineAttributies(rootView);
        defineRecyclerView();
        setContactNumber();
        actionAttributes();

        return rootView;
    }


    public void defineAttributies(View rootView){

        recyclerView_fragmentShareContact = rootView.findViewById(R.id.recyclerView_fragmentShareContact);
        imageView_fragmentShareContact_backClick = rootView.findViewById(R.id.imageView_fragmentShareContact_backClick);
        textView_fragmetnShareContact_contactNumber = rootView.findViewById(R.id.textView_fragmetnShareContact_contactNumber);

        userContacsList = new ArrayList<>();

        USER_PHONE_NUMBER = getActivity().getSharedPreferences("userInfo",getActivity().MODE_PRIVATE).getString("phoneNum","bos num");

        FRIEND_PHONE_NUMBER = getArguments().getString("friendNum", "bos friend num");
        FRIEND_NAME = getArguments().getString("friendName", "bos friend name");

    }


    /**
     * recyceler view u tanimlar .
     */
    public void defineRecyclerView(){

        userContacsList = FriendNumberControl.getPhoneNumbersFromGuideBook(getActivity());

        recyclerView_fragmentShareContact.setHasFixedSize(true);
        recyclerView_fragmentShareContact.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new ShareContactsAdapter(getActivity(),userContacsList, USER_PHONE_NUMBER, FRIEND_PHONE_NUMBER, FRIEND_NAME);
        recyclerView_fragmentShareContact.setAdapter(adapter);


    }


    public void actionAttributes(){

        imageView_fragmentShareContact_backClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String friendNum = getArguments().getString("friendNum", "bos num");
                String friendName = getArguments().getString("friendName", "bos name");

                ChatFragment chatFragment = new ChatFragment();
                Bundle bundle = new Bundle();
                bundle.putString("friendNum", friendNum);
                bundle.putString("friendName", friendName);
                chatFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity, chatFragment).commit();

            }
        });


    }



    /**
     * Kullanicinin rehberinde kaç kisi kayitli ?
     * Bunu gerekli gorsel nesneye yerlestirir .
     */
    public void setContactNumber(){

        textView_fragmetnShareContact_contactNumber.setText(String.valueOf(userContacsList.size()) + " kişi");

    }





}
