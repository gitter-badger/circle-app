package me.circleapp.circle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import me.circleapp.api.objects.Review;
import me.circleapp.circle.R;

import java.util.ArrayList;

/**
 * Created by henocdz on 18/11/14.
 */
public class PlaceReviewAdapter extends ArrayAdapter<Review>{
    private ArrayList<Review> mReviews;
    private Context mContext;
    private static final int review_layout = R.layout.review;
    private static final int TAG_ID = 0;
    public PlaceReviewAdapter(Context context, ArrayList<Review> reviews){
        super(context, review_layout, reviews);

        this.mReviews = reviews;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if(view == null){
            view = LayoutInflater.from(mContext).inflate(review_layout, null);

            holder = new ViewHolder();
            holder.container = view.findViewById(R.id.container);
            holder.review = holder.container.findViewById(R.id.review);
            holder.userpic = (ImageView) holder.container.findViewById(R.id.userpic);
            holder.username = (TextView) holder.review.findViewById(R.id.username);
            holder.rating = (RatingBar) holder.review.findViewById(R.id.rating);
            holder.title = (TextView) holder.review.findViewById(R.id.title);
            holder.description = (TextView) holder.review.findViewById(R.id.description);

            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        Review review = mReviews.get(position);

        //Set values of Review in GUI
        //holder.userpic.setImageResource(); // Picasso

        Picasso.with(mContext).load("https://graph.facebook.com/"+ review.getFacebookId() +"/picture?type=large").resize(100, 100).centerCrop().into(holder.userpic);
        holder.username.setText(review.getUserName());
        holder.title.setText(review.getTitle());
        holder.description.setText(review.getDescription());
        holder.rating.setRating(review.getStars());

        return view;
    }

    protected class ViewHolder{
        View container;
        View review;
        ImageView userpic;
        RatingBar rating;
        TextView username;
        TextView title;
        TextView description;
    }
}
