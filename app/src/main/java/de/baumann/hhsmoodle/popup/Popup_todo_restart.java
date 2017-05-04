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

package de.baumann.hhsmoodle.popup;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.data_schedule.Schedule_helper;
import de.baumann.hhsmoodle.data_todo.Todo_DbAdapter;
import de.baumann.hhsmoodle.helper.class_AlarmService;
import de.baumann.hhsmoodle.helper.helper_security;

@SuppressWarnings("SameParameterValue")
public class Popup_todo_restart extends Activity {

    private Todo_DbAdapter db;
    private ListView lv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            helper_security.decrypt(Popup_todo_restart.this, "/databases/todo_DB_v01_en.db", "/databases/todo_DB_v01.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_popup_restart);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        lv = (ListView)findViewById(R.id.dialogList);
        //calling Notes_DbAdapter
        db = new Todo_DbAdapter(Popup_todo_restart.this);
        db.open();
        setTodoList();
    }

    private void setTodoList() {

        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();

        Schedule_helper.setAlarm(Popup_todo_restart.this);

        Intent serviceIntent = new Intent(Popup_todo_restart.this, class_AlarmService.class);
        startService(serviceIntent);

        //display data
        final int layoutstyle=R.layout.list_item_notes;
        int[] xml_id = new int[] {
                R.id.textView_title_notes,
                R.id.textView_des_notes,
                R.id.textView_create_notes
        };
        String[] column = new String[] {
                "todo_title",
                "todo_content",
                "todo_creation"
        };
        final Cursor row = db.fetchAllData(Popup_todo_restart.this);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(Popup_todo_restart.this, layoutstyle,row,column, xml_id, 0) {
            @Override
            public View getView (final int position, View convertView, ViewGroup parent) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String todo_title = row2.getString(row2.getColumnIndexOrThrow("todo_title"));
                final String todo_content = row2.getString(row2.getColumnIndexOrThrow("todo_content"));
                final String todo_attachment = row2.getString(row2.getColumnIndexOrThrow("todo_attachment"));

                View v = super.getView(position, convertView, parent);
                ImageView iv_attachment = (ImageView) v.findViewById(R.id.att_notes);

                switch (todo_attachment) {
                    case "true":
                        iv_attachment.setVisibility(View.VISIBLE);
                        iv_attachment.setImageResource(R.drawable.alert_circle);
                        break;
                    default:
                        iv_attachment.setVisibility(View.VISIBLE);
                        iv_attachment.setImageResource(R.drawable.alert_circle_red);

                        int n = Integer.valueOf(_id);

                        android.content.Intent iMain = new android.content.Intent();
                        iMain.setAction("shortcutToDo");
                        iMain.setClassName(Popup_todo_restart.this, "de.baumann.hhsmoodle.activities.Activity_splash");
                        PendingIntent piMain = PendingIntent.getActivity(Popup_todo_restart.this, n, iMain, 0);

                        NotificationCompat.Builder builderSummary =
                                new NotificationCompat.Builder(Popup_todo_restart.this)
                                        .setSmallIcon(R.drawable.school)
                                        .setColor(ContextCompat.getColor(Popup_todo_restart.this, R.color.colorPrimary))
                                        .setGroup("HHS_Moodle")
                                        .setGroupSummary(true)
                                        .setContentIntent(piMain);

                        Notification notification = new NotificationCompat.Builder(Popup_todo_restart.this)
                                .setColor(ContextCompat.getColor(Popup_todo_restart.this, R.color.colorPrimary))
                                .setSmallIcon(R.drawable.school)
                                .setContentTitle(todo_title)
                                .setContentText(todo_content)
                                .setContentIntent(piMain)
                                .setAutoCancel(true)
                                .setGroup("HHS_Moodle")
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(todo_content))
                                .setPriority(Notification.PRIORITY_DEFAULT)
                                .setVibrate(new long[0])
                                .build();

                        NotificationManager notificationManager = (NotificationManager) Popup_todo_restart.this.getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(n, notification);
                        notificationManager.notify(0, builderSummary.build());
                        break;
                }
                return v;
            }
        };

        lv.setAdapter(adapter);
        //onClick function
        try {
            helper_security.encrypt(Popup_todo_restart.this, "/databases/todo_DB_v01.db","/databases/todo_DB_v01_en.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 1000);
    }
}