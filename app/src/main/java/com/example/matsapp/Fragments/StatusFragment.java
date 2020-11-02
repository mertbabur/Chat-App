package com.example.matsapp.Fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matsapp.Activities.StoriesActivity;
import com.example.matsapp.Adapters.FriendStatusAdapter;
import com.example.matsapp.Models.Story;
import com.example.matsapp.Models.UserFriend;
import com.example.matsapp.R;
import com.example.matsapp.Utils.FirebaseUtils;
import com.example.matsapp.Utils.TimeClass;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatusFragment extends Fragment {

    private CardView cardView_userStatus_click;
    private ImageView imageView_userStatus_userPP, imageView_userStatus_openCamera, imageView_userStatus_openGallery,
                      imageView_userStatus_sentStory, imageView_userStatus_addIcon, imageView_fragmentStatus_refresh;
    private RecyclerView recyclerView_userStatus;
    private TextView textView_userStatus_statusTime, textView_userStatus_addStatusText;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private FirebaseStorage storage;
    private StorageReference stRef;

    private String USER_PHONE_NUMBER;

    private static final int PHOTO_TAKEN_FROM_CAMERA = 1;
    private static final int PHOTO_SELECTED_FROM_GALLERY = 2;

    private Bitmap bitmap_Photo;
    private Uri photo_selected_from_galleryOrTakenCamera_uri;

    private String currentPhotoPath;

    private List<UserFriend> storyFriendList;

    private FriendStatusAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_status_design,container,false);

        defineAttributes(rootView);
        getMyStories();
        actionAttributes();
        getFriendStatus();

        return rootView;
    }


    public void defineAttributes(View rootView){

        cardView_userStatus_click = rootView.findViewById(R.id.cardView_userStatus_click);
        imageView_userStatus_userPP = rootView.findViewById(R.id.imageView_userStatus_userPP);
        imageView_userStatus_openCamera = rootView.findViewById(R.id.imageView_userStatus_openCamera);
        imageView_userStatus_openGallery = rootView.findViewById(R.id.imageView_userStatus_openGallery);
        recyclerView_userStatus = rootView.findViewById(R.id.recyclerView_userStatus);
        textView_userStatus_statusTime = rootView.findViewById(R.id.textView_userStatus_statusTime);
        textView_userStatus_addStatusText = rootView.findViewById(R.id.textView_userStatus_addStatusText);
        imageView_userStatus_sentStory = rootView.findViewById(R.id.imageView_userStatus_sentStory);
        imageView_userStatus_addIcon = rootView.findViewById(R.id.imageView_userStatus_addIcon);
        imageView_fragmentStatus_refresh = rootView.findViewById(R.id.imageView_fragmentStatus_refresh);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        storage = FirebaseStorage.getInstance();
        stRef = storage.getReference();

        USER_PHONE_NUMBER = getActivity().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE).getString("phoneNum","bos num");

        storyFriendList= new ArrayList<>();

    }



    public void actionAttributes(){

        // durum cardView ne tiklandiginde kameradan mi yoksa galeriden mi diye sorar .
        cardView_userStatus_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertView();

            }
        });

        // kamerayi acar .
        imageView_userStatus_openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                askCameraPermissions();

            }
        });

        // galeriyi acar .
        imageView_userStatus_openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PHOTO_SELECTED_FROM_GALLERY);

            }
        });

        //kullanici storylerini goster .
        imageView_userStatus_sentStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), StoriesActivity.class);
                intent.putExtra("phoneNum", USER_PHONE_NUMBER);
                startActivity(intent);


            }
        });


        imageView_fragmentStatus_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainUserPageFragment mainUserPageFragment = new MainUserPageFragment();
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder_userLoggedInActivity,mainUserPageFragment).commit();

            }
        });




    }


    /**
     * kullanicinin profil resmi icin db den keyi alip stroage de bulur ve gerekli gorsel nesneye yerlestirir .
     */
    public void setUserPP(){

        myRef.child("Users").child(USER_PHONE_NUMBER).child("userPP").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try{ // eger catch e dusuyor ise kullanicinin pp si yok ...

                    String userPPKey = snapshot.getValue().toString();

                    stRef.child("UsersPictures").child(userPPKey).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Picasso.get().load(uri).into(imageView_userStatus_userPP);

                        }
                    });

                }
                catch (Exception e){

                    Log.e("setUserPP excptn", e.getMessage());

                    imageView_userStatus_userPP.setImageDrawable(getResources().getDrawable(R.drawable.default_user));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    /**
     * alertView olusturur.
     * Kameradan resim alir ve ya galeriden resim secer .
     */
    public void alertView(){

        AlertDialog.Builder alertDialogbuilder = new AlertDialog.Builder(getActivity());

        alertDialogbuilder.setTitle("Profil Fotoğrafı");
        alertDialogbuilder.setMessage("Kameranızdan resim çekmek ister misiniz ?");
        alertDialogbuilder.setIcon(R.drawable.camera_photo);

        alertDialogbuilder.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                askCameraPermissions();

            }
        });

        alertDialogbuilder.setNegativeButton("GALERİDEN SEÇ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PHOTO_SELECTED_FROM_GALLERY);

            }
        });

        alertDialogbuilder.create().show();

    }


    /**
     * eger kameraya erisim izni yoksa kullaniciya sorar .
     */
    public void askCameraPermissions(){

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 101 );

        }
        else {

            dispatchTakePictureIntent();

        }

    }


    /**
     * kullanicidan hangi izni istedigini yakalar .
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 101){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }
        }
        else{

        }


    }


    /**
     * Alınan resimler sonucunda yapılacak islemler .
     * @param requestCode --> kamera mi galeri mi ?
     * @param resultCode
     * @param data
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHOTO_TAKEN_FROM_CAMERA) { // foto kameradan cekilirse .

            File f = new File(currentPhotoPath);
            photo_selected_from_galleryOrTakenCamera_uri = Uri.fromFile(f);

            imageView_userStatus_addIcon.setVisibility(View.INVISIBLE);
            imageView_userStatus_userPP.setVisibility(View.INVISIBLE);
            imageView_userStatus_sentStory.setVisibility(View.VISIBLE);


            if (photo_selected_from_galleryOrTakenCamera_uri != null){

                FirebaseUtils.saveStoryFirebaseStorage(photo_selected_from_galleryOrTakenCamera_uri,stRef,myRef, USER_PHONE_NUMBER, TimeClass.getClock());

            }
            else{

                Log.e("null deger uri", "null deger uri");

            }


        } else { // foto galeriden secilirse .

            if (data.getData() == null)
                return;

            photo_selected_from_galleryOrTakenCamera_uri = data.getData();

            if (photo_selected_from_galleryOrTakenCamera_uri != null){

                FirebaseUtils.saveStoryFirebaseStorage(photo_selected_from_galleryOrTakenCamera_uri,stRef,myRef, USER_PHONE_NUMBER, TimeClass.getClock());

            }
            else{

                Log.e("null deger uri", "null deger uri");

            }

            imageView_userStatus_addIcon.setVisibility(View.INVISIBLE);
            imageView_userStatus_userPP.setVisibility(View.INVISIBLE);
            imageView_userStatus_sentStory.setVisibility(View.VISIBLE);

        }

    }


    /**
     * kameradan cekilen resim icin file olusturur .
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    /**
     * kameradan cekilen resim icin uri olusturur .
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, PHOTO_TAKEN_FROM_CAMERA);
            }
        }
    }


    /**
     * arkadas storylerini liste atar .
     * o listte recyclerView e verilir .
     */
    public void getFriendStatus(){

        storyFriendList.clear(); // *** --> buna dikkat ...

        myRef.child("UserFriends").child(USER_PHONE_NUMBER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<UserFriend> friendArr = new ArrayList<>();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    if (postSnapshot == null)
                        return;
                    friendArr.add(postSnapshot.getValue(UserFriend.class));
                }

                myRef.child("UserStories").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            List<String> keyArr = new ArrayList<>();

                            for (DataSnapshot postSnapshot : snapshot.getChildren()){
                                if (postSnapshot == null)
                                    return;
                                String key = postSnapshot.getKey();
                                Log.e("story", key);
                                keyArr.add(key);
                            }

                            for (UserFriend friend : friendArr){
                                for (String num : keyArr){
                                    if (friend.getFriendPhone().equals(num)){

                                        // Burada story saati doldu mu bu kontrol ediliyor .
                                        myRef.child("UserStories").child(friend.getFriendPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                int counter = 0;
                                                for (DataSnapshot postSnapshot : snapshot.getChildren()){
                                                    Story story = postSnapshot.getValue(Story.class);
                                                    long timecurrent = System.currentTimeMillis();

                                                    if (timecurrent > story.getTimeStart() && timecurrent < story.getTimeEnd()){
                                                        counter++;
                                                    }
                                                } // for sonu...

                                                if (counter != 0){
                                                    storyFriendList.add(friend);
                                                    adapter.notifyDataSetChanged();
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        break;
                                    }
                                }
                            }

                            Log.e("size3", storyFriendList.size()+"");
                            recyclerView_userStatus.setHasFixedSize(true);
                            recyclerView_userStatus.setLayoutManager(new LinearLayoutManager(getActivity()));

                            adapter = new FriendStatusAdapter(getActivity(), storyFriendList);
                            recyclerView_userStatus.setAdapter(adapter);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                defineRecyclerView();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    /**
     * recyceler view u tanimlar .
     */
    public void defineRecyclerView(){

        Log.e("size-->", storyFriendList.size()+"");

        recyclerView_userStatus.setHasFixedSize(true);
        recyclerView_userStatus.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new FriendStatusAdapter(getActivity(), storyFriendList);
        recyclerView_userStatus.setAdapter(adapter);


    }


    /**
     * telefona giren kullanici story si varsa ona gore gorsel nesneye yerlestirme yapilir .
     */
    public void getMyStories(){




        myRef.child("UserStories").child(USER_PHONE_NUMBER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getChildrenCount() == 0){ // eger story yok ise ...
                    setUserPP();
                }
                else { // daha onceden atilmis story var ise ...

                    // Burada atilan storylerin zamanina bakacagiz ...
                    int counter = 0;
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Story story = postSnapshot.getValue(Story.class);
                        long timecurrent = System.currentTimeMillis();

                        if (timecurrent > story.getTimeStart() && timecurrent < story.getTimeEnd()) {
                            counter++;
                        }
                    }

                    if (counter == 0){ // zamana uyan story yok ...
                        setUserPP();
                    }
                    else { // eger zamana uyan story var ise ...

                        imageView_userStatus_addIcon.setVisibility(View.INVISIBLE);
                        imageView_userStatus_userPP.setVisibility(View.INVISIBLE);
                        imageView_userStatus_sentStory.setVisibility(View.VISIBLE);

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




}
