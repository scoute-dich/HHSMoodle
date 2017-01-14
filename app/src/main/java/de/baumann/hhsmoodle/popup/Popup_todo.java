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
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.activities.Activity_password;
import de.baumann.hhsmoodle.databases.Database_Todo;
import de.baumann.hhsmoodle.helper.class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_main;

public class Popup_todo extends Activity {

    private ListView listView = null;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        class_SecurePreferences sharedPrefSec = new class_SecurePreferences(Popup_todo.this, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);
        String pw = sharedPrefSec.getString("protect_PW");

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (pw != null  && pw.length() > 0) {
            if (sharedPref.getBoolean("isOpened", true)) {
                helper_main.switchToActivity(Popup_todo.this, Activity_password.class, "", false);
            }
        }

        setContentView(R.layout.activity_popup);

        listView = (ListView)findViewById(R.id.dialogList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                @SuppressWarnings("unchecked")
                HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);
                final String title = map.get("title");
                String att = getString(R.string.todo_title) + ": " + title;
                sharedPref.edit().putString("handleTextAttachment", att).apply();
                finish();
            }
        });
        
        setNotesList();
    }

    private void setNotesList() {

        ArrayList<HashMap<String,String>> mapList = new ArrayList<>();

        try {
            Database_Todo db = new Database_Todo(Popup_todo.this);
            ArrayList<String[]> bookmarkList = new ArrayList<>();
            db.getBookmarks(bookmarkList, Popup_todo.this);
            if (bookmarkList.size() == 0) {
                db.loadInitialData();
                db.getBookmarks(bookmarkList, Popup_todo.this);
            }
            db.close();

            for (String[] strAry : bookmarkList) {
                HashMap<String, String> map = new HashMap<>();
                map.put("seqno", strAry[0]);
                map.put("title", strAry[1]);
                map.put("cont", strAry[2]);
                map.put("icon", strAry[3]);
                map.put("attachment", strAry[4]);
                map.put("createDate", strAry[5]);
                mapList.add(map);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(
                    Popup_todo.this,
                    mapList,
                    R.layout.list_item_notes,
                    new String[] {"title", "cont", "createDate"},
                    new int[] {R.id.textView_title_notes, R.id.textView_des_notes, R.id.textView_create_notes}
            ) {
                @Override
                public View getView (final int position, View convertView, ViewGroup parent) {

                    @SuppressWarnings("unchecked")
                    HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);
                    final String title = map.get("title");
                    final String cont = map.get("cont");
                    final String seqnoStr = map.get("seqno");
                    final String icon = map.get("icon");
                    final String attachment = map.get("attachment");
                    final String create = map.get("createDate");

                    View v = super.getView(position, convertView, parent);
                    ImageView i=(ImageView) v.findViewById(R.id.icon_notes);
                    ImageView i2=(ImageView) v.findViewById(R.id.att_notes);

                    switch (icon) {
                        case "1":
                            i.setImageResource(R.drawable.circle_green);
                            break;
                        case "2":
                            i.setImageResource(R.drawable.circle_yellow);
                            break;
                        case "3":
                            i.setImageResource(R.drawable.circle_red);
                            break;
                    }
                    switch (attachment) {
                        case "":
                            i2.setVisibility(View.GONE);
                            break;
                        default:
                            i2.setVisibility(View.VISIBLE);
                            i2.setImageResource(R.drawable.ic_attachment);
                            break;
                    }

                    File file = new File(attachment);
                    if (!file.exists()) {
                        i2.setVisibility(View.GONE);
                    }

                    i.setOnClickListener(new View.OnClickListener() {

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
                                                try {
                                                    final Database_Todo db = new Database_Todo(Popup_todo.this);
                                                    db.deleteNote((Integer.parseInt(seqnoStr)));
                                                    db.addBookmark(title, cont, "1", attachment, create);
                                                    db.close();
                                                    setNotesList();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            } else if (item == 1) {
                                                try {
                                                    final Database_Todo db = new Database_Todo(Popup_todo.this);
                                                    db.deleteNote((Integer.parseInt(seqnoStr)));
                                                    db.addBookmark(title, cont, "2",attachment, create);
                                                    db.close();
                                                    setNotesList();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            } else if (item == 2) {
                                                try {
                                                    final Database_Todo db = new Database_Todo(Popup_todo.this);
                                                    db.deleteNote((Integer.parseInt(seqnoStr)));
                                                    db.addBookmark(title, cont, "3", attachment, create);
                                                    db.close();
                                                    setNotesList();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }).show();
                        }
                    });
                    return v;
                }
            };

            listView.setAdapter(simpleAdapter);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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