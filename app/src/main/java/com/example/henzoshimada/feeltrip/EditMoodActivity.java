package com.example.henzoshimada.feeltrip;
// removed unused imports, may slow down build

import android.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditMoodActivity extends AppCompatActivity {

    public static final int MAX_PHOTO_SIZE = 65536;
    private static int NUM_EMOTIONS = FeelTripApplication.getNumEmotions();
    private EditText inputMoodDescription;
    private LinearLayout emojiList;
    private ImageButton emojiButton;
    private TextView emojiTextview;
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
                    datePick(findViewById(R.id.date_bottom_button).getContext());
                    return true;
                case R.id.photo_bottom_button:
                    Log.d("Mytag", "Tapped on photo");
                    cameraPerm();
                    return true;
            }
            return true;
        }
    };
    private int emojiID;

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

        Integer posEditMood;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            posEditMood = extras.getInt("editmood");
            editmood = FeelTripApplication.getMoodArrayList().get(posEditMood);
        }
        addItemsOnSocialSituationSpinner();
        try {
            addItemsOnEmojiScroller();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (editmood != null) {
            try {
                dateTime.setTime(editmood.getDate());
            } catch (Exception e) {
                Log.d("tag", "No date found in editmood");
            }
            try {
                encodedPhoto = Html.fromHtml(editmood.getImage()).toString(); //TODO: Depreciated method
                if (encodedPhoto != null) {
                    byte[] decodedString = Base64.decode(encodedPhoto, Base64.DEFAULT);
                    Bitmap decodedPhoto = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ImageView imageView = (ImageView) findViewById(R.id.imgView);
                    imageView.setImageBitmap(decodedPhoto);
                    TextView modeLPhotoText = (TextView) findViewById(R.id.modePhoto);
                    modeLPhotoText.setText("On");
                    modeLPhotoText.setTextColor(getResources().getColor(R.color.green));
                }
            } catch (Exception e) {
                Log.d("tag", "No image found in editmood");
            }
            try {
                latitude = editmood.getLatitude();
            } catch (Exception e) {
                Log.d("tag", "No latitude found in editmood");
            }
            try {
                longitude = editmood.getLongitude();
                TextView modeLocationText = (TextView) findViewById(R.id.modeLocation);
                modeLocationText.setText("On");
                modeLocationText.setTextColor(getResources().getColor(R.color.green));
                locationOn = true;
            } catch (Exception e) {
                Log.d("tag", "No longitude found in editmood");
            }
            try {
                inputMoodDescription.setText(editmood.getDescription());
            } catch (Exception e) {
                Log.d("tag", "No description found in editmood");
            }
            try {
                socialSit = editmood.getSocialSit();
            } catch (Exception e) {
                Log.d("tag", "No socialsit found in editmood");
            }
            try {
                emotionalState = editmood.getEmotionalState();
                TextView emotionString = (TextView) findViewById(R.id.emotionString);
                emotionString.setText(emotionalState);
                emotionString.setTextColor(getResources().getColor(R.color.green));
            } catch (Exception e) {
                Log.d("tag", "No emotionalstate found in editmood");
            }
            try {
                emojiID = editmood.getEmoji();
            } catch (Exception e) {
                Log.d("tag", "No emoji found in editmood");
            }
            if (!editmood.getPrivate()) {
                TextView modePostText = (TextView) findViewById(R.id.modePost);
                modePostText.setText(R.string.mode_public);
                modePostText.setTextColor(getResources().getColor(R.color.green));
                showPublicOn = true;
            }
            socialSituationSpinner.setSelection(getIndex(socialSituationSpinner, editmood.getSocialSit()));
            editflag = true;

        } else {
            editflag = false;
        }
        BottomNavigationView options_bar = (BottomNavigationView) findViewById(R.id.options_post);
        options_bar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    } //end of on create

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public void setPostMode() {
        TextView modeShowPublic = (TextView) findViewById(R.id.modePost);
        if (showPublicOn == true) {
            modeShowPublic.setText(R.string.mode_private);
            modeShowPublic.setTextColor(getResources().getColor(R.color.red));
            showPublicOn = false;
        } else {
            modeShowPublic.setText(R.string.mode_public);
            modeShowPublic.setTextColor(getResources().getColor(R.color.green));
            showPublicOn = true;
        }
    }

    public void cameraPerm() {
        int loginPermissionCheck = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA);
        if (loginPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.d("tag", "Camera permission not granted");
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.CAMERA}, 1);
        } else {
            Log.d("tag", "Camera permission granted");
            takeAPhoto();
        }
    }

    public void datePick(Context context) {
        //selectDate();
        Log.d("Mytag", "Went into date");
        new DatePickerDialog(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,datePickerDialogListener, dateTime.get(Calendar.YEAR),
                dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }


    private void submitMood() throws DescriptionTooLongException {
        try {
            if (editflag) {
                Mood mood = editmood;
                ElasticSearchController.EditMoodTask editMoodTask = new ElasticSearchController.EditMoodTask();
                if (showPublicOn) {
                    mood.setPublic();
                } else {
                    mood.setPrivate();
                }
                if (locationOn) {
                    mood.setMapPosition(latitude, longitude);
                    // this is the setter for latitude and longitude
                } else {
                    mood.setNullLocation();
                }
                if (emotionalState.equals("")) {
                    throw new RuntimeException();
                } else {
                    mood.setEmotionalState(emotionalState);
                }

                mood.setEmoji(emojiID);

                mood.setSocialSit(socialSit);

                if (inputMoodDescription.getText().toString().equals("")) {
                    throw new NullPointerException();
                } else {
                    mood.setDescription(String.valueOf(inputMoodDescription.getText()));
                }

                mood.setDate(dateTime.getTime());

                if (encodedPhoto != null) {
                    mood.setImage(encodedPhoto);
                } else {
                    mood.setNullImage();
                }
                editMoodTask.execute(mood);
                Log.d("tag", "Editing mood");
                finish();
            } else {
                ElasticSearchController.AddMoodTask addMoodTask = new ElasticSearchController.AddMoodTask();
                Participant participant = FeelTripApplication.getParticipant();
                if (showPublicOn) {
                    mood.setPublic();
                } else {
                    mood.setPrivate();
                }
                if (locationOn) {
                    mood.setMapPosition(latitude, longitude);
                    // this is the setter for latitude and longitude
                } else {
                    mood.setNullLocation();
                }
                if (emotionalState.equals("")) {
                    throw new RuntimeException();
                } else {
                    mood.setEmotionalState(emotionalState);
                }

                mood.setEmoji(emojiID);

                mood.setSocialSit(socialSit);

                if (inputMoodDescription.getText().toString().equals("")) {
                    throw new NullPointerException();
                } else {
                    mood.setDescription(String.valueOf(inputMoodDescription.getText()));
                }
                mood.setDate(dateTime.getTime());

                if (encodedPhoto != null) {
                    mood.setImage(encodedPhoto);
                } else {
                    mood.setNullImage();
                }

                addMoodTask.execute(mood);
                Toast.makeText(getApplicationContext(), "Successfully posted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(), "Description Required " + e, Toast.LENGTH_SHORT).show();
        } catch (RuntimeException e) {
            Toast.makeText(getApplicationContext(), "Emoji choice required " + e, Toast.LENGTH_SHORT).show();
        }
    }

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

    private void takeAPhoto() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Camera";
        File folder = new File(path);
        if (!folder.exists())
            folder.mkdir();
        imagePathAndFileName = path + File.separator + String.valueOf(System.currentTimeMillis()) + ".JPEG";
        File imageFile = new File(imagePathAndFileName);
        imageFileUri = Uri.fromFile(imageFile);

        Log.d("tag", "Can make file path?");
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        Log.d("tag", "Take photo");
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private byte[] compress(Bitmap photo) {
        int quality = 100;

        if (photo.getByteCount() <= MAX_PHOTO_SIZE) {
            photo.compress(Bitmap.CompressFormat.JPEG, quality, photoStream);
            return photoStream.toByteArray();
        }

        //
        while (true) {
            photo.compress(Bitmap.CompressFormat.JPEG, quality, photoStream);
            byte[] compressedPhoto = photoStream.toByteArray();
            if (compressedPhoto.length <= MAX_PHOTO_SIZE || quality == 5) {
                return compressedPhoto;
            } else {
                quality -= 5;
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d("tag", "In Take Photo");
        TextView modePicture = (TextView) findViewById(R.id.modePhoto);
        if (requestCode == TAKE_PHOTO) {

            if (resultCode == RESULT_OK) {
                Log.d("tag", "Taking photo");

                Bitmap photo = (Bitmap) intent.getExtras().get("data");

                // Clear the old photoStream
                photoStream.reset();

                // Compress the photo if needed
                byte[] compressedPhoto = compress(photo);

                // Encode photo to string here
                encodedPhoto = ""; //Clear the old String just in case.
                encodedPhoto = Base64.encodeToString(compressedPhoto, Base64.DEFAULT);

                // This is how to decode the photo
                // Give it a String encoded[Photo/Emoji]
                byte[] decodedString = Base64.decode(encodedPhoto, Base64.DEFAULT);
                Bitmap decodedPhoto = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                // Set the image here
                ImageView imageView = (ImageView) findViewById(R.id.imgView);
                imageView.setImageBitmap(decodedPhoto);
                modePicture.setText(R.string.default_photo_on);
                modePicture.setTextColor(getResources().getColor(R.color.green));

            } else if (resultCode == RESULT_CANCELED)
                Toast.makeText(getApplicationContext(), "Photo Cancelled", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }


    //https://www.youtube.com/watch?v=8mFW6dA5xDE
    DatePickerDialog.OnDateSetListener datePickerDialogListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            if (!editflag) {
                dateTime.set(Calendar.YEAR, year);
                dateTime.set(Calendar.MONTH, month);
                dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
            Log.d("myTag", "Date: " + formatDateTime.format(dateTime.getTime()));
        }
    };


    private void addItemsOnSocialSituationSpinner() {
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
        } catch (DescriptionTooLongException e) {
            Toast.makeText(getApplicationContext(), "Your description is too long.", Toast.LENGTH_SHORT).show();
        }
        if (editflag) {
            int selectposition = 0;
            switch (editmood.getSocialSit()) {
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


    private void toggleLocation() {
        TextView modeLocation = (TextView) findViewById(R.id.modeLocation);
        // The toggle is enabled
        GPSLocation gps = new GPSLocation(EditMoodActivity.this);
        verifyLocationPermissions(this);
        gps.canGetLocation();
        if (gps.getLocation() != null && !locationOn) {
            locationOn = true;
            Log.d("myTag", "try to get location");
            // Check if GPS enabled
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            modeLocation.setText("On");
            modeLocation.setTextColor(getResources().getColor(R.color.green));
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
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
            Log.d("permTag", "verify: permissionDenied");
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_LOCATION, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            permissionDenied = false;
        }
    }

    public void addItemsOnEmojiScroller() throws IllegalAccessException {
        Field[] fields = R.drawable.class.getFields();
        List<Integer> drawables = new ArrayList<Integer>();
        for (Field field : fields) {
            // Take only those with name starting with "emoji"
            if (field.getName().startsWith("emoji")) {
                drawables.add(field.getInt(null));
            }
        }

        emojiList = (LinearLayout) findViewById(R.id.emojiList);
        for (int i = 1; i <= drawables.size(); i++) {

            LinearLayout emojiLayout = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            emojiLayout.setPadding(0,0,20,0);
            emojiLayout.setOrientation(LinearLayout.VERTICAL);
            emojiLayout.setTag(i); // This is crucial for grabbing the "index" of the clicked emoji later

            emojiButton = new ImageButton(getApplicationContext());
            emojiButton.setAdjustViewBounds(true);
            emojiButton.setMaxHeight(150);
            emojiButton.setMaxWidth(150);
            emojiButton.setBackgroundResource(R.color.white);
            if(getApplicationContext().getResources().getIdentifier("emoji" + i, "drawable", getApplicationContext().getPackageName()) != 0) { // Check if desired emoji exists
                emojiButton.setImageResource(getApplicationContext().getResources().getIdentifier("emoji" + i, "drawable", getApplicationContext().getPackageName()));
            } else {
                emojiButton.setImageResource(getApplicationContext().getResources().getIdentifier("err", "drawable", getApplicationContext().getPackageName()));
            }
            emojiButton.setPadding(0,0,0,0);
            emojiLayout.addView(emojiButton);
            emojiButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View layout = ((LinearLayout)v.getParent()); // Cast to a View from  ViewParent, since ViewParents don't allow us to use getTag()
                    int selected = (int) layout.getTag();
                    selectEmotion(selected);
                }
            });

            int emotionID = i % NUM_EMOTIONS;
            emojiTextview = new TextView(getApplicationContext());
            if(emotionID == 0) {
                emotionID = NUM_EMOTIONS;
            }
            emojiTextview.setText(FeelTripApplication.getEmotionalState(emotionID));

            emojiTextview.setTextSize(15);
            emojiTextview.setTextColor(Color.GRAY);
            emojiTextview.setGravity(Gravity.CENTER);
            emojiTextview.setTypeface(emojiTextview.getTypeface(), Typeface.BOLD);
            emojiLayout.addView(emojiTextview);

            emojiList.addView(emojiLayout);

        }

    }

    private void selectEmotion(int position) {
        emojiID = position;
        int emotionposition = position % NUM_EMOTIONS;
        if(emotionposition == 0) {
            emotionposition = NUM_EMOTIONS;
        }
        emotionalState = FeelTripApplication.getEmotionalState(emotionposition);
        TextView emotionString = (TextView)findViewById(R.id.emotionString);
        emotionString.setText(emotionalState);
        emotionString.setTextColor(getResources().getColor(R.color.green));
    }
}