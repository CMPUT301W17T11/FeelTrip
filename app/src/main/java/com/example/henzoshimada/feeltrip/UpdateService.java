package com.example.henzoshimada.feeltrip;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Esus2 on 2017-03-26.
 */
public class UpdateService extends IntentService{

    public UpdateService(){
        super("UpdateService");
    }

    /**
      Javadoc cannot be created for this method.
      It calls runUpdate method to do the job when this service is called
     */
    @Override
    protected void onHandleIntent(Intent intent){
        UpdateQueueController updateQueueController = FeelTripApplication.getUpdateQueueController();
        updateQueueController.runUpdate();
    }
}
