package com.example.iyeharayeu.videoapp.model;

import android.content.Context;

import com.example.iyeharayeu.videoapp.utilities.Utils;
import com.example.iyeharayeu.videoapp.entities.MovieListEntity;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;

public class DataSource implements MoviesData {


    private final Context mContext;

    public DataSource(Context context) {
        mContext = context;
    }

    @Override
    public Observable<MovieListEntity> movies(final String jsonName) {
        return Observable.create(new Observable.OnSubscribe<MovieListEntity>() {
            @Override
            public void call(Subscriber<? super MovieListEntity> subscriber) {
                try {
                    subscriber.onNext(Utils.convertToSerializableForBundle(Utils.convertSourceToEntity(mContext, jsonName)));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

}
