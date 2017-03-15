package com.example.henzoshimada.feeltrip;

import java.util.ArrayDeque;

/**
 * Created by Esus2 on 2017-03-09.
 */
/* this class implements a queue that stores mood events that need to be
   updated when go back online.
 */

public class UpdateQueue {
    private ArrayDeque<Mood> queue;

    /**
     * Instantiates a new Update queue.
     */
    public UpdateQueue(){
        queue = new ArrayDeque<>();
    }

    /**
     * En queue.
     * Enqueue a mood object to the end of queue
     * @param mood the mood
     */
    public void enQueue(Mood mood){
        queue.addLast(mood);
    }

    /**
     * De queue mood.
     * Remove a mood object from top of queue and return the object
     * @return the mood
     */
    public Mood deQueue(){
        return queue.pollFirst();
    }

    /**
     * Get size int.
     *
     * @return the int
     */
    public int getSize(){
        return queue.size();
    }

    /**
     * Is empty boolean.
     *
     * @return the boolean
     */
    public Boolean isEmpty(){
        return queue.isEmpty();
    }
}
