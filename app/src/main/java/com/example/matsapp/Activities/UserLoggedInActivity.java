package com.example.matsapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;

import com.example.matsapp.Fragments.MainUserPageFragment;
import com.example.matsapp.R;
import com.example.matsapp.Utils.TimeClass;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class UserLoggedInActivity extends AppCompatActivity {


    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    private List<Fragment> fragmentList;
    private List<String> fragmentTitleNameList;/** fragmentTitleName --> tabLayout daki isimleri tutar .*/

    private String USER_PHONE_NUMBER;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_logged_in_activity);

        defineAttributies();
        goToMainUserPage();


    }



    public void defineAttributies(){

        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager2);

        fragmentList = new ArrayList<>();

        fragmentTitleNameList = new ArrayList<>();

        USER_PHONE_NUMBER = getSharedPreferences("userInfo",MODE_PRIVATE).getString("phoneNum","1");




    }


    /**
     * Giriş yapildiktan sonra anasayfaya yonlendirir .
     */
    public void goToMainUserPage(){

        MainUserPageFragment mainUserPageFragment = new MainUserPageFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder_userLoggedInActivity,mainUserPageFragment).commit();



    }


    @Override
    protected void onStop() {
        super.onStop();

        TimeClass.updateUserStatus(USER_PHONE_NUMBER,"Çevrimdışı");

        Log.e("onStop","cikti");


    }


    @Override
    protected void onStart() {
        super.onStart();

        TimeClass.updateUserStatus(USER_PHONE_NUMBER,"Çevrimiçi");
        Log.e("onStart","girdi");


    }
}
