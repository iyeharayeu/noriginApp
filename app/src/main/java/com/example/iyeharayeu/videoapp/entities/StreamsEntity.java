package com.example.iyeharayeu.videoapp.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StreamsEntity implements Serializable {


    public static final String FIELD_TYPE = "type";
    public static final String FIELD_URL = "url";

    @SerializedName(FIELD_TYPE)
    private String mType;

    @SerializedName(FIELD_URL)
    private String mUrl;


    public String getType() {
        return mType;
    }

    public String getUrl() {
        return mUrl;
    }

}
