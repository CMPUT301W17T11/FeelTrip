package com.example.henzoshimada.feeltrip;
// removed unused imports, may slow down build

import android.*;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

    private String emotionalState;
    private String socialSit;

    // The following are constants for emotion based emoji
    public static final int angry = 0x1F624;
    public static final int confused = 0x1F635;
    public static final int disgusted = 0x1F623; //might change this one...
    public static final int fearful = 0x1F628;
    public static final int happy = 0x1F60A;
    public static final int sad = 0x1F622;
    public static final int shameful = 0x1F61E;
    public static final int cool = 0x1F60E;
    public static final int somethingwentwrong = 0x1F31A;

    // Used for taking a photo
    private ByteArrayOutputStream photoStream = new ByteArrayOutputStream();
    private String encodedPhoto;
    private String imagePathAndFileName;
    Uri imageFileUri;
    private static int TAKE_PHOTO = 2;
    Activity activity;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_page);
        inputMoodDescription = (EditText) findViewById(R.id.moodEventDescription);
        activity = this;
        context = this;
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
        addListenerOnSubmitButton();


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


        Button selectImageButton = (Button) findViewById(R.id.take_photo);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int loginPermissionCheck = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA);
                if (loginPermissionCheck != PackageManager.PERMISSION_GRANTED) {
                    Log.d("tag", "Camera permission not granted");
                    ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.CAMERA}, 1);
                } else {
                    Log.d("tag", "Camera permission granted");
                    takeAPhoto();
                }

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

    private void submitMood() throws DescriptionTooLongException {
        ElasticSearchController.AddMoodTask addMoodTask = new ElasticSearchController.AddMoodTask();
        Participant participant = FeelTripApplication.getParticipant();
        Mood mood = new Mood(participant.getUserName());
        if(showPublicOn){
            mood.setPublic();
        }
        else {
            mood.setPrivate();
        }
        if(locationOn) {
            //TODO: mood.setMapPosition();
            // this is the setter for latitude and longitude
        }
        mood.setEmotionalState(emotionalState);
        mood.setSocialSit(socialSit);
        mood.setDescription(String.valueOf(inputMoodDescription.getText()), " -Feeling " + emotionalState); //TODO: We might not need to send this append part to Elasticsearch, but rather add it to our displayed description after we fetch from Elasticsearch
        mood.setDate(dateTime.getTime());
        mood.setImage(encodedPhoto);
        addMoodTask.execute(mood);
    }

    public int emojiUnicode(String emotion) {
        switch(emotion) {
            case "Angry":
                return angry;
            case "Confused":
                return confused;
            case "Disgusted":
                return disgusted;
            case "Fearful":
                return fearful;
            case "Happy":
                return happy;
            case "Sad":
                return sad;
            case "Shameful":
                return shameful;
            case "Cool":
                return cool;
            default:
                return somethingwentwrong; // the secret "only accessible by hacks" face
        }
    }

    public String getEmojiByUnicode(int unicode){
        if (unicode == 0) {
            return "";
        }
        return new String(Character.toChars(unicode));
    }

/*
    private Mood submitMood(){
    //create mood obj
    //create elastic search.addmoodtask
    //addmoodtask.execute(mood)
    //handle queue if offline

    }
*/

    private void takeAPhoto(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Camera";
        File folder = new File(path);
        if (!folder.exists())
            folder.mkdir();
        imagePathAndFileName = path + File.separator + String.valueOf(System.currentTimeMillis()) + ".jpg";
        File imageFile = new File(imagePathAndFileName);
        imageFileUri = Uri.fromFile(imageFile);

        Log.d("tag", "Can make file path?");
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        Log.d("tag", "Take photo");
        startActivityForResult(intent, TAKE_PHOTO);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        Log.d("tag", "In Take Photo");
        if (requestCode == TAKE_PHOTO){

            if (resultCode == RESULT_OK){
                Log.d("tag", "Taking photo");

                Bitmap photo = (Bitmap) intent.getExtras().get("data");
                photo.compress(Bitmap.CompressFormat.JPEG, 100, photoStream);
                byte [] compressedPhoto = photoStream.toByteArray();
                encodedPhoto = Base64.encodeToString(compressedPhoto, Base64.DEFAULT);

                //byte[] decodedString = Base64.decode(encodedPhoto, Base64.DEFAULT);
                //Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                ImageView imageView = (ImageView)findViewById(R.id.imgView);
                imageView.setImageBitmap(photo);

            }
            else
            if (resultCode == RESULT_CANCELED)
                Toast.makeText(getApplicationContext(), "Photo Cancelled", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
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
        emotionalStateList.add("Angry " + getEmojiByUnicode(emojiUnicode("Angry")));
        emotionalStateList.add("Confused " + getEmojiByUnicode(emojiUnicode("Confused")));
        emotionalStateList.add("Disgusted " + getEmojiByUnicode(emojiUnicode("Disgusted")));
        emotionalStateList.add("Fearful " + getEmojiByUnicode(emojiUnicode("Fearful")));
        emotionalStateList.add("Happy " + getEmojiByUnicode(emojiUnicode("Happy")));
        emotionalStateList.add("Sad " + getEmojiByUnicode(emojiUnicode("Sad")));
        emotionalStateList.add("Shameful " + getEmojiByUnicode(emojiUnicode("Shameful")));
        emotionalStateList.add("Cool " + getEmojiByUnicode(emojiUnicode("Cool")));



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

    // get the selected dropdown list value
    public void addListenerOnSubmitButton() {

        emotionalStateSpinner = (Spinner) findViewById(R.id.emotional_state_spinner);
        socialSituationSpinner = (Spinner) findViewById(R.id.social_event_spinner);
        Button submitButton = (Button) findViewById(R.id.post_mood_button);

        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                emotionalState = String.valueOf(emotionalStateSpinner.getSelectedItem());
                socialSit = String.valueOf(socialSituationSpinner.getSelectedItem());

                try {
                    submitMood();
                    Toast.makeText(getApplicationContext(), "Successfully posted!", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (DescriptionTooLongException e) {
                    Toast.makeText(getApplicationContext(), "Your description is too long.", Toast.LENGTH_SHORT).show();
                }
            }

        });
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
