package com.robert.android.firebaseapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class WeatherMainActivity extends AppCompatActivity {

//    private FirebaseAuth mAuth;
//    private DatabaseReference mRef;
//    private StorageReference mStorage;
//
//    StorageImageUpload mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_main);

//        mAuth = FirebaseAuth.getInstance();
//        mRef = FirebaseDatabase.getInstance().getReference("Storage").child(mAuth.getCurrentUser().getUid());
//        mStorage = FirebaseStorage.getInstance().getReference(mAuth.getCurrentUser().getUid());
//
//        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot downloadData : dataSnapshot.getChildren()){
//                    mUploads = downloadData.getValue(StorageImageUpload.class);
//                    StorageImageUpload storageImageUpload = new StorageImageUpload(downloadData.getKey(), mUploads.name, mUploads.uploadTime, mUploads.size, mUploads.extension, mUploads.url, mUploads.thumbUrl);
//                    mRef.child(downloadData.getKey()).setValue(storageImageUpload);
//                    Log.i("the daaata" , downloadData.getKey());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }
}
