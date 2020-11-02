package com.example.matsapp.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.matsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInFragment extends Fragment {

    private EditText editText_phone_number_field;
    private Button button_ileri;

    private String phoneNumber;

    private int TOOLBAR_ID;
    private String TOOLBAR_MESSAGE;

    private FirebaseDatabase database;
    private DatabaseReference myRef;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login_design,container,false);

        defineAttributies(rootView);
        actionAttributies();


        return rootView;
    }

    /**
     * Görsel nesneler burada tanimlanir .
     * Sayfa ilk acildiginda ne olacaksa burada yaptirilir .
     */
    public void defineAttributies(View rootView){

        editText_phone_number_field = rootView.findViewById(R.id.editText_phone_number_field);
        button_ileri = rootView.findViewById(R.id.button_ileri);


        // Bu fragment toolbarId keyi ile yakalar .
        TOOLBAR_ID = getArguments().getInt("toolbarId"); // toolbar id yi yakalıyoruz.

        // Bu fragment toolbarMessage keyi ile yakalar .
        TOOLBAR_MESSAGE = getArguments().getString("toolbarMessage");
        toolbarSetUpMessage(TOOLBAR_ID,TOOLBAR_MESSAGE);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();




    }


    /**
     * Action verecek buton vs burada tetiklenir .
     */
    public void actionAttributies(){

        // kullanici sistemde banli degil ise bir sonraki isleve gecilecek .
        button_ileri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phoneNumber = editText_phone_number_field.getText().toString();

                if(phoneNumber.isEmpty() && phoneNumber.length() < 10){
                    Toast.makeText(getActivity(), "Lütfen telefon numarasını eksiksiz giriniz .", Toast.LENGTH_SHORT).show();
                }
                else{

                    // eger deger null ise kullanici banli listesinde bulunmaz .
                    myRef.child("BannedUsers").child("+90"+phoneNumber).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            try {

                                String temp = snapshot.getValue().toString();

                                Toast.makeText(getActivity(), "Girdiğiniz numara sistemimizde banlı. Bu namara ile maalesef giriş yapamazsınız .", Toast.LENGTH_LONG).show();

                            }
                            catch (Exception e){

                                Log.e("exception numara giris", e.getMessage());

                                alertViewBuilder();

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }


            }
        });

    }

    /**
     * LogInActivityden gelen toolbar basligini yerlestirir .
     * @param toolbarId
     * @param toolbarMessage
     */
    public void toolbarSetUpMessage(int toolbarId, String toolbarMessage){

        Toolbar toolbar = getActivity().findViewById(toolbarId);
        toolbar.setTitle(toolbarMessage);

    }


    /**
     * Burada alertView oluşturuluyor .
     */
    public void alertViewBuilder(){


        AlertDialog.Builder alertDialogbuilder = new AlertDialog.Builder(getActivity());

        alertDialogbuilder.setTitle("Aşağıdaki numaraya doğrulama kodu yollanacak :");
        alertDialogbuilder.setMessage("+90 " + phoneNumber + "\n\n" + "numara doğru ise tamama basınız değil ise yeniden deneyebilirsiniz." );

       alertDialogbuilder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {

               CodeVerificationFragment codeVerificationFragment = new CodeVerificationFragment();
               Bundle b = new Bundle();
               b.putString("phoneNumber","+90"+phoneNumber);
               b.putInt("tbarId",TOOLBAR_ID);
               codeVerificationFragment.setArguments(b);
               getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,codeVerificationFragment).commit();

           }
       });

       alertDialogbuilder.setNegativeButton("YENİDEN DENE", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {

               LogInFragment logInFragment = new LogInFragment();
               Bundle b = new Bundle();
               b.putInt("toolbarId",TOOLBAR_ID);
               b.putString("toolbarMessage","Lütfen telefon numaranızı giriniz");
               logInFragment.setArguments(b);
               getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,logInFragment).commit();


           }
       });


       alertDialogbuilder.create().show();

    }



}
