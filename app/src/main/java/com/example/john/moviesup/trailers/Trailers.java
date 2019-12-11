package com.example.john.moviesup.trailers;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Trailers {

    private Trailers() {}


    @SerializedName("results")
    public List<Trailer> items = null;

    @SerializedName("id")
    public int movieId;

    public int getMovieId() {
        return movieId;
    }

    public List<Trailer> getItems() {
        return items;
    }
}
