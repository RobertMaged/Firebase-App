package com.robert.android.firebaseapp;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.ArrayList;

//for the list item listView
public class CitesAdapter extends ArrayAdapter<CitesMyCity> {


    private Context mContext;

    public CitesAdapter(Context context, ArrayList<CitesMyCity> myCity) {
        super(context, 0, myCity);
        this.mContext = context;
    }

    ImageView countryFlag;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.cites_list_item, parent, false);
        }

        CitesMyCity currentPostion = getItem(position);

        countryFlag = (ImageView) listItemView.findViewById(R.id.country_image);





        try {

            Log.i("the_url", currentPostion.flagUrl);
            // loading image By Glide then view
            GlideApp.with(mContext).load(currentPostion.flagUrl).placeholder(R.drawable.flag_ic).into(countryFlag);

        } catch (Exception e) {
            Log.i("some_bitmap", "eroooor");
        }


        TextView countryName = (TextView) listItemView.findViewById(R.id.country_name);
        countryName.setText(currentPostion.name);


        return listItemView;
    }

}

