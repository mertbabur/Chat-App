package com.example.matsapp.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.matsapp.R;
import com.example.matsapp.Utils.FirebaseUtils;
import com.example.matsapp.Utils.TimeClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.Date;

public class PhotoSendFragment extends Fragment {

    private FloatingActionButton floatingActionButton_photoSendFragment_sendImage;
    private ImageView imageView_photoSendFrgment_imageMessageField, imageView_photoSendFragment_close;
    private EditText editTextText_photoSend_textMessageField;

    private String FRIEND_PHONE_NUMBER;
    private String FRIEND_NAME;
    private String USER_PHONE_NUMBER;

    private static final int PHOTO_TAKEN_FROM_CAMERA = 1;
    private static final int PHOTO_SELECTED_FROM_GALLERY = 2;

    private Bitmap bitmap_Photo;
    private Uri photo_selected_from_galleryOrTakenCamera_uri;

    private String currentPhotoPath;

    private FirebaseStorage storage;
    private StorageReference stRef;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private MediaPlayer mediaPlayer;

    private int which;
    private static final int CAMERA = 11;
    private static final int GALLERY = 22;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_photosend_design, container, false);

        defineAttributies(rootView);
        actionAttributies();

        if (which == CAMERA){ // chat fragmenttaki camera butonundan geldi ise .
            openCamera();
        }
        else { // eger chat fragmentindan gallery butonu ile geldi ise .
            openGallery();
        }


        return rootView;


    }


    public void defineAttributies(View rootView){

        FRIEND_PHONE_NUMBER = getArguments().getString("friendNum", "2");
        FRIEND_NAME = getArguments().getString("friendName", "null");
        USER_PHONE_NUMBER = getActivity().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE).getString("phoneNum", "1");

        floatingActionButton_photoSendFragment_sendImage = rootView.findViewById(R.id.floatingActionButton_photoSendFragment_sendImage);
        imageView_photoSendFrgment_imageMessageField = rootView.findViewById(R.id.imageView_photoSendFrgment_imageMessageField);
        editTextText_photoSend_textMessageField = rootView.findViewById(R.id.editTextText_photoSend_textMessageField);
        imageView_photoSendFragment_close = rootView.findViewById(R.id.imageView_photoSendFragment_close);

        storage = FirebaseStorage.getInstance();
        stRef = storage.getReference();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.send_message);

        which = getArguments().getInt("which",0);

    }


    public void actionAttributies(){

        floatingActionButton_photoSendFragment_sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.e("tik","tik");
                String messageText = editTextText_photoSend_textMessageField.getText().toString();
                Log.e("gonderilen message",messageText);

                if (photo_selected_from_galleryOrTakenCamera_uri != null && messageText.equals("")){ // sadece resim gonderiliyor ise ... -> type = image olarak belirlenir .

                    sendImage(messageText, "image");


                }
                else{ // resim ile birlikte text mesaji da gonderiliyor ise ... --> type = imageText olarak belirlenir .

                    sendImage(messageText,"imageText");

                }

            }
        });

        imageView_photoSendFragment_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goToChatFragment();

            }
        });

    }


    /**
     * resmi db ve storageye kaydedip chat fragment a gectikten sonra mesaj gonderme sesini calistirir .
     * @param messageText
     * @param messageType
     */
    public void sendImage(String messageText, String messageType){

        FirebaseUtils.saveMessagePhotoFirebaseStorageAndDB(photo_selected_from_galleryOrTakenCamera_uri, messageText, messageType, "false", TimeClass.getClock(), stRef, myRef, USER_PHONE_NUMBER, FRIEND_PHONE_NUMBER);

        editTextText_photoSend_textMessageField.setText("");

        goToChatFragment();

        mediaPlayer.start();


    }


    /**
     * telefon galerisini acar .
     */
    public void openGallery(){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_SELECTED_FROM_GALLERY);

    }

    /**
     * telefon kamerasini acar .
     */
    public void openCamera(){

        askCameraPermissions();

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


            if (photo_selected_from_galleryOrTakenCamera_uri != null){

                imageView_photoSendFrgment_imageMessageField.setImageURI(photo_selected_from_galleryOrTakenCamera_uri);

            }
            else{

                Log.e("null deger uri", "null deger uri");

            }


        } else { // foto galeriden secilirse .

            photo_selected_from_galleryOrTakenCamera_uri = data.getData();

            if (photo_selected_from_galleryOrTakenCamera_uri != null){

                Picasso.get().load(photo_selected_from_galleryOrTakenCamera_uri).into(imageView_photoSendFrgment_imageMessageField);

            }
            else{

                Log.e("null deger uri", "null deger uri");

            }




        }


    }


    /**
     * chatFragment i acar .
     */
    public void goToChatFragment(){

        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("friendName", FRIEND_NAME);
        bundle.putString("friendNum",FRIEND_PHONE_NUMBER);
        chatFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity,chatFragment).commit();
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



}
