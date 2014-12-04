package me.circleapp.circle.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import me.circleapp.circle.R;

import java.util.ArrayList;

/**
 * Created by henocdz on 25/11/14.
 */
public class FavoriteAdapter extends ArrayAdapter<ParseObject> {

    public static final String LOG_TAG = "FavoriteAdapter";
    public static int favorite_layout = R.layout.favorite;
    public ArrayList<ParseObject> mPlaces;
    protected Context mContext;

    public FavoriteAdapter(Context context, ArrayList<ParseObject> favorites){
        super(context, favorite_layout, favorites);

        this.mPlaces = favorites;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if(view == null){
            view = LayoutInflater.from(mContext).inflate(favorite_layout, null);
            holder = new ViewHolder();

            holder.container = view.findViewById(R.id.container);
            holder.background = (ImageView) holder.container.findViewById(R.id.background);
            holder.location = (TextView) holder.container.findViewById(R.id.place_location);
            holder.name = (TextView) holder.container.findViewById(R.id.place_name);

            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        ParseObject place = mPlaces.get(position);

        Log.e(LOG_TAG, place.getRelation("pix").toString());
        //Picasso.with(this.getContext()).load().centerCrop().into(holder.background);

        holder.location.setText(place.getString("name"));
        holder.name.setText(place.getString("shortLocation"));

        return view;
    }

    public class ViewHolder{
        View container;
        ImageView background;
        ImageButton toogleFav;
        TextView name;
        TextView location;
    }
}
