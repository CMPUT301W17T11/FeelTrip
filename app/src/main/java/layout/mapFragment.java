package layout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
//import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henzoshimada.feeltrip.ElasticSearchController;
import com.example.henzoshimada.feeltrip.FeelTripApplication;
import com.example.henzoshimada.feeltrip.Mood;
import com.example.henzoshimada.feeltrip.Participant;
import com.example.henzoshimada.feeltrip.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


//18 March 2017 http://stackoverflow.com/questions/19353255/how-to-put-google-maps-v2-on-a-fragment-using-viewpager
public class mapFragment extends Fragment implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener{
    // ActivityCompat.OnRequestPermissionsResultCallback{

    MapView mMapView;


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
    private boolean permissionDenied = false;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private GoogleMap mMap;


    private GoogleApiClient mGoogleApiClient;
    private Location mLastKnownLocation;

    private static final String frag = "map";

    private CameraPosition mCameraPosition;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private ArrayList<Mood> moodArrayList = new ArrayList<Mood>();

    private Marker mSelectedMarker;
    private Participant participant;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        //verifyLocationPermissions(getActivity());
        View locationButton = ((View) mMapView.findViewById(1).getParent()).findViewById(2);

        // and next place it, for exemple, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 80, 200); // left, top, right, bottom

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.d("mapTag","on create");
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.fragment_map);

        //testing get array of moods

        if(!FeelTripApplication.getFrag().equals(frag)) {
            FeelTripApplication.setFrag(frag); //TODO: Put this wherever we initialize the fragment, I think here works fine.
        }


        participant = FeelTripApplication.getParticipant();


        /*
        try {
            Log.d("mapTag", "before");
            testCreateMoodArray();
            Log.d("mapTag", "after");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/
        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

    }

    //called when map is ready
    @Override
    public void onMapReady(GoogleMap map) {
        Log.d("markerTag","on map ready");
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
                //return null;
                View view = getActivity().getLayoutInflater().inflate(R.layout.info_window_layout, null);
                view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)); //width, height
                int index = Integer.parseInt(marker.getSnippet());
                Mood mood = moodArrayList.get(index);
                TextView usernameView = (TextView) view.findViewById(R.id.info_window_person);
                TextView descriptionView = (TextView) view.findViewById(R.id.info_window_description);
                TextView social_situationView = (TextView) view.findViewById(R.id.info_window_socialSituation);
                TextView dateView = (TextView) view.findViewById(R.id.info_window_date);
                TextView feelingView = (TextView) view.findViewById(R.id.info_window_append);
                ImageView emojiView = (ImageView) view.findViewById(R.id.info_window_emojiImage);
                ImageView imageView = (ImageView) view.findViewById(R.id.info_window_image);

                usernameView.setText(mood.getUsername());
                descriptionView.setText(mood.getDescription());
                social_situationView.setText(mood.getSocialSit());
                dateView.setText(mood.getDate().toString());
                feelingView.setText(" - Feeling " + mood.getEmotionalState());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int emojiID = getContext().getApplicationContext().getResources().getIdentifier("emoji" + String.valueOf(mood.getEmoji()),"drawable",getContext().getApplicationContext().getPackageName());
                    if(emojiID != 0) {
                        emojiView.setImageResource(emojiID);
                    }
                    else { // This field can only be accessed if something goes wrong, or if someone alters the main database. It's mainly a fallback safety.
                        emojiView.setImageResource(getContext().getApplicationContext().getResources().getIdentifier("err","drawable",getContext().getApplicationContext().getPackageName()));
                    }
                }
                //######################################################
                String encodedImageString = mood.getImage();
                if(encodedImageString != null) {
                    byte[] decodedString = Base64.decode(encodedImageString, Base64.DEFAULT);
                    Log.d("Bitmap","Length: "+decodedString.length);
                    Bitmap photo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageView.setImageBitmap(photo);
                    Log.d("imageTag", "have image");
                }else {
                    imageView.setImageBitmap(null);
                    imageView.setVisibility(View.GONE);
                    Log.d("imageTag", "no image");
                }

                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                /*
                View view = getActivity().getLayoutInflater().inflate(R.layout.info_window_layout, null);
                view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)); //width, height
                int index = Integer.parseInt(marker.getSnippet());
                Mood mood = moodArrayList.get(index);
                TextView usernameView = (TextView) view.findViewById(R.id.info_window_person);
                TextView descriptionView = (TextView) view.findViewById(R.id.info_window_description);
                TextView social_situationView = (TextView) view.findViewById(R.id.info_window_socialSituation);
                TextView dateView = (TextView) view.findViewById(R.id.info_window_date);
                TextView feelingView = (TextView) view.findViewById(R.id.info_window_append);
                ImageView emojiView = (ImageView) view.findViewById(R.id.info_window_emojiImage);
                ImageView imageView = (ImageView) view.findViewById(R.id.info_window_image);

                usernameView.setText(mood.getUsername());
                descriptionView.setText(mood.getDescription());
                social_situationView.setText(mood.getSocialSit());
                dateView.setText(mood.getDate().toString());
                feelingView.setText(mood.getEmotionalState());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int emojiID = getContext().getApplicationContext().getResources().getIdentifier("emoji" + String.valueOf(mood.getEmoji()),"drawable",getContext().getApplicationContext().getPackageName());
                    if(emojiID != 0) {
                        emojiView.setImageResource(emojiID);
                    }
                    else { // This field can only be accessed if something goes wrong, or if someone alters the main database. It's mainly a fallback safety.
                        emojiView.setImageResource(getContext().getApplicationContext().getResources().getIdentifier("err","drawable",getContext().getApplicationContext().getPackageName()));
                    }
                }
                //######################################################
                String encodedImageString = mood.getImage();
                if(encodedImageString != null) {
                    byte[] decodedString = Base64.decode(encodedImageString, Base64.DEFAULT);
                    Log.d("Bitmap","Length: "+decodedString.length);
                    Bitmap photo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageView.setImageBitmap(photo);
                    Log.d("imageTag", "have image");
                }else {
                    imageView.setImageBitmap(null);
                    imageView.setVisibility(View.GONE);
                    Log.d("imageTag", "no image");
                }

                return view;
                */
                return null;
            }
        });

        verifyLocationPermissions(getActivity());
        try{
            //Log.d("mapFragCamTag","long lat: "+participant.getLatitude()+" "+participant.getLongitude());
            if ((participant.getLatitude() != 0.0) && (participant.getLongitude() != 0.0)){
                //Log.d("mapFragCamTag","move");
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(participant.getLatitude(), participant.getLongitude())));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }//else{Log.d("mapFragCamTag","no move");}

        }catch (NullPointerException e){
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setMoodMarker();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setMoodMarker(){

        //get array for setting marker, make sure participant location is set
        testCreateMoodArray();

        Log.d("mapTag","set marker");
        if (mMap != null) {
            mMap.clear();
            Log.d("mapTag","inside set marker");
            Mood mood;
            Marker marker;


            Log.d("mapTag","mood array size: "+moodArrayList.size());
            for(int i = 0; i < moodArrayList.size(); i++){
                mood = moodArrayList.get(i);

                int emojiID; //R.drawable.emoji1

                try {
                    int emojierr = getContext().getApplicationContext().getResources().getIdentifier("emoji" + String.valueOf(mood.getEmoji()), "drawable", getContext().getApplicationContext().getPackageName());
                    if (emojierr != 0) {
                        emojiID = emojierr;
                    } else { // This field can only be accessed if something goes wrong, or if someone alters the main database. It's mainly a fallback safety.
                        emojiID = getContext().getApplicationContext().getResources().getIdentifier("err", "drawable", getContext().getApplicationContext().getPackageName());
                    }

                Bitmap emojiBitmap = BitmapFactory.decodeResource(getResources(), emojiID);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(emojiBitmap, 50, 50, false);

                    marker = mMap.addMarker(new MarkerOptions()
                            //.position(new LatLng(53.528033+i, -113.525355+i))
                            .position(new LatLng(mood.getLatitude(), mood.getLongitude()))
                            .snippet(String.valueOf(i))
                            //.icon(BitmapDescriptorFactory.fromResource(emojiID))
                            .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap))
                    );
                } catch (NullPointerException e) {
                    Log.d("permTag", "NULLPOINTER"); //TODO: Handle
                }

                //Log.d("mapTag", "i= "+String.valueOf(i));
            }
        }

    }

    //https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.OnMyLocationButtonClickListener
    //called when pressing  my location button
    @Override
    public boolean onMyLocationButtonClick() {
        Log.d("mapTag", "my locaiton button clicked");
        Toast.makeText(getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

            //update participant last known location
            participant.setLongitude(mLastKnownLocation.getLongitude());
            participant.setLatitude(mLastKnownLocation.getLatitude());

            ElasticSearchController.EditParticipantTask editParticipantTask = new ElasticSearchController.EditParticipantTask("geoLocation");
            editParticipantTask.execute(participant);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setMoodMarker();
            }

            //5km radius circle for debug
            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()))
                    .radius(5000) //in meters
                    .strokeColor(Color.RED));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        } else {
            Toast.makeText(getActivity(), "Please turn on Location Service and retry", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //must be called after participant location is set
    private void testCreateMoodArray() {
        //FeelTripApplication.loadFromElasticSearch(); THIS LINE ISN'T NEEDED DUE TO THE WAY WE CALL mapFragment
        try {
            FeelTripApplication.setLatitude(participant.getLatitude());
            FeelTripApplication.setLongitude(participant.getLongitude());
        }catch (NullPointerException e){

        }

        FeelTripApplication.loadFromElasticSearch();
        moodArrayList = FeelTripApplication.getMoodArrayList();
        Log.d("mapTag","test size: "+moodArrayList.size());
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

    public void verifyLocationPermissions(Activity activity) {
        Log.d("permTag", "in verify perm");
        // Check if we have location permission
        try {
            int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                permissionDenied = true;
                Log.d("permTag", "verify: permissionDenied");
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(activity, PERMISSIONS_LOCATION, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                permissionDenied = false;
                if (mMap != null) {
                    // Access to the location has been granted to the app.
                    mMap.setMyLocationEnabled(true);
                }
            }

        } catch (NullPointerException e) {
            Log.d("permTag", "verify: NULLPOINTER"); //TODO: Handle
        }

    }



}