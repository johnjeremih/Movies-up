package com.example.john.moviesup.trailers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.john.moviesup.DetailActivity;
import com.example.john.moviesup.R;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailersAdapter  extends RecyclerView.Adapter<TrailersAdapter.TrailerAdapterViewHolder> {

    private static final String LOG_TAG = TrailersAdapter.class.getSimpleName();
    private ArrayList<Trailer> mTrailers;
    private Context mContext;
    private final TrailersAdapter.TrailerAdapterClickHandler mClickHandler;



    // Interface implementation for item click
    public interface TrailerAdapterClickHandler {
        void onClickHandler(Trailer trailer);
    }

    // Public constructor
    public TrailersAdapter(Context context, TrailerAdapterClickHandler handler) {
        mContext = context;
        mClickHandler = handler;
    }

    // The ViewHolder class of the ReviewAdapter
    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // The title and the post of the Review
        @BindView(R.id.trailer_item_name_tv) TextView mNameTextView;
        @BindView (R.id.trailer_item_type_tv) TextView mTypeTextView;

        // Constructor for the ViewHolder class
        public TrailerAdapterViewHolder(View view) {
            super(view);

            // Butterknife binding
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Trailer trailer = mTrailers.get(getAdapterPosition());
            mClickHandler.onClickHandler(trailer);
        }
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Create a layout from the list_item.xml and initalize the ViewHolder with it
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_item, parent, false);
        return new TrailersAdapter.TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TrailersAdapter.TrailerAdapterViewHolder holder, int position) {

        // Get the current Review and its needed properties
        Trailer actualTrailer = mTrailers.get(position);
        String actualName = actualTrailer.getName();
        String actualType = actualTrailer.getType();

        holder.mNameTextView.setText(actualName);
        holder.mTypeTextView.setText(actualType);

    }

    @Override
    public int getItemCount() {

        // If we don't have reviews, return 0
        if (mTrailers == null) {
            return 0;
        }

        // Otherwise, return the size of the Review list
        return mTrailers.size();
    }

    public void setTrailerData(List<Trailer> trailers){

        // Refresh the review list and notify the loader
        mTrailers = (ArrayList<Trailer>) trailers;
        notifyDataSetChanged();
    }


}
