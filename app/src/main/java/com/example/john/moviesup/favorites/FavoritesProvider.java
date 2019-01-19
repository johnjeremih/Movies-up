package com.example.john.moviesup.favorites;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.john.moviesup.favorites.Favorites.FavoritesEntry;

public class FavoritesProvider extends ContentProvider {

    private static final String LOG_TAG = FavoritesProvider.class.getSimpleName();

    public static final int CODE_FAVORITES = 100;
    public static final int CODE_FAVORITES_ITEM = 101;

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private FavoritesHelper movieHelper;


    @Override
    public boolean onCreate() {

        movieHelper = new FavoritesHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;

        int match = uriMatcher.match(uri);
        SQLiteDatabase db = movieHelper.getReadableDatabase();

        switch (match) {
            case CODE_FAVORITES:


                cursor = db.query(Favorites.FavoritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_FAVORITES_ITEM:


                String[] args = {uri.getLastPathSegment()};
                cursor = db.query(FavoritesEntry.TABLE_NAME,
                        projection,
                        FavoritesEntry.COLUMN_MOVIE_ID + "=?",
                        args,
                        null,
                        null,
                        sortOrder
                );

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {


        if (contentValues == null || contentValues.size() == 0) {

            return null;

        }

        long newRowId;
        int movieId = 0;
        String title = null;


        if (contentValues.containsKey(FavoritesEntry.COLUMN_MOVIE_ID)) {

            movieId = contentValues.getAsInteger(FavoritesEntry.COLUMN_MOVIE_ID);

        }

        if (contentValues.containsKey(FavoritesEntry.COLUMN_TITLE)) {

            title = contentValues.getAsString(FavoritesEntry.COLUMN_TITLE);

        }

        if (movieId <= 0 || title == null) {

            throw new IllegalArgumentException("A movie needs a valid movie id and a title");

        }

        int match = uriMatcher.match(uri);
        SQLiteDatabase db = movieHelper.getWritableDatabase();

        switch (match) {
            case CODE_FAVORITES:


                newRowId = db.insert(FavoritesEntry.TABLE_NAME, null, contentValues);

                if (newRowId == -1) {

                    Log.e(LOG_TAG, "Movie insert failed for URI: " + uri);

                } else {

                    getContext().getContentResolver().notifyChange(uri, null);
                    return ContentUris.withAppendedId(uri, movieId);

                }

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return null;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {


        if (contentValues.size() == 0) {

            return 0;

        }

        int numberOfRowsUpdated;
        int movieId = 0;
        String title = null;


        if (contentValues.containsKey(FavoritesEntry.COLUMN_MOVIE_ID)) {

            movieId = contentValues.getAsInteger(FavoritesEntry.COLUMN_MOVIE_ID);

        }

        if (contentValues.containsKey(FavoritesEntry.COLUMN_TITLE)) {

            title = contentValues.getAsString(FavoritesEntry.COLUMN_TITLE);

        }

        if (movieId <= 0 || title == null) {

            throw new IllegalArgumentException("A recipe needs a valid recipe id and a title");

        }

        int match = uriMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = movieHelper.getWritableDatabase();

        switch (match) {
            case CODE_FAVORITES:


                numberOfRowsUpdated = sqLiteDatabase.update(FavoritesEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case CODE_FAVORITES_ITEM:

                String[] args = new String[]{uri.getLastPathSegment()};
                numberOfRowsUpdated = sqLiteDatabase.update(
                        FavoritesEntry.TABLE_NAME,
                        contentValues,
                        FavoritesEntry.COLUMN_MOVIE_ID + "=?",
                        args);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numberOfRowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int numberOfRowsDeleted;

        int match = uriMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = movieHelper.getWritableDatabase();

        switch (match) {
            case CODE_FAVORITES:

                numberOfRowsDeleted = sqLiteDatabase.delete(FavoritesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CODE_FAVORITES_ITEM:

                String[] args = {uri.getLastPathSegment()};
                numberOfRowsDeleted = sqLiteDatabase.delete(FavoritesEntry.TABLE_NAME, FavoritesEntry.COLUMN_MOVIE_ID + "=?", args);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numberOfRowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        int match = uriMatcher.match(uri);

        switch (match) {
            case CODE_FAVORITES:
                return FavoritesEntry.CONTENT_LIST_TYPE;
            case CODE_FAVORITES_ITEM:
                return FavoritesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Uri " + uri + "has no match with " + match);
        }
    }


    public static UriMatcher buildUriMatcher() {

        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(Favorites.CONTENT_AUTHORITY, Favorites.PATH_FAVORITES, CODE_FAVORITES);
        uriMatcher.addURI(Favorites.CONTENT_AUTHORITY, Favorites.PATH_FAVORITES + "/#", CODE_FAVORITES_ITEM);

        return uriMatcher;
    }
}
