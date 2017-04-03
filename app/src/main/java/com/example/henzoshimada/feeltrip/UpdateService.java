package com.example.henzoshimada.feeltrip;

import android.app.IntentService;
import android.content.Intent;

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
     * This override is to call queue controller for offline functionality of the app
     */
    @Override
    protected void onHandleIntent(Intent intent){
        UpdateQueueController updateQueueController = FeelTripApplication.getUpdateQueueController();
        updateQueueController.runUpdate();
    }
}
