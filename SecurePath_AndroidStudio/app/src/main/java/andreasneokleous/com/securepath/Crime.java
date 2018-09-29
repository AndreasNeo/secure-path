package andreasneokleous.com.securepath;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Andreas Neokleous.
 */

public class Crime  implements ClusterItem  {
    private int mId;
    private String mStreet;
    private String mCategory;
    private LatLng mPosition;
    private String mOutcome;


    public Crime(int id, Double mLat, Double mLng, String mStreet, String mCategory, String mOutcome){
        this.mId = id;
        this.mStreet = mStreet;

        String format = mCategory.replaceAll("-"," ");
        if (format.length()>1)
        this.mCategory = format.substring(0,1).toUpperCase() + format.substring(1);

        this.mOutcome = mOutcome;

        mPosition = new LatLng(mLat,mLng);
    }


    public String getmOutcome() {
        return mOutcome;
    }
    public int getmId() {
        return mId;
    }
    public LatLng getmPosition() {
        return mPosition;
    }

    public String getmStreet() {
        return mStreet;
    }


    public String getmCategory() {
        return mCategory;
    }

    @Override
    public LatLng getPosition() {
        return this.mPosition;
    }

    @Override
    public String getTitle() {
        return this.mCategory;
    }

    @Override
    public String getSnippet() {
        return "";
    }
}
