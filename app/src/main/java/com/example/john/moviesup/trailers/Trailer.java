package com.example.john.moviesup.trailers;

import com.google.gson.annotations.SerializedName;

public class Trailer {

    private static final String TRAILER_BASE_URL = "https://www.youtube.com/watch?v=";

    @SerializedName("id")
    private String mId;

    @SerializedName("key")
    private String mKey;

    @SerializedName("name")
    private String mName;


    @SerializedName("type")
    private String mType;

    public Trailer(String id, String key, String name, String type){
        mId = id;
        mKey = key;
        mName = name;

        mType = type;
    }

    public String getId(){
        return mId;
    }

    public void setId(String newId){
        mId = newId;
    }

    public String getKey(){
        return mKey;
    }

    public void setKey(String newKey){
        mId = newKey;
    }

    public String getName(){
        return mName;
    }

    public void setName(String newName){
        mName = newName;
    }

    String getType() {
        return mType;
    }

    public void setType(String newType) {
        mType = newType;
    }

    public String getFullPath(){
        return TRAILER_BASE_URL.concat(mKey);
    }

}
