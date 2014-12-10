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

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.squareup.picasso.Picasso;

import me.circleapp.circle.FavoritesFragment;
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

    public FavoriteAdapter(Context context, ArrayList<ParseObject> favorites) {
        super(context, favorite_layout, favorites);

        this.mPlaces = favorites;
        this.mContext = context;

    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(favorite_layout, null);
            holder = new ViewHolder();

            holder.container = view.findViewById(R.id.container);
            holder.background = (ImageView) holder.container.findViewById(R.id.background);
            holder.location = (TextView) holder.container.findViewById(R.id.place_location);
            holder.name = (TextView) holder.container.findViewById(R.id.place_name);
            holder.unfav = (ImageButton) holder.container.findViewById(R.id.unfav);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ParseObject place = mPlaces.get(position);
        ParseQuery pixQuery = place.getRelation("pix").getQuery();
        holder.background.setImageResource(android.R.color.transparent);
        final ViewHolder holderBackground = holder;
        pixQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (parseObject != null) {
                    ParseFile image = parseObject.getParseFile("image");
                    Picasso.with(getContext()).load(image.getUrl()).into(holderBackground.background);
                }
            }
        });

        holder.unfav.setOnClickListener(new UnFavClickListener(position));
        holder.unfav.setImageResource(R.drawable.ic_action_heart);
        holder.location.setText(place.getString("name"));
        holder.name.setText(place.getString("shortLocation"));

        return view;
    }

    private class UnFavClickListener implements View.OnClickListener {

        private int index;
        private FavoriteAdapter adapter;

        public UnFavClickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            ImageButton me = (ImageButton) v;
            me.setImageResource(R.drawable.ic_action_sad_heart);
            FavoritesFragment.unFav(this.index);
        }

    }



    public class ViewHolder {
        View container;
        ImageView background;
        ImageButton unfav;
        TextView name;
        TextView location;
    }
}
