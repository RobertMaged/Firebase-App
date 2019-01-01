package com.robert.android.firebaseapp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
//shape of data in Notes reference
//called by all Notes activities
public class NotesMyNote {

    public String id, title, note, date;
    public NotesMyNote(String id, String title, String note, String date){
        this.id = id;     //acount user id take by mAuth
        this.title = title;
        this.note = note;
        this.date = date;
    }


    public NotesMyNote(){

    }
}
