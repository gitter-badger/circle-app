package me.circleapp.circle;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import me.circleapp.api.Where2GoAPI;
import me.circleapp.api.objects.Place;
import me.circleapp.api.responses.TreeResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class GameActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String LOG_TAG = "GameActivity";
    protected boolean playServices = false;
    protected boolean connected = false;
    protected LocationClient mLocationClient;
    protected Location mLocation;
    protected Place mTree;
    protected List<Place> mNextList;
    protected ProgressBar mLoader;
    protected View mGameLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_activiy);
        findViewById(R.id.ynimage).setOnTouchListener(new MyTouchListener());
        findViewById(R.id.start).setOnDragListener(new MyDragListener());
        findViewById(R.id.positive).setOnDragListener(new MyDragListener());
        findViewById(R.id.negative).setOnDragListener(new MyDragListener());

        mLoader = (ProgressBar) findViewById(R.id.loader);
        mGameLayout = findViewById(R.id.gameLayout);

        mLocationClient = new LocationClient(this, this, this);
        mLocationClient.connect();

    }

    protected Callback<TreeResponse> treeCallback = new Callback<TreeResponse>() {
        @Override
        public void success(TreeResponse treeResponse, Response response) {
            if(treeResponse.error == null){
                mLoader.setVisibility(ProgressBar.GONE);
                mGameLayout.setVisibility(View.VISIBLE);
                if(treeResponse.results.size() > 0){
                    mTree = treeResponse.results.remove(0);
                    mNextList = treeResponse.results;
                }else{
                    //No hay lugares cerca
                    Toast.makeText(GameActivity.this, "No nearby place found", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }else{
                onBackPressed();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Toast.makeText(GameActivity.this, "Game start failed" + error.getMessage(), Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    };

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        mLocation = mLocationClient.getLastLocation();
        if(mLocation != null){
            double latitude = mLocation.getLatitude();
            double longitude = mLocation.getLongitude();
            int radius = 5;
            Where2GoAPI api = new Where2GoAPI();
            api.getTree(latitude, longitude, radius, treeCallback);
        }else{
            Toast.makeText(this, "No location found!", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }


    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in_null, R.anim.push_right_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //  getMenuInflater().inflate(R.menu.game_activiy, menu);
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
        }else if(id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final class MyTouchListener implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListener implements OnDragListener {
        Drawable enterShapeY = getResources().getDrawable(R.drawable.dropareay);
        Drawable enterShapeN = getResources().getDrawable(R.drawable.droparean);
        Drawable normalShape = getResources().getDrawable(R.drawable.shape);

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    if(v == findViewById(R.id.positive)){
                        v.setBackgroundDrawable(enterShapeY);
                    }
                    if(v == findViewById(R.id.negative)){
                        v.setBackgroundDrawable(enterShapeN);
                    }
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundDrawable(normalShape);
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
                    View view = (View) event.getLocalState();
                    TextView pregunta = (TextView) findViewById(R.id.pregunta);
                    View icono = (View) findViewById(R.id.ynimage);
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);
                    RelativeLayout container = (RelativeLayout) v;
                    container.addView(view);
                    view.setVisibility(View.VISIBLE);
                    if(v == findViewById(R.id.positive)){
                        pregunta.setText("Sí.");

                        Intent favs = new Intent(GameActivity.this, PlaceActivity.class);
                        favs.putExtra("place", mTree);
                        favs.putExtra("nextList", (ArrayList<Place>) mNextList);
                        startActivity(favs);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out_null);

                        //icono.setVisibility(View.GONE);
                    }
                    if(v == findViewById(R.id.negative)){
                        pregunta.setText("No.");
                        //icono.setVisibility(View.GONE);
                    }
                    if(v == findViewById(R.id.start)){
                        pregunta.setText("¿Vas solo?");
                        //icono.setVisibility(View.GONE);
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundDrawable(normalShape);
                default:
                    break;
            }
            return true;
        }
    }

    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                        break;
                }
        }
    }

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(getFragmentManager(),
                        "Location Updates");
            }
        }

        return false;
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }
    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //showErrorDialog(connectionResult.getErrorCode());
        }
    }

}
