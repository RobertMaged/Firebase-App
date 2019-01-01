package com.robert.android.firebaseapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

@IgnoreExtraProperties
public class AccountInfoDatabase {   //used in Main Activity only    and  AccountProccessing class

    public String id, email, name, age, profilePicUrl;

     //shape of data in RealTime Database
    public AccountInfoDatabase(String id, String email, String name, String age, String prifilePicUrl){
        this.id = id;               //id is the key which saved in Emails reference and equals user token id in mAuth
        this.email = email;        //user email
        this.name = name;
        this.age = age;
        this.profilePicUrl = prifilePicUrl;       //profile pic but not set til now
    }


//    public AccountInfoDatabase(String email, String name, String age, String prifilePicUrl){
//        this.email = email;
//        this.name = name;
//        this.age = age;
//        this.profilePicUrl = prifilePicUrl;
//    }

    public AccountInfoDatabase(){

    }


}

