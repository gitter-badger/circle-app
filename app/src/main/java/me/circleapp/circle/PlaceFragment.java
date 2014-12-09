package me.circleapp.circle;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import me.circleapp.api.objects.Place;
import me.circleapp.api.objects.Review;
import me.circleapp.circle.adapters.PlaceReviewAdapter;

import java.util.ArrayList;
import java.util.List;


public class PlaceFragment extends Fragment {

    protected TextView mPlaceName;
    protected TextView mPlaceShortLocation;
    protected ListView mReviews;
    protected Place place;
    protected boolean next;
    protected Button mNextPlace;
    protected ImageButton mFavButton;
    protected ParseObject parsePlace;
    protected PlaceReviewAdapter mReviewAdapter;
    protected ArrayList<Review> mReviewsList;

    private OnFragmentInteractionListener mListener;

    public static PlaceFragment newInstance(Place place, boolean next) {
        PlaceFragment fragment = new PlaceFragment();
        Bundle args = new Bundle();
        args.putBoolean("next", next);
        args.putSerializable("place", place);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceFragment() {
        // Required empty public constructor
    }

    public void toggleFav(boolean fav) {
        if (fav) {
            mFavButton.setImageResource(R.drawable.ic_action_heart);
        } else {
            mFavButton.setImageResource(R.drawable.ic_action_sad_heart);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            place = (Place) getArguments().getSerializable("place");
            next = getArguments().getBoolean("next");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_place, container, false);
        mPlaceName = (TextView) v.findViewById(R.id.place_name);
        mPlaceShortLocation = (TextView) v.findViewById(R.id.place_short_location);
        mReviews = (ListView) v.findViewById(R.id.reviews_list);
        mNextPlace = (Button) v.findViewById(R.id.nextPlace);
        mFavButton = (ImageButton) v.findViewById(R.id.favButton);
        mPlaceName.setText(place.getName());
        mPlaceShortLocation.setText(place.getShortLocation());

        mReviewsList = new ArrayList<Review>();
        mReviewAdapter = new PlaceReviewAdapter(getActivity(), mReviewsList);

        mReviews.setAdapter(mReviewAdapter);

        loadReviews();

        if (!next) {
            mNextPlace.setVisibility(Button.GONE);
        }

        return v;
    }

    public void loadReviews(){
        ParseQuery reviewsQuery = ParseQuery.getQuery("Review");
        reviewsQuery.whereEqualTo("place", ParseObject.createWithoutData("Place", place.getObjectId()));
        reviewsQuery.orderByDescending("createdAt");
        reviewsQuery.include("user");

        reviewsQuery.findInBackground(new FindCallback() {
            @Override
            public void done(List list, ParseException e) {
                if (e == null) {
                    mReviewsList.clear();
                    for (Object review : list) {
                        mReviewsList.add(new Review((ParseObject) review));
                        mReviewAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.reviews_list_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
