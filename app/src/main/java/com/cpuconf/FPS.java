package com.cpuconf;
import android.os.Environment;
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
          // final Process process = Runtime.getRuntime().exec("logcat | grep FPS");

           // BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = "";
          //  while ((line = bufferedReader.readLine()) != null && line.contains("FPS")) {
                Log.d(TAG,"fpsss inside while loop1: " + line);
                String fps = line.substring(line.lastIndexOf(':') + 1);
                Log.d(TAG,"fpsss inside while loop2: " + fps);
             //   fps_log.add(Double.parseDouble(fps));
          //  }



           // BufferedReader br = new BufferedReader(reader)


           // while(reader.ready()){

           // }



        } catch (Exception e){}
    }





    public static void get_FPS_stats(){

       // File sdcard = Environment.getExternalStorageDirectory();

//Get the text file
       // File file = new File(sdcard,"file.log");

        String path = Environment.getExternalStorageDirectory() + "/file.log";

//Read text from file
       // StringBuilder text = new StringBuilder();

        Log.d(TAG, "path is: " + path);

        FileReader fstream = null;

        try {
            fstream = new FileReader(path);
        } catch (FileNotFoundException e) {
           {Log.e("MonNet", "Could not read " + path);}

        }
        BufferedReader in = new BufferedReader(fstream, 500);
        String line;



        try {
          //  BufferedReader br = new BufferedReader(new FileReader(path));
          //  String line;

            while ((line = in.readLine()) != null) {
              //  text.append(line);
              //  text.append('\n');

                String fps = line.substring(line.lastIndexOf(':') + 1);

                fps_log.add(Double.parseDouble(fps));
                Log.d(TAG, "fps data is: " + fps);

            }
            in.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }






        Double FPS_sum, frametime_sum, mean_FPS, mean_frametime, stdev_FPS, stdev_frametime, max_frametime, temp_FPS;
        FPS_sum = frametime_sum = mean_FPS = mean_frametime = stdev_FPS = stdev_frametime = temp_FPS= 0.0;
        max_frametime = -1.0;
        int count = 0;

        Log.d(TAG, "fps log size is: " + fps_log.size());

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

        Log.d(TAG, "mean fps is: " + mean_FPS);

        mean_FPS = FPS_sum/count;
        mean_frametime = frametime_sum/count;

        for(int j = 0; j < fps_log.size(); j++){
            temp_FPS = fps_log.get(j);

            stdev_FPS += (temp_FPS-mean_FPS)*(temp_FPS-mean_FPS);
            stdev_frametime += (1/temp_FPS-mean_frametime)*(1/temp_FPS-mean_frametime);
        }

        stdev_FPS = Math.sqrt(stdev_FPS/count);
        stdev_frametime = Math.sqrt(stdev_frametime/count);

        Log.d("FPSInfo: ", "" + mean_FPS + " " + stdev_FPS + " " + mean_frametime + " " + stdev_frametime + " " + max_frametime);

        fps_log.clear();

        get_logs();
    }




}
