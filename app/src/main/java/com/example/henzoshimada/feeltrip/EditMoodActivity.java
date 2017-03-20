package com.example.henzoshimada.feeltrip;
// removed unused imports, may slow down build
import android.app.DatePickerDialog;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditMoodActivity extends AppCompatActivity {

    private EditText inputMoodDescription;
    private boolean locationOn;
    private boolean showPublicOn;
    private Date date;
    private Spinner emotionalStateSpinner;
    private Spinner socialSituationSpinner;

    DateFormat formatDateTime = DateFormat.getDateInstance();
    private Calendar dateTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_page);
        inputMoodDescription = (EditText)findViewById(R.id.moodEventDescription);

        addItemsOnEmotionalStateSpinner();
        addItemsOnSocialSitualtionSpinner();
        Button submitButton = (Button) findViewById(R.id.post_mood_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //submitMood();
            }
        });

        ToggleButton toggleLocationButton = (ToggleButton)findViewById(R.id.toggle_location);
        toggleLocationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    locationOn = true;
                } else {
                    // The toggle is disabled
                    locationOn = false;
                }
                Log.d("myTag","location on is: "+String.valueOf(locationOn));
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

    private void selectDate(){
        new DatePickerDialog(this, datePickerDialogListener, dateTime.get(Calendar.YEAR),
                dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    //https://www.youtube.com/watch?v=8mFW6dA5xDE
    DatePickerDialog.OnDateSetListener datePickerDialogListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, month);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Log.d("myTag","Date: "+formatDateTime.format(dateTime.getTime()));
            date = dateTime.getTime();
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


    private void addItemsOnSocialSitualtionSpinner(){
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

}
