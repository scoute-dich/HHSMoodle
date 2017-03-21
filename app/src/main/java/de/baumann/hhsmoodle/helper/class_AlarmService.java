package de.baumann.hhsmoodle.helper;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class class_AlarmService extends IntentService {

    private SharedPreferences sharedPref;

    protected void onHandleIntent(Intent intent){

        Log.w("HHS_Moodle", "Alarm fired");
        sharedPref = PreferenceManager.getDefaultSharedPreferences(class_AlarmService.this);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if (day == Calendar.MONDAY) {
            alarms(1,2,3,4,5,6,7,8,9,10,11,12,
                    "hour_01","hour_02","hour_03","hour_04","hour_05","hour_06","hour_07","hour_08","hour_09","hour_10","hour_11");
        } else if (day == Calendar.TUESDAY) {
            alarms(12,13,14,15,16,17,18,19,20,21,22,23,
                    "hour_12","hour_13","hour_14","hour_15","hour_16","hour_17","hour_18","hour_19","hour_20","hour_21","hour_22");
        } else if (day == Calendar.WEDNESDAY) {
            alarms(23,24,25,26,27,28,29,30,31,32,33,34,
                    "hour_23","hour_24","hour_25","hour_26","hour_27","hour_28","hour_29","hour_30","hour_31","hour_32","hour_33");
        } else if (day == Calendar.THURSDAY) {
            alarms(34,35,36,37,38,39,40,41,42,43,44,45,
                    "hour_34","hour_35","hour_36","hour_37","hour_38","hour_39","hour_40","hour_41","hour_42","hour_43","hour_44");
        } else if (day == Calendar.FRIDAY) {
            alarms(45,46,47,48,49,50,51,52,53,54,55,1,
                    "hour_45","hour_46","hour_47","hour_48","hour_49","hour_50","hour_51","hour_52","hour_53","hour_54","hour_55");
        }
    }

    public class_AlarmService() {
        super("class_AlarmService");
        Log.d("hhs_moodle", "AlarmService");
    }

    private void setFlightMode(){
        if (isRooted()) {
            // Set Airplane / Flight mode using su commands.
            String command = "settings put global airplane_mode_on 1";
            executeCommandWithoutWait(command);
            command = "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true";
            executeCommandWithoutWait(command);
        }
    }

    private void setFlightModeOff(){
        if (isRooted()) {
            // Set Airplane / Flight mode using su commands.
            String command = "settings put global airplane_mode_on 0";
            executeCommandWithoutWait(command);
            command = "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false";
            executeCommandWithoutWait(command);
        }
    }

    private  boolean isRooted() {
        // get from build info
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }
        // check if /system/app/Superuser.apk is present
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e1) {
            // ignore
        }
        // try executing commands
        return canExecuteCommand("/system/xbin/which su")
                || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su");
    }

    // executes a command on the system
    private  boolean canExecuteCommand(String command) {
        boolean executedSuccessfully;
        try {
            Runtime.getRuntime().exec(command);
            executedSuccessfully = true;
        } catch (Exception e) {
            executedSuccessfully = false;
        }
        return executedSuccessfully;
    }

    private void executeCommandWithoutWait(String command) {
        String su = "su";
        for (int i=0; i < 3; i++) {
            // "su" command executed successfully.
            if (i == 1) {
                su = "/system/xbin/su";
            } else if (i == 2) {
                su = "/system/bin/su";
            }
            try {
                // execute command
                Runtime.getRuntime().exec(new String[]{su, "-c", command});
            } catch (IOException e) {
                Log.e(TAG, "su command has failed due to: " + e.fillInStackTrace());
            }
        }
    }

    private void alarms(int hour_1,int hour_2,int hour_3,int hour_4,int hour_5,
                       int hour_6,int hour_7,int hour_8,int hour_9,int hour_10,int hour_11,int hour_12,
                       String hour_1s,String hour_2s,String hour_3s,String hour_4s,String hour_5s,
                       String hour_6s,String hour_7s,String hour_8s,String hour_9s,String hour_10s,String hour_11s) {

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // 1. hour
        if ((hour == 7 && minute >= 40) || (hour == 8 && minute < 30)) {
            sharedPref.edit().putInt("getLine", hour_1).apply();
            if (sharedPref.getBoolean ("silent_mode", false)){
                if (sharedPref.getString(hour_1s, "false").equals("true")) {
                    if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightMode();}
                    AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
            }
            Log.w("HHS_Moodle", "Alarm Yeah 1");
        }

        // 2. hour
        if ((hour == 8 && minute >= 30) || (hour == 9 && minute < 15)) {
            sharedPref.edit().putInt("getLine", hour_2).apply();
            if (sharedPref.getBoolean ("silent_mode", false)){
                if (sharedPref.getString(hour_2s, "false").equals("true")) {
                        if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightMode();}
                        AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    } else if (sharedPref.getString(hour_1s, "false").equals("true")) {
                        if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightModeOff();}
                        AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    }
            }
            Log.w("HHS_Moodle", "Alarm Yeah 2");
        }

        // Break
        if ((hour == 9 && (minute >= 15 || minute < 30))) {
            sharedPref.edit().putInt("getLine", hour_3).apply();
            if (sharedPref.getBoolean ("silent_mode", false) && sharedPref.getString(hour_2s, "false").equals("true")){
                if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightModeOff();}
                    AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
            Log.w("HHS_Moodle", "Alarm Yeah B1");
        }

        // 3. hour
        if ((hour == 9 && minute >= 30) || (hour == 10 && minute < 20)) {
            sharedPref.edit().putInt("getLine", hour_3).apply();
            if (sharedPref.getBoolean ("silent_mode", false)){
                if (sharedPref.getString(hour_3s, "false").equals("true")) {
                    if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightMode();}
                    AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
            }
            Log.w("HHS_Moodle", "Alarm Yeah 3");
        }

        // 4. hour
        if ((hour == 10 && minute >= 20) || (hour == 11 && minute < 5)) {
            sharedPref.edit().putInt("getLine", hour_4).apply();
            if (sharedPref.getBoolean ("silent_mode", false)){
                if (sharedPref.getString(hour_4s, "false").equals("true")) {
                        if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightMode();}
                        AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    } else if (sharedPref.getString(hour_3s, "false").equals("true")){
                        if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightModeOff();}
                        AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    }
                }
            Log.w("HHS_Moodle", "Alarm Yeah 4");
        }

        // Break
        if ((hour == 11 && (minute >= 5 || minute < 20))) {
            sharedPref.edit().putInt("getLine", hour_5).apply();
            if (sharedPref.getBoolean ("silent_mode", false) && sharedPref.getString(hour_4s, "false").equals("true")){
                if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightModeOff();}
                    AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
            Log.w("HHS_Moodle", "Alarm Yeah B2");
        }

        // 5. hour
        if ((hour == 11 && minute >= 20) || (hour == 12 && minute < 10)) {
            sharedPref.edit().putInt("getLine", hour_5).apply();
            if (sharedPref.getBoolean ("silent_mode", false)){
                if (sharedPref.getString(hour_5s, "false").equals("true")) {
                    if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightMode();}
                    AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
            }
            Log.w("HHS_Moodle", "Alarm Yeah 5");
        }

        // 6. hour
        if (hour == 12 && (minute >= 10 && minute < 55)) {
            sharedPref.edit().putInt("getLine", hour_6).apply();
                if (sharedPref.getBoolean ("silent_mode", false)){
                    if (sharedPref.getString(hour_6s, "false").equals("true")) {
                        if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightMode();}
                        AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    } else if (sharedPref.getString(hour_5s, "false").equals("true")){
                        if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightModeOff();}
                        AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    }
            }
            Log.w("HHS_Moodle", "Alarm Yeah 6");
        }

        // Break
        if ((hour == 12 && minute >= 55) || (hour == 13 && minute < 15)) {
            sharedPref.edit().putInt("getLine", hour_7).apply();
            if (sharedPref.getBoolean ("silent_mode", false) && sharedPref.getString(hour_6s, "false").equals("true")){
                if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightModeOff();}
                    AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
            Log.w("HHS_Moodle", "Alarm Yeah B3");
        }

        // 7. hour
        if ((hour == 13 && minute >= 15) || (hour == 14 && minute < 5)) {
            sharedPref.edit().putInt("getLine", hour_7).apply();
            if (sharedPref.getBoolean ("silent_mode", false)){
                if (sharedPref.getString(hour_7s, "false").equals("true")) {
                    if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightMode();}
                    AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
            }
            Log.w("HHS_Moodle", "Alarm Yeah 7");
        }

        // 8. hour
        if ((hour == 14 && (minute >= 5 || minute < 50))) {
            sharedPref.edit().putInt("getLine", hour_8).apply();
                if (sharedPref.getBoolean ("silent_mode", false)){
                    if (sharedPref.getString(hour_8s, "false").equals("true")) {
                        if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightMode();}
                        AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    } else if (sharedPref.getString(hour_7s, "false").equals("true")){
                        if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightModeOff();}
                        AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    }
            }
            Log.w("HHS_Moodle", "Alarm Yeah 8");
        }

        // Break
        if (hour == 14 && minute >= 50) {
            sharedPref.edit().putInt("getLine", hour_9).apply();
            if (sharedPref.getBoolean ("silent_mode", false) && sharedPref.getString(hour_8s, "false").equals("true")){
                if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightModeOff();}
                    AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
            Log.w("HHS_Moodle", "Alarm Yeah B4");
        }

        // 9. hour
        if ((hour == 15 && (minute >= 0 || minute < 50))) {
            sharedPref.edit().putInt("getLine", hour_9).apply();
            if (sharedPref.getBoolean ("silent_mode", false)){
                if (sharedPref.getString(hour_9s, "false").equals("true")) {
                    if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightMode();}
                    AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
            }
            Log.w("HHS_Moodle", "Alarm Yeah 9");
        }

        // 10. hour
        if ((hour == 15 && minute >= 50) || (hour == 16 && minute < 35)) {
            sharedPref.edit().putInt("getLine", hour_10).apply();
                if (sharedPref.getBoolean ("silent_mode", false)){
                    if (sharedPref.getString(hour_10s, "false").equals("true")) {
                        if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightMode();}
                        AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    } else if (sharedPref.getString(hour_9s, "false").equals("true")){
                        if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightModeOff();}
                        AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    }
            }
            Log.w("HHS_Moodle", "Alarm Yeah 10");
        }

        // Break
        if ((hour == 16 && (minute >= 35 || minute < 40))) {
            sharedPref.edit().putInt("getLine", hour_11).apply();
            if (sharedPref.getBoolean ("silent_mode", false) && sharedPref.getString(hour_10s, "false").equals("true")){
                if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightModeOff();}
                    AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
            Log.w("HHS_Moodle", "Alarm Yeah B5");
        }

        // 11. hour
        if ((hour == 16 && minute >= 40) || (hour == 17 && minute < 30)) {
            sharedPref.edit().putInt("getLine", hour_11).apply();
            if (sharedPref.getBoolean ("silent_mode", false)){
                if (sharedPref.getString(hour_11s, "false").equals("true")) {
                    if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightMode();}
                    AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
            }
            Log.w("HHS_Moodle", "Alarm Yeah 11");
        }

        // End
        if ((hour == 17 && (minute >= 30 || minute < 59)) || hour >= 18) {
            sharedPref.edit().putInt("getLine", hour_12).apply();
            if (sharedPref.getBoolean ("silent_mode", false) && sharedPref.getString(hour_11s, "false").equals("true")){
                if (sharedPref.getBoolean ("airplane_mode", false)) {setFlightModeOff();}
                    AudioManager audioManager = (AudioManager)class_AlarmService.this.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
            Log.w("HHS_Moodle", "Alarm Yeah End");
        }
    }
}