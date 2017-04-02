package com.example.henzoshimada.feeltrip;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Esus2 on 2017-03-09.
 */
// Structure of code borrowed from FillerCreep. 2017-03-09

    /*
    this class implements a singleton of UpdateQueue and provides a global access point
    to the queue.
     */
public class FeelTripApplication extends Application {

    private static final List<String> allEmotions = Arrays.asList( // MODIFY THIS IF YOU WISH TO ADD/DELETE EMOTIONS
            "Zero", // THIS IS HERE SO WE CAN REFERENCE THE EMOTIONS WITHOUT WORRYING ABOUT ZERO INDEXING
            "Angry",
            "Confused",
            "Disgusted",
            "Fearful",
            "Happy",
            "Sad",
            "Shameful",
            "Cool");

    /**
     * Gets num emotions.
     *
     * @return the num emotions
     */
    public static int getNumEmotions() {
        return allEmotions.size() - 1; // MINUS ONE TO COMPENSATE FOR THE ZERO INDEX ELEMENT
    }

    /**
     * Get emotional state string.
     *
     * @param index the index
     * @return the string
     */
    public static String getEmotionalState(int index){
        return allEmotions.get(index);
    }


    // THEMES HERE
    private static int themeID = R.style.DefaultTheme;

    /**
     * Gets theme id.
     *
     * @return the theme id
     */
    public static int getThemeID() {
        return themeID;
    }

    /**
     * Sets theme id.
     *
     * @param themeID the theme id
     */
    public static void setThemeID(int themeID) {
        FeelTripApplication.themeID = themeID;
    }

    /**
     * Gets colorprimary.
     *
     * @return the colorprimary
     */
    public static int getCOLORPRIMARY() {
        return COLORPRIMARY;
    }

    /**
     * Sets colorprimary.
     *
     * @param COLORPRIMARY the colorprimary
     */
    public static void setCOLORPRIMARY(int COLORPRIMARY) {
        FeelTripApplication.COLORPRIMARY = COLORPRIMARY;
    }

    /**
     * Gets textcolorprimary.
     *
     * @return the textcolorprimary
     */
    public static int getTEXTCOLORPRIMARY() {
        return TEXTCOLORPRIMARY;
    }

    /**
     * Sets textcolorprimary.
     *
     * @param TEXTCOLORPRIMARY the textcolorprimary
     */
    public static void setTEXTCOLORPRIMARY(int TEXTCOLORPRIMARY) {
        FeelTripApplication.TEXTCOLORPRIMARY = TEXTCOLORPRIMARY;
    }

    /**
     * Gets textcolorsecondary.
     *
     * @return the textcolorsecondary
     */
    public static int getTEXTCOLORSECONDARY() {
        return TEXTCOLORSECONDARY;
    }

    /**
     * Sets textcolorsecondary.
     *
     * @param TEXTCOLORSECONDARY the textcolorsecondary
     */
    public static void setTEXTCOLORSECONDARY(int TEXTCOLORSECONDARY) {
        FeelTripApplication.TEXTCOLORSECONDARY = TEXTCOLORSECONDARY;
    }

    /**
     * Gets textcolortertiary.
     *
     * @return the textcolortertiary
     */
    public static int getTEXTCOLORTERTIARY() {
        return TEXTCOLORTERTIARY;
    }

    /**
     * Sets textcolortertiary.
     *
     * @param TEXTCOLORTERTIARY the textcolortertiary
     */
    public static void setTEXTCOLORTERTIARY(int TEXTCOLORTERTIARY) {
        FeelTripApplication.TEXTCOLORTERTIARY = TEXTCOLORTERTIARY;
    }

    private static int COLORPRIMARY = 0xFF3F51B5;

    private static int TEXTCOLORPRIMARY = 0xFF3F51B5;

    private static int TEXTCOLORSECONDARY = 0xFF3F51B5;

    private static int TEXTCOLORTERTIARY = 0xFF3F51B5;

    private static int BACKGROUNDCOLOR = 0xFF3F51B5;

    /**
     * Gets backgroundcolor.
     *
     * @return the backgroundcolor
     */
    public static int getBACKGROUNDCOLOR() {
        return BACKGROUNDCOLOR;
    }

    /**
     * Sets backgroundcolor.
     *
     * @param BACKGROUNDCOLOR the backgroundcolor
     */
    public static void setBACKGROUNDCOLOR(int BACKGROUNDCOLOR) {
        FeelTripApplication.BACKGROUNDCOLOR = BACKGROUNDCOLOR;
    }

    // Singleton
    private static UpdateQueue updateQueue = null;

    /**
     * Get update queue update queue.
     * Create a UpdateQueue object
     *
     * @return the update queue
     */
    static UpdateQueue getUpdateQueue(){
        if (updateQueue == null){
            updateQueue = new UpdateQueue();
        }
        return updateQueue;
    }

    // Singleton
    private static UpdateQueueController updateQueueController = null;

    /**
     * Get update queue controller update queue controller.
     * Create an UpdateQueue object and return it if it does not exist.
     * return the existing UpdateQueue otherwise
     *
     * @return the update queue controller
     */
    public static UpdateQueueController getUpdateQueueController(){
        if (updateQueueController == null){
            updateQueueController = new UpdateQueueController(getUpdateQueue());
        }
        return updateQueueController;
    }


    private static Boolean networkAvailable = null;

    /**
     * Set network available.
     *
     * @param b the b
     */
    public static void setNetworkAvailable(Boolean b){
        networkAvailable = b;
    }

    /**
     * Get network available boolean.
     *
     * @return the boolean
     */
    public static Boolean getNetworkAvailable(){
        if (networkAvailable == null){
            networkAvailable = Boolean.FALSE;
        }
        return networkAvailable;
    }

    private static ArrayList<Mood> localMainList = null;
    private static ArrayList<Mood> localProfileList = null;

    /**
     * Get local main list array list.
     *
     * @return the array list
     */
    public static ArrayList<Mood> getLocalMainList(){
        if (localMainList == null){
            localMainList = new ArrayList<>();
        }
        return localMainList;
    }

    /**
     * Get local profile list array list.
     *
     * @return the array list
     */
    public static ArrayList<Mood> getLocalProfileList(){
        if (localProfileList == null){
            localProfileList = new ArrayList<>();
        }
        return localProfileList;
    }

    private static Participant participant = null;

    /**
     * Get participant participant.
     * call FeelTripApplication.getParticipant() to access current participant
     *
     * @return the participant
     */
    public static Participant getParticipant(){
        if (participant == null){
            participant = new Participant();
        }
        return participant;
    }

    private static FilterController filterController = null;

    /**
     * Get filter controller filter controller.
     *
     * @return the filter controller
     */
    public static FilterController getFilterController(){
        if (filterController == null){
            filterController = new FilterController();
        }
        return filterController;
    }

    private static ArrayList<Mood> moodArrayList = new ArrayList<Mood>();

    /**
     * Gets mood array list.
     *
     * @return the mood array list
     */
    public static ArrayList<Mood> getMoodArrayList() {
        if(moodArrayList == null) {
            moodArrayList = new ArrayList<Mood>();
        }
        return moodArrayList;
    }

    //request adapter
    private static  RequestAdapter requestAdapter = null;
    private static ArrayList<FollowRequest> requestsArray = new ArrayList<FollowRequest>();

    /**
     * Gets request adapter.
     *
     * @param context the context
     * @return the request adapter
     */
    public static RequestAdapter getRequestAdapter(Context context) {
        if (requestAdapter == null) {
            requestAdapter = new RequestAdapter(requestsArray, context.getApplicationContext());
        }
        return requestAdapter;
    }

    /**
     * Gets requests array.
     *
     * @return the requests array
     */
    public static ArrayList<FollowRequest> getRequestsArray() {
        if(requestsArray == null) {
            requestsArray = new ArrayList<FollowRequest>();
        }
        return requestsArray;
    }

    //user search adapter
    private static  UserFoundAdapter userFoundAdapter = null;
    private static ArrayList<String> usersFoundArray = new ArrayList<String>();

    /**
     * Gets user found adapter.
     *
     * @param context the context
     * @return the user found adapter
     */
    public static UserFoundAdapter getUserFoundAdapter(Context context) {
        if (userFoundAdapter == null) {
            userFoundAdapter = new UserFoundAdapter(usersFoundArray, context.getApplicationContext());
        }
        return userFoundAdapter;
    }

    /**
     * Gets users found array.
     *
     * @return the users found array
     */
    public static ArrayList<String> getUsersFoundArray() {
        if(usersFoundArray == null) {
            usersFoundArray = new ArrayList<String>();
        }
        return usersFoundArray;
    }

    //following adapter
    private static  FollowingAdapter followingAdapter = null;
    private static ArrayList<String> followingArray = new ArrayList<String>();

    /**
     * Gets following adapter.
     *
     * @param context the context
     * @return the following adapter
     */
    public static FollowingAdapter getFollowingAdapter(Context context) {
        if (followingAdapter == null) {
            followingAdapter = new FollowingAdapter(followingArray, context.getApplicationContext());
        }
        return followingAdapter;
    }

    /**
     * Gets following array.
     *
     * @return the following array
     */
    public static ArrayList<String> getFollowingArray() {
        if(followingArray == null) {
            followingArray = new ArrayList<String>();
        }
        return followingArray;
    }


    /**
     * Sets mood array list.
     *
     * @param moodArrayList the mood array list
     */
    public static void setMoodArrayList(ArrayList<Mood> moodArrayList) {
        FeelTripApplication.moodArrayList = moodArrayList;
    }

    /**
     * Gets frag.
     *
     * @return the frag
     */
    public static String getFrag() {
        return frag;
    }

    private static String frag = "main"; // main by default

    /**
     * Sets frag.
     *
     * @param fragstr the fragstr
     */
    public static void setFrag(String fragstr) {
        if(fragstr.equals("main") || fragstr.equals("profile") || fragstr.equals("map")) {
            frag = fragstr;
        }
        else {
            Log.i("Error", "The given search mode is invalid.");
            return;
        }
    }

    //lat var, setter,gett
    //lon
    private static double longitude;
    private static double latitude;

    /**
     * Gets longitude.
     *
     * @return the longitude
     */
    public static double getLongitude() {
        return longitude;
    }

    /**
     * Sets longitude.
     *
     * @param longi the longi
     */
    public static void setLongitude(double longi) {
        longitude = longi;
    }

    /**
     * Gets latitude.
     *
     * @return the latitude
     */
    public static double getLatitude() {
        return latitude;
    }

    /**
     * Sets latitude.
     *
     * @param lati the lati
     */
    public static void setLatitude(double lati) {
        latitude = lati;
    }

    /**
     * Load from elastic search.
     */
    public static void loadFromElasticSearch(){
        Log.d("listTag", "load from ES");
        moodArrayList.clear();
        ElasticSearchController.GetFilteredMoodsTask getMoodTask;
        if(frag.equals("main") || frag.equals("profile") || frag.equals("map")) {
            getMoodTask = new ElasticSearchController.GetFilteredMoodsTask(frag);
        }
        else {
            Log.i("Error", "The given search mode is invalid.");
            return;
        }
        getMoodTask.execute();
        try {
            moodArrayList.addAll(getMoodTask.get());
            Log.d("mood array", "moodArrayList");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets list view adapter.
     *
     * @param context the context
     * @return the list view adapter
     */
    public static ListViewAdapter getListViewAdapter(Context context) {
        if (listViewAdapter == null) {
            listViewAdapter = new ListViewAdapter(context);
        }
        return listViewAdapter;
    }

    private static ListViewAdapter listViewAdapter = null;

    /**
     * The constant username.
     */
    public static String username;

    /**
     * Get user name string.
     *
     * @return the string
     */
    public static String getUserName(){
        return username;
    }

    /**
     * Set username.
     *
     * @param name the name
     */
    public static void setUsername(String name){
        username = name;
    }

}