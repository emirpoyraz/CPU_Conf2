package com.cpuconf;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import eu.chainfire.libsuperuser.Shell;

/**
 * Created by emir on 4/1/16.
 * in here we need to specify applications...Therefore
 * app1= com.bulsy.greenwall
 * app2= com.google.android.youtube
 * app3= com.google.android.videos
 * app4= com.nostra13.universalimageloader
 * app5= com.google.android.calendar
 * app6= com.android.chrome
 * app7= com.google.android.deskclock
 * app8= com.google.android.talk
 * app9= com.google.android.apps.photos
 * app10= com.google.android.googlequicksearchbox
 * app11= com.google.android.googlequicksearchbox:interactor
 * app12= com.google.android.gms
 * app13= com.ea.game.nfs14_row
 * app14= com.ea.games.r3_na
 * app15= com.google.android.GoogleCamera
 * app16= com.google.android.apps.maps
 * app17= com.google.android.apps.messaging
 * app18= com.google.android.calculator
 * app19= com.google.android.music
 * app20= com.google.android.music
 * app21= com.google.android.play.games
 * app22= com.android.phone
 * app23= com.skgames.trafficracer
 * app24= com.android.systemui
 * app25= com.google.android.googlequicksearchbox:search
 *
 */
public class Applications {
    final private static boolean DBG = Definitions.DBG;
    private static final String TAG = "Applications";

    private static final String APP_ID_PATTERN = "u\\d+_a\\d+";
    private static final String PROCESSES_FILE = "ps | grep com.";

    private byte[] procStatCommandCharss;
    private int found = 0;
    private boolean firstLine = true;
    private long userOld =0;
    private long totalTimeInUserOld =0;

    String[] apps_old = new String[25];

    private boolean firstTime = true;

    private static List<String> appList = new ArrayList<String>(Arrays.asList(
            "com.bulsy.greenwall",
            "com.google.android.youtube",
            "com.google.android.videos",
            "com.nostra13.universalimageloader",
            "com.google.android.calendar",
            "com.android.chrome",
            "com.google.android.deskclock",
            "com.google.android.talk",
            "com.google.android.apps.photos",
            "com.google.android.googlequicksearchbox",
            "com.google.android.googlequicksearchbox:interactor",
    "com.google.android.gms",
    "com.ea.game.nfs14_row",
    "com.ea.games.r3_na",
    "com.google.android.GoogleCamera",
    "com.google.android.apps.maps",
    "com.google.android.apps.messaging",
    "com.google.android.calculator",
    "com.google.android.music",
    "com.google.android.music",
    "com.google.android.play.games",
    "com.android.phone",
    "com.skgames.trafficracer",
    "com.android.systemui",
    "com.google.android.googlequicksearchbox:search"));

    //  private static final byte[] PROCESSES_FILE_COMMAND = FileRepeatReader.generateReadfileCommand(PROCESSES_FILE);



    public Applications(){
      //  getApps();




    }


    private boolean inAppList(String s){

        if(appList.contains(s)){
            return true;
        }
        else return false;
    }

    public void getApps(){
        int totalApps=0;
        long totalTimeInUser =0;
        long deltaTotalUser =0;
     //   String[] apps = new String[25];
        List<Process> processes = new ArrayList<>();
        try {
        //    List<String> stdout = Shell.SH.run("ps | grep");
            List<String> stdout = Shell.SH.run("ps | grep u0");
            int myPid = android.os.Process.myPid();

            String[] apps = new String[25];

            for(int h=0; h<25; h++){
                apps[h]= appList.get(h)+" "+0+" "+0;
            }


            // Log.i(TAG, "Process Manager1: " +line );
            for(String line : stdout) {
                String[] lineArray = line.split("\\s+");
                String user = lineArray[0];

                if (!user.equalsIgnoreCase("u0_a97") && lineArray[8] != null ) {

                    if(inAppList(lineArray[8])) {

                        int pid = Integer.parseInt(lineArray[1]);
                        int ppid = Integer.parseInt(lineArray[2]);

                        String name = lineArray[8];
                        //  Log.i(TAG, "Process Manager2: " +name + " " + getUser(pid) + " " + pid );
                        // Process processMan = new Process(line);

                        for(int p =0; p<25; p++) {

                            //  Log.i(TAG, "Process Manager3: " +name + " " + getUser(pid) + " " + pid );
                         //   if (ppid != myPid || !name.equals("toolbox") || !name.contains("cpuconf") || !name.equals("ps") || !name.equals("grep")) {
                            if (lineArray[8].equalsIgnoreCase(appList.get(p))) {

                                long getUserTime = getUser(pid);
                                apps[p] = name + " " + getUserTime + " " + pid;
                                //ServiceClass.getLogger().logEntry(apps[totalApps]);
                                Log.d(TAG, "Process Manager5: " + apps[p]);
                              //  totalApps++;

                                totalTimeInUser = totalTimeInUser + getUserTime;
                            }
                        }
                    }
                }
            }

/*
            for (int k = 0; k < 25; k++) {
                for (int p = 0; p < 15; p++) {
                    if (temp_apps[p] != null && !temp_apps[p].equalsIgnoreCase("")) {

                        String[] lineTempApps = temp_apps[p].split("\\s+");
                        if (lineTempApps[0].equalsIgnoreCase(appList.get(k))) {

                        }
                        else{

                        }


                        }

                    }
                }
*/







            // comparing with the old list

            if(totalTimeInUserOld == 0) {
                totalTimeInUserOld = totalTimeInUser;
            }
            else {
                deltaTotalUser = totalTimeInUser - totalTimeInUserOld;
                totalTimeInUserOld = totalTimeInUser;
            }

            Log.d(TAG, "In here - 1 ########## deltaTotalUser: " + deltaTotalUser);

            if(firstTime){
                apps_old = apps;
                Log.d(TAG, "In here - 2 ##########: " + apps_old[0]);
                firstTime = false;
            } else {
                String line;
                Log.d(TAG, "In here - 3 ##########: " + apps_old[0]);
                //   int i = 0;
                //   int j = 0;
                //  while (i < 35) {
                for (int i = 0; i < 25; i++) {
                    for (int j = 0; j < 25; j++) {
                        // while (j < 35) {
                      //  Log.d(TAG, "In here - 4 ##########: " + apps_old[j]);
                        if (apps[i] != null && apps_old[j] != null && !apps[i].equalsIgnoreCase("") && !apps_old[j].equalsIgnoreCase("")) {
                        //    Log.d(TAG, "i and j: " + i + " " + j);
                            String[] lineArrayNew = apps[i].split("\\s+");
                            String[] lineArrayOld = apps_old[j].split("\\s+");
                        //    Log.d(TAG, "In here - 6 ##########: " + lineArrayNew[0] + " " + lineArrayOld[0]);
                            if (lineArrayNew[0].equalsIgnoreCase(lineArrayOld[0])) {   // same app
                                int userTimeOld = Integer.parseInt(lineArrayOld[1]);
                                int userTimeNew = Integer.parseInt(lineArrayNew[1]);
                              //  Log.d(TAG, "In here - 5 ##########: userTimeOldTraffic: " + lineArrayNew[0] + " " + lineArrayOld[0] + " " + userTimeOld + " " + userTimeNew);

                                int deltaUserTime = userTimeNew - userTimeOld;


                                Log.d(TAG, "DeltaUserTime: " + apps[i] + " " + deltaUserTime);
                              //  if (deltaUserTime / deltaTotalUser * 100 >= 10) {

                                String[] appNames = apps[i].split("\\.");

                                String appName = appNames[appNames.length-1];
                               // String appName = appNames[0];

                                Log.d(TAG, "App: " + appName + " " + deltaUserTime);

                                ServiceClass.getLogger().logEntry("App: " + appName + " " + deltaUserTime);
                                ServiceClass.getLogger().arffEntryLong(deltaUserTime);



/*
                                if (deltaUserTime >= 200) {
                                    Log.d(TAG, apps[i] + " ListA: " + (deltaUserTime/deltaTotalUser*100));
                                } else {
                                    Log.d(TAG, apps[i] + " " + deltaUserTime + " " + deltaTotalUser + " ListB: " + (deltaUserTime/deltaTotalUser*100));
                                }
                                // j = 34;
*/
                            }
                        }
                        //  j++;

                    }
                    // i++;


                }
            }


                apps_old = apps;
              //  Log.d(TAG, " " + deltaTotalUser + " ListB: " + deltaTotalUser * 100);

/*
                for (int b = 0; b < 35; b++) {
                  //  Log.d(TAG, "Old apps: " + apps_old[b]);

                }

                for (int c = 0; c < 35; c++) {
                   // Log.d(TAG, "New apps1: " + apps[c]);

                }

*/
             //   for (int f = 0; f < 30; f++) {
             //       apps[f] = null;
             //   }



            


        } catch (Exception e) {
            android.util.Log.d(TAG, "Failed parsing line1 " + e);
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            String stackTrace = writer.toString();

            // flatten the string to a single line
            stackTrace=stackTrace.replace("\n\r", " / ");
            stackTrace=stackTrace.replace("\r\n", " / ");
            stackTrace=stackTrace.replace("\r", " / ");
            stackTrace=stackTrace.replace("\n", " / ");

            Log.d(TAG, "ERROR: " + stackTrace);

        }
    }


    private long getUser(int pid) {

        long[] data;
        long user =0;
        long kern = 0;
        long userDelta =0;

        procStatCommandCharss = FileRepeatReader.generateReadfileCommand("/proc/" + pid + "/stat");

        try {
            data = getAndParseProcStat(procStatCommandCharss);

            if (data == null) {
                // read is invalid
                return 0;
            } else {

                user = data[0];
                kern = data[1] ;

            }

        }

        catch(Throwable e){}
        return user;
    }

    private static long[] getAndParseProcStat(byte[] fReadCommand) throws Throwable {
        FileRepeatReader mRepeatReader = ServiceClass.getRepeatReader();
        if (mRepeatReader == null) {
                Log.d(TAG, "mRepeatReader is null");
                return null;
        } else {
            long[] ret = null;
            try {
                mRepeatReader.lock();
                mRepeatReader.refresh(fReadCommand);

                FileRepeatReader.SpaceSeparatedLine ssLine;

                if (mRepeatReader.hasNextLine()) {
                    ssLine = mRepeatReader.getSSLine();
                    long user = Long.parseLong(ssLine.getToken(13));
                    long kern = Long.parseLong(ssLine.getToken(14));

                    ret = new long[2];
                    ret[0] = user;
                    ret[1] = kern;
                    //   ret[3] = run;
                } else {
                    ret = null;
                }

            } finally {
                mRepeatReader.unlock();
            }
            return ret;
        }
    }

}

