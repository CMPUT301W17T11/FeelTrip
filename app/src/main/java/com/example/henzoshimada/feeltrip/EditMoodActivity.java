package com.example.henzoshimada.feeltrip;

import android.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * The type Edit mood activity.
 * This is the screen to add a new mood, as well as edit a previous mood
 */
public class EditMoodActivity extends AppCompatActivity {

    /**
     * The constant MAX_PHOTO_SIZE.
     */
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

    /**
     * The Format date time.
     */
    DateFormat formatDateTime = DateFormat.getDateInstance();
    private Calendar dateTime = Calendar.getInstance();
    private String socialSit;

    // Used for taking a photo
    private ByteArrayOutputStream photoStream = new ByteArrayOutputStream();
    private String encodedPhoto;
    private String imagePathAndFileName;
    /**
     * The Image file uri.
     */
    Uri imageFileUri;
    private static final int TAKE_PHOTO = 2;
    private static final int GET_LOC = 3;
    /**
     * The Activity.
     */
    Activity activity;
    private static Context context;
    private String emotionalState;
    private Participant participant = FeelTripApplication.getParticipant();
    private Mood mood = new Mood(participant.getUserName());

    // Used for edit functionality
    private Mood editmood;
    private boolean editflag;

    /**
     * The Mode location text.
     */
    TextView modeLocationText;

    /**
     * This is the listener to toggle the different attributes that is connected with a mood. This
     * keeps track of whether the participant wants a location attacked or wants their mood to be
     * private or public, etc.
     */
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
                /*
                case R.id.check_bottom_button:
                    Log.d("Mytag", "Tapped on submit");
                    addListenerOnSubmitButton();
                    return true;
                    */
                case R.id.date_bottom_button:
                    Log.d("Mytag", "Tapped on date");
                    datePick(findViewById(R.id.date_bottom_button).getContext());
                    //datePick(findViewById(R.id.date_bottom_button).getContext());

                    return true;

                case R.id.time_bottom_button:
                    Log.d("Mytag", "Tapped on Time");
                    timePick(findViewById(R.id.date_bottom_button).getContext());
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


//        setTheme(R.style.NaughtyPenguins); //TODO - theme
//        setTheme(R.style.DefaultTheme);
        setTheme(FeelTripApplication.getThemeID());

        setContentView(R.layout.add_edit_page);
        EditMoodActivity.context = getApplicationContext();


        inputMoodDescription = (EditText) findViewById(R.id.moodEventDescription);
        modeLocationText = (TextView) findViewById(R.id.modeLocation);


        if(FeelTripApplication.getThemeID() == R.style.CustomTheme_Light || FeelTripApplication.getThemeID() == R.style.CustomTheme_Dark) {
            inputMoodDescription.setHintTextColor(FeelTripApplication.getTEXTCOLORTERTIARY());

            TextView feelingTextView = (TextView) findViewById(R.id.feelingTextView);
            feelingTextView.setTextColor(FeelTripApplication.getTEXTCOLORSECONDARY());
            TextView locationTextView = (TextView) findViewById(R.id.locationTextView);
            locationTextView.setTextColor(FeelTripApplication.getTEXTCOLORSECONDARY());
            TextView modeTextView = (TextView) findViewById(R.id.modeTextView);
            modeTextView.setTextColor(FeelTripApplication.getTEXTCOLORSECONDARY());
            TextView photoAttachedTextView = (TextView) findViewById(R.id.photoAttachedTextView);
            photoAttachedTextView.setTextColor(FeelTripApplication.getTEXTCOLORSECONDARY());

            int[][] navstates = new int[][] {
                    new int[] { android.R.attr.state_checked},  // checked
                    new int[] {-android.R.attr.state_checked}  // unchecked
            };

            int[] navcolors = new int[] {
                    FeelTripApplication.getCOLORPRIMARY(),
                    FeelTripApplication.getTEXTCOLORSECONDARY()
            };
            ColorStateList navList = new ColorStateList(navstates, navcolors);
            android.support.design.widget.BottomNavigationView bottomNavigationView = (android.support.design.widget.BottomNavigationView) findViewById(R.id.options_post);
            bottomNavigationView.setItemIconTintList(navList);
            bottomNavigationView.setItemTextColor(navList);
        }

        else if(FeelTripApplication.getThemeID() == R.style.Simplicity) {
            ScrollView background = (ScrollView) findViewById(R.id.editMood_scrollView);
            background.setBackgroundResource(R.drawable.simplicity_bg);
        }

        else if(FeelTripApplication.getThemeID() == R.style.Overwatch) {
        }

        else if(FeelTripApplication.getThemeID() == R.style.GalaxyTheme) {
            ScrollView background = (ScrollView) findViewById(R.id.editMood_scrollView);
            background.setBackgroundResource(R.drawable.galaxy_bg);
        }


        activity = this;
        context = this;
        showPublicOn = false;
        locationOn = false;

        verifyLocationPermissions(this);
        editmood = null;
        encodedPhoto = null;

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

        /**
         * If this is not a new mood, then load all the old information that is found on the database
         */
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

        ImageButton submitButton = (ImageButton) findViewById(R.id.check_bottom_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addListenerOnSubmitButton();
            }
        });

    } //end of onCreate

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Returning will discard any working changes you may have made. Are you sure you wish to return?")
                .setTitle("Discard Changes?");

        builder.setPositiveButton("RETURN", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditMoodActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Get the index of the current mood
     * @param spinner
     * @param myString
     * @return
     */
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

    /**
     * Sets post mode.
     */
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

    /**
     * Camera perm.
     * If the app has permission to access the camera then it will take a photo, else it will ask
     * for the permission from the participant to access the camera on the device
     */
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

    /**
     * Called when user click the Pick Date option
     * Uses DatePickerDiaLog
     *
     * @param context the context
     * @see DatePickerDialog
     */
    public void datePick(Context context) {
        Log.d("Mytag", "Went into date");
        new DatePickerDialog(context, datePickerDialogListener, dateTime.get(Calendar.YEAR), //TODO: Theme these properly
                dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * Called when user click the Pick Time option
     * Uses TimePickerDialog
     *
     * @param context the context
     * @see TimePickerDialog
     */
    public void timePick(Context context) {
        //selectDate();
        Log.d("Mytag", "Went into date");
        new TimePickerDialog(context, onTimeSetListener, dateTime.get(Calendar.HOUR_OF_DAY),
                dateTime.get(Calendar.MINUTE), true).show();
    }

    /**
     * This is what sends the mood to Elastic search database to be stored, as long as the information
     * entered is valid.
     * @throws DescriptionTooLongException
     */
    private void submitMood() throws DescriptionTooLongException {
        try {
            if (editflag) {
                Mood mood = editmood;
                mood.resetState();
                mood.setAdd();
                FeelTripApplication.getLocalProfileList().remove(mood);
                ElasticSearchController.EditMoodTask editMoodTask = new ElasticSearchController.EditMoodTask();
                if (showPublicOn) {
                    mood.setPublic();
                } else {
                    mood.setPrivate();
                }
                if (locationOn) {
                    Log.d("camTag","in edit: "+latitude+" "+longitude);
                    mood.setMapPosition(latitude, longitude);
                    // this is the setter for latitude and longitude
                } else {
                    Log.d("camTag","in edit: null");
                    mood.setNullLocation();
                }
                if (emotionalState.equals("")) {

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
                FeelTripApplication.getLocalProfileList().add(0, mood);

                Log.d("tag", "Editing mood");
                finish();
            } else {
                ElasticSearchController.AddMoodTask addMoodTask = new ElasticSearchController.AddMoodTask();
                //Participant participant = FeelTripApplication.getParticipant();
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
                    mood.setDescription(String.valueOf(inputMoodDescription.getText()));
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
            Toast.makeText(getApplicationContext(), "Missing required fields ", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gets app context.
     *
     * @return the app context
     */
    public static Context getAppContext() {
        return EditMoodActivity.context;
    }

    /**
     * This method accesses the device's camera to take a photo and stores it in the DCIM directory
     * that the participant will be able to access afterwards.
     */

    private void takeAPhoto() {
        //Taken: http://stackoverflow.com/questions/29576098/get-path-of-dcim-folder-on-both-primary-and-secondary-storage
        // On: March 17, 2017 at 17:25
        //String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Camera";
        String path = Environment.DIRECTORY_DCIM;
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

    /**
     * This is the method that is used to compress a photo if it needs to be.
     * It will always compress the photo at least once with the highest quality (100), ie with
     * the minimal compression, so it can be stored as a JPEG file.
     *
     * TALKED WITH TA: Zharkyn Kassenov on March 20, 2017, 18:00
     * @param photo
     * @return
     */
    private byte[] compress(Bitmap photo) {
        int quality = 100;

        if (photo.getByteCount() <= MAX_PHOTO_SIZE) {
            photo.compress(Bitmap.CompressFormat.JPEG, quality, photoStream);
            return photoStream.toByteArray();
        }

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

    /**
     * This is the method that handles the different activity results, whether it be accessing the
     * camera, or retrieving the location of the mood.
     * @param requestCode
     * @param resultCode
     * @param intent
     */
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
        }else if (requestCode == GET_LOC){
            Log.d("locTag", "request code = get loc");
            if(resultCode == Activity.RESULT_OK){
                Log.d("locTag", "result ok");

                try {
                    //String temp = intent.getStringExtra("resultLat");
                    //latitude = Double.parseDouble(temp);
                    //temp = intent.getStringExtra("resultLong");
                    //latitude = Double.parseDouble(temp);
                    //longitude = Double.parseDouble(intent.getStringExtra("resultLong"));
                    latitude = intent.getDoubleExtra("resultLat",0);
                    longitude = intent.getDoubleExtra("resultLong",0);

                    if ((latitude == 0.0) && (longitude == 0.0)) {
                        Log.d("locTag", "0 0 true");
                        return; //return to previous state
                    }
                    locationOn = true;
                    modeLocationText.setText("On");
                    modeLocationText.setTextColor(getResources().getColor(R.color.green));
                    Log.d("locTag", "result ok done");
                }catch (NullPointerException e){
                    locationOn = false;
                    Log.d("locTag", "bad");
                }


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("locTag", "result cancel");
                //Write your code if there's no result
                //locationOn = true;
                //modeLocationText.setText("On");
                //modeLocationText.setTextColor(getResources().getColor(R.color.green));
            }
        }
    }


    /**
     * The Date picker dialog listener.
     */
//23 Feb 2017 https://www.youtube.com/watch?v=8mFW6dA5xDE
    DatePickerDialog.OnDateSetListener datePickerDialogListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, month);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            Log.d("myTag", "Date: " + formatDateTime.format(dateTime.getTime()));
        }
    };

    /**
     * The On time set listener.
     */
//23 Feb 2017 https://www.youtube.com/watch?v=8mFW6dA5xDE
    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTime.set(Calendar.MINUTE, minute);

            Log.d("myTag", "time Date: " + formatDateTime.format(dateTime.getTime()));
        }
    };


    private void addItemsOnSocialSituationSpinner() {
        socialSituationSpinner = (Spinner) findViewById(R.id.social_event_spinner);
        List<String> socialSituationList = new ArrayList<>();

        socialSituationList.add("None");
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
     * Add listener on submit button.
     */
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
        if (locationOn) {
            locationOn = false;
            modeLocationText.setText("Off");
            modeLocationText.setTextColor(getResources().getColor(R.color.red));
        }else{
            Intent intent = new Intent(this, MapsActivity.class);
            Log.d("camTag","before pass: "+latitude+" "+longitude);
            intent.putExtra("currentLong",longitude);
            intent.putExtra("currentLat",latitude);
            startActivityForResult(intent, GET_LOC);
        }
    }

    /**
     * Check if ACCESS_FINE_LOCATION permission is granted, if not, ask for the permission
     *
     * @param activity the activity
     */
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

    /**
     * Add items on emoji scroller.
     *
     * @throws IllegalAccessException the illegal access exception
     */
    public void addItemsOnEmojiScroller() throws IllegalAccessException {

        emojiList = (LinearLayout) findViewById(R.id.emojiList);

        int theme_offset;
        switch (FeelTripApplication.getThemeID()) {
            case R.style.Simplicity:
                theme_offset = 1;
                break;
            case R.style.Overwatch:
                theme_offset = 2;
                break;
            case R.style.GalaxyTheme:
                theme_offset = 3;
                break;
            default:
                theme_offset = 0;
                break;
        }
        theme_offset = theme_offset * NUM_EMOTIONS;

        for (int i = 1 + theme_offset; i <= theme_offset + NUM_EMOTIONS; i++) {

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
            emojiButton.setBackgroundResource(R.color.transparent);
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
                    View layout = ((LinearLayout)v.getParent()); // Cast to a View from a ViewParent, since ViewParents don't allow us to use getTag()
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
            emojiTextview.setGravity(Gravity.CENTER);
            if(FeelTripApplication.getThemeID() == R.style.CustomTheme_Light || FeelTripApplication.getThemeID() == R.style.CustomTheme_Dark) {
                emojiTextview.setTextColor(FeelTripApplication.getTEXTCOLORSECONDARY());
            } else {
                TypedValue tv = new TypedValue();
                getTheme().resolveAttribute(android.R.attr.textColorSecondary, tv, true);
                emojiTextview.setTextColor(getResources().getColor(tv.resourceId)); //TODO: Depreciated. Next call up is API 23
            }
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