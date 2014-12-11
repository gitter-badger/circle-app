
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
 * Created by henocdz on 10/12/14.
 */
public class CardAdapter extends ArrayAdapter<ParseObject> {

    public static final String LOG_TAG = "FavoriteAdapter";
    public static int card_layout = R.layout.card;
    public ArrayList<ParseObject> mPlaces;
    protected Context mContext;

    public CardAdapter(Context context, ArrayList<ParseObject> favorites) {
        super(context, card_layout, favorites);

        this.mPlaces = favorites;
        this.mContext = context;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(card_layout, null);
            holder = new ViewHolder();

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        return view;
    }

    public class ViewHolder {

    }
}
