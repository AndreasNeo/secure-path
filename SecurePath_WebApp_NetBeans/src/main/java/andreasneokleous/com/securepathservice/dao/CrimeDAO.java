package andreasneokleous.com.securepathservice.dao;

import andreasneokleous.com.securepathservice.model.CrimeLocation;
import com.google.maps.model.LatLng;
import java.util.List;


/**
 * @author Andreas Neokleous
 */

public interface CrimeDAO {

    public List<CrimeLocation> crimesOnLocations(List<LatLng> route);
}
