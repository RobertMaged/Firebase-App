package com.robert.android.firebaseapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;

public class SignUpActivity extends AppCompatActivity {

    EditText email, password, confirmPassword;
    Button register, login;
    FirebaseAuth mAuth = null;
    ProgressBar progressBar ;
    AccountProcessing accountProcessing = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.pass_word);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        register = (Button) findViewById(R.id.register);
        login = (Button) findViewById(R.id.login);
        //progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String getEmail = email.getText().toString();
                String getPassword = password.getText().toString();
                String getConfirmPassword = confirmPassword.getText().toString();

                accountProcessing = new AccountProcessing(mAuth, getEmail, getPassword, getConfirmPassword, SignUpActivity.this);
                accountProcessing.createNewUser();
            }
        });

    }


//    Uri uri;
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if ( requestCode == 20 && resultCode == Activity.RESULT_OK){
//            uri = data.getData();
//            ImageView imageView = (ImageView) findViewById(R.id.new_pic);
//            imageView.setImageURI(uri);
//            imageView.setVisibility(View.VISIBLE);
//            if (uri != null) {
//                mStorage.child(Calendar.getInstance().getTime().toString()).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(SignUpActivity.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
//                        }else{
//                            Toast.makeText(SignUpActivity.this, massage(task.getException().toString()), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//            }
//        }
//    }
}
