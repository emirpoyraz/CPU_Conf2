package com.cpuconf;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by emir on 5/7/16.
 */
public class MyWakefulReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Start the service, keeping the device awake while the service is
        // launching. This is the Intent to deliver to the service.
      //  Intent service = new Intent(context, ServiceClass.class);
       // startWakefulService(context, service);
    }
}