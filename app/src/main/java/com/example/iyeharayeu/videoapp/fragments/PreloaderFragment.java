package com.example.iyeharayeu.videoapp.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.iyeharayeu.videoapp.R;
import com.example.iyeharayeu.videoapp.utilities.Utils;
import com.example.iyeharayeu.videoapp.entities.MovieEntity;

import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class PreloaderFragment extends BaseFragment {

    public static final String TAG = PreloaderFragment.class.getSimpleName();

    public static final String BUNDLE_DESCRIPTION = "BUNDLE_DESCRIPTION";
    public static final int DELAY_TIME = 1;

    private MovieEntity mMovieInfo;

    private Holder mHolder = null;

    private Subscription mSubscription;


    public static PreloaderFragment newInstance(MovieEntity movie) {
        PreloaderFragment fragment = new PreloaderFragment();
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_DESCRIPTION, movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if (getArguments() != null) {
            mMovieInfo = (MovieEntity) getArguments().getSerializable(BUNDLE_DESCRIPTION);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        mSubscription = rx.Observable.just("")
                .delaySubscription(DELAY_TIME, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mActivityListener.onAllowToPlay();
                    }
                });

    }

    @Override
    public void onPause() {
        mSubscription.unsubscribe();

        super.onPause();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_preloader, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHolder = new Holder(view);

        if (mMovieInfo != null) {
            setDataOnUi();
        }

        if (savedInstanceState != null) {
            mMovieInfo = (MovieEntity) savedInstanceState.getSerializable(BUNDLE_DESCRIPTION);
            setDataOnUi();
        }
    }


    private void setDataOnUi() {
        mHolder.tvTitle.setText(mMovieInfo.getTitle());
        mHolder.tvSubTitle.setText(mMovieInfo.getDescription());
        mHolder.tvYear.setText(mMovieInfo.getMeta().getReleaseYear());
        Bitmap mBmp = Utils.getBitmapFromAssets(getActivity(), mMovieInfo.getImages()
                           .getPlaceholder());
        mHolder.ivPlaceHolder.setImageBitmap(mBmp);
    }

    @Override
    public void onDestroyView() {
        mHolder = null;
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BUNDLE_DESCRIPTION, mMovieInfo);

    }

    private static class Holder {

        TextView tvTitle = null;
        TextView tvSubTitle = null;
        TextView tvYear = null;
        ImageView ivPlaceHolder = null;

        public Holder(View view) {
            tvTitle = (TextView) view.findViewById(R.id.preloader_title);
            tvSubTitle = (TextView) view.findViewById(R.id.preloader_subtitle);
            tvYear = (TextView) view.findViewById(R.id.preloader_year);
            ivPlaceHolder = (ImageView) view.findViewById(R.id.preloader_placeholder);
        }
    }

}
