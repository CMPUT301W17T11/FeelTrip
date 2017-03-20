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
import android.widget.Spinner;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import layout.homeFragmment;
import layout.mapFragment;
import layout.profileFragment;

// This is the main screen: This is what the Participant first sees
public class MainScreen extends AppCompatActivity {
    private Context context;
    private Spinner filterSpinner;

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
                    /*ft.replace(R.id.fragent_frame,fragment);
                    ft.addToBackStack(null);
                    ft.commit();*/
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
