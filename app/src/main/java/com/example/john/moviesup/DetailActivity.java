package com.example.john.moviesup;

import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.john.moviesup.api.Client;
import com.example.john.moviesup.favorites.Favorites;
import com.example.john.moviesup.favorites.Favorites.FavoritesEntry;
import com.example.john.moviesup.model.Movie;
import com.example.john.moviesup.reviews.Review;
import com.example.john.moviesup.reviews.Reviews;
import com.example.john.moviesup.reviews.ReviewsAdapter;
import com.example.john.moviesup.trailers.Trailer;
import com.example.john.moviesup.trailers.Trailers;
import com.example.john.moviesup.trailers.TrailersAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailActivity extends AppCompatActivity implements TrailersAdapter.TrailerAdapterClickHandler {


    // Butterknife inits
    @BindView(R.id.title_tv)
    TextView titleView;
    @BindView(R.id.poster_path)
    ImageView posterView;
    @BindView(R.id.background)
    ImageView backgroundView;
    @BindView(R.id.back_drop_path)
    ImageView backDropView;
    @BindView(R.id.overview_tv)
    TextView overviewView;
    @BindView(R.id.rating_text_view)
    TextView ratingView;
    @BindView(R.id.release_date_tv)
    TextView releasesView;
    @BindView(R.id.reviews_tv)
    RecyclerView reviewsRecyclerView;
    @BindView(R.id.reviews_empty_tv)
    TextView reviewEmptyView;
    @BindView(R.id.trailers_tv)
    RecyclerView trailersRecyclerView;
    @BindView(R.id.trailers_empty_tv)
    TextView trailerEmptyView;
    @BindView(R.id.favorite_button)
    RelativeLayout favoriteButtonView;
    @BindView(R.id.favorite_icon)
    ImageView mFavoriteIcon;

    private int mId;
    private String voteAverage;
    private String mTitle;
    private String posterPath;
    private String backGround;
    private String backdropPath;
    private String overview;
    private String releaseDate;

    private ReviewsAdapter reviewAdapter;
    private TrailersAdapter trailersAdapter;
    private boolean isFavorite = false;


    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private Context context;
    private Intent intent;
    public static String EXTRA_PHOTO = "extra_photo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);
        Movie movie;

        Bundle extras = getIntent().getExtras();



        if (extras != null) {

            if (extras.containsKey(MovieActivity.DETAIL_MOVIE_KEY)) {
                movie = extras.getParcelable(MovieActivity.DETAIL_MOVIE_KEY);
                mId = movie.getId();
                mTitle = movie.getTitle();
                posterPath = movie.getPosterPath();



                backdropPath = movie.getBackdropPath();
                backGround = movie.getBackdropPath();
                overview = movie.getOverview();
                voteAverage = movie.getVoteAverage();
                releaseDate = movie.getReleaseDate();

            }
        }
        setFavorites();


        String[] parts = releaseDate.split("-");
        String part1 = parts[1];
        String part2 = parts[2];
        String part3 = parts[0];
        String release = part1 + "/" + part2 + "/" + part3;

        releasesView.setText(release);
        titleView.setText(mTitle);
        this.setTitle(mTitle);

        overviewView.setText(overview);

        Picasso.get()
                .load("https://image.tmdb.org/t/p/w500" + posterPath)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(posterView);

        Picasso.get()
                .load("https://image.tmdb.org/t/p/w500" + backdropPath)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(backDropView);

        Picasso.get()
                .load("https://image.tmdb.org/t/p/w500" + backGround)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(backgroundView);

        ratingView.setText(voteAverage);


        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        reviewsRecyclerView.setLayoutManager(reviewLayoutManager);
        reviewAdapter = new ReviewsAdapter(this);
        reviewsRecyclerView.setAdapter(reviewAdapter);

        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        trailersRecyclerView.setLayoutManager(trailerLayoutManager);
        trailersAdapter = new TrailersAdapter(this, this);
        trailersRecyclerView.setAdapter(trailersAdapter);


        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();


        if (isConnected) {

            loadReviews();
            loadTrailers();

        } else {
            hideReview();
            hideTrailer();
        }


        favoriteButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isFavorite) {

                    deleteMovie();

                } else {

                    insertMovie();
                }

            }
        });
    }


    private void setFavorites() {

        Uri queryUri = FavoritesEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mId)).build();
        Cursor cursor = getContentResolver().query(
                queryUri,
                null,
                null,
                null,
                null);

        if (cursor == null) {
            return;
        }


        if (cursor.moveToFirst()) {
            mFavoriteIcon.setImageResource(R.drawable.ic_favorite);
            isFavorite = true;
            cursor.close();
        }


    }

    @Override
    public void onClickHandler(Trailer trailer) {
        Uri url = Uri.parse(trailer.getFullPath());

        Intent launchTrailerIntent = new Intent(Intent.ACTION_VIEW, url);

        if (launchTrailerIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(launchTrailerIntent);
        }
    }

    private void insertMovie() {


        ContentValues values = new ContentValues();
        values.put(FavoritesEntry.COLUMN_MOVIE_ID, mId);
        values.put(FavoritesEntry.COLUMN_TITLE, mTitle);
        values.put(FavoritesEntry.COLUMN_POSTER_PATH, posterPath);
        values.put(FavoritesEntry.COLUMN_BACKDROP_PATH, backdropPath);
        values.put(FavoritesEntry.COLUMN_OVERVIEW, overview);
        values.put(FavoritesEntry.COLUMN_VOTE_AVERAGE, voteAverage);
        values.put(FavoritesEntry.COLUMN_RELEASE_DATE, releaseDate);

        Uri mUri = getContentResolver().insert(FavoritesEntry.CONTENT_URI, values);

        if (mUri != null) {

            Toast.makeText(DetailActivity.this, String.format(getString(R.string.add_favorite_text), mTitle), Toast.LENGTH_LONG).show();
            mFavoriteIcon.setImageResource(R.drawable.ic_favorite);
            isFavorite = true;
        }

    }


    private void deleteMovie() {


        Uri deleteUri = FavoritesEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mId)).build();
        int delete = getContentResolver().delete(deleteUri, null, null);

        if (delete != 0) {


            Toast.makeText(DetailActivity.this, String.format(getString(R.string.remove_favorite_text), mTitle), Toast.LENGTH_LONG).show();
            mFavoriteIcon.setImageResource(R.drawable.ic_favorite_border);
            isFavorite = false;
        }

    }

    private void loadReviews() {

        Call<Reviews> call = Client.getReviews(mId);

        if (call == null) {
            return;
        }

        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(@NonNull Call<Reviews> call, @NonNull Response<Reviews> response) {

                reviewAdapter.setReviewData(null);

                Reviews reviews = response.body();

                if (reviews != null && reviews.getReviewCount() > 0) {

                    List<Review> reviewList = reviews.getItems();
                    reviewAdapter.setReviewData(reviewList);
                    showReview();
                } else {
                    hideReview();
                }

            }

            @Override
            public void onFailure(@NonNull Call<Reviews> call, @NonNull Throwable t) {
                call.cancel();
                hideReview();
            }
        });

    }

    private void loadTrailers() {

        // Make a call with the endpoint
        Call<Trailers> call = Client.getTrailers(mId);

        if (call == null) {
            return;
        }

        call.enqueue(new Callback<Trailers>() {
            @Override
            public void onResponse(@NonNull Call<Trailers> call, @NonNull Response<Trailers> response) {

                trailersAdapter.setTrailerData(null);

                Trailers trailers = response.body();

                if (trailers != null && trailers.getItems().size() > 0) {

                    List<Trailer> trailerList = trailers.getItems();
                    trailersAdapter.setTrailerData(trailerList);
                    showTrailer();

                } else {
                    hideTrailer();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Trailers> call, @NonNull Throwable t) {
                call.cancel();
                hideTrailer();
            }
        });

    }


    private void showReview() {
        reviewEmptyView.setVisibility(View.INVISIBLE);
        reviewsRecyclerView.setVisibility(View.VISIBLE);
    }


    private void hideReview() {
        reviewsRecyclerView.setVisibility(View.INVISIBLE);
        reviewEmptyView.setVisibility(View.VISIBLE);
    }

    private void showTrailer() {
        trailerEmptyView.setVisibility(View.INVISIBLE);
        trailersRecyclerView.setVisibility(View.VISIBLE);
    }


    private void hideTrailer() {
        trailersRecyclerView.setVisibility(View.INVISIBLE);
        trailerEmptyView.setVisibility(View.VISIBLE);
    }


}