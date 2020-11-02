package com.example.matsapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.matsapp.R;
import com.example.matsapp.Utils.FriendNumberControl;

public class AddNewContactFragment extends Fragment {

    TextView textView_addNewContactFragment_quit_click, textView_addNewContactFragment_save_click;
    EditText editTextText_addNewContactFragment_userName, editTextText_addNewContactFragment_userSurname, editTextText_addNewContactFragment_userPhoneNumber;

    private String USER_PHONE_NUMBER;

    private String contactName;
    private String contactNum;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.add_new_contact_fragment,container,false);

        defineAttributies(rootView);

        isMessageContact();

        actionAttributies();

        visibleSaveClickButton();



        return rootView;
    }


    public void defineAttributies(View rootView){

        textView_addNewContactFragment_quit_click = rootView.findViewById(R.id.textView_addNewContactFragment_quit_click);
        textView_addNewContactFragment_save_click = rootView.findViewById(R.id.textView_addNewContactFragment_save_click);
        editTextText_addNewContactFragment_userName = rootView.findViewById(R.id.editTextText_addNewContactFragment_userName);
        editTextText_addNewContactFragment_userSurname = rootView.findViewById(R.id.editTextText_addNewContactFragment_userSurname);
        editTextText_addNewContactFragment_userPhoneNumber = rootView.findViewById(R.id.editTextText_addNewContactFragment_userPhoneNumber);

        USER_PHONE_NUMBER = getActivity().getSharedPreferences("userInfo",getActivity().MODE_PRIVATE).getString("phoneNum","null");

        try {
            contactName = getArguments().getString("contactName", "bos");
            contactNum = getArguments().getString("contactNum", "bos");
        }
        catch (Exception e){

        }

    }


    public void actionAttributies(){

        textView_addNewContactFragment_quit_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainUserPageFragment mainUserPageFragment = new MainUserPageFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity, mainUserPageFragment).commit();


            }
        });

        textView_addNewContactFragment_save_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = editTextText_addNewContactFragment_userName.getText().toString();
                String userSurname = editTextText_addNewContactFragment_userSurname.getText().toString();
                String userPhoneNumber = editTextText_addNewContactFragment_userPhoneNumber.getText().toString();

                if (!userName.equals("") && !userSurname.equals("") && !userPhoneNumber.equals("")){

                    if (controlNumber(userPhoneNumber)) {

                        saveContactFriend();

                        textView_addNewContactFragment_save_click.setVisibility(View.VISIBLE);


                        MainUserPageFragment mainUserPageFragment = new MainUserPageFragment();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity, mainUserPageFragment).commit();


                    }
                    else{
                        Toast.makeText(getActivity(), "Numara başına +90 yazılmalı ve 13 haneli olmalı .", Toast.LENGTH_SHORT).show();
                    }


                }
                else{

                    Toast.makeText(getActivity(), "Lütfen bilgileri tam giriniz .", Toast.LENGTH_LONG).show();

                }

            }
        });


    }



    /**
     * kaydeet butonunu aktif hale getirir .
     */
    public void visibleSaveClickButton(){


        editTextText_addNewContactFragment_userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                textView_addNewContactFragment_save_click.setVisibility(View.VISIBLE);



            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        editTextText_addNewContactFragment_userSurname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                textView_addNewContactFragment_save_click.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        editTextText_addNewContactFragment_userPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                textView_addNewContactFragment_save_click.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }


    public void saveContactFriend(){

        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        String name = editTextText_addNewContactFragment_userName.getText().toString();
        String surName = editTextText_addNewContactFragment_userSurname.getText().toString();
        String phoneNumber = editTextText_addNewContactFragment_userPhoneNumber.getText().toString();

        intent
                .putExtra(ContactsContract.Intents.Insert.NAME, name + " " + surName)
                .putExtra(ContactsContract.Intents.Insert.PHONE,phoneNumber)
                .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
        ;

        startActivity(intent);

    }


    /**
     * Numara dogru formatta yazildi mi ? Bunu kontrol eder .
     */
    public boolean controlNumber(String phoneNumber){

        if (phoneNumber.contains("+90") && phoneNumber.trim().length() == 13)
            return true;

        return false;

    }


    /**
     * paylasilan contact in messageTextten gelindi ona gore gorsel nesnelere yerlestirir .
     */
    public void isMessageContact(){



        try {

            editTextText_addNewContactFragment_userName.setText(contactName);
            editTextText_addNewContactFragment_userPhoneNumber.setText(contactNum);

            if (!contactNum.equals("null") && !contactName.equals("null")) {
                textView_addNewContactFragment_save_click.setVisibility(View.VISIBLE);
            }

        }
        catch (Exception e){

        }


    }



}
