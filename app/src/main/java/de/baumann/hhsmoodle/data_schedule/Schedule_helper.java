package de.baumann.hhsmoodle.data_schedule;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;
import java.util.Random;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.class_AlarmService;

/**
 * Created by juergen on 06.02.17
 */

public class Schedule_helper {

    public static void setAlarm (Activity activity) {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (day == Calendar.MONDAY ||
                day == Calendar.TUESDAY ||
                day == Calendar.WEDNESDAY ||
                day == Calendar.THURSDAY ||
                day == Calendar.FRIDAY) {

            if (hour <= 7) {
                setHours(activity, 7, 40);
                setHours(activity, 8, 30);
                setHours(activity, 9, 15);
                setHours(activity, 9, 30);
                setHours(activity, 10, 20);
                setHours(activity, 11, 5);
                setHours(activity, 11, 20);
                setHours(activity, 12, 10);
                setHours(activity, 12, 55);
                setHours(activity, 13, 15);
                setHours(activity, 14, 5);
                setHours(activity, 14, 50);
                setHours(activity, 15, 0);
                setHours(activity, 15, 50);
                setHours(activity, 16, 35);
                setHours(activity, 16, 40);
                setHours(activity, 17, 30);
            } else if (hour == 8) {
                setHours(activity, 8, 30);
                setHours(activity, 9, 15);
                setHours(activity, 9, 30);
                setHours(activity, 10, 20);
                setHours(activity, 11, 5);
                setHours(activity, 11, 20);
                setHours(activity, 12, 10);
                setHours(activity, 12, 55);
                setHours(activity, 13, 15);
                setHours(activity, 14, 5);
                setHours(activity, 14, 50);
                setHours(activity, 15, 0);
                setHours(activity, 15, 50);
                setHours(activity, 16, 35);
                setHours(activity, 16, 40);
                setHours(activity, 17, 30);
            } else if (hour == 9) {
                setHours(activity, 9, 15);
                setHours(activity, 9, 30);
                setHours(activity, 10, 20);
                setHours(activity, 11, 5);
                setHours(activity, 11, 20);
                setHours(activity, 12, 10);
                setHours(activity, 12, 55);
                setHours(activity, 13, 15);
                setHours(activity, 14, 5);
                setHours(activity, 14, 50);
                setHours(activity, 15, 0);
                setHours(activity, 15, 50);
                setHours(activity, 16, 35);
                setHours(activity, 16, 40);
                setHours(activity, 17, 30);
            } else if (hour == 10) {
                setHours(activity, 10, 20);
                setHours(activity, 11, 5);
                setHours(activity, 11, 20);
                setHours(activity, 12, 10);
                setHours(activity, 12, 55);
                setHours(activity, 13, 15);
                setHours(activity, 14, 5);
                setHours(activity, 14, 50);
                setHours(activity, 15, 0);
                setHours(activity, 15, 50);
                setHours(activity, 16, 35);
                setHours(activity, 16, 40);
                setHours(activity, 17, 30);
            } else if (hour == 11) {
                setHours(activity, 11, 5);
                setHours(activity, 11, 20);
                setHours(activity, 12, 10);
                setHours(activity, 12, 55);
                setHours(activity, 13, 15);
                setHours(activity, 14, 5);
                setHours(activity, 14, 50);
                setHours(activity, 15, 0);
                setHours(activity, 15, 50);
                setHours(activity, 16, 35);
                setHours(activity, 16, 40);
                setHours(activity, 17, 30);
            } else if (hour == 12) {
                setHours(activity, 12, 10);
                setHours(activity, 12, 55);
                setHours(activity, 13, 15);
                setHours(activity, 14, 5);
                setHours(activity, 14, 50);
                setHours(activity, 15, 0);
                setHours(activity, 15, 50);
                setHours(activity, 16, 35);
                setHours(activity, 16, 40);
                setHours(activity, 17, 30);
            } else if (hour == 13) {
                setHours(activity, 13, 15);
                setHours(activity, 14, 5);
                setHours(activity, 14, 50);
                setHours(activity, 15, 0);
                setHours(activity, 15, 50);
                setHours(activity, 16, 35);
                setHours(activity, 16, 40);
                setHours(activity, 17, 30);
            } else if (hour == 14) {
                setHours(activity, 14, 5);
                setHours(activity, 14, 50);
                setHours(activity, 15, 0);
                setHours(activity, 15, 50);
                setHours(activity, 16, 35);
                setHours(activity, 16, 40);
                setHours(activity, 17, 30);
            } else if (hour == 15) {
                setHours(activity, 15, 0);
                setHours(activity, 15, 50);
                setHours(activity, 16, 35);
                setHours(activity, 16, 40);
                setHours(activity, 17, 30);
            } else if (hour == 16) {
                setHours(activity, 16, 35);
                setHours(activity, 16, 40);
                setHours(activity, 17, 30);
            } else if (hour >= 17) {
                setHours(activity, 17, 30);
            }
        }
    }

    private static void setHours (Activity activity, int hour, int minute) {

        AlarmManager alarmMgr = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);

        Random rand = new Random();
        int n = rand.nextInt(1000000);

        Intent intent = new Intent(activity, class_AlarmService.class);
        PendingIntent pendingIntent = PendingIntent.getService(activity, n, intent, 0);

        // Set the alarm to start at approximately 2:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        Log.w("HHS_Moodle", "Alarm set");

        int ALARM_TYPE = AlarmManager.RTC_WAKEUP;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmMgr.setExactAndAllowWhileIdle(ALARM_TYPE, calendar.getTimeInMillis(), pendingIntent);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            alarmMgr.setExact(ALARM_TYPE, calendar.getTimeInMillis(), pendingIntent);
        else
            alarmMgr.set(ALARM_TYPE, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void insertDefaultBookmarks (Activity activity) {

        Schedule_DbAdapter db = new Schedule_DbAdapter(activity);
        db.open();

        // Weekend
        db.insert(activity.getString(R.string.schedule_weekend), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), activity.getString(R.string.schedule_weekend), "00");

        // Monday
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "1. " + activity.getString(R.string.schedule_mon) + " " + "07:45 - 08:30", "01");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "2. " + activity.getString(R.string.schedule_mon) + " " + "08:30 - 09:15", "02");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "3. " + activity.getString(R.string.schedule_mon) + " " + "09:35 - 10:20", "03");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "4. " + activity.getString(R.string.schedule_mon) + " " + "10:20 - 11:05", "04");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "5. " + activity.getString(R.string.schedule_mon) + " " + "11:25 - 12:10", "05");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "6. " + activity.getString(R.string.schedule_mon) + " " + "12:10 - 12:55", "06");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "7. " + activity.getString(R.string.schedule_mon) + " " + "13:20 - 14:05", "07");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "8. " + activity.getString(R.string.schedule_mon) + " " + "14-05 - 14:50", "08");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "9. " + activity.getString(R.string.schedule_mon) + " " + "15-05 - 15:50", "09");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "10. " + activity.getString(R.string.schedule_mon) + " " + "15-50 - 16:35", "10");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "11. " + activity.getString(R.string.schedule_mon) + " " + "16-45 - 17:30", "11");

        // Tuesday
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "1. " + activity.getString(R.string.schedule_tue) + " " + "07:45 - 08:30", "12");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "2. " + activity.getString(R.string.schedule_tue) + " " + "08:45 - 09:15", "13");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "3. " + activity.getString(R.string.schedule_tue) + " " + "09:35 - 10:20", "14");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "4. " + activity.getString(R.string.schedule_tue) + " " + "10:20 - 11:05", "15");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "5. " + activity.getString(R.string.schedule_tue) + " " + "11:25 - 12:10", "16");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "6. " + activity.getString(R.string.schedule_tue) + " " + "12:10 - 12:55", "17");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "7. " + activity.getString(R.string.schedule_tue) + " " + "13:20 - 14:05", "18");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "8. " + activity.getString(R.string.schedule_tue) + " " + "14-05 - 14:50", "19");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "8. " + activity.getString(R.string.schedule_tue) + " " + "15-05 - 15:50", "20");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "10. " + activity.getString(R.string.schedule_tue) + " " + "15-50 - 16:35", "21");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "11. " + activity.getString(R.string.schedule_tue) + " " + "16-45 - 17:30", "22");

        // Wednesday
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "1. " + activity.getString(R.string.schedule_wed) + " " + "07:45 - 08:30", "23");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "2. " + activity.getString(R.string.schedule_wed) + " " + "08:45 - 09:15", "24");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "3. " + activity.getString(R.string.schedule_wed) + " " + "09:35 - 10:20", "25");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "4. " + activity.getString(R.string.schedule_wed) + " " + "10:20 - 11:05", "26");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "5. " + activity.getString(R.string.schedule_wed) + " " + "11:25 - 12:10", "27");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "6. " + activity.getString(R.string.schedule_wed) + " " + "12:10 - 12:55", "28");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "7. " + activity.getString(R.string.schedule_wed) + " " + "13:20 - 14:05", "29");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "8. " + activity.getString(R.string.schedule_wed) + " " + "14-05 - 14:50", "30");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "9. " + activity.getString(R.string.schedule_wed) + " " + "15-05 - 15:50", "31");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "10. " + activity.getString(R.string.schedule_wed) + " " + "15-50 - 16:35", "32");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "11. " + activity.getString(R.string.schedule_wed) + " " + "16-45 - 17:30", "33");

        // Thursday
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "1. " + activity.getString(R.string.schedule_thu) + " " + "07:45 - 08:30", "34");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "2. " + activity.getString(R.string.schedule_thu) + " " + "08:45 - 09:15", "35");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "3. " + activity.getString(R.string.schedule_thu) + " " + "09:35 - 10:20", "36");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "4. " + activity.getString(R.string.schedule_thu) + " " + "10:20 - 11:05", "37");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "5. " + activity.getString(R.string.schedule_thu) + " " + "11:25 - 12:10", "38");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "6. " + activity.getString(R.string.schedule_thu) + " " + "12:10 - 12:55", "39");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "7. " + activity.getString(R.string.schedule_thu) + " " + "13:20 - 14:05", "40");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "8. " + activity.getString(R.string.schedule_thu) + " " + "14-05 - 14:50", "41");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "9. " + activity.getString(R.string.schedule_thu) + " " + "15-05 - 15:50", "42");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "10. " + activity.getString(R.string.schedule_thu) + " " + "15-50 - 16:35", "43");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "11. " + activity.getString(R.string.schedule_thu) + " " + "16-45 - 17:30", "44");

        // Friday
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "1. " + activity.getString(R.string.schedule_fri) + " " + "07:45 - 08:30", "45");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "2. " + activity.getString(R.string.schedule_fri) + " " + "08:45 - 09:15", "46");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "3. " + activity.getString(R.string.schedule_fri) + " " + "09:35 - 10:20", "47");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "4. " + activity.getString(R.string.schedule_fri) + " " + "10:20 - 11:05", "48");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "5. " + activity.getString(R.string.schedule_fri) + " " + "11:25 - 12:10", "49");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "6. " + activity.getString(R.string.schedule_fri) + " " + "12:10 - 12:55", "50");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "7. " + activity.getString(R.string.schedule_fri) + " " + "13:20 - 14:05", "51");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "8. " + activity.getString(R.string.schedule_fri) + " " + "14-05 - 14:50", "52");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "9. " + activity.getString(R.string.schedule_fri) + " " + "15-05 - 15:50", "53");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "10. " + activity.getString(R.string.schedule_fri) + " " + "15-50 - 16:35", "54");
        db.insert(activity.getString(R.string.schedule_def_title), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), "11. " + activity.getString(R.string.schedule_fri) + " " + "16-45 - 17:30", "55");

        // Weekend
        db.insert(activity.getString(R.string.schedule_weekend), activity.getString(R.string.schedule_def_teacher), "11", activity.getString(R.string.schedule_def_teacher), activity.getString(R.string.schedule_weekend), "56");
    }
}
