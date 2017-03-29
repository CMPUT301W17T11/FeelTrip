package com.example.henzoshimada.feeltrip;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import layout.homeFragmment;
import layout.mapFragment;
import layout.profileFragment;

// This is the main screen: This is what the Participant first sees

public class MainScreen extends AppCompatActivity {

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
    private ListView userFoundView;  //who participant searched
    private ListView followingView; //who participant is following
    private ListView requestView;   //who participant wants to follow

    private ArrayList<String> usersFoundArray = new ArrayList<String>(); //todo need custom adabter
    private ArrayList<String> followingArray = new ArrayList<String>(); //todo use default adapter
    private ArrayList<String> requestsArray = new ArrayList<String>(); //todo need custom adabter

    private Participant participant = FeelTripApplication.getParticipant();

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

    // Taken from http://stackoverflow.com/questions/4828636/edittext-clear-focus-on-touch-outside on 2017-03-27 08:52
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
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
                    FeelTripApplication.setFrag("main");
                    fragment = new homeFragmment();
                    ft.replace(R.id.fragent_frame,fragment);
                    //ft.addToBackStack(null);
                    ft.commit();
                    break;
                case R.id.navigation_profile:
                    Log.d("Mytag","Tapped on profile");
                    FeelTripApplication.setFrag("profile");
                    fragment = new profileFragment();
                    ft.replace(R.id.fragent_frame,fragment);
                    //ft.addToBackStack(null);
                    ft.commit();
                    break;
                case R.id.navigation_map:
                    Log.d("Mytag","Tapped on map");
                    FeelTripApplication.setFrag("map");
                    fragment = new mapFragment();
                    ft.replace(R.id.fragent_frame,fragment);
                    //ft.addToBackStack(null);
                    ft.commit();
                    break;
                default:
                    fragment = new homeFragmment();
                    ft.replace(R.id.fragent_frame,fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                    break;
            }
//            Intent i = getIntent();
//            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//            finish();
//            startActivity(i);
            //onPause(); // Refreshes the current activity without calling onCreate
            onResume();
            return true;
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

//        setTheme(R.style.NaughtyPenguins); //TODO - theme
        setTheme(R.style.DefaultTheme);


        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setFirstItemNavigationView();
        addItemsOnEmotionalStateSpinner();

        ToggleButton toggleRecent = (ToggleButton) findViewById(R.id.toggleRecent);
        toggleRecent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                //Toast.makeText(getApplicationContext(),"Toggled filter for recent posts!",Toast.LENGTH_SHORT).show();
                FeelTripApplication.getFilterController().setPastweekfilter(isChecked);
                ElasticSearchController.loadFromElasticSearch();
                FeelTripApplication.getMoodAdapter(getBaseContext()).notifyDataSetChanged();
                if (FeelTripApplication.getFrag().equals("map")) {
                    updateMap();
                }
            }
        });

        ToggleButton toggleFriends = (ToggleButton) findViewById(R.id.toggleFriends);
        toggleFriends.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                //Toast.makeText(getApplicationContext(),"Toggled filter for friends!",Toast.LENGTH_SHORT).show();
                FeelTripApplication.getFilterController().setFriendsonlyfilter(isChecked);
                ElasticSearchController.loadFromElasticSearch();
                FeelTripApplication.getMoodAdapter(getBaseContext()).notifyDataSetChanged();
                if (FeelTripApplication.getFrag().equals("map")) {
                    updateMap();
                }
            }
        });

        ToggleButton toggleMostRecent = (ToggleButton) findViewById(R.id.toggleMostRecent);
        toggleMostRecent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                //Toast.makeText(getApplicationContext(),"Toggled filter for friends!",Toast.LENGTH_SHORT).show();
                FeelTripApplication.getFilterController().setMostrecentfilter(isChecked);
                ElasticSearchController.loadFromElasticSearch();
                FeelTripApplication.getMoodAdapter(getBaseContext()).notifyDataSetChanged();
                if (FeelTripApplication.getFrag().equals("map")) {
                    updateMap();
                }
            }
        });

        ImageButton searchButton = (ImageButton) findViewById(R.id.imageButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchKeyword(v);
            }
        });

        ImageButton searchUserButton = (ImageButton) findViewById(R.id.user_search_button);
        searchUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchUser(v);
            }
        });

        userFoundView = (ListView) findViewById(R.id.found_user);
        followingView = (ListView) findViewById(R.id.follow_list);
        requestView = (ListView) findViewById(R.id.request_list);
    }

    private void searchUser(View view){

    }

    @Override
    protected void onResume() {
        super.onResume();

        ToggleButton toggleFriends = (ToggleButton) findViewById(R.id.toggleFriends);
        TextView textView = (TextView) findViewById(R.id.textView5);
        if(FeelTripApplication.getFrag().equals("profile")) {
            toggleFriends.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        } else {
            toggleFriends.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        }

        EditText string_keyword = (EditText) findViewById(R.id.keyword);
        FilterController.setKeywordfilter(string_keyword.getText().toString()); // Always take into account what's written in the "Keyword search" field. This eliminates confusion when words are typed but "Search" isn't explicitly tapped
        ElasticSearchController.loadFromElasticSearch();
        FeelTripApplication.getMoodAdapter(getBaseContext()).notifyDataSetChanged();

        //todo load req,folow arrays
        loadRequestsArray();
        loadFollowingsArray();
    }

    //todo or michael already finished: update the follow request list
    private void loadRequestsArray(){//sender
        ArrayList<FollowRequest> followRequests = participant.getFollowRequest();
        String username;
        for (int i = 0; i < followRequests.size(); i++){
            username = followRequests.get(i).getSender();
            requestsArray.set(i, username);
        }
    }

    private void loadFollowingsArray(){//receiver
        followingArray.addAll(participant.getFollowing());
    }

    public String getEmojiByUnicode(int unicode){
        if (unicode == 0) {
            return "";
        }
        return new String(Character.toChars(unicode));
    }

    public void searchKeyword(View v){
        EditText string_keyword = (EditText) findViewById(R.id.keyword);
        FilterController.setKeywordfilter(string_keyword.getText().toString());
        ElasticSearchController.loadFromElasticSearch();
        FeelTripApplication.getMoodAdapter(getBaseContext()).notifyDataSetChanged();
    }



    private void addItemsOnEmotionalStateSpinner(){ //TODO: Redo the way this is called so the feelings can be dynamically loaded
        emotionalStateSpinner = (Spinner) findViewById(R.id.filterMood);
        List<String> emotionalStateList = new ArrayList<>();
        emotionalStateList.add("None");
        emotionalStateList.add("Angry " + getEmojiByUnicode(emojiUnicode("Angry"))); // TODO: Replace these unicode emojis with image ones.
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
        emotionalStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(position != 0) {
                    FilterController.setEmotionfilter(FeelTripApplication.getEmotionalState(position));
                }
                else {
                    FilterController.setEmotionfilter("");
                }
                ElasticSearchController.loadFromElasticSearch();
                FeelTripApplication.getMoodAdapter(getBaseContext()).notifyDataSetChanged();

            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
    }
    private void updateMap(){
        FeelTripApplication.setFrag("map");
        Fragment fragment = new mapFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragent_frame,fragment);
        fragmentTransaction.commit();
    }
}
