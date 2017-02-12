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
import de.baumann.hhsmoodle.activities.Activity_password;
import de.baumann.hhsmoodle.data_schedule.Schedule_DbAdapter;
import de.baumann.hhsmoodle.data_subjects.Subject_DbAdapter;
import de.baumann.hhsmoodle.helper.class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_main;

public class Popup_subjects extends Activity {

    //calling variables
    private Subject_DbAdapter db;
    private ListView lv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        class_SecurePreferences sharedPrefSec = new class_SecurePreferences(Popup_subjects.this, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);
        String pw = sharedPrefSec.getString("protect_PW");

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (pw != null  && pw.length() > 0) {
            if (sharedPref.getBoolean("isOpened", true)) {
                helper_main.switchToActivity(Popup_subjects.this, Activity_password.class, false);
            }
        }

        setContentView(R.layout.activity_popup);
        lv = (ListView) findViewById(R.id.dialogList);

        //calling Notes_DbAdapter
        db = new Subject_DbAdapter(Popup_subjects.this);
        db.open();

        setSubjectsList();

        if (lv.getAdapter().getCount() == 0) {
            Snackbar.make(lv, R.string.toast_noEntry, Snackbar.LENGTH_INDEFINITE).show();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, 1000);
        }
    }

    private void setSubjectsList() {

        PreferenceManager.setDefaultValues(Popup_subjects.this, R.xml.user_settings, false);
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Popup_subjects.this);

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
                final String subject_icon = row2.getString(row2.getColumnIndexOrThrow("subject_icon"));

                View v = super.getView(position, convertView, parent);
                ImageView iv_icon = (ImageView) v.findViewById(R.id.icon_notes);

                switch (subject_icon) {
                    case "1":iv_icon.setImageResource(R.drawable.circle_red);break;
                    case "2":iv_icon.setImageResource(R.drawable.circle_pink);break;
                    case "3":iv_icon.setImageResource(R.drawable.circle_purple);break;
                    case "4":iv_icon.setImageResource(R.drawable.circle_blue);break;
                    case "5":iv_icon.setImageResource(R.drawable.circle_teal);break;
                    case "6":iv_icon.setImageResource(R.drawable.circle_green);break;
                    case "7":iv_icon.setImageResource(R.drawable.circle_lime);break;
                    case "8":iv_icon.setImageResource(R.drawable.circle_yellow);break;
                    case "9":iv_icon.setImageResource(R.drawable.circle_orange);break;
                    case "10":iv_icon.setImageResource(R.drawable.circle_brown);break;
                    case "11":iv_icon.setImageResource(R.drawable.circle_grey);break;
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
                final String subject_title = row2.getString(row2.getColumnIndexOrThrow("subject_title"));
                final String subject_content = row2.getString(row2.getColumnIndexOrThrow("subject_content"));
                final String subject_icon = row2.getString(row2.getColumnIndexOrThrow("subject_icon"));
                final String subject_attachment = row2.getString(row2.getColumnIndexOrThrow("subject_attachment"));

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

    @Override
    public void onBackPressed() {
        helper_main.isClosed(Popup_subjects.this);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Popup_subjects.this);
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Popup_subjects.this);
    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isClosed(Popup_subjects.this);
    }
}