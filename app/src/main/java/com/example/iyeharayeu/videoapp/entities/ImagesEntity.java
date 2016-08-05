package com.example.iyeharayeu.videoapp.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ImagesEntity implements Serializable{

    private static final String FIELD_COVER = "cover";
    private static final String FIELD_PLACEHOLDER = "placeholder";

    @SerializedName(FIELD_COVER)
    private String mCover;

    @SerializedName(FIELD_PLACEHOLDER)
    private String mPlaceholder;

    public String getCover() {
        return mCover;
    }


    public String getPlaceholder() {
        return mPlaceholder;
    }

}
