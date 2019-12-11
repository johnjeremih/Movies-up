package com.example.john.moviesup.reviews;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Review implements Parcelable {

    @SerializedName("id")
    private String mId;

    @SerializedName("author")
    public String mAuthor;

    @SerializedName("content")
    public String mContent;

    @SerializedName("url")
    public String mUrl;

    public Review(String id, String author, String content, String url) {
        mId = id;
        mAuthor = author;
        mContent = content;
        mUrl = url;
    }

    protected Review(Parcel in) {
        mId = in.readString();
        mAuthor = in.readString();
        mContent = in.readString();
        mUrl = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getId() {
        return mId;
    }

    public void setId(String newId) {
        mId = newId;
    }

    String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String newAuthor) {
        mAuthor = newAuthor;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String newContent) {
        mContent = newContent;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String newUrl) {
        mUrl = newUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mAuthor);
        dest.writeString(mContent);
        dest.writeString(mUrl);
    }
}
