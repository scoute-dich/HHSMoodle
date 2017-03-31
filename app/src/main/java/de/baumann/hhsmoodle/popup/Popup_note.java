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
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.File;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.activities.Activity_EditNote;
import de.baumann.hhsmoodle.activities.Activity_password;
import de.baumann.hhsmoodle.data_notes.Notes_DbAdapter;
import de.baumann.hhsmoodle.data_todo.Todo_DbAdapter;
import de.baumann.hhsmoodle.helper.class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_main;

public class Popup_note extends Activity {

    //calling variables
    private Notes_DbAdapter db;
    private ListView lv = null;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        class_SecurePreferences sharedPrefSec = new class_SecurePreferences(Popup_note.this, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);
        String pw = sharedPrefSec.getString("protect_PW");

        PreferenceManager.setDefaultValues(Popup_note.this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(Popup_note.this);

        if (pw != null  && pw.length() > 0) {
            if (sharedPref.getBoolean("isOpened", true)) {
                helper_main.switchToActivity(Popup_note.this, Activity_password.class, false);
            }
        }

        setContentView(R.layout.activity_popup);
        lv = (ListView) findViewById(R.id.dialogList);

        //calling Notes_DbAdapter
        db = new Notes_DbAdapter(Popup_note.this);
        db.open();

        setNotesList();
    }

    private void setNotesList() {

        //display data
        final int layoutstyle=R.layout.list_item_notes;
        int[] xml_id = new int[] {
                R.id.textView_title_notes,
                R.id.textView_des_notes,
                R.id.textView_create_notes
        };
        String[] column = new String[] {
                "note_title",
                "note_content",
                "note_creation"
        };
        final String search = sharedPref.getString("filter_note_subject", "");
        final Cursor row = db.fetchDataByFilter(search, "note_title");
        
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(Popup_note.this, layoutstyle,row,column, xml_id, 0) {
            @Override
            public View getView (final int position, View convertView, ViewGroup parent) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String note_title = row2.getString(row2.getColumnIndexOrThrow("note_title"));
                final String note_content = row2.getString(row2.getColumnIndexOrThrow("note_content"));
                final String note_icon = row2.getString(row2.getColumnIndexOrThrow("note_icon"));
                final String note_attachment = row2.getString(row2.getColumnIndexOrThrow("note_attachment"));
                final String note_creation = row2.getString(row2.getColumnIndexOrThrow("note_creation"));

                View v = super.getView(position, convertView, parent);
                ImageView iv_icon = (ImageView) v.findViewById(R.id.icon_notes);
                ImageView iv_attachment = (ImageView) v.findViewById(R.id.att_notes);

                switch (note_icon) {
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

                switch (note_attachment) {
                    case "":
                        iv_attachment.setVisibility(View.GONE);
                        break;
                    default:
                        iv_attachment.setVisibility(View.VISIBLE);
                        iv_attachment.setImageResource(R.drawable.ic_attachment);
                        break;
                }

                File file = new File(note_attachment);
                if (!file.exists()) {
                    iv_attachment.setVisibility(View.GONE);
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
                                Popup_note.this,
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

                        new AlertDialog.Builder(Popup_note.this)
                                .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }
                                })
                                .setAdapter(adapter, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int item) {
                                        if (item == 0) {
                                            db.update(Integer.parseInt(_id),note_title, note_content, "3", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 1) {
                                            db.update(Integer.parseInt(_id),note_title, note_content, "2", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 2) {
                                            db.update(Integer.parseInt(_id),note_title, note_content, "1", note_attachment, note_creation);
                                            setNotesList();
                                        }
                                    }
                                }).show();
                    }
                });
                iv_attachment.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        helper_main.openAtt(Popup_note.this, lv, note_attachment);
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
                final String note_title = row2.getString(row2.getColumnIndexOrThrow("note_title"));
                final String note_content = row2.getString(row2.getColumnIndexOrThrow("note_content"));
                final String note_icon = row2.getString(row2.getColumnIndexOrThrow("note_icon"));
                final String note_attachment = row2.getString(row2.getColumnIndexOrThrow("note_attachment"));
                final String note_creation = row2.getString(row2.getColumnIndexOrThrow("note_creation"));

                final Button attachment2;
                final TextView textInput;

                LayoutInflater inflater = Popup_note.this.getLayoutInflater();

                final ViewGroup nullParent = null;
                final View dialogView = inflater.inflate(R.layout.dialog_note_show, nullParent);

                final String attName = note_attachment.substring(note_attachment.lastIndexOf("/")+1);
                final String att = getString(R.string.app_att) + ": " + attName;

                attachment2 = (Button) dialogView.findViewById(R.id.button_att);
                if (attName.equals("")) {
                    attachment2.setVisibility(View.GONE);
                } else {
                    attachment2.setText(att);
                }
                File file2 = new File(note_attachment);
                if (!file2.exists()) {
                    attachment2.setVisibility(View.GONE);
                }

                textInput = (TextView) dialogView.findViewById(R.id.note_text_input);
                textInput.setText(note_content);
                Linkify.addLinks(textInput, Linkify.WEB_URLS);

                attachment2.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        helper_main.openAtt(Popup_note.this, lv, note_attachment);
                    }
                });

                final ImageView be = (ImageView) dialogView.findViewById(R.id.imageButtonPri);

                switch (note_icon) {
                    case "3":
                        be.setImageResource(R.drawable.circle_green);
                        break;
                    case "2":
                        be.setImageResource(R.drawable.circle_yellow);
                        break;
                    case "1":
                        be.setImageResource(R.drawable.circle_red);
                        break;
                }

                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Popup_note.this)
                        .setTitle(note_title)
                        .setView(dialogView)
                        .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton(R.string.note_edit, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                sharedPref.edit()
                                        .putString("handleTextTitle", note_title)
                                        .putString("handleTextText", note_content)
                                        .putString("handleTextIcon", note_icon)
                                        .putString("handleTextSeqno", _id)
                                        .putString("handleTextAttachment", note_attachment)
                                        .putString("handleTextCreate", note_creation)
                                        .apply();
                                helper_main.switchToActivity(Popup_note.this, Activity_EditNote.class, false);
                            }
                        });
                dialog.show();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String note_title = row2.getString(row2.getColumnIndexOrThrow("note_title"));
                final String note_content = row2.getString(row2.getColumnIndexOrThrow("note_content"));
                final String note_icon = row2.getString(row2.getColumnIndexOrThrow("note_icon"));
                final String note_attachment = row2.getString(row2.getColumnIndexOrThrow("note_attachment"));
                final String note_creation = row2.getString(row2.getColumnIndexOrThrow("note_creation"));

                final CharSequence[] options = {
                        getString(R.string.note_edit),
                        getString(R.string.note_share),
                        getString(R.string.todo_menu),
                        getString(R.string.bookmark_createEvent),
                        getString(R.string.note_remove_note)};
                new AlertDialog.Builder(Popup_note.this)
                        .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals(getString(R.string.note_edit))) {
                                    sharedPref.edit()
                                            .putString("handleTextTitle", note_title)
                                            .putString("handleTextText", note_content)
                                            .putString("handleTextIcon", note_icon)
                                            .putString("handleTextSeqno", _id)
                                            .putString("handleTextAttachment", note_attachment)
                                            .putString("handleTextCreate", note_creation)
                                            .apply();
                                    helper_main.switchToActivity(Popup_note.this, Activity_EditNote.class, false);
                                }

                                if (options[item].equals (getString(R.string.note_share))) {
                                    File attachment = new File(note_attachment);
                                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, note_title);
                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, note_content);

                                    if (attachment.exists()) {
                                        Uri bmpUri = Uri.fromFile(attachment);
                                        sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                    }

                                    startActivity(Intent.createChooser(sharingIntent, (getString(R.string.note_share_2))));
                                }

                                if (options[item].equals (getString(R.string.todo_menu))) {

                                    Todo_DbAdapter db = new Todo_DbAdapter(Popup_note.this);
                                    db.open();
                                    if(db.isExist(note_title)){
                                        Snackbar.make(lv, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                                    }else{
                                        db.insert(note_title, note_content, "3", "true", helper_main.createDate());
                                        ViewPager viewPager = (ViewPager) Popup_note.this.findViewById(R.id.viewpager);
                                        viewPager.setCurrentItem(2);
                                        Popup_note.this.setTitle(R.string.todo_title);
                                        dialog.dismiss();
                                    }
                                }

                                if (options[item].equals (getString(R.string.bookmark_createEvent))) {
                                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                                    calIntent.setType("vnd.android.cursor.item/event");
                                    calIntent.putExtra(CalendarContract.Events.TITLE, note_title);
                                    calIntent.putExtra(CalendarContract.Events.DESCRIPTION, note_content);
                                    startActivity(calIntent);
                                }

                                if (options[item].equals(getString(R.string.note_remove_note))) {
                                    Snackbar snackbar = Snackbar
                                            .make(lv, R.string.note_remove_confirmation, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.toast_yes, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    db.delete(Integer.parseInt(_id));
                                                    setNotesList();
                                                }
                                            });
                                    snackbar.show();
                                }
                            }
                        }).show();

                return true;
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
        helper_main.isClosed(Popup_note.this);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Popup_note.this);
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Popup_note.this);
    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isClosed(Popup_note.this);
    }
}