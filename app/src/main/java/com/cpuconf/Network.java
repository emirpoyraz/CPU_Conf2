package com.cpuconf;

import android.content.Intent;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by emir on 5/12/16.
 */
public class Network {


    final private static boolean DBG = Definitions.DBG;
    final private static String TAG = "Network";
    final private static String DEV_FILE = "/proc/self/net/dev";
    private static final String NETWORK_WLAN = "wlan0:";
    private static final String WIFI_COM = "WifiBPE"; // wifi byte packets errors
    private static final String NETWORK_LO = "lo:";
    private static final String BLU_COM = "BluBPE"; // wifi byte packets errors
    private int bytes_deltaW =0;
    private int packets_deltaW=0;
    private int errors_deltaW=0;

    private int bytes_deltaB =0;
    private int packets_deltaB=0;
    private int errors_deltaB=0;
    private boolean firstTimeW =true;
    private boolean firstTimeB =true;


    private static final byte[] DEV_FILE_COMMAND = FileRepeatReader.generateReadfileCommand(DEV_FILE);



    public int[] readWifi() {
        FileRepeatReader mRepeatReader = ServiceClass.getRepeatReader();

        int[] wifiBluNet = new int[6];



        if (mRepeatReader == null) {
            if (DBG) Log.e(TAG, "FileRepeatReader is not open");
        } else {
            try {
                mRepeatReader.lock();
                mRepeatReader.refresh(DEV_FILE_COMMAND);
                FileRepeatReader.SpaceSeparatedLine ssLine;
                String bytesRE, packetsRE, errorsRE, wifi;
                int bytes = 0;
                int errors = 0;
                int packets= 0;
                while (mRepeatReader.hasNextLine()) {
                    ssLine = mRepeatReader.getSSLine();
                    wifi = ssLine.getToken(0);
                    if (NETWORK_WLAN.equalsIgnoreCase(wifi) || wifi.contains(NETWORK_WLAN)) {
                        bytesRE = ssLine.getToken(1);
                        packetsRE = ssLine.getToken(2);
                        errorsRE = ssLine.getToken(3);

                        bytes = (int) Double.parseDouble(bytesRE);
                        packets = (int) Double.parseDouble(packetsRE);
                        errors = (int) Double.parseDouble(errorsRE);

                        if(firstTimeW){
                            bytes_deltaW =bytes;
                            packets_deltaW=packets;
                            errors_deltaW=errors;
                            firstTimeW=false;
                        }
                        else{
                            bytes_deltaW= bytes- bytes_deltaW;
                            packets_deltaW=packets- packets_deltaW;
                            errors_deltaW=errors- errors_deltaW;
                        }



                        if (DBG) Log.i(TAG, "WifiBPE: " + bytes);
                      //  LoggerService.getLogger().networkTrafficUpdatedVifi(WIFI_COM, bytes_delta, packets_delta, errors_delta);

                        wifiBluNet[0] = bytes_deltaW;
                        wifiBluNet[1] = packets_deltaW;
                        wifiBluNet[2] = errors_deltaW;

                        bytes_deltaW =bytes;
                        packets_deltaW=packets;
                        errors_deltaW=errors;

                        //break;


                    }



                    if (NETWORK_LO.equalsIgnoreCase(wifi) || wifi.contains(NETWORK_LO)) {
                        bytesRE = ssLine.getToken(1);
                        packetsRE = ssLine.getToken(2);
                        errorsRE = ssLine.getToken(3);

                        bytes = (int) Double.parseDouble(bytesRE);
                        packets = (int) Double.parseDouble(packetsRE);
                        errors = (int) Double.parseDouble(errorsRE);

                        if(firstTimeB){
                            bytes_deltaB =bytes;
                            packets_deltaB=packets;
                            errors_deltaB=errors;
                            firstTimeB=false;
                        }
                        else{
                            bytes_deltaB= bytes- bytes_deltaB;
                            packets_deltaB=packets- packets_deltaB;
                            errors_deltaB=errors- errors_deltaB;
                        }



                        if (DBG) Log.i(TAG, "BluBPE: " + bytes);
                        //  LoggerService.getLogger().networkTrafficUpdatedVifi(WIFI_COM, bytes_delta, packets_delta, errors_delta);

                        wifiBluNet[3] = bytes_deltaB;
                        wifiBluNet[4] = packets_deltaB;
                        wifiBluNet[5] = errors_deltaB;

                        bytes_deltaB =bytes;
                        packets_deltaB=packets;
                        errors_deltaB=errors;

                        //break;


                    }
                }


            } catch (InterruptedException e) {
                if (DBG) {
                    Log.e(TAG, "InterruptedException");
                    e.printStackTrace();
                }
                //  LoggerService.getLogger().errorOccurred(e);
            } catch (FileNotFoundException e) {
                if (DBG) {
                    Log.e(TAG, "FileNotFoundException");
                    e.printStackTrace();
                }
              //  LoggerService.getLogger().errorOccurred(e);
            } catch (IOException e) {
                if (DBG) {
                    Log.e(TAG, "IOException");
                    e.printStackTrace();
                }
             //   ServiceClass.getLogger().errorOccurred(e);
            } catch (NumberFormatException e) {
                if (DBG) {
                    Log.e(TAG, "NumberFormatException");
                    e.printStackTrace();
                }
                //  LoggerService.getLogger().errorOccurred(e);
            } finally {
                mRepeatReader.unlock();
            }
        }

        return wifiBluNet;
    }


}

