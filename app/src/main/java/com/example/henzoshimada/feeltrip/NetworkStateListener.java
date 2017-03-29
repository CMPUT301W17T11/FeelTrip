package com.example.henzoshimada.feeltrip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Esus2 on 2017-03-25.
 */

public class NetworkStateListener extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent )
    {
        //final PendingResult result = goAsync();

        Log.d("debug", "entered");
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

        if ( activeNetInfo != null )
        {
            if (activeNetInfo.isConnected()){
                /*
                Thread thread = new Thread(){
                  @Override
                  public void run(){
                      UpdateQueueController updateQueueController = FeelTripApplication.getUpdateQueueController();
                      updateQueueController.runUpdate();
                  }

                };
                thread.start();
                UpdateQueueController updateQueueController = FeelTripApplication.getUpdateQueueController();
                result.setResultCode(RESULT_OK);
                Log.d("debug", "finished");
                Log.d("debug size is", ""+updateQueueController.getSize());
                result.finish();
                */


                Intent serviceIntent = new Intent(context, UpdateService.class);
                context.startService(serviceIntent);


            }
        }


    }
}
