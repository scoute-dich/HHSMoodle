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

package de.baumann.hhsmoodle.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import de.baumann.hhsmoodle.HHS_MainScreen;
import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.Database_Random;
import de.baumann.hhsmoodle.helper.Popup_courseList;
import de.baumann.hhsmoodle.helper.class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.helper.helper_notes;

public class Activity_dice extends AppCompatActivity {

    private TextView textFile;
    private ListView listView = null;
    private ScrollView scrollView;
    private SharedPreferences sharedPref;
    private FloatingActionButton fab_add;
    private FloatingActionButton fab;
    private class_SecurePreferences sharedPrefSec;

    private int number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefSec = new class_SecurePreferences(Activity_dice.this, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);
        String pw = sharedPrefSec.getString("protect_PW");

        if (pw != null  && pw.length() > 0) {
            if (sharedPref.getBoolean("isOpened", true)) {
                helper_main.switchToActivity(Activity_dice.this, Activity_password.class, "", false);
            }
        }

        setContentView(R.layout.activity_dice);
        setTitle(R.string.number_title);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(toolbar != null) {
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String startURL = sharedPref.getString("favoriteURL", "https://moodle.huebsch.ka.schule-bw.de/moodle/");
                    final String startType = sharedPref.getString("startType", "1");

                    helper_main.resetStartTab(Activity_dice.this);

                    if (startType.equals("2")) {
                        helper_main.isOpened(Activity_dice.this);
                        helper_main.switchToActivity(Activity_dice.this, Activity_grades.class, startURL, false);
                    } else if (startType.equals("1")){
                        helper_main.isOpened(Activity_dice.this);
                        helper_main.switchToActivity(Activity_dice.this, HHS_MainScreen.class, "", true);
                    }
                }
            });

            if (sharedPref.getBoolean ("longPress", false)){
                toolbar.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        helper_main.resetStartTab(Activity_dice.this);
                        helper_main.isClosed(Activity_dice.this);
                        finishAffinity();
                        return true;
                    }
                });
            }
        }

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        textFile = (TextView) findViewById(R.id.textFile);
        listView = (ListView)findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                @SuppressWarnings("unchecked")
                HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);
                final String title = map.get("title");
                final String text = map.get("text");

                listView.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                textFile.setText(String.valueOf(text));
                fab_add.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                setTitle(title);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                @SuppressWarnings("unchecked")
                HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);
                final String seqnoStr = map.get("seqno");
                final String title = map.get("title");
                final String text = map.get("text");

                final CharSequence[] options = {
                        getString(R.string.bookmark_edit_title),
                        getString(R.string.number_edit_entry),
                        getString(R.string.bookmark_remove_bookmark)};
                new android.app.AlertDialog.Builder(Activity_dice.this)
                        .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals(getString(R.string.bookmark_edit_title))) {
                                    try {

                                        final Database_Random db = new Database_Random(Activity_dice.this);

                                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Activity_dice.this);
                                        View dialogView = View.inflate(Activity_dice.this, R.layout.dialog_edit_title, null);

                                        final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                                        edit_title.setHint(R.string.bookmark_edit_title);
                                        edit_title.setText(title);

                                        builder.setView(dialogView);
                                        builder.setTitle(R.string.bookmark_edit_title);
                                        builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {

                                                String inputTag = edit_title.getText().toString().trim();
                                                db.deleteBookmark((Integer.parseInt(seqnoStr)));
                                                db.addBookmark(inputTag, text);
                                                db.close();
                                                setBookmarkList();
                                                Snackbar.make(listView, R.string.bookmark_added, Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                                        builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                dialog.cancel();
                                            }
                                        });

                                        final android.app.AlertDialog dialog2 = builder.create();
                                        // Display the custom alert dialog on interface
                                        dialog2.show();

                                        new Handler().postDelayed(new Runnable() {
                                            public void run() {
                                                helper_main.showKeyboard(Activity_dice.this,edit_title);
                                            }
                                        }, 200);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (options[item].equals(getString(R.string.number_edit_entry))) {

                                    try {

                                        final Database_Random db = new Database_Random(Activity_dice.this);

                                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Activity_dice.this);
                                        View dialogView = View.inflate(Activity_dice.this, R.layout.dialog_edit_text, null);

                                        final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                                        edit_title.setHint(R.string.bookmark_edit_title);
                                        edit_title.setText(text);

                                        builder.setView(dialogView);
                                        builder.setTitle(R.string.number_edit_entry);
                                        builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {

                                                String inputTag = edit_title.getText().toString().trim();
                                                sharedPrefSec.put(title + "text", inputTag);
                                                db.deleteBookmark((Integer.parseInt(seqnoStr)));
                                                db.addBookmark(title, sharedPrefSec.getString(title + "text"));
                                                db.close();
                                                setBookmarkList();
                                                Snackbar.make(listView, R.string.bookmark_added, Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                                        builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                dialog.cancel();
                                            }
                                        });

                                        final android.app.AlertDialog dialog2 = builder.create();
                                        // Display the custom alert dialog on interface
                                        dialog2.show();

                                        new Handler().postDelayed(new Runnable() {
                                            public void run() {
                                                helper_main.showKeyboard(Activity_dice.this,edit_title);
                                            }
                                        }, 200);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (options[item].equals(getString(R.string.bookmark_remove_bookmark))) {
                                    try {
                                        Database_Random db = new Database_Random(Activity_dice.this);
                                        final int count = db.getRecordCount();
                                        db.close();

                                        if (count == 1) {
                                            Snackbar snackbar = Snackbar
                                                    .make(listView, R.string.bookmark_remove_cannot, Snackbar.LENGTH_LONG);
                                            snackbar.show();

                                        } else {
                                            Snackbar snackbar = Snackbar
                                                    .make(listView, R.string.bookmark_remove_confirmation, Snackbar.LENGTH_LONG)
                                                    .setAction(R.string.toast_yes, new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            try {
                                                                Database_Random db = new Database_Random(Activity_dice.this);
                                                                db.deleteBookmark(Integer.parseInt(seqnoStr));
                                                                db.close();
                                                                setBookmarkList();
                                                            } catch (PackageManager.NameNotFoundException e) {
                                                                e.printStackTrace();
                                                            }
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

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper_main.isOpened(Activity_dice.this);
                Intent mainIntent = new Intent(Activity_dice.this, Popup_courseList.class);
                mainIntent.setAction("courseList_random");
                startActivity(mainIntent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String origString = textFile.getText().toString();

                try {
                    Random rand = new Random();
                    final int n = rand.nextInt(textFile.getLineCount());

                    int startPos = textFile.getLayout().getLineStart(n);
                    int endPos = textFile.getLayout().getLineEnd(n);

                    String theLine = textFile.getText().toString().substring(startPos, endPos);
                    String theLine2 = theLine.substring(0, theLine.length()-1);
                    String text = getString(R.string.number_chosen) + " " + String.valueOf(n + 1) + " " + theLine2;

                    Spannable highlight = new SpannableString(origString);
                    highlight.setSpan(new ForegroundColorSpan(ContextCompat.getColor(Activity_dice.this, R.color.colorAccent)), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    textFile.setText(highlight);
                    Layout layout = textFile.getLayout();
                    scrollView.scrollTo(0, layout.getLineTop(textFile.getTop() + layout.getLineForOffset(startPos)));
                    Snackbar.make(textFile, text, Snackbar.LENGTH_LONG).show();
                } catch(NumberFormatException nfe) {
                    Snackbar.make(textFile, R.string.number_error, Snackbar.LENGTH_LONG).show();
                    nfe.printStackTrace();
                }
            }
        });
        setBookmarkList();
    }

    private void setBookmarkList() {

        ArrayList<HashMap<String,String>> mapList = new ArrayList<>();

        try {
            Database_Random db = new Database_Random(Activity_dice.this);
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
                map.put("text", strAry[2]);
                mapList.add(map);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(
                    Activity_dice.this,
                    mapList,
                    R.layout.list_item,
                    new String[] {"title", "text"},
                    new int[] {R.id.textView_title_notes, R.id.textView_des_notes}
            );

            listView.setAdapter(simpleAdapter);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (scrollView.getVisibility() == View.VISIBLE) {
            listView.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            fab_add.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
            setTitle(R.string.number_title);
        } else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Activity_dice.this);
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        setBookmarkList();
        helper_main.isOpened(Activity_dice.this);
    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isClosed(Activity_dice.this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (sharedPref.getBoolean ("help", false)){
            menu.getItem(3).setVisible(false); // here pass the index of save menu item
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_not) {

            sharedPref.edit()
                    .putString("handleTextTitle", "")
                    .putString("handleTextText", "")
                    .putString("handleTextIcon", "")
                    .putString("handleTextAttachment", "")
                    .putString("handleTextCreate", helper_main.createDate())
                    .putString("handleTextSeqno", "")
                    .apply();
            helper_notes.editNote(Activity_dice.this);
        }

        if (id == R.id.action_folder) {
            String startDir = Environment.getExternalStorageDirectory() + "/HHS_Moodle/";
            helper_main.openFilePicker(Activity_dice.this, textFile, startDir);
        }

        if (id == android.R.id.home) {
            helper_main.resetStartTab(Activity_dice.this);
            helper_main.isOpened(Activity_dice.this);
            helper_main.switchToActivity(Activity_dice.this, HHS_MainScreen.class, "", true);
        }

        if (id == R.id.action_help) {
            final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Activity_dice.this)
                    .setTitle(R.string.number_title)
                    .setMessage(helper_main.textSpannable(getString(R.string.helpRandom_text)))
                    .setPositiveButton(getString(R.string.toast_yes), null);
            dialog.show();
        }

        if (id == R.id.action_dice) {

            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_dice.this);
            View dialogView = View.inflate(Activity_dice.this, R.layout.dialog_dice, null);

            final TextView textChoose2 = (TextView) dialogView.findViewById(R.id.textChoose);
            final EditText editNumber2 = (EditText) dialogView.findViewById(R.id.editNumber);
            editNumber2.setHint(R.string.number_dice_hint);

            builder.setView(dialogView);
            builder.setTitle(R.string.number_dice);
            builder.setPositiveButton(R.string.toast_yes, null);
            builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            });

            final AlertDialog dialog2 = builder.create();
            // Display the custom alert dialog on interface
            dialog2.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {

                    Button b = dialog2.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            try {
                                number = Integer.parseInt(editNumber2.getText().toString());
                                Random rand = new Random();
                                int n = rand.nextInt(number);
                                textChoose2.setText(String.valueOf(n +1));
                            } catch(NumberFormatException nfe) {
                                Snackbar.make(textChoose2, R.string.number_dice_error, Snackbar.LENGTH_LONG).show();
                                nfe.printStackTrace();
                            }
                        }
                    });
                }
            });

            dialog2.show();

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    helper_main.showKeyboard(Activity_dice.this,editNumber2);
                }
            }, 200);
        }

        return super.onOptionsItemSelected(item);
    }
}