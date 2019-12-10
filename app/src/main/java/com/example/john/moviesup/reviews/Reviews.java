package com.example.john.moviesup.reviews;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Reviews {

    private Reviews() {
    }


    @SerializedName("results")
    private List<Review> items = null;


    @SerializedName("total_results")
    private int reviewCount;

    @SerializedName("id")
    private int movieId;

    public int getReviewCount() {
        return reviewCount;
    }

    public List<Review> getItems() {
        return items;
    }

    public int getMovieId() {
        return movieId;
    }


}
