package me.circleapp.api.objects;

import com.parse.ParseObject;

import java.io.Serializable;

/**
 * Created by henocdz on 15/11/14.
 */
public class Place implements Serializable {
    private String objectId;
    private String updatedAt;
    private Location location;
    private String createdAt;
    private String name;
    private String shortLocation;

    public Place(ParseObject place){
        name = place.getString("name");
    }
    public Place(){ this.name = "Prueba :)"; }

    public String getName(){
        return this.name;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public Location getLocation() {
        return location;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getShortLocation() {
        return shortLocation;
    }
}
