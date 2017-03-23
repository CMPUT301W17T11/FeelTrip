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
import android.text.TextUtils;
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
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EditMoodActivity extends AppCompatActivity {

    public static final int MAX_PHOTO_SIZE = 65536;
    private EditText inputMoodDescription;
    private boolean locationOn;
    private double latitude;
    private double longitude;
    private boolean showPublicOn;
    private Spinner emotionalStateSpinner;
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
    private static final int TAKE_PHOTO = 2;
    Activity activity;
    Context context;

    // Used for edit functionality
    private Mood editmood;
    private boolean editflag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_page);
        inputMoodDescription = (EditText) findViewById(R.id.moodEventDescription);
        activity = this;
        context = this;
        verifyLocationPermissions(this);

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
                encodedPhoto = editmood.getImage();
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

/*
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
        enableMyLocation();
*/
        ToggleButton toggleLocationButton = (ToggleButton) findViewById(R.id.toggle_location); //TODO: I don't think this should be a toggle due to the editflag, let's find some other UI method?
        if(editflag) {
            assert editmood != null;
            toggleLocationButton.setChecked(editmood.getLocation() != null);
        }
        toggleLocationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleLocation(isChecked, buttonView);
            }
        });

        ToggleButton togglePublicButton = (ToggleButton)findViewById(R.id.toggle_public);
        if(editflag) {
            assert editmood != null;
            togglePublicButton.setChecked(!editmood.getPrivate());
        }
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
        // TODO: Replace this code with whatever is compatible with our final UI design, display image if editflag
//        if(editflag) {
//            assert editmood != null;
//            selectImageButton.setSelected(!editmood.getImage().isEmpty());
//        }
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
//                if(editflag) {
//                    dateTime.setTime(editmood.getDate());
//                    new DatePickerDialog(v.getContext(), datePickerDialogListener, dateTime.get(Calendar.YEAR),
//                            dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show(); //TODO: Also highlight the current date if editflag
//                }
                new DatePickerDialog(v.getContext(), datePickerDialogListener, dateTime.get(Calendar.YEAR),
                        dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //TODO: if editflag, highlight whichever emoji the editmood references. This can only be done once we've finalized our UI.

        addItemsOnEmotionalStateSpinner(editmood);

        addItemsOnSocialSituationSpinner(editmood);
        addListenerOnSubmitButton();

    }

    private void submitMood() throws DescriptionTooLongException {
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
            mood.setDescription(String.valueOf(inputMoodDescription.getText()), " -Feeling "); //TODO: append the emotionalState upon fetching from Elasticsearch
            mood.setDate(dateTime.getTime());

            if(encodedPhoto != null) {
                mood.setImage(encodedPhoto);
            }
            else {
                mood.setNullImage();
            }

            editMoodTask.execute(mood);

            Log.d("tag", "Editing mood");
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
            mood.setEmotionalState(emotionalState);
            mood.setSocialSit(socialSit);
            mood.setDescription(inputMoodDescription.getText().toString().replace("\\","\\\\"), " -Feeling "); //TODO: append the emotionalState upon fetching from Elasticsearch
            mood.setDate(dateTime.getTime());
            mood.setImage(encodedPhoto);

            addMoodTask.execute(mood);
            Log.d("tag", "Adding mood");
        }
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

    private byte[] compress(Bitmap photo){
        int quality = 100;

        //
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
                imageView.setImageBitmap(decodedPhoto);

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

    private void addItemsOnEmotionalStateSpinner(Mood editmood){
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


    private void addItemsOnSocialSituationSpinner(Mood editmood){
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

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */

    private void toggleLocation(boolean isChecked, CompoundButton button){
        if (isChecked) {
            verifyLocationPermissions(this);
            if (permissionDenied == true){
                Log.d("permTag", "checked but no permi");
                button.toggle();
                return;
            }
            // The toggle is enabled
            locationOn = true;
            //enableMyLocation();
            Log.d("myTag", "try to get location");
            GPSLocation gps = new GPSLocation(EditMoodActivity.this);

            // Check if GPS enabled
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();

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
            Log.d("permTag","location off");
        }

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
}
