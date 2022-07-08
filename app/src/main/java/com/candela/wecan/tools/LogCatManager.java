package com.candela.wecan.tools;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.candela.wecan.StartupActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class LogCatManager extends Thread {
    /*
            * Called when the application is starting, before any activity, service, or receiver objects (excluding content providers) have been created.
     */
    public LogCatManager(){
        this.StartSupplicantLog();
        this.StartAppLog();
    }
    public void StartAppLog() {
        if ( isExternalStorageWritable() ) {

            File logFile = new File(Environment.getExternalStorageDirectory() + File.separator + "lf_interop.log");
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            int id = android.os.Process.myPid();
            int sizePerFile = 6000; // size in kilobytes
            int rotationCount = 10; // file rotation count
            String filter = "D"; // Debug priority
            Log.d(StartupActivity.TAG, "Process ID of Interop App: " + String.valueOf(id));
            String[] args = new String[]{"logcat",
                    "--pid", String.valueOf(id),
                    "-v", "time",
                    "-f", logFile.getAbsolutePath(),
                    "-r", Integer.toString(sizePerFile),
                    "-n", Integer.toString(rotationCount),
                    "*:" + filter};
            Log.d(StartupActivity.TAG, Arrays.toString(args));
            try {
                Process logProcess = Runtime.getRuntime().exec(args);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if ( isExternalStorageReadable() ) {
            Log.e(StartupActivity.TAG, "External Storage is Not Readable for Capturing Logs");
            // only readable
        } else {
            Log.e(StartupActivity.TAG, "External Storage is Not Accessible for Capturing Logs");
            // not accessible
        }
    }
    public void StartSupplicantLog() {

        if ( isExternalStorageWritable() ) {

            File logFile = new File(Environment.getExternalStorageDirectory() + File.separator + "lf_interop_wpa_supplicant.log");
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            int sizePerFile = 6000; // size in kilobytes
            int rotationCount = 10; // file rotation count

            String[] args = new String[]{"logcat",
                    "wpa_supplicant:V" , "*:S",
                    "-v", "time",
                    "-f", logFile.getAbsolutePath(),
                    "-r", Integer.toString(sizePerFile),
                    "-n", Integer.toString(rotationCount)};
            Log.d(StartupActivity.TAG, Arrays.toString(args));
            try {
                Process logProcess = Runtime.getRuntime().exec(args);
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if ( isExternalStorageReadable() ) {
            Log.e(StartupActivity.TAG, "External Storage is Not Readable for Capturing Logs");
            // only readable
        } else {
            Log.e(StartupActivity.TAG, "External Storage is Not Accessible for Capturing Logs");
            // not accessible
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) ) {
            return true;
        }
        return false;
    }
}
