package com.example.iyeharayeu.videoapp.fragments;

import com.example.iyeharayeu.videoapp.entities.MovieEntity;

public interface OnFragmentInteraction {

    void onReadyToPlay();

    void onAllowToPlay();

    void onStartSelectedVideo(MovieEntity tag);


}
