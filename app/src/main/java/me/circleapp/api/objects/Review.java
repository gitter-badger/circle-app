package me.circleapp.api.objects;

import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.Serializable;

/**
 * Created by henocdz on 17/11/14.
 */
public class Review implements Serializable {
    private String title;
    private String description;
    private float stars;
    private String image;
    private String facebookId;
    private String userName;

    public Review(float stars, String title, String description) {
        this.title = title;
        this.description = description;
        this.stars = stars;
        this.facebookId = "";
        this.image = null;
    }

    public Review(ParseObject review){
        this.title = review.getString("title");
        this.description = review.getString("description");
        this.stars = (float) review.getDouble("stars");
        ParseUser user = review.getParseUser("user");
        if(user != null){
            this.facebookId = user.getString("facebookId");
            this.userName = user.getString("username");
        }else{
            this.facebookId = "2345667654321";
            this.userName = "Undefined";
        }
    }

    public String getUserName(){ return userName; }

    public String getFacebookId(){ return facebookId; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
