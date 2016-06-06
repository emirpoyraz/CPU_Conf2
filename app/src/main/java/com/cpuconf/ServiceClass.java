package com.cpuconf;

import android.annotation.TargetApi;
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
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.trees.M5P;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ConverterUtils;
import weka.gui.experiment.DatasetListPanel;

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
    private RunningProcs runningProcs;
    private VoltageNow voltageNow;
    private CPUFreq cpuFreq;
    private Network network;

    private boolean firstTime = true;
    private String fpsline;

    private FPS fps;
    final private static String FPS_PATH = "/sdcard/file.log";

   private static long appStartTime = System.currentTimeMillis();

    private Instances data;
    private LibSVM svm;
    private M5P m5p;

    private  String currentConf="44std";

    private static final String ARFF_FILE_NAME = "CpuConfLogs.arff";
    private static final String FPS_CHECKING = "FPS.log";

    final private static Object mLogLock = new Object();

    private boolean modelIsCreated = false;

    private static final byte[] FPS_DATA = FileRepeatReader.generateReadfileCommand(FPS_PATH);
    private static Vector<Double> fps_log = new Vector<Double>();

    private Random random;
    private static Vector<String> blackListConf = new Vector<String>();

    private int log_counter=1;
    private String activeCore = "1min";
    private boolean standardGov= true;

    private static long cpu_conf_time_interval = 30000; // 30 sec to change cpu configuration

    private int cpu_change_counter;

    private int order =0;

    ArrayList<String> cpuConfList = new ArrayList<String>(Arrays.asList(
            "20min", "20mid", "20std",
            "02min","02mid","02std",
            "40min", "40mid", "40std",
            "04min","04mid","04std",
            "42min", "42mid", "42std",
            "24min","24mid","24std",
            "44min","44mid","44std"));

    private long lastTime = System.currentTimeMillis();
    private long lastFileSize = System.currentTimeMillis();
    private static String[] featureList = {"CpuUtilT", "CpuUtil0", "CpuUtil1", "CpuUtil2",
            "CpuUtil3" , "CpuUtil4" , "CpuUtil5" , "CpuUtil6" , "CpuUtil7" ,"CpuFreq0", "CpuFreq1", "CpuFreq2",
            "CpuFreq3" , "CpuFreq4" , "CpuFreq5" , "CpuFreq6" , "CpuFreq7" ,
            "mean_FPS", "std_FPS", "mean_f_time", "stdev_f_time", "max_f_time",
            "act_proc",
            "wifi",// totally 41 features
            "satisfaction"};

    private static String[] featureListStaticModel = {"CpuUtilT","CpuUtil3", "CpuUtil7", "CpuFreq0", "CpuFreq1", "CpuFreq2",
            "CpuFreq3" , "CpuFreq4" , "CpuFreq5" , "CpuFreq6" , "CpuFreq7" ,
            "mean_FPS", "std_FPS", "mean_f_time", "stdev_f_time", "max_f_time","act_proc","wifi",
            "satisfaction"};
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

            Logger.createFpsFile(this);

            File log_file = new File(this.getFilesDir(), ARFF_FILE_NAME);

            Log.d(TAG, "File length is: " + log_file.length());

            if (log_file.length()<=20) {

                Logger.InitiateArffFile(featureListStaticModel);
            }



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



          //  PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
          //  PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
           //         "MyWakelockTag");
           // wakeLock.acquire();


            //mStatsProc.reset();
            //   uStats = new UStats();

            fps = new FPS();
          //  applications = new Applications();
            cpUtil = new CPUtil();
            runningProcs = new RunningProcs();
            voltageNow = new VoltageNow();
            cpuFreq = new CPUFreq();
            network = new Network();

            random = new Random();

            Collections.shuffle(cpuConfList);

            mActivityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);


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

            mHandler.postDelayed(mRefresh, 2000);
          //  mCpuHandler.postDelayed(mCpuRefresh, 2000);


            mScreenBrightness = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
            if (mScreenBrightness >= 0) {
                if (DBG) {
                    Log.i(TAG, "Screen brightness now " + mScreenBrightness);
                }
                //mLogger.logIntEntry(Logger.EntryType.SCREEN_BRIGHTNESS, mScreenBrightness);
                //
                //  mLogger.screenBrightness(mScreenBrightness);
            }
        } catch (Settings.SettingNotFoundException e) {
            if (DBG) {
                Log.e(TAG, "Brightness setting not found");
            }
        } catch (Throwable t) {
            Log.d(TAG, "Error Occured in ServiceClass: " + t);
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
                blackListConf.clear();  // screen is turned off, so that the black list will start from beginning, workload changes
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
    //    mCpuHandler.removeCallbacksAndMessages(null);

        Intent service = new Intent(this, ServiceClass.class);
        MyWakefulReceiver.completeWakefulIntent(service);

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

    Handler mHandler = new Handler();   // handler is more convenient for massage passing between objects and also UI friendly.
    // so if we need to put some info or even in notifications we may need handler instead of thread.
    Runnable mRefresh = new Runnable() {


        @Override
        public void run() {


            try {




             //   UStats.getStats(ServiceClass.this);
             //   uStats.printCurrentUsageStatus(ServiceClass.this);
                Log.d(TAG, "Before and after applicationss: " + System.currentTimeMillis());

                boolean cpu_util, apps, fpss;
                cpu_util = apps = fpss = false;

                //cpu util

               // int arffWrite =0;
              //  if(System.currentTimeMillis()-appStartTime>=3000) arffWrite=1;

                double[] cpuUtils = new double[8];

                cpuUtils = cpUtil.readStats();

                if(cpuUtils!=null) cpu_util = true;

                double[] cpuFreqs = cpuFreq.readStats();


                // all 25 applications

             //   long[] appsList = new long[25];

             //   appsList = applications.getApps();

              //  if(appsList!=null) apps = true;

                // fps data

                fps.get_logs();

                Log.d(TAG, "BufferedReader comes from Service class");

                double[] fpsStats = new double[5];


                fpsStats = fps.get_FPS_stats();

                if (fpsStats != null) fpss = true;



              //  runnning processes

                long runnPro =0;

                runnPro = runningProcs.readStats();

                int[] wifiBlu = network.readWifi();

                int core,freq;

                if(currentConf.contains("02")||currentConf.contains("20")){
                    core = 2;
                }
                else if(currentConf.contains("24")||currentConf.contains("42")){
                    core = 6;
                }
                else if(currentConf.contains("04")||currentConf.contains("40")){
                    core = 4;
                }
                else if(currentConf.contains("24")||currentConf.contains("42")){
                    core = 6;
                }
                else core =8;

                if(currentConf.contains("min")){
                    freq=0;
                }
                else if(currentConf.contains("mid")){
                    freq=1;
                }
                else freq=2;




             //   int nbRunning = 0;
             //   for (Thread t : Thread.getAllStackTraces().keySet()) {
             //       if (t.getState()==Thread.State.RUNNABLE) nbRunning++;
            //    }

               if(cpu_util && fpss) { // all file writing part!!!

                   for (int i = 0; i < cpuUtils.length; i++) {
                      // mLogger.arffEntryDouble(cpuUtils[i]);
                       //    mLogger.logEntry("CPU"+i+" " + cpuUtils[i] + " " + cpuFreqs[i]);
                   }
                   mLogger.arffEntryDouble(cpuUtils[0]);
                   mLogger.arffEntryDouble(cpuUtils[3]);
                   mLogger.arffEntryDouble(cpuUtils[7]);


                   for (int b = 0; b < cpuFreqs.length; b++) {
                       mLogger.arffEntryDouble(cpuFreqs[b]);
                       //    mLogger.logEntry("CPU"+i+" " + cpuUtils[i] + " " + cpuFreqs[i]);
                   }

                   mLogger.logEntry("Active core number and frequecy: " + activeCore);


                   mLogger.logEntry("CPUtils:" + " " + cpuUtils[0] + " " + cpuUtils[1] + " " + cpuUtils[2] + " " + cpuUtils[3] + " "
                           + cpuUtils[4] + " " + cpuUtils[5] + " " + cpuUtils[6] + " " + cpuUtils[7] + " " + cpuUtils[8]);

                   mLogger.logEntry("CPUFreqs:" + " " + cpuFreqs[0] + " " + cpuFreqs[1] + " " + cpuFreqs[2] + " " + cpuFreqs[3] + " "
                           + cpuFreqs[4] + " " + cpuFreqs[5] + " " + cpuFreqs[6] + " " + cpuFreqs[7]);


                   Log.d(TAG, "CPU_freq_utilStarts: ");
                   Log.d(TAG, "CPU_freq_util0: " + cpuFreqs[0] + "   " + cpuUtils[1]);
                   Log.d(TAG, "CPU_freq_util1: " + cpuFreqs[1] + "   " + cpuUtils[2]);
                   Log.d(TAG, "CPU_freq_util2: " + cpuFreqs[2] + "   " + cpuUtils[3]);
                   Log.d(TAG, "CPU_freq_util3: " + cpuFreqs[3] + "   " + cpuUtils[4]);
                   Log.d(TAG, "CPU_freq_util4: " + cpuFreqs[4] + "   " + cpuUtils[5]);
                   Log.d(TAG, "CPU_freq_util5: " + cpuFreqs[5] + "   " + cpuUtils[6]);
                   Log.d(TAG, "CPU_freq_util6: " + cpuFreqs[6] + "   " + cpuUtils[7]);
                   Log.d(TAG, "CPU_freq_util7: " + cpuFreqs[7] + "   " + cpuUtils[8]);
                   Log.d(TAG, "CPU_freq_utilEnds: ");


/*
                   for (int j = 0; j < appsList.length; j++) {
                       mLogger.arffEntryLong(appsList[j]);
                   }

                   mLogger.logEntry("AppsAll:" + " " + appsList[0] + " " + appsList[1] + " " + appsList[2] + " " + appsList[3] + " "
                           + appsList[4] + " " + appsList[5] + " " + appsList[6] + " " + appsList[7] + " " + appsList[8] + " " +
                           appsList[9] + " " + appsList[10] + " " + appsList[11] + " " + appsList[12] + " " + appsList[13] + " "
                           + appsList[14] + " " + appsList[15] + " " + appsList[16] + " " + appsList[17] + " " + appsList[18] + " "
                           + appsList[19] + " " + appsList[20] + " " + appsList[21] + " " + appsList[22] + " " + appsList[23] + " "
                           + appsList[24]);
*/

                   for (int k = 0; k < fpsStats.length; k++) {
                       mLogger.arffEntryDouble(fpsStats[k]);
                   }


                   mLogger.logEntry("FPSAll:" + " " + fpsStats[0] + " " + fpsStats[1] + " " + fpsStats[2]
                           + " " + fpsStats[3] + " " + fpsStats[4]);


                   mLogger.arffEntryLong(runnPro);

                   mLogger.logEntry("RunningProc:" + " " + runnPro);

                //   mLogger.arffEntryLong(core);
                //   mLogger.arffEntryLong(freq);


                   mLogger.arffEntryDouble(wifiBlu[0]);

                  // for (int a = 0; a < wifiBlu.length; a++) {
                  //     mLogger.arffEntryDouble(wifiBlu[a]);
                       //    mLogger.logEntry("CPU"+i+" " + cpuUtils[i] + " " + cpuFreqs[i]);
                 //  }


                   mLogger.logEntry("WifiBlue:" + " "+ wifiBlu[0] + " " + wifiBlu[1] + " "+ wifiBlu[2] +" "+ wifiBlu[3]+
                   " "+  wifiBlu[4] +" "+ wifiBlu[5]);

                   //  mLogger.arffEntryLong(active_core);
                   //   mLogger.arffEntryLong(active_freq);


                    mLogger.logEntry("core: " + core);
                   mLogger.logEntry("freq: " + freq);

                   //  new instance




                   //  long current_now = getCurrent();
                   long voltage =0;
                   long current_now = 0;
                   double power =0;

                   try {
                       voltage = voltageNow.readVoltage();
                       current_now = voltageNow.readCurrent();
                       power = current_now * voltage / 1000000000; // 10^9
                       Log.d(TAG, "Power: " + power);
                   }catch (Exception e){
                       Log.d(TAG,"Voltage Error: " + e);
                   }


                   //   mLogger.logEntry("Current1: " + current_now);

                   mLogger.logEntry("CurrentNow: " + current_now);

                   mLogger.logEntry("VoltageNow: " + voltage);

                   mLogger.logEntry("PowerCurAvg: " + power);

                   mLogger.arffEntryLongLast(DataHolder.getInstance().getUserSatisfaction());

                   mLogger.logEntry("StGovernor: " + String.valueOf(standardGov));

                   mLogger.logEntry("UserSatisfaction: " + DataHolder.getInstance().getUserSatisfaction());

                   DataHolder.getInstance().setUserSatisfaction(0);



                   Date date = new Date();
                   mLogger.logEntry("Time: " + date + " in milisec: " + System.currentTimeMillis());

                   mLogger.logEntry("End_of_log: " + log_counter);

                   log_counter++;

                   mLogger.arffEntryNewInstance();

               }
                // check if 2 days data were collected.

             //   if(System.currentTimeMillis()-appStartTime >= 86400000 ) { // 24 hour time

             //   if(System.currentTimeMillis()-appStartTime >= 60000 ) { // 24 hour time


           //     if (Logger.logFileSize(ServiceClass.this) > Definitions.TRAINING_SIZE_THRESHOLD && !modelIsCreated) {
                if (!modelIsCreated) {
              //  if (appStartTime-System.currentTimeMillis()>=86400000/2) {

                        Log.d(TAG, "Weka started: ");
                        createModel();
                        Log.d(TAG, "Weka ended: ");
                       modelIsCreated = true;
                        mLogger.logEntry("TrainingData for 12 hours: "+ Logger.logFileSize(ServiceClass.this));


                }
                if(modelIsCreated){

                    Log.d(TAG, "Weka prediction started: ");
                    makePredictions();
                    Log.d(TAG, "Weka prediction ended: ");

                }



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
                mHandler.postDelayed(mRefresh, cpu_conf_time_interval / 3); // in every 10 sec

            } catch (Exception e) {
                Log.i(TAG, "Error occured " + e);

            }
        }
    };





    private void createModel() {
        synchronized (mLogLock) {
            try {



                Log.d(TAG, "Weka createModel1 ");



           //     BufferedReader reader = new BufferedReader(new FileReader("/data/data/com.cpuconf/files/CpuConfLogs.arff"));


            //    ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource("/data/data/com.cpuconf/files/CpuConfLogs.arff");


                ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource("/sdcard/TotalStatic4.arff");


                data = dataSource.getDataSet();

                data.setClassIndex(data.numAttributes()-1);



            //    data = new Instances(reader);

             //   reader.close();

                // setting class attribute if the data format does not provide this information
                // For example, the XRFF format saves the class attribute information as well
             //   if (data.classIndex() == -1)
             //       data.setClassIndex(data.numAttributes() - 1);

                Log.d(TAG, "Weka createModel11: " + data.numInstances());


            //    svm = new LibSVM();
            //    svm.setSVMType(new SelectedTag(LibSVM.SVMTYPE_EPSILON_SVR,
            //            LibSVM.TAGS_SVMTYPE));
            //    svm.buildClassifier(data);

                m5p = new M5P();
                m5p.buildClassifier(data);




                //fc = new FilteredClassifier();
                // fc.setClassifier(svm);

                Log.d(TAG, "Weka createModel12: ");


                Evaluation eTest = new Evaluation(data);
                eTest.evaluateModel(m5p, data);

                Log.d(TAG, "Weka createModel13: " + m5p.getMinNumInstances());

                Log.d(TAG, "Results: " + eTest.correct());

            } catch (Exception e) {
                Log.d(TAG, "Weka there is an error in model: " +e );

                mLogger.logEntry("Errorrr in this line: " + e);

            }
        }
    }


    private void makePredictions() {
        Log.d(TAG, "Weka prediction starts insode: " );
        try {
        //    for (int i = 0; i < data.numInstances(); i++) {

                // double pred = svm.classifyInstance(data.instance(i));
             //   double pred = svm.classifyInstance(data.instance(data.numInstances()-1));

            ConverterUtils.DataSource dataSource1 = new ConverterUtils.DataSource("/data/data/com.cpuconf/files/CpuConfLogs.arff");

            Instances testData = dataSource1.getDataSet();

            testData.setClassIndex(testData.numAttributes()-1);

          //  Instance testing = data.instance(data.numInstances() - 1);





             //   testing.setMissing(testing.numAttributes() - 1);

           //     Log.d(TAG, "Weka actual: " + testData.classAttribute().value((int) testData.classValue()));

                Log.d(TAG, "Weka actual: " + testData.instance(testData.numInstances()-1).classValue());

                Instance newInst= testData.lastInstance();

            Log.d(TAG,"Weka last Instance1: "+ newInst);


            double pred = m5p.classifyInstance(newInst);

                Log.d(TAG,"Weka pred1: " + pred);

                Log.d(TAG, "Weka pred: " + testData.classAttribute().value((int) pred));


          if(!standardGov) {


              if (pred >= 1.0) {
                  if(!blackListConf.contains(currentConf)) {
                      blackListConf.add(currentConf);
                      Log.d(TAG, "BlackList: " + currentConf);
                  }
                  increaseCore();

              } else if (pred >= 0.5) {
                  increaseFreq();
              } else if (pred <= 0.3) {
                  decreaseFreqCore();
              }
          }
            else currentConf = "44std";


            //    Log.d(TAG, "Weka ID: " + data.instance(data.numInstances() - 1));
            //    Log.d(TAG,"Weka actual: " + data.classAttribute().value((int) data.instance(data.numInstances()-1).classValue()));
            //    Log.d(TAG, "Weka predicted: " + data.classAttribute().value((int) pred));
            //    Log.d(TAG, "Weka , i: " + (data.numAttributes()-1));
         //   }


         //   mLogger.logEntry("Weka actual: " + data.classAttribute().value((int) data.instance(data.numInstances()-1).classValue()));
         //   mLogger.logEntry("Weka predicted: " + data.classAttribute().value((int) pred));


        } catch (Exception e) {
            Log.d(TAG, "Weka , error: " + e);

        }
    }

    private void increaseCore() {
        if(currentConf.equalsIgnoreCase("20min") || currentConf.equalsIgnoreCase("02min") ){
            currentConf="40min";
        }
        else if(currentConf.equalsIgnoreCase("40min") || currentConf.equalsIgnoreCase("04min") ){
            currentConf="24min";
        }
        else currentConf ="44min";

    }

    private void increaseFreq() {
        if(currentConf.equalsIgnoreCase("20min") || currentConf.equalsIgnoreCase("02min") ){
            currentConf="20mid";
        }

        else if(currentConf.equalsIgnoreCase("20mid") || currentConf.equalsIgnoreCase("02mid") ){
            currentConf="20std";
        }

        else if(currentConf.equalsIgnoreCase("20std") || currentConf.equalsIgnoreCase("02std") ){
            currentConf="40min";
        }

        else if(currentConf.equalsIgnoreCase("40min") || currentConf.equalsIgnoreCase("04min")){
            currentConf="40mid";
        }

        else if(currentConf.equalsIgnoreCase("40mid") || currentConf.equalsIgnoreCase("04mid") ){
            currentConf="40std";
        }

        else if(currentConf.equalsIgnoreCase("24min") || currentConf.equalsIgnoreCase("42min") ){
            currentConf="42mid";
        }

      //  else currentConf ="44min";

    }

    private void decreaseFreqCore(){
        Log.d(TAG, "Weka decreaseFreqCore");
        if(currentConf.equalsIgnoreCase("44std") || currentConf.equalsIgnoreCase("44mid") ||
                currentConf.equalsIgnoreCase("44min") || currentConf.equalsIgnoreCase("44mid")){
            currentConf="24std";
        }

        else if((currentConf.equalsIgnoreCase("42mid") || currentConf.equalsIgnoreCase("24mid") ||
                currentConf.equalsIgnoreCase("42std") || currentConf.equalsIgnoreCase("24std")) && !blackListConf.contains("40std")){
            currentConf="40std";
        }

        else if((currentConf.equalsIgnoreCase("42min") || currentConf.equalsIgnoreCase("24min")) && !blackListConf.contains("40mid") ){
            currentConf="40mid";
        }

        else if((currentConf.equalsIgnoreCase("40mid") || currentConf.equalsIgnoreCase("04mid") ||
                currentConf.equalsIgnoreCase("40std") || currentConf.equalsIgnoreCase("04std")) && !blackListConf.contains("40min")){
            currentConf="40min";
        }

        else if((currentConf.equalsIgnoreCase("40min") || currentConf.equalsIgnoreCase("04min")) && !blackListConf.contains("20mid")) {
            currentConf="20mid";
        }

        else if((currentConf.equalsIgnoreCase("20std") || currentConf.equalsIgnoreCase("20mid") ||
                currentConf.equalsIgnoreCase("02std") || currentConf.equalsIgnoreCase("02mid")) && !blackListConf.contains("20min")){
            currentConf="20min";
        }

       // else currentConf ="44std";

    }


    private boolean blackList(String s){
        if(blackListConf.contains(s)){
            return true;
        }
        else return false;
    }






    private String shuffleConfs(int order){

        String returnValue = cpuConfList.get(order);


        if(order>=20){

            Collections.shuffle(cpuConfList);
        }

        return returnValue;

    }



  //  private Handler mCpuHandler = new Handler();   // So this handler for randomly changing the cpu configuration


    Runnable mCpuRefresh = new Runnable() {

   // mCpuHandler.postDelayed(new Runnable() {

        @Override
        public void run() {

         //   if (System.currentTimeMillis() - lastTime >= 30000) {
            try {
                    lastTime = System.currentTimeMillis();

    /*
                    random = new Random(); // for active core number

                    int i = random.nextInt(7 - 0) + 0;

                    random = new Random(); // for cpu frequency

                    int j = random.nextInt(3 - 0) + 0;

                    active_core = i;
                    active_freq = j;

    */
              //   currentConf = shuffleConfs(order);


                activeCore = currentConf;


                Log.d(TAG, "Active core number and frequecy: " + currentConf);





                    // final Process process = Runtime.getRuntime().exec(new String[] { "su", "-c", "getevent -lt /dev/input/event0 > /sdcard/geteventFile" });

                //    Process process = Runtime.getRuntime().exec("su");
                    Process process = Runtime.getRuntime().exec("su");
                    DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());
                  //  DataOutputStream outputStream = null;





                if(currentConf.equalsIgnoreCase("20min")){
                    try {
                        outputStream.writeBytes("sh /sdcard/20.bash\n");
                        outputStream.writeBytes("sh /sdcard/min.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("20mid")){
                    try {
                        outputStream.writeBytes("sh /sdcard/20.bash\n");
                        outputStream.writeBytes("sh /sdcard/mid.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("20std")){
                    try {
                        outputStream.writeBytes("sh /sdcard/20.bash\n");
                        outputStream.writeBytes("sh /sdcard/std.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("02min")){
                    try {
                        outputStream.writeBytes("sh /sdcard/02.bash\n");
                        outputStream.writeBytes("sh /sdcard/min.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("02mid")){
                    try {
                        outputStream.writeBytes("sh /sdcard/02.bash\n");
                        outputStream.writeBytes("sh /sdcard/mid.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("02std")){
                    try {
                        outputStream.writeBytes("sh /sdcard/02.bash\n");
                        outputStream.writeBytes("sh /sdcard/std.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("40min")){
                    try {
                        outputStream.writeBytes("sh /sdcard/40.bash\n");
                        outputStream.writeBytes("sh /sdcard/min.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("40mid")){
                    try {
                        outputStream.writeBytes("sh /sdcard/40.bash\n");
                        outputStream.writeBytes("sh /sdcard/mid.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("40std")){
                    try {
                        outputStream.writeBytes("sh /sdcard/40.bash\n");
                        outputStream.writeBytes("sh /sdcard/std.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("04min")){
                    try {
                        outputStream.writeBytes("sh /sdcard/04.bash\n");
                        outputStream.writeBytes("sh /sdcard/min.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("04mid")){
                    try {
                        outputStream.writeBytes("sh /sdcard/04.bash\n");
                        outputStream.writeBytes("sh /sdcard/mid.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("04std")){
                    try {
                        outputStream.writeBytes("sh /sdcard/04.bash\n");
                        outputStream.writeBytes("sh /sdcard/std.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("24min")){
                    try {
                        outputStream.writeBytes("sh /sdcard/24.bash\n");
                        outputStream.writeBytes("sh /sdcard/min.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("24mid")){
                    try {
                        outputStream.writeBytes("sh /sdcard/24.bash\n");
                        outputStream.writeBytes("sh /sdcard/mid.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("24std")){
                    try {
                        outputStream.writeBytes("sh /sdcard/24.bash\n");
                        outputStream.writeBytes("sh /sdcard/std.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("42min")){
                    try {
                        outputStream.writeBytes("sh /sdcard/42.bash\n");
                        outputStream.writeBytes("sh /sdcard/min.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("42mid")){
                    try {
                        outputStream.writeBytes("sh /sdcard/42.bash\n");
                        outputStream.writeBytes("sh /sdcard/mid.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("42std")){
                    try {
                        outputStream.writeBytes("sh /sdcard/42.bash\n");
                        outputStream.writeBytes("sh /sdcard/std.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("44min")){
                    try {
                        outputStream.writeBytes("sh /sdcard/44.bash\n");
                        outputStream.writeBytes("sh /sdcard/min.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("44mid")){
                    try {
                        outputStream.writeBytes("sh /sdcard/44.bash\n");
                        outputStream.writeBytes("sh /sdcard/mid.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }
                }

                else if(currentConf.equalsIgnoreCase("44std")){
                    try {
                        outputStream.writeBytes("sh /sdcard/44.bash\n");
                        outputStream.writeBytes("sh /sdcard/std.bash\n");
                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        Log.d("", "it is gonna get getevent1");


                    } catch (Exception e) {
                        Log.i(TAG, "Error occured " + e);

                    }



                    } else {
                     //   mCpuHandler.postDelayed(mCpuRefresh, cpu_conf_time_interval*4);
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
                if(order>=20){
                    order =0;
                    Log.d(TAG,"Active CpuConfHandler End of first 21");
                    Toast.makeText(ServiceClass.this, "**** ShellUSer granted ****", Toast.LENGTH_LONG).show();

                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();

                    if(!standardGov) standardGov = true;
                    else standardGov=false;

                //    Toast.makeText(ServiceClass.this, "**** Please open the Chrome Application now!! ****", Toast.LENGTH_SHORT).show();

               //     r.play();
                    r.play();
                    blackListConf.clear();
                  //  MediaPlayer mp=MediaPlayer.create(ServiceClass.this, R.raw.amusic);
                  //  mp.start();
                   // Toast.makeText(ServiceClass.this, "#######Please open the Chrome Application now!!#######", Toast.LENGTH_SHORT).show();

                }
                else order++;

              //  fps.get_logs(0);
                // fps.get_logs(1);

                fps.get_FPS_initiate();
            //    fps.get_logs(1);

                Log.d(TAG,"Before and after bef: " + System.currentTimeMillis());
           //    mCpuHandler.postDelayed(mCpuRefresh, cpu_conf_time_interval/2);
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


    @TargetApi(21)
    public long getCurrent(){
        //BatteryManager nBatteryManager = Context.BATTERY_SERVICE;
        BatteryManager mBatteryManager =
                (BatteryManager)this.getSystemService(Context.BATTERY_SERVICE);
        long energy =
                mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);

        return energy;
    }















    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
