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
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.data_random.Random_DbAdapter;
import de.baumann.hhsmoodle.helper.helper_main;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Activity_random extends AppCompatActivity {

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
        helper_main.onStart(Activity_random.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        toDo_title = sharedPref.getString("random_title", "");
        toDo_icon = sharedPref.getString("random_icon", "19");
        toDo_create = sharedPref.getString("random_create", "");
        String toDo_content = sharedPref.getString("random_content", "");
        todo_attachment = sharedPref.getString("random_attachment", "");
        if (!sharedPref.getString("random_seqno", "").isEmpty()) {
            toDo_seqno = Integer.parseInt(sharedPref.getString("random_seqno", ""));
        }

        setTitle(toDo_title);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Random rand = new Random();
                    int n = rand.nextInt(lvItems.getCount());
                    setAdapter(n);
                    lvItems.setSelection(n-1);
                } catch(Exception e) {
                    e.printStackTrace();
                    Snackbar.make(lvItems, getString(R.string.number_enterData), Snackbar.LENGTH_LONG).show();
                }
            }
        });

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

        lvItems = findViewById(R.id.lvItems);
        itemsTitle = new ArrayList<>();
        readItemsTitle();
        setAdapter(1000);

        lvItems.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView<?> parent,
                                    View view, final int position, long id) {

                index = lvItems.getFirstVisiblePosition();
                View v = lvItems.getChildAt(0);
                top = (v == null) ? 0 : (v.getTop() - lvItems.getPaddingTop());
                final String title = itemsTitle.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_random.this);
                View dialogView = View.inflate(Activity_random.this, R.layout.dialog_edit_text_singleline_count, null);

                final EditText edit_title = dialogView.findViewById(R.id.pass_title);
                edit_title.setText(title);

                builder.setView(dialogView);
                builder.setTitle(R.string.number_edit_entry);
                builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        String inputTag = edit_title.getText().toString().trim();
                        // Remove the item within array at position
                        itemsTitle.remove(position);
                        itemsTitle.add(position, inputTag);
                        setAdapter(1000);
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
                helper_main.showKeyboard(Activity_random.this,edit_title);
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
                setAdapter(1000);
                writeItemsTitle();

                Snackbar snackbar = Snackbar
                        .make(lvItems, R.string.todo_removed, Snackbar.LENGTH_LONG)
                        .setAction(R.string.todo_removed_back, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                itemsTitle.add(position, title);
                                setAdapter(1000);
                                writeItemsTitle();
                            }
                        });
                snackbar.show();
                return true;
            }
        });
    }

    private void setAdapter (final int count) {
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this,
                R.layout.list_item_count, itemsTitle) {

            @NonNull
            @Override
            public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

                if (convertView == null) {
                    LayoutInflater infInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert infInflater != null;
                    convertView = infInflater.inflate(R.layout.list_item_count, parent, false);
                }

                ImageButton ib_plus = convertView.findViewById(R.id.but_plus);
                ImageButton ib_minus = convertView.findViewById(R.id.but_minus);
                TextView textTITLE = convertView.findViewById(R.id.count_title);
                TextView textDES = convertView.findViewById(R.id.count_count);

                ib_plus.setVisibility(View.GONE);
                ib_minus.setVisibility(View.GONE);
                textDES.setVisibility(View.GONE);
                textTITLE.setText(itemsTitle.get(position));

                if (position == 1000) {
                    convertView.setBackgroundColor(ContextCompat.getColor(Activity_random.this, R.color.color_trans));
                } else if (position == count) {
                    convertView.setBackgroundColor(ContextCompat.getColor(Activity_random.this, R.color.colorAccent_trans));
                } else {
                    convertView.setBackgroundColor(ContextCompat.getColor(Activity_random.this, R.color.color_trans));
                }
                return convertView;
            }
        };
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
        return  new File(Activity_random.this.getFilesDir() + "title.txt");
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    private void closeActivity () {
        Random_DbAdapter db = new Random_DbAdapter(Activity_random.this);
        db.open();

        if (!sharedPref.getString("random_seqno", "").isEmpty()) {
            db.update(toDo_seqno, helper_main.secString(toDo_title), helper_main.secString(getTextTitle()), toDo_icon, todo_attachment, toDo_create);
        } else {
            if(db.isExist(helper_main.secString(toDo_title))){
                Snackbar.make(lvItems, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
            } else {
                db.insert(helper_main.secString(toDo_title), helper_main.secString(getTextTitle()), toDo_icon, todo_attachment, toDo_create);
            }
        }

        sharedPref.edit().putString("random_title", "").apply();
        sharedPref.edit().putString("random_text", "").apply();
        sharedPref.edit().putString("random_seqno", "").apply();
        sharedPref.edit().putString("random_icon", "").apply();
        sharedPref.edit().putString("random_create", "").apply();
        sharedPref.edit().putString("random_attachment", "").apply();
        sharedPref.edit().putString("random_content", "").apply();
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
            final EditText edit_title = dialogView.findViewById(R.id.pass_title);
            edit_title.setHint(R.string.menu_addEntry);

            builder.setView(dialogView);
            builder.setTitle(R.string.menu_addEntry);
            builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    String itemText = edit_title.getText().toString();
                    itemsTitle.add(0, itemText);
                    writeItemsTitle();
                    setAdapter(1000);
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