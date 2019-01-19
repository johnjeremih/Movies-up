package com.example.john.moviesup.favorites;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoritesHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;
    private static final String MODIFY_TABLE_COMMAND = "";

    public FavoritesHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create table command for favourites table
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + Favorites.FavoritesEntry.TABLE_NAME + " (" +
                Favorites.FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Favorites.FavoritesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                Favorites.FavoritesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                Favorites.FavoritesEntry.COLUMN_POSTER_PATH + " TEXT, " +
                Favorites.FavoritesEntry.COLUMN_BACKDROP_PATH + " TEXT, " +
                Favorites.FavoritesEntry.COLUMN_OVERVIEW + " TEXT, " +
                Favorites.FavoritesEntry.COLUMN_VOTE_AVERAGE + " TEXT, " +
                Favorites.FavoritesEntry.COLUMN_RELEASE_DATE + " TEXT);";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < DATABASE_VERSION) {

            sqLiteDatabase.execSQL(MODIFY_TABLE_COMMAND);

        }
    }
}



