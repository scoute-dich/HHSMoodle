package de.baumann.hhsmoodle.helper;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import de.baumann.hhsmoodle.HHS_MainScreen;
import de.baumann.hhsmoodle.HHS_Note;
import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.popup.Popup_bookmarks;
import de.baumann.hhsmoodle.popup.Popup_info;
import de.baumann.hhsmoodle.popup.Popup_notes;

public class Widget_shortcuts extends AppWidgetProvider {

    private PendingIntent configPendingIntent5;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {

            PreferenceManager.setDefaultValues(context, R.xml.user_settings, false);
            final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            final String startURL = sharedPref.getString("favoriteURL", "https://moodle.huebsch.ka.schule-bw.de/moodle/");
            final String startType = sharedPref.getString("startType", "1");

            if (startType.equals("2")) {
                Intent configIntent5 = new Intent(context, HHS_MainScreen.class);
                configIntent5.putExtra("url", startURL);
                configPendingIntent5 = PendingIntent.getActivity(context, 0, configIntent5, 0);
            } else if (startType.equals("1")){
                Intent configIntent5 = new Intent(context, HHS_MainScreen.class);
                configPendingIntent5 = PendingIntent.getActivity(context, 0, configIntent5, 0);
            }

            Intent configIntent = new Intent(context, Popup_info.class);
            configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);

            Intent configIntent2 = new Intent(context, Popup_bookmarks.class);
            configIntent2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            PendingIntent configPendingIntent2 = PendingIntent.getActivity(context, 0, configIntent2, 0);

            Intent configIntent3 = new Intent(context, Popup_notes.class);
            configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            PendingIntent configPendingIntent3 = PendingIntent.getActivity(context, 0, configIntent3, 0);

            Intent configIntent4 = new Intent(context, HHS_Note.class);
            configIntent3.setAction(Intent.ACTION_MAIN);
            PendingIntent configPendingIntent4 = PendingIntent.getActivity(context, 0, configIntent4, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_shortcuts);

            views.setOnClickPendingIntent(R.id.imageButton, configPendingIntent);
            views.setOnClickPendingIntent(R.id.imageButton2, configPendingIntent2);
            views.setOnClickPendingIntent(R.id.imageButton3, configPendingIntent3);
            views.setOnClickPendingIntent(R.id.imageButton4, configPendingIntent4);
            views.setOnClickPendingIntent(R.id.textView, configPendingIntent5);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
