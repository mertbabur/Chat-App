package com.example.matsapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.matsapp.Models.User;
import com.example.matsapp.R;

import com.example.matsapp.Utils.TimeClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CodeVerificationFragment extends Fragment {

    private TextView textView_wrong_number, textView_resend_sms, textView_time, textView_app_message, textView_wrong_verify_code;
    private EditText editText_verify_code_field;
    private Button button_continue;

    private ProgressBar progressBar;


    private FirebaseAuth mAuth;

    private String USER_PHONE_NUMBER;

    private String codeSent;

    private Toolbar toolbar;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private int TOOLBAR_ID;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_codeverification_design, container, false);

        defineAttributies(rootView);
        toolbarSetTitleAgain();



        sendVerificationCode();

        actionAttributies();


        return rootView;
    }

    public void defineAttributies(View rootView) {

        textView_wrong_number = rootView.findViewById(R.id.textView_wrong_number);
        textView_resend_sms = rootView.findViewById(R.id.textView_resend_sms);
        editText_verify_code_field = rootView.findViewById(R.id.editText_verify_code_field);
        button_continue = rootView.findViewById(R.id.button_continue);
        textView_time = rootView.findViewById(R.id.textView_time);
        progressBar = rootView.findViewById(R.id.progressBar);
        textView_app_message = rootView.findViewById(R.id.textView_app_message);
        textView_wrong_verify_code = rootView.findViewById(R.id.textView_wrong_verify_code);

        //Bu fragment tbarId keyi ile yakalar .
        TOOLBAR_ID = getArguments().getInt("tbarId");

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        USER_PHONE_NUMBER = getArguments().getString("phoneNumber");

        textView_app_message.setText("Sms " + USER_PHONE_NUMBER + " numarasına gönderiliyor, lütfen bekleyiniz .");


    }

    public void actionAttributies() {

        textView_wrong_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LogInFragment logInFragment = new LogInFragment();
                Bundle b = new Bundle();
                b.putInt("toolbarId",TOOLBAR_ID);
                b.putString("toolbarMessage","Lütfen telefon numaranızı giriniz");
                logInFragment.setArguments(b);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, logInFragment).commit();

            }
        });

        textView_resend_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendVerificationCode();
                Toast.makeText(getActivity(), "Doğrulama kodunuz tekrardan gönderildi .", Toast.LENGTH_LONG).show();


            }
        });

        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editText_verify_code_field.getText().toString().length() == 6) {

                    verifySignInCode();



                } else {
                    Toast.makeText(getActivity(), "Doğrulama kodu 6 haneli olmalı .", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    /**
     * toolbar basligini guncellemek icin .
     */
    public void toolbarSetTitleAgain() {

        int toolbarId = getArguments().getInt("tbarId");
        toolbar = getActivity().findViewById(toolbarId);
        toolbar.setTitle(USER_PHONE_NUMBER + " doğrula .");

    }


    /**
     * Dogrulama kodu gonderme kismi .
     */
    public void sendVerificationCode() {

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {


                Log.e("hata1", "Verification Complete");

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                // Hata anında çalışan yer
                Log.e("hata", "Verification Failed: " + e.getMessage());

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                // Kod gönderildiği zaman çalışan yer

                Log.e("hata", "Code Sent");
                codeSent = verificationId;


            }
        };


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                USER_PHONE_NUMBER,
                2,
                TimeUnit.SECONDS,
                getActivity(),
                mCallbacks);

        doVisibleProgressbarAndTextView();

    }


    /**
     * Kod dogrulama kismi .
     * eger kod basarili bir sekilde dogrulandi ise isUserDB fonksiyonu cagrilir .
     * telefon hafizasina kullanici numarasini kaydeder .
     * Ardindan userEntry Fragment ina gecis yapilir .
     */
    public void verifySignInCode() {

        String fromUserCode = editText_verify_code_field.getText().toString();

        final PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, fromUserCode);

        // Firebase mAuth nesnemizle kontrol işlemini yapıyoruz
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Eğer task başarılı dönerse kod doğrulanmış demektir
                        if (task.isSuccessful()) {

                            Log.e("hata", "Verification Success");

                            isUserDB(USER_PHONE_NUMBER);// user db de var ise kaydetme yok ise db ye kaydet .

                            saveUserPhoneNumToStorage();

                            UserEntryFragment userEntryFragment = new UserEntryFragment();
                            Bundle b = new Bundle();
                            b.putInt("tobarId", TOOLBAR_ID);
                            b.putString("userPhone",USER_PHONE_NUMBER);
                            userEntryFragment.setArguments(b);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, userEntryFragment).commit();

                        }
                        else {
                            textView_wrong_verify_code.setVisibility(View.VISIBLE);
                        }


                    }
                });


    }


    /**
     * Giris yapan kullanicinin telefon numarasini telefon hafizasina kaydeder .
     */
    public void saveUserPhoneNumToStorage(){

        SharedPreferences mSharedPrefs = getActivity().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPrefs.edit();

        editor.putString("phoneNum",USER_PHONE_NUMBER);

        editor.commit();

    }


    /**
     * Dogrulama kodu geldikten sonra bi iki attribute visible olacak .
     * Ve ardindan sayac calisacak . (60 saniye icinde islem sona erecek .)
     */
    public void doVisibleProgressbarAndTextView() {

        progressBar.setVisibility(View.VISIBLE);
        textView_time.setVisibility(View.VISIBLE);

        CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textView_time.setText(String.valueOf(millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {

                progressBar.setVisibility(View.INVISIBLE);
                textView_time.setVisibility(View.INVISIBLE);

            }
        }.start();

    }


    /**
     * Kullanici ilk giris yaptifinda db ye kayit eder .
     * @param number -- > kullanicinin giris yaptigi tel no
     */
    public void addUsertoDB (String number){

        String userAboutDate = TimeClass.getDate();

        User user = new User("null",mAuth.getUid(),"null","null","Hey there, i am using matsapp !", number, userAboutDate, "2131165315");

        myRef.child("Users").child(number).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Log.e("addUsertoDB","db kaydedildi.");
                }
                else {
                    Log.e("addUsertoDB",task.getException()+"");

                }

            }
        });


    }


    /**
     * kullanici daha onceden db ye kayitli mi .
     * kullanici kayitli ise bir sey yapmaz .
     * kullanici db ye kayitli degil ise addUserToDB metodu cagrilir .
     * @param number -- > kullanici telefon nosunu tutar .
     */
    public void isUserDB(final String number){

        myRef.child("Users").child(USER_PHONE_NUMBER).child("userPhone").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {

                    String userNumber = dataSnapshot.getValue().toString();

                }catch (Exception e){
                    Log.e("hata exception : ", e.getMessage());
                    addUsertoDB(number);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                //isUserDBControl = false;
                Log.e("isUserDB", databaseError.getMessage());


            }
        });



    }





}
