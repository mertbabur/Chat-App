package com.example.matsapp.Fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.matsapp.Activities.MainActivity;
import com.example.matsapp.Models.User;
import com.example.matsapp.R;
import com.example.matsapp.Utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.shape.InterpolateOnScrollPositionChangeHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class UserProfileFragment extends Fragment {

    private TextView textView_fragmentUserProfile_modify_click, textView_FragmentUserProfile_userPhoneNumber, textView_FragmentUserProfile_userAbout;
    private EditText editTextText_fragmentUserProfile_userName;
    private CardView cardView_FragmentUserProfile_userAbout, cardView_fragmentUserProfile_chooseWallpaper, cardView_fragmentUserProfile_signInDifferentA;
    private ImageView imageView_fragmentUserProfile_saveButton, fragmentUserProfile_userPP;

    private String USER_PHONE_NUMBER;
    private String USER_ABOUT;

    private FirebaseAuth mAuth;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private FirebaseStorage storage;
    private StorageReference stRef;

    private static final int PHOTO_TAKEN_FROM_CAMERA = 1;
    private static final int PHOTO_SELECTED_FROM_GALLERY = 2;

    private Bitmap bitmap_Photo;
    private Uri photo_selected_from_galleryOrTakenCamera_uri;

    private String currentPhotoPath;

    private Uri filePath;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_user_profile,container,false);

        defineAttributies(rootView);
        getAboutFromDB();

        actionAttributies();

        return rootView;

    }


    public void defineAttributies(View rootView){



        textView_fragmentUserProfile_modify_click = rootView.findViewById(R.id.textView_fragmentUserProfile_modify_click);
        textView_FragmentUserProfile_userPhoneNumber = rootView.findViewById(R.id.textView_FragmentUserProfile_userPhoneNumber);
        textView_FragmentUserProfile_userAbout = rootView.findViewById(R.id.textView_FragmentUserProfile_userAbout);
        editTextText_fragmentUserProfile_userName = rootView.findViewById(R.id.editTextText_fragmentUserProfile_userName);
        cardView_FragmentUserProfile_userAbout = rootView.findViewById(R.id.cardView_FragmentUserProfile_userAbout);
        imageView_fragmentUserProfile_saveButton = rootView.findViewById(R.id.imageView_fragmentUserProfile_saveButton);
        fragmentUserProfile_userPP = rootView.findViewById(R.id.fragmentUserProfile_userPP);
        cardView_fragmentUserProfile_chooseWallpaper = rootView.findViewById(R.id.cardView_fragmentUserProfile_chooseWallpaper);
        cardView_fragmentUserProfile_signInDifferentA = rootView.findViewById(R.id.cardView_fragmentUserProfile_signInDifferentA);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        storage = FirebaseStorage.getInstance();
        stRef = storage.getReference();

        USER_PHONE_NUMBER = getActivity().getSharedPreferences("userInfo",getActivity().MODE_PRIVATE).getString("phoneNum","1");




    }

    public void actionAttributies(){

        cardView_fragmentUserProfile_signInDifferentA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();


            }
        });


        cardView_fragmentUserProfile_chooseWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WalpaperFragment walpaperFragment = new WalpaperFragment();
                Bundle b = new Bundle();
                b.putString("whichFragment","UserProfileFragment");
                walpaperFragment.setArguments(b);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity, walpaperFragment).commit();


            }
        });


        textView_fragmentUserProfile_modify_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertView();

            }
        });


        cardView_FragmentUserProfile_userAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserAboutFragment userAboutFragment = new UserAboutFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity,userAboutFragment).commit();

            }
        });


        editTextText_fragmentUserProfile_userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                imageView_fragmentUserProfile_saveButton.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imageView_fragmentUserProfile_saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                modifyUserName();
                imageView_fragmentUserProfile_saveButton.setVisibility(View.INVISIBLE);


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

               // Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(intent, PHOTO_TAKEN_FROM_CAMERA);
                //deleteUserPPFromDBAndFirebaseStorage();

                askCameraPermissions();

            }
        });

        alertDialogbuilder.setNegativeButton("GALERİDEN SEÇ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PHOTO_SELECTED_FROM_GALLERY);
                //deleteUserPPFromDBAndFirebaseStorage();


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

            /*Bundle b = data.getExtras();
            bitmap_Photo =(Bitmap)b.get("data");
            fragmentUserProfile_userPP.setImageBitmap(bitmap_Photo);*/

            File f = new File(currentPhotoPath);
            photo_selected_from_galleryOrTakenCamera_uri = Uri.fromFile(f);
            fragmentUserProfile_userPP.setImageURI(photo_selected_from_galleryOrTakenCamera_uri);

            //photo_selected_from_galleryOrTakenCamera_uri = data.getData();




            if (photo_selected_from_galleryOrTakenCamera_uri != null){

                FirebaseUtils.savePhotoFirebaseStorage(photo_selected_from_galleryOrTakenCamera_uri,stRef,myRef, USER_PHONE_NUMBER);

            }
            else{

                Log.e("null deger uri", "null deger uri");

            }


        } else { // foto galeriden secilirse .

            photo_selected_from_galleryOrTakenCamera_uri = data.getData();

            if (photo_selected_from_galleryOrTakenCamera_uri != null){

                FirebaseUtils.savePhotoFirebaseStorage(photo_selected_from_galleryOrTakenCamera_uri,stRef,myRef, USER_PHONE_NUMBER);

            }
            else{

                Log.e("null deger uri", "null deger uri");

            }

            Picasso.get().load(photo_selected_from_galleryOrTakenCamera_uri).into(fragmentUserProfile_userPP);


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
     * firebase storageden eski resmi siler .
     * firebase db de userPP ye null yerlestirir .
     */
    public void deleteUserPPFromDBAndFirebaseStorage(){

        myRef.child("Users").child(USER_PHONE_NUMBER).child("userPP").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String userPPKey = snapshot.getValue().toString();

                stRef.child("UsersPictures").child(userPPKey).delete();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    /**
     * DB den kullanici hakkinda bilgisini ceker ve telefon nosuyla birlikte gerekli gorsel nesnlere yerlestirir .
     * kullanici ismini de gorsel nesneye yerlestirir .
     */
    public void getAboutFromDB(){

        myRef.child("Users").child(USER_PHONE_NUMBER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = new User();
                user = dataSnapshot.getValue(User.class);

                USER_ABOUT = user.getUserState();
                String userPPKey = user.getUserPP();

                if (!userPPKey.equals("null")){

                    stRef.child("UsersPictures").child(userPPKey).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Picasso.get().load(uri).into(fragmentUserProfile_userPP);

                        }
                    });

                }
                else{

                    fragmentUserProfile_userPP.setImageDrawable(getResources().getDrawable(R.drawable.default_user));

                }


                textView_FragmentUserProfile_userPhoneNumber.setText(USER_PHONE_NUMBER);
                textView_FragmentUserProfile_userAbout.setText(USER_ABOUT);

                String userName = user.getUserName();
                if (!userName.equals("null")){
                    editTextText_fragmentUserProfile_userName.setText(userName);
                }
                else {
                    editTextText_fragmentUserProfile_userName.setText("");
                }


                imageView_fragmentUserProfile_saveButton.setVisibility(View.INVISIBLE);

                Log.e("getAboutFromDB","calisti");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public void modifyUserName(){

        String newUserName = editTextText_fragmentUserProfile_userName.getText().toString();

        myRef.child("Users").child(USER_PHONE_NUMBER).child("userName").setValue(newUserName);

        //Log.e("modifyUserName","calisti");



    }




}
