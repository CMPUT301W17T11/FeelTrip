package com.example.henzoshimada.feeltrip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

// This is the main screen: This is what the Participant first sees
public class MainScreen extends AppCompatActivity {

    private Spinner filterSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_menu);
        Button goToMapButton = (Button) findViewById(R.id.goto_map);
        Button addMoodButton = (Button) findViewById(R.id.post_mood_button);

        addItemsOnFilterSpinner();

        goToMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapActivity.class);
                startActivity(intent);
            }
        });

        addMoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditMoodActivity.class);
                startActivity(intent);
            }
        });

    }

    // Taken from: https://www.mkyong.com/android/android-spinner-drop-down-list-example/
    // On: March 5, 2017 17:03
    public void addItemsOnFilterSpinner(){
        filterSpinner = (Spinner) findViewById(R.id.filter_spinner);
        List<String> filterList = new ArrayList<>();

        filterList.add("filter1");
        filterList.add("filter2");
        filterList.add("filter3");

        ArrayAdapter<String> filterListAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, filterList);
        filterListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(filterListAdapter);
    }


}
