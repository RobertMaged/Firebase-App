package com.robert.android.firebaseapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.Calendar;



public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth ;    // Firebase Authentication
    AccountProcessing accountProcessing = null;    //this class make some proccess for login and creating emails
    DatabaseReference mRef;     //any operation made in FireBase RealTime data made by this
    private AccountInfoDatabase accountInfoDatabase;   //shape of the data set and get in data base

    /**
     *  shape of "Email" is like the following:
     *
     *  -Emails{
     *        +sdfsdfsdfsdaafadadafsdf         //and this key is the user id which taken from the loged user when created first time
     *        +sdfsdfsdfsdaafadadafsdf
     *        +sdfsdfsdfsdaafadadafsdf
     *  }
     */


    boolean haveData = false;  //to user if we get the data of the user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference("Emails");  //Emails is the reference which accounts data saved
        accountProcessing = new AccountProcessing(mAuth, mRef, MainActivity.this);




        GridLayout gridLayout = (GridLayout) findViewById(R.id.grid_layout);
        for ( int i=0; i<gridLayout.getChildCount(); i++ ) {
            FrameLayout frameLayout = (FrameLayout) gridLayout.getChildAt(i);
            frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (String.valueOf(v.getTag())) {
                        case "Notes Frame":
                            startActivity(new Intent(MainActivity.this, NotesMainActivity.class));
                            break;

                        case "Storage Frame":
                            startActivity(new Intent(MainActivity.this, StorageMainActivity.class));
                            break;

                        case "Weather Frame":  //unused activity till now
                            startActivity(new Intent(MainActivity.this, WeatherMainActivity.class));
                            break;

                        case "Cites Frame":
                            startActivity(new Intent(MainActivity.this, CitesMainActivity.class));
                            break;
                    }
                }
            });
        }






//        imageView = (ImageView) findViewById(R.id.selected_image);
//        Button pickedImage = (Button) findViewById(R.id.pick_image);
//
//        pickedImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), 20);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // take a picture from storage
//        if ( requestCode == 20 && resultCode == Activity.RESULT_OK){
//            uri = data.getData();
//            imageView.setImageURI(uri);
//            imageView.setVisibility(View.VISIBLE);
//            if (uri != null) {
//                mStorage.child(Calendar.getInstance().getTime().toString()).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(MainActivity.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
//                        }else{
//                            Toast.makeText(MainActivity.this, massage(task.getException().toString()), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//            }
//        }


        //get result which started from onStart() if user has Loged in and verified his email to can start app main functions
        if ( requestCode == AccountProcessing.EMAIL_SIGNEDIN_VERIFIED && resultCode == AccountProcessing.EMAIL_SIGNEDIN_VERIFIED ){
            mAuth.getCurrentUser();
            //accountInfoDatabase = accountProcessing.getProfileData();
        }

        // get result which started from onStart()  if user has Loged in but disabled verification just for test
        if ( requestCode == AccountProcessing.EMAIL_SIGNEDIN_VERIFIED && resultCode == AccountProcessing.EMAIL_SIGNEDIN_NOT_VERIFIED ){
            disableVerify = true;
            mAuth.getCurrentUser();
            //accountInfoDatabase = accountProcessing.getProfileData();
        }
    }
    boolean disableVerify = false;

    @Override
    protected void onStart() {
        super.onStart();
        //first if to check if user has email and log in, second if to get data about his email
        if ( accountProcessing.start(disableVerify)){
            startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), AccountProcessing.EMAIL_SIGNEDIN_VERIFIED);
        }else if ( !haveData){
            //got to "Emails" then "Email user Id geted from mAuth"
            mRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                for (DataSnapshot dataDownload : dataSnapshot.getChildren()) {
//                    if (dataDownload.getKey().equals(mAuth.getCurrentUser().getUid())) {
//                        accountInfoDatabase = dataDownload.getValue(AccountInfoDatabase.class);
//                        Log.i("the Account data id:  ", accountInfoDatabase.id);
//                    }
//                }
                    haveData = true;
                    Log.i("dataSnapShot", dataSnapshot.getValue(AccountInfoDatabase.class).profilePicUrl);

                    accountInfoDatabase = dataSnapshot.getValue(AccountInfoDatabase.class);
                    Log.i("the AAAccount data id: ", accountInfoDatabase.id);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    //clear the any cache downloaded by Glide
    @Override
    protected void onDestroy() {
        super.onDestroy();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(MainActivity.this).clearDiskCache();
                //Toast.makeText(StorageMainActivity.this, "Destroy", Toast.LENGTH_SHORT).show();
            }
        }).start();

        Glide.get(this).clearMemory();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private AlertDialog alertDialog;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //sign out from Email
        if ( item.getItemId() == R.id.logout ) {
            accountProcessing.signOut();
            mAuth.getCurrentUser();
            haveData = false;
            recreate();
        }

        //View the info Entered by user and can be edited
        if ( item.getItemId() == R.id.profile_option ) {
            if (!haveData) {
                Toast.makeText(MainActivity.this, "Still loading, wait a second ", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);

                View viewNote = LayoutInflater.from(MainActivity.this).inflate(R.layout.basic_info, null);
                alertBuilder.setView(viewNote);
                alertDialog = alertBuilder.create();
                alertDialog.show();  //open a view to show info

                //initialize Views in that view
                final EditText newName = (EditText) viewNote.findViewById(R.id.new_name);
                final EditText newAge = (EditText) viewNote.findViewById(R.id.new_age);
                Button saveProfile = (Button) viewNote.findViewById(R.id.new_save);
                TextView closeInfo = (TextView) viewNote.findViewById(R.id.close_info);
                final TextView saveEditInfo = (TextView) viewNote.findViewById(R.id.save_edit_info);

                try {
                    newName.setText(accountInfoDatabase.name);
                    newAge.setText(accountInfoDatabase.age);
                } catch (Exception e) {
                }
                saveProfile.setVisibility(View.GONE);
                closeInfo.setOnClickListener(new View.OnClickListener() {     //close the alart dialog
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                saveEditInfo.setVisibility(View.VISIBLE);
                saveEditInfo.setOnClickListener(new View.OnClickListener() {  // save data and close alert dialog
                    @Override
                    public void onClick(View v) {
                        mRef.child(accountInfoDatabase.id).setValue(new AccountInfoDatabase(accountInfoDatabase.id, accountInfoDatabase.email, newName.getText().toString(), newAge.getText().toString(), "https://just for test"));
                        accountInfoDatabase.name = newName.getText().toString();
                        accountInfoDatabase.age = newAge.getText().toString();
                        alertDialog.dismiss();
                        saveEditInfo.setVisibility(View.GONE);
                    }
                });
            }
        }

        return true;
    }
}
