package com.example.henzoshimada.feeltrip;
// removed unused imports, may slow down build

import android.*;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditMoodActivity extends AppCompatActivity {

    private EditText inputMoodDescription;
    private boolean locationOn;
    private boolean showPublicOn;
    private Spinner emotionalStateSpinner;
    private Spinner socialSituationSpinner;

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastKnownLocation;


    DateFormat formatDateTime = DateFormat.getDateInstance();
    private Calendar dateTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_page);
        inputMoodDescription = (EditText) findViewById(R.id.moodEventDescription);
/*
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
        enableMyLocation();
*/
        addItemsOnEmotionalStateSpinner();
        addItemsOnSocialSituationSpinner();
        Button submitButton = (Button) findViewById(R.id.post_mood_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //submitMood();
            }
        });

        ToggleButton toggleLocationButton = (ToggleButton) findViewById(R.id.toggle_location);
        toggleLocationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleLocation(isChecked, buttonView);
            }
        });

        ToggleButton togglePublicButton = (ToggleButton)findViewById(R.id.toggle_public);
        togglePublicButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    showPublicOn = true;
                } else {
                    // The toggle is disabled
                    showPublicOn = false;
                }
                Log.d("myTag","show public is: "+String.valueOf(showPublicOn));
            }
        });


        Button selectImageButton = (Button) findViewById(R.id.select_image);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        Button selectDateButton = (Button) findViewById(R.id.select_date);
        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selectDate();
                new DatePickerDialog(v.getContext(), datePickerDialogListener, dateTime.get(Calendar.YEAR),
                        dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

/*
    private Mood submitMood(){
    //create mood obj
    //create elastic search.addmoodtask
    //addmoodtask.execute(mood)
    //handle queue if offline

    }
*/

    private void selectImage(){
        Toast.makeText(getApplicationContext(), "Get Image", Toast.LENGTH_SHORT).show();
    }



    //https://www.youtube.com/watch?v=8mFW6dA5xDE
    DatePickerDialog.OnDateSetListener datePickerDialogListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, month);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Log.d("myTag","Date: "+formatDateTime.format(dateTime.getTime()));
        }
    };

    private void addItemsOnEmotionalStateSpinner(){
        emotionalStateSpinner = (Spinner) findViewById(R.id.emotional_state_spinner);
        List<String> emotionalStateList = new ArrayList<>();
        emotionalStateList.add("Anger");
        emotionalStateList.add("Confusion");
        emotionalStateList.add("Disgust");
        emotionalStateList.add("Fear");
        emotionalStateList.add("Happiness");
        emotionalStateList.add("Sadness");
        emotionalStateList.add("Shame");


        ArrayAdapter<String> emotionalStateAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, emotionalStateList);
        emotionalStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emotionalStateSpinner.setAdapter(emotionalStateAdapter);
    }


    private void addItemsOnSocialSituationSpinner(){
        socialSituationSpinner = (Spinner) findViewById(R.id.social_event_spinner);
        List<String> socialSituationList = new ArrayList<>();

        socialSituationList.add("Alone");
        socialSituationList.add("With one other person");
        socialSituationList.add("With two to several people");
        socialSituationList.add("With a crowd");


        ArrayAdapter<String> socialSituationAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, socialSituationList);
        socialSituationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socialSituationSpinner.setAdapter(socialSituationAdapter);
    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    private void toggleLocation(boolean isChecked, CompoundButton button){
        if (isChecked) {
            // The toggle is enabled
            locationOn = true;
            enableMyLocation();
            Log.d("myTag", "try to get location");
            GPSLocation gps = new GPSLocation(EditMoodActivity.this);

            // Check if GPS enabled
            if (gps.canGetLocation()) {

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                // \n is for new line
                Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.
                gps.showSettingsAlert();
                button.toggle();
            }
        } else {
            // The toggle is disabled
            locationOn = false;
        }
        Log.d("myTag", "location on is: " + String.valueOf(locationOn));
    }

}
