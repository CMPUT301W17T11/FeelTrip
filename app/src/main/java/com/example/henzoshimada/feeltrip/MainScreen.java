package com.example.henzoshimada.feeltrip;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import layout.homeFragmment;
import layout.mapFragment;
import layout.profileFragment;

// This is the main screen: This is what the Participant first sees
public class MainScreen extends AppCompatActivity {
    private Context context;
    private Spinner filterSpinner;
    private String emotionalState;
    private String socialSit;
    private Spinner emotionalStateSpinner;

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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Log.d("Mytag","Tapped on home");
                    fragment = new homeFragmment();
                    ft.replace(R.id.fragent_frame,fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                    return true;
                case R.id.navigation_profile:
                    Log.d("Mytag","Tapped on profile");
                    fragment = new profileFragment();
                    ft.replace(R.id.fragent_frame,fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                    return true;
                case R.id.navigation_map:
                    Log.d("Mytag","Tapped on map");
                    fragment = new mapFragment();
                    ft.replace(R.id.fragent_frame,fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                    return true;
                default:
                    fragment = new homeFragmment();
                    ft.replace(R.id.fragent_frame,fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                    return true;
            }
        }

    };

    private void setFirstItemNavigationView() {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.getMenu().getItem(1).setChecked(true);
        navigation.getMenu().performIdentifierAction(R.id.navigation_home, 0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setFirstItemNavigationView();
        addItemsOnEmotionalStateSpinner();

        ToggleButton toggleRecent = (ToggleButton) findViewById(R.id.toggleRecent);
        toggleRecent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                Toast.makeText(getApplicationContext(),"Toggled filter for recent posts!",Toast.LENGTH_SHORT).show();
            }
        });

        ToggleButton toggleFriends = (ToggleButton) findViewById(R.id.toggleFriends);
        toggleFriends.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                Toast.makeText(getApplicationContext(),"Toggled filter for friends!",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String getEmojiByUnicode(int unicode){
        if (unicode == 0) {
            return "";
        }
        return new String(Character.toChars(unicode));
    }

    private void addItemsOnEmotionalStateSpinner(){
        emotionalStateSpinner = (Spinner) findViewById(R.id.filterMood);
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

        /*
    // Taken from: https://www.mkyong.com/android/android-spinner-drop-down-list-example/
    // On: March 5, 2017 17:03
    public void addItemsOnFilterSpinner() {
        filterSpinner = (Spinner) findViewById(R.id.filter_spinner);
        List<String> filterList = new ArrayList<>();

        filterList.add("filter1");
        filterList.add("filter2");
        filterList.add("filter3");

        ArrayAdapter<String> filterListAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, filterList);
        filterListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(filterListAdapter);
    }*/

}
