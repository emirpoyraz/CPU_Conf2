package com.cpuconf;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;

/**
 * Created by emir on 3/23/16.
 */
public class ServiceClass extends Service {


    final private static boolean DBG = Definitions.DBG;
    final public static String TAG = "ServiceClass";

    private static Logger mLogger = new Logger();
    private static FileRepeatReader mRepeatReader;

    private int mScreenBrightness = -1;
  //  private UStats uStats;
    private Applications applications;
    private CPUtil cpUtil;

    private FPS fps;
    final private static String FPS_PATH = "/sdcard/file.log";

    private static final byte[] FPS_DATA = FileRepeatReader.generateReadfileCommand(FPS_PATH);
    private static Vector<Double> fps_log = new Vector<Double>();


    private Random random;

    private static long cpu_conf_time_interval = 30000; // 30 sec to change cpu configuration

    private int cpu_change_counter;

    private long lastTime = System.currentTimeMillis();
    private long lastFileSize = System.currentTimeMillis();
    private static String[] featureList = {"cpuT", "cpu0", "cpu1", "cpu2", "cpu3" , "cpu4" , "cpu5" , "cpu6" , "cpu7" ,
            "greenwall",
            "youtube",
            "videos",
            "universalimageloader",
            "calendar",
            "chrome",
            "deskclock",
            "talk",
            "photos",
            "googlequicksearchbox",
            "googlequicksearchbox:interactor",
            "gms",
            "nfs14_row",
            "r3_na",
            "GoogleCamera",
            "maps",
            "messaging",
            "calculator",
            "music",
            "music",
            "play.games",
            "phone",
            "trafficracer",
            "systemui",
            "googlequicksearchbox:search",
            "mean_FPS", "stdev_FPS", "mean_frametime", "stdev_frametime", "max_frametime",
            "active_core", "active_freq",
            "user_rate"};
                                        // i better write here all applications. i can find them from /data/data
    public int active_core =0;
    public int active_freq=0;

    static {
        try {
            mRepeatReader = new FileRepeatReader(4096);
        } catch (IOException e) {
            if (DBG) {
                Log.e(TAG, "Error creating FileRepeatReader");
                e.printStackTrace();
            }
            mRepeatReader=null;
        }
    }


    public ActivityManager mActivityManager;

    @Override
    public void onCreate() {



        try {




            Logger.createLogFile(this);
           // Logger.createLogFileToUpload(this);
            mLogger.logEntry("Logger On");
            //  mProcessManager.readStats();

            Logger.createArffFile(this);  // for weka ml, arff file format


            Logger.InitiateArffFile(featureList);

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



            //mStatsProc.reset();
         //   uStats = new UStats();

            fps = new FPS();
            applications = new Applications();
            cpUtil = new CPUtil();

            random = new Random();

            mActivityManager = (ActivityManager) this.getSystemService( ACTIVITY_SERVICE );


            /*
            int notificationId = 001;
            Intent viewIntent = new Intent(this, Dissatisfaction.class);
            PendingIntent viewPendingIntent =
                    PendingIntent.getActivity(this, 0, viewIntent, 0);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.on)
                            .setContentTitle(getText(R.string.iconized_alert1))
                            .setContentText("Hello Emir")
                            .setContentIntent(viewPendingIntent);

            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(this);

            notificationManager.notify(notificationId, notificationBuilder.build());

*/

            registerAll();

            mHandler.postDelayed(mRefresh, 1500);
            mCpuHandler.postDelayed(mCpuRefresh,2000);


            try {
                mScreenBrightness = Settings.System.getInt(getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS);
                if (mScreenBrightness >= 0) {
                    if (DBG) {Log.i(TAG, "Screen brightness now " + mScreenBrightness);}
                    //mLogger.logIntEntry(Logger.EntryType.SCREEN_BRIGHTNESS, mScreenBrightness);
                    //
                    //  mLogger.screenBrightness(mScreenBrightness);
                }
            } catch (Settings.SettingNotFoundException e) {
                if (DBG) {Log.e(TAG, "Brightness setting not found");}
            }
        } catch (Throwable t) {
            Log.d(TAG,"Error Occured in ServiceClass: " + t);
        }
    }

    public static FileRepeatReader getRepeatReader() {
        return mRepeatReader;
    }

    public void registerAll(){
     // screen on/off + wifi manager rssi + airplane mode + date + timezone + headset plugged +
        // applications = all of them (music, browser, game?, texting, radio)
        // how much each app is used, optimal configuration...
        // current app info and current cpu conf? also

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        filter.addAction(Intent.ACTION_CAMERA_BUTTON);
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_DEVICE_STORAGE_LOW);
        filter.addAction(Intent.ACTION_DEVICE_STORAGE_OK);
        filter.addAction(Intent.ACTION_GTALK_SERVICE_CONNECTED);
        filter.addAction(Intent.ACTION_GTALK_SERVICE_DISCONNECTED);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(Intent.ACTION_MANAGE_PACKAGE_STORAGE);
        // PUT INTO PHONE STUFF? - skipping for now
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);

        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_UMS_CONNECTED);
        filter.addAction(Intent.ACTION_UMS_DISCONNECTED);

        filter.addAction(AudioManager.VIBRATE_SETTING_CHANGED_ACTION);

        registerReceiver(mBroadcastIntentReceiver, filter);


    }


    public static Logger getLogger() {
        return mLogger;
    }








    BroadcastReceiver mBroadcastIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                if (DBG) {Log.i(TAG,">>>>>>>>>> caught ACTION_SCREEN_OFF <<<<<<<<<<<");	}
                //mLogger.logSimpleEntry(Logger.EntryType.SCREEN_OFF);
                mLogger.logEntry("ScreenOff");
                //  if(wakeLock==null)wakeLock.acquire();

            }
            else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                if (DBG) {Log.i(TAG,">>>>>>>>>> caught ACTION_SCREEN_ON <<<<<<<<<<<");}
                //mLogger.logSimpleEntry(Logger.EntryType.SCREEN_ON);
                mLogger.logEntry("ScreenOn");
                //   if (wakeLock!=null ||  wakeLockisOn) wakeLock.release();
                //    wakeLock=null;

            }

            else if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
                Bundle extra = intent.getExtras();
                int strength = extra.getInt(WifiManager.EXTRA_NEW_RSSI);

                if (DBG) {Log.i(TAG,"Wifi signal strength: " + strength);}
                //mLogger.logIntEntry(Logger.EntryType.WIFI_SIGNAL_STRENGTH, strength);
                mLogger.logEntry("Wifi signal strength: " + strength);
            }

            else if (intent.getAction().equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
                Bundle extra = intent.getExtras();
                if (true == (Boolean) extra.get("state")) {
                    if (DBG) {Log.i(TAG,">>>>>>>>>> AIRPLANE MODE: on <<<<<<<<<<<");}
                    //mLogger.logSimpleEntry(Logger.EntryType.AIRPLANE_MODE_ON);
                    mLogger.logEntry("Airplane_On");
                } else {
                    if (DBG) {Log.i(TAG,">>>>>>>>>> AIRPLANE MODE: off <<<<<<<<<<<");}
                    //mLogger.logSimpleEntry(Logger.EntryType.AIRPLANE_MODE_OFF);
                    mLogger.logEntry("Airplane_Off");
                }
            }


            else if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
                if (DBG) {Log.i(TAG,"Date changed");}
                //mLogger.logSimpleEntry(Logger.EntryType.DATE_CHANGED);
                mLogger.logEntry("Date_changed");
            }

            else if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                Bundle extra = intent.getExtras();
                String state = "";
                switch ((Integer) extra.get("state")) {
                    case 0:
                        state = "unplugged";
                        break;
                    case 1:
                        state = "plugged";
                        break;
                    default:
                        state = "invalid";
                        break;
                }
                if (DBG) {Log.i(TAG,"Headset Plug: " + state);}
                //mLogger.logStringEntry(Logger.EntryType.HEADSET_PLUG, state);
                mLogger.logEntry("Headset_Plug: " + state);
            }

            else if (intent.getAction().equals(Intent.ACTION_TIME_CHANGED)) {
                if (DBG) {Log.i(TAG,"Time changed");}
                //mLogger.logSimpleEntry(Logger.EntryType.TIME_CHANGED);
                mLogger.logEntry("Time_Changed");
            }

            else if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                Bundle extra = intent.getExtras();
                String timezone = (String) extra.get("time-zone");
                TimeZone tz = java.util.TimeZone.getTimeZone(timezone);
                int tz_offset = tz.getRawOffset();
                if (DBG) {Log.i(TAG,"Timezone Change: " + tz_offset);}
                //mLogger.logIntEntry(Logger.EntryType.TIMEZONE_CHANGED, tz_offset);
                mLogger.logEntry("Timezone_Changed: " + tz_offset);
            }

            else if (intent.getAction().equals(Intent.ACTION_GTALK_SERVICE_CONNECTED)) {
                if (DBG) {Log.i(TAG,"GTalk Service Connected");}
                //mLogger.logSimpleEntry(Logger.EntryType.GTALK_SERVICE_CONNECTED);
                mLogger.logEntry("GTalk_Service_Connected");
            }
            else if (intent.getAction().equals(Intent.ACTION_GTALK_SERVICE_DISCONNECTED)) {
                if (DBG) {Log.i(TAG,"GTalk Service Disconnected");}
                //mLogger.logSimpleEntry(Logger.EntryType.GTALK_SERVICE_DISCONNECTED);
                mLogger.logEntry("GTalk_Service_Disconnected");
            }

            // TODO: POSSIBLY STOP LOGGING WHEN STORAGE IS LOW
            else if (intent.getAction().equals(Intent.ACTION_DEVICE_STORAGE_LOW)) {
                if (DBG) {Log.i(TAG,"Device Storage Low");}
                //mLogger.logSimpleEntry(Logger.EntryType.DEVICE_STORAGE_LOW);
                mLogger.logEntry("Device_Storage_Low");
            }
            else if (intent.getAction().equals(Intent.ACTION_DEVICE_STORAGE_OK)) {
                if (DBG) {Log.i(TAG,"Device Storage Ok");}
                //mLogger.logSimpleEntry(Logger.EntryType.DEVICE_STORAGE_OK);
                mLogger.logEntry("Device_Storage_Ok");
            }

            // low memory condition acknowledged by user - start package management
            else if (intent.getAction().equals(Intent.ACTION_MANAGE_PACKAGE_STORAGE)) {
                if (DBG) {Log.i(TAG,"Manage Package Storage");}
                //mLogger.logSimpleEntry(Logger.EntryType.MANAGE_PACKAGE_STORAGE);
                mLogger.logEntry("Manage_Package_Storage");
            }

            else if (intent.getAction().equals(Intent.ACTION_UMS_CONNECTED)) {
                if (DBG) {Log.i(TAG,"UMS Connected");}
                mLogger.logEntry("UMS_Connected");
            }
            else if (intent.getAction().equals(Intent.ACTION_UMS_DISCONNECTED)) {
                if (DBG) {Log.i(TAG,"UMS Connected");}
                mLogger.logEntry("UMS_Disconnected");
            }

            // TODO ensure this vibrate settings thing works
            else if (intent.getAction().equals(AudioManager.VIBRATE_SETTING_CHANGED_ACTION)) {
                if (DBG) {Log.i(TAG,"Vibrate Setting changed");}

            }

            // TODO: maybe add NULL checking to be safe with all extra.get()s
			/*
			// This one will take some more work with configuration DIFF
			filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);

			// PUT INTO PHONE STUFF?
			// REQUIRE PROCESS_OUTGOING_CALLS permission - do we really need this?
			filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
			*/
        }
    };

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service onDestroy", Toast.LENGTH_LONG).show();
        unregisterReceiver(mBroadcastIntentReceiver);
        mHandler.removeCallbacksAndMessages(null);
        mCpuHandler.removeCallbacksAndMessages(null);
        stopSelf();
    }



    private String[] getActivePackagesCompat(){
        final List<ActivityManager.RunningTaskInfo> taskInfo = mActivityManager.getRunningTasks(1);
        final ComponentName componentName = taskInfo.get(0).topActivity;
        final String[] activePackages = new String[1];
        activePackages[0] = componentName.getPackageName();
        return activePackages;
    }


    private String[] getActivePackages(){
        Set<String> activePackages = new HashSet<String>();
        List<ActivityManager.RunningAppProcessInfo> processInfos = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                Log.d(TAG, "Process Name: "+ processInfo.processName);
                activePackages.addAll(Arrays.asList(processInfo.pkgList));

            }
        }
        return activePackages.toArray(new String[activePackages.size()]);
    }



 /*

        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                ActivityManager.RunningAppProcessInfo processName
                if (processInfo.equals("com.google.android.calendar")) {
                    activePackages.addAll(Arrays.asList(processInfo.pkgList));
                }
            }
        }
        return activePackages.toArray(new String[activePackages.size()]);
    }

*/

    private Handler mHandler = new Handler();   // handler is more convenient for massage passing between objects and also UI friendly.
    // so if we need to put some info or even in notifications we may need handler instead of thread.
    Runnable mRefresh = new Runnable() {


        @Override
        public void run() {


            try {



             //   mHandler.postDelayed(mRefresh, 3000);

/*
                String[] activePackages;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) { // so it is lollipop then it is gonna go here!
                    activePackages = getActivePackagesCompat();
                   // Log.d(TAG, activePackages[0]);
                   // Log.d(TAG,"it is in build-1" + activePackages.toString());
                } else {
                    activePackages = getActivePackagesCompat();
                  //  Log.d(TAG,"it is in build-2" + activePackages.toString());
                }
                if (activePackages != null) {
                    for (String activePackage : activePackages) {
                        Log.d(TAG, activePackage);
                        if (activePackage.equals("com.google.android.calendar")) {
                            //Calendar app is launched, do something
                           // Log.d(TAG,"Calender is working");
                        }
                      //  else Log.d(TAG,"Calender is not working");
                    }
                }

*/
             //   UStats.getStats(ServiceClass.this);
             //   uStats.printCurrentUsageStatus(ServiceClass.this);
                Log.d(TAG, "Before and after applicationss: " + System.currentTimeMillis());

                boolean cpu_util, apps, fpss, threads;
                cpu_util = apps = fpss = threads = false;

                //maybe else backup plans here...

                if(cpUtil.readStats()) cpu_util = true;
                applications.getApps();


                if(fps.get_logs(1)) fps.get_FPS_stats();

              //  if(fps.get_FPS_stats()) fpss = true;






              //  get_FPS_stats();


                mLogger.arffEntryLong(active_core);
                mLogger.arffEntryLong(active_freq);

                mLogger.arffEntryLong(DataHolder.getInstance().getUserSatisfaction());

                DataHolder.getInstance().setUserSatisfaction(0);

                // fps





                mLogger.arffEntryNewInstance();


                if(System.currentTimeMillis() - lastFileSize > 360000) { // 1 hour time
                    Log.d(TAG, "File Size: " + Logger.logFileSize(ServiceClass.this));
                   // ServiceClass.getLogger().logEntry("File_Size: " + Logger.logFileSize(ServiceClass.this) + " for: " + (System.currentTimeMillis() - lastFileSize));
                    lastFileSize = System.currentTimeMillis();
                }

              //  mHandler.postDelayed(this, 1000);

/*
                try {



                } catch (Exception e) {
                    Log.i(TAG, "Error occured while collecting sensors " + e);
                }

                // deletes 1 sec after sending and creates new log file


                //Send to phone in every 10 sec

*/
                mHandler.postDelayed(mRefresh, cpu_conf_time_interval/6); // in every 5 sec

            } catch (Exception e) {
                Log.i(TAG, "Error occured " + e);

            }
        }
    };



    private Handler mCpuHandler = new Handler();   // So this handler for randomly changing the cpu configuration


    Runnable mCpuRefresh = new Runnable() {

   // mCpuHandler.postDelayed(new Runnable() {

        @Override
        public void run() {

         //   if (System.currentTimeMillis() - lastTime >= 30000) {
            try {
                    lastTime = System.currentTimeMillis();

                    random = new Random(); // for active core number

                    int i = random.nextInt(9 - 0) + 0;

                    random = new Random(); // for cpu frequency

                    int j = random.nextInt(4 - 0) + 0;

                    active_core = i;
                    active_freq = j;


                Log.d(TAG, "Active core number and frequecy: " + i + " " + j);

               // fps.get_logs(0);


                  //  Log.d(TAG, "It calls fps object");

                    mLogger.logEntry("Active core number and frequecy: " + i + " " + j);


                    // final Process process = Runtime.getRuntime().exec(new String[] { "su", "-c", "getevent -lt /dev/input/event0 > /sdcard/geteventFile" });

                //    Process process = Runtime.getRuntime().exec("su");
                    Process process = Runtime.getRuntime().exec("su");
                    DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());




                // BufferedReader br = new BufferedReader(reader)


                // while(reader.ready()){




               // Process processFPS = Runtime.getRuntime().exec("logcat | grep FPS > /sdcard/file.log");

              //  InputStreamReader reader = new InputStreamReader(processFPS.getInputStream());

/*
                    Log.d(TAG,"fpsss inputStream command0");
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    Log.d(TAG,"fpsss bufferedReader command0 " + reader.read());




                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        //Log.d(TAG,"fpsss inside while loop10: " + line);
                        String fps = line.substring(line.lastIndexOf(':') + 1);
                        Log.d(TAG,"fpsss inside while loop20: " + fps);
                       // fps_log.add(Double.parseDouble(fps));
                    }
*/

                if (i == 1 || i == 0) {

                        try {
                            outputStream.writeBytes("sh /sdcard/1.bash\n");
                            outputStream.writeBytes("exit\n");
                            outputStream.flush();
                         //   outputStream.writeBytes("exit\n");

                            Log.d("", "it is gonna get getevent1");


                        } catch (Exception e) {
                            Log.i(TAG, "Error occured " + e);

                        }
                    } else if (i == 2 || i == 3) {
                        try {

                            outputStream.writeBytes("sh /sdcard/2.bash\n");
                            outputStream.writeBytes("exit\n");
                            outputStream.flush();
                         //   outputStream.writeBytes("exit\n");

                            Log.d("", "it should be good");


                        } catch (Exception e) {
                            Log.i(TAG, "Error occured " + e);

                        }
                    } else if (i == 4 || i == 5) {
                        try {

                            outputStream.writeBytes("sh /sdcard/4.bash\n");
                            outputStream.writeBytes("exit\n");
                            outputStream.flush();
                         //   outputStream.writeBytes("exit\n");

                            Log.d("", "it should be good");


                        } catch (Exception e) {
                            Log.i(TAG, "Error occured " + e);

                        }
                    } else if (i == 6 || i == 7) {

                        try {

                            outputStream.writeBytes("sh /sdcard/6.bash\n");
                            outputStream.writeBytes("exit\n");
                            outputStream.flush();
                          //

                            Log.d("", "it should be good");


                        } catch (Exception e) {
                            Log.i(TAG, "Error occured " + e);

                        }
                    } else if (i == 8 || i == 9) {

                        try {

                            outputStream.writeBytes("sh /sdcard/8.bash\n");
                            outputStream.writeBytes("exit\n");
                            outputStream.flush();
                          //  outputStream.writeBytes("exit\n");

                            Log.d("", "it should be good");


                        } catch (Exception e) {
                            Log.i(TAG, "Error occured " + e);

                        }
                    }

                    // and frequencies start!!
                    if (j == 0) {
                        try {

                            outputStream.writeBytes("sh /sdcard/min.bash\n");
                            outputStream.writeBytes("exit\n");
                            outputStream.flush();
                         //   outputStream.writeBytes("exit\n");

                            Log.d("", "it is min freq");


                        } catch (Exception e) {
                            Log.i(TAG, "Error occured " + e);

                        }
                    } else if (j == 1) {
                        try {

                            outputStream.writeBytes("sh /sdcard/mid.bash\n");
                            outputStream.writeBytes("exit\n");
                            outputStream.flush();
                         //   outputStream.writeBytes("exit\n");

                            Log.d("", "it is mid freq");


                        } catch (Exception e) {
                            Log.i(TAG, "Error occured " + e);

                        }
                    } else if (j == 2) {
                        try {

                            outputStream.writeBytes("sh /sdcard/max.bash\n");
                            outputStream.writeBytes("exit\n");
                            outputStream.flush();
                         //   outputStream.writeBytes("exit\n");

                            Log.d("", "it is max freq");


                        } catch (Exception e) {
                            Log.i(TAG, "Error occured " + e);

                        }
                    } else if (j == 3) {
                        try {

                            outputStream.writeBytes("sh /sdcard/std.bash\n");
                            outputStream.writeBytes("exit\n");
                            outputStream.flush();
                         //   outputStream.writeBytes("exit\n");

                            Log.d("", "it is std freq");


                        } catch (Exception e) {
                            Log.i(TAG, "Error occured " + e);

                        }
                    } else {
                        mCpuHandler.postDelayed(mCpuRefresh, cpu_conf_time_interval);
                    }

               // fps.get_logs(1);
/*
                Process processFPS = Runtime.getRuntime().exec("su");

                DataOutputStream outputStream2 = new DataOutputStream(processFPS.getOutputStream());
                outputStream2.writeBytes("logcat | grep FPS\n");
              //  outputStream2.flush();
                Log.d(TAG, "FPS inputStreamReader it should be good now1");

                //  Process processFPS = Runtime.getRuntime().exec("logcat | grep FPS > /sdcard/file.log");

                InputStreamReader reader = new InputStreamReader(processFPS.getInputStream());

                Log.d(TAG, "FPS inputStreamReader: " + reader.read() + " " + reader.ready());

                BufferedReader bufferedReader = new BufferedReader(reader);

                if(reader.ready()){
                    String line = bufferedReader.readLine();
                    bufferedReader.readLine();
                    String line1 = bufferedReader.readLine();
                    String line2 = bufferedReader.readLine();


                    Log.d(TAG, "BufferedReader lines: \n" + line + "\n " + line1 + "\n " + line2);
                }

              //  processFPS = Runtime.getRuntime().exec("^C");


                outputStream2.writeBytes("^C\n");
                outputStream2.flush();
                Log.d(TAG, "FPS inputStreamReader it should be good now");

                //  Process processFPS = Runtime.getRuntime().exec("logcat | grep FPS > /sdcard/file.log");


                    String line = bufferedReader.readLine();
                    bufferedReader.readLine();
                    String line1 = bufferedReader.readLine();
                    String line2 = bufferedReader.readLine();


                    Log.d(TAG, "BufferedReader lines2: \n" + line + "\n " + line1 + "\n " + line2);

*/

/*
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(processFPS.getInputStream()));
                Log.d(TAG, "BufferedReader is started again");
                String line = "";
                long lastCall = System.currentTimeMillis();
                Log.d(TAG, "BufferedReader is started again11");
                //  while ((line = bufferedReader.readLine()) != null && line.contains("frames")) {

                //  if(System.currentTimeMillis() - lastCall >10000);

                int counter =0;
                while(counter<5) {

                    Log.d(TAG, "BufferedReader is now: " + bufferedReader.readLine());
                    if ((bufferedReader.readLine()) != null) {
                        line = bufferedReader.readLine();
                        Log.d(TAG, "fpsss inside loop1: " + line);
                        String fps = line.substring(line.lastIndexOf(':') + 1);
                        Log.d(TAG, "fpsss inside loop2: " + fps);
                        fps_log.add(Double.parseDouble(fps));

                    }
                    counter++;
                }

                bufferedReader.close();

                Log.d(TAG, "BufferedReader is closed");

                Log.d(TAG, "BufferedReader is closed212");


*/

              //  fps.get_logs(0);
               // fps.get_logs(1);

                fps.get_FPS_initiate();
                fps.get_logs(1);

                Log.d(TAG,"Before and after bef: " + System.currentTimeMillis());
                mCpuHandler.postDelayed(mCpuRefresh, cpu_conf_time_interval);
                Log.d(TAG, "Before and after aft: " + System.currentTimeMillis());

                }catch(Exception e){
                    Log.i(TAG, "Error occured- 11 " + e);


                }
            }

       // }
    };


    public void get_FPS_initiate(){

        File file = new File(FPS_PATH);
        file.delete();


        try {
            Process processFPS = Runtime.getRuntime().exec("logcat | grep FPS > /sdcard/file.log");
            InputStreamReader reader = new InputStreamReader(processFPS.getInputStream());
        }catch (Exception e){

        }
    }


/*
    public void get_FPS_logs() {


        FileRepeatReader mRepeatReader = ServiceClass.getRepeatReader();
        if (mRepeatReader == null) {
            Log.e(TAG, "FPS Frequency file could not be created111");
            //return false;
        } else {
            boolean ret = false;
            try {
                mRepeatReader.lock();
                mRepeatReader.refresh(FPS_DATA);

                FileRepeatReader.SpaceSeparatedLine ssLine;
                String key, key1;
                boolean found = false;
                int freq;

                while (mRepeatReader.hasNextLine()) {
                    ssLine = mRepeatReader.getSSLine();
                    key = ssLine.getToken(11); // either one of them should be frames
                    key1 = ssLine.getToken(12);
                    //if (DBG) {Log.i(TAG, "Key: " + key);}
                    Log.d(TAG, "FPS data path: " + key1 + " " + key + " " + mRepeatReader.getLine());
                    fps_log.add(Double.valueOf(key1));
                    // pre-LogListener:
                    //JamLoggerService.getLogger().logIntEntry(Logger.EntryType.CPU_FREQ, freq);

                }

                if (!found) {
                    // ServiceClass.getLogger().errorOccurred(new ParseException("Could not parse CPU Frequency"));
                }
                mRepeatReader.unlock();
                ret = found;
            } catch (FileNotFoundException e) {

                Log.e(TAG, "CPU Frequency file not found");
                e.printStackTrace();

                ret = false;
            } catch (IOException e) {

                Log.e(TAG, "CPU Frequency file: IOException");
                e.printStackTrace();

                ret = false;
            } catch (NumberFormatException e) {

                Log.e(TAG, "CPU Frequency file: Could not format number");
                e.printStackTrace();

                ret = false;
            } catch (InterruptedException e) {

                Log.e(TAG, "CPU Frequency file: Interrupted during lock");
                e.printStackTrace();

                ret = false;
            } finally {
                mRepeatReader.unlock();
            }

        }
    }

*/

    public void get_FPS_stats() {

        try {
            Double FPS_sum, frametime_sum, mean_FPS, mean_frametime, stdev_FPS, stdev_frametime, max_frametime, temp_FPS;
            FPS_sum = frametime_sum = mean_FPS = mean_frametime = stdev_FPS = stdev_frametime = temp_FPS = 0.0;
            max_frametime = -1.0;
            int count = 0;

            Log.d(TAG, "fps log size is: " + fps_log.size());

            for (int i = 0; i < fps_log.size(); i++) {
                temp_FPS = fps_log.get(i);
                FPS_sum += temp_FPS;
                frametime_sum += 1 / temp_FPS;

                if (1 / temp_FPS > max_frametime) {
                    max_frametime = temp_FPS;
                }

                count++;
            }


            mean_FPS = FPS_sum / count;
            mean_frametime = frametime_sum / count;

            Log.d(TAG, "mean fps is: " + mean_FPS);

            for (int j = 0; j < fps_log.size(); j++) {
                temp_FPS = fps_log.get(j);

                stdev_FPS += (temp_FPS - mean_FPS) * (temp_FPS - mean_FPS);
                stdev_frametime += (1 / temp_FPS - mean_frametime) * (1 / temp_FPS - mean_frametime);
            }

            stdev_FPS = Math.sqrt(stdev_FPS / count);
            stdev_frametime = Math.sqrt(stdev_frametime / count);

            Log.d("FPSInfo: ", "" + mean_FPS + " " + stdev_FPS + " " + mean_frametime + " " + stdev_frametime + " " + max_frametime);

            fps_log.clear();

            //  get_logs();
        } catch (Exception e) {

        }
    }











    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
