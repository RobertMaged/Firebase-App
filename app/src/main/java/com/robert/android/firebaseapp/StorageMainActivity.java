package com.robert.android.firebaseapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class StorageMainActivity extends AppCompatActivity {

    /**
     *  in FireBase we use Storage to upload the files on it
     *  and RealTime dataBase to save File data "Name, URL, URL thumbnail, size, Type, Uploaded time"
     *
     *  in Storage first Reference For Every Email Account there is a folder named by his UserId which token from mAuth
     *  then each folder contain a thumbnail folder to upload in it thumbnails of images picked during uploading
     *
     *  in RealTime there is a reference called "Storage" in it push child by UserId token from mAuth then child for every file
     *
     *
     *        shape of "Storage" is like the following:
     *
     *        -Storage{
     *              +sdfsdfsdfsdaafadadafsdf         //this key is the user id which taken from the User Account when created first tim
     *              +sdfsdfsdfsdaafadadafsdf
     *              -sdfsdfsdfsdaafadadafsdf {
     *                           +ghfdfgrtrtrtyrtrttrt    //random id pushed for every file uploaded by user contain file information
     *                           +ghfdfgrtrtrtyrtrttrt
     *                           +ghfdfgrtrtrtyrtrttrt
     *                              }
     *        }
     *
     *
     */


    private RecyclerView mRecyclerView;
    private StorageImageAdaptor mStorageImageAdaptor;

    private FirebaseAuth mAuth;      //to know which user app is dealing with
    private DatabaseReference mRef;
    StorageReference mStorage = null;            //save the reference of user in Storage database
    private List<StorageImageUpload> myUploads;    //take saved files in Storage database to view it in recycler view

    Uri uri;       //the picked data
    String currTime;  //time by milli secend
    StorageImageUpload storageImageUpload = null;     //used for recycler View
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_main);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.upload_floatingButton);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = (ProgressBar) findViewById(R.id.storage_progressBar);

        myUploads = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference("Storage").child(mAuth.getCurrentUser().getUid());
        mStorage = FirebaseStorage.getInstance().getReference(mAuth.getCurrentUser().getUid());

        //get all uploaded files data included url to download later
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myUploads.clear();
                for (DataSnapshot dataDownload : dataSnapshot.getChildren()) {
                    myUploads.add(0, dataDownload.getValue(StorageImageUpload.class));
                }

                progressBar.setVisibility(View.GONE);
                mStorageImageAdaptor = new StorageImageAdaptor(StorageMainActivity.this, myUploads);
                mRecyclerView.setAdapter(mStorageImageAdaptor);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                makeToast(databaseError.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });

        //pick a file to upload it
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("*/*"), 20);
            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //get the filed picked in uri
        if (requestCode == 20 && resultCode == Activity.RESULT_OK) {
            uri = data.getData();


            if (uri != null) {
                //first know the file type  if image we make a thumbnail and upload it to
                storageImageUpload = new StorageImageUpload(null, null, null, null, fileExtension(uri), null, null);

                makeToast("Uploading...");   //show toast
                currTime = String.valueOf(System.currentTimeMillis());   //get time

                //now we start uploading the file ;
                mStorage.child(currTime + fileName(uri)).putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return mStorage.child(currTime + fileName(uri)).getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        //file is now uploaded successfully lets put the file data and url in RealTime DataBase
                        if (task.isSuccessful()) {
                            //main file is now uploaded
                            storageImageUpload.name = currTime + fileName(uri);
                            storageImageUpload.uploadTime = Calendar.getInstance().getTime().toString();
                            storageImageUpload.size = fileSize(uri);
                            storageImageUpload.extension = fileExtension(uri);
                            storageImageUpload.url = task.getResult().toString();  //URL


                            //we do not need to upload the data two times so if file is not image we upload data info here
                            //if not in next if w create thumbnail then push data there
                            // noNull is based on file not image  variable  'storageImageUpload.urlThumb' set to "No Thumbnail" in next if
                            if (noNull()) {
                                //process completed and push urls to realtime database
                                storageImageUpload.id = mRef.push().getKey();
                                mRef.child(storageImageUpload.id).setValue(storageImageUpload);
                                makeToast("Upload Successfully");
                                storageImageUpload = null;
                            }

                        } else {
                            makeToast(task.getException().toString());
                        }
                    }
                });

                //check if uploaded is image crate thumbnail and upload it too
                if (storageImageUpload.getType().equals("image")) {   // getType is a method to know the file type

                    //make a thumbnail of image and uploading it
                    mStorage.child("thumbnails").child("thumb-" + currTime + fileName(uri)).putBytes(makeThumbnail(uri)).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return mStorage.child("thumbnails").child("thumb-" + currTime + fileName(uri)).getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            storageImageUpload.thumbUrl = task.getResult().toString();

                            if (noNull()) {
                                //process completed and push urls to realtime database
                                storageImageUpload.id = mRef.push().getKey();
                                mRef.child(storageImageUpload.id).setValue(storageImageUpload);
                                makeToast("Upload Successfully");
                                storageImageUpload = null;
                            }
                        }
                    });

                }else{
                    storageImageUpload.thumbUrl = "NO THUMBNAIL";
                }
            }
        }


    }




    private boolean noNull() {
        if (storageImageUpload == null) return false;
        else if (storageImageUpload.url == null) return false;
        else if (storageImageUpload.thumbUrl == null) return false;
        else return true;
    }

    private byte[] makeThumbnail(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = ThumbnailUtils.extractThumbnail(
                    MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri),
                    128,
                    128);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    private String fileName(Uri uri) {
        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    private String fileSize(Uri uri) {
        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String size = returnCursor.getString(sizeIndex);
        returnCursor.close();

        //size = Formatter.formatFileSize(StorageMainActivity.this, Long.parseLong(size));

        return size;
    }

    private String fileExtension(Uri uri) {
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        try { return mime.getExtensionFromMimeType(getContentResolver().getType(uri)).toUpperCase();}
        catch (Exception e) { return "unKnown";}
    }


    // Fire base exceptions is like "mAuthErrorNoLikeId: no such user found",
    // so before the ':' its for developer not user so delete it
    private void makeToast(String toast) {
        try {
            toast = toast.split(": ")[1];
        } catch (Exception e) {
        }
        Toast.makeText(StorageMainActivity.this, toast, Toast.LENGTH_SHORT).show();
    }
}
