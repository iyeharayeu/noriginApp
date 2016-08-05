package com.example.iyeharayeu.videoapp.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MetaEntity implements Serializable{


    private static final String FIELD_RELEASE_YEAR = "releaseYear";
    private static final String FIELD_DIRECTORS = "directors";
    private static final String FIELD_ACTORS = "actors";

    @SerializedName(FIELD_RELEASE_YEAR)
    private String mReleaseYear;

    @SerializedName(FIELD_DIRECTORS)
    private DirectorEntity[] mDirectors;

    @SerializedName(FIELD_ACTORS)
    private ActorEntity[] mActors;

    public String getReleaseYear() {
        return mReleaseYear;
    }

    public DirectorEntity[] getDirectors() {
        return mDirectors;
    }

    public ActorEntity[] getActors() {
        return mActors;
    }


}
