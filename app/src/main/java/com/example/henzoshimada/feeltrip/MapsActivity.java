package com.example.henzoshimada.feeltrip;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Class for editing the location of a mood
 */
public class MapsActivity extends FragmentActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener {

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

    private View mMapView;

    private GoogleApiClient mGoogleApiClient;
    private static Location mLastKnownLocation;

    private CameraPosition mCameraPosition;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private Marker marker;

    private Marker mSelectedMarker;

    private double longitude ;
    private double latitude;

    private Participant participant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        longitude = intent.getDoubleExtra("currentLong", 0);
        latitude = intent.getDoubleExtra("currentLat", 0);

        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        mGoogleApiClient = new GoogleApiClient.Builder(this)


                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapActivity);
        mMapView = mapFragment.getView();

        mapFragment.getMapAsync(this);

        participant = FeelTripApplication.getParticipant();

        FloatingActionButton submit = (FloatingActionButton)  findViewById(R.id.submit_loc);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeLocation();
            }
        });

        FloatingActionButton cancel = (FloatingActionButton)  findViewById(R.id.cancel_loc);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED,intent);
                finish();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationButtonClickListener(this);
                View locationButton = ((View) mMapView.findViewById(1).getParent()).findViewById(2);
                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                // position on right bottom
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                rlp.setMargins(0, 0, 80, 200); // left, top, right, bottom
            }
        }
        if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setOnMarkerDragListener(this);
            mMap.setOnMapLongClickListener(this);
            try {
                //set marker to where mood is posted
                if ((longitude != 0.0) && (latitude != 0.0)) {
                    Log.d("markerTag", "first time lat, long: " + latitude + " " + longitude);
                    LatLng lastLoc = new LatLng(latitude, longitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLoc));
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(lastLoc)
                            .draggable(true)
                    );
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                } else{
                    if ((participant.getLongitude() != 0.0) && (participant.getLatitude() != 0.0)) {
                        latitude = participant.getLatitude();
                        longitude = participant.getLongitude();
                        LatLng lastLoc = new LatLng(latitude, longitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLoc));
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(lastLoc)
                                .draggable(true)
                        );
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    }
                }
                Log.d("camTag","success"+latitude+" "+longitude);
            }catch (NullPointerException e) {
                Log.d("camTag","fail");
            }
        }

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


    }

    //https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.OnMyLocationButtonClickListener
    //called when pressing  my location button
    @Override
    public boolean onMyLocationButtonClick() {
        Log.d("mapATag","location clicked");
        Toast.makeText(getApplicationContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
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
            Log.d("mapATag", "Lat= " + String.valueOf(mLastKnownLocation.getLatitude()) + " and Long= " + String.valueOf(mLastKnownLocation.getLongitude()));
            longitude = mLastKnownLocation.getLongitude();
            latitude = mLastKnownLocation.getLatitude();

            //update participant last known location
            participant.setLatitude(latitude);
            participant.setLongitude(longitude);

            ElasticSearchController.EditParticipantTask editParticipantTask = new ElasticSearchController.EditParticipantTask("geoLocation");
            editParticipantTask.execute(participant);

            if (marker != null){
                marker.remove();
            }
            marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()))
                    .draggable(true)
                    );
        } else {
            Log.d("mapATag","location null");
            Toast.makeText(getApplicationContext(), "Please turn on Location Service and retry", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    public void onMarkerDrag(Marker arg0) {
        // Auto-generated method stub
        // Log.d("info","onMarkerDrag");
    }

    @Override
    public void onMarkerDragEnd(Marker arg0) {
        //Log.d("info","onMarkerDragEnd");
        // Auto-generated method stub
        LatLng dragPosition = arg0.getPosition();
        latitude = dragPosition.latitude;
        longitude = dragPosition.longitude;

        //Toast.makeText(getApplicationContext(), "Marker Dragged..!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMarkerDragStart(Marker arg0) {
        //Log.d("info","onMarkerDragStart");
        // TODO Auto-generated method stub
    }

    /**
     * Called when submit button is called, set longitude and latitude as result for call back in EditMoodActivity
     *
     */
    private void storeLocation(){
        Log.d("locTag","store location");
        Intent intent = new Intent();
        Log.d("locTag","store loc long = "+String.valueOf(longitude));
        Log.d("locTag","store loc lat = "+String.valueOf(latitude));
        intent.putExtra("resultLong",longitude);
        intent.putExtra("resultLat",latitude);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (marker != null){
            marker.remove();
        }
        marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
        );
        longitude = latLng.longitude;
        latitude = latLng.latitude;
    }
}
