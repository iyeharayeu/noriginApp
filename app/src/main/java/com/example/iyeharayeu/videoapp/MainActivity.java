package com.example.iyeharayeu.videoapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.iyeharayeu.videoapp.utilities.Utils;
import com.example.iyeharayeu.videoapp.controllers.ContainerController;
import com.example.iyeharayeu.videoapp.entities.MovieEntity;
import com.example.iyeharayeu.videoapp.entities.MovieListEntity;
import com.example.iyeharayeu.videoapp.fragments.OnFragmentInteraction;
import com.example.iyeharayeu.videoapp.fragments.PickMovieFragment;
import com.example.iyeharayeu.videoapp.fragments.PreloaderFragment;
import com.example.iyeharayeu.videoapp.model.DataSource;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements OnFragmentInteraction {

    private static final String BUNDLE_LAST_VALUE = "bundle_last_value";
    private static final String BUNDLE_LAST_MOVIES_LIST = "last_movies_list";
    private static final String JSON_FILE_NAME = "movies.json";

    private boolean mIsHandlerFinished = false;
    private ContainerController mContainerController;
    private MovieEntity mMovie = null;
    private MovieListEntity mMovieListEntity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContainerController = new ContainerController(getSupportFragmentManager(), R.id.activity_main_container);
        if (savedInstanceState!=null) {
            mMovie = (MovieEntity) savedInstanceState.getSerializable(BUNDLE_LAST_VALUE);
            mMovieListEntity = (MovieListEntity) savedInstanceState.getSerializable(BUNDLE_LAST_MOVIES_LIST);
        }else{
            loadDataInBg();
        }
    }

    private void instantiateFragments() {

        mMovie = Utils.getMovieAccordingToIndex(mMovieListEntity, (mMovie!=null)?mMovie.getId():BuildConfig.MOVIE_ID);
            if(mMovie!=null) {
                mContainerController.addFragment(PickMovieFragment.newInstance(mMovieListEntity, mMovie.getId()), PickMovieFragment.TAG);
            }

        mContainerController.addFragment(PreloaderFragment.newInstance(mMovie), PreloaderFragment.TAG);

    }

    private Subscriber<MovieListEntity> mSubscriber = new Subscriber<MovieListEntity>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            instantiateFragments();
            Toast.makeText(MainActivity.this, R.string.error_string, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(MovieListEntity movieListEntity) {
            mMovieListEntity = movieListEntity;
            instantiateFragments();
        }
    };


    private void loadDataInBg() {
        if(mMovieListEntity!=null){
            return;
        }

        DataSource dataSource = new DataSource(this);
        Observable<MovieListEntity> observable = dataSource.movies(JSON_FILE_NAME);
        observable.subscribe(mSubscriber);
        observable.subscribeOn(Schedulers.computation())
                  .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    protected void onPause() {

        if(mSubscriber!=null) {
            mSubscriber.unsubscribe();
        }

        super.onPause();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(BUNDLE_LAST_VALUE, mMovie);
        outState.putSerializable(BUNDLE_LAST_MOVIES_LIST, mMovieListEntity);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onReadyToPlay() {

        if (mIsHandlerFinished) {
            mContainerController.removeFragment(PreloaderFragment.TAG);
        }
        mIsHandlerFinished = true;
    }

    @Override
    public void onAllowToPlay() {

        if(mIsHandlerFinished){
            mContainerController.removeFragment(PreloaderFragment.TAG);
        }
        mIsHandlerFinished = true;
    }

    @Override
    public void onStartSelectedVideo(MovieEntity manuallySelectedMovie) {
        mIsHandlerFinished = false;
        mMovie = manuallySelectedMovie;
        instantiateFragments();
    }

    @Override
    public void onBackPressed() {
    }
}


