package com.cpuconf;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by emir on 3/23/16.
 */
public class Logger {


    final private static boolean DBG = Definitions.DBG;
    final static public String TAG = "Logger";
    private static final String LOG_FILE_NAME = "CpuConfLogs.log";
    private static final String ARFF_FILE_NAME = "CpuConfLogs.arff";
    private static final String FPS_CHECKING = "FPS.log";

    private static final String LOG_FILE_NAME_PHONE = "CpuConfLogs2";
    final static public String UPLOAD_FILE_NAME = "Upload.log";
    private static FileOutputStream mOutputStream = null;
    private static FileOutputStream mOutputStreamArff = null;
    private static FileOutputStream mOutputStreamFps = null;


    final private static Object mLogLock = new Object();

    private static final byte[] SPACE  = " ".getBytes();
    private static final byte[] NEWLINE= "\n".getBytes();
    private static final byte[] COMMA= ",".getBytes();



    public static void createLogFile(Context c) {
        synchronized (mLogLock) {
            try {
                mOutputStream = c.openFileOutput(LOG_FILE_NAME, Context.MODE_APPEND);
            } catch (Exception e) {
                Log.e(TAG, "Can't open file " + LOG_FILE_NAME + ":" + e);

            }
        }
    }


    public static void createArffFile(Context c) {
        synchronized (mLogLock) {
            try {
                mOutputStreamArff = c.openFileOutput(ARFF_FILE_NAME, Context.MODE_APPEND);
            } catch (Exception e) {
                Log.e(TAG, "Can't open file " + ARFF_FILE_NAME + ":" + e);

            }
        }
    }

    public static void createFpsFile(Context c) {
        synchronized (mLogLock) {
            try {
                mOutputStreamFps = c.openFileOutput(FPS_CHECKING, Context.MODE_APPEND);
            } catch (Exception e) {
                Log.e(TAG, "Can't open file " + ARFF_FILE_NAME + ":" + e);

            }
        }
    }


/*
    public static void createLogFileToUpload(Context c) {
        synchronized (mLogLock) {
            try {
                mOutputStreamPhone = c.openFileOutput(LOG_FILE_NAME_PHONE, Context.MODE_APPEND);
            } catch (Exception e) {
                Log.e(TAG, "Can't open file " + LOG_FILE_NAME_PHONE + ":" + e);

            }
        }
    }
*/




    public void logEntry(String s){
        try {
            mOutputStream.write(s.getBytes());
            mOutputStream.write(NEWLINE);
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }


    public void fpsLogEntry(String s){
        try {
            mOutputStreamFps.write(s.getBytes());
            mOutputStreamFps.write(NEWLINE);
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }

    public void arffEntryLong(long s){
        try {
            mOutputStreamArff.write(ToByteString.getBytes(s));
            mOutputStreamArff.write(COMMA);
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }

    public void arffEntryLongLast(long s){
        try {
            mOutputStreamArff.write(ToByteString.getBytes(s));
          //  mOutputStreamArff.write(COMMA);
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }

    private static final int DBUFSIZE = 16;
    private static final byte[] dbuf = new byte[DBUFSIZE];

    public void arffEntryDouble(double i) {
        int count = ToByteString.putBytes(i, dbuf, 0);
        try {

            mOutputStreamArff.write(dbuf, 0, count);
            mOutputStreamArff.write(COMMA);
        } catch (IOException ioe) {
            if (DBG) {Log.e(TAG, "ERROR: Can't write double to file: " + ioe);}
        }
    }



    public void arffEntryNewInstance(){
        try {
            mOutputStreamArff.write(NEWLINE);
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }


    public static void InitiateArffFile(String[] s) { // feature list in arff format
        synchronized (mLogLock) {
            try {
                mOutputStreamArff.write("@relation".getBytes());
                mOutputStreamArff.write(SPACE);
                mOutputStreamArff.write("CpuConfLogs".getBytes());
                mOutputStreamArff.write(NEWLINE);
                mOutputStreamArff.write(NEWLINE);

                for (int i = 0; i < s.length; i++) {  // for all attributes

                    mOutputStreamArff.write("@attribute".getBytes());
                    mOutputStreamArff.write(SPACE);
                    mOutputStreamArff.write(s[i].getBytes());
                    mOutputStreamArff.write(SPACE);
                    mOutputStreamArff.write("numeric".getBytes());
                    mOutputStreamArff.write(NEWLINE);

                }

                mOutputStreamArff.write(NEWLINE);
                mOutputStreamArff.write("@data".getBytes());
                mOutputStreamArff.write(NEWLINE);

            } catch (IOException ioe) {
                Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
            }
        }
    }







    public static long logFileSize(Context c) {
        File log_file = new File(c.getFilesDir(), ARFF_FILE_NAME);
        if (!log_file.exists()) {
            return 0;
        }
        return log_file.length();
    }


    public static class ToByteString {
        private ToByteString() {
            throw new AssertionError();
        }

        private static final byte[] ZERO = "0".getBytes();

        public static byte[] getBytes(int i) {
            //int init = i;
            boolean neg = (i<0);
            int inc = neg ? 1 : 0;
            i=Math.abs(i);

            int length;
            if (i == 0) {
                return ZERO;
            } else {
                length = ((int)Math.log10(i))+1;
            }

            byte[] output;
            if (neg) {
                output = new byte[length+1];
                output[0]='-';
            } else {
                output = new byte[length];
            }

            putBytes(i, output, inc);

			/*
			int rem;
			for (int j=0; j<(length); j++) {
				rem = i % 10;
				i = i / 10;
				output[length+inc-j-1]=(byte)(rem+'0');
			}
			*/
			/*
			// Compare
			String s = Integer.toString(init);
			boolean error = false;
			byte[] comp = s.getBytes();
			for (int j=0; j<output.length; j++) {
				if (output[j] != comp[j]) {
					Log.e(TAG, "ERROR: input: "+i+" , byte "+j+", \""+output[j]+"\" != \""+comp[j]+"\"");
					error = true;
				}
			}
			if (! error) {
				Log.d(TAG, "Conversion OK");
			}*/

            return output;
        }
        public static byte[] getBytes(long l) {
            boolean neg = (l<0);
            int inc = neg ? 1 : 0;
            l=Math.abs(l);
            //long init = l;

            int length;
            if (l == 0) {
                return ZERO;
            } else {
                length = ((int)Math.log10(l))+1;
            }

            byte[] output;
            if (neg) {
                output = new byte[length+1];
                output[0]='-';
            } else {
                output = new byte[length];
            }

            long rem;
            for (int j=0; j<(length); j++) {
                rem = l % 10;
                l = l / 10;
                output[length+inc-j-1]=(byte)(rem+'0');
            }



            return output;
        }


        /**
         * Put the bytestring representation of integer 'i' into the buffer 'buf'
         * starting at 'offset'.
         *
         * @param i
         * @param buf
         * @param offset
         * @return number of bytes written
         */
        public static int putBytes(int i, byte[] buf, int offset) {
            boolean neg = (i<0);

            i=Math.abs(i);
            //int init = i;

            // length is the number of bytes to represent the number WITHOUT the sign
            int length;
            if (i == 0) {
                buf[offset] = ZERO[0];
                return 1;
            } else {
                length = ((int)Math.log10(i))+1;
            }

            // output is the number of bytes to represent the number WITH the sign
            int output = length;
            if (neg) {
                output++;
                buf[offset]='-';
            }

            int rem;
            for (int j=0; j<(length); j++) {
                rem = i % 10;
                i = i / 10;
                buf[offset+output-j-1]=(byte)(rem+'0');
            }

			/*
			// Compare
			String s = Integer.toString(init);
			boolean error = false;
			byte[] comp = s.getBytes();
			for (int j=0; j<output.length; j++) {
				if (output[j] != comp[j]) {
					Log.e(TAG, "ERROR: input: "+i+" , byte "+j+", \""+output[j]+"\" != \""+comp[j]+"\"");
					error = true;
				}
			}
			if (! error) {
				Log.d(TAG, "Conversion OK");
			}
			*/

            return output;
        }

        /**
         * Put the bytestring representation of double 'd' into the buffer 'buf'
         * starting at 'offset'.
         *
         * The format limits to 2 places after the decimal point
         *
         * @param d
         * @param buf
         * @param offset
         * @return number of bytes written
         */
        public static int putBytes(double d, byte[] buf, int offset) {
            //if (DBG) Log.d(TAG, "Input: \""+mDoubleFmt.format(d)+"\"");
            int intPart = (int)d;
            d = d - intPart;
            int fracPart = (int)Math.round(d*100.0);

            // integer part
            int count = putBytes(intPart, buf, offset);
            // decimal point
            buf[offset+count] = '.';
            count += 1;
            // fractional part
            count += putBytes(fracPart, buf, offset+count);

            //if (DBG) Log.d(TAG, "Output: \""+(new String(buf, offset, count))+"\"");

            return count;
        }
    }
};

