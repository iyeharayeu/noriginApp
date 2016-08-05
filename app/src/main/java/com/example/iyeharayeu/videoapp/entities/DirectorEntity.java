package com.example.iyeharayeu.videoapp.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DirectorEntity implements Serializable {

    private static final String FIELD_NAME = "name";

    @SerializedName(FIELD_NAME)
    private String mName;

    public String getName() {
        return mName;
    }
}
