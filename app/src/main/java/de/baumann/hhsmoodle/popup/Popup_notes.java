package de.baumann.hhsmoodle.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.design.widget.Snackbar;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import de.baumann.hhsmoodle.HHS_Note;
import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.Database_Notes;

public class Popup_notes extends Activity {

    private ListView listView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(Popup_notes.this, R.xml.user_settings, false);
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Popup_notes.this);

        setContentView(R.layout.activity_popup);

        listView = (ListView)findViewById(R.id.dialogList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                @SuppressWarnings("unchecked")
                HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);

                final String title = map.get("title");
                final String cont = map.get("cont");
                final String seqnoStr = map.get("seqno");
                final String icon = map.get("icon");

                LinearLayout layout = new LinearLayout(Popup_notes.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setGravity(Gravity.CENTER_HORIZONTAL);
                layout.setPadding(50, 0, 50, 0);

                final TextView textTitle = new TextView(Popup_notes.this);
                textTitle.setText(title);
                textTitle.setTextSize(24);
                textTitle.setTypeface(null, Typeface.BOLD);
                textTitle.setPadding(5,50,0,0);
                layout.addView(textTitle);

                final TextView textContent = new TextView(Popup_notes.this);
                textContent.setText(cont);
                textContent.setTextSize(16);
                textContent.setPadding(5,25,0,0);
                layout.addView(textContent);

                ScrollView sv = new ScrollView(Popup_notes.this);
                sv.pageScroll(0);
                sv.setBackgroundColor(0);
                sv.setScrollbarFadingEnabled(true);
                sv.setVerticalFadingEdgeEnabled(false);
                sv.addView(layout);

                if (sharedPref.getBoolean ("links", false)){
                    Linkify.addLinks(textContent, Linkify.WEB_URLS);
                    Linkify.addLinks(textTitle, Linkify.WEB_URLS);

                }

                final AlertDialog.Builder dialog = new AlertDialog.Builder(Popup_notes.this)
                        .setView(sv)
                        .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.note_edit, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Popup_notes.this);
                                sharedPref.edit()
                                        .putString("handleTextTitle", title)
                                        .putString("handleTextText", cont)
                                        .putString("handleTextIcon", icon)
                                        .apply();

                                Intent intent_in = new Intent(Popup_notes.this, HHS_Note.class);
                                startActivity(intent_in);

                                try {
                                    Database_Notes db = new Database_Notes(Popup_notes.this);
                                    db.deleteNote((Integer.parseInt(seqnoStr)));
                                    db.close();
                                    setNotesList();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                finish();
                            }
                        });
                dialog.show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                @SuppressWarnings("unchecked")
                HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);

                final String seqnoStr = map.get("seqno");
                final String title = map.get("title");
                final String cont = map.get("cont");
                final String icon = map.get("icon");

                final CharSequence[] options = {
                        getString(R.string.note_edit),
                        getString(R.string.note_share),
                        getString(R.string.bookmark_createEvent),
                        getString(R.string.note_remove_note)};
                new AlertDialog.Builder(Popup_notes.this)
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals(getString(R.string.note_edit))) {
                                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Popup_notes.this);
                                    sharedPref.edit()
                                            .putString("handleTextTitle", title)
                                            .putString("handleTextText", cont)
                                            .putString("handleTextIcon", icon)
                                            .apply();

                                    Intent intent_in = new Intent(Popup_notes.this, HHS_Note.class);
                                    startActivity(intent_in);

                                    try {
                                        Database_Notes db = new Database_Notes(Popup_notes.this);
                                        db.deleteNote((Integer.parseInt(seqnoStr)));
                                        db.close();
                                        setNotesList();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    finish();
                                }

                                if (options[item].equals (getString(R.string.note_share))) {

                                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, cont);
                                    startActivity(Intent.createChooser(sharingIntent, (getString(R.string.note_share_2))));
                                    finish();
                                }

                                if (options[item].equals (getString(R.string.bookmark_createEvent))) {

                                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                                    calIntent.setType("vnd.android.cursor.item/event");
                                    calIntent.putExtra(CalendarContract.Events.TITLE, title);
                                    calIntent.putExtra(CalendarContract.Events.DESCRIPTION, cont);
                                    startActivity(calIntent);
                                    finish();
                                }

                                if (options[item].equals(getString(R.string.note_remove_note))) {

                                    try {
                                        Database_Notes db = new Database_Notes(Popup_notes.this);
                                        final int count = db.getRecordCount();
                                        db.close();

                                        if (count == 1) {
                                            Snackbar snackbar = Snackbar
                                                    .make(listView, R.string.note_remove_cannot, Snackbar.LENGTH_LONG);
                                            snackbar.show();

                                        } else {
                                            Snackbar snackbar = Snackbar
                                                    .make(listView, R.string.note_remove_confirmation, Snackbar.LENGTH_LONG)
                                                    .setAction(R.string.toast_yes, new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            try {
                                                                Database_Notes db = new Database_Notes(Popup_notes.this);
                                                                db.deleteNote(Integer.parseInt(seqnoStr));
                                                                db.close();
                                                                setNotesList();
                                                            } catch (PackageManager.NameNotFoundException e) {
                                                                e.printStackTrace();
                                                            }
                                                            finish();
                                                        }
                                                    });
                                            snackbar.show();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).show();
                return true;
            }
        });

        setNotesList();
    }

    private void setNotesList() {
        
        ArrayList<HashMap<String,String>> mapList = new ArrayList<>();

        try {
            Database_Notes db = new Database_Notes(Popup_notes.this);
            ArrayList<String[]> bookmarkList = new ArrayList<>();
            db.getBookmarks(bookmarkList);
            if (bookmarkList.size() == 0) {
                db.loadInitialData();
                db.getBookmarks(bookmarkList);
            }
            db.close();

            for (String[] strAry : bookmarkList) {
                HashMap<String, String> map = new HashMap<>();
                map.put("seqno", strAry[0]);
                map.put("title", strAry[1]);
                map.put("cont", strAry[2]);
                map.put("icon", strAry[3]);
                mapList.add(map);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(
                    Popup_notes.this,
                    mapList,
                    R.layout.list_item_note,
                    new String[] {"title", "cont", "icon"},
                    new int[] {R.id.textView_title, R.id.textView_des, R.id.pri}
            ) {
                @Override
                public View getView (final int position, View convertView, ViewGroup parent) {

                    View v = super.getView(position, convertView, parent);

                    TextView i=(TextView) v.findViewById(R.id.pri);
                    i.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {

                            @SuppressWarnings("unchecked")
                            HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);

                            final String title = map.get("title");
                            final String cont = map.get("cont");
                            final String seqnoStr = map.get("seqno");

                            final CharSequence[] options = {
                                    getString(R.string.note_priority_0),
                                    getString(R.string.note_priority_1),
                                    getString(R.string.note_priority_2),
                                    getString(R.string.note_priority_3)};
                            new AlertDialog.Builder(Popup_notes.this)
                                    .setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int item) {
                                            if (options[item].equals(getString(R.string.note_priority_0))) {

                                                try {
                                                    Database_Notes db = new Database_Notes(Popup_notes.this);
                                                    db.deleteNote((Integer.parseInt(seqnoStr)));
                                                    db.close();

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                try {

                                                    final Database_Notes db = new Database_Notes(Popup_notes.this);
                                                    db.addBookmark(title, cont, "");
                                                    db.close();
                                                    setNotesList();

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                finish();
                                            }

                                            if (options[item].equals(getString(R.string.note_priority_1))) {

                                                try {
                                                    Database_Notes db = new Database_Notes(Popup_notes.this);
                                                    db.deleteNote((Integer.parseInt(seqnoStr)));
                                                    db.close();

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                try {

                                                    final Database_Notes db = new Database_Notes(Popup_notes.this);
                                                    db.addBookmark(title, cont, "!");
                                                    db.close();
                                                    setNotesList();

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                finish();
                                            }

                                            if (options[item].equals(getString(R.string.note_priority_2))) {

                                                try {
                                                    Database_Notes db = new Database_Notes(Popup_notes.this);
                                                    db.deleteNote((Integer.parseInt(seqnoStr)));
                                                    db.close();

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                try {

                                                    final Database_Notes db = new Database_Notes(Popup_notes.this);
                                                    db.addBookmark(title, cont, "!!");
                                                    db.close();
                                                    setNotesList();

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                finish();
                                            }

                                            if (options[item].equals(getString(R.string.note_priority_3))) {

                                                try {
                                                    Database_Notes db = new Database_Notes(Popup_notes.this);
                                                    db.deleteNote((Integer.parseInt(seqnoStr)));
                                                    db.close();

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                try {

                                                    final Database_Notes db = new Database_Notes(Popup_notes.this);
                                                    db.addBookmark(title, cont, "!!!");
                                                    db.close();
                                                    setNotesList();

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                finish();
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
}
