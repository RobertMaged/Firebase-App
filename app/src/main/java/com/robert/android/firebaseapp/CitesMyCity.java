package com.robert.android.firebaseapp;


import android.content.Context;

//shape of downloaded data from JSON
public class CitesMyCity {

//    private String cloud = "https://api.cloudconvert.com/convert?apikey=EAsaE7rCnj5OryuCHgTppx3MeYxBQHEG2ReKfcCZvB152G8GcFANCYwGWF7U1MNn&inputformat=svg&outputformat=png&input=download&file=https%3A%2F%2Frestcountries.eu%2Fdata%2F";

    //first part if ong flag images URL
    private String url = "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png100px/";

    public String name, flagUrl;
    public Context context;

    public CitesMyCity(Context context, String name, String alpha2Code){
        this.name = name;
        //this.alpha3Code = alpha3Code;
        url = url + alpha2Code + ".png"; //completing URL
        this.flagUrl = url;  //url is ready for download now
    }


    public CitesMyCity(){

    }
}
