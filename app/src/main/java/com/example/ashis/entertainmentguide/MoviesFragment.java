package com.example.ashis.entertainmentguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashis on 12/22/2016.
 */

public class MoviesFragment extends Fragment {
   private ArrayList<String> imagesArray = null;
    private ArrayList<Integer> movieId;
   private View rootView;
   String sortValue;
    String BASE_URL = "http://image.tmdb.org/t/p/w500";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main,container,false);

        FetchMovieData getMovieData = new FetchMovieData();


        getMovieData.execute();
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.sortBy: Intent i = new Intent(getActivity(),SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.refresh: updateMovies();
                break;

        }
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main,menu);
    }



    @Override
    public void onStart() {
        super.onStart();
       updateMovies();
    }



    private void updateMovies() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortValue = pref.getString(getString(R.string.pref_sort_key),getString(R.string.pref_pop_value));
        Log.i("sortValue",sortValue);
        FetchMovieData movieData = new FetchMovieData();
        imagesArray =null;
        movieData.execute(sortValue);

    }

    public class FetchMovieData extends AsyncTask<String,Void,ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            String moviesJsonString = null;

            String BASE_URI = "https://api.themoviedb.org/3/discover/movie?";

            String LANGUAGE = "language";

            String LANGUAGE_VALUE = "en";

            String SORT_BY = "sort_by";

            String URI_API_KEY = "api_key";


            HttpURLConnection urlConnection = null;

            BufferedReader reader = null;



            try {

                Uri.Builder uribuilder = Uri.parse(BASE_URI).buildUpon().appendQueryParameter(URI_API_KEY,BuildConfig.OPEN_MOVIE_GUIDE_API_KEY).appendQueryParameter(LANGUAGE,LANGUAGE_VALUE).appendQueryParameter(SORT_BY,params[0]);


                URL url = new URL(uribuilder.toString());

                Log.i("url", String.valueOf(url));

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

            return imagesArray;
        }

        @Override
        protected void onPostExecute(ArrayList<String> imageUrl) {

            ProgressBar progress = (ProgressBar)rootView.findViewById(R.id.progress);


            GridView gridView = (GridView) rootView.findViewById(R.id.gridview);

            if (imagesArray !=null) {

                progress.setVisibility(View.INVISIBLE);

                gridView.setAdapter(new ImageAdapter(getActivity()));

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity(),DetailsActivity.class);
                        intent.putExtra("idPosition", movieId.get(position));
                        startActivity(intent);

                    }
                });

            }

        }

        private void getmoviesString(String moviesJsonString) {
        imagesArray = new ArrayList<String>();
            movieId = new ArrayList<>();
            try {
                JSONObject rootMovie = new JSONObject(moviesJsonString);

                JSONArray moviesArray = rootMovie.getJSONArray("results");

                if (moviesArray.length() == 0){
                    Log.i("arrayLemgth","0");
                }
                else {

                    for (int i = 0; i < moviesArray.length(); i++) {

                        JSONObject arrayObj = moviesArray.getJSONObject(i);

                        String posterImage = arrayObj.getString("poster_path");

                        String movieName = arrayObj.getString("original_title");

                        String imageUrl = BASE_URL+posterImage;

                        int idNum = arrayObj.getInt("id");

                        movieId.add(idNum);

                        if (posterImage.equals("null"))
                        {
                            imagesArray.add("http://logicinception.com/realestate/images/no-logo.png");
                        }
                        else {
                            imagesArray.add(imageUrl);
                        }

                    }

                    Log.i("movieId",movieId.toString());
                    Log.i("imageURl",imagesArray.toString());


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }




        }


    }

    public class ImageAdapter extends BaseAdapter{

        Context mContext;



        public ImageAdapter(Context c) {
            mContext = c;

        }

        @Override
        public int getCount() {
            return imagesArray.size() ;
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


            RecordHolder holder = new RecordHolder();

            View gridview = convertView;
            if (convertView == null){

              LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                gridview = new View(mContext);
                gridview=inflater.inflate(R.layout.grid_view_layout,parent,false);
                holder.imageView =(ImageView)gridview.findViewById(R.id.imageView);

                gridview.setTag(holder);
            }
            else {holder=(RecordHolder)gridview.getTag();


            }
            if (!imagesArray.get(position).isEmpty()){

                Glide.with(mContext).load(imagesArray.get(position)).into(holder.imageView);

            }



            return gridview;
        }
    }
    static class RecordHolder{
        ImageView imageView;

    }

    }

