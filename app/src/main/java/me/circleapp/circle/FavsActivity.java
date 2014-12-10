package me.circleapp.circle;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseObject;

import java.util.ArrayList;

import me.circleapp.api.objects.Place;


public class FavsActivity extends Activity implements FavoritesFragment.OnFragmentInteractionListener {
    //910-213

    protected FavoritesFragment favsFragment;
    private static final String LOG_TAG = "FavsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favs);

        FragmentManager man = getFragmentManager();
        FragmentTransaction trans = man.beginTransaction();
        favsFragment = FavoritesFragment.newInstance();

        trans.add(R.id.fragment_view, favsFragment);
        trans.commit();
    }

    public void onItemClicked(Place place) {
        Log.d(LOG_TAG, ">>>>>>>>>>> Placeando");
        Intent favs = new Intent(this, PlaceActivity.class);
        favs.putExtra("place", place);
        favs.putExtra("nextList", new ArrayList<Place>());
        startActivity(favs);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out_null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.favs, menu);
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_left_in_null, R.anim.push_left_out);
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
