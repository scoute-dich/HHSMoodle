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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.data_count.Count_helper;
import de.baumann.hhsmoodle.data_notes.Notes_helper;
import de.baumann.hhsmoodle.data_courses.Courses_DbAdapter;
import de.baumann.hhsmoodle.data_random.Random_helper;
import de.baumann.hhsmoodle.data_todo.Todo_helper;
import de.baumann.hhsmoodle.helper.helper_main;

public class Popup_courseList extends Activity {

    private ListView lv = null;
    private Courses_DbAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_popup);

        lv = findViewById(R.id.dialogList);

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
                    String courses_title = row2.getString(row2.getColumnIndexOrThrow("courses_title"));
                    String courses_content = row2.getString(row2.getColumnIndexOrThrow("courses_content"));
                    String courses_icon = row2.getString(row2.getColumnIndexOrThrow("courses_icon"));
                    Random_helper.newRandom(Popup_courseList.this, courses_title, courses_content,courses_icon, getString(R.string.courseList_content), true);
                }
            });

        } else if ("courseList_note".equals(action)) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                    String courses_title = row2.getString(row2.getColumnIndexOrThrow("courses_title"));
                    String courses_content = row2.getString(row2.getColumnIndexOrThrow("courses_content"));
                    String courses_icon = row2.getString(row2.getColumnIndexOrThrow("courses_icon"));
                    Notes_helper.newNote(Popup_courseList.this, courses_title, courses_content, courses_icon, getString(R.string.courseList_content), true);
                }
            });

        } else if ("courseList_todo".equals(action)) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                    String courses_title = row2.getString(row2.getColumnIndexOrThrow("courses_title"));
                    String courses_content = row2.getString(row2.getColumnIndexOrThrow("courses_content"));
                    String courses_icon = row2.getString(row2.getColumnIndexOrThrow("courses_icon"));
                    Todo_helper.newTodo(Popup_courseList.this, courses_title, courses_content, courses_icon, getString(R.string.courseList_content), true);
                }
            });
        } else if ("courseList_count".equals(action)) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    sharedPref.edit().putString("count_content", "").apply();
                    Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                    String courses_title = row2.getString(row2.getColumnIndexOrThrow("courses_title"));
                    String courses_content = row2.getString(row2.getColumnIndexOrThrow("courses_content"));
                    String courses_icon = row2.getString(row2.getColumnIndexOrThrow("courses_icon"));
                    Count_helper.newCount(Popup_courseList.this, courses_title, courses_content, courses_icon, getString(R.string.courseList_content), true);
                }
            });
        } else if ("courseList_subject".equals(action)) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                    String courses_title = row2.getString(row2.getColumnIndexOrThrow("courses_title"));
                    String courses_icon = row2.getString(row2.getColumnIndexOrThrow("courses_icon"));
                    sharedPref.edit().putString("subject_title", courses_title).apply();
                    sharedPref.edit().putString("subject_icon", courses_icon).apply();
                    finish();
                }
            });
        } else if ("search_byCourse".equals(action)) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                    String courses_title = row2.getString(row2.getColumnIndexOrThrow("courses_title"));
                    sharedPref.edit().putString("search_byCourse", courses_title).apply();
                    finish();
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
                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                String courses_icon = row2.getString(row2.getColumnIndexOrThrow("courses_icon"));
                View v = super.getView(position, convertView, parent);
                ImageView iv_icon = v.findViewById(R.id.icon_notes);
                helper_main.switchIcon(Popup_courseList.this, courses_icon,"courses_icon", iv_icon);
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
}