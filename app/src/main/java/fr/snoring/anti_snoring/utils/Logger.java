package fr.snoring.anti_snoring.utils;

import android.util.Log;

public class Logger {

    private static final String APP_NAME = "Antisnoring";

    public static void error(String msg) {
        Log.e(APP_NAME, msg);
    }

}
