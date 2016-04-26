package com.cpuconf;
import android.util.Log;

import java.io.*;
import java.util.Vector;
/**
 * Created by boochikashinkunti on 4/22/16.
 */
public class FPS {

    private static final String TAG = FPS.class.getCanonicalName();
    private static final String processId = Integer.toString(android.os.Process
            .myPid());
    private static Vector<Double> fps_log = new Vector<Double>();



    public FPS(){get_logs();}

    public static void get_logs() {

        try
        {
            Process process = Runtime.getRuntime().exec("logcat | grep FPS");

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line = "";
            while ((line = bufferedReader.readLine()) != null && line.contains("FPS")) {
                String fps = line.substring(line.lastIndexOf(':') + 1);
                fps_log.add(Double.parseDouble(fps));
            }

        } catch (IOException e){}
    }

    public static void get_FPS_stats(){
        Double FPS_sum, frametime_sum, mean_FPS, mean_frametime, stdev_FPS, stdev_frametime, max_frametime, temp_FPS;
        FPS_sum = frametime_sum = mean_FPS = mean_frametime = stdev_FPS = stdev_frametime = temp_FPS= 0.0;
        max_frametime = -1.0;
        int count = 0;

        for(int i = 0; i < fps_log.size(); i++)
        {
            temp_FPS = fps_log.get(i);
            FPS_sum+=temp_FPS;
            frametime_sum += 1/temp_FPS;

            if(1/temp_FPS > max_frametime){
                max_frametime = temp_FPS;
            }

            count++;
        }

        mean_FPS = FPS_sum/count;
        mean_frametime = frametime_sum/count;

        for(int j = 0; j < fps_log.size(); j++){
            temp_FPS = fps_log.get(j);

            stdev_FPS += (temp_FPS-mean_FPS)*(temp_FPS-mean_FPS);
            stdev_frametime += (1/temp_FPS-mean_frametime)*(1/temp_FPS-mean_frametime);
        }

        stdev_FPS = Math.sqrt(stdev_FPS/count);
        stdev_frametime = Math.sqrt(stdev_frametime/count);

        Log.v("FPSInfo: ", ""+mean_FPS + " " + stdev_FPS + " " + mean_frametime + " " + stdev_frametime + " " + max_frametime);

        fps_log.clear();

        get_logs();
    }




}
