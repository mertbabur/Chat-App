package com.example.matsapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.matsapp.Fragments.LogInFragment;
import com.example.matsapp.R;


public class LogInActivity extends AppCompatActivity {

    private Toolbar toolbar_logIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        defineAttributes();
        defineToolbar();



    }

    /**
     * Görsel nesneler burada tanimlanir .
     * Sayfa ilk acildiginda ne olacaksa burada yaptirilir .
     */
    public void defineAttributes(){

        toolbar_logIn = findViewById(R.id.toolbar_logIn);

        LogInFragment logInFragment = new LogInFragment();
        Bundle b = new Bundle();
        b.putInt("toolbarId",R.id.toolbar_logIn);
        b.putString("toolbarMessage","Lütfen telefon numaranızı giriniz");
        logInFragment.setArguments(b);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder,logInFragment).commit();



    }

    /**
     * Toolbar tanimlama .
     */
    public void defineToolbar(){

        toolbar_logIn.setTitle("Lütfen telefon numaranızı giriniz");
        toolbar_logIn.setTitleTextColor(getResources().getColor(R.color.colorWappGreen));
        toolbar_logIn.setBackgroundColor(getResources().getColor(R.color.colorWhiteChocolate));
        setSupportActionBar(toolbar_logIn);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // menu tasarimi toolbara bagladik .

        getMenuInflater().inflate(R.menu.login_menu_design,menu);
        return true;

    }




}
