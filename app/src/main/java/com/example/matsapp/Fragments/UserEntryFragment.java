package com.example.matsapp.Fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.matsapp.Activities.UserLoggedInActivity;
import com.example.matsapp.Models.User;
import com.example.matsapp.R;
import com.example.matsapp.Utils.DBBitmapUtility;
import com.example.matsapp.Utils.FirebaseUtils;
import com.example.matsapp.Utils.FriendNumberControl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UserEntryFragment extends Fragment {

    private EditText editText_user_name;
    private ImageView imageView_user_pp;
    private Button userEntry_button_continue;
    private TextView textView_select_pp_click;

    private Toolbar toolbar;

    private static final int PHOTO_TAKEN_FROM_CAMERA = 1;
    private static final int PHOTO_SELECTED_FROM_GALLERY = 2;

    private String USER_PHONE_NUMBER; // CodeVerificationFragmenttan gelen bundle da ki numarayi tutar. (bundle userPhone keyi ile tutar .)

    private Bitmap bitmap_Photo;
    private Uri photo_selected_from_galleryOrTakenCamera_uri;

    private FirebaseStorage storage;
    private StorageReference stRef;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private FirebaseAuth mAuth;

    private String currentPhotoPath;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_userentry_design, container, false);

        defineAttributies(rootView);
        toolbarSetTitleAgain();
        setUserNameAndPPToAttributies();
        actionAttributies();



        return rootView;
    }

    public void defineAttributies(View rootView) {

        editText_user_name = rootView.findViewById(R.id.editText_user_name);
        imageView_user_pp = rootView.findViewById(R.id.imageView_user_pp);
        userEntry_button_continue = rootView.findViewById(R.id.userEntry_button_continue);
        textView_select_pp_click = rootView.findViewById(R.id.textView_select_pp_click);

        USER_PHONE_NUMBER = getArguments().getString("userPhone");

        storage = FirebaseStorage.getInstance();
        stRef = storage.getReference();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        mAuth = FirebaseAuth.getInstance();



    }

    public void actionAttributies() {

        textView_select_pp_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogbuilder = new AlertDialog.Builder(getActivity());

                alertDialogbuilder.setTitle("Profil Fotoğrafı");
                alertDialogbuilder.setMessage("Kameranızdan resim çekmek ister misiniz ?");
                alertDialogbuilder.setIcon(R.drawable.camera_photo);

                alertDialogbuilder.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //startActivityForResult(intent, PHOTO_TAKEN_FROM_CAMERA);

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


        });


        userEntry_button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FriendNumberControl.useFriendNumberControlClass(((AppCompatActivity)getActivity()),USER_PHONE_NUMBER);

                if (!editText_user_name.getText().toString().equals("")){

                    saveUserNameToDB();
                    startActivity(new Intent(getActivity(), UserLoggedInActivity.class));
                    getActivity().finish();

                }
                else {

                    Toast.makeText(getActivity(), "Lütfen isminizi giriniz .", Toast.LENGTH_SHORT).show();

                }

            }
        });



    }


    /**
     * uygulamaya ilk giris yaparken kullanicinin ismini db ye kayderer .
     */
    public void saveUserNameToDB(){

        String userName = editText_user_name.getText().toString();

        myRef.child("Users").child(USER_PHONE_NUMBER).child("userName").setValue(userName);

    }


    /**
     * Kullanici ilk girdiginde firebase deki bilgileri ilgili gorsellere yerlestirir .
     */
    public void setUserNameAndPPToAttributies(){


        myRef.child("Users").child(USER_PHONE_NUMBER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                String userProfilePhotoKey = user.getUserPP();
                String userName = user.getUserName();


                if (userProfilePhotoKey != null){

                    stRef.child("UsersPictures").child(userProfilePhotoKey).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Picasso.get().load(uri).into(imageView_user_pp);

                        }
                    });

                }
                else{

                    imageView_user_pp.setImageDrawable(getResources().getDrawable(R.drawable.default_user));

                }

                if (!userName.equals("null")){

                    editText_user_name.setText(userName);

                }
                else {

                    editText_user_name.setHint("Lütfen isminizi giriniz .");

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    public void toolbarSetTitleAgain() {

        int toolbarId = getArguments().getInt("tobarId");
        toolbar = getActivity().findViewById(toolbarId);
        toolbar.setTitle("Profil Bilgisi");

    }


    public void askCameraPermissions(){

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 101 );

        }
        else {

            dispatchTakePictureIntent();

        }


    }

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
            imageView_user_pp.setImageBitmap(bitmap_Photo);

            photo_selected_from_galleryOrTakenCamera_uri = data.getData();*/

            File f = new File(currentPhotoPath);
            photo_selected_from_galleryOrTakenCamera_uri = Uri.fromFile(f);
            imageView_user_pp.setImageURI(photo_selected_from_galleryOrTakenCamera_uri);

            if (photo_selected_from_galleryOrTakenCamera_uri != null){


                FirebaseUtils.savePhotoFirebaseStorage(photo_selected_from_galleryOrTakenCamera_uri, stRef, myRef, USER_PHONE_NUMBER);

            }
            else{

                Log.e("null deger uri", "null deger uri");

            }


        } else { // foto galeriden secilirse .

            photo_selected_from_galleryOrTakenCamera_uri = data.getData();

            if (photo_selected_from_galleryOrTakenCamera_uri != null){

                FirebaseUtils.savePhotoFirebaseStorage(photo_selected_from_galleryOrTakenCamera_uri, stRef, myRef, USER_PHONE_NUMBER);

            }
            else{

                Log.e("null deger uri", "null deger uri");

            }

            Picasso.get().load(photo_selected_from_galleryOrTakenCamera_uri).into(imageView_user_pp);


        }


    }


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








}
