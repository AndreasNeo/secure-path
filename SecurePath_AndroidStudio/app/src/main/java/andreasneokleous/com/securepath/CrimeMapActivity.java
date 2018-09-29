package andreasneokleous.com.securepath;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import android.Manifest;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Andreas Neokleous.
 */

public class CrimeMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener {

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 99;
    private GoogleMap mMap;


    public static final String CRIME_REQUEST = "CrimeRequest";
    private RequestQueue mRequestQueue;

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;


    private ArrayList<Crime>crimesToBeAdded  = new ArrayList<>();
    private HashMap<Integer, Marker> visibleMarkers = new HashMap<>();


    //Bottom sheet
    private LinearLayout layoutBottomSheet;
    private BottomSheetBehavior sheetBehavior;
    private ImageButton closeSheet;
    private ImageButton openSheet;
    private TextView titleText;
    private ListView bottomList;
    private FloatingActionMenu fabDirections;
    private com.github.clans.fab.FloatingActionButton fabLocation, fabSpark, fabPolice, fabPredict, fabInfo, fabMap;

    private PlaceAutocompleteFragment autocompleteSearch;
    private  StreetViewPanorama mStreetViewPanorama;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_map);

        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                .findFragmentById(R.id.streetviewpanorama);

        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
                        mStreetViewPanorama = streetViewPanorama;
                        if (savedInstanceState == null){
                            mStreetViewPanorama.setPosition(new LatLng(51.236134, -0.570950));
                        }
                    }
                }
        );
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mRequestQueue = Volley.newRequestQueue(this);

        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        closeSheet = findViewById(R.id.closeSheet);
        openSheet = findViewById(R.id.openSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        titleText = layoutBottomSheet.findViewById(R.id.bottom_title);
        bottomList = layoutBottomSheet.findViewById(R.id.bottom_list);

        fabDirections = findViewById(R.id.fab_menu);
        fabSpark = findViewById(R.id.fab_dir_spark);
        fabPolice = findViewById(R.id.fab_dir_police);
        fabPredict = findViewById(R.id.fab_dir_predict);
        fabLocation = findViewById(R.id.fab_location);
        fabInfo = findViewById(R.id.fab_info);
        fabMap = findViewById(R.id.fab_map);

        autocompleteSearch = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_search);
        autocompleteSearch.setHint("Search for a location");
        autocompleteSearch.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition( new CameraPosition.Builder()
                        .target(place.getLatLng())
                        .zoom(17)
                        .build()));
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }

            @Override
            public void onError(Status status) {

            }
        });

    sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if ( newState == BottomSheetBehavior.STATE_DRAGGING){
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            if ( newState == BottomSheetBehavior.STATE_EXPANDED){
                openSheet.setImageDrawable(ContextCompat.getDrawable(CrimeMapActivity.this, android.R.drawable.arrow_down_float));
            }
            if ( newState == BottomSheetBehavior.STATE_COLLAPSED){
                openSheet.setImageDrawable(ContextCompat.getDrawable(CrimeMapActivity.this, android.R.drawable.arrow_up_float));
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); //Set map type to normal
        mMap.getUiSettings().setZoomControlsEnabled(false); //Enable zooming buttons on the map
        mMap.getUiSettings().setCompassEnabled(true); //Enable the compass on the map
        mMap.getUiSettings().setMapToolbarEnabled(false); //Disable the Toolbar of the map

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            setCameraToMyLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }

        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);

        onClickActions();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.v("MARKERCLICK", String.valueOf(marker.getPosition()));
                mStreetViewPanorama.setPosition(marker.getPosition());
                if (fabDirections.isOpened())
                fabDirections.close(true);
                 ArrayList<Crime> crimeOnMarker = new ArrayList<>();
                         for (Crime crime : crimesToBeAdded) {
                            if (crime.getmPosition().equals(marker.getPosition())) {
                                crimeOnMarker.add(crime);
                            }
                        }
                        if (crimeOnMarker.size()>1) {
                            titleText.setText(crimeOnMarker.size() + " Crimes " + crimeOnMarker.get(0).getmStreet());
                        }else if (crimeOnMarker.size()==1){
                            titleText.setText(crimeOnMarker.size() + " Crime " + crimeOnMarker.get(0).getmStreet());
                        }
                CrimeMarkerInfo_ListAdapter crimeMarkerInfoListAdapter = new CrimeMarkerInfo_ListAdapter(CrimeMapActivity.this,crimeOnMarker);
                crimeMarkerInfoListAdapter.notifyDataSetChanged();
                bottomList.setAdapter(crimeMarkerInfoListAdapter);

                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return true;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            }
        });

    }



    private void httpRequest(String url, final String displayMode) {
        Log.v("JSON_Test", url);
        crimesToBeAdded.clear();
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject crime = response.getJSONObject(i);
                                JSONObject crimeLocation = crime.getJSONObject("location");

                                String crimeOutcome = "null";

                                if (!crimeOutcome.equals(crime.getString("outcome_status"))){
                                    JSONObject crimeOutcomeStatus = crime.getJSONObject("outcome_status");
                                    crimeOutcome = crimeOutcomeStatus.getString("category");
                                }else {
                                    crimeOutcome = "Outcome Unknown";
                                }

                                int crimeId = crime.getInt("id");
                                Double crimeLat = crimeLocation.getDouble("latitude");
                                Double crimeLng = crimeLocation.getDouble("longitude");
                                String crimeStreet = crimeLocation.getJSONObject("street").getString("name");
                                String crimeCategory = crime.getString("category");

                                Log.v("JSON_Test", "Category: "+crimeCategory );
                                Log.v("JSON_Test", "Latitude: "+String.valueOf(crimeLat)  + ", Longitude: " + String.valueOf(crimeLng));
                                Log.v("JSON_Test", "Street: "+crimeStreet );
                                Log.v("JSON_Test", "Outcome: "+crimeOutcome );
                                Log.v("JSON_Test", "Crime ID: " + String.valueOf(crimeId));


                                Crime crime1 = new Crime(crimeId, crimeLat, crimeLng, crimeStreet, crimeCategory, crimeOutcome);

                                crimesToBeAdded.add(crime1);

                            }
                            if (displayMode == "marker") {
                                drawCrimeMarker(crimesToBeAdded);
                            }
                            if (displayMode == "heatmap") {
                                drawCrimeHeatMap(crimesToBeAdded);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (isNetworkAvailable()) {
                            Toast.makeText(CrimeMapActivity.this, "Too many crimes to load. Please zoom in!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(CrimeMapActivity.this, "Please connect to the internet", Toast.LENGTH_SHORT).show();

                        }
                    }

                }){

        };


        jsArrayRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsArrayRequest.setTag(CRIME_REQUEST);

        mRequestQueue.add(jsArrayRequest);
    }

    private void drawCrimeMarker(ArrayList<Crime> list) {
        if (mOverlay!=null)
            mOverlay.remove();
        LatLngBounds bounds = this.mMap.getProjection().getVisibleRegion().latLngBounds;
        for (Crime crime : list) {
            if (bounds.contains(crime.getmPosition())) {
                if (!visibleMarkers.containsKey(crime.getmId())) {
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .visible(true)
                            .draggable(false)
                            .position(crime.getmPosition()));
                    this.visibleMarkers.put(crime.getmId(), marker);
                }
            }
        }
    }

    private void drawCrimeHeatMap(ArrayList<Crime> list) {
        visibleMarkers.clear();
        Collection<LatLng> heatMapData = new ArrayList<>();
        for (int i=0;i<list.size();i++){
            heatMapData.add( list.get(i).getmPosition());
        }
        if (!heatMapData.isEmpty()) {
            mProvider = new HeatmapTileProvider.Builder().data(heatMapData).build();
            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }
            if (mOverlay!=null)
            mOverlay.clearTileCache();
    }


    @Override
    public void onCameraIdle() {

        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        double northwestLat = bounds.northeast.latitude;
        double northwestLng = bounds.southwest.longitude;

        double southeastLat = bounds.southwest.latitude;
        double southeastLng = bounds.northeast.longitude;

        LatLng northwest = new LatLng(northwestLat, northwestLng);
        LatLng northeast = bounds.northeast;
        LatLng southeast = new LatLng(southeastLat, southeastLng);
        LatLng southwest = bounds.southwest;

        String url = "https://data.police.uk/api/crimes-street/all-crime?poly="
                + northwest.latitude + "," + northwest.longitude + ":"
                + northeast.latitude + "," + northeast.longitude + ":"
                + southeast.latitude + "," + southeast.longitude + ":"
                + southwest.latitude + "," + southwest.longitude;



        //Display markers or heatmap based on zoom level
        if (mMap.getCameraPosition().zoom > 15) {
            removeMarkerOutOfBounds(bounds);
            httpRequest(url, "marker");
        } else if (mMap.getCameraPosition().zoom < 15) {
            mMap.clear();
            httpRequest(url, "heatmap");
        }
    }

    private void removeMarkerOutOfBounds( LatLngBounds bounds){
        if (crimesToBeAdded != null && crimesToBeAdded.size()>0) {
            for (Crime crime : crimesToBeAdded) {
                if (!bounds.contains(crime.getmPosition())) {
                    if (visibleMarkers.containsKey(crime.getmId())) {
                        visibleMarkers.get(crime.getmId()).remove();
                        visibleMarkers.remove(crime.getmId());
                    }
                }
            }

        }
    }

    //Cancel Request onStop
    @Override
    protected void onStop() {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(CRIME_REQUEST);
        }
    }

    //Cancel Request on Camera Move
    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            if (sheetBehavior.getState() != sheetBehavior.STATE_HIDDEN)
             sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            if (mRequestQueue != null)
                mRequestQueue.cancelAll(CRIME_REQUEST);

        }
    }

   private void onClickActions() {
       closeSheet.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED || sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
                   sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
               }
           }
       });
       openSheet.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
                   sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
               }
               if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                   sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
               }
           }
       });
       fabMap.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
                   mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
               }

               else if (mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE){
                   mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
               }
           }
       });
       fabInfo.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               InfoDialog infoDialog = new InfoDialog();
               infoDialog.create(CrimeMapActivity.this).show();
           }
       });
       fabLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fabDirections.close(true);
                        setCameraToMyLocation();
                    }
                });

       fabPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabDirections.close(true);
                Intent mapDirections = new Intent(CrimeMapActivity.this, DirectionsPoliceActivity.class);
                startActivity(mapDirections);

            }
        });

       fabSpark.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               fabDirections.close(true);
               Intent mapDirections = new Intent(CrimeMapActivity.this, DirectionsSparkActivity.class);
               startActivity(mapDirections);

           }
       });

       fabPredict.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               fabDirections.close(true);
            //   Intent mapDirections = new Intent(CrimeMapActivity.this, DirectionsPredictActivity.class);
              // startActivity(mapDirections);
           }
       });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {

                        mMap.setMyLocationEnabled(true);
                        setCameraToMyLocation();
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private void setCameraToMyLocation() {
        Location location = getLastKnownLocation();
         if (location != null) {
          //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }else{
             Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
         }
    }

    private Location getLastKnownLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

    Location location =null;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

           location  = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }

        return location;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    private static class InfoDialog{
        public static AppCompatDialog create(Context context) {

            float dpi = context.getResources().getDisplayMetrics().density;

            final TextView message = new TextView(context);
            final SpannableString s =
                    new SpannableString(context.getText(R.string.info));
            Linkify.addLinks(s, Linkify.WEB_URLS);
            message.setText(s);
            message.setMovementMethod(LinkMovementMethod.getInstance());


            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle("Data Information")
                    .setCancelable(true)
                    .setPositiveButton("Dismiss", null)
                    ;
            @SuppressLint("RestrictedApi") AppCompatDialog dialog = builder.setView(message,(int)(19*dpi), (int)(5*dpi), (int)(14*dpi), (int)(5*dpi)).create();
            return dialog;
        }
    }

    @Override
    public void onBackPressed() {
        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else{
        super.onBackPressed();}
    }

}

