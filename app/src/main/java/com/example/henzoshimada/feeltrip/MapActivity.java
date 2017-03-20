package com.example.henzoshimada.feeltrip;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
<<<<<<< HEAD
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
=======
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
>>>>>>> b68fe67afe72ee34bcd4bd4a4dcb073174b4a35a
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

//http://www.latlong.net/
//(PermissionUtils.java, MyLocationDemoActivity.java)https://github.com/googlemaps/android-samples
//https://developers.google.com/maps/documentation/android-api/current-place-tutorial
public class MapActivity extends FragmentActivity
        implements
        OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;
    private static Location mLastKnownLocation;

    private CameraPosition mCameraPosition;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";


    private ArrayList<Mood> moodArrayList = new ArrayList<Mood>();

    private Marker mSelectedMarker;

    //creates and sets up map
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d("mapTag","on create");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_map);

        //testing get array of moods
        try {
            Log.d("mapTag", "before");
            testCreateMoodArray();
            Log.d("mapTag", "after");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        mGoogleApiClient = new GoogleApiClient.Builder(this)


                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragent_frame);
        mapFragment.getMapAsync(this);
    }

<<<<<<< HEAD
    /*
    @Override
    protected void onResume(){
        super.onResume();
        Log.d("mapTag","on resume");
        if (mLastKnownLocation != null) {
            Log.d("mapTag", "use last known location");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), 15));
            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()))
                    .radius(5000) //in meters
                    .strokeColor(Color.RED));
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

    }
*/
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

=======
>>>>>>> b68fe67afe72ee34bcd4bd4a4dcb073174b4a35a
    //called when map is ready
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        // Set listener for marker click event.  See the bottom of this class for its behavior.
        mMap.setOnMarkerClickListener(this);
        // Set listener for map click event.  See the bottom of this class for its behavior.
        mMap.setOnMapClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                //use default InfoWindow frame
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Log.d("mapTag", "get info contents");
                View view = getLayoutInflater().inflate(R.layout.info_window_layout, null);
                view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)); //width, height

                int index = Integer.parseInt(marker.getSnippet());
                Mood mood = moodArrayList.get(index);
                TextView usernameView = (TextView) view.findViewById(R.id.person);
                TextView social_situationView = (TextView) view.findViewById(R.id.social_situation);
                usernameView.setText(mood.getUser());
                //social_situationView.setText(mood.getSocialSit());

                return view;
            }
        });

        enableMyLocation();
        setMoodMarker();
    }

    private void setMoodMarker(){
        //Log.d("mapTag", "set mood marker");
        if (mMap != null) {
            Mood mood;
            Marker marker;
            //Log.d("mapTag","mood array size: "+moodArrayList.size());
            for(int i = 0; i < moodArrayList.size(); i++){
                mood = moodArrayList.get(i);
                //get longitude
                //get latitude
                marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(53.528033+i, -113.525355+i))
                        .snippet(String.valueOf(i)));
                //Log.d("mapTag", "i= "+String.valueOf(i));
            }
        }
    }
    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    //https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.OnMyLocationButtonClickListener
    //called when pressing  my location button
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return false;
        }
        //get last known location todo handle when location or wifi is off
        mLastKnownLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        if (mLastKnownLocation != null) {
            Log.d("mapTag", "Lat= " + String.valueOf(mLastKnownLocation.getLatitude()) + " and Long= " + String.valueOf(mLastKnownLocation.getLongitude()));

            //5km radius circle for debug
            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()))
                    .radius(5000) //in meters
                    .strokeColor(Color.RED));
        } else {
            Toast.makeText(this, "Please turn on Location Service and retry", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
            Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
        Log.d("mapTag","on resume fragments");
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
        .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    private void testCreateMoodArray() throws ExecutionException, InterruptedException {
        ElasticSearchController.GetMoodTask getMoodTask = new ElasticSearchController.GetMoodTask();
        getMoodTask.execute("user");
        moodArrayList.addAll(getMoodTask.get());

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // The user has re-tapped on the marker which was already showing an info window.
        Log.d("markerTag","marker clicked");
        if (marker.equals(mSelectedMarker)) {
            Log.d("markerTag","equal");
            // The showing info window has already been closed - that's the first thing to happen
            // when any marker is clicked.
            // Return true to indicate we have consumed the event and that we do not want the
            // the default behavior to occur (which is for the camera to move such that the
            // marker is centered and for the marker's info window to open, if it has one).
            mSelectedMarker = null;
            return true;
        }
        Log.d("markerTag","not equal");
        mSelectedMarker = marker;

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur.
        return false;
    }

    @Override
    public void onMapClick(final LatLng point) {
        // Any showing info window closes when the map is clicked.
        // Clear the currently selected marker.
        mSelectedMarker = null;
    }
}
