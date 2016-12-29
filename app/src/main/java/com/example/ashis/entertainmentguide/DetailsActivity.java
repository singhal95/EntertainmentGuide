package com.example.ashis.entertainmentguide;

import android.content.Intent;
import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
private int id;

   private HttpURLConnection connection = null;

    private BufferedReader reader = null;

    private String detailsJson = null;

    private String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";

    private String productionHouse,genre,movieTitle,imageURL,rating,releaseDate,movieOverview;

    private ImageView detailsImage ;

    private TextView textMovieTitle,textMovieRating,textMovieGenre,textMovieProduction,textMovieSynopsis,textMovieReleaseDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        try {


            id = getIntent().getExtras().getInt("idPosition");
            Log.i("movieId",String.valueOf(id));

            }catch (Exception e){
            e.printStackTrace();
        }

        FetchMovieDetails fetchMovieDetails = new FetchMovieDetails();
        fetchMovieDetails.execute(id);

         detailsImage = (ImageView) findViewById(R.id.detailsImage);

        textMovieTitle = (TextView) findViewById(R.id.textViewMovieTitle);

        textMovieRating = (TextView) findViewById(R.id.textViewRating);

        textMovieGenre = (TextView) findViewById(R.id.textViewGenre);

        textMovieProduction = (TextView) findViewById(R.id.textViewProduction);

        textMovieSynopsis = (TextView) findViewById(R.id.textViewSynopsis);

        textMovieReleaseDate = (TextView) findViewById(R.id.textViewReleaseDate);

    }

    public  class FetchMovieDetails extends AsyncTask<Integer,Void,Void>{

        private String BASE_URL_MOVIE_DETAILS = "https://api.themoviedb.org/3/movie/";
        private String API_KEY = "api_key";

        @Override
        protected Void doInBackground(Integer... params) {


            try {
                Uri.Builder uri = Uri.parse(BASE_URL_MOVIE_DETAILS).buildUpon().appendPath(String.valueOf(params[0])).appendQueryParameter(API_KEY,BuildConfig.OPEN_MOVIE_GUIDE_API_KEY);

                URL url = new URL(uri.toString());
                Log.i("movieDetailsUrl",url.toString());

                getConnection(url);

                detailsJson = getJsonData();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            setTitle(movieTitle);

            Glide.with(getApplicationContext()).load(imageURL).into(detailsImage);

            textMovieTitle.setText(movieTitle);

            textMovieRating.setText("("+rating+")");

            textMovieGenre.setText(genre);

            textMovieProduction.setText(productionHouse);

            textMovieSynopsis.setText(movieOverview);

            textMovieReleaseDate.setText(releaseDate);
        }

        private String getJsonData() throws IOException, JSONException {

            InputStream inputStream = connection.getInputStream();

            StringBuffer buffer = new StringBuffer();

            if (inputStream == null)
                detailsJson = null;

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0)
                detailsJson = null;

            detailsJson=buffer.toString();
            getJsonElements(detailsJson);

        return detailsJson;
        }



        private void getJsonElements(String detailsJson) throws JSONException {




            JSONObject root = new JSONObject(detailsJson);

             movieTitle = root.getString("original_title");

             movieOverview = root.getString("overview");

            String imagePoster = root.getString("poster_path");

             imageURL = IMAGE_BASE_URL+imagePoster;

             releaseDate = root.getString("release_date");

             rating = root.getString("vote_average");

            JSONArray productionArray = root.getJSONArray("production_companies");

            for (int i=0;i<productionArray.length();i++){
                JSONObject obj = productionArray.getJSONObject(i);
                productionHouse = obj.getString("name");
            }

           JSONArray genreArray = root.getJSONArray("genres");
            for (int i=0;i<genreArray.length();i++){

                JSONObject obj = genreArray.getJSONObject(i);
                genre=obj.getString("name");
            }

            Log.i("movieDetails",movieTitle+"\n"+genre+"\n"+productionHouse+"\n"+imageURL+"\n"+movieOverview+"\n"+releaseDate+"\n"+rating);



        }

        private void getConnection(URL url) throws IOException {


            connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod("GET");

            connection.connect();
        }
    }
}
