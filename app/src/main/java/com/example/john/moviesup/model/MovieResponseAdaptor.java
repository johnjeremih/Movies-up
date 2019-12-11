package com.example.john.moviesup.model;

import android.content.Context;

import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.john.moviesup.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MovieResponseAdaptor extends RecyclerView.Adapter<MovieResponseAdaptor.MovieAdapterViewHolder> {

    private static final String LOG_TAG = MovieResponseAdaptor.class.getSimpleName();
    private static final String PICASSO_ERROR = "Picasso loading error";
    private ArrayList<Movie> mMovies;
    private Context mContext;
    private MovieAdapterClickHandler mItemListener;
    private String VIDEO_PATH = "https://image.tmdb.org/t/p/w500";

    public interface MovieAdapterClickHandler {
        void onClickHandler(Movie movie);
    }


    public MovieResponseAdaptor(Context context, MovieAdapterClickHandler handler) {
        mContext = context;
        mItemListener = handler;

    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.title_view)
        TextView movieTitleView;
        @BindView(R.id.poster_view)
        ImageView moviePosterView;
        @BindView(R.id.rating_view)
        TextView ratingView;
        GradientDrawable ratingCircle;


        public MovieAdapterViewHolder(View view) {
            super(view);

            // Butterknife binding
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
            ratingCircle = (GradientDrawable) ratingView.getBackground();





        }

        @Override
        public void onClick(View v) {
            Movie movie = mMovies.get(getAdapterPosition());
            mItemListener.onClickHandler(movie);

        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.movie_grid_item, parent, false);
        return new MovieAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final MovieAdapterViewHolder holder, int position) {

        Movie currentMovie = mMovies.get(position);
        final String actualTitle = currentMovie.getTitle();
        final String actualImagePoster = currentMovie.getPosterPath();
        final String actualRating = currentMovie.getVoteAverage();
        final int ratingColor = getRatingColor(currentMovie.getVoteAverage());

        Picasso.get()
                .load(VIDEO_PATH + actualImagePoster)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(holder.moviePosterView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.movieTitleView.setText(actualTitle);
                        holder.moviePosterView.setContentDescription(VIDEO_PATH + actualImagePoster);
                        holder.ratingView.setText(actualRating);
                        holder.ratingCircle.setColor(ratingColor);
                    }

                    @Override
                    public void onError(Exception e) {
                        Timber.e(PICASSO_ERROR);
                        holder.moviePosterView.setImageResource(R.drawable.ic_placeholder);
                        holder.movieTitleView.setText(actualTitle);
                        holder.moviePosterView.setContentDescription(actualTitle);

                    }
                });
    }

    @Override
    public int getItemCount() {

        if (mMovies == null) {
            return 0;
        }

        return mMovies.size();
    }

    public void setMovieData(List<Movie> movies) {

        // Refresh the movie list and notify the loader
        mMovies = (ArrayList<Movie>) movies;
        notifyDataSetChanged();
    }


    private int getRatingColor(String rating) {

        int ratingColorResourceId;
        int ratingColorFloor = (int) Math.floor(Double.parseDouble(rating));

        switch (ratingColorFloor) {

            case 0:
            case 1:
                ratingColorResourceId = R.color.start1;
                break;

            case 2:
                ratingColorResourceId = R.color.start2;
                break;

            case 3:
                ratingColorResourceId = R.color.start3;
                break;

            case 4:
                ratingColorResourceId = R.color.start4;
                break;

            case 5:
                ratingColorResourceId = R.color.start5;
                break;

            case 6:
                ratingColorResourceId = R.color.start6;
                break;

            case 7:
                ratingColorResourceId = R.color.start7;
                break;

            case 8:
                ratingColorResourceId = R.color.start8;
                break;

            case 9:
                ratingColorResourceId = R.color.start9;
                break;

            case 10:
                ratingColorResourceId = R.color.start10;
                break;

            default:
                ratingColorResourceId = R.color.start1;
                break;

        }

        return ContextCompat.getColor(mContext, ratingColorResourceId);

    }


}
