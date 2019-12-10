package com.example.john.moviesup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.john.moviesup.api.Client;
import com.example.john.moviesup.model.Movie;
import com.example.john.moviesup.model.MovieResponse;
import com.example.john.moviesup.model.MovieResponseAdaptor;
import com.example.john.moviesup.favorites.Favorites.FavoritesEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class MovieActivity extends AppCompatActivity implements MovieResponseAdaptor.MovieAdapterClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener {


    public static final String DETAIL_MOVIE_KEY = "movie";
    public static final String LIST_STATE_KEY = "extra_list_state";


    private MovieResponseAdaptor mAdapter;
    private Parcelable mListState;

    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.recycler_view_main)
    RecyclerView mRecyclerView;
    private boolean mSettingsUpdated = false;
    private Context context;
    private Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);




        if (savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);

        }

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);


        mAdapter = new MovieResponseAdaptor(this, this);
        mRecyclerView.setAdapter(mAdapter);


        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        emptyView.setText(R.string.missing_key_message);

        if (isConnected) {

            loadMovies();

        } else {

            progressBar.setVisibility(View.GONE);

            emptyView.setText(R.string.no_internet_connection);

        }

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onClickHandler(Movie movie) {

        Intent detailLaunch = new Intent(this, DetailActivity.class);
        detailLaunch.putExtra(DETAIL_MOVIE_KEY, movie);


        startActivity(detailLaunch);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mSettingsUpdated) {
            loadMovies();
            mSettingsUpdated = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        mSettingsUpdated = true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
            case R.id.action_delete:
                showFavoritesDeleteDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFavoritesDeleteDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_delete_favorites);
        builder.setMessage(R.string.delete_dialog_message);


        builder.setPositiveButton(R.string.delete_dialog_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int numberOfRowsDeleted = getContentResolver().delete(FavoritesEntry.CONTENT_URI, null, null);

                if (numberOfRowsDeleted != 0) {

                    Toast.makeText(MovieActivity.this, getString(R.string.delete_dialog_success), Toast.LENGTH_LONG).show();
                    loadMoviesFromDb();

                } else {

                    Toast.makeText(MovieActivity.this, getString(R.string.delete_dialog_error), Toast.LENGTH_LONG).show();

                }
            }
        });

        builder.setNegativeButton(R.string.delete_dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });

        builder.create().show();

    }


    private void loadMovies() {


        showProgressBar();


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = preferences.getString(getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));

        if (sortOrder.equals(getString(R.string.settings_order_by_favorites_value))) {

            loadMoviesFromDb();

        } else {


            loadMoviesFromApi(sortOrder);

        }


    }


    private void loadMoviesFromApi(String endpoint) {


        Call<MovieResponse> call = Client.getMovies(endpoint);

        if (call == null) {
            return;
        }

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {

                mAdapter.setMovieData(null);


                MovieResponse movies = response.body();


                if (movies != null) {
                    List<Movie> movieList = movies.getResults();
                    mAdapter.setMovieData(movieList);
                    showRecyclerContainer();
                    mRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
                } else {
                    showEmptyText();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                call.cancel();
                showEmptyText();
            }

        });

    }


    private void showProgressBar() {
        emptyView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showEmptyText() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        emptyView.setVisibility(View.VISIBLE);
    }

    private void showRecyclerContainer() {
        emptyView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MovieActivity.this);
        String sortOrder = preferences.getString(getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));


        if (!sortOrder.equals(getString(R.string.settings_order_by_favorites_value))) {
            MenuItem item = menu.findItem(R.id.action_delete);
            item.setVisible(false);
        }

        return true;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(LIST_STATE_KEY, Objects.requireNonNull(mRecyclerView.getLayoutManager()).onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }


    private void loadMoviesFromDb() {

        mAdapter.setMovieData(null);


        String[] projection = {
                FavoritesEntry.COLUMN_TITLE,
                FavoritesEntry.COLUMN_MOVIE_ID,
                FavoritesEntry.COLUMN_POSTER_PATH,
                FavoritesEntry.COLUMN_BACKDROP_PATH,
                FavoritesEntry.COLUMN_OVERVIEW,
                FavoritesEntry.COLUMN_VOTE_AVERAGE,
                FavoritesEntry.COLUMN_RELEASE_DATE
        };


        Cursor cursor = getContentResolver().query(
                FavoritesEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

        if (cursor == null) {
            return;
        }

        List<Movie> movies = new ArrayList<>();


        for (int i = 0; i < cursor.getCount(); i++) {

            cursor.moveToPosition(i);
            String title = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_TITLE));
            int movieId = cursor.getInt(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_ID));
            String posterPath = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_POSTER_PATH));
            String backDropPath = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_BACKDROP_PATH));
            String overview = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_OVERVIEW));
            String averageVotes = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_VOTE_AVERAGE));
            String date = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_RELEASE_DATE));

            Movie movie = new Movie(movieId, title, posterPath, backDropPath, overview, averageVotes, date);

            movies.add(movie);

        }

        cursor.close();


        if (movies.size() != 0) {

            mAdapter.setMovieData(movies);

            showRecyclerContainer();
            Objects.requireNonNull(mRecyclerView.getLayoutManager()).onRestoreInstanceState(mListState);

        } else {
            emptyView.setText(R.string.favorite_empty_text);
            showEmptyText();
        }

    }

}

