package com.example.ashis.entertainmentguide;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.ashis.entertainmentguide.data.MovieContract;
import com.example.ashis.entertainmentguide.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailsActivity extends AppCompatActivity {

    public static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    private final String TRAILER_BASE_URL = "https://api.themoviedb.org/3/movie";

    private ScrollView scroll;

    private int id;

    private Movie movie;


    private String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";

    private String productionHouse,genre,movieTitle,imageURL,rating,releaseDate,movieOverview;

    private ImageView detailsImage ;
    private Button trailerBtn;
    private TextView textMovieTitle,
            textMovieRating,
            textMovieGenre,
            textReviews,
            textMovieSynopsis,
            textMovieReleaseDate;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.context_menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.fav:

                addToDatabase();

                return true;

            default: return false;
        }
    }



    private void addToDatabase() {

        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_NAME,movieTitle);
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_GENRE,genre);
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATING,rating);
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,releaseDate);
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS,movieOverview);
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,imageURL);
        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,values);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        movie = (Movie) getIntent().getSerializableExtra(getString(R.string.intentData));


        detailsImage = (ImageView) findViewById(R.id.detailsImage);
        textMovieTitle = (TextView) findViewById(R.id.textViewMovieTitle);
        textMovieRating = (TextView) findViewById(R.id.textViewRating);
        textReviews = (TextView) findViewById(R.id.moviesReviews);
        textMovieGenre = (TextView) findViewById(R.id.textViewGenre);
        textMovieSynopsis = (TextView) findViewById(R.id.textViewSynopsis);
        textMovieReleaseDate = (TextView) findViewById(R.id.textViewReleaseDate);
        trailerBtn = (Button) findViewById(R.id.button_trailer);
        // progressBar.getIndeterminateDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);


        populateData();
    }

    private void populateData() {

        textMovieTitle.setText(movie.getmTitle());
        textMovieGenre.setText(movie.getmGenre());
        textMovieRating.setText(movie.getmAvgRating());
        textMovieSynopsis.setText(movie.getmOverview());
        textMovieReleaseDate.setText(Utility.formatDate(movie.getmReleaseDate()));

        Log.i(LOG_TAG,IMAGE_BASE_URL+movie.getmPictureUrl());

        Glide.with(getApplicationContext())
                .load(movie.getmPictureUrl())
                .error(R.drawable.no_pic_logo)
                .into(detailsImage);

        trailerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringRequest sr = new StringRequest(StringRequest.Method.GET,
                        Utility.buildTrailerUrl(movie.getmMovieId()),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                JSONObject root = null;
                                try {
                                    root = new JSONObject(response);
                                    JSONArray resultsArray = root.getJSONArray("results");

                                    JSONObject keyObject  = resultsArray.getJSONObject(0);

                                    String key = keyObject.getString("key");

                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("http://www.youtube.com/watch?v="+key)));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });

                RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
                rq.add(sr);

            }
        });

        textReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest sr = new StringRequest(StringRequest.Method.GET,
                        Utility.buildReviewsUrl(movie.getmMovieId()),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                JSONObject root = null;
                                try {
                                    root = new JSONObject(response);
                                    JSONArray resultsArray = root.getJSONArray("results");
                                    if (resultsArray.length() == 0){
                                        Toast.makeText(getApplicationContext(),"No Reviews Found",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    JSONObject keyObject  = resultsArray.getJSONObject(0);

                                    String url = keyObject.getString("url");

                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(url)));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();

                            }
                        });

                RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
                rq.add(sr);

            }
        });
    }


}
