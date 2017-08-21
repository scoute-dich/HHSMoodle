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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Comparator;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.data_courses.Courses_DbAdapter;
import de.baumann.hhsmoodle.helper.helper_main;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Activity_course extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private ArrayList<String> itemsTitle;
    private ListView lvItems;

    private String toDo_title;
    private String toDo_icon;
    private String todo_attachment;
    private String toDo_create;

    private int toDo_seqno;
    private int top;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_random);
        helper_main.onStart(Activity_course.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        toDo_title = sharedPref.getString("courses_title", "");
        toDo_icon = sharedPref.getString("courses_icon", "");
        toDo_create = sharedPref.getString("courses_create", "");
        String toDo_content = sharedPref.getString("courses_content", "");
        todo_attachment = sharedPref.getString("courses_attachment", "");
        if (!sharedPref.getString("courses_seqno", "").isEmpty()) {
            toDo_seqno = Integer.parseInt(sharedPref.getString("courses_seqno", ""));
        }

        setTitle(toDo_title);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        try {
            FileOutputStream fOut = new FileOutputStream(newFileTitle());
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(toDo_content);
            myOutWriter.close();
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        lvItems = (ListView) findViewById(R.id.lvItems);
        itemsTitle = new ArrayList<>();
        readItemsTitle();
        setAdapter();

        lvItems.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView<?> parent,
                                    View view, final int position, long id) {

                index = lvItems.getFirstVisiblePosition();
                View v = lvItems.getChildAt(0);
                top = (v == null) ? 0 : (v.getTop() - lvItems.getPaddingTop());
                final String title = itemsTitle.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_course.this);
                View dialogView = View.inflate(Activity_course.this, R.layout.dialog_edit_text_singleline_count, null);

                final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                edit_title.setText(title);

                builder.setView(dialogView);
                builder.setTitle(R.string.number_edit_entry);
                builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        String inputTag = edit_title.getText().toString().trim();
                        // Remove the item within array at position
                        itemsTitle.remove(position);
                        itemsTitle.add(position, inputTag);
                        setAdapter();
                        // Return true consumes the long click event (marks it handled)
                        writeItemsTitle();
                    }
                });
                builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                // Display the custom alert dialog on interface
                dialog.show();
                helper_main.showKeyboard(Activity_course.this,edit_title);
            }
        });

        lvItems.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(android.widget.AdapterView<?> parent,
                                    View view, final int position, long id) {

                index = lvItems.getFirstVisiblePosition();
                View v = lvItems.getChildAt(0);
                top = (v == null) ? 0 : (v.getTop() - lvItems.getPaddingTop());
                final String title = itemsTitle.get(position);

                itemsTitle.remove(position);
                setAdapter();
                writeItemsTitle();

                Snackbar snackbar = Snackbar
                        .make(lvItems, R.string.todo_removed, Snackbar.LENGTH_LONG)
                        .setAction(R.string.todo_removed_back, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                itemsTitle.add(position, title);
                                setAdapter();
                                writeItemsTitle();
                            }
                        });
                snackbar.show();
                return true;
            }
        });
    }

    private void setAdapter () {
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemsTitle);
        itemsAdapter.sort(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);   //or whatever your sorting algorithm
            }
        });
        lvItems.setAdapter(itemsAdapter);
        lvItems.setSelectionFromTop(index, top);
    }

    private void readItemsTitle() {
        try {
            //noinspection unchecked
            itemsTitle = new ArrayList<>(FileUtils.readLines(newFileTitle()));
        } catch (IOException e) {
            itemsTitle = new ArrayList<>();
        }
    }

    private void writeItemsTitle() {
        try {
            FileUtils.writeLines(newFileTitle(), itemsTitle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTextTitle() {
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(newFileTitle()));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return  text.toString();
    }

    private File newFileTitle() {
        return  new File(Activity_course.this.getFilesDir() + "course.txt");
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    private void closeActivity () {
        Courses_DbAdapter db = new Courses_DbAdapter(Activity_course.this);
        db.open();

        if (!sharedPref.getString("courses_seqno", "").isEmpty()) {
            db.update(toDo_seqno, helper_main.secString(toDo_title), helper_main.secString(getTextTitle()), toDo_icon, todo_attachment, toDo_create);
        } else {
            if(db.isExist(helper_main.secString(toDo_title))){
                Snackbar.make(lvItems, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
            } else {
                db.insert(helper_main.secString(toDo_title), helper_main.secString(getTextTitle()), toDo_icon, todo_attachment, toDo_create);
            }
        }
        sharedPref.edit().putString("courses_title", "").apply();
        sharedPref.edit().putString("courses_text", "").apply();
        sharedPref.edit().putString("courses_seqno", "").apply();
        sharedPref.edit().putString("courses_icon", "").apply();
        sharedPref.edit().putString("courses_create", "").apply();
        sharedPref.edit().putString("courses_attachment", "").apply();
        sharedPref.edit().putString("courses_content", "").apply();
        newFileTitle().delete();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_random, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            closeActivity();
        }

        if (id == R.id.action_add) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = View.inflate(this, R.layout.dialog_edit_title, null);

            index = lvItems.getFirstVisiblePosition();
            View v = lvItems.getChildAt(0);
            top = (v == null) ? 0 : (v.getTop() - lvItems.getPaddingTop());
            final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
            edit_title.setHint(R.string.menu_addEntry);

            builder.setView(dialogView);
            builder.setTitle(R.string.menu_addEntry);
            builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    String itemText = edit_title.getText().toString();
                    itemsTitle.add(0, itemText);
                    writeItemsTitle();
                    setAdapter();
                }
            });
            builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            });

            final AlertDialog dialog = builder.create();
            dialog.show();
            helper_main.showKeyboard(this,edit_title);
        }
        return super.onOptionsItemSelected(item);
    }
}