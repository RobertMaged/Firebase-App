package com.robert.android.firebaseapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button login, signUp;
    FirebaseAuth mAuth = null;
    ProgressBar progressBar ;
    AccountProcessing accountProcessing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.pass_word);
        login = (Button) findViewById(R.id.login);
        signUp= (Button) findViewById(R.id.sign_up);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);



        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(LoginActivity.this, SignUpActivity.class), AccountProcessing.EMAIL_CREATED);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountProcessing = new AccountProcessing(mAuth, email.getText().toString(), password.getText().toString(), LoginActivity.this);
                accountProcessing.signInExistUser(disableVerify);
            }
        });

    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == AccountProcessing.EMAIL_CREATED && resultCode == AccountProcessing.EMAIL_CREATED){
            finishActivity(AccountProcessing.EMAIL_CREATED);
        }

        if ( requestCode == AccountProcessing.EMAIL_SIGNEDIN_VERIFIED && resultCode == AccountProcessing.EMAIL_SIGNEDIN_VERIFIED){
            finishActivity(AccountProcessing.EMAIL_SIGNEDIN_VERIFIED);
        }
    }


    int help;
    boolean disableVerify;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        help = 0;
        disableVerify = false;
        //accountProcessing.setVerifyEnabled();
       getMenuInflater().inflate(R.menu.login_help, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if ( item.getItemId() == R.id.help){
            if (help >= 6){
                Toast.makeText(LoginActivity.this, "Verification disabled, but next open you need to log in again", Toast.LENGTH_LONG).show();
                //accountProcessing.setVerifyDisabled();
                disableVerify = true;
            }else {
                Toast.makeText(LoginActivity.this, "You must verify your Email, for testing you can disable verify by pressing help button 5 times", Toast.LENGTH_LONG).show();
                help ++;
            }
            }

        return true;
    }



    //    private void isEmailVerified(){
//       if ( mAuth.getCurrentUser().isEmailVerified()){
//           finish();
//       }else{
//           Toast.makeText(LoginActivity.this, "Please verify your account", Toast.LENGTH_SHORT).show();
//       }
//    }

//    private String massage(String toast) {
//        return toast.split(": ")[1];
//    }
}
