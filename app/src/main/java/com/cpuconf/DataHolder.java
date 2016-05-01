package com.cpuconf;

import android.util.Log;

/**
 * Created by emir on 4/28/16.
 */
public class DataHolder {

    private boolean reset;
    private int user_rate;
    final private static String TAG = "DataHolder";

    public boolean getFPS() {return reset;}
    public void setFPS(boolean reset) {this.reset = reset;
       Log.i(TAG, "DataHolder reset: " + reset);}


    public int getUserSatisfaction() {return user_rate;}
    public void setUserSatisfaction(int user_rate) {this.user_rate = user_rate;
        Log.i(TAG, "DataHolder user_rate: " + user_rate);}


    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}
}

