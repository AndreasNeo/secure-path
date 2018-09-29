package andreasneokleous.com.securepathservice.model;


/**
 * @author Andreas Neokleous
 */

public class CrimeLocation {
    
    private double latitude;
    private double longitude;
    private int seriousness;
    private int crime_count;
    
    public CrimeLocation(){
        
    }
    public CrimeLocation(double latitude, double longitude, int seriousness,int crime_count ){
        this.latitude = latitude;
        this.longitude = longitude;
        this.seriousness = seriousness;
        this.crime_count = crime_count;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getSeriousness() {
        return seriousness;
    }

    public void setSeriousness(int seriousness) {
        this.seriousness = seriousness;
    }

    public int getCrime_count() {
        return crime_count;
    }

    public void setCrime_count(int crime_count) {
        this.crime_count = crime_count;
    }
    
    
    
}
