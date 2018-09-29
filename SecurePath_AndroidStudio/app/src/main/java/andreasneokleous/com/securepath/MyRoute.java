package andreasneokleous.com.securepath;

import android.support.annotation.NonNull;

import com.directions.route.Route;

/**
 * Created by Andreas Neokleous.
 */

public class MyRoute implements Comparable<MyRoute> {
    private int crimesOnRoute;
    private int seriousness;
    private  Route route;

    public int getSeriousness() {
        return seriousness;
    }

    public MyRoute(Route route, int crimesOnRoute){
        super();
        this.crimesOnRoute = crimesOnRoute;
        this.route = route;
    }
    public MyRoute(Route route, int crimesOnRoute, int seriousness){
        super();
        this.crimesOnRoute = crimesOnRoute;
        this.route = route;
        this.seriousness = seriousness;
    }

    public int getCrimesOnRoute() {
        return crimesOnRoute;
    }


    public Route getRoute() {
        return route;
    }


    @Override
    public int compareTo(@NonNull MyRoute o) {
        return String.valueOf(o.getCrimesOnRoute()).compareTo(String.valueOf(this.crimesOnRoute));
    }
}
