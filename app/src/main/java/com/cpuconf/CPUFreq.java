package com.cpuconf;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by emir on 5/11/16.
 */
public class CPUFreq {
    final private static boolean DBG = Definitions.DBG;
    final private static String TAG = "CPUFreq";
    final private static String CPU0_FREQ_FILE_SCALE = "/sys/devices/system/cpu/cpufreq/stats/cpu0/time_in_state";
    final private static String CPU1_FREQ_FILE_SCALE = "/sys/devices/system/cpu/cpufreq/stats/cpu1/time_in_state";
    final private static String CPU2_FREQ_FILE_SCALE = "/sys/devices/system/cpu/cpufreq/stats/cpu2/time_in_state";
    final private static String CPU3_FREQ_FILE_SCALE = "/sys/devices/system/cpu/cpufreq/stats/cpu3/time_in_state";
    final private static String CPU4_FREQ_FILE_SCALE = "/sys/devices/system/cpu/cpufreq/stats/cpu4/time_in_state";
    final private static String CPU5_FREQ_FILE_SCALE = "/sys/devices/system/cpu/cpufreq/stats/cpu5/time_in_state";
    final private static String CPU6_FREQ_FILE_SCALE = "/sys/devices/system/cpu/cpufreq/stats/cpu6/time_in_state";
    final private static String CPU7_FREQ_FILE_SCALE = "/sys/devices/system/cpu/cpufreq/stats/cpu7/time_in_state";


    private int old_freq01,old_freq02,old_freq03,old_freq04,old_freq05,old_freq06,old_freq07,old_freq08,old_freq09,old_freq010,old_freq011;
    private int old_freq11,old_freq12,old_freq13,old_freq14,old_freq15,old_freq16,old_freq17,old_freq18,old_freq19,old_freq110,old_freq111;
    private int old_freq21,old_freq22,old_freq23,old_freq24,old_freq25,old_freq26,old_freq27,old_freq28,old_freq29,old_freq210,old_freq211;
    private int old_freq31,old_freq32,old_freq33,old_freq34,old_freq35,old_freq36,old_freq37,old_freq38,old_freq39,old_freq310,old_freq311;
    private int old_freq41,old_freq42,old_freq43,old_freq44,old_freq45,old_freq46,old_freq47,old_freq48,old_freq49,old_freq410,old_freq411,old_freq412,old_freq413,old_freq414;
    private int old_freq51,old_freq52,old_freq53,old_freq54,old_freq55,old_freq56,old_freq57,old_freq58,old_freq59,old_freq510,old_freq511,old_freq512,old_freq513,old_freq514;
    private int old_freq61,old_freq62,old_freq63,old_freq64,old_freq65,old_freq66,old_freq67,old_freq68,old_freq69,old_freq610,old_freq611,old_freq612,old_freq613,old_freq614;
    private int old_freq71,old_freq72,old_freq73,old_freq74,old_freq75,old_freq76,old_freq77,old_freq78,old_freq79,old_freq710,old_freq711,old_freq712,old_freq713,old_freq714;








    private int old_three = 0;
    private int old_three_eight = 0;
    private int old_six = 0;
    private int old_seven = 0;
    private int old_eigth = 0;
    private int old_nine = 0;
    private int old_ten = 0;
    private int old_ten_nine = 0;
    private int old_eleven = 0;


    public double[] readStats() {


        double[] cpuFreqs = new double[8];

        try {
            cpuFreqs[0] = readCPU0();

        } catch (Exception e){ Log.d(TAG, "Error in freq0: " + e); cpuFreqs[0] =0;}

        try {
            cpuFreqs[1] = readCPU1();
        } catch (Exception e){ Log.d(TAG, "Error in freq1: " + e);cpuFreqs[1] =0;}

        try {
            cpuFreqs[2] = readCPU2();
        } catch (Exception e){ Log.d(TAG, "Error in freq2: " + e); cpuFreqs[2] =0;}

        try {
            cpuFreqs[3] = readCPU3();
        } catch (Exception e){ Log.d(TAG, "Error in freq3: " + e);cpuFreqs[3]=0;}

        try {
            cpuFreqs[4] = readCPU4();
        } catch (Exception e){ Log.d(TAG, "Error in freq4: " + e);cpuFreqs[4]=0;}

        try {
            cpuFreqs[5] = readCPU5();
        } catch (Exception e){ Log.d(TAG, "Error in freq5: " + e); cpuFreqs[5]=0;}

        try {
            cpuFreqs[6] = readCPU6();
        } catch (Exception e){ Log.d(TAG, "Error in freq6: " + e); cpuFreqs[6]=0;}

        try {
            cpuFreqs[7] = readCPU7();
        } catch (Exception e){ Log.d(TAG, "Error in freq7: " + e); cpuFreqs[7] =0;}



        return cpuFreqs;
    }






    private double readCPU0() throws Exception {

        FileReader fstream = null;

        //    this.readCpuFreqScale();

        try {
            fstream = new FileReader(CPU0_FREQ_FILE_SCALE);
        } catch (FileNotFoundException e) {
            if (DBG) {
                Log.e("MonNet", "Could not read " + CPU0_FREQ_FILE_SCALE);
            }
        }
        BufferedReader in = new BufferedReader(fstream, 500);
        String line;
        int value = 0;
        String[] segs;
        int freq01,freq02,freq03,freq04,freq05,freq06,freq07,freq08,freq09,freq010,freq011;
        freq01=freq02=freq03=freq04=freq05=freq06=freq07=freq08=freq09=freq010=freq011=0;


        while ((line = in.readLine()) != null) {
            // 300000  384000  600000  787200  998400  1094400  1190400  -->all possibilities in frequencies
            if (line.startsWith("384000")) {
                segs = line.trim().split("[ ]+");
                freq01 = Integer.parseInt(segs[1]) - old_freq01; // field 3
                old_freq01 = Integer.parseInt(segs[1]);

            }
            if (line.startsWith("460800")) {
                segs = line.trim().split("[ ]+");
                freq02 = Integer.parseInt(segs[1]) - old_freq02; // field 3
                old_freq02 = Integer.parseInt(segs[1]);
            }
            if (line.startsWith("600000")) {
                segs = line.trim().split("[ ]+");
                freq03 = Integer.parseInt(segs[1]) - old_freq03; // field 3
                old_freq03 = Integer.parseInt(segs[1]);

            }
            if (line.startsWith("672000")) {
                segs = line.trim().split("[ ]+");
                freq04 = Integer.parseInt(segs[1]) - old_freq04; // field 3
                old_freq04 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("768000")) {
                segs = line.trim().split("[ ]+");
                freq05 = Integer.parseInt(segs[1]) - old_freq05; // field 3
                old_freq05 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("864000")) {
                segs = line.trim().split("[ ]+");
                freq06 = Integer.parseInt(segs[1]) - old_freq06; // field 3
                old_freq06 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("960000")) {
                segs = line.trim().split("[ ]+");
                freq07 = Integer.parseInt(segs[1]) - old_freq07; // field 3
                old_freq07 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("1248000")) {
                segs = line.trim().split("[ ]+");
                freq08 = Integer.parseInt(segs[1]) - old_freq08; // field 3
                old_freq08 = Integer.parseInt(segs[1]);


            }

            if (line.startsWith("1344000")) {
                segs = line.trim().split("[ ]+");
                freq09 = Integer.parseInt(segs[1]) - old_freq09; // field 3
                old_freq09 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1478400")) {
                segs = line.trim().split("[ ]+");
                freq010 = Integer.parseInt(segs[1]) - old_freq010; // field 3
                old_freq010 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1555200")) {
                segs = line.trim().split("[ ]+");
                freq011 = Integer.parseInt(segs[1]) - old_freq011; // field 3
                old_freq011 = Integer.parseInt(segs[1]);

            }

        }
        in.close();
        //           BigInteger three_total = (300000* three) / (three + six + eigth + ten);
        //           BigInteger six_total =(600000* six) / (three + six + eigth + ten);
        //           BigInteger eigth_total =(800000* eigth) / (three + six + eigth + ten);
        //           BigInteger ten_total =(1000000* ten) / (three + six + eigth + ten);

        double total =(384000*freq01 +460800*freq02  +  600000*freq03 + 672000*freq04 + 768000*freq05 + 864000*freq06 +
                960000*freq07 + 1248000*freq08 + 1344000*freq09 +1478400*freq010  +1555200*freq011 )/ (freq01+freq02+freq03+freq04+freq05+freq06+freq07+freq08+freq09+freq010+freq011);

        //           total = total * (1000);
        if(DBG) {
            Log.i(TAG, "Cpu0 Frequency is: " + total);
        }
        //           if(total<0){total = -total;}
      //  LoggerService.getLogger().cpuFreqUpdated(total);

        return total;
    }



    private double readCPU1() throws Exception {

        FileReader fstream = null;

        //    this.readCpuFreqScale();

        try {
            fstream = new FileReader(CPU1_FREQ_FILE_SCALE);
        } catch (FileNotFoundException e) {
            if (DBG) {
                Log.e("MonNet", "Could not read " + CPU1_FREQ_FILE_SCALE);
            }
        }
        BufferedReader in = new BufferedReader(fstream, 500);
        String line;
        int value = 0;
        String[] segs;
        int freq11,freq12,freq13,freq14,freq15,freq16,freq17,freq18,freq19,freq110,freq111;
        freq11=freq12=freq13=freq14=freq15=freq16=freq17=freq18=freq19=freq110=freq111=0;


        while ((line = in.readLine()) != null) {
            // 300000  384000  600000  787200  998400  1094400  1190400  -->all possibilities in frequencies
            if (line.startsWith("384000")) {
                segs = line.trim().split("[ ]+");
                freq11 = Integer.parseInt(segs[1]) - old_freq11; // field 3
                old_freq11 = Integer.parseInt(segs[1]);

            }
            if (line.startsWith("460800")) {
                segs = line.trim().split("[ ]+");
                freq12 = Integer.parseInt(segs[1]) - old_freq12; // field 3
                old_freq12 = Integer.parseInt(segs[1]);
            }
            if (line.startsWith("600000")) {
                segs = line.trim().split("[ ]+");
                freq13 = Integer.parseInt(segs[1]) - old_freq13; // field 3
                old_freq13 = Integer.parseInt(segs[1]);

            }
            if (line.startsWith("672000")) {
                segs = line.trim().split("[ ]+");
                freq14 = Integer.parseInt(segs[1]) - old_freq14; // field 3
                old_freq14 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("768000")) {
                segs = line.trim().split("[ ]+");
                freq15 = Integer.parseInt(segs[1]) - old_freq15; // field 3
                old_freq15 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("864000")) {
                segs = line.trim().split("[ ]+");
                freq16 = Integer.parseInt(segs[1]) - old_freq16; // field 3
                old_freq16 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("960000")) {
                segs = line.trim().split("[ ]+");
                freq17 = Integer.parseInt(segs[1]) - old_freq17; // field 3
                old_freq17 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("1248000")) {
                segs = line.trim().split("[ ]+");
                freq18 = Integer.parseInt(segs[1]) - old_freq18; // field 3
                old_freq18 = Integer.parseInt(segs[1]);


            }

            if (line.startsWith("1344000")) {
                segs = line.trim().split("[ ]+");
                freq19 = Integer.parseInt(segs[1]) - old_freq19; // field 3
                old_freq19 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1478400")) {
                segs = line.trim().split("[ ]+");
                freq110 = Integer.parseInt(segs[1]) - old_freq110; // field 3
                old_freq110 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1555200")) {
                segs = line.trim().split("[ ]+");
                freq111 = Integer.parseInt(segs[1]) - old_freq111; // field 3
                old_freq111 = Integer.parseInt(segs[1]);

            }

        }
        in.close();
        //           BigInteger three_total = (300000* three) / (three + six + eigth + ten);
        //           BigInteger six_total =(600000* six) / (three + six + eigth + ten);
        //           BigInteger eigth_total =(800000* eigth) / (three + six + eigth + ten);
        //           BigInteger ten_total =(1000000* ten) / (three + six + eigth + ten);

        double total =(384000*freq11 +460800*freq12  +  600000*freq13 + 672000*freq14 + 768000*freq15 + 864000*freq16 +
                960000*freq17 + 1248000*freq18 + 1344000*freq19 +1478400*freq110  +1555200*freq111 )/ (freq11+freq12+freq13+freq14+freq15+freq16+freq17+freq18+freq19+freq110+freq111);

        //           total = total * (1000);
        if(DBG) {
            Log.i(TAG, "Cpu1 Frequency is: " + total);
        }
        //           if(total<0){total = -total;}
        //  LoggerService.getLogger().cpuFreqUpdated(total);

        return total;
    }



    private double readCPU2() throws Exception {

        FileReader fstream = null;

        //    this.readCpuFreqScale();

        try {
            fstream = new FileReader(CPU2_FREQ_FILE_SCALE);
        } catch (FileNotFoundException e) {
            if (DBG) {
                Log.e("MonNet", "Could not read " + CPU2_FREQ_FILE_SCALE);
            }
        }
        BufferedReader in = new BufferedReader(fstream, 500);
        String line;
        int value = 0;
        String[] segs;
        int freq21,freq22,freq23,freq24,freq25,freq26,freq27,freq28,freq29,freq210,freq211;
        freq21=freq22=freq23=freq24=freq25=freq26=freq27=freq28=freq29=freq210=freq211=0;


        while ((line = in.readLine()) != null) {
            // 300000  384000  600000  787200  998400  1094400  1190400  -->all possibilities in frequencies
            if (line.startsWith("384000")) {
                segs = line.trim().split("[ ]+");
                freq21 = Integer.parseInt(segs[1]) - old_freq21; // field 3
                old_freq21 = Integer.parseInt(segs[1]);

            }
            if (line.startsWith("460800")) {
                segs = line.trim().split("[ ]+");
                freq22 = Integer.parseInt(segs[1]) - old_freq22; // field 3
                old_freq22 = Integer.parseInt(segs[1]);
            }
            if (line.startsWith("600000")) {
                segs = line.trim().split("[ ]+");
                freq23 = Integer.parseInt(segs[1]) - old_freq23; // field 3
                old_freq23 = Integer.parseInt(segs[1]);

            }
            if (line.startsWith("672000")) {
                segs = line.trim().split("[ ]+");
                freq24 = Integer.parseInt(segs[1]) - old_freq24; // field 3
                old_freq24 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("768000")) {
                segs = line.trim().split("[ ]+");
                freq25 = Integer.parseInt(segs[1]) - old_freq25; // field 3
                old_freq25 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("864000")) {
                segs = line.trim().split("[ ]+");
                freq26 = Integer.parseInt(segs[1]) - old_freq26; // field 3
                old_freq26 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("960000")) {
                segs = line.trim().split("[ ]+");
                freq27 = Integer.parseInt(segs[1]) - old_freq27; // field 3
                old_freq27 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("1248000")) {
                segs = line.trim().split("[ ]+");
                freq28 = Integer.parseInt(segs[1]) - old_freq28; // field 3
                old_freq28 = Integer.parseInt(segs[1]);


            }

            if (line.startsWith("1344000")) {
                segs = line.trim().split("[ ]+");
                freq29 = Integer.parseInt(segs[1]) - old_freq29; // field 3
                old_freq29 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1478400")) {
                segs = line.trim().split("[ ]+");
                freq210 = Integer.parseInt(segs[1]) - old_freq210; // field 3
                old_freq210 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1555200")) {
                segs = line.trim().split("[ ]+");
                freq211 = Integer.parseInt(segs[1]) - old_freq211; // field 3
                old_freq211 = Integer.parseInt(segs[1]);

            }

        }
        in.close();
        //           BigInteger three_total = (300000* three) / (three + six + eigth + ten);
        //           BigInteger six_total =(600000* six) / (three + six + eigth + ten);
        //           BigInteger eigth_total =(800000* eigth) / (three + six + eigth + ten);
        //           BigInteger ten_total =(1000000* ten) / (three + six + eigth + ten);

        double total =(384000*freq21 +460800*freq22  +  600000*freq23 + 672000*freq24 + 768000*freq25 + 864000*freq26 +
                960000*freq27 + 1248000*freq28 + 1344000*freq29 +1478400*freq210  +1555200*freq211 )/ (freq21+freq22+freq23+freq24+freq25+freq26+freq27+freq28+freq29+freq210+freq211);

        //           total = total * (1000);
        if(DBG) {
            Log.i(TAG, "Cpu2 Frequency is: " + total);
        }
        //           if(total<0){total = -total;}
        //  LoggerService.getLogger().cpuFreqUpdated(total);

        return total;
    }



    private double readCPU3() throws Exception {

        FileReader fstream = null;

        //    this.readCpuFreqScale();

        try {
            fstream = new FileReader(CPU3_FREQ_FILE_SCALE);
        } catch (FileNotFoundException e) {
            if (DBG) {
                Log.e("MonNet", "Could not read " + CPU3_FREQ_FILE_SCALE);
            }
        }
        BufferedReader in = new BufferedReader(fstream, 500);
        String line;
        int value = 0;
        String[] segs;
        int freq31,freq32,freq33,freq34,freq35,freq36,freq37,freq38,freq39,freq310,freq311;
        freq31=freq32=freq33=freq34=freq35=freq36=freq37=freq38=freq39=freq310=freq311=0;


        while ((line = in.readLine()) != null) {
            // 300000  384000  600000  787200  998400  1094400  1190400  -->all possibilities in frequencies
            if (line.startsWith("384000")) {
                segs = line.trim().split("[ ]+");
                freq31 = Integer.parseInt(segs[1]) - old_freq31; // field 3
                old_freq31 = Integer.parseInt(segs[1]);

            }
            if (line.startsWith("460800")) {
                segs = line.trim().split("[ ]+");
                freq32 = Integer.parseInt(segs[1]) - old_freq32; // field 3
                old_freq32 = Integer.parseInt(segs[1]);
            }
            if (line.startsWith("600000")) {
                segs = line.trim().split("[ ]+");
                freq33 = Integer.parseInt(segs[1]) - old_freq33; // field 3
                old_freq33 = Integer.parseInt(segs[1]);

            }
            if (line.startsWith("672000")) {
                segs = line.trim().split("[ ]+");
                freq34 = Integer.parseInt(segs[1]) - old_freq34; // field 3
                old_freq34 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("768000")) {
                segs = line.trim().split("[ ]+");
                freq35 = Integer.parseInt(segs[1]) - old_freq35; // field 3
                old_freq35 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("864000")) {
                segs = line.trim().split("[ ]+");
                freq36 = Integer.parseInt(segs[1]) - old_freq36; // field 3
                old_freq36 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("960000")) {
                segs = line.trim().split("[ ]+");
                freq37 = Integer.parseInt(segs[1]) - old_freq37; // field 3
                old_freq37 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("1248000")) {
                segs = line.trim().split("[ ]+");
                freq38 = Integer.parseInt(segs[1]) - old_freq38; // field 3
                old_freq38 = Integer.parseInt(segs[1]);


            }

            if (line.startsWith("1344000")) {
                segs = line.trim().split("[ ]+");
                freq39 = Integer.parseInt(segs[1]) - old_freq39; // field 3
                old_freq39 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1478400")) {
                segs = line.trim().split("[ ]+");
                freq310 = Integer.parseInt(segs[1]) - old_freq310; // field 3
                old_freq310 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1555200")) {
                segs = line.trim().split("[ ]+");
                freq311 = Integer.parseInt(segs[1]) - old_freq311; // field 3
                old_freq311 = Integer.parseInt(segs[1]);

            }

        }
        in.close();
        //           BigInteger three_total = (300000* three) / (three + six + eigth + ten);
        //           BigInteger six_total =(600000* six) / (three + six + eigth + ten);
        //           BigInteger eigth_total =(800000* eigth) / (three + six + eigth + ten);
        //           BigInteger ten_total =(1000000* ten) / (three + six + eigth + ten);

        double total =(384000*freq31 +460800*freq32  +  600000*freq33 + 672000*freq34 + 768000*freq35 + 864000*freq36 +
                960000*freq37 + 1248000*freq38 + 1344000*freq39 +1478400*freq310  +1555200*freq311 )/ (freq31+freq32+freq33+freq34+freq35+freq36+freq37+freq38+freq39+freq310+freq311);

        //           total = total * (1000);
        if(DBG) {
            Log.i(TAG, "Cpu3 Frequency is: " + total);
        }
        //           if(total<0){total = -total;}
        //  LoggerService.getLogger().cpuFreqUpdated(total);

        return total;
    }



    private double readCPU4() throws Exception {

        FileReader fstream = null;

        //    this.readCpuFreqScale();

        try {
            fstream = new FileReader(CPU4_FREQ_FILE_SCALE);
        } catch (FileNotFoundException e) {
            if (DBG) {
                Log.e("MonNet", "Could not read " + CPU4_FREQ_FILE_SCALE);
            }
        }
        BufferedReader in = new BufferedReader(fstream, 500);
        String line;
        int value = 0;
        String[] segs;
        int freq41,freq42,freq43,freq44,freq45,freq46,freq47,freq48,freq49,freq410,freq411,freq412,freq413,freq414;
        freq41=freq42=freq43=freq44=freq45=freq46=freq47=freq48=freq49=freq410=freq411=freq412=freq413=freq414=0;


        while ((line = in.readLine()) != null) {
            // 300000  384000  600000  787200  998400  1094400  1190400  -->all possibilities in frequencies
            if (line.startsWith("384000")) {
                segs = line.trim().split("[ ]+");
                freq41 = Integer.parseInt(segs[1]) - old_freq41; // field 3
                old_freq41 = Integer.parseInt(segs[1]);

            }
            if (line.startsWith("480000")) {
                segs = line.trim().split("[ ]+");
                freq42 = Integer.parseInt(segs[1]) - old_freq42; // field 3
                old_freq42 = Integer.parseInt(segs[1]);
            }
            if (line.startsWith("633600")) {
                segs = line.trim().split("[ ]+");
                freq43 = Integer.parseInt(segs[1]) - old_freq43; // field 3
                old_freq43 = Integer.parseInt(segs[1]);

            }
            if (line.startsWith("768000")) {
                segs = line.trim().split("[ ]+");
                freq44 = Integer.parseInt(segs[1]) - old_freq44; // field 3
                old_freq44 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("864000")) {
                segs = line.trim().split("[ ]+");
                freq45 = Integer.parseInt(segs[1]) - old_freq45; // field 3
                old_freq45 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("960000")) {
                segs = line.trim().split("[ ]+");
                freq46 = Integer.parseInt(segs[1]) - old_freq46; // field 3
                old_freq46 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("1248000")) {
                segs = line.trim().split("[ ]+");
                freq47 = Integer.parseInt(segs[1]) - old_freq47; // field 3
                old_freq47 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("1344000")) {
                segs = line.trim().split("[ ]+");
                freq48 = Integer.parseInt(segs[1]) - old_freq48; // field 3
                old_freq48 = Integer.parseInt(segs[1]);


            }

            if (line.startsWith("1440000")) {
                segs = line.trim().split("[ ]+");
                freq49 = Integer.parseInt(segs[1]) - old_freq49; // field 3
                old_freq49 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1536000")) {
                segs = line.trim().split("[ ]+");
                freq410 = Integer.parseInt(segs[1]) - old_freq410; // field 3
                old_freq410 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1632000")) {
                segs = line.trim().split("[ ]+");
                freq411 = Integer.parseInt(segs[1]) - old_freq411; // field 3
                old_freq411 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1728000")) {
                segs = line.trim().split("[ ]+");
                freq412 = Integer.parseInt(segs[1]) - old_freq412; // field 3
                old_freq412 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1824000")) {
                segs = line.trim().split("[ ]+");
                freq413 = Integer.parseInt(segs[1]) - old_freq413; // field 3
                old_freq413 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1958400")) {
                segs = line.trim().split("[ ]+");
                freq414 = Integer.parseInt(segs[1]) - old_freq414; // field 3
                old_freq414 = Integer.parseInt(segs[1]);

            }

        }
        in.close();
        //           BigInteger three_total = (300000* three) / (three + six + eigth + ten);
        //           BigInteger six_total =(600000* six) / (three + six + eigth + ten);
        //           BigInteger eigth_total =(800000* eigth) / (three + six + eigth + ten);
        //           BigInteger ten_total =(1000000* ten) / (three + six + eigth + ten);

        double total =(384000*freq41 +480000*freq42  +  633600*freq43 + 768000*freq44 + 864000*freq45 + 960000*freq46 +
                1248000*freq47 + 1344000*freq48 + 1440000*freq49 +1536000*freq410  +1632000*freq411 +1728000*freq412 +1824000*freq413  +1958400* freq414)/
                (freq41+freq42+freq43+freq44+freq45+freq46+freq47+freq48+freq49+freq410+freq411+freq412+freq413+freq414);

        //           total = total * (1000);
        if(DBG) {
            Log.i(TAG, "Cpu4 Frequency is: " + total);
        }
        //           if(total<0){total = -total;}
        //  LoggerService.getLogger().cpuFreqUpdated(total);

        return total;
    }



    private double readCPU5() throws Exception {

        FileReader fstream = null;

        //    this.readCpuFreqScale();

        try {
            fstream = new FileReader(CPU5_FREQ_FILE_SCALE);
        } catch (FileNotFoundException e) {
            if (DBG) {
                Log.e("MonNet", "Could not read " + CPU5_FREQ_FILE_SCALE);
            }
        }
        BufferedReader in = new BufferedReader(fstream, 500);
        String line;
        int value = 0;
        String[] segs;
        int freq51,freq52,freq53,freq54,freq55,freq56,freq57,freq58,freq59,freq510,freq511,freq512,freq513,freq514;
        freq51=freq52=freq53=freq54=freq55=freq56=freq57=freq58=freq59=freq510=freq511=freq512=freq513=freq514=0;


        while ((line = in.readLine()) != null) {
            // 300000  384000  600000  787200  998400  1094400  1190400  -->all possibilities in frequencies
            if (line.startsWith("384000")) {
                segs = line.trim().split("[ ]+");
                freq51 = Integer.parseInt(segs[1]) - old_freq51; // field 3
                old_freq51 = Integer.parseInt(segs[1]);

            }
            if (line.startsWith("480000")) {
                segs = line.trim().split("[ ]+");
                freq52 = Integer.parseInt(segs[1]) - old_freq52; // field 3
                old_freq52 = Integer.parseInt(segs[1]);
            }
            if (line.startsWith("633600")) {
                segs = line.trim().split("[ ]+");
                freq53 = Integer.parseInt(segs[1]) - old_freq53; // field 3
                old_freq53 = Integer.parseInt(segs[1]);

            }
            if (line.startsWith("768000")) {
                segs = line.trim().split("[ ]+");
                freq54 = Integer.parseInt(segs[1]) - old_freq54; // field 3
                old_freq54 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("864000")) {
                segs = line.trim().split("[ ]+");
                freq55 = Integer.parseInt(segs[1]) - old_freq55; // field 3
                old_freq55 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("960000")) {
                segs = line.trim().split("[ ]+");
                freq56 = Integer.parseInt(segs[1]) - old_freq56; // field 3
                old_freq56 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("1248000")) {
                segs = line.trim().split("[ ]+");
                freq57 = Integer.parseInt(segs[1]) - old_freq57; // field 3
                old_freq57 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("1344000")) {
                segs = line.trim().split("[ ]+");
                freq58 = Integer.parseInt(segs[1]) - old_freq58; // field 3
                old_freq58 = Integer.parseInt(segs[1]);


            }

            if (line.startsWith("1440000")) {
                segs = line.trim().split("[ ]+");
                freq59 = Integer.parseInt(segs[1]) - old_freq59; // field 3
                old_freq59 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1536000")) {
                segs = line.trim().split("[ ]+");
                freq510 = Integer.parseInt(segs[1]) - old_freq510; // field 3
                old_freq510 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1632000")) {
                segs = line.trim().split("[ ]+");
                freq511 = Integer.parseInt(segs[1]) - old_freq511; // field 3
                old_freq511 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1728000")) {
                segs = line.trim().split("[ ]+");
                freq512 = Integer.parseInt(segs[1]) - old_freq512; // field 3
                old_freq512 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1824000")) {
                segs = line.trim().split("[ ]+");
                freq513 = Integer.parseInt(segs[1]) - old_freq513; // field 3
                old_freq513 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1958400")) {
                segs = line.trim().split("[ ]+");
                freq514 = Integer.parseInt(segs[1]) - old_freq514; // field 3
                old_freq514 = Integer.parseInt(segs[1]);

            }

        }
        in.close();
        //           BigInteger three_total = (300000* three) / (three + six + eigth + ten);
        //           BigInteger six_total =(600000* six) / (three + six + eigth + ten);
        //           BigInteger eigth_total =(800000* eigth) / (three + six + eigth + ten);
        //           BigInteger ten_total =(1000000* ten) / (three + six + eigth + ten);

        double total =(384000*freq51 +480000*freq52  +  633600*freq53 + 768000*freq54 + 864000*freq55 + 960000*freq56 +
                1248000*freq57 + 1344000*freq58 + 1440000*freq59 +1536000*freq510  +1632000*freq511 +1728000*freq512 +1824000*freq513  +1958400* freq514)/
                (freq51+freq52+freq53+freq54+freq55+freq56+freq57+freq58+freq59+freq510+freq511+freq512+freq513+freq514);

        //           total = total * (1000);
        if(DBG) {
            Log.i(TAG, "Cpu5 Frequency is: " + total);
        }
        //           if(total<0){total = -total;}
        //  LoggerService.getLogger().cpuFreqUpdated(total);

        return total;
    }



    private double readCPU6() throws Exception {

        FileReader fstream = null;

        //    this.readCpuFreqScale();

        try {
            fstream = new FileReader(CPU6_FREQ_FILE_SCALE);
        } catch (FileNotFoundException e) {
            if (DBG) {
                Log.e("MonNet", "Could not read " + CPU6_FREQ_FILE_SCALE);
            }
        }
        BufferedReader in = new BufferedReader(fstream, 500);
        String line;
        int value = 0;
        String[] segs;
        int freq61,freq62,freq63,freq64,freq65,freq66,freq67,freq68,freq69,freq610,freq611,freq612,freq613,freq614;
        freq61=freq62=freq63=freq64=freq65=freq66=freq67=freq68=freq69=freq610=freq611=freq612=freq613=freq614=0;


        while ((line = in.readLine()) != null) {
            // 300000  384000  600000  787200  998400  1094400  1190400  -->all possibilities in frequencies
            if (line.startsWith("384000")) {
                segs = line.trim().split("[ ]+");
                freq61 = Integer.parseInt(segs[1]) - old_freq61; // field 3
                old_freq61 = Integer.parseInt(segs[1]);

            }
            if (line.startsWith("480000")) {
                segs = line.trim().split("[ ]+");
                freq62 = Integer.parseInt(segs[1]) - old_freq62; // field 3
                old_freq62 = Integer.parseInt(segs[1]);
            }
            if (line.startsWith("633600")) {
                segs = line.trim().split("[ ]+");
                freq63 = Integer.parseInt(segs[1]) - old_freq63; // field 3
                old_freq63 = Integer.parseInt(segs[1]);

            }
            if (line.startsWith("768000")) {
                segs = line.trim().split("[ ]+");
                freq64 = Integer.parseInt(segs[1]) - old_freq64; // field 3
                old_freq64 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("864000")) {
                segs = line.trim().split("[ ]+");
                freq65 = Integer.parseInt(segs[1]) - old_freq65; // field 3
                old_freq65 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("960000")) {
                segs = line.trim().split("[ ]+");
                freq66 = Integer.parseInt(segs[1]) - old_freq66; // field 3
                old_freq66 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("1248000")) {
                segs = line.trim().split("[ ]+");
                freq67 = Integer.parseInt(segs[1]) - old_freq67; // field 3
                old_freq67 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("1344000")) {
                segs = line.trim().split("[ ]+");
                freq68 = Integer.parseInt(segs[1]) - old_freq68; // field 3
                old_freq68 = Integer.parseInt(segs[1]);


            }

            if (line.startsWith("1440000")) {
                segs = line.trim().split("[ ]+");
                freq69 = Integer.parseInt(segs[1]) - old_freq69; // field 3
                old_freq69 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1536000")) {
                segs = line.trim().split("[ ]+");
                freq610 = Integer.parseInt(segs[1]) - old_freq610; // field 3
                old_freq610 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1632000")) {
                segs = line.trim().split("[ ]+");
                freq611 = Integer.parseInt(segs[1]) - old_freq611; // field 3
                old_freq611 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1728000")) {
                segs = line.trim().split("[ ]+");
                freq612 = Integer.parseInt(segs[1]) - old_freq612; // field 3
                old_freq612 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1824000")) {
                segs = line.trim().split("[ ]+");
                freq613 = Integer.parseInt(segs[1]) - old_freq613; // field 3
                old_freq613 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1958400")) {
                segs = line.trim().split("[ ]+");
                freq614 = Integer.parseInt(segs[1]) - old_freq614; // field 3
                old_freq614 = Integer.parseInt(segs[1]);

            }

        }
        in.close();
        //           BigInteger three_total = (300000* three) / (three + six + eigth + ten);
        //           BigInteger six_total =(600000* six) / (three + six + eigth + ten);
        //           BigInteger eigth_total =(800000* eigth) / (three + six + eigth + ten);
        //           BigInteger ten_total =(1000000* ten) / (three + six + eigth + ten);

        double total =(384000*freq61 +480000*freq62  +  633600*freq63 + 768000*freq64 + 864000*freq65 + 960000*freq66 +
                1248000*freq67 + 1344000*freq68 + 1440000*freq69 +1536000*freq610  +1632000*freq611 +1728000*freq612 +1824000*freq613  +1958400* freq614)/
                (freq61+freq62+freq63+freq64+freq65+freq66+freq67+freq68+freq69+freq610+freq611+freq612+freq613+freq614);

        //           total = total * (1000);
        if(DBG) {
            Log.i(TAG, "Cpu6 Frequency is: " + total);
        }
        //           if(total<0){total = -total;}
        //  LoggerService.getLogger().cpuFreqUpdated(total);

        return total;
    }



    private double readCPU7() throws Exception {

        FileReader fstream = null;

        //    this.readCpuFreqScale();

        try {
            fstream = new FileReader(CPU7_FREQ_FILE_SCALE);
        } catch (FileNotFoundException e) {
            if (DBG) {
                Log.e("MonNet", "Could not read " + CPU7_FREQ_FILE_SCALE);
            }
        }
        BufferedReader in = new BufferedReader(fstream, 500);
        String line;
        int value = 0;
        String[] segs;
        int freq71,freq72,freq73,freq74,freq75,freq76,freq77,freq78,freq79,freq710,freq711,freq712,freq713,freq714;
        freq71=freq72=freq73=freq74=freq75=freq76=freq77=freq78=freq79=freq710=freq711=freq712=freq713=freq714=0;


        while ((line = in.readLine()) != null) {
            // 300000  384000  600000  787200  998400  1094400  1190400  -->all possibilities in frequencies
            if (line.startsWith("384000")) {
                segs = line.trim().split("[ ]+");
                freq71 = Integer.parseInt(segs[1]) - old_freq71; // field 3
                old_freq71 = Integer.parseInt(segs[1]);

            }
            if (line.startsWith("480000")) {
                segs = line.trim().split("[ ]+");
                freq72 = Integer.parseInt(segs[1]) - old_freq72; // field 3
                old_freq72 = Integer.parseInt(segs[1]);
            }
            if (line.startsWith("633600")) {
                segs = line.trim().split("[ ]+");
                freq73 = Integer.parseInt(segs[1]) - old_freq73; // field 3
                old_freq73 = Integer.parseInt(segs[1]);

            }
            if (line.startsWith("768000")) {
                segs = line.trim().split("[ ]+");
                freq74 = Integer.parseInt(segs[1]) - old_freq74; // field 3
                old_freq74 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("864000")) {
                segs = line.trim().split("[ ]+");
                freq75 = Integer.parseInt(segs[1]) - old_freq75; // field 3
                old_freq75 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("960000")) {
                segs = line.trim().split("[ ]+");
                freq76 = Integer.parseInt(segs[1]) - old_freq76; // field 3
                old_freq76 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("1248000")) {
                segs = line.trim().split("[ ]+");
                freq77 = Integer.parseInt(segs[1]) - old_freq77; // field 3
                old_freq77 = Integer.parseInt(segs[1]);


            }
            if (line.startsWith("1344000")) {
                segs = line.trim().split("[ ]+");
                freq78 = Integer.parseInt(segs[1]) - old_freq78; // field 3
                old_freq78 = Integer.parseInt(segs[1]);


            }

            if (line.startsWith("1440000")) {
                segs = line.trim().split("[ ]+");
                freq79 = Integer.parseInt(segs[1]) - old_freq79; // field 3
                old_freq79 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1536000")) {
                segs = line.trim().split("[ ]+");
                freq710 = Integer.parseInt(segs[1]) - old_freq710; // field 3
                old_freq710 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1632000")) {
                segs = line.trim().split("[ ]+");
                freq711 = Integer.parseInt(segs[1]) - old_freq711; // field 3
                old_freq711 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1728000")) {
                segs = line.trim().split("[ ]+");
                freq712 = Integer.parseInt(segs[1]) - old_freq712; // field 3
                old_freq712 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1824000")) {
                segs = line.trim().split("[ ]+");
                freq713 = Integer.parseInt(segs[1]) - old_freq713; // field 3
                old_freq713 = Integer.parseInt(segs[1]);

            }

            if (line.startsWith("1958400")) {
                segs = line.trim().split("[ ]+");
                freq714 = Integer.parseInt(segs[1]) - old_freq714; // field 3
                old_freq714 = Integer.parseInt(segs[1]);

            }

        }
        in.close();
        //           BigInteger three_total = (300000* three) / (three + six + eigth + ten);
        //           BigInteger six_total =(600000* six) / (three + six + eigth + ten);
        //           BigInteger eigth_total =(800000* eigth) / (three + six + eigth + ten);
        //           BigInteger ten_total =(1000000* ten) / (three + six + eigth + ten);

        double total =(384000*freq71 +480000*freq72  +  633600*freq73 + 768000*freq74 + 864000*freq75 + 960000*freq76 +
                1248000*freq77 + 1344000*freq78 + 1440000*freq79 +1536000*freq710  +1632000*freq711 +1728000*freq712 +1824000*freq713  +1958400* freq714)/
                (freq71+freq72+freq73+freq74+freq75+freq76+freq77+freq78+freq79+freq710+freq711+freq712+freq713+freq714);

        //           total = total * (1000);
        if(DBG) {
            Log.i(TAG, "Cpu Frequency is: " + total);
        }
        //           if(total<0){total = -total;}
        //  LoggerService.getLogger().cpuFreqUpdated(total);

        return total;
    }




}

