package com.example.ashis.entertainmentguide.utility;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ashis.entertainmentguide.BuildConfig;
import com.example.ashis.entertainmentguide.constants.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

public  class  Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();


    public static String getMovieGenre(int genreId){

        switch (genreId){

            case 28:
                return "Action";
            case 12:
                return "Adventure";
            case 16:
                return "Animation";
            case 35:
                return "Comedy";
            case 80:
                return "Crime";
            case 99:
                return "Documentary";
            case 18:
                return "Drama";
            case 10751:
                return "Family";
            case 14:
                return "Fantasy";
            case 36:
                return "History";
            case 27:
                return "Horror";
            case 10402:
                return "Music";
            case 9648:
                return "Mystery";
            case 10749:
                return "Romance";
            case 878:
                return "Science Fiction";
            case 10770:
                return "TV Movie";
            case 53:
                return "Thriller";
            case 10752:
                return "War";
            case 37:
                return "Western";

            default:
                return "Genre not Specified";
        }
    }

    public static String buildUri(String sortBy){



        Uri.Builder uribuilder = Uri.parse(Constants.BASE_URI).buildUpon()
                .appendQueryParameter(Constants.URI_API_KEY, BuildConfig.OPEN_MOVIE_GUIDE_API_KEY)
                .appendQueryParameter(Constants.LANGUAGE, Constants.LANGUAGE_VALUE)
                .appendQueryParameter(Constants.SORT_BY,sortBy)
                .appendQueryParameter(Constants.RATING, Constants.RATING_VALUE)
                .appendQueryParameter(Constants.RELEASE_DATE, Constants.RELEASE_DATE_VALUE)
                .appendQueryParameter(Constants.MOVIE_LANGUAGE, Constants.LANGUAGE_VALUE);

        Log.i("ashish",uribuilder.build().toString());

        return uribuilder.build().toString();

    }

    public static String getActionBarTitle(String prefValue){

        switch (prefValue){
            case "popularity.desc":
                return "Most Popular";
            case "original_title.asc":
                return "Alphabetical Order";
            case "revenue.desc":
                return "Earnings Order";
            case "vote_average.desc":
                return "Average Ratings";
            case "primary_release_date.desc":
                return "Release Date Order";

            default:
                return "No Order Selected";
        }
    }

    public static String buildTrailerUrl(String movieID){

        Uri.Builder builder = Uri.parse(Constants.TRAILER_BASE_URI).buildUpon()
                .appendPath(movieID)
                .appendPath(Constants.MOVIE_VIDEOS)
                .appendQueryParameter(Constants.URI_API_KEY,BuildConfig.OPEN_MOVIE_GUIDE_API_KEY)
                .appendQueryParameter(Constants.MOVIE_TRAILER_LANG,Constants.MOVIE_TRAILER_LANG_VALUE);

        Log.i(LOG_TAG,builder.build().toString());

        return builder.build().toString();
    }

    public  static String formatDate(String mDate){

        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-DD");
        try {
            Date date = dateFormat.parse(mDate);
            java.text.SimpleDateFormat newFormat = new java.text.SimpleDateFormat("EEE, MMM d, ''yy");
            Log.i(LOG_TAG,newFormat.format(date));
            return newFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return mDate;
        }
    }

    public static String buildReviewsUrl(String movieID){

        Uri.Builder builder = Uri.parse(Constants.TRAILER_BASE_URI).buildUpon()
                .appendPath(movieID)
                .appendPath(Constants.MOVIE_REVIEW)
                .appendQueryParameter(Constants.URI_API_KEY,BuildConfig.OPEN_MOVIE_GUIDE_API_KEY)
                .appendQueryParameter(Constants.MOVIE_TRAILER_LANG,Constants.MOVIE_TRAILER_LANG_VALUE);

        return builder.build().toString();
    }

}
