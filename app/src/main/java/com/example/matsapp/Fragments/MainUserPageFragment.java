package com.example.matsapp.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.matsapp.Activities.MainActivity;
import com.example.matsapp.Activities.UserLoggedInActivity;
import com.example.matsapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainUserPageFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    private List<Fragment> fragmentList;
    private List<String> fragmentTitleNameList;/** fragmentTitleName --> tabLayout daki isimleri tutar .*/


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main_user_page,container,false);

        defineAttributies(rootView);
        createTabLayout();


        return rootView;

    }

    /**
     * ViewPager icin adapter inner class i .
     */
    private class MyViewPagerAdapter extends FragmentStateAdapter {//2


        public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }
    }


    public void defineAttributies(View rootView){

        tabLayout = rootView.findViewById(R.id.tabLayout);
        viewPager2 = rootView.findViewById(R.id.viewPager2);

        fragmentList = new ArrayList<>();

        fragmentTitleNameList = new ArrayList<>();


    }


    /**
     * tablayout olusturur .
     */
    public void createTabLayout(){

        addFragmentToArrayList();
        adapterTransferToViewPager();
        addTitleNameForTabLayout();

        new TabLayoutMediator(tabLayout,viewPager2,   //5
                (tab,position)->tab.setText(fragmentTitleNameList.get(position))).attach();

    }


    /**
     * Fragmentlari viewpager da gostermek icin; fragmentlari arrayliste atacağız .
     */
    public void addFragmentToArrayList(){//1

        fragmentList.add(new ChatListFragment());
        fragmentList.add(new StatusFragment());
        fragmentList.add(new UserProfileFragment());

    }


    /**
     * MyViewPager inner class indan bir adapter olusturup ViewPager atiyoruz .
     */
    public void adapterTransferToViewPager(){//3

        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getActivity());

        viewPager2.setAdapter(adapter);

    }

    /**
     * TabLayout icin list e title ekler .
     */
    public void addTitleNameForTabLayout(){//4

        fragmentTitleNameList.add("SOHBETLER");
        fragmentTitleNameList.add("DURUM");
        fragmentTitleNameList.add("PROFİL");

    }




}
