package com.engineerkoghar.engineerkoghar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by janardan on 7/29/17.
 */

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        if(intent.getExtras()!=null) {
            HomePageActivity.showNotification(context);
            /**
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo!=null && networkInfo.isConnectedOrConnecting()) {
                Log.d("NetworkStateReceiver","Network" + networkInfo.getTypeName() + "connected.");
                //new NotificationClass().startService();
                //ShowNotification.showNotification();
                HomePageActivity.showNotification(context);
            }else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)){
                Log.d("NetworkStateReceiver","There is no network connectivity");
            }*/
        }
    }
}
