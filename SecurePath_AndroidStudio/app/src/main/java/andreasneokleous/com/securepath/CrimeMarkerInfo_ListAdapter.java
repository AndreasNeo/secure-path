package andreasneokleous.com.securepath;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Andreas Neokleous.
 */

public class CrimeMarkerInfo_ListAdapter extends ArrayAdapter<Crime> {

    public CrimeMarkerInfo_ListAdapter(Context context, ArrayList<Crime> crimes) {
        super(context, 0, crimes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        Crime crime = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.crime_info_adapter, parent, false);
            viewHolder.crimeOutcome = (TextView) convertView.findViewById(R.id.crimeOutcome);
            viewHolder.crimeLocation = (TextView) convertView.findViewById(R.id.crimeLocation);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.crimeOutcome.setText(String.valueOf(crime.getmOutcome()));

        viewHolder.crimeLocation.setText(String.valueOf(crime.getmCategory()));
        // Return the completed view to render on screen
        return convertView;


    }

    static class ViewHolder{
        TextView crimeLocation;
        TextView crimeOutcome;
    }
}

