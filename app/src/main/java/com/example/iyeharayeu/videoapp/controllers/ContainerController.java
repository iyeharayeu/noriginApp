package com.example.iyeharayeu.videoapp.controllers;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class ContainerController {

    private static final String TAG = ContainerController.class.getSimpleName();

    private final FragmentManager mManager;
    private final int mContainer;

    public ContainerController(FragmentManager manager, int container) {
        this.mManager = manager;
        this.mContainer = container;
    }


    public void addFragment(Fragment fragment, String TAG){
        removeFragment(TAG);
        mManager.beginTransaction()
                .add(mContainer,fragment,TAG)
                .commit();
    }
    public void removeFragment(String TAG){
        Fragment foundFragment = mManager.findFragmentByTag(TAG);
        if(foundFragment!=null){
            mManager.beginTransaction()
                    .remove(foundFragment)
                    .commit();
        }
    }

}
