package com.example.henzoshimada.feeltrip;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Esus2 on 2017-03-26.
 */
public class UpdateService extends IntentService{

    /**
     * Instantiates a new Update service.
     */
    public UpdateService(){
        super("UpdateService");
    }

    /**
     * This overide is to update the queue controller for offline functionality of the app
     */
    @Override
    protected void onHandleIntent(Intent intent){
        UpdateQueueController updateQueueController = FeelTripApplication.getUpdateQueueController();
        updateQueueController.runUpdate();
    }
}
