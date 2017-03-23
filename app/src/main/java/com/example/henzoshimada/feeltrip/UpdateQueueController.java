package com.example.henzoshimada.feeltrip;


import android.os.AsyncTask;

/**
 * Created by Esus2 on 2017-03-09.
 */
public class UpdateQueueController {
    private UpdateQueue updateQueue;

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
     * @param mood the mood
     */
    public void addMood(Mood mood){
        updateQueue.enQueue(mood);
    }

    /**
     * Pop mood mood.
     * Get the first mood object from the top of UpdateQueue, the object is then deleted from the queue.
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
     * @return the int
     */
    public int getSize(){
        return updateQueue.getSize();
    }

    public boolean isEmpty(){
        return updateQueue.isEmpty();
    }



    public void runUpdate(){

    }
}
