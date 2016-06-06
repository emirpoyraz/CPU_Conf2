package com.cpuconf;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by emir on 5/11/16.
 */
public class VoltageNow {
    final private static boolean DBG = Definitions.DBG;
    final private static String TAG = "BatteryMon";

    final private DecimalFormat mPercentFmt = new DecimalFormat("#0.0");
    final private static DecimalFormat mIntFmt = new DecimalFormat("#0");

    final private static String BATT_VOL_FILE = "/sys/class/power_supply/battery/voltage_now";

    final private static String BATT_CURRENT_FILE = "/sys/class/power_supply/battery/current_now";


    final private static String SCREEN_BRIGHTNESS_FILE = "/sys/class/leds/lcd-backlight/brightness";

    // dummy bad value:
    //final private static String BATT_AVG_FILE = "/sys/class/power_supply/battery/batt_currentfalse";

    private static final byte[] SCREEN_BRIGHTNESS_COMMAND = FileRepeatReader.generateReadfileCommand(SCREEN_BRIGHTNESS_FILE);


    private int mScreenBrightness = -1;
    private int sample_screen = 0;
    private int screen_on = 0;

    private Context mContext;


    private static final String TESTED_FILES_SP_KEY = "batteryMonTested";
    private static final String FOUND_FILES_SP_KEY = "batteryMonFilesExist";

    private boolean mFilesTested;
    private boolean mFilesExist;




    public VoltageNow() {



        //  readBattCurNow();
    }



    /**
     * For both 'readBattVol' and 'readBattCur':
     * These have been migrated from a 'FileReader' & 'BufferedReader'
     * implementation to using 'FileRepeatReader'.
     * <p/>
     * 'FileRepeatReader' was created to read files quickly, with minimal
     * buffering and conversion overhead.
     */


    public long readVoltage() {
        // FileRepeatReader
        FileRepeatReader mRepeatReader = ServiceClass.getRepeatReader();

        boolean ret;
        long returnValue = 0;

        if (mRepeatReader == null) {
            if (DBG) Log.e(TAG, "FileRepeatReader is not open");
            ret = false;
        } else {
            try {
                mRepeatReader.lock();
                final byte[] BATT_VOL_COMMAND = FileRepeatReader.generateReadfileCommand(BATT_VOL_FILE);
                mRepeatReader.refresh(BATT_VOL_COMMAND);

                FileRepeatReader.SpaceSeparatedLine ssLine;
                String key;

                if (mRepeatReader.hasNextLine()) {
                    ssLine = mRepeatReader.getSSLine();
                    key = ssLine.getToken(0);
                    int mBattVol = Integer.parseInt(key);
                    returnValue = mBattVol;
                    if (DBG) Log.d(TAG, "Batt Vol: " + key);
                    ret = true;
                } else {
                    ret = false;
                    if (DBG) Log.d(TAG, "Batt Vol couldnt read");
                }
                if (!mFilesTested) {

                }
            } catch (InterruptedException e) {
                if (DBG) {
                    Log.e(TAG, "InterruptedException");
                    e.printStackTrace();
                }
                ret = false;
            } catch (FileNotFoundException e) {
                if (DBG) {
                    Log.e(TAG, "FileNotFoundException");
                    e.printStackTrace();
                }

                ret = false;
            } catch (IOException e) {
                if (DBG) {
                    Log.e(TAG, "IOException");
                    e.printStackTrace();
                }
                ret = false;
            } catch (NumberFormatException e) {
                if (DBG) {
                    Log.e(TAG, "NumberFormatException");
                    e.printStackTrace();
                }
                ret = false;
            } finally {
                mRepeatReader.unlock();
            }
        }
        return returnValue;
    }

    public long readCurrent() {
        // FileRepeatReader
        FileRepeatReader mRepeatReader = ServiceClass.getRepeatReader();

        boolean ret;
        long returnValue = 0;

        if (mRepeatReader == null) {
            if (DBG) Log.e(TAG, "FileRepeatReader is not open");
            ret = false;
        } else {
            try {
                mRepeatReader.lock();
                final byte[] BATT_CURRENT_COMMAND = FileRepeatReader.generateReadfileCommand(BATT_CURRENT_FILE);
                mRepeatReader.refresh(BATT_CURRENT_COMMAND);

                FileRepeatReader.SpaceSeparatedLine ssLine;
                String key;

                if (mRepeatReader.hasNextLine()) {
                    ssLine = mRepeatReader.getSSLine();
                    key = ssLine.getToken(0);
                    int mBattVol = Integer.parseInt(key);
                    returnValue = mBattVol;
                    if (DBG) Log.d(TAG, "Batt Current: " + key);
                    ret = true;
                } else {
                    ret = false;
                    if (DBG) Log.d(TAG, "Batt Current couldnt read");
                }
                if (!mFilesTested) {

                }
            } catch (InterruptedException e) {
                if (DBG) {
                    Log.e(TAG, "InterruptedException");
                    e.printStackTrace();
                }
                ret = false;
            } catch (FileNotFoundException e) {
                if (DBG) {
                    Log.e(TAG, "FileNotFoundException");
                    e.printStackTrace();
                }

                ret = false;
            } catch (IOException e) {
                if (DBG) {
                    Log.e(TAG, "IOException");
                    e.printStackTrace();
                }
                ret = false;
            } catch (NumberFormatException e) {
                if (DBG) {
                    Log.e(TAG, "NumberFormatException");
                    e.printStackTrace();
                }
                ret = false;
            } finally {
                mRepeatReader.unlock();
            }
        }
        return returnValue;
    }

}


