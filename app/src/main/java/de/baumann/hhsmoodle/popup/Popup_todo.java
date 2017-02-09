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
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.activities.Activity_password;
import de.baumann.hhsmoodle.activities.Activity_todo;
import de.baumann.hhsmoodle.data_notes.Notes_helper;
import de.baumann.hhsmoodle.data_todo.Todo_DbAdapter;
import de.baumann.hhsmoodle.helper.class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_main;

public class Popup_todo extends Activity {

    //calling variables
    private Todo_DbAdapter db;
    private ListView lv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        class_SecurePreferences sharedPrefSec = new class_SecurePreferences(Popup_todo.this, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);
        String pw = sharedPrefSec.getString("protect_PW");

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (pw != null  && pw.length() > 0) {
            if (sharedPref.getBoolean("isOpened", true)) {
                helper_main.switchToActivity(Popup_todo.this, Activity_password.class, false);
            }
        }

        setContentView(R.layout.activity_popup);
        lv = (ListView) findViewById(R.id.dialogList);

        //calling Notes_DbAdapter
        db = new Todo_DbAdapter(Popup_todo.this);
        db.open();

        setTodoList();

        if (lv.getAdapter().getCount() == 0) {
            helper_main.makeToast(Popup_todo.this, getString(R.string.toast_noEntry));
            finish();
        }
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
                final String todo_creation = row2.getString(row2.getColumnIndexOrThrow("todo_creation"));

                View v = super.getView(position, convertView, parent);
                ImageView iv_icon = (ImageView) v.findViewById(R.id.icon_notes);
                ImageView iv_attachment = (ImageView) v.findViewById(R.id.att_notes);

                switch (todo_icon) {
                    case "3":
                        iv_icon.setImageResource(R.drawable.circle_green);
                        break;
                    case "2":
                        iv_icon.setImageResource(R.drawable.circle_yellow);
                        break;
                    case "1":
                        iv_icon.setImageResource(R.drawable.circle_red);
                        break;
                }

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

                iv_icon.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        final Item[] items = {
                                new Item(getString(R.string.note_priority_0), R.drawable.circle_green),
                                new Item(getString(R.string.note_priority_1), R.drawable.circle_yellow),
                                new Item(getString(R.string.note_priority_2), R.drawable.circle_red),
                        };

                        ListAdapter adapter = new ArrayAdapter<Item>(
                                Popup_todo.this,
                                android.R.layout.select_dialog_item,
                                android.R.id.text1,
                                items){
                            @NonNull
                            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                                //Use super class to create the View
                                View v = super.getView(position, convertView, parent);
                                TextView tv = (TextView)v.findViewById(android.R.id.text1);
                                tv.setTextSize(18);
                                tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);
                                //Add margin between image and text (support various screen densities)
                                int dp5 = (int) (24 * getResources().getDisplayMetrics().density + 0.5f);
                                tv.setCompoundDrawablePadding(dp5);

                                return v;
                            }
                        };

                        new AlertDialog.Builder(Popup_todo.this)
                                .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }
                                })
                                .setAdapter(adapter, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int item) {
                                        if (item == 0) {
                                            db.update(Integer.parseInt(_id),todo_title, todo_content, "3", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 1) {
                                            db.update(Integer.parseInt(_id),todo_title, todo_content, "2", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 2) {
                                            db.update(Integer.parseInt(_id),todo_title, todo_content, "1", todo_attachment, todo_creation);
                                            setTodoList();
                                        }
                                    }
                                }).show();
                    }
                });
                iv_attachment.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        switch (todo_attachment) {
                            case "true":
                                db.update(Integer.parseInt(_id), todo_title, todo_content, todo_icon, "", todo_creation);
                                setTodoList();
                                break;
                            default:
                                db.update(Integer.parseInt(_id), todo_title, todo_content, todo_icon, "true", todo_creation);
                                setTodoList();
                                break;
                        }
                    }
                });
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

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String todo_title = row2.getString(row2.getColumnIndexOrThrow("todo_title"));
                final String todo_content = row2.getString(row2.getColumnIndexOrThrow("todo_content"));
                final String todo_icon = row2.getString(row2.getColumnIndexOrThrow("todo_icon"));
                final String todo_attachment = row2.getString(row2.getColumnIndexOrThrow("todo_attachment"));
                final String todo_creation = row2.getString(row2.getColumnIndexOrThrow("todo_creation"));

                final CharSequence[] options = {
                        getString(R.string.bookmark_edit_title),
                        getString(R.string.todo_share),
                        getString(R.string.bookmark_createNote),
                        getString(R.string.bookmark_createEvent),
                        getString(R.string.bookmark_remove_bookmark)};
                new AlertDialog.Builder(Popup_todo.this)
                        .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals(getString(R.string.bookmark_edit_title))) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(Popup_todo.this);
                                    View dialogView = View.inflate(Popup_todo.this, R.layout.dialog_edit_title, null);

                                    final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                                    edit_title.setHint(R.string.bookmark_edit_title);
                                    edit_title.setText(todo_title);

                                    builder.setView(dialogView);
                                    builder.setTitle(R.string.bookmark_edit_title);
                                    builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            String inputTag = edit_title.getText().toString().trim();
                                            db.update(Integer.parseInt(_id), inputTag, todo_content, todo_icon, todo_attachment, todo_creation);
                                            setTodoList();
                                            Snackbar.make(lv, R.string.bookmark_added, Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                                    builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.cancel();
                                        }
                                    });

                                    final AlertDialog dialog2 = builder.create();
                                    // Display the custom alert dialog on interface
                                    dialog2.show();

                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            helper_main.showKeyboard(Popup_todo.this,edit_title);
                                        }
                                    }, 200);
                                }

                                if (options[item].equals (getString(R.string.todo_share))) {
                                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, todo_title);
                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, todo_content);
                                    startActivity(Intent.createChooser(sharingIntent, (getString(R.string.note_share_2))));
                                }

                                if (options[item].equals (getString(R.string.bookmark_createEvent))) {
                                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                                    calIntent.setType("vnd.android.cursor.item/event");
                                    calIntent.putExtra(CalendarContract.Events.TITLE, todo_title);
                                    calIntent.putExtra(CalendarContract.Events.DESCRIPTION, todo_content);
                                    startActivity(calIntent);
                                }

                                if (options[item].equals(getString(R.string.bookmark_remove_bookmark))) {
                                    Snackbar snackbar = Snackbar
                                            .make(lv, R.string.note_remove_confirmation, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.toast_yes, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    db.delete(Integer.parseInt(_id));
                                                    setTodoList();
                                                }
                                            });
                                    snackbar.show();
                                }

                                if (options[item].equals (getString(R.string.bookmark_createNote))) {

                                    sharedPref.edit()
                                            .putString("handleTextTitle", todo_title)
                                            .putString("handleTextText", todo_content)
                                            .putString("handleTextCreate", todo_creation)
                                            .putString("handleTextIcon", todo_icon)
                                            .putString("handleTextAttachment", todo_icon)
                                            .putString("handleTextSeqno", "")
                                            .apply();
                                    Notes_helper.newNote(Popup_todo.this);
                                }

                            }
                        }).show();

                return true;
            }
        });
    }

    public static class Item{
        public final String text;
        public final int icon;
        Item(String text, Integer icon) {
            this.text = text;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    @Override
    public void onBackPressed() {
        helper_main.isClosed(Popup_todo.this);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Popup_todo.this);
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Popup_todo.this);
    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isClosed(Popup_todo.this);
    }
}