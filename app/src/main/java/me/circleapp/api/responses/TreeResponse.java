package me.circleapp.api.responses;

import me.circleapp.api.objects.Place;

import java.util.List;

/**
 * Created by henocdz on 15/11/14.
 */
public class TreeResponse {
    public String error;
    public List<Place> results;

    public TreeResponse(String error){
        this.error = error;
    }

    public TreeResponse(List<Place> results){
        this.error = null;
        this.results = results;
    }
}
