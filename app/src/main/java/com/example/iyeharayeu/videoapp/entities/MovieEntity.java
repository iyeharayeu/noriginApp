package com.example.iyeharayeu.videoapp.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MovieEntity implements Serializable {


    private static final String FIELD_ID = "id";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_META = "meta";
    private static final String FIELD_IMAGES = "images";
    private static final String FIELD_STREAMS = "streams";
    
    @SerializedName(FIELD_ID)
    private String mId;
    
    @SerializedName(FIELD_TITLE)
    private String mTitle;
    
    @SerializedName(FIELD_DESCRIPTION)
    private String mDescription;
    
    @SerializedName(FIELD_META)
    private MetaEntity mMeta;
    
    @SerializedName(FIELD_IMAGES)
    private ImagesEntity mImages;
    
    @SerializedName(FIELD_STREAMS)
    private StreamsEntity mStreams;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getTitle() {
        return mTitle;
    }


    public String getDescription() {
        return mDescription;
    }


    public MetaEntity getMeta() {
        return mMeta;
    }

    public ImagesEntity getImages() {
        return mImages;
    }

    public StreamsEntity getStreams() {
        return mStreams;
    }

}
