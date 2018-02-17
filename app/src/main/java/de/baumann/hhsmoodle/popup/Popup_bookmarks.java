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

        lv = findViewById(R.id.dialogList);

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
                ImageView iv_icon = v.findViewById(R.id.icon_notes);
                helper_main.switchIcon(Popup_bookmarks.this, bookmarks_icon, "subject_icon", iv_icon);
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