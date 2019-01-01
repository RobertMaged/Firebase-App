package com.robert.android.firebaseapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

//for ListView In Notes MainActivity
public class NotesAdapter extends ArrayAdapter<NotesMyNote> {

    public NotesAdapter(Context context, ArrayList<NotesMyNote> myNote) {
        super(context, 0, myNote);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.notes_list_item, parent, false);
        }

        NotesMyNote currentPostion = getItem(position);

        TextView textTilte = (TextView) listItemView.findViewById(R.id.my_list_title);
        textTilte.setText(currentPostion.title);

        TextView textDate = (TextView) listItemView.findViewById(R.id.my_list_date);
        textDate.setText(currentPostion.date);


        return listItemView;
    }
}