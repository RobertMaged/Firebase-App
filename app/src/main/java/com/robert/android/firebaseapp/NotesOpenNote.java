package com.robert.android.firebaseapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.robert.android.firebaseapp.NotesMyNote;
import com.robert.android.firebaseapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotesOpenNote extends AppCompatActivity {

    TextView titleView, noteView;   //in case to view note
    EditText titleEdit, noteEdit;   //in case to create or edit note
    String id, titleText, noteText;
    ImageView edit;                 //the edit icon
    boolean editing = false;        //is intent open in editing case or viewing case, use for toggle
    FirebaseAuth mAuth;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_open_note);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference("Notes").child(mAuth.getCurrentUser().getUid());


        titleView = (TextView) findViewById(R.id.view_title);
        titleEdit = (EditText) findViewById(R.id.edit_title);

        noteView = (TextView) findViewById(R.id.view_note);
        noteEdit = (EditText) findViewById(R.id.edit_note);

        final TextView date = (TextView) findViewById(R.id.open_date);  //currunt date of created notes

        edit = (ImageView) findViewById(R.id.edit);
        ImageView delete = (ImageView) findViewById(R.id.delete);  //delete icon

        try {      // in case user viewing a note then get its data
            id = getIntent().getExtras().getString("id_key");
            titleText = getIntent().getExtras().getString("title_key");
            noteText = getIntent().getExtras().getString("note_key");
            date.setText( getIntent().getExtras().getString("date_key"));
            switchView("view", titleText, noteText);
        }catch (Exception e){  // if not data returned then he is creating a new note
            id = mRef.push().getKey() ;
            switchView("edit", titleText, noteText);   //procces in which case we are edit or view to show view and getStrings
            editing = true; //editing is now open
        }


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( !editing ) {

                    switchView("edit", titleText, noteText);
                    editing = true;
                }else{  // get data of entered Strings
                    edit.setImageResource(R.drawable.edit_ic);
                    titleText = titleEdit.getText().toString();
                    noteText = noteEdit.getText().toString();
                    date.setText(currentDate());
                    switchView("view", titleText, noteText);
                    editing = false;

                    mRef.child(id).setValue(new NotesMyNote(id, titleText, noteText, currentDate()));
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {  //delete note from database
            @Override
            public void onClick(View v) {
                mRef.child(id).removeValue();
                finish();
            }
        });
    }

    //procces in which case we are edit or view to show views
    private void switchView(String viewOrEdit, String titleValue, String noteValue){
        switch (viewOrEdit){
            case"view":
                titleView.setText(titleValue);
                titleEdit.setVisibility(View.GONE); /////
                titleView.setVisibility(View.VISIBLE);
                edit.setImageResource(R.drawable.edit_ic);
                noteView.setText(noteValue);
                noteEdit.setVisibility(View.GONE); ///////
                noteView.setVisibility(View.VISIBLE);
                break;

            case "edit":
                titleEdit.setText(titleValue);
                titleView.setVisibility(View.GONE); /////
                titleEdit.setVisibility(View.VISIBLE);
                edit.setImageResource(R.drawable.save_ic);
                noteEdit.setText(noteValue);
                noteView.setVisibility(View.GONE); ///////
                noteEdit.setVisibility(View.VISIBLE);
                break;
        }
    }

    public String currentDate(){   // get currunt date from user device

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mformat = new SimpleDateFormat("EEEE hh:mm a ");
        String theDate = mformat.format(calendar.getTime());
        return theDate;
    }
}
