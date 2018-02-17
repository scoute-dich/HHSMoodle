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
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.activities.Activity_todo;
import de.baumann.hhsmoodle.data_todo.Todo_DbAdapter;
import de.baumann.hhsmoodle.helper.helper_main;

public class Popup_todo extends Activity {

    //calling variables
    private Todo_DbAdapter db;
    private ListView lv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_popup);
        lv = findViewById(R.id.dialogList);

        //calling Notes_DbAdapter
        db = new Todo_DbAdapter(Popup_todo.this);
        db.open();

        setTodoList();
    }

    private void setTodoList() {

        PreferenceManager.setDefaultValues(Popup_todo.this, R.xml.user_settings, false);
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Popup_todo.this);

        NotificationManager nMgr = (NotificationManager) Popup_todo.this.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();

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

        final String search = sharedPref.getString("filter_todo_subject", "");
        final Cursor row = db.fetchDataByFilter(search, "todo_title");
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(Popup_todo.this, layoutstyle,row,column, xml_id, 0) {
            @Override
            public View getView (final int position, View convertView, ViewGroup parent) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String todo_title = row2.getString(row2.getColumnIndexOrThrow("todo_title"));
                final String todo_content = row2.getString(row2.getColumnIndexOrThrow("todo_content"));
                final String todo_icon = row2.getString(row2.getColumnIndexOrThrow("todo_icon"));
                final String todo_attachment = row2.getString(row2.getColumnIndexOrThrow("todo_attachment"));

                View v = super.getView(position, convertView, parent);
                ImageView iv_icon = v.findViewById(R.id.icon_notes);
                ImageView iv_attachment = v.findViewById(R.id.att_notes);
                helper_main.switchIcon(Popup_todo.this, todo_icon, "todo_icon", iv_icon);

                switch (todo_attachment) {
                    case "true":
                        iv_attachment.setVisibility(View.VISIBLE);
                        iv_attachment.setImageResource(R.drawable.alert_circle_dark);
                        break;
                    default:
                        iv_attachment.setVisibility(View.VISIBLE);
                        iv_attachment.setImageResource(R.drawable.alert_circle_red);

                        int n = Integer.valueOf(_id);

                        android.content.Intent iMain = new android.content.Intent();
                        iMain.setAction("shortcutToDo");
                        iMain.setClassName(Popup_todo.this, "de.baumann.hhsmoodle.activities.Activity_splash");
                        PendingIntent piMain = PendingIntent.getActivity(Popup_todo.this, n, iMain, 0);

                        NotificationCompat.Builder builderSummary =
                                new NotificationCompat.Builder(Popup_todo.this)
                                        .setSmallIcon(R.drawable.school)
                                        .setColor(ContextCompat.getColor(Popup_todo.this, R.color.colorPrimary))
                                        .setGroup("HHS_Moodle")
                                        .setGroupSummary(true)
                                        .setContentIntent(piMain);

                        Notification notification = new NotificationCompat.Builder(Popup_todo.this)
                                .setColor(ContextCompat.getColor(Popup_todo.this, R.color.colorPrimary))
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

                        NotificationManager notificationManager = (NotificationManager) Popup_todo.this.getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(n, notification);
                        notificationManager.notify(0, builderSummary.build());
                        break;
                }
                return v;
            }
        };

        lv.setAdapter(adapter);
        //onClick function
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String todo_title = row2.getString(row2.getColumnIndexOrThrow("todo_title"));
                final String todo_content = row2.getString(row2.getColumnIndexOrThrow("todo_content"));
                final String todo_icon = row2.getString(row2.getColumnIndexOrThrow("todo_icon"));
                final String todo_attachment = row2.getString(row2.getColumnIndexOrThrow("todo_attachment"));
                final String todo_creation = row2.getString(row2.getColumnIndexOrThrow("todo_creation"));

                sharedPref.edit().putString("toDo_title", todo_title).apply();
                sharedPref.edit().putString("toDo_text", todo_content).apply();
                sharedPref.edit().putString("toDo_seqno", _id).apply();
                sharedPref.edit().putString("toDo_icon", todo_icon).apply();
                sharedPref.edit().putString("toDo_create", todo_creation).apply();
                sharedPref.edit().putString("toDo_attachment", todo_attachment).apply();

                helper_main.switchToActivity(Popup_todo.this, Activity_todo.class, false);
            }
        });

        if (lv.getAdapter().getCount() == 0) {
            new android.app.AlertDialog.Builder(this)
                    .setMessage(helper_main.textSpannable(getString(R.string.toast_noEntry)))
                    .setPositiveButton(this.getString(R.string.toast_yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            })
                    .show();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, 2000);
        }
    }
}