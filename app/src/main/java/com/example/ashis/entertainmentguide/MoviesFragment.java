package com.example.ashis.entertainmentguide;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ashis.entertainmentguide.utility.Utility;

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


public class MoviesFragment extends Fragment {
    public static final String LOG_TAG = MoviesFragment.class.getSimpleName();

   // FetchMovieData mMovieData = new FetchMovieData();


    private RecyclerView.Adapter mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Integer> movieId;
    private View rootView;
    private List<Movie> movieList = new ArrayList<>();
    private static final String BASE_URL = "http://image.tmdb.org/t/p/w500";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(LOG_TAG,"inside on CreateView");
        rootView = inflater.inflate(R.layout.fragmentmain, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        movieList.clear();
        loadRecyclerViewData();

        return rootView;
    }

    private void loadRecyclerViewData() {

        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading Movies ...");
        pd.show();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortValue = pref.getString(getString(R.string.pref_sort_key), getString(R.string.pref_pop_value));
        Log.i(LOG_TAG,sortValue);


        String JSON_URL = Utility.buildUri(sortValue);
        Log.i(LOG_TAG,JSON_URL);

        StringRequest mStringReq = new StringRequest(StringRequest.Method.GET,
                JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();

                        JSONObject rootMovie = null;
                        try {
                            rootMovie = new JSONObject(response);
                            String mGenre;
                            JSONArray moviesArray = rootMovie.getJSONArray("results");

                            Log.i(LOG_TAG, String.valueOf(moviesArray.length()));
                            final int len = moviesArray.length();
                            for (int i=0 ; i < len ; i++){

                                JSONObject o = moviesArray.getJSONObject(i);

                                String posterPath = o.getString("poster_path");
                                JSONArray genreArray = o.getJSONArray("genre_ids");
                                if (genreArray.length() != 0) {
                                     mGenre = Utility.getMovieGenre(genreArray.getInt(0));
                                }else {
                                    mGenre = "Not Specified";
                                }
                                String mTitle = o.getString("original_title");
                                String mOverview = o.getString("overview");
                                String vote_avg = o.getString("vote_average");
                                String relDate = o.getString("release_date");
                                String movieId = o.getString("id");
                                Movie movie = new Movie( BASE_URL+posterPath,
                                        mTitle,
                                        mOverview,
                                        relDate,
                                        vote_avg,
                                        mGenre,
                                        movieId
                                );
                                movieList.add(movie);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
}
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();

                    }
                });

        RequestQueue rq = Volley.newRequestQueue(getActivity());
        rq.add(mStringReq);

        mAdapter = new MyRecyclerViewAdapter(movieList,getActivity());

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.sortBy:
                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.refresh:
                updateMovies();
                break;

        }
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.i(LOG_TAG,"inside on Create");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG,"inside onResume");
        }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG,"inside onStart");
    }


    private void updateMovies() {
        movieList.clear();
       // mMovieData.execute(sortValue);

    }


/*

    public class FetchMovieData extends AsyncTask<String, Void,String> {


        @Override
        protected String doInBackground(String... params) {




            HttpURLConnection urlConnection = null;

            BufferedReader reader = null;


            try {



                URL url = new URL(uribuilder.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
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
                movieList = getmoviesString(moviesJsonString);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
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
            return moviesJsonString;
        }

        @Override
        protected void onPostExecute(String s) {
            mRecyclerView.setAdapter(mRecyclerAdapter);

            mRecyclerAdapter.notifyDataSetChanged();
        }

        private List<Movie> getmoviesString(String moviesJsonString) {
            movieId = new ArrayList<>();
            try {
                JSONObject rootMovie = new JSONObject(moviesJsonString);

                JSONArray moviesArray = rootMovie.getJSONArray("results");
                if (moviesArray.length() == 0) {
                    Log.i("arrayLemgth", "0");
                } else {

                    for (int i = 0; i < moviesArray.length(); i++) {

                        JSONObject arrayObj = moviesArray.getJSONObject(i);

                        String posterImage = arrayObj.getString("poster_path");

                        String movieName = arrayObj.getString("original_title");

                        String imageUrl = BASE_URL + posterImage;

                        int idNum = arrayObj.getInt("id");

                        movieId.add(idNum);


                        if (posterImage.equals("null")) {
                            Movie movie = new Movie("http://logicinception.com/realestate/images/no-logo.png", movieName);
                            movieList.add(movie);
                        } else {
                            Movie movie = new Movie(imageUrl, movieName);
                            movieList.add(movie);
                        }


                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return movieList;
        }


    }
*/
}



