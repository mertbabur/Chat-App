package com.example.matsapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.matsapp.Fragments.CodeVerificationFragment;
import com.example.matsapp.Fragments.LogInFragment;
import com.example.matsapp.Fragments.MainUserPageFragment;
import com.example.matsapp.R;
import com.example.matsapp.Utils.FriendNumberControl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;

    private String USER_PHONE_NUMBER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        defineAttributes();
        permissionsForContact();

    }

    public void defineAttributes(){

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        USER_PHONE_NUMBER = getSharedPreferences("userInfo",MODE_PRIVATE).getString("phoneNum","1");

    }


    /**
     * rehbere erisim icin izin ister .
     */
    public void permissionsForContact(){

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_CONTACTS}, 103 );

        }
        else {

            isUserLogged();

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

        if (requestCode == 103){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                isUserLogged();
            }
            else{
                Toast.makeText(this, "Rehbere erişime izin veriniz. Uygulamayı tekrar açıp kapayınız.", Toast.LENGTH_SHORT).show();
                Log.e("onReq","girdi");
            }
        }

    }

    /**
     * Kullanici giris yapti ise MainUserPageActivity e yonlendirir.
     * Kullanici giris yapmadi ise LogInActivity e yonledirir.
     */
    public void isUserLogged() {

        if (user == null) {

            startActivity(new Intent(MainActivity.this, LogInActivity.class));

            finish();
        } else {

            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            // eger kullanici ban listesinde null doner ve catch kismi gerceklesir .
            myRef.child("BannedUsers").child(USER_PHONE_NUMBER).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    try {

                        String temp = snapshot.getValue().toString();

                        alertView();

                    } catch (Exception e) {
                        Log.e("exception main activity", e.getMessage());

                        FriendNumberControl.useFriendNumberControlClass(MainActivity.this, USER_PHONE_NUMBER);

                        startActivity(new Intent(MainActivity.this, UserLoggedInActivity.class));
                        finish();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }


    /**
     * alertView olusturur .
     * uygulamayı kapatır .
     */
    public void alertView(){

        AlertDialog.Builder alertDialogbuilder = new AlertDialog.Builder(MainActivity.this);

        alertDialogbuilder.setTitle("Opps !");
        alertDialogbuilder.setMessage(USER_PHONE_NUMBER + " numarası şikayetler üzerine banlanmıştır. Hesabınız bu yüzden askıya alındı .");

        alertDialogbuilder.setPositiveButton("Uygulamayı kapat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                System.exit(0);

            }
        });

        alertDialogbuilder.setNegativeButton("", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }});

        alertDialogbuilder.create().show();

    }

}
