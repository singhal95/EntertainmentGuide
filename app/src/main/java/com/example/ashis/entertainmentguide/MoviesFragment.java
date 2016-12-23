package com.example.ashis.entertainmentguide;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ashis on 12/22/2016.
 */

public class MoviesFragment extends Fragment {
   private ArrayList<String> images;
    private String imageUrl = null;
    private View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main,container,false);

        FetchMovieData getMovieData = new FetchMovieData();

        getMovieData.execute();
       // int item = images.size();
        //Log.i("size",String.valueOf(item));
     //   gridView.setAdapter(new ImageAdapter(this));
        return rootView;
    }

    public class FetchMovieData extends AsyncTask<String,Void,ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;

            BufferedReader reader = null;

            String moviesJsonString = null;

            String BASE_URI = "https://api.themoviedb.org/3/discover/movie?";

            String LANGUAGE = "language";

            String LANGUAGE_VALUE = "en-US";

            String SORT_BY = "sort_by";

            String SORT_BY_VALUE = "popularity.desc";

            String URI_API_KEY = "api_key";

            Uri.Builder uribuilder = Uri.parse(BASE_URI).buildUpon().appendQueryParameter(URI_API_KEY,BuildConfig.OPEN_MOVIE_GUIDE_API_KEY).appendQueryParameter(LANGUAGE,LANGUAGE_VALUE).appendQueryParameter(SORT_BY,SORT_BY_VALUE);

            try {

                URL url = new URL(uribuilder.toString());

                urlConnection = (HttpURLConnection)url.openConnection();

                urlConnection.setRequestMethod("GET");

                urlConnection.connect();


                InputStream streamWriter = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();

                if (streamWriter == null)

                    moviesJsonString = null;

                reader = new BufferedReader(new InputStreamReader(streamWriter));

                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0)
                    moviesJsonString = null;

                moviesJsonString = buffer.toString();
                getmoviesString(moviesJsonString);


            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {

                    urlConnection.disconnect();

                }

                if (reader != null) {

                    try {

                        reader.close();

                    } catch (final IOException e) {

                        Log.i("PlaceholderFragment", "Error closing stream", e);

                    }

                }

            }

            return images;
        }

        @Override
        protected void onPostExecute(ArrayList<String> imageUrl) {

            GridView gridView = (GridView) rootView.findViewById(R.id.gridView);

            gridView.setAdapter(new ImageAdapter(getActivity()));


        }

        private void getmoviesString(String moviesJsonString) {
        images = new ArrayList<String>();
            try {
                JSONObject rootMovie = new JSONObject(moviesJsonString);

                JSONArray moviesArray = rootMovie.getJSONArray("results");

                for (int i=0;i<moviesArray.length();i++) {

                    JSONObject arrayObj = moviesArray.getJSONObject(i);

                    String posterImage = arrayObj.getString("backdrop_path");

                    String imageJsonData = getImageJson();

                    JSONObject rootImage = new JSONObject(imageJsonData);

                    JSONObject imageObj = rootImage.optJSONObject("images");

                    String imageBaseUrl = imageObj.getString("secure_base_url");

                    JSONArray sizeArray = imageObj.optJSONArray("logo_sizes");

                    String imageSize = sizeArray.getString(5);

                    images.add(imageBaseUrl+imageSize+posterImage);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }




        }

        private String getImageJson() {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String imageJson = null ;
            try {

                URL url = new URL("https://api.themoviedb.org/3/configuration?api_key=c686b5d39204b19a48fb9a27f5457a41");

                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");

                connection.connect();



                InputStream streamWriter = connection.getInputStream();

                StringBuffer buffer = new StringBuffer();

                if (streamWriter == null)
                    imageJson = null;

                reader = new BufferedReader(new InputStreamReader(streamWriter));

                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0)
                    imageJson = null;

                imageJson = buffer.toString();


            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (connection != null) {

                    connection.disconnect();

                }

                if (reader != null) {

                    try {

                        reader.close();

                    } catch (final IOException e) {

                        Log.i("PlaceholderFragment", "Error closing stream", e);

                    }

                }

            }

            return imageJson;
        }

    }

    public class ImageAdapter extends BaseAdapter{

        Context mContext;



        public ImageAdapter(Context c) {
            mContext = c;

        }

        @Override
        public int getCount() {
            return images.size() ;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null){

                imageView = new ImageView(mContext);

                imageView.setLayoutParams(new GridView.LayoutParams(100,100));

                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                imageView.setPadding(8, 8, 8, 8);

            }
            else {
                imageView=(ImageView)convertView;
            }
            Glide.with(mContext).load(images.get(position)).into(imageView);

            return imageView;
        }
    }

    }

