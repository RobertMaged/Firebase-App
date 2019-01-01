package com.robert.android.firebaseapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 *        API and how to get needed fields
 *
 *         https://restcountries.eu/rest/v2/all?fields=name;flag     give name and flag link of all
 *
 *         https://restcountries.eu/rest/v2/name/Afghanistan?fields=name;flag?fullText=true     give name and flag for specific country
 *
 *        https://restcountries.eu/rest/v2/region/europe     give by regions
 *        https://restcountries.eu/rest/v2/all
 */

public class CitesMainActivity extends AppCompatActivity {

    ProgressBar progressBar;

    //downloading the JSON then putting data in  mCities  ArrayList
    public class DownloadTask extends AsyncTask<Void, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(Void... voids) {

            String result = "";
            URL url;
            HttpURLConnection httpURLConnection = null;

            try {
                url = new URL("https://restcountries.eu/rest/v2/all?fields=name;alpha2Code");

            httpURLConnection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = httpURLConnection.getInputStream();

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            int data = inputStreamReader.read();

            while ( data != -1){

                result += (char) data;
                data = inputStreamReader.read();
            }

            //Log.i("first json", result);
            return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONArray arr = new JSONArray(result);

                for ( int i=0; i< arr.length(); i++ ){
                    myCities.add(new CitesMyCity(CitesMainActivity.this, arr.getJSONObject(i).getString("name"), arr.getJSONObject(i).getString("alpha2Code").toLowerCase()));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressBar.setVisibility(View.GONE);
            CitesAdapter adapter = new CitesAdapter(CitesMainActivity.this, myCities);
            listView.setAdapter(adapter);
        }
    }



    ArrayList<CitesMyCity> myCities = new ArrayList<CitesMyCity>();
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cites_main);

        listView = (ListView) findViewById(R.id.city_list);
        progressBar = (ProgressBar) findViewById(R.id.city_progressBar);

        DownloadTask task = new DownloadTask();
        task.execute();



//        try {
//
//            result = task.execute("https://restcountries.eu/rest/v2/all?fields=name;alpha2Code").toString();
//            JSONArray arr = new JSONArray(result);
//
//
//            for ( int i=0; i< arr.length(); i++ ){
//                myCities.add(new CitesMyCity(CitesMainActivity.this, arr.getJSONObject(i).getString("name"), arr.getJSONObject(i).getString("alpha2Code").toLowerCase()));
//            }
//
//        } catch (Exception e) {
//            Log.i("result: ", " failed");
//            // e.printStackTrace();
//        }




//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                View toolbar = view.findViewById(R.id.second);
//
//                // Creating the expand animation for the item
//                ExpandAnimation expandAni = new ExpandAnimation(toolbar, 500);
//
//                // Start the animation on the toolbar
//                toolbar.startAnimation(expandAni);
//
//            }
//        });


    }


//    public class ExpandAnimation extends Animation {
//        private View mAnimatedView;
//        private RelativeLayout.LayoutParams mViewLayoutParams;
//        private int mMarginStart, mMarginEnd;
//        private boolean mIsVisibleAfter = false;
//        private boolean mWasEndedAlready = false;
//
//        /**
//         * Initialize the animation
//         * @param view The layout we want to animate
//         * @param duration The duration of the animation, in ms
//         */
//        public ExpandAnimation(View view, int duration) {
//
//            setDuration(duration);
//            mAnimatedView = view;
//            mViewLayoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
//
//            // decide to show or hide the view
//            mIsVisibleAfter = (view.getVisibility() == View.VISIBLE);
//
//            mMarginStart = mViewLayoutParams.bottomMargin;
//            mMarginEnd = (mMarginStart == 0 ? (0- view.getHeight()) : 0);
//
//            view.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected void applyTransformation(float interpolatedTime, Transformation t) {
//            super.applyTransformation(interpolatedTime, t);
//
//            if (interpolatedTime < 1.0f) {
//
//                // Calculating the new bottom margin, and setting it
//                mViewLayoutParams.bottomMargin = mMarginStart
//                        + (int) ((mMarginEnd - mMarginStart) * interpolatedTime);
//
//                // Invalidating the layout, making us seeing the changes we made
//                mAnimatedView.requestLayout();
//
//                // Making sure we didn't run the ending before (it happens!)
//            } else if (!mWasEndedAlready) {
//                mViewLayoutParams.bottomMargin = mMarginEnd;
//                mAnimatedView.requestLayout();
//
//                if (mIsVisibleAfter) {
//                    mAnimatedView.setVisibility(View.GONE);
//                }
//                mWasEndedAlready = true;
//            }
//        }
//    }
}
