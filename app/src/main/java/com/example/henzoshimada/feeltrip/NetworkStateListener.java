package com.example.henzoshimada.feeltrip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Esus2 on 2017-03-25.
 */
public class NetworkStateListener extends BroadcastReceiver{

    /*
        code borrowed from http://stackoverflow.com/questions/1783117/network-listener-android
        Name: Eric
        Date: 2017-03-25
     */

    /**
     * This overrides is to properly handle the intent being received. In this case,
     * intent only filters network connectivity changed.
     *
     * @param context The Context in which the receiver is running
     * @param intent The intent being received
     */
    @Override
    public void onReceive(Context context, Intent intent )
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

        if ( activeNetInfo != null )
        {
            if (activeNetInfo.isConnected()){
                FeelTripApplication.setNetworkAvailable(Boolean.TRUE);

                // start a service to update local mood event changes
                Intent serviceIntent = new Intent(context, UpdateService.class);
                context.startService(serviceIntent);
            }
            else {
                FeelTripApplication.setNetworkAvailable(Boolean.FALSE);
            }
        }
        else {
            FeelTripApplication.setNetworkAvailable(Boolean.FALSE);
        }

    }
}
