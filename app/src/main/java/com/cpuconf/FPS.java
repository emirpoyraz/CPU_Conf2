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

    final private static String FPS_PATH = "/sdcard/file.log";

    private static final byte[] FPS_DATA = FileRepeatReader.generateReadfileCommand(FPS_PATH);
    private static Vector<Double> fps_log = new Vector<Double>();


 //   private Process process;
 //   private DataOutputStream outputStream = new DataOutputStream(process.getOutputStream()) ;

 //   private BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));;

    private boolean firstOne = true;

    private Double FPS_sum_old, frametime_sum_old, mean_FPS_old, mean_frametime_old,
            stdev_FPS_old, stdev_frametime_old, max_frametime_old, temp_FPS_old;

    private boolean bufferedReaderNew = true;





    public FPS() {
            get_logs(1);
        try {
        //  final Process process = Runtime.getRuntime().exec("\n");
        //  final DataOutputStream outputStream = new DataOutputStream(process.getOutputStream()) ;
          //  outputStream = process.getOutputStream();
            //  outputStream = DataOutputStream(process.getOutputStream());
         //   outputStream.writeBytes("^C\n");
         //   outputStream.writeBytes("logcat | grep FPS\n");
         //   outputStream.close();
           // bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        } catch (Exception e) {

        }
    }
    public boolean get_logs(int a) {


            try {


  /*
                if(firstOne){

                    final Process process = Runtime.getRuntime().exec("\n");
                    final DataOutputStream outputStream = new DataOutputStream(process.getOutputStream()) ;
                    //  outputStream = process.getOutputStream();
                    //  outputStream = DataOutputStream(process.getOutputStream());
                    //   outputStream.writeBytes("^C\n");
                    outputStream.writeBytes("logcat | grep FPS\n");
                    outputStream.close();

                    firstOne = false;
                }
*/
                Process process = Runtime.getRuntime().exec("\n");

                DataOutputStream  outputStream = new DataOutputStream(process.getOutputStream());

               outputStream.writeBytes("logcat | grep FPS\n");
              //  outputStream.writeBytes("^C\n");
                outputStream.flush();
                outputStream.close();
                Log.d(TAG, "BufferedReader is started againAfterFlush/ cccccccc                                d");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));;
                Log.d(TAG, "BufferedReader is started again");
                String line = "";
                Log.d(TAG, "BufferedReader is started again11");
                int counter =0;
                while ((line = bufferedReader.readLine()) != null && line.contains("FPS") && counter < 10) {

                    Log.d(TAG, "BufferedReader is now: " + bufferedReader.readLine());

                    line = bufferedReader.readLine();
                    Log.d(TAG, " inside loop1: " + line);
                    String fps = line.substring(line.lastIndexOf(':') + 1);
                    Log.d(TAG, " inside loop2: " + fps);
                    fps_log.add(Double.parseDouble(fps));
                    counter++;

                }

                outputStream.writeBytes("^C\n");
                outputStream.flush();
                outputStream.close();

                Log.d(TAG, "BufferedReader is closed212");
                bufferedReader.close();

                Log.d(TAG, "BufferedReader is closed");


                // BufferedReader br = new BufferedReader(reader)


                // while(reader.ready()){


            } catch (Exception e) {
                return false;
            }


            try {

         /*
                outputStream.writeBytes("^C\n");
                outputStream.flush();


               // bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                Log.d(TAG, "BufferedReader is started again0");
                String line = "";
                long lastCall = System.currentTimeMillis();
                Log.d(TAG, "BufferedReader is started again0");
                while ((line = bufferedReader.readLine()) != null && line.contains("FPS")) {

                    Log.d(TAG, "BufferedReader is now: " + bufferedReader.readLine());

                    line = bufferedReader.readLine();
                    Log.d(TAG, "fpsss inside loop0: " + line);
                    String fps = line.substring(line.lastIndexOf(':') + 1);
                    Log.d(TAG, "fpsss inside loop0: " + fps);
                    fps_log.add(Double.parseDouble(fps));

                }

                bufferedReader.close();

                Log.d(TAG, "BufferedReader is closed");

                Log.d(TAG, "BufferedReader is closed212");

                // BufferedReader br = new BufferedReader(reader)


                // while(reader.ready()){
*/

            } catch (Exception e) {
                return false;
            }
    return true;
    }




    public boolean get_FPS_initiate() {
        boolean initiated = false;
        try {


            fps_log.clear();


/*
            File file = new File("/sdcard/file.log");
           // file.delete();

          //  if(!file.exists()){
               // File file_new = new File("/sdcard/file.log");
          //      file.createNewFile();
          //  }

            try {
                //BufferedWriter for performance, true to set append to file flag
                BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
                buf.write("");

                buf.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            Process processFPS = Runtime.getRuntime().exec("logcat | grep FPS > /sdcard/file.log");

            InputStreamReader reader = new InputStreamReader(processFPS.getInputStream());

            initiated = true;
*/
        }catch (Exception e) {
        }

    return initiated;
    }

/*
    public boolean get_FPS_logs() {


        FileRepeatReader mRepeatReader = ServiceClass.getRepeatReader();
        boolean ret = false;
        if (mRepeatReader == null) {
            Log.e(TAG, "FPS Frequency file could not be created111");
            return false;
        } else {

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
                    ret = true;

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
        return ret;
    }

*/
    public boolean get_FPS_stats() {






    /*
        FileRepeatReader mRepeatReader = ServiceClass.getRepeatReader();

       // File sdcard = Environment.getExternalStorageDirectory();

//Get the text file
       // File file = new File(sdcard,"file.log");

        String path = Environment.getExternalStorageDirectory() + "/file.log";

//Read text from file
       // StringBuilder text = new StringBuilder();

        Log.d(TAG, "path is: " + path);

        FileReader fstream = null;

        try {
            fstream = new FileReader(String.valueOf(FPS_DATA));
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


*/


            Double FPS_sum, frametime_sum, mean_FPS, mean_frametime, stdev_FPS, stdev_frametime, max_frametime, temp_FPS;
            FPS_sum = frametime_sum = mean_FPS = mean_frametime = stdev_FPS = stdev_frametime = temp_FPS = 0.0;
            max_frametime = -1.0;
            int count = 0;

            Log.d(TAG, "framePerSec log size is: " + fps_log.size());

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

            Log.d(TAG, "mean framePerSec is: " + mean_FPS);

            for (int j = 0; j < fps_log.size(); j++) {
                temp_FPS = fps_log.get(j);

                stdev_FPS += (temp_FPS - mean_FPS) * (temp_FPS - mean_FPS);
                stdev_frametime += (1 / temp_FPS - mean_frametime) * (1 / temp_FPS - mean_frametime);
            }

            stdev_FPS = Math.sqrt(stdev_FPS / count);
            stdev_frametime = Math.sqrt(stdev_frametime / count);

            Log.d("framePerSecInfo: ", "" + mean_FPS + " " + stdev_FPS + " " + mean_frametime + " " + stdev_frametime + " " + max_frametime);

            if(mean_FPS<=0 || mean_FPS == null) mean_FPS = mean_FPS_old;
            if(stdev_FPS<=0 || stdev_FPS == null) stdev_FPS = stdev_FPS_old;
            if(mean_frametime<=0 || mean_frametime == null) mean_frametime = mean_frametime_old;
            if(stdev_frametime<=0 || stdev_frametime == null) stdev_frametime = stdev_frametime_old;
            if(max_frametime<=0 || max_frametime == null) max_frametime = max_frametime_old;

            ServiceClass.getLogger().logEntry("framePerSecInfo: " + mean_FPS + " " + stdev_FPS + " " + mean_frametime + " " + stdev_frametime + " " + max_frametime);;


            ServiceClass.getLogger().arffEntryDouble(mean_FPS);
            ServiceClass.getLogger().arffEntryDouble(stdev_FPS);
            ServiceClass.getLogger().arffEntryDouble(mean_frametime);
            ServiceClass.getLogger().arffEntryDouble(stdev_frametime);
            ServiceClass.getLogger().arffEntryDouble(max_frametime);


            mean_FPS_old = mean_FPS;
            stdev_FPS_old = stdev_FPS;
            mean_frametime_old = mean_frametime;
            stdev_frametime_old = stdev_frametime;
            max_frametime_old = max_frametime;





           // get_logs(0);

         //   get_logs(1);

        return false;
        }
    }

