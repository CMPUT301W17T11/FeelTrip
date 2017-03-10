package com.example.henzoshimada.feeltrip;

import java.util.ArrayDeque;

/**
 * Created by Esus2 on 2017-03-09.
 */

public class UpdateQueueController {
    private UpdateQueue updateQueue;

    public UpdateQueueController(UpdateQueue updateQueue){
        this.updateQueue = updateQueue;
    }

    public void addMood(Mood mood){
        updateQueue.enQueue(mood);
    }

    public Mood popMood(){
        if (updateQueue.isEmpty()){
            // throw exception
            return null;
        }
        else{
            return updateQueue.deQueue();
        }
    }

    public int getSize(){
        return updateQueue.getSize();
    }
}
