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
import de.baumann.hhsmoodle.data_random.Random_helper;
import de.baumann.hhsmoodle.data_schedule.Schedule_DbAdapter;
import de.baumann.hhsmoodle.data_subjects.Subject_DbAdapter;
import de.baumann.hhsmoodle.data_todo.Todo_helper;
import de.baumann.hhsmoodle.helper.helper_main;

public class Popup_subjects extends Activity {

    //calling variables
    private Subject_DbAdapter db;
    private ListView lv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_popup);
        lv = (ListView) findViewById(R.id.dialogList);

        //calling Notes_DbAdapter
        db = new Subject_DbAdapter(Popup_subjects.this);
        db.open();

        onNewIntent(getIntent());
        setSubjectsList();
    }

    protected void onNewIntent(final Intent intent) {

        String action = intent.getAction();
        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if ("subjectList_random".equals(action)) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                    String subject_title = row2.getString(row2.getColumnIndexOrThrow("subject_title"));
                    String subject_content = row2.getString(row2.getColumnIndexOrThrow("subject_content"));
                    String subject_icon = row2.getString(row2.getColumnIndexOrThrow("subject_icon"));
                    Random_helper.newRandom(Popup_subjects.this, subject_title, subject_content,subject_icon, getString(R.string.note_content), true);
                }
            });

        } else if ("subjectList_note".equals(action)) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                    String subject_title = row2.getString(row2.getColumnIndexOrThrow("subject_title"));
                    String subject_content = row2.getString(row2.getColumnIndexOrThrow("subject_content"));
                    String subject_icon = row2.getString(row2.getColumnIndexOrThrow("subject_icon"));
                    Notes_helper.newNote(Popup_subjects.this, subject_title, subject_content, subject_icon, getString(R.string.note_content), true);
                }
            });

        } else if ("subjectList_todo".equals(action)) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                    String subject_title = row2.getString(row2.getColumnIndexOrThrow("subject_title"));
                    String subject_content = row2.getString(row2.getColumnIndexOrThrow("subject_content"));
                    String subject_icon = row2.getString(row2.getColumnIndexOrThrow("subject_icon"));
                    Todo_helper.newTodo(Popup_subjects.this, subject_title, subject_content, subject_icon, getString(R.string.note_content), true);
                }
            });
        } else if ("subjectList_count".equals(action)) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    sharedPref.edit().putString("count_content", "").apply();
                    Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                    String subject_title = row2.getString(row2.getColumnIndexOrThrow("subject_title"));
                    String subject_content = row2.getString(row2.getColumnIndexOrThrow("subject_content"));
                    String subject_icon = row2.getString(row2.getColumnIndexOrThrow("subject_icon"));
                    Count_helper.newCount(Popup_subjects.this, subject_title, subject_content, subject_icon, getString(R.string.note_content), true);
                }
            });
        } else if ("search_bySubject".equals(action)) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                    String courses_title = row2.getString(row2.getColumnIndexOrThrow("subject_title"));
                    sharedPref.edit().putString("search_bySubject", courses_title).apply();
                    finish();
                }
            });
        } else {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {

                    Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                    String subject_title = row2.getString(row2.getColumnIndexOrThrow("subject_title"));
                    String subject_content = row2.getString(row2.getColumnIndexOrThrow("subject_content"));
                    String subject_icon = row2.getString(row2.getColumnIndexOrThrow("subject_icon"));
                    String subject_attachment = row2.getString(row2.getColumnIndexOrThrow("subject_attachment"));

                    String handleSubjectCreation = sharedPref.getString("handleSubjectCreation", "");
                    String handleSubject_id = sharedPref.getString("handleSubject_id", "");
                    String handle_id = sharedPref.getString("handle_id", "");

                    Schedule_DbAdapter db = new Schedule_DbAdapter(Popup_subjects.this);
                    db.open();
                    db.update(Integer.parseInt(handle_id), subject_title, subject_content, subject_icon, subject_attachment, handleSubjectCreation, handleSubject_id);

                    sharedPref.edit()
                            .putString("handleSubjectCreation", "")
                            .putString("handleSubject_id", "")
                            .putString("handle_id", "")
                            .apply();
                    finish();
                }
            });
        }
    }

    private void setSubjectsList() {

        //display data
        final int layoutstyle=R.layout.list_item_schedule;
        int[] xml_id = new int[] {
                R.id.textView_title_notes,
                R.id.textView_des_notes,
                R.id.att_notes
        };
        String[] column = new String[] {
                "subject_title",
                "subject_content",
                "subject_attachment"
        };
        final Cursor row = db.fetchAllData();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(Popup_subjects.this, layoutstyle, row, column, xml_id, 0) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                String subject_icon = row2.getString(row2.getColumnIndexOrThrow("subject_icon"));

                View v = super.getView(position, convertView, parent);
                ImageView iv_icon = (ImageView) v.findViewById(R.id.icon_notes);
                helper_main.switchIcon(Popup_subjects.this, subject_icon, "subject_icon", iv_icon);

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