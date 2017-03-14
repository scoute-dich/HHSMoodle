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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.activities.Activity_EditNote;
import de.baumann.hhsmoodle.activities.Activity_password;
import de.baumann.hhsmoodle.data_random.Random_DbAdapter;
import de.baumann.hhsmoodle.data_courses.Courses_DbAdapter;
import de.baumann.hhsmoodle.data_todo.Todo_helper;
import de.baumann.hhsmoodle.helper.class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_main;

public class Popup_courseList extends Activity {

    private ListView lv = null;
    private Courses_DbAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        class_SecurePreferences sharedPrefSec = new class_SecurePreferences(Popup_courseList.this, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);
        String pw = sharedPrefSec.getString("protect_PW");

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (pw != null  && pw.length() > 0) {
            if (sharedPref.getBoolean("isOpened", true)) {
                helper_main.switchToActivity(Popup_courseList.this, Activity_password.class, false);
            }
        }

        setContentView(R.layout.activity_popup);

        lv = (ListView)findViewById(R.id.dialogList);

        db = new Courses_DbAdapter(Popup_courseList.this);
        db.open();

        onNewIntent(getIntent());
        setCoursesList();
    }

    protected void onNewIntent(final Intent intent) {

        String action = intent.getAction();
        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if ("courseList_random".equals(action)) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                    final String courses_title = row2.getString(row2.getColumnIndexOrThrow("courses_title"));
                    final String courses_content = row2.getString(row2.getColumnIndexOrThrow("courses_content"));
                    Random_DbAdapter db = new Random_DbAdapter(Popup_courseList.this);
                    db.open();

                    if(db.isExist(courses_title)){
                        Snackbar.make(lv, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                    } else {
                        db.insert(courses_title, courses_content, "", "", helper_main.createDate());
                        finish();
                    }
                }
            });

        } else if ("courseList_note".equals(action)) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                    String courses_title = row2.getString(row2.getColumnIndexOrThrow("courses_title"));
                    sharedPref.edit().putString("handleTextTitle", courses_title).apply();
                    helper_main.switchToActivity(Popup_courseList.this, Activity_EditNote.class, false);
                }
            });

        } else if ("courseList_todo".equals(action)) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                    final String courses_title = row2.getString(row2.getColumnIndexOrThrow("courses_title"));
                    final String courses_content = row2.getString(row2.getColumnIndexOrThrow("courses_content"));
                    Todo_helper.newTodo(Popup_courseList.this, courses_title, courses_content, getString(R.string.courseList_content));
                }
            });
        }
    }

    private void setCoursesList() {

        //display data
        final int layoutstyle=R.layout.list_item_notes;
        int[] xml_id = new int[] {
                R.id.textView_title_notes,
                R.id.textView_des_notes,
                R.id.textView_create_notes
        };
        String[] column = new String[] {
                "courses_title",
                "courses_content",
                "courses_creation"
        };
        final Cursor row = db.fetchAllData();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(Popup_courseList.this, layoutstyle, row, column, xml_id, 0) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                ImageView iv_icon = (ImageView) v.findViewById(R.id.icon_notes);
                iv_icon.setVisibility(View.GONE);

                return v;
            }
        };

        lv.setAdapter(adapter);

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

    @Override
    public void onBackPressed() {
        helper_main.isClosed(Popup_courseList.this);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Popup_courseList.this);
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Popup_courseList.this);
    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isClosed(Popup_courseList.this);
    }
}