package com.example.iyeharayeu.videoapp.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;


public class BaseFragment extends Fragment {

    OnFragmentInteraction mActivityListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityListener = (OnFragmentInteraction) getActivity();
    }

    @Override
    public void onDetach() {
        mActivityListener = null;
        super.onDetach();
    }
}
