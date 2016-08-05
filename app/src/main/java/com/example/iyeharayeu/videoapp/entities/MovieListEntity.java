package com.example.iyeharayeu.videoapp.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MovieListEntity implements Serializable{

    private static final String FIELD_MOVIES = "movies";

    @SerializedName(FIELD_MOVIES)
    private MovieEntity[] mMovies;

    public MovieEntity[] getMovies() {
        return mMovies;
    }

    public void setMovies(MovieEntity[] movies) {
        this.mMovies = movies;
    }

}
