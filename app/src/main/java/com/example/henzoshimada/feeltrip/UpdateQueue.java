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

    public UpdateQueue(){
        queue = new ArrayDeque<Mood>();
    }

    public void enQueue(Mood mood){
        queue.addLast(mood);
    }

    public Mood deQueue(){
        return queue.pollFirst();
    }

    public int getSize(){
        return queue.size();
    }

    public Boolean isEmpty(){
        return queue.isEmpty();
    }
}
