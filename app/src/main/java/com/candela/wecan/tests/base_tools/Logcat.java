package com.candela.wecan.tests.base_tools;

import android.os.Environment;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

public class Logcat extends AppCompatActivity {
    public void lc(){

        if ( isExternalStorageWritable() ) {

            File appDirectory = new File( Environment.getExternalStorageDirectory() + "/WE-CAN" );
            File logDirectory = new File( appDirectory + "/logs" );
            File logFile = new File( logDirectory, "logcat_" + System.currentTimeMillis() + ".txt" );

            // create app folder
            if ( !appDirectory.exists() ) {
                appDirectory.mkdir();
                System.out.println("App Directory Created");
                if( appDirectory.exists()) {
                    System.out.println("WE-CAN Folder Exists");
                }
            }

            // create log folder
            if ( !logDirectory.exists() ) {
                logDirectory.mkdir();
                System.out.println("Log Folder Created");
            }

            // clear the previous logcat and then write the new one to the file
            try {
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile);
            } catch ( IOException e ) {
                e.printStackTrace();
                System.out.println("Command not executed");
            }

        } else if ( isExternalStorageReadable() ) {
            System.out.println("External Storage not readable");
            // only readable
        } else {
            System.out.println("External Storage Not accessible");
        }

        /* Checks if external storage is available to at least read */
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) ) {
            return true;
        }
        return false;
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ) {
            return true;
        }
        return false;
    }


}
