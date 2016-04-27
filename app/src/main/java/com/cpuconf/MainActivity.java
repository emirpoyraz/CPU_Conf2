package com.cpuconf;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.DataOutputStream;

public class MainActivity extends AppCompatActivity {


    private Button startService;
    private Button stopService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        int notificationId = 001;
        Intent viewIntent = new Intent(this, Dissatisfaction.class);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.normal)
                        .setContentTitle(getText(R.string.iconized_alert1))
                        .setContentText("Push to rank")
                        .setContentIntent(viewPendingIntent)
                        .setOngoing(true);


        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        notificationManager.notify(notificationId, notificationBuilder.build());



        startService = (Button) this.findViewById(R.id.startServiceId);
        if (startService != null) {
            startService.setOnClickListener(startServiceListener);
        }

        stopService = (Button) this.findViewById(R.id.stopServiceId);
        if (stopService != null) {
            stopService.setOnClickListener(stopServiceListener);


        }
    }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }


        private View.OnClickListener startServiceListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());


                outputStream.writeBytes("setprop debug.gr.calcfps 1\n");
                outputStream.writeBytes("setprop debug.gr.calcfps.period 1\n");
                outputStream.writeBytes("stop\n");
                outputStream.writeBytes("start\n");

                outputStream.flush();
                // outputStream.writeBytes("exit\n");

                Log.d("", "it is gonna get logcat now");
                } catch (Throwable t) {
                    Log.d(" ","Error Occured in ServiceClass: " + t);
                }

               // Intent intent = new Intent(MainActivity.this, ServiceClass.class);
               // MainActivity.this.startService(intent);
            }
        };

        private View.OnClickListener stopServiceListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ServiceClass.class);
                MainActivity.this.stopService(intent);

            }
        };


    }

