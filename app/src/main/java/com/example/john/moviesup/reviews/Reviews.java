package com.example.john.moviesup.reviews;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Reviews {

    private Reviews() {
    }


    @SerializedName("results")
    public List<Review> items = null;


    @SerializedName("total_results")
    public int reviewCount;

    @SerializedName("id")
    public int movieId;

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
