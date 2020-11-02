package com.example.matsapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.matsapp.Adapters.WalpaperAdapterForFriend;
import com.example.matsapp.Adapters.WalpaperAdapterForGeneral;
import com.example.matsapp.R;

import java.util.ArrayList;
import java.util.List;

public class WalpaperFragment extends Fragment {

    private ImageView imageView_fragmentWalpaper_back;
    private TextView textView_fragmentWalpaper_selectFromGallery;
    private RecyclerView recyclerView_fragmentWalpaper;

    private List<Integer> walpaperList;

    private WalpaperAdapterForFriend adapterForFriend;
    private WalpaperAdapterForGeneral adapterForGeneral;

    private String FRIEND_PHONE_NUMBER;
    private String FRIEND_NAME;

    private String WHICH_FRAGMENT;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_walpaper_design, container,false);

        defineAttributies(rootView);
        addWalpaperToList();
        defineRecylerViewForFriend();
        actionAttributies();

        return rootView;

    }


    public void defineAttributies(View rootView){

        imageView_fragmentWalpaper_back = rootView.findViewById(R.id.imageView_fragmentWalpaper_back);
        textView_fragmentWalpaper_selectFromGallery = rootView.findViewById(R.id.textView_fragmentWalpaper_selectFromGallery);
        recyclerView_fragmentWalpaper = rootView.findViewById(R.id.recyclerView_fragmentWalpaper);

        walpaperList = new ArrayList<>();

        WHICH_FRAGMENT = getArguments().getString("whichFragment","yok");


        if (WHICH_FRAGMENT.equals("UserProfileFragment")){ // hangi fragmentten geldigini belirler .

        }
        else { // chatFragmenttan geldik .
            FRIEND_PHONE_NUMBER = getArguments().getString("friendNum", "1");
            FRIEND_NAME = getArguments().getString("friendName", "2");
        }

    }


    public void actionAttributies(){

        imageView_fragmentWalpaper_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("tık","tık");

                if (WHICH_FRAGMENT.equals("UserProfileFragment")){

                    MainUserPageFragment mainUserPageFragment = new MainUserPageFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity, mainUserPageFragment).commit();

                }
                else{

                    ChatFragment chatFragment = new ChatFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("friendNum",FRIEND_PHONE_NUMBER);
                    bundle.putString("friendName",FRIEND_NAME);
                    chatFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity, chatFragment).commit();

                }




            }
        });


    }

    /**
     * Listeye drawable da ki walpaperlarin idlerini atar .
     */
    public void addWalpaperToList(){

        walpaperList.add(R.drawable.default_background);
        walpaperList.add(R.drawable.pic1);
        walpaperList.add(R.drawable.pic2);
        walpaperList.add(R.drawable.pic3);
        walpaperList.add(R.drawable.pic4);
        walpaperList.add(R.drawable.pic5);
        walpaperList.add(R.drawable.pic6);
        walpaperList.add(R.drawable.pic7);
        walpaperList.add(R.drawable.pic8);

    }


    /**
     * app deki wallpaperlari recyc e atar .
     * Daha sonra burada recylerView tanimlanir .
     */
    public void defineRecylerViewForFriend(){

                recyclerView_fragmentWalpaper.setHasFixedSize(true);
                recyclerView_fragmentWalpaper.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

                if (WHICH_FRAGMENT.equals("UserProfileFragment")){

                    adapterForGeneral = new WalpaperAdapterForGeneral(getActivity(), walpaperList);
                    recyclerView_fragmentWalpaper.setAdapter(adapterForGeneral);

                }
                else {// chat fragmenttan geldik ise ...

                    adapterForFriend = new WalpaperAdapterForFriend(getActivity(), walpaperList, FRIEND_PHONE_NUMBER, FRIEND_NAME);
                    recyclerView_fragmentWalpaper.setAdapter(adapterForFriend);

                }

    }



}
