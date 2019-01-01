package com.robert.android.firebaseapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotesMainActivity extends AppCompatActivity {

    /**
     *  shape of "Notes" is like the following:
     *
     *  -notes{
     *        +sdfsdfsdfsdaafadadafsdf         //and this key is the user id which taken from the logedIn user when created first time
     *        +sdfsdfsdfsdaafadadafsdf
     *        +sdfsdfsdfsdaafadadafsdf
     *  }
     */

    FirebaseAuth mAuth = null;        //Firebase Authentication
    DatabaseReference mRef = null;
    ArrayList<NotesMyNote> mNoteList = null;  //list to take data by a for loop for notes
    ListView listView;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_main);

        FloatingActionButton floatingButton = (FloatingActionButton) findViewById(R.id.floatingButton);
        listView= (ListView) findViewById(R.id.list_view);
        progressBar = (ProgressBar) findViewById(R.id.note_progressBar);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference("Notes").child(mAuth.getCurrentUser().getUid());
        mNoteList = new ArrayList<NotesMyNote>();


        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openNote = new Intent(NotesMainActivity.this, NotesOpenNote.class);
                startActivity(openNote);
            }
        });

        //open by position Notes Open note Activity to view the note parts
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotesMyNote notesMyNote = mNoteList.get(position);
                Intent openNote = new Intent(NotesMainActivity.this, NotesOpenNote.class);
                openNote.putExtra("id_key", notesMyNote.id);
                openNote.putExtra("title_key", notesMyNote.title);
                openNote.putExtra("note_key", notesMyNote.note);
                openNote.putExtra("date_key", notesMyNote.date);
                startActivity(openNote);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //take all of user Notes
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mNoteList.clear();
                for (DataSnapshot downloadData: dataSnapshot.getChildren()){
                    NotesMyNote ss =  downloadData.getValue(NotesMyNote.class);
                    mNoteList.add(0, ss);  // 0 to sort by last note
                }

                progressBar.setVisibility(View.GONE);
                //Note adapter is for the customized  list item
                NotesAdapter notesAdapter = new NotesAdapter(NotesMainActivity.this, mNoteList);
                listView.setAdapter(notesAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
               Toast.makeText(NotesMainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
