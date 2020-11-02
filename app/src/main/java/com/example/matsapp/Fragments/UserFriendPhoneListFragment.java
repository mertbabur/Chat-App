package com.example.matsapp.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matsapp.Adapters.UserFriendPhoneListAdapter;
import com.example.matsapp.Models.UserFriend;
import com.example.matsapp.R;
import com.example.matsapp.Utils.FriendNumberControl;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserFriendPhoneListFragment extends Fragment implements SearchView.OnQueryTextListener {

    private TextView textView_newContact_click, textView_UserFriendPhoneList_friendCount;
    private RecyclerView recyclerView_userFriendPhoneListFragment;
    private ImageView imageView_userFriendPhoneList_backClick;

    private List<UserFriend> userFriendPhoneList;
    private UserFriendPhoneListAdapter adapter;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private String USER_PHONE_NUMBER;
    private long USER_FRIEND_COUNT;

    private static final int USER_FRIEND_PHONE_LIST_FRAGMENT = 0;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_userfriendphonelist_design,container,false);

        defineAttributies(rootView);

        //userFriendPhoneList.clear();

        actionAttributies();

        createAndModifyToolbar();

        defineRecylerView();



        return rootView;

    }

    public void defineAttributies(View rootView){

        textView_newContact_click = rootView.findViewById(R.id.textView_newContact_click);
        recyclerView_userFriendPhoneListFragment = rootView.findViewById(R.id.recyclerView_userFriendPhoneListFragment);
        textView_UserFriendPhoneList_friendCount = rootView.findViewById(R.id.textView_UserFriendPhoneList_friendCount);
        imageView_userFriendPhoneList_backClick = rootView.findViewById(R.id.imageView_userFriendPhoneList_backClick);

        userFriendPhoneList = new ArrayList<>();

        USER_PHONE_NUMBER = getActivity().getSharedPreferences("userInfo",getActivity().MODE_PRIVATE).getString("phoneNum","1").toString();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();


    }


    public void actionAttributies(){

        textView_newContact_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddNewContactFragment addNewContactFragment = new AddNewContactFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity,addNewContactFragment).commit();


            }
        });

        imageView_userFriendPhoneList_backClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainUserPageFragment mainUserPageFragment = new MainUserPageFragment();
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder_userLoggedInActivity,mainUserPageFragment).commit();

            }
        });

    }


    /**
     * Kullanicinin firebase db deki bulunun arkadaslarinin numaralarini getirildikten sonra userFriendPhoneList e atilir .
     * Daha sonra burada recylerView tanimlanir .
     */
    public void defineRecylerView(){

        myRef.child("UserFriends").child(USER_PHONE_NUMBER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    UserFriend userFriend = postSnapshot.getValue(UserFriend.class);

                    userFriendPhoneList.add(userFriend);


                }

                recyclerView_userFriendPhoneListFragment.setHasFixedSize(true);
                recyclerView_userFriendPhoneListFragment.setLayoutManager(new LinearLayoutManager(getActivity()));



                adapter = new UserFriendPhoneListAdapter(getActivity(),userFriendPhoneList, USER_FRIEND_PHONE_LIST_FRAGMENT);
                recyclerView_userFriendPhoneListFragment.setAdapter(adapter);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    /**
     * Toolbari olusturup duzenleme isi burada yapilir .
     * Firebase baglanilip arkadas sayisi cekilip toolbarin alt basligina yazar .
     */
    public void createAndModifyToolbar(){ // toolbar benzeri cardView ile yapildi .

        myRef.child("UserFriends").child(USER_PHONE_NUMBER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                textView_UserFriendPhoneList_friendCount.setText(dataSnapshot.getChildrenCount()+" Kişi");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);


        inflater.inflate(R.menu.user_friend_phone_list_design,menu);

        MenuItem item = menu.findItem(R.id.action_search_userFriendPhoneList);

        SearchView searchView = (SearchView) item.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.e("kelime : ", query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Log.e("harf harf : ", newText);

                return false;
            }
        });




    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        /*if (item.getItemId() == R.id.action_refresh_userFriendPhoneList){

            FriendNumberControl.useFriendNumberControlClass(getActivity(), USER_PHONE_NUMBER);

            Log.e("aciton item","calisti");

        }
*/



        return super.onOptionsItemSelected(item);
    }





    @Override
    public boolean onQueryTextSubmit(String query) {



        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {



        return false;
    }
}
