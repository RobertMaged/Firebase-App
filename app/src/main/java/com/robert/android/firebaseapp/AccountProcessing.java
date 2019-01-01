package com.robert.android.firebaseapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AccountProcessing {  // used in "Login", "Signup", "Main" Activities
    private FirebaseAuth mAuth ;
    private String email, password, confirmPassword;
    private ProgressBar progressBar;
    private Context context;
    private Activity activity;
    public static int EMAIL_CREATED = 3, EMAIL_SIGNEDIN_VERIFIED = 4, EMAIL_SIGNEDIN_NOT_VERIFIED = 5;

    private String id;
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Emails");

    public AccountProcessing() {
    }

    //called by Main Activity
    public AccountProcessing(FirebaseAuth firebaseAuth, DatabaseReference mRef, Context context) {
        mAuth = firebaseAuth;
        this.context = context;
        this.mRef = mRef;
    }

    //called by SignUp Activity
    public AccountProcessing(FirebaseAuth firebaseAuth, String userEmail, String userPassword, String userConfirmPassword, Context context) {
        mAuth = firebaseAuth;
        email = userEmail;
        password = userPassword;
        confirmPassword = userConfirmPassword;
        this.context = context;
        this.activity = (Activity) context;
        progressBar = (ProgressBar) activity.findViewById(R.id.progress_bar);
    }

    //called by LogIn Activity
    public AccountProcessing(FirebaseAuth firebaseAuth, String userEmail, String userPassword, Context context) {
        mAuth = firebaseAuth;
        email = userEmail;
        password = userPassword;
        this.context = context;
        this.activity = (Activity) context;
        progressBar = (ProgressBar) activity.findViewById(R.id.progress_bar);
    }

    //method called in MainActivity by onStart
    //check if user loged in and verified or not verified
    public boolean start(boolean disableVerify) {
        if ( !disableVerify ) {
            return (mAuth.getCurrentUser() == null || !mAuth.getCurrentUser().isEmailVerified());
        }else{
            return (mAuth.getCurrentUser() == null);
        }
    }


//called by main Activity option menu to sign out user
    public void signOut() {
        mAuth.signOut();
    }

    ////called by SignUp Activity to create a new user
    public void createNewUser() {
        //check if all Fields filled and same passwors
        if (!email.isEmpty() && !password.isEmpty()) {
            if (password.equals(confirmPassword)) {
                progressBar.setVisibility(View.VISIBLE);
                //create a new user
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            verifyProcess("send");   //method to send verify email
                            makeToast("Done, Email verification sent");  //make toast is method to show toast but with special case
                            activity.setResult(EMAIL_CREATED);
                            progressBar.setVisibility(View.GONE);
//                            activity.finish();
                        } else {
                            makeToast(task.getException().toString());
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });
            } else {
                makeToast("Password and Confirm must be the same");
            }
        } else {
            makeToast("Please fill all fields");
        }
    }

////called by LogIn Activity to logIn
    public void signInExistUser(final boolean disableVerify) {
        if (!email.isEmpty() && !password.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if ( !disableVerify) {  // check if verification is disabled just for test by help opten menu
                            verifyProcess("check"); //method to check if email verified
                        }else {
                            activity.setResult(EMAIL_SIGNEDIN_NOT_VERIFIED); //do not care about verify and sign in user
                            progressBar.setVisibility(View.GONE);
                            activity.finish();
                        }
                    } else {
                        makeToast(task.getException().toString());
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            makeToast("Please fill all fields");
        }

    }


    /**
     * Send verify email        ->    type = "send"
     * check email if verified   ->    type = "check"
     */
    private void verifyProcess(String type) {

        switch (type) {
            case "send":
                mAuth.getCurrentUser().sendEmailVerification();
                setProfileData();  //method to open view to take user Name and Age
                break;


            case "check":
                if (mAuth.getCurrentUser().isEmailVerified()) {
                    activity.setResult(EMAIL_SIGNEDIN_VERIFIED);
                    progressBar.setVisibility(View.GONE);
                    activity.finish();
                } else {
                    makeToast("Please verify your account");
                    progressBar.setVisibility(View.GONE);
                }

                break;
        }
    }


    private AccountInfoDatabase accountInfoDatabase = null;
    private AlertDialog alertDialog;
    private EditText newName, newAge;

    //open View To set user Name And Age and make all of his data in RealTime Database reference
    public void setProfileData() {


//             Open Basic info Screen
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

        View viewNote = LayoutInflater.from(context).inflate(R.layout.basic_info, null);
        alertBuilder.setView(viewNote);
        alertDialog = alertBuilder.create();
        alertDialog.show();

        ImageView newProfilePic = (ImageView) viewNote.findViewById(R.id.new_pic);
        newName = (EditText) viewNote.findViewById(R.id.new_name);
        newAge = (EditText) viewNote.findViewById(R.id.new_age);
        Button saveProfile = (Button) viewNote.findViewById(R.id.new_save);
        TextView closeInfo = (TextView) viewNote.findViewById(R.id.close_info);
        closeInfo.setVisibility(View.GONE);
//        newProfilePic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                activity.startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*").putExtra("user_id", ), 20);
//            }
//        });

//        id = mRef.push().getKey();
        id = mAuth.getCurrentUser().getUid();

        //first make new reference for the user no care for name and age
        mRef.child(id).setValue(new AccountInfoDatabase(id, mAuth.getCurrentUser().getEmail(), "No Name Set", "No Age Set", "https://just for test"));
        //save data he entered
        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mRef.child(id).setValue(new AccountInfoDatabase(id, mAuth.getCurrentUser().getEmail(), newName.getText().toString(), newAge.getText().toString(), "https://just for test"));
                alertDialog.dismiss();
                activity.finish();
            }
        });
    }


    // Fire base exceptions is like "mAuthErrorNoLikeId: no such user found",
    // so before the ':' its for developer not user so delete it
    private void makeToast(String toast) {
        try {
            toast = toast.split(": ")[1];
        } catch (Exception e) {
        }
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }
}
