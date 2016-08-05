package com.example.iyeharayeu.videoapp.model;

import com.example.iyeharayeu.videoapp.entities.MovieListEntity;

import rx.Observable;

interface MoviesData {
        /**
         * Get an {@link rx.Observable} which will emit a {@link com.example.iyeharayeu.videoapp.entities.MovieListEntity}.
         *
         * @param jsonName The name used to parse data
         */
        Observable<MovieListEntity> movies(String jsonName);
}
