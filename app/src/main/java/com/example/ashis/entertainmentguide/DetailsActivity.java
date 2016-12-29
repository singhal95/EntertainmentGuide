package com.example.ashis.entertainmentguide;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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

public class DetailsActivity extends AppCompatActivity {
private int id;

   private HttpURLConnection connection = null;

    private BufferedReader reader = null;

    private String detailsJson = null;

    private String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";

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

            String productionHouse = null,genre=null;


            JSONObject root = new JSONObject(detailsJson);

            String movieTitle = root.getString("original_title");

            String movieOverview = root.getString("overview");

            String imagePoster = root.getString("poster_path");

            String imageURL = IMAGE_BASE_URL+imagePoster;

            String releaseDate = root.getString("release_date");

            String rating = root.getString("vote_average");

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
