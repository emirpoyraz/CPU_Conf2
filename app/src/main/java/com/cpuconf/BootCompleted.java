package com.cpuconf;

/**
 * Created by emir on 4/26/16.
 */
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

// here is the OnRevieve methode which will be called when boot completed
public class BootCompleted extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //we double check here for only boot complete event
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                //here we start the service
                Intent serviceIntent = new Intent(context, MainActivity.class);
                context.startService(serviceIntent);



               // startWakefulService(context, serviceIntent);


            }
        }
    }
