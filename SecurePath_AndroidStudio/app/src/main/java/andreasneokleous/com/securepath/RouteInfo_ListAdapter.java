package andreasneokleous.com.securepath;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Andreas Neokleous.
 */

public class RouteInfo_ListAdapter extends ArrayAdapter<MyRoute> {
    private Boolean showSeriousness = false;

    public RouteInfo_ListAdapter(@NonNull Context context, ArrayList<MyRoute> routes, Boolean showSeriousness) {
        super(context,0, routes);
        this.showSeriousness = showSeriousness;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        MyRoute route = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        RouteInfo_ListAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new RouteInfo_ListAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            if (showSeriousness) {
                convertView = inflater.inflate(R.layout.route_info_adapter, parent, false);
                viewHolder.routeSeriousness = (TextView) convertView.findViewById(R.id.route_seriousness);
            }else{
                convertView = inflater.inflate(R.layout.route_info_adapter2, parent, false);
            }
            viewHolder.routeDistance = (TextView) convertView.findViewById(R.id.route_distance);
            viewHolder.routeDuration = (TextView) convertView.findViewById(R.id.route_duration);
            viewHolder.routeButton = (Button) convertView.findViewById(R.id.route_button);
            viewHolder.routeInfo = (LinearLayout) convertView.findViewById(R.id.route_info);
            viewHolder.routeCrimes = (TextView) convertView.findViewById(R.id.route_crimes);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (RouteInfo_ListAdapter.ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.routeDistance.setText(String.valueOf(route.getRoute().getDistanceValue()) + " m");

        viewHolder.routeDuration.setText(String.valueOf(route.getRoute().getDurationText()));
        viewHolder.routeCrimes.setText(String.valueOf(route.getCrimesOnRoute()));
        if (showSeriousness) {
            viewHolder.routeSeriousness.setText(String.valueOf(route.getSeriousness()));
        }
        viewHolder.routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v,position,0);
            }
        });

        viewHolder.routeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v,position,0);
            }
        });
        //   viewHolder.crimeLocation.setText(crime.getmLocation());
        // Return the completed view to render on screen
        return convertView;


    }

    static class ViewHolder{
        LinearLayout routeInfo;
        TextView routeDuration;
        TextView routeDistance;
        Button routeButton;
        TextView routeCrimes;
        TextView routeSeriousness;
        int position;
    }
}
