package com.example.henzoshimada.feeltrip;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;

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

    public static int getNumEmotions() {
        return allEmotions.size() - 1; // MINUS ONE TO COMPENSATE FOR THE ZERO INDEX ELEMENT
    }

    public static String getEmotionalState(int index){
        return allEmotions.get(index);
    }


    // THEMES HERE
    private static int themeID = R.style.DefaultTheme;

    public static int getThemeID() {
        return themeID;
    }

    public static void setThemeID(int themeID) {
        FeelTripApplication.themeID = themeID;
    }

    public static int getCOLORPRIMARY() {
        return COLORPRIMARY;
    }

    public static void setCOLORPRIMARY(int COLORPRIMARY) {
        FeelTripApplication.COLORPRIMARY = COLORPRIMARY;
    }

    public static int getTEXTCOLORPRIMARY() {
        return TEXTCOLORPRIMARY;
    }

    public static void setTEXTCOLORPRIMARY(int TEXTCOLORPRIMARY) {
        FeelTripApplication.TEXTCOLORPRIMARY = TEXTCOLORPRIMARY;
    }

    public static int getTEXTCOLORSECONDARY() {
        return TEXTCOLORSECONDARY;
    }

    public static void setTEXTCOLORSECONDARY(int TEXTCOLORSECONDARY) {
        FeelTripApplication.TEXTCOLORSECONDARY = TEXTCOLORSECONDARY;
    }

    public static int getTEXTCOLORTERTIARY() {
        return TEXTCOLORTERTIARY;
    }

    public static void setTEXTCOLORTERTIARY(int TEXTCOLORTERTIARY) {
        FeelTripApplication.TEXTCOLORTERTIARY = TEXTCOLORTERTIARY;
    }

    private static int COLORPRIMARY = 0xFF3F51B5;

    private static int TEXTCOLORPRIMARY = 0xFF3F51B5;

    private static int TEXTCOLORSECONDARY = 0xFF3F51B5;

    private static int TEXTCOLORTERTIARY = 0xFF3F51B5;

    private static int BACKGROUNDCOLOR = 0xFF3F51B5;

    public static int getBACKGROUNDCOLOR() {
        return BACKGROUNDCOLOR;
    }

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


    private static Participant participant = null;
    /**
     * Get participant participant.
     * call FeelTripApplication.getParticipant() to access current participant
     * @return the participant
     */
    public static Participant getParticipant(){
        if (participant == null){
            participant = new Participant();
        }
        return participant;
    }

    private static FilterController filterController = null;
    public static FilterController getFilterController(){
        if (filterController == null){
            filterController = new FilterController();
        }
        return filterController;
    }

    private static ArrayList<Mood> moodArrayList = new ArrayList<Mood>();

    public static ArrayList<Mood> getMoodArrayList() {
        if(moodArrayList == null) {
            moodArrayList = new ArrayList<Mood>();
        }
        return moodArrayList;
    }

    //request adapter
    private static  RequestAdapter requestAdapter = null;
    private static ArrayList<FollowRequest> requestsArray = new ArrayList<FollowRequest>();
    public static RequestAdapter getRequestAdapter(Context context) {
        if (requestAdapter == null) {
            requestAdapter = new RequestAdapter(requestsArray, context.getApplicationContext());
        }
        return requestAdapter;
    }

    public static ArrayList<FollowRequest> getRequestsArray() {
        if(requestsArray == null) {
            requestsArray = new ArrayList<FollowRequest>();
        }
        return requestsArray;
    }

    //user search adapter
    private static  UserFoundAdapter userFoundAdapter = null;
    private static ArrayList<String> usersFoundArray = new ArrayList<String>();
    public static UserFoundAdapter getUserFoundAdapter(Context context) {
        if (userFoundAdapter == null) {
            userFoundAdapter = new UserFoundAdapter(usersFoundArray, context.getApplicationContext());
        }
        return userFoundAdapter;
    }

    public static ArrayList<String> getUsersFoundArray() {
        if(usersFoundArray == null) {
            usersFoundArray = new ArrayList<String>();
        }
        return usersFoundArray;
    }


    public static void setMoodArrayList(ArrayList<Mood> moodArrayList) {
        FeelTripApplication.moodArrayList = moodArrayList;
    }

    public static String getFrag() {
        return frag;
    }

    private static String frag = "main"; // main by default
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

    public static double getLongitude() {
        return longitude;
    }

    public static void setLongitude(double longi) {
        longitude = longi;
    }

    public static double getLatitude() {
        return latitude;
    }

    public static void setLatitude(double lati) {
        latitude = lati;
    }

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

    public static ListViewAdapter getListViewAdapter(Context context) {
        if (listViewAdapter == null) {
            listViewAdapter = new ListViewAdapter(context);
        }
        return listViewAdapter;
    }

    private static ListViewAdapter listViewAdapter = null;

    public static String username;
    public static String getUserName(){
        return username;
    }

    public static void setUsername(String name){
        username = name;
    }

}