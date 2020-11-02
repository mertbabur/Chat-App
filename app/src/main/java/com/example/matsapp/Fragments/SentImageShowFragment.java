package com.example.matsapp.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.matsapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class SentImageShowFragment extends Fragment {

    private ImageView imageView_fragmentSentImageShow_backClick, imageView_fragmentSentImage_imageField;
    private TextView textView_fragmentSentImage_messageField, textView_fragmentSentImageShow_whoSent;

    private String USER_PHONE_NUMBER;
    private String FRIEND_PHONE_NUMBER;
    private String FRIEND_NAME;
    private int WHICH_FROM_FRAGMENT;

    private FirebaseStorage storage;
    private StorageReference stRef;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sent_image_show_design, container, false);

        defineAttributes(rootView);
        actionAttributes();
        setImageToAttribute();

        return rootView;

    }


    public void defineAttributes(View rootView){

        imageView_fragmentSentImageShow_backClick = rootView.findViewById(R.id.imageView_fragmentSentImageShow_backClick);
        imageView_fragmentSentImage_imageField = rootView.findViewById(R.id.imageView_fragmentSentImage_imageField);
        textView_fragmentSentImage_messageField = rootView.findViewById(R.id.textView_fragmentSentImage_messageField);
        textView_fragmentSentImageShow_whoSent = rootView.findViewById(R.id.textView_fragmentSentImageShow_whoSent);

        USER_PHONE_NUMBER = getActivity().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE).getString("phoneNum", "bos user num");
        FRIEND_PHONE_NUMBER = getArguments().getString("friendNum", "bos friend num");
        FRIEND_NAME = getArguments().getString("friendName", "bos friend name");
        WHICH_FROM_FRAGMENT = getArguments().getInt("whichFromFragment",0);

        storage = FirebaseStorage.getInstance();
        stRef = storage.getReference();

    }


    public void actionAttributes(){

        imageView_fragmentSentImageShow_backClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedImageFragment sharedImageFragment = new SharedImageFragment();
                Bundle bundle = new Bundle();
                bundle.putString("friendNum", FRIEND_PHONE_NUMBER);
                bundle.putString("friendName", FRIEND_NAME);
                bundle.putInt("whichFromFragment",WHICH_FROM_FRAGMENT);
                sharedImageFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity, sharedImageFragment).commit();
                

            }
        });

    }


    /**
     * mesaj resmini gerekli gorsel nesneye yerlestirir.
     * eger text mesaj ile gonderilmisse text i de yerlestirir.
     */
    public void setImageToAttribute(){

        String messageType = getArguments().getString("messageType","bos type");
        String imageKey = getArguments().getString("messageText", "bos text");
        String messageFrom = getArguments().getString("whoSent", "bos who");

        if (messageFrom.equals(USER_PHONE_NUMBER)){ // eger app i kullanan kisi gonderdi ise ...

            textView_fragmentSentImageShow_whoSent.setText("Siz");

        }
        else { // eger arkadasi gonderdi ise ...

            textView_fragmentSentImageShow_whoSent.setText(FRIEND_NAME);

        }


        if (messageType.equals("imageText")){ // image ile birlikte text message atildi ise ...

            String[] arr = imageKey.trim().split(",");
            imageKey = arr[0]; // image keyi 0. indiste ...

            String message = arr[1]; // 1. indiste text message i var ...
            textView_fragmentSentImage_messageField.setText(message);

        }
        else { // eger sadece image atildi ise ...

            textView_fragmentSentImage_messageField.setVisibility(View.INVISIBLE);

        }


        stRef.child("UsersMessagesPictures").child(imageKey).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri).into(imageView_fragmentSentImage_imageField);

            }
        });


    }





}
