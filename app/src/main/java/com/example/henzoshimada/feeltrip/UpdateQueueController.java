package com.example.henzoshimada.feeltrip;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

/**
 * Created by Esus2 on 2017-03-09.
 */
public class UpdateQueueController {
    private static final String FILENAME = "queue.sav";
    private UpdateQueue updateQueue;
    private Context context;

    /**
     * Instantiates a new Update queue controller.
     *
     * @param updateQueue the update queue
     */
    public UpdateQueueController(UpdateQueue updateQueue){
        this.updateQueue = updateQueue;
    }

    /**
     * Add mood.
     * Add a mood object to the end of UpdateQueue
     *
     * @param mood the mood
     */
    public void addMood(Mood mood){
        updateQueue.enQueue(mood);
        saveInFile();
    }

    public void addAllMood(ArrayList<Mood> moods){
        for (Mood mood : moods){
            addMood(mood);
        }
    }

    /**
     * Pop mood mood.
     * Get the first mood object from the top of UpdateQueue, the object is then deleted from the queue.
     *
     * @return the mood
     */
    public Mood popMood(){
        if (this.isEmpty()){
            return null;
        }
        else{
            return updateQueue.deQueue();
        }
    }

    /**
     * Get size int.
     * Get the current number of elements in the UpdateQueue
     *
     * @return the int
     */
    public int getSize(){
        return updateQueue.getSize();
    }

    /**
     * Is empty boolean.
     *
     * @return the boolean
     */
    public boolean isEmpty(){
        return updateQueue.isEmpty();
    }


    /**
     * This method will be called from the Update service when internet connection is resumed
     * it goes through the update queue and do add/update/delete accordingly.
     */
    public void runUpdate(){

        if (updateQueue.isEmpty()){
            return;
        }
        Mood mood;

        /* does not get size dynamically because if internet goes off during update,
           moods will be re-enqueued to updateQueue and the loop never stops
         */
        int size = getSize();
        while (size > 0){
            mood = popMood();

            // this is a new mood
            if (mood.getId().equals("-1")){
                ElasticSearchController.AddMoodTask addMoodTask = new ElasticSearchController.AddMoodTask();
                addMoodTask.execute(mood);
            }
            else{
                // this mood needs to be deleted
                if (mood.getDelState()){
                    ElasticSearchController.DeleteMoodTask deleteMoodTask = new ElasticSearchController.DeleteMoodTask();
                    deleteMoodTask.execute(mood);
                }

                // this mood needs to be edited
                else{
                    ElasticSearchController.EditMoodTask editMoodTask = new ElasticSearchController.EditMoodTask();
                    editMoodTask.execute(mood);
                }
            }
            size--;
        }

        context.deleteFile(FILENAME);
        saveInFile();
    }




    public void loadFromFile() {
        ArrayList<Mood> moods;
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Mood>>(){}.getType();
            moods = gson.fromJson(in,listType);
            addAllMood(moods);

        } catch (FileNotFoundException e) {
            return;
        }
    }


    public void saveInFile() {
        if (getSize() == 0){
            return;
        }
        ArrayList<Mood> moods;
        moods = updateQueue.toArray();

        try {
            FileOutputStream fos = context.openFileOutput(FILENAME,
                    Context.MODE_PRIVATE);

            BufferedWriter out = new BufferedWriter((new OutputStreamWriter(fos)));

            Gson gson = new Gson();
            gson.toJson(moods, out);
            out.flush();

            fos.close();
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void setContext(Context context){
        this.context = context;
    }
}
