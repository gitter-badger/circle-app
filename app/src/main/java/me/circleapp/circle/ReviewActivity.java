package me.circleapp.circle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import me.circleapp.api.objects.Review;


public class ReviewActivity extends Activity {

    protected EditText mDescriptionText;
    protected EditText mTitleText;
    protected RatingBar mRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        mDescriptionText = (EditText) findViewById(R.id.description);
        mTitleText = (EditText) findViewById(R.id.title);
        mRatingBar = (RatingBar) findViewById(R.id.stars);

        mTitleText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    mDescriptionText.requestFocus();
                    mTitleText.clearFocus();
                    return true;
                }
                return false;
            }
        });

        mDescriptionText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mDescriptionText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });
    }

    public void submit(View v) {
        float rating = mRatingBar.getRating();
        String title = mTitleText.getText().toString();
        String description = mDescriptionText.getText().toString();

        if(rating <= 0){
            Toast.makeText(this, R.string.invalid_rating, Toast.LENGTH_SHORT).show();
            return;
        }
        /* Habilitar si no se quieren reviews vacíos
        else if(title.isEmpty()){
            Toast.makeText(this, R.string.invalid_review_title, Toast.LENGTH_SHORT).show();
        }else if(description.isEmpty()){
            Toast.makeText(this, R.string.invalid_review_description, Toast.LENGTH_SHORT).show();
        }*/

        Intent response = getIntent();
        Review review = new Review(rating, title, description);
        response.putExtra("review", review);
        setResult(Activity.RESULT_OK, response);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.review, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }
}
