/*
    This file is part of the HHS Moodle WebApp.

    HHS Moodle WebApp is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    HHS Moodle WebApp is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Diaspora Native WebApp.

    If not, see <http://www.gnu.org/licenses/>.
 */

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
import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.popup.Popup_bookmarks;
import de.baumann.hhsmoodle.popup.Popup_calendar;
import de.baumann.hhsmoodle.popup.Popup_info;
import de.baumann.hhsmoodle.popup.Popup_notes;

public class class_Widget_shortcuts extends AppWidgetProvider {


    public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {

            PreferenceManager.setDefaultValues(context, R.xml.user_settings, false);
            final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            final String startURL = sharedPref.getString("favoriteURL", "https://moodle.huebsch.ka.schule-bw.de/moodle/");

            Intent configIntent = new Intent(context, Popup_info.class);
            configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);

            Intent configIntent2 = new Intent(context, Popup_bookmarks.class);
            configIntent2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            PendingIntent configPendingIntent2 = PendingIntent.getActivity(context, 0, configIntent2, 0);

            Intent configIntent3 = new Intent(context, Popup_notes.class);
            configIntent3.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            PendingIntent configPendingIntent3 = PendingIntent.getActivity(context, 0, configIntent3, 0);

            Intent configIntent4 = new Intent(context, HHS_MainScreen.class);
            configIntent4.setAction(Intent.ACTION_SEND);
            PendingIntent configPendingIntent4 = PendingIntent.getActivity(context, 0, configIntent4, 0);

            Intent configIntent5 = new Intent(context, Popup_calendar.class);
            configIntent5.putExtra("url", startURL);
            PendingIntent configPendingIntent5 = PendingIntent.getActivity(context, 0, configIntent5, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_shortcuts);

            views.setOnClickPendingIntent(R.id.imageButton, configPendingIntent);
            views.setOnClickPendingIntent(R.id.imageButton2, configPendingIntent2);
            views.setOnClickPendingIntent(R.id.imageButton3, configPendingIntent3);
            views.setOnClickPendingIntent(R.id.imageButton4, configPendingIntent4);
            views.setOnClickPendingIntent(R.id.imageButton21, configPendingIntent5);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
