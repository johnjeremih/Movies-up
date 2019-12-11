package com.example.john.moviesup.reviews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.john.moviesup.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewAdapterViewHolder> {

    private static final String LOG_TAG = ReviewsAdapter.class.getSimpleName();
    private ArrayList<Review> mReviews;
    private Context mContext;

    public ReviewsAdapter(Context context) {
        mContext = context;
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.review_item_content)
        TextView mContentView;
        @BindView(R.id.review_item_author)
        TextView mAuthorView;


        public ReviewAdapterViewHolder(View view) {
            super(view);

            // Butterknife binding
            ButterKnife.bind(this, view);
        }


    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        return new ReviewsAdapter.ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewsAdapter.ReviewAdapterViewHolder holder, int position) {


        Review actualReview = mReviews.get(position);
        String actualContent = actualReview.getContent();
        String actualAuthor = actualReview.getAuthor();

        holder.mContentView.setText(actualContent);
        holder.mAuthorView.setText(actualAuthor);

    }

    @Override
    public int getItemCount() {

        if (mReviews == null) {
            return 0;
        }

        return mReviews.size();
    }

    public void setReviewData(List<Review> reviews) {

        mReviews = (ArrayList<Review>) reviews;
        notifyDataSetChanged();
    }
}
