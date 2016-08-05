package com.example.iyeharayeu.videoapp.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.iyeharayeu.videoapp.R;
import com.example.iyeharayeu.videoapp.utilities.Utils;
import com.example.iyeharayeu.videoapp.entities.MovieEntity;

public class PreloaderFragment extends BaseFragment {

    public static final String TAG = PreloaderFragment.class.getSimpleName();

    public static final String BUNDLE_DESCRIPTION = "BUNDLE_DESCRIPTION";
    public static final int HANDLER_DELAY_MILLIS = 1000;

    private MovieEntity mMovieInfo;
    private Handler mHandler = new Handler();

    private Holder mHolder = null;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mActivityListener.onAllowToPlay();
        }
    };


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
        mHandler.postDelayed(mRunnable, HANDLER_DELAY_MILLIS);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
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

        if(mMovieInfo!=null) {
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
        Bitmap mBmp = Utils.getBitmapFromAssets((Context) mActivityListener, mMovieInfo.getImages().getPlaceholder());
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

    private static class Holder{

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
