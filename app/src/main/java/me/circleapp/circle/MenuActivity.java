package me.circleapp.circle;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;


public class MenuActivity extends Activity {


    protected App app;

    private static final String LOG_TAG = "MenuActivity";
    protected ProgressBar pb;
    protected Button facebookBtn;
    protected Button playBtn;
    protected Button favoritesBtn;
    protected Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_activiy);

        getActionBar().setTitle("");

        menu = null;
        app = (App) this.getApplicationContext();
        pb = (ProgressBar) findViewById(R.id.progressBar);
        facebookBtn = (Button) findViewById(R.id.facebook_btn);
        playBtn = (Button) findViewById(R.id.play_btn);
        favoritesBtn = (Button) findViewById(R.id.favorites);
        try {

            PackageInfo info = getPackageManager().getPackageInfo("me.circleapp.circle", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (app.isUserLogged()) {
            toggleUI(true);
        }
    }

    private void toggleUI(boolean userLogged) {
        if (userLogged) {
            pb.setVisibility(View.GONE);
            facebookBtn.setVisibility(View.GONE);
            playBtn.setVisibility(View.VISIBLE);
            favoritesBtn.setVisibility(View.VISIBLE);
        } else {
            facebookBtn.setVisibility(View.VISIBLE);
            playBtn.setVisibility(View.GONE);
            favoritesBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activiy, menu);
        if (app.isUserLogged()) {
            menu.setGroupVisible(R.id.group_menu, true);
        } else {
            menu.setGroupVisible(R.id.group_menu, false);
        }
        return true;
    }

    public void letsPlay(View v) {
        Intent game = new Intent(this, GameActivity.class);
        game.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(game);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out_null);
    }

    public void favs(View v) {
        Intent favs = new Intent(this, FavsActivity.class);
        startActivity(favs);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out_null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }


    public void loginFacebook(View v) {
        facebookBtn.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        List<String> perms = Arrays.asList("email", "public_profile");
        ParseFacebookUtils.logIn(perms, this, new LogInCallback() {
            @Override
            public void done(final ParseUser user, ParseException e) {
                pb.setVisibility(View.GONE);
                if (user == null) {
                    facebookBtn.setVisibility(View.VISIBLE);
                } else { //mUser.isNew()
                    app.loginUser(user);
                    menu.setGroupVisible(R.id.group_menu, true);
                    toggleUI(true);

                    Request.executeMeRequestAsync(Session.getActiveSession(), new Request.GraphUserCallback() {
                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser u, Response response) {
                            String fullName = u.getName();
                            String email = u.getProperty("email").toString();
                            String facebookId = u.getId();

                            app.mUser.setEmail(email);
                            app.mUser.setUsername(fullName);
                            app.mUser.put("facebookId", facebookId);

                            app.mUser.saveInBackground();
                            //Do whatever you want

                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            app.mUser.logOut();

            if (ParseUser.getCurrentUser() == null) {
                facebookBtn.setVisibility(View.VISIBLE);
                playBtn.setVisibility(View.GONE);
                favoritesBtn.setVisibility(View.GONE);
                app.logoutUser();
                menu.setGroupVisible(R.id.group_menu, false);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
