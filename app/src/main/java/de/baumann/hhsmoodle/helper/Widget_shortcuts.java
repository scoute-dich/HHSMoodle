package de.baumann.hhsmoodle.helper;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import de.baumann.hhsmoodle.HHS_Browser;
import de.baumann.hhsmoodle.HHS_MainScreen;
import de.baumann.hhsmoodle.HHS_Note;
import de.baumann.hhsmoodle.Notes_MainActivity;
import de.baumann.hhsmoodle.R;

public class Widget_shortcuts extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {

            PreferenceManager.setDefaultValues(context, R.xml.user_settings, false);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String startURL = sharedPref.getString("favoriteURL", "https://moodle.huebsch.ka.schule-bw.de/moodle/");

            Intent configIntent = new Intent(context, HHS_Note.class);
            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);

            Intent configIntent2 = new Intent(context, Notes_MainActivity.class);
            PendingIntent configPendingIntent2 = PendingIntent.getActivity(context, 0, configIntent2, 0);

            Intent configIntent3 = new Intent(context, Notes_MainActivity.class);
            configIntent3.setAction(Intent.ACTION_MAIN);
            PendingIntent configPendingIntent3 = PendingIntent.getActivity(context, 0, configIntent3, 0);

            Intent configIntent4 = new Intent(context, HHS_MainScreen.class);
            PendingIntent configPendingIntent4 = PendingIntent.getActivity(context, 0, configIntent4, 0);

            Intent configIntent5 = new Intent(context, HHS_Browser.class);

            configIntent5.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            configIntent5.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            configIntent5.putExtra("url", startURL);
            PendingIntent configPendingIntent5 = PendingIntent.getActivity(context, 0, configIntent5, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_shortcuts);

            views.setOnClickPendingIntent(R.id.imageButton, configPendingIntent);
            views.setOnClickPendingIntent(R.id.imageButton2, configPendingIntent2);
            views.setOnClickPendingIntent(R.id.imageButton3, configPendingIntent3);
            views.setOnClickPendingIntent(R.id.imageButton4, configPendingIntent5);
            views.setOnClickPendingIntent(R.id.textView, configPendingIntent4);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
