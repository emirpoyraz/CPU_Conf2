package com.cpuconf;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * Created by emir on 4/13/16.
 */
public class CPUtil {

    final private static boolean DBG = Definitions.DBG;
    final private static String TAG = "CPUtil";
    final private static String STAT_FILE = "/proc/stat";
    final private static String CPU_FREQ_FILE = "/proc/cpuinfo";
    final private static String CPU_FREQ_FILE_SCALE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";
    final private DecimalFormat mPercentFmt = new DecimalFormat("#0.0");


    private long mUser8, mUser0,mUser1,mUser2,mUser3,mUser4,mUser5,mUser6,mUser7;
    private long mSystem8,mSystem0,mSystem1,mSystem2,mSystem3,mSystem4,mSystem5,mSystem6,mSystem7;
    private long mTotal8,mTotal0,mTotal1,mTotal2,mTotal3,mTotal4,mTotal5,mTotal6,mTotal7;


    private double user_sys_perc2;
    private double user_sys_perc3;
    private Vector<TextView> mDisplay;

    private int putItToDataHolder =0;
    private int user_sys_perc2Counter =0;



    public CPUtil() {
      //  readStats();
    }


    private static final byte[] FREQ_COMMAND = FileRepeatReader.generateReadfileCommand(CPU_FREQ_FILE);
    private static final String FREQ_KEY = "BogoMIPS";
    private static final byte[] FREQ_COMMAND_SCALE = FileRepeatReader.generateReadfileCommand(CPU_FREQ_FILE_SCALE);
    private static final String FREQ_KEY_SCALE = "CPU_Freq_Scale";



    public boolean readStats() {
        FileReader fstream;

        //   this.readCpuFreqScale();

        try {
            fstream = new FileReader(STAT_FILE);
        } catch (FileNotFoundException e) {
            if (DBG) {Log.e("MonNet", "Could not read " + STAT_FILE);}
            return false;
        }
        BufferedReader in = new BufferedReader(fstream, 500);
        String line;
        try {
            while ((line = in.readLine()) != null ) {
                String[] firstLine = line.split("\\s+");
                if (firstLine[0].equalsIgnoreCase("cpu")) {
                    updateStats(line.trim().split("[ ]+"), 8);
                   // return true;
                }
                 else if (firstLine[0].equalsIgnoreCase("cpu0")) {
                    updateStats(line.trim().split("[ ]+"), 0);
                  //  return true;
                }
                else if (firstLine[0].equalsIgnoreCase("cpu1")) {
                    updateStats(line.trim().split("[ ]+"), 1);
                   // return true;
                }
                else if (firstLine[0].equalsIgnoreCase("cpu2")) {
                    updateStats(line.trim().split("[ ]+"), 2);
                    //return true;
                }
                else if (firstLine[0].equalsIgnoreCase("cpu3")) {
                    updateStats(line.trim().split("[ ]+"),3);
                    //return true;
                }
                else if (firstLine[0].equalsIgnoreCase("cpu4")) {
                    updateStats(line.trim().split("[ ]+"), 4);
                   /// return true;
                }
                else if (firstLine[0].equalsIgnoreCase("cpu5")) {
                    updateStats(line.trim().split("[ ]+"), 5);
                   // return true;
                }
                else if (firstLine[0].equalsIgnoreCase("cpu6")) {
                    updateStats(line.trim().split("[ ]+"), 6);
                    //return true;
                }
                else if (firstLine[0].equalsIgnoreCase("cpu7")) {
                    updateStats(line.trim().split("[ ]+"), 7);
                    return true;
                }






            }
        } catch (IOException e) {
            if (DBG) {Log.e("MonNet", e.toString());}
        }
        return false;
    }

    private void updateStats(String[] segs, int core) {
        // user = user + nice
        long user = Long.parseLong(segs[1]) + Long.parseLong(segs[2]);
        // system = system + intr + soft_irq
        long system = Long.parseLong(segs[3]) +
                Long.parseLong(segs[6]) + Long.parseLong(segs[7]);
        // total = user + system + idle + io_wait
        long total = user + system + Long.parseLong(segs[4]) + Long.parseLong(segs[5]);


        if (core == 8) {
            if (mTotal8 != 0 || total >= mTotal8) {
                long duser = user - mUser8;
                long dsystem = system - mSystem8;
                long dtotal = total - mTotal8;
                broadcast(duser, dsystem, dtotal);
                double user_sys_perc = (double) (duser + dsystem) * 100.0 / dtotal;
                double user_perc = (double) (duser) * 100.0 / dtotal;
                double sys_perc = (double) (dsystem) * 100.0 / dtotal;

                if (mDisplay != null) {
                    mDisplay.get(0).setText(mPercentFmt.format(user_sys_perc) + "% ("
                            + mPercentFmt.format(user_perc) + "/"
                            + mPercentFmt.format(sys_perc) + ")");
                }

                Log.d(TAG, "CPU" + core + " " + user_sys_perc + " " + user_perc + " " + sys_perc);
                ServiceClass.getLogger().logEntry("CPU" + core + " " + user_sys_perc + " " + user_perc + " " + sys_perc);
               // if(dtotal == 0 ){
              //      user_sys_perc = 0.0;
              //  }
                ServiceClass.getLogger().arffEntryDouble(user_sys_perc);
            }

            mUser8 = user;
            mSystem8 = system;
            mTotal8 = total;

        }



        else if (core == 0) {
            if (mTotal0 != 0 || total >= mTotal0) {
                long duser = user - mUser0;
                long dsystem = system - mSystem0;
                long dtotal = total - mTotal0;
                broadcast(duser, dsystem, dtotal);
                double user_sys_perc = (double) (duser + dsystem) * 100.0 / dtotal;
                double user_perc = (double) (duser) * 100.0 / dtotal;
                double sys_perc = (double) (dsystem) * 100.0 / dtotal;

                if (mDisplay != null) {
                    mDisplay.get(0).setText(mPercentFmt.format(user_sys_perc) + "% ("
                            + mPercentFmt.format(user_perc) + "/"
                            + mPercentFmt.format(sys_perc) + ")");
                }

                Log.d(TAG, "CPU" + core + " " + user_sys_perc + " " + user_perc + " " + sys_perc);
                ServiceClass.getLogger().logEntry("CPU" + core + " " + user_sys_perc + " " + user_perc + " " + sys_perc);
                if(dtotal == 0 ){
                    user_sys_perc = 0.0;
                }
                ServiceClass.getLogger().arffEntryDouble(user_sys_perc);
            }

            mUser0 = user;
            mSystem0 = system;
            mTotal0 = total;

        }

        else if (core == 1) {
            if (mTotal1 != 0 || total >= mTotal1) {
                long duser = user - mUser1;
                long dsystem = system - mSystem1;
                long dtotal = total - mTotal1;
                broadcast(duser, dsystem, dtotal);
                double user_sys_perc = (double) (duser + dsystem) * 100.0 / dtotal;
                double user_perc = (double) (duser) * 100.0 / dtotal;
                double sys_perc = (double) (dsystem) * 100.0 / dtotal;

                if (mDisplay != null) {
                    mDisplay.get(0).setText(mPercentFmt.format(user_sys_perc) + "% ("
                            + mPercentFmt.format(user_perc) + "/"
                            + mPercentFmt.format(sys_perc) + ")");
                }

                Log.d(TAG, "CPU" + core + " " + user_sys_perc + " " + user_perc + " " + sys_perc);
                ServiceClass.getLogger().logEntry("CPU" + core + " " + user_sys_perc + " " + user_perc + " " + sys_perc);

               // if(dtotal == 0.0){
               //     user_sys_perc = 0.0;
              //  }
                ServiceClass.getLogger().arffEntryDouble(user_sys_perc);
            }

            mUser1 = user;
            mSystem1 = system;
            mTotal1 = total;

        }

        else if (core == 2) {
            if (mTotal2 != 0 || total >= mTotal2) {
                long duser = user - mUser2;
                long dsystem = system - mSystem2;
                long dtotal = total - mTotal2;
                broadcast(duser, dsystem, dtotal);
                double user_sys_perc = (double) (duser + dsystem) * 100.0 / dtotal;
                double user_perc = (double) (duser) * 100.0 / dtotal;
                double sys_perc = (double) (dsystem) * 100.0 / dtotal;

                if (mDisplay != null) {
                    mDisplay.get(0).setText(mPercentFmt.format(user_sys_perc) + "% ("
                            + mPercentFmt.format(user_perc) + "/"
                            + mPercentFmt.format(sys_perc) + ")");
                }

                Log.d(TAG, "CPU" + core + " " + user_sys_perc + " " + user_perc + " " + sys_perc);
                ServiceClass.getLogger().logEntry("CPU" + core + " " + user_sys_perc + " " + user_perc + " " + sys_perc);
                if(dtotal == 0){
                    user_sys_perc = 0;
                }
                ServiceClass.getLogger().arffEntryDouble(user_sys_perc);
            }

            mUser2 = user;
            mSystem2 = system;
            mTotal2 = total;

        }

        else if (core == 3) {
            if (mTotal3 != 0 || total >= mTotal3) {
                long duser = user - mUser3;
                long dsystem = system - mSystem3;
                long dtotal = total - mTotal3;
                broadcast(duser, dsystem, dtotal);
                double user_sys_perc = (double) (duser + dsystem) * 100.0 / dtotal;
                double user_perc = (double) (duser) * 100.0 / dtotal;
                double sys_perc = (double) (dsystem) * 100.0 / dtotal;

                if (mDisplay != null) {
                    mDisplay.get(0).setText(mPercentFmt.format(user_sys_perc) + "% ("
                            + mPercentFmt.format(user_perc) + "/"
                            + mPercentFmt.format(sys_perc) + ")");
                }

                Log.d(TAG, "CPU" + core + " " + user_sys_perc + " " + user_perc + " " + sys_perc);
                ServiceClass.getLogger().logEntry("CPU" + core + " " + user_sys_perc + " " + user_perc + " " + sys_perc);
                if(dtotal == 0){
                    user_sys_perc = 0;
                }
                ServiceClass.getLogger().arffEntryDouble(user_sys_perc);
            }

            mUser3 = user;
            mSystem3 = system;
            mTotal3 = total;

        }

        else if (core == 4) {
            if (mTotal4 != 0 || total >= mTotal4) {
                long duser = user - mUser4;
                long dsystem = system - mSystem4;
                long dtotal = total - mTotal4;
                broadcast(duser, dsystem, dtotal);
                double user_sys_perc = (double) (duser + dsystem) * 100.0 / dtotal;
                double user_perc = (double) (duser) * 100.0 / dtotal;
                double sys_perc = (double) (dsystem) * 100.0 / dtotal;

                if (mDisplay != null) {
                    mDisplay.get(0).setText(mPercentFmt.format(user_sys_perc) + "% ("
                            + mPercentFmt.format(user_perc) + "/"
                            + mPercentFmt.format(sys_perc) + ")");
                }

                Log.d(TAG, "CPU" + core + " " + user_sys_perc + " " + user_perc + " " + sys_perc);
                ServiceClass.getLogger().logEntry("CPU" + core + " " + user_sys_perc + " " + user_perc + " " + sys_perc);
                if(dtotal == 0){
                    user_sys_perc = 0;
                }
                ServiceClass.getLogger().arffEntryDouble(user_sys_perc);
            }

            mUser4 = user;
            mSystem4 = system;
            mTotal4 = total;

        }

        else if (core == 5) {
            if (mTotal5 != 0 || total >= mTotal5) {
                long duser = user - mUser5;
                long dsystem = system - mSystem5;
                long dtotal = total - mTotal5;
                broadcast(duser, dsystem, dtotal);
                double user_sys_perc = (double) (duser + dsystem) * 100.0 / dtotal;
                double user_perc = (double) (duser) * 100.0 / dtotal;
                double sys_perc = (double) (dsystem) * 100.0 / dtotal;

                if (mDisplay != null) {
                    mDisplay.get(0).setText(mPercentFmt.format(user_sys_perc) + "% ("
                            + mPercentFmt.format(user_perc) + "/"
                            + mPercentFmt.format(sys_perc) + ")");
                }

                Log.d(TAG, "CPU" + core + " " + user_sys_perc + " " + user_perc + " " + sys_perc);
                ServiceClass.getLogger().logEntry("CPU" + core + " " + user_sys_perc + " " + user_perc + " " + sys_perc);
                if(dtotal == 0){
                    user_sys_perc = 0;
                }
                ServiceClass.getLogger().arffEntryDouble(user_sys_perc);
            }

            mUser5 = user;
            mSystem5 = system;
            mTotal5 = total;

        }

        else if (core == 6) {
            if (mTotal6 != 0 || total >= mTotal6) {
                long duser = user - mUser6;
                long dsystem = system - mSystem6;
                long dtotal = total - mTotal6;
                broadcast(duser, dsystem, dtotal);
                double user_sys_perc = (double) (duser + dsystem) * 100.0 / dtotal;
                double user_perc = (double) (duser) * 100.0 / dtotal;
                double sys_perc = (double) (dsystem) * 100.0 / dtotal;

                if (mDisplay != null) {
                    mDisplay.get(0).setText(mPercentFmt.format(user_sys_perc) + "% ("
                            + mPercentFmt.format(user_perc) + "/"
                            + mPercentFmt.format(sys_perc) + ")");
                }

                Log.d(TAG, "CPU" + core + " " + user_sys_perc + " " + user_perc + " " + sys_perc);
                ServiceClass.getLogger().logEntry("CPU" + core + " " + user_sys_perc + " " + user_perc + " " + sys_perc);
                if(dtotal == 0){
                    user_sys_perc = 0;
                }
                ServiceClass.getLogger().arffEntryDouble(user_sys_perc);

            }

            mUser6 = user;
            mSystem6 = system;
            mTotal6 = total;

        }

        else if (core == 7) {
            if (mTotal7 != 0 || total >= mTotal7) {
                long duser = user - mUser7;
                long dsystem = system - mSystem7;
                long dtotal = total - mTotal7;
                broadcast(duser, dsystem, dtotal);
                double user_sys_perc = (double) (duser + dsystem) * 100.0 / dtotal;
                double user_perc = (double) (duser) * 100.0 / dtotal;
                double sys_perc = (double) (dsystem) * 100.0 / dtotal;

                if (mDisplay != null) {
                    mDisplay.get(0).setText(mPercentFmt.format(user_sys_perc) + "% ("
                            + mPercentFmt.format(user_perc) + "/"
                            + mPercentFmt.format(sys_perc) + ")");
                }

                Log.d(TAG, "CPU" + core + " " + user_sys_perc + " " + user_perc + " " + sys_perc);
                ServiceClass.getLogger().logEntry("CPU" + core + " " + user_sys_perc + " " + user_perc + " " + sys_perc);
                if(dtotal == 0){
                    user_sys_perc = 0;
                }
                ServiceClass.getLogger().arffEntryDouble(user_sys_perc);

            }

            mUser7 = user;
            mSystem7 = system;
            mTotal7 = total;

        }





    }

    private void resetCpuReading(){
        user_sys_perc3 =0;
        user_sys_perc2Counter =0;
        user_sys_perc2 =0;
        putItToDataHolder =0;
    }

    private Set<CpuMonListener> mListeners =
            Collections.synchronizedSet(
                    new HashSet<CpuMonListener>());

    public void registerListener(CpuMonListener l) {
        mListeners.add(l);
    }
    public void unregisterListener(CpuMonListener l) {
        mListeners.remove(l);
    }
    private void broadcast(long dUser, long dSystem, long dTotal) {
        for (CpuMonListener l : mListeners) {
            l.CpuUtilUpdated(dUser, dSystem, dTotal);
        }
    }

    public interface CpuMonListener {
        public void CpuUtilUpdated(long deltaUser, long deltaSystem, long deltaTotal);
    }
};



