package me.circleapp.circle;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import me.circleapp.api.objects.Place;
import me.circleapp.api.objects.Review;

import java.util.ArrayList;
import java.util.List;


public class PlaceActivity extends Activity {

    public static final String LOG_TAG = "PlaceActivity";
    protected static final int REVIEW_REQUEST_CODE = 1;
    protected App app;
    protected Place place = null;
    protected Place nextPlace = null;
    protected ArrayList<Place> nextList;
    protected boolean showNext;
    protected PlaceFragment placeFragment;
    protected boolean isFavorite;
    protected ParseObject parsePlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_activiy);
        app = (App) getApplication();

        Intent intent = getIntent();
        this.place = (Place) intent.getSerializableExtra("place");
        this.nextList = (ArrayList<Place>) intent.getSerializableExtra("nextList");
        this.parsePlace = ParseObject.createWithoutData("Place", place.getObjectId());

        showNext = false;
        if (nextList.size() > 0) {
            nextPlace = nextList.remove(0);
            showNext = true;
        }

        getActionBar().setTitle(place.getName());

        //Put Fragment on place
        FragmentManager man = getFragmentManager();
        FragmentTransaction trans = man.beginTransaction();
        placeFragment = PlaceFragment.newInstance(place, showNext);

        trans.add(R.id.fragment_view, placeFragment);
        trans.commit();
        //!Put fragment

        ParseQuery query = app.mUser.getRelation("favs").getQuery();
        query.whereEqualTo("objectId", place.getObjectId());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if(list.size() > 0){
                        isFavorite = true;
                    }else{
                        isFavorite = false;
                    }
                } else {
                    isFavorite = false;
                }

                placeFragment.toggleFav(isFavorite);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REVIEW_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Review review = (Review) data.getSerializableExtra("review");
                ParseObject parseReview = new ParseObject("Review");
                parseReview.put("user", app.mUser);
                parseReview.put("place", parsePlace);
                parseReview.put("title", review.getTitle());
                parseReview.put("stars", review.getStars());
                parseReview.put("description", review.getDescription());
                parseReview.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            placeFragment.loadReviews();
                        }else{
                            Log.i(LOG_TAG, e.getMessage());
                        }
                    }
                });
            }
        }
    }

    public void nextPlace(View v) {
        if (!showNext) {
            return;
        }

        Intent next = new Intent(this, PlaceActivity.class);
        next.putExtra("place", nextPlace);
        next.putExtra("nextList", (ArrayList<Place>) nextList.clone());
        startActivity(next);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out_null);
        finish(); // Quitar si se quiere ver la recomendacion anterior
    }

    public void exit(View v) {
        /*Intent menu = new Intent(this, MenuActivity.class);
        menu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(menu);
        overridePendingTransition(R.anim.push_left_in_null, R.anim.push_left_out);*/
        finish();
    }

    public void uploadPhoto(View v){

    }

    public void writeReview(View v) {
        Intent review = new Intent(this, ReviewActivity.class);
        startActivityForResult(review, REVIEW_REQUEST_CODE);
    }

    public void addToFavorites(View v) {
        ParseRelation favs = app.mUser.getRelation("favs");
        if (isFavorite) {
            favs.remove(parsePlace);
        } else {
            favs.add(parsePlace);
        }

        app.mUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
            if (e == null) {
                placeFragment.toggleFav(!isFavorite);
                isFavorite = !isFavorite;
            }else{
                Log.i(LOG_TAG, e.getMessage());
            }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_left_in_null, R.anim.push_left_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.place_activiy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
