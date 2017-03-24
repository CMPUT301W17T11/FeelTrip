package com.example.henzoshimada.feeltrip;
// removed unused imports, may slow down build

import android.*;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import layout.homeFragmment;
import layout.mapFragment;
import layout.profileFragment;

public class EditMoodActivity extends AppCompatActivity {

    public static final int MAX_PHOTO_SIZE = 65536;
    private EditText inputMoodDescription;
    private boolean locationOn;
    private double latitude;
    private double longitude;
    private boolean showPublicOn;
    private Spinner socialSituationSpinner;

    private boolean permissionDenied = false;
    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };


    private GoogleApiClient mGoogleApiClient;
    private Location mLastKnownLocation;


    DateFormat formatDateTime = DateFormat.getDateInstance();
    private Calendar dateTime = Calendar.getInstance();
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
    private static final int TAKE_PHOTO = 2;
    Activity activity;
    private static Context context;
    private String emotionalState;
    private Participant participant = FeelTripApplication.getParticipant();
    private Mood mood = new Mood(participant.getUserName());

    // Used for edit functionality
    private Mood editmood;
    private boolean editflag;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.location_toggle:
                    Log.d("Mytag", "Tapped on location");
                    //verifyLocationPermissions(getApplicationContext());
                    toggleLocation();
                    return true;
                case R.id.private_toggle:
                    Log.d("Mytag", "Tapped on private");
                    setPostMode();
                    return true;
                case R.id.check_bottom_button:
                    Log.d("Mytag", "Tapped on submit");
                    addListenerOnSubmitButton();
                    return true;
                case R.id.date_bottom_button:
                    Log.d("Mytag", "Tapped on date");
                    datePick();
                    return true;
                case R.id.photo_bottom_button:
                    Log.d("Mytag", "Tapped on photo");
                    cameraPerm();
                    return true;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_page);
        EditMoodActivity.context = getApplicationContext();
        inputMoodDescription = (EditText) findViewById(R.id.moodEventDescription);

        activity = this;
        context = this;
        showPublicOn = false;
        locationOn = false;

        verifyLocationPermissions(this);
        editmood = null;
        encodedPhoto = null;
//        socialSit = null;
//        emotionalState = null;
//        dateTime = null;

        String jsonEditMood = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonEditMood = extras.getString("editmood");
        }
        editmood = new Gson().fromJson(jsonEditMood, Mood.class);
        if(editmood != null) {
            try {
                dateTime.setTime(editmood.getDate());
            }
            catch (Exception e) {
                Log.d("tag", "No date found in editmood");
            }
            try {
                encodedPhoto = Html.fromHtml(editmood.getImage()).toString(); //TODO: Depreciated method
                if (encodedPhoto != null) {
                    byte[] decodedString = Base64.decode(encodedPhoto, Base64.DEFAULT);
                    Bitmap decodedPhoto = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ImageView imageView = (ImageView) findViewById(R.id.imgView);
                    imageView.setImageBitmap(decodedPhoto);
                }
            } catch (Exception e) {
                Log.d("tag", "No image found in editmood");
            }
            try {
                latitude = editmood.getLatitude();
            }
            catch (Exception e) {
                Log.d("tag", "No latitude found in editmood");
            }
            try {
                longitude = editmood.getLongitude();
            }
            catch (Exception e) {
                Log.d("tag", "No longitude found in editmood");
            }
            try {
                inputMoodDescription.setText(editmood.getDescription());
            }
            catch (Exception e) {
                Log.d("tag", "No description found in editmood");
            }
            try {
                socialSit = editmood.getSocialSit(); //TODO: There is a massive UI bug where if editmood's privacy settings are currently set to public, and the user then changes the socialsit, it changes the privacy settings too.
            }
            catch (Exception e) {
                Log.d("tag", "No socialsit found in editmood");
            }
            try {
                emotionalState = editmood.getEmotionalState();
            }
            catch (Exception e) {
                Log.d("tag", "No emotionalstate found in editmood");
            }

            editflag = true;

        }
        else{
            editflag = false;
        }
        addItemsOnSocialSituationSpinner(editmood);
        BottomNavigationView options_bar = (BottomNavigationView) findViewById(R.id.options_post);
        options_bar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    } //end of on create


    public void setPostMode(){
        TextView modeShowPublic = (TextView) findViewById(R.id.modePost);
        if(showPublicOn == true){
            modeShowPublic.setText(R.string.mode_private);
            modeShowPublic.setTextColor(getResources().getColor(R.color.red));
            showPublicOn = false;
        }
        else{
            modeShowPublic.setText(R.string.mode_public);
            modeShowPublic.setTextColor(getResources().getColor(R.color.green));
            showPublicOn = true;
        }
    }

    public void cameraPerm (){
        int loginPermissionCheck = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA);
        if (loginPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.d("tag", "Camera permission not granted");
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.CAMERA}, 1);
        } else {
            Log.d("tag", "Camera permission granted");
            takeAPhoto();
        }
    }

    //#################################################################################
    //Start of handling emojis

    public void addHappyEmoji(View v) {
        TextView emotionString = (TextView)findViewById(R.id.emotionString);
        emotionString.setText("Happy");
        emotionString.setTextColor(getResources().getColor(R.color.green));
        emotionalState = "Happy";
    }
    public void addAngryEmoji(View v){
        TextView emotionString = (TextView)findViewById(R.id.emotionString);
        emotionString.setText("Angry");
        emotionString.setTextColor(getResources().getColor(R.color.green));
        emotionalState = "Angry";
    }
    public void addConfusedEmoji(View v){
        TextView emotionString = (TextView)findViewById(R.id.emotionString);
        emotionString.setText("Confused");
        emotionString.setTextColor(getResources().getColor(R.color.green));
        emotionalState = "Confused";
    }
    public void addDisEmoji(View v){
        TextView emotionString = (TextView)findViewById(R.id.emotionString);
        emotionString.setText("Disgust");
        emotionString.setTextColor(getResources().getColor(R.color.green));
        emotionalState = "Disgust";
    }
    public void addFearEmoji(View v){
        TextView emotionString = (TextView)findViewById(R.id.emotionString);
        emotionString.setText("Fear");
        emotionString.setTextColor(getResources().getColor(R.color.green));
        emotionalState = "Fear";
    }
    public void addSadEmoji(View v){
        TextView emotionString = (TextView)findViewById(R.id.emotionString);
        emotionString.setText("Sad");
        emotionString.setTextColor(getResources().getColor(R.color.green));
        emotionalState = "Sad";
    }
    public void addShamEmoji(View v){
        TextView emotionString = (TextView)findViewById(R.id.emotionString);
        emotionString.setText("Shameful");
        emotionString.setTextColor(getResources().getColor(R.color.green));
        emotionalState = "Shameful";
    }
    public void addCoolEmoji(View v){
        TextView emotionString = (TextView)findViewById(R.id.emotionString);
        emotionString.setText("Cool");
        emotionString.setTextColor(getResources().getColor(R.color.green));
        emotionalState = "Cool";
    }




    //#################################################################################
    public void datePick() {
        //selectDate();
        Log.d("Mytag","Went into date");
        new DatePickerDialog(getAppContext(), datePickerDialogListener, dateTime.get(Calendar.YEAR),
                dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

/*
    private void submitMood() throws DescriptionTooLongException {
<<<<<<< HEAD
        if(editflag) {
            Mood mood = editmood;
            ElasticSearchController.EditMoodTask editMoodTask = new ElasticSearchController.EditMoodTask(this);
            if (showPublicOn) {
                mood.setPublic();
            } else {
                mood.setPrivate();
            }
            if (locationOn) {
                mood.setMapPosition(latitude, longitude);
                // this is the setter for latitude and longitude
            }
            else {
                mood.setNullLocation();
            }
            mood.setEmotionalState(emotionalState);
            mood.setSocialSit(socialSit);
            mood.setDescription(String.valueOf(inputMoodDescription.getText())); //TODO: append the emotionalState upon fetching from Elasticsearch
            mood.setDate(dateTime.getTime());

            if(encodedPhoto != null) {
                mood.setImage(encodedPhoto);
            }
            else {
                mood.setNullImage();
            }

            editMoodTask.execute(mood);

            Log.d("tag", "Editing mood");
=======
        ElasticSearchController.AddMoodTask addMoodTask = new ElasticSearchController.AddMoodTask();

        if(showPublicOn){
            mood.setPublic();
>>>>>>> a1b732e279e48e4e7f2766874ed37fa6cd3ac079
        }
        else {
            ElasticSearchController.AddMoodTask addMoodTask = new ElasticSearchController.AddMoodTask(this);
            Participant participant = FeelTripApplication.getParticipant();
            Mood mood = new Mood(participant.getUserName());
            if (showPublicOn) {
                mood.setPublic();
            } else {
                mood.setPrivate();
            }
            if (locationOn) {
                mood.setMapPosition(latitude, longitude);
                // this is the setter for latitude and longitude
            }
            else {
                mood.setNullLocation();
            }
            mood.setEmotionalState(emotionalState);
            mood.setSocialSit(socialSit);
            mood.setDescription(String.valueOf(inputMoodDescription.getText())); //TODO: append the emotionalState upon fetching from Elasticsearch
            mood.setDate(dateTime.getTime());

            if(encodedPhoto != null) {
                mood.setImage(encodedPhoto);
            }
            else {
                mood.setNullImage();
            }

            addMoodTask.execute(mood);
            Log.d("tag", "Adding mood");
        }
<<<<<<< HEAD
=======
        mood.setSocialSit(socialSit);
        mood.setDescription(String.valueOf(inputMoodDescription.getText()), " -Feeling "  + emotionalState); //TODO: We might not need to send this append part to Elasticsearch, but rather add it to our displayed description after we fetch from Elasticsearch
        mood.setDate(dateTime.getTime());
        mood.setImage(encodedPhoto);
        mood.setEmotionalState(emotionalState);
        addMoodTask.execute(mood);
>>>>>>> a1b732e279e48e4e7f2766874ed37fa6cd3ac079
    }

*/
    public static Context getAppContext() {
        return EditMoodActivity.context;
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

    private byte[] compress(Bitmap photo){
        int quality = 100;

        if (photo.getByteCount()<= MAX_PHOTO_SIZE) {
            photo.compress(Bitmap.CompressFormat.PNG, quality, photoStream);
            return photoStream.toByteArray();
        }

        //
        while(true) {
            photo.compress(Bitmap.CompressFormat.JPEG, quality, photoStream);
            byte [] compressedPhoto = photoStream.toByteArray();
            if(compressedPhoto.length <= MAX_PHOTO_SIZE || quality == 5){
                return compressedPhoto;
            }
            else {
                quality -= 5;
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        Log.d("tag", "In Take Photo");
        TextView modePicture = (TextView) findViewById(R.id.modePhoto);
        if (requestCode == TAKE_PHOTO){

            if (resultCode == RESULT_OK){
                Log.d("tag", "Taking photo");

                Bitmap photo = (Bitmap) intent.getExtras().get("data");

                // Compress the photo if needed
                byte [] compressedPhoto = compress(photo);

                // Encode photo to string here
                encodedPhoto = Base64.encodeToString(compressedPhoto, Base64.DEFAULT);

                // This is how to decode the photo
                                                    // Give it a String encoded[Photo/Emoji]
                byte[] decodedString = Base64.decode(encodedPhoto, Base64.DEFAULT);
                Bitmap decodedPhoto = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                // Set the image here
                ImageView imageView = (ImageView)findViewById(R.id.imgView);
                imageView.setImageBitmap(photo);
                modePicture.setText(R.string.default_photo_on);
                modePicture.setTextColor(getResources().getColor(R.color.green));
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
            if(!editflag) {
                dateTime.set(Calendar.YEAR, year);
                dateTime.set(Calendar.MONTH, month);
                dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
            Log.d("myTag","Date: "+formatDateTime.format(dateTime.getTime()));
        }
    };



    private void addItemsOnSocialSituationSpinner(Mood editmood) {
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

        socialSituationSpinner = (Spinner) findViewById(R.id.social_event_spinner);
        //ooemotionalState = String.valueOf(emotionalStateSpinner.getSelectedItem());
        socialSit = String.valueOf(socialSituationSpinner.getSelectedItem());

        try {
            submitMood();
            Toast.makeText(getApplicationContext(), "Successfully posted!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (DescriptionTooLongException e) {
            Toast.makeText(getApplicationContext(), "Your description is too long.", Toast.LENGTH_SHORT).show();
        }
        if(editflag) {
            int selectposition = 0;
            switch(editmood.getSocialSit()) {
                case "Alone":
                    selectposition = 0;
                    break;
                case "With one other person":
                    selectposition = 1;
                    break;
                case "With two to several people":
                    selectposition = 2;
                    break;
                case "With a crowd":
                    selectposition = 3;
                    break;
                default:
                    selectposition = 0;
                    break;
            }
            socialSituationSpinner.setSelection(selectposition);

        }
    }



    private void toggleLocation(){
        TextView modeLocation = (TextView) findViewById(R.id.modeLocation);
        // The toggle is enabled
        GPSLocation gps = new GPSLocation(EditMoodActivity.this);
        verifyLocationPermissions(this);
        gps.canGetLocation();
        if(gps.getLocation() != null && !locationOn) {
            locationOn = true;
            Log.d("myTag", "try to get location");
            // Check if GPS enabled
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            modeLocation.setText("On");
            modeLocation.setTextColor(getResources().getColor(R.color.green));
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            locationOn = false;
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            //gps.showSettingsAlert();
            modeLocation.setText("Off");
            modeLocation.setTextColor(getResources().getColor(R.color.red));
        }
        // The toggle is disabled
        Log.d("myTag", "location on is: " + String.valueOf(locationOn));
    }

    public void verifyLocationPermissions(Activity activity) {
        // Check if we have location permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionDenied = true;
            Log.d("permTag","verify: permissionDenied");
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_LOCATION, LOCATION_PERMISSION_REQUEST_CODE);
        }else{
            permissionDenied = false;
        }
    }

    private void submitMood()throws  DescriptionTooLongException{}

    /*
    // get the selected dropdown list value
    public void addListenerOnSubmitButton() {

        //emotionalStateSpinner = (Spinner) findViewById(R.id.emotional_state_spinner);
        socialSituationSpinner = (Spinner) findViewById(R.id.social_event_spinner);
        //Button submitButton = (Button) findViewById(R.id.post_mood_button);

        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                emotionalState = String.valueOf(emotionalStateSpinner.getSelectedItem());
                socialSit = String.valueOf(socialSituationSpinner.getSelectedItem());

                try {
                    submitMood();
                    if(editflag) {
                        Toast.makeText(getApplicationContext(), "Successfully updated!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Successfully posted!", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                } catch (DescriptionTooLongException e) {
                    Toast.makeText(getApplicationContext(), "Your description is too long.", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
*/
}
