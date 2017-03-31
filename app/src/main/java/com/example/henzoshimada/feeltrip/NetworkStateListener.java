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

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

        if ( activeNetInfo != null )
        {
            if (activeNetInfo.isConnected()){
                // start a service to update local mood event changes
                Intent serviceIntent = new Intent(context, UpdateService.class);
                context.startService(serviceIntent);
            }
        }


    }
}
