package com.example.henzoshimada.feeltrip;

import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
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

}