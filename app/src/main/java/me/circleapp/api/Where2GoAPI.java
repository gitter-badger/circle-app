package me.circleapp.api;

import me.circleapp.api.responses.TreeResponse;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by henocdz on 13/11/14.
 */
public class Where2GoAPI {
    private static final String API_URL = "http://w2g.henocdz.com/";
    private RestAdapter adapter;

    public Where2GoAPI(){
        adapter = new  RestAdapter.Builder().setEndpoint(API_URL).build();
    }

    protected interface Tree{
        @GET("/tree")
        public void getTree(
                @Query("latitude") double latitude,
                @Query("longitude") double longitude, @Query("radius") int radius,
                Callback<TreeResponse> callback);
    }

    // Public Methods
    public void getTree(double latitude, double longitude, int radius, Callback<TreeResponse> callback){
        Tree request = adapter.create(Tree.class);
        request.getTree(latitude, longitude, radius, callback);
    }
}
