package com.example.henzoshimada.feeltrip;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
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
import android.util.TypedValue;
import android.view.Gravity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.akiniyalocts.minor.MinorLayout;
import com.akiniyalocts.minor.MinorView;
import com.akiniyalocts.minor.behavior.MinorBehavior;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import layout.homeFragmment;
import layout.mapFragment;
import layout.profileFragment;

/**
 * The type Main screen.
 * The main screen contatins everything that the participant will see and use when first
 * logging into the app.
 */
public class MainScreen extends AppCompatActivity{

    private Spinner emotionalStateSpinner;
    private ListView userFoundView;  //who participant searched
    private ListView followingView; //who participant is following
    private ListView requestView;   //who participant wants to follow
    private EditText inputTextView;
    private TextView notFoundTextView;
    private static int NUM_EMOTIONS = FeelTripApplication.getNumEmotions();
    private LinearLayout emojiList;
    private ImageButton emojiButton;
    private TextView emojiTextview;

    //use custom adapter
    private ArrayList<String> usersFoundArray = FeelTripApplication.getUsersFoundArray();
    //private ArrayList<String> followingArray = new ArrayList<String>(); //todo use default adapter
    private ArrayList<String> followingArray = FeelTripApplication.getFollowingArray();
    private ArrayList<FollowRequest> requestsArray; //use custom adabter

    private RequestAdapter requestAdapter;
    //private ArrayAdapter<String> follwingAdapter;
    private FollowingAdapter follwingAdapter;
    private UserFoundAdapter userFoundAdapter;

    private Participant participant = FeelTripApplication.getParticipant();;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    // Taken from http://stackoverflow.com/questions/4828636/edittext-clear-focus-on-touch-outside
    // on 2017-03-27 08:52
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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure you wish to logout?")
                .setTitle("Logout?");

        builder.setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MainScreen.super.onBackPressed();
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
     * This is the method that will run when the Home button is pressed
     * This is what we needed to use so we could have dynamic icons for the different themes
     */

    private void onHomeTouch() {
        Fragment fragment;
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragCurrent = fm.findFragmentById(R.id.fragent_frame);
        Log.d("Mytag","Tapped on home");
        FeelTripApplication.setFrag("main");
        fragment = new homeFragmment();
        ft.replace(R.id.fragent_frame,fragment);
        //ft.addToBackStack(null);
        ft.commit();
        BetterMinorView minorHome = (BetterMinorView)findViewById(R.id.minor_home);
        BetterMinorView minorProfile = (BetterMinorView)findViewById(R.id.minor_profile);
        BetterMinorView minorMaps = (BetterMinorView)findViewById(R.id.minor_maps);
        minorHome.selected();
        minorProfile.unselected();
        minorMaps.unselected();


        if(FeelTripApplication.getThemeID() == R.style.CustomTheme_Light || FeelTripApplication.getThemeID() == R.style.CustomTheme_Dark) {
            minorHome.setTextColor(FeelTripApplication.getCOLORPRIMARY());
            minorHome.setIconColor(FeelTripApplication.getCOLORPRIMARY());
            minorProfile.setTextColor(FeelTripApplication.getTEXTCOLORSECONDARY());
            minorProfile.setIconColor(FeelTripApplication.getTEXTCOLORSECONDARY());
            minorMaps.setTextColor(FeelTripApplication.getTEXTCOLORSECONDARY());
            minorMaps.setIconColor(FeelTripApplication.getTEXTCOLORSECONDARY());
        }

        else if(FeelTripApplication.getThemeID() == R.style.Simplicity) {
            minorHome.setIcon(R.drawable.simplicity_icon_home);
            minorHome.setTextColor(Color.BLACK);
        }

        else if(FeelTripApplication.getThemeID() == R.style.Overwatch) {
            minorHome.setIcon(R.drawable.overwatch_icon_home);
        }

        else if(FeelTripApplication.getThemeID() == R.style.GalaxyTheme) {
            minorHome.setIcon(R.drawable.galaxy_icon_home); //TODO: THEME
        }
        onResume();
    }

    /**
     * This is what will run when the profile button is selected
     * This is what we needed to use so we could have dynamic icons for the different themes
     */
    private void onProfileTouch() {
        Fragment fragment;
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragCurrent = fm.findFragmentById(R.id.fragent_frame);
        Log.d("Mytag","Tapped on profile");
        FeelTripApplication.setFrag("profile");
        fragment = new profileFragment();
        ft.replace(R.id.fragent_frame,fragment);
        //ft.addToBackStack(null);
        ft.commit();
        BetterMinorView minorHome = (BetterMinorView)findViewById(R.id.minor_home);
        BetterMinorView minorProfile = (BetterMinorView)findViewById(R.id.minor_profile);
        BetterMinorView minorMaps = (BetterMinorView)findViewById(R.id.minor_maps);
        minorProfile.selected();
        minorHome.unselected();
        minorMaps.unselected();

        if(FeelTripApplication.getThemeID() == R.style.CustomTheme_Light || FeelTripApplication.getThemeID() == R.style.CustomTheme_Dark) {
            minorHome.setTextColor(FeelTripApplication.getTEXTCOLORSECONDARY());
            minorHome.setIconColor(FeelTripApplication.getTEXTCOLORSECONDARY());
            minorProfile.setTextColor(FeelTripApplication.getCOLORPRIMARY());
            minorProfile.setIconColor(FeelTripApplication.getCOLORPRIMARY());
            minorMaps.setTextColor(FeelTripApplication.getTEXTCOLORSECONDARY());
            minorMaps.setIconColor(FeelTripApplication.getTEXTCOLORSECONDARY());
        }

        else if(FeelTripApplication.getThemeID() == R.style.Simplicity) {
            minorProfile.setIcon(R.drawable.simplicity_icon_profile);
            minorProfile.setTextColor(Color.BLACK);
        }

        else if(FeelTripApplication.getThemeID() == R.style.Overwatch) {
            minorProfile.setIcon(R.drawable.overwatch_icon_profile);
        }

        else if(FeelTripApplication.getThemeID() == R.style.GalaxyTheme) {
        }
        onResume();
    }

    /**
     * This is what will run when the map button is selected
     * This is what we needed to use so we could have dynamic icons for the different themes
     */

    private void onMapsTouch() {
        Fragment fragment;
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragCurrent = fm.findFragmentById(R.id.fragent_frame);
        Log.d("Mytag","Tapped on map");
        FeelTripApplication.setFrag("map");
        fragment = new mapFragment();
        ft.replace(R.id.fragent_frame,fragment);
        //ft.addToBackStack(null);
        ft.commit();
        BetterMinorView minorHome = (BetterMinorView)findViewById(R.id.minor_home);
        BetterMinorView minorProfile = (BetterMinorView)findViewById(R.id.minor_profile);
        BetterMinorView minorMaps = (BetterMinorView)findViewById(R.id.minor_maps);
        minorMaps.selected();
        minorHome.unselected();
        minorProfile.unselected();

        if(FeelTripApplication.getThemeID() == R.style.CustomTheme_Light || FeelTripApplication.getThemeID() == R.style.CustomTheme_Dark) {
            minorHome.setTextColor(FeelTripApplication.getTEXTCOLORSECONDARY());
            minorHome.setIconColor(FeelTripApplication.getTEXTCOLORSECONDARY());
            minorProfile.setTextColor(FeelTripApplication.getTEXTCOLORSECONDARY());
            minorProfile.setIconColor(FeelTripApplication.getTEXTCOLORSECONDARY());
            minorMaps.setTextColor(FeelTripApplication.getCOLORPRIMARY());
            minorMaps.setIconColor(FeelTripApplication.getCOLORPRIMARY());
        }

        else if(FeelTripApplication.getThemeID() == R.style.Simplicity) {
            minorMaps.setIcon(R.drawable.simplicity_icon_maps);
            minorMaps.setTextColor(Color.BLACK);
        }

        else if(FeelTripApplication.getThemeID() == R.style.Overwatch) {
            minorMaps.setIcon(R.drawable.overwatch_icon_maps);
        }

        else if(FeelTripApplication.getThemeID() == R.style.GalaxyTheme) {
        }
        onResume();
    }

    private void setFirstItemNavigationView() {
        final MinorView minorHome = (MinorView)findViewById(R.id.minor_home);
        minorHome.performClick();
    }

    /**
     * The onCreate is what runs when an instance of the MainScreen is first ran
     * It holds everything that is shown on the main screen, all the togglebuttons, and buttons
     * to the other activities, etc.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        setTheme(R.style.NaughtyPenguins); //TODO - theme
//        setTheme(R.style.DefaultTheme);
        setTheme(FeelTripApplication.getThemeID());


        participant = FeelTripApplication.getParticipant();
        setContentView(R.layout.activity_main);
        BetterMinorView minorHome = (BetterMinorView)findViewById(R.id.minor_home);
        minorHome.setText("Home");
        minorHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onHomeTouch();
            }
        });
        BetterMinorView minorProfile = (BetterMinorView)findViewById(R.id.minor_profile);
        minorProfile.setText("Profile");
        minorProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onProfileTouch();
            }
        });
        BetterMinorView minorMaps = (BetterMinorView)findViewById(R.id.minor_maps);
        minorMaps.setText("Maps");
        minorMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMapsTouch();
            }
        });

        setFirstItemNavigationView();
        addItemsOnEmotionalStateSpinner();


        Switch toggleRecent = (Switch) findViewById(R.id.toggleRecent);
        toggleRecent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                //Toast.makeText(getApplicationContext(),"Toggled filter for recent posts!",Toast.LENGTH_SHORT).show();
                FeelTripApplication.getFilterController().setPastweekfilter(isChecked);
                ElasticSearchController.loadFromElasticSearch();
                FeelTripApplication.getListViewAdapter(getBaseContext()).notifyDataSetChanged();
                if (FeelTripApplication.getFrag().equals("map")) {
                    updateMap();
                }
            }
        });

        Switch toggleFriends = (Switch) findViewById(R.id.toggleFriends);
        toggleFriends.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                //Toast.makeText(getApplicationContext(),"Toggled filter for friends!",Toast.LENGTH_SHORT).show();
                FeelTripApplication.getFilterController().setFriendsonlyfilter(isChecked);
                ElasticSearchController.loadFromElasticSearch();
                FeelTripApplication.getListViewAdapter(getBaseContext()).notifyDataSetChanged();
                if (FeelTripApplication.getFrag().equals("map")) {
                    updateMap();
                }
            }
        });

        Switch toggleMostRecent = (Switch) findViewById(R.id.toggleMostRecent);
        toggleMostRecent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                //Toast.makeText(getApplicationContext(),"Toggled filter for friends!",Toast.LENGTH_SHORT).show();
                FeelTripApplication.getFilterController().setMostrecentfilter(isChecked);
                ElasticSearchController.loadFromElasticSearch();
                FeelTripApplication.getListViewAdapter(getBaseContext()).notifyDataSetChanged();
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
                notFoundTextView.setVisibility(View.GONE);
                searchUser(v);
            }
        });
      
        inputTextView = (EditText) findViewById(R.id.user_search);
        userFoundView = (ListView) findViewById(R.id.found_user);
        followingView = (ListView) findViewById(R.id.follow_list);
        requestView = (ListView) findViewById(R.id.request_list);
        notFoundTextView = (TextView) findViewById(R.id.not_found_text);

        if(FeelTripApplication.getThemeID() == R.style.CustomTheme_Light || FeelTripApplication.getThemeID() == R.style.CustomTheme_Dark) {
            android.support.design.widget.AppBarLayout appBarLayout = (android.support.design.widget.AppBarLayout) findViewById(R.id.appBarLayout);
            appBarLayout.setBackgroundColor(FeelTripApplication.getCOLORPRIMARY());

            EditText searchText = (EditText) findViewById(R.id.keyword);
            searchText.getBackground().setColorFilter(FeelTripApplication.getTEXTCOLORTERTIARY(), PorterDuff.Mode.SRC_IN);
            searchText.setHintTextColor(FeelTripApplication.getTEXTCOLORTERTIARY());

            int[][] tintstates = new int[][] {
                    new int[] { android.R.attr.state_pressed},  // pressed
                    new int[] {-android.R.attr.state_pressed}  // unpressed
            };

            int[] tintcolors = new int[] {
                    FeelTripApplication.getTEXTCOLORTERTIARY(),
                    FeelTripApplication.getTEXTCOLORTERTIARY()
            };
            ColorStateList tintList = new ColorStateList(tintstates, tintcolors);
            searchButton.setBackgroundTintList(tintList);

            TextView pastWeekView = (TextView) findViewById(R.id.textView2);
            pastWeekView.setTextColor(FeelTripApplication.getTEXTCOLORTERTIARY());
            TextView moodsFilterView = (TextView) findViewById(R.id.textView3);
            moodsFilterView.setTextColor(FeelTripApplication.getTEXTCOLORTERTIARY());
            TextView mostRecentView = (TextView) findViewById(R.id.textView4);
            mostRecentView.setTextColor(FeelTripApplication.getTEXTCOLORTERTIARY());
            TextView friendsOnlyView = (TextView) findViewById(R.id.textView5);
            friendsOnlyView.setTextColor(FeelTripApplication.getTEXTCOLORTERTIARY());

            minorHome.setIcon(R.drawable.ic_home_black_24dp);
            minorProfile.setIcon(R.drawable.ic_person_black_24dp);
            minorMaps.setIcon(R.drawable.ic_location_searching_black_24dp);


        }

        else if(FeelTripApplication.getThemeID() == R.style.Simplicity) {
            RelativeLayout background = (RelativeLayout) findViewById(R.id.content_main);
            background.setBackgroundResource(R.drawable.simplicity_bg);
            minorHome.setIcon(R.drawable.simplicity_icon_home);
            minorProfile.setIcon(R.drawable.simplicity_icon_profile);
            minorMaps.setIcon(R.drawable.simplicity_icon_maps);
        }

        else if(FeelTripApplication.getThemeID() == R.style.Overwatch) {
            minorHome.setIcon(R.drawable.overwatch_icon_home);
            minorProfile.setIcon(R.drawable.overwatch_icon_profile);
            minorMaps.setIcon(R.drawable.overwatch_icon_maps);
        }

        else if(FeelTripApplication.getThemeID() == R.style.Simplicity) {
            RelativeLayout background = (RelativeLayout) findViewById(R.id.content_main);
            background.setBackgroundResource(R.drawable.simplicity_bg);
            minorHome.setIcon(R.drawable.simplicity_icon_home);
            minorProfile.setIcon(R.drawable.simplicity_icon_profile);
            minorMaps.setIcon(R.drawable.simplicity_icon_maps);
        }

        else if(FeelTripApplication.getThemeID() == R.style.Overwatch) {
            minorHome.setIcon(R.drawable.overwatch_icon_home);
            minorProfile.setIcon(R.drawable.overwatch_icon_profile);
            minorMaps.setIcon(R.drawable.overwatch_icon_maps);
        }

        else if(FeelTripApplication.getThemeID() == R.style.GalaxyTheme) {
            RelativeLayout background = (RelativeLayout) findViewById(R.id.content_main);
            background.setBackgroundResource(R.drawable.galaxy_bg);
            minorHome.setIcon(R.drawable.galaxy_icon_home);
            minorProfile.setIcon(R.drawable.overwatch_icon_profile); // TODO: theme
            minorMaps.setIcon(R.drawable.overwatch_icon_maps);
        }

        else {
            minorHome.setIcon(R.drawable.ic_home_black_24dp);
            minorProfile.setIcon(R.drawable.ic_person_black_24dp);
            minorMaps.setIcon(R.drawable.ic_location_searching_black_24dp);
        }

        setFirstItemNavigationView();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                usersFoundArray.clear();
                userFoundAdapter.notifyDataSetChanged();

                followingArray.clear();
                follwingAdapter.notifyDataSetChanged();
                //load stuff here
                notFoundTextView.setVisibility(View.GONE);
                loadRequestsArray();
                loadFollowingsArray();

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                inputTextView.setText("");
                usersFoundArray.clear();
                userFoundAdapter.notifyDataSetChanged();

                followingArray.clear();
                follwingAdapter.notifyDataSetChanged();

                notFoundTextView.setVisibility(View.GONE);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        //requestAdapter = new RequestAdapter(requestsArray, this); //view,dataArray
        requestAdapter = FeelTripApplication.getRequestAdapter(this);
        requestView.setAdapter(requestAdapter);

        //follwingAdapter = new ArrayAdapter<>(this, R.layout.username_list_item, followingArray); //view,dataArray
        follwingAdapter = FeelTripApplication.getFollowingAdapter(this);
        followingView.setAdapter(follwingAdapter);

        userFoundAdapter = FeelTripApplication.getUserFoundAdapter(this);
        userFoundView.setAdapter(userFoundAdapter);
      
    }

    /**
     * This is the method that is used to search for another user using the search in the
     * side swipe menu for followers/following
     * @param view
     */

    private void searchUser(View view){
        String inputText = inputTextView.getText().toString();
        usersFoundArray.clear();

        // search for participants that matches keyword
        ElasticSearchController.GetParticipantTask getParticipantTask = new ElasticSearchController.GetParticipantTask(true, inputText);
        getParticipantTask.execute();


        // add userNames to userFoundArray
        try {
            ArrayList<Participant> participants = new ArrayList<>();
            participants.addAll(getParticipantTask.get());

            for (Participant foundUser : participants){
                // add foundUser to list except:
                // already followed this user
                // found user is self
                if (!participant.getFollowing().contains(foundUser.getUserName()) &&
                        !foundUser.getUserName().equals(participant.getUserName())){
                    usersFoundArray.add(foundUser.getUserName());
                }
            }
            if (usersFoundArray.size() == 0){
                notFoundTextView.setVisibility(View.VISIBLE);
            }
        } catch (InterruptedException e) {
            return;
        } catch (ExecutionException e) {
            return;
        }
        userFoundAdapter.notifyDataSetChanged();

        Log.d("searchUser","search user click");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Switch toggleFriends = (Switch) findViewById(R.id.toggleFriends);
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
        FeelTripApplication.getListViewAdapter(getBaseContext()).notifyDataSetChanged();

//        loadRequestsArray();
//        loadFollowingsArray();
    }

    /**
     * This is how we load the requests of other users to the current participant to follow their
     * mood events
     */
    private void loadRequestsArray(){//sender
        requestsArray = FeelTripApplication.getRequestsArray();
        requestsArray.clear();

        ElasticSearchController.GetRequestTask getRequestTask = new ElasticSearchController.GetRequestTask(false);
        getRequestTask.execute(FeelTripApplication.getParticipant().getUserName());

        try {
            requestsArray.addAll(getRequestTask.get());
            Log.d("size" , "" + requestsArray.size());
        } catch (InterruptedException e) {
            return;
        } catch (ExecutionException e) {
            return;
        }

        requestAdapter.notifyDataSetChanged();
    }

    /**
     * This is how we load the other users that the current participant is currently following and
     * can see their mood events
     */
    private void loadFollowingsArray(){//receiver
        followingArray.clear();

        // extra work need to be done when other user accepted participant's request
        ElasticSearchController.GetRequestTask getAcceptedRequest = new ElasticSearchController.GetRequestTask(true);
        ElasticSearchController.DeleteRequestTask deleteRequestTask = new ElasticSearchController.DeleteRequestTask();
        ElasticSearchController.EditParticipantTask editParticipantTask = new ElasticSearchController.EditParticipantTask("following");

        getAcceptedRequest.execute(participant.getUserName());
        ArrayList<FollowRequest> acceptedRequests = new ArrayList<>();
        try {
            acceptedRequests.addAll(getAcceptedRequest.get());
        } catch (InterruptedException e) {
            return;
        } catch (ExecutionException e) {
            return;
        }
        for (FollowRequest request : acceptedRequests){
            // add to following list
            participant.addFollowing(request.getReceiver());
            // update following list change to server
            editParticipantTask.execute(participant);
            // then delete this request
            deleteRequestTask.execute(request);
        }
        followingArray.addAll(participant.getFollowing());
        follwingAdapter.notifyDataSetChanged();
    }

    /**
     * Get emoji by unicode string.
     *
     * @param unicode the unicode
     * @return the string
     */
    public String getEmojiByUnicode(int unicode){
        if (unicode == 0) {
            return "";
        }
        return new String(Character.toChars(unicode));
    }

    /**
     * Search keyword.
     * This is the filter function of searching by a particular word in a mood event
     *
     * @param v the v
     */
    public void searchKeyword(View v){
        EditText string_keyword = (EditText) findViewById(R.id.keyword);
        FilterController.setKeywordfilter(string_keyword.getText().toString());
        ElasticSearchController.loadFromElasticSearch();
        FeelTripApplication.getListViewAdapter(getBaseContext()).notifyDataSetChanged();
    }

    private void addItemsOnEmotionalStateSpinner(){ //TODO: Redo the way this is called so the feelings can be dynamically loaded
        emotionalStateSpinner = (Spinner) findViewById(R.id.filterMood);
        List<String> emotionalStateList = new ArrayList<>();
        for(int i = 0; i <= FeelTripApplication.getNumEmotions(); i++) {
            emotionalStateList.add("Filter emotion: " + FeelTripApplication.getEmotionalState(i));
        }

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
                FeelTripApplication.getListViewAdapter(getBaseContext()).notifyDataSetChanged();

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
