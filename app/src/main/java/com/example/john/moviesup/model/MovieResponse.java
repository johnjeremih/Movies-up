package com.example.john.moviesup.model;

import com.example.john.moviesup.model.Movie;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {


    @SerializedName("results")
    @Expose
    private List<Movie> movies = null;


    public List<Movie> getResults() {
        return movies;
    }

}
