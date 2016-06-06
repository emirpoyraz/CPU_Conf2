package com.cpuconf;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Vector;

/**
 * Created by emir on 5/6/16.
 */
public class RunningProcs {

    final private static boolean DBG = Definitions.DBG;
    final private static String TAG = "CPUtil";
    final private static String STAT_FILE = "/proc/stat";
    final private DecimalFormat mPercentFmt = new DecimalFormat("#0.0");


    private long mUser8, mUser0, mUser1, mUser2, mUser3, mUser4, mUser5, mUser6, mUser7;
    private long mSystem8, mSystem0, mSystem1, mSystem2, mSystem3, mSystem4, mSystem5, mSystem6, mSystem7;
    private long mTotal8, mTotal0, mTotal1, mTotal2, mTotal3, mTotal4, mTotal5, mTotal6, mTotal7;


    private double user_sys_perc2;
    private double user_sys_perc3;
    private Vector<TextView> mDisplay;

    private int putItToDataHolder = 0;
    private int user_sys_perc2Counter = 0;


    public RunningProcs() {
        //  readStats();
    }


    //TODO: Threads should be calculated!!!
    public long readStats() {
        FileReader fstream = null;

        //   this.readCpuFreqScale();

        try {
            fstream = new FileReader(STAT_FILE);
        } catch (FileNotFoundException e) {
            if (DBG) {
                Log.e("MonNet", "Could not read " + STAT_FILE);
            }

        }

        long returnValue = 0;
        BufferedReader in = new BufferedReader(fstream, 500);
        String line;

            try {
                while ((line = in.readLine()) != null) {
                    String[] firstLine = line.split("\\s+");
                    if (firstLine[0].equalsIgnoreCase("procs_running")) {
                        if (firstLine[1] != null) {
                            Log.d(TAG, "procs_running: " + firstLine[1]);
                           // ServiceClass.getLogger().logEntry("procs_running: " + firstLine[1]);
                           // ServiceClass.getLogger().arffEntryLong(Long.parseLong(firstLine[1]));
                            returnValue = Long.parseLong(firstLine[1]);
                        }


                    }

                }
            } catch (IOException e) {
                if (DBG) {
                    Log.e("MonNet", e.toString());
                }
            }


        return returnValue;
        }

    }

