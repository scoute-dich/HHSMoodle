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

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.data_count.Count_DbAdapter;
import de.baumann.hhsmoodle.helper.helper_main;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Activity_count extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private ArrayList<String> itemsTitle;
    private ListView lvItems;

    private String titleString;
    private String countString;

    private String toDo_title;
    private String toDo_icon;
    private String toDo_create;
    private String todo_attachment;
    private int toDo_seqno;

    private int count;
    private int top;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        toDo_title = sharedPref.getString("count_title", "");
        String todo_title = sharedPref.getString("count_content", "");
        toDo_icon = sharedPref.getString("count_icon", "19");
        toDo_create = sharedPref.getString("count_create", "");
        todo_attachment = sharedPref.getString("count_attachment", "");
        if (!sharedPref.getString("count_seqno", "").isEmpty()) {
            toDo_seqno = Integer.parseInt(sharedPref.getString("count_seqno", ""));
        }

        setContentView(R.layout.activity_random);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        setTitle(toDo_title);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        helper_main.onStart(Activity_count.this);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        try {
            FileOutputStream fOut = new FileOutputStream(newFileTitle());
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(todo_title);
            myOutWriter.close();
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        lvItems = findViewById(R.id.lvItems);
        itemsTitle = new ArrayList<>();
        readItemsTitle();
        setAdapter();

        lvItems.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView<?> parent,
                                    View view, final int position, long id) {

                getValues(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_count.this);
                View dialogView = View.inflate(Activity_count.this, R.layout.dialog_edit_text_singleline_count, null);

                final EditText edit_title = dialogView.findViewById(R.id.pass_title);
                edit_title.setText(titleString);

                builder.setView(dialogView);
                builder.setTitle(R.string.number_edit_entry);
                builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        String inputTag = edit_title.getText().toString().trim();
                        String itemText = inputTag + "|" + countString;
                        itemsTitle.remove(position);
                        itemsTitle.add(position, itemText);

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
                // Display the custom alert dialog on interface
                dialog.show();
                helper_main.showKeyboard(Activity_count.this,edit_title);
            }
        });

        lvItems.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(android.widget.AdapterView<?> parent,
                                    View view, final int position, long id) {

                getValues(position);
                final String title = itemsTitle.get(position);
                itemsTitle.remove(position);
                writeItemsTitle();
                setAdapter();

                Snackbar snackbar = Snackbar
                        .make(lvItems, R.string.todo_removed, Snackbar.LENGTH_LONG)
                        .setAction(R.string.todo_removed_back, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                itemsTitle.add(position, title);
                                writeItemsTitle();
                                setAdapter();
                            }
                        });
                snackbar.show();
                return true;
            }
        });
    }

    private void setAdapter () {

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

                getValues(position);

                ImageButton ib_plus = convertView.findViewById(R.id.but_plus);
                ImageButton ib_minus = convertView.findViewById(R.id.but_minus);

                TextView textTITLE = convertView.findViewById(R.id.count_title);
                TextView textDES = convertView.findViewById(R.id.count_count);

                textTITLE.setText(titleString);
                textDES.setText(countString);

                if (count < 0) {
                    textDES.setTextColor(ContextCompat.getColor(Activity_count.this,R.color.color_red));
                } else if (count > 0) {
                    textDES.setTextColor(ContextCompat.getColor(Activity_count.this,R.color.color_green));
                } else if (count == 0) {
                    textDES.setTextColor(ContextCompat.getColor(Activity_count.this,R.color.color_grey));
                }

                ib_plus.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        getValues(position);
                        int a = count + 1;
                        String plus = String.valueOf(a);
                        String itemText = titleString + "|" + plus;
                        itemsTitle.remove(position);
                        itemsTitle.add(position, itemText);
                        writeItemsTitle();
                        setAdapter();
                    }
                });

                ib_minus.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        getValues(position);
                        int a = count - 1;
                        String minus = String.valueOf(a);
                        String itemText = titleString + "|" + minus;
                        itemsTitle.remove(position);
                        itemsTitle.add(position, itemText);
                        writeItemsTitle();
                        setAdapter();
                    }
                });
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

    private void getValues(int position) {
        try {
            titleString = itemsTitle.get(position).substring(0, itemsTitle.get(position).lastIndexOf("|"));
            countString = itemsTitle.get(position).substring(itemsTitle.get(position).lastIndexOf("|")+1);
        } catch (Exception e) {
            titleString = getString(R.string.number_error_read);
            countString = getString(R.string.number_error_read);
        }

        try {
            count = Integer.parseInt(countString);
        } catch (Exception e) {
            count = 0;
        }
        index = lvItems.getFirstVisiblePosition();
        View v = lvItems.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - lvItems.getPaddingTop());
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
        return  new File(Activity_count.this.getFilesDir() + "count.txt");
    }

    private void closeActivity () {
        Count_DbAdapter db = new Count_DbAdapter(Activity_count.this);
        db.open();

        if (!sharedPref.getString("count_seqno", "").isEmpty()) {
            db.update(toDo_seqno, helper_main.secString(toDo_title), helper_main.secString(getTextTitle()), toDo_icon, todo_attachment, toDo_create);
        } else {
            if(db.isExist(helper_main.secString(toDo_title))){
                Snackbar.make(lvItems, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
            } else {
                db.insert(helper_main.secString(toDo_title), helper_main.secString(getTextTitle()), toDo_icon, todo_attachment, toDo_create);
            }
        }
        sharedPref.edit().putString("count_title", "").apply();
        sharedPref.edit().putString("count_text", "").apply();
        sharedPref.edit().putString("count_seqno", "").apply();
        sharedPref.edit().putString("count_icon", "").apply();
        sharedPref.edit().putString("count_create", "").apply();
        sharedPref.edit().putString("count_attachment", "").apply();
        sharedPref.edit().putString("count_content", "").apply();
        newFileTitle().delete();
        finish();
    }

    @Override
    public void onBackPressed() {
        closeActivity();
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

            getValues(0);
            final EditText edit_title = dialogView.findViewById(R.id.pass_title);
            edit_title.setHint(R.string.menu_addEntry);

            builder.setView(dialogView);
            builder.setTitle(R.string.menu_addEntry);
            builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    String itemText = edit_title.getText().toString();
                    itemsTitle.add(itemText + "|0");
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