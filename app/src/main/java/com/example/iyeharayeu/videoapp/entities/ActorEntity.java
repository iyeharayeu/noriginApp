package com.example.iyeharayeu.videoapp.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ActorEntity implements Serializable{

    private final static String FIELD_NAME = "name";

    @SerializedName(FIELD_NAME)
    private String mName;

    public String getName() {
        return mName;
    }
}
