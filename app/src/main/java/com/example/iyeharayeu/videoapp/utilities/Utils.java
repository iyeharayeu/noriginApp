package com.example.iyeharayeu.videoapp.utilities;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.iyeharayeu.videoapp.entities.MovieEntity;
import com.example.iyeharayeu.videoapp.entities.MovieListEntity;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {

    public static Bitmap getBitmapFromAssets(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();

        InputStream istr = null;
        try {
            istr = assetManager.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeStream(istr);
    }

    public static MovieEntity getMovieAccordingToIndex(MovieListEntity movieListEntity, String movieId) {
        for (MovieEntity curr : movieListEntity.getMovies()) {
            if (curr.getId().equals(movieId)) {
                return curr;

            }
        }
        return null;
    }

    public static MovieListEntity convertToSerializableForBundle(MovieEntity[] mMovieEntities) {
        MovieListEntity movieListEntity = new MovieListEntity();
        movieListEntity.setMovies(mMovieEntities);
        return movieListEntity;
    }

    public static MovieEntity[] convertSourceToEntity(Context context, String jsonLink) throws IOException {
            InputStreamReader is = new InputStreamReader(context.getAssets().open(jsonLink));
            Gson gson = new Gson();
            return gson.fromJson(is, MovieEntity[].class);
    }
}