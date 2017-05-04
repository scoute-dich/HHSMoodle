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

import de.baumann.hhsmoodle.data_bookmarks.Bookmarks_DbAdapter;
import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.helper_main;

public class Popup_bookmarks extends Activity {

    //calling variables
    private Bookmarks_DbAdapter db;
    private ListView lv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_popup);

        lv = (ListView) findViewById(R.id.dialogList);

        //calling Notes_DbAdapter
        db = new Bookmarks_DbAdapter(Popup_bookmarks.this);
        db.open();

        setBookmarksList();
    }

    private void setBookmarksList() {

        //display data
        final int layoutstyle=R.layout.list_item_notes;
        int[] xml_id = new int[] {
                R.id.textView_title_notes,
                R.id.textView_des_notes,
        };
        String[] column = new String[] {
                "bookmarks_title",
                "bookmarks_content"
        };
        final Cursor row = db.fetchAllData(Popup_bookmarks.this);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(Popup_bookmarks.this, layoutstyle, row, column, xml_id, 0) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String bookmarks_icon = row2.getString(row2.getColumnIndexOrThrow("bookmarks_icon"));

                View v = super.getView(position, convertView, parent);
                ImageView iv_icon = (ImageView) v.findViewById(R.id.icon_notes);

                switch (bookmarks_icon) {
                    case "01":
                        iv_icon.setImageResource(R.drawable.circle_red);
                        break;
                    case "02":
                        iv_icon.setImageResource(R.drawable.circle_yellow);
                        break;
                    case "03":
                        iv_icon.setImageResource(R.drawable.circle_green);
                        break;
                    case "04":
                        iv_icon.setImageResource(R.drawable.ic_school_grey600_48dp);
                        break;
                    case "05":
                        iv_icon.setImageResource(R.drawable.ic_view_dashboard_grey600_48dp);
                        break;
                    case "06":
                        iv_icon.setImageResource(R.drawable.ic_face_profile_grey600_48dp);
                        break;
                    case "07":
                        iv_icon.setImageResource(R.drawable.ic_calendar_grey600_48dp);
                        break;
                    case "08":
                        iv_icon.setImageResource(R.drawable.ic_chart_areaspline_grey600_48dp);
                        break;
                    case "09":
                        iv_icon.setImageResource(R.drawable.ic_bell_grey600_48dp);
                        break;
                    case "10":
                        iv_icon.setImageResource(R.drawable.ic_settings_grey600_48dp);
                        break;
                    case "11":
                        iv_icon.setImageResource(R.drawable.ic_web_grey600_48dp);
                        break;
                    case "12":
                        iv_icon.setImageResource(R.drawable.ic_magnify_grey600_48dp);
                        break;
                    case "13":
                        iv_icon.setImageResource(R.drawable.ic_pencil_grey600_48dp);
                        break;
                    case "14":
                        iv_icon.setImageResource(R.drawable.ic_check_grey600_48dp);
                        break;
                    case "15":
                        iv_icon.setImageResource(R.drawable.ic_clock_grey600_48dp);
                        break;
                    case "16":
                        iv_icon.setImageResource(R.drawable.ic_bookmark_grey600_48dp);
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
                final String bookmarks_content = row2.getString(row2.getColumnIndexOrThrow("bookmarks_content"));

                PreferenceManager.setDefaultValues(Popup_bookmarks.this, R.xml.user_settings, false);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Popup_bookmarks.this);
                sharedPref.edit().putString("load_next", "true").apply();
                sharedPref.edit().putString("loadURL", bookmarks_content).apply();

                Popup_bookmarks.this.finish();
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