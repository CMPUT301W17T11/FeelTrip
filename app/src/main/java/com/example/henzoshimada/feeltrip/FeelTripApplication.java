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

    private static  MoodAdapter moodAdapter = null;
    private static ArrayList<Mood> moodArrayList = new ArrayList<Mood>();
    public static MoodAdapter getMoodAdapter(Context context) {
        if (moodAdapter == null) {
            moodAdapter = new MoodAdapter(moodArrayList, context.getApplicationContext());
        }
        return moodAdapter;
    }

    public static ArrayList<Mood> getMoodArrayList() {
        if(moodArrayList == null) {
            moodArrayList = new ArrayList<Mood>();
        }
        return moodArrayList;
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


}