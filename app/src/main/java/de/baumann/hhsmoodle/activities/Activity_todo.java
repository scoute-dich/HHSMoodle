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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.data_todo.Todo_DbAdapter;
import de.baumann.hhsmoodle.helper.helper_main;

public class Activity_todo extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;

    private String toDo_title;
    private String toDo_icon;
    private String toDo_create;
    private String todo_attachment;
    private int toDo_seqno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        toDo_title = sharedPref.getString("toDo_title", "");
        String toDo_text = sharedPref.getString("toDo_text", "");
        toDo_icon = sharedPref.getString("toDo_icon", "");
        toDo_create = sharedPref.getString("toDo_create", "");
        todo_attachment = sharedPref.getString("toDo_attachment", "");
        if (!sharedPref.getString("toDo_seqno", "").isEmpty()) {
            toDo_seqno = Integer.parseInt(sharedPref.getString("toDo_seqno", ""));
        }

        setContentView(R.layout.activity_todo);
        setTitle(toDo_title);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        helper_main.onStart(Activity_todo.this);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        try {
            FileOutputStream fOut = new FileOutputStream(newFile());
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(toDo_text);
            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<>();
        readItems();
        itemsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        lvItems.post(new Runnable(){
            public void run() {
                lvItems.setSelection(lvItems.getCount() - 1);
            }});

        setupListViewListener();

        final EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        ImageButton ib_paste = (ImageButton) findViewById(R.id.imageButtonPaste);
        final ImageButton ib_not = (ImageButton) findViewById(R.id.imageButtonNot);

        switch (todo_attachment) {
            case "true":
                ib_not.setImageResource(R.drawable.alert_circle);
                break;
            case "":
                ib_not.setImageResource(R.drawable.alert_circle_red);
                break;
        }

        ib_not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (todo_attachment) {
                    case "true":
                        ib_not.setImageResource(R.drawable.alert_circle_red);
                        sharedPref.edit().putString("toDo_attachment", "").apply();
                        todo_attachment = sharedPref.getString("toDo_attachment", "");
                        break;
                    case "":
                        ib_not.setImageResource(R.drawable.alert_circle);
                        sharedPref.edit().putString("toDo_attachment", "true").apply();
                        todo_attachment = sharedPref.getString("toDo_attachment", "");
                        break;
                }

            }
        });

        ib_paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] options = {
                        getString(R.string.paste_date),
                        getString(R.string.paste_time)};
                new android.app.AlertDialog.Builder(Activity_todo.this)
                        .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        })
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals(getString(R.string.paste_date))) {
                                    Date date = new Date();
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    String dateNow = format.format(date);
                                    etNewItem.getText().insert(etNewItem.getSelectionStart(), dateNow);
                                }

                                if (options[item].equals (getString(R.string.paste_time))) {
                                    Date date = new Date();
                                    SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                    String timeNow = format.format(date);
                                    etNewItem.getText().insert(etNewItem.getSelectionStart(), timeNow);
                                }
                            }
                        }).show();
            }
        });
        
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String itemText = etNewItem.getText().toString();

                if (itemText.isEmpty()) {
                    Snackbar.make(lvItems, R.string.todo_enter, Snackbar.LENGTH_LONG).show();
                } else {
                    itemsAdapter.add(itemText);
                    etNewItem.setText("");
                    writeItems();
                    lvItems.post(new Runnable(){
                        public void run() {
                            lvItems.setSelection(lvItems.getCount() - 1);
                        }});
                }
            }
        });
    }

    private void readItems() {
        try {
            //noinspection unchecked
            items = new ArrayList<>(FileUtils.readLines(newFile()));
        } catch (IOException e) {
            items = new ArrayList<>();
        }
    }

    private void writeItems() {
        try {
            FileUtils.writeLines(newFile(), items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getText() {
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(newFile()));
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

    private File newFile() {
        return  new File(Activity_todo.this.getFilesDir() + "todo.txt");
    }

    // Attaches a long click listener to the listView
    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, final int pos, long id) {

                        final String text=(String)adapter.getItemAtPosition(pos);
                        // Remove the item within array at position
                        items.remove(pos);
                        // Refresh the adapter
                        itemsAdapter.notifyDataSetChanged();
                        // Return true consumes the long click event (marks it handled)
                        writeItems();

                        Snackbar snackbar = Snackbar
                                .make(lvItems, R.string.todo_removed, Snackbar.LENGTH_LONG)
                                .setAction(R.string.todo_removed_back, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        items.add(pos, text);
                                        // Refresh the adapter
                                        itemsAdapter.notifyDataSetChanged();
                                        // Return true consumes the long click event (marks it handled)
                                        writeItems();
                                    }
                                });
                        snackbar.show();

                        return true;
                    }
                });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter,
                                    View item, final int pos, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_todo.this);
                View dialogView = View.inflate(Activity_todo.this, R.layout.dialog_edit_text_singleline, null);

                final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                ImageButton ib_paste = (ImageButton) dialogView.findViewById(R.id.imageButtonPaste);

                ib_paste.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        final CharSequence[] options = {
                                getString(R.string.paste_date),
                                getString(R.string.paste_time)};
                        new android.app.AlertDialog.Builder(Activity_todo.this)
                                .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                    }
                                })
                                .setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int item) {
                                        if (options[item].equals(getString(R.string.paste_date))) {
                                            Date date = new Date();
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                            String dateNow = format.format(date);
                                            edit_title.getText().insert(edit_title.getSelectionStart(), dateNow);
                                        }

                                        if (options[item].equals (getString(R.string.paste_time))) {
                                            Date date = new Date();
                                            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                            String timeNow = format.format(date);
                                            edit_title.getText().insert(edit_title.getSelectionStart(), timeNow);
                                        }
                                    }
                                }).show();
                    }
                });

                String text=(String)adapter.getItemAtPosition(pos);
                edit_title.setText(text);

                builder.setView(dialogView);
                builder.setTitle(R.string.number_edit_entry);
                builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        String inputTag = edit_title.getText().toString().trim();
                        // Remove the item within array at position
                        items.remove(pos);
                        items.add(pos, inputTag);
                        // Refresh the adapter
                        itemsAdapter.notifyDataSetChanged();
                        // Return true consumes the long click event (marks it handled)
                        writeItems();

                    }
                });
                builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                final AlertDialog dialog2 = builder.create();
                // Display the custom alert dialog on interface
                dialog2.show();
                helper_main.showKeyboard(Activity_todo.this,edit_title);
            }
        });
    }

    @Override
    public void onBackPressed() {

        Todo_DbAdapter db = new Todo_DbAdapter(Activity_todo.this);
        db.open();

        if (!sharedPref.getString("toDo_seqno", "").isEmpty()) {
            db.update(toDo_seqno, toDo_title, getText(), toDo_icon, todo_attachment, toDo_create);
        } else {
            if(db.isExist(toDo_title)){
                Snackbar.make(lvItems, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
            } else {
                db.insert(toDo_title, getText(), toDo_icon, todo_attachment, toDo_create);
            }
        }

        sharedPref.edit().putString("toDo_title", "").apply();
        sharedPref.edit().putString("toDo_text", "").apply();
        sharedPref.edit().putString("toDo_seqno", "").apply();
        sharedPref.edit().putString("toDo_icon", "").apply();
        sharedPref.edit().putString("toDo_create", "").apply();
        sharedPref.edit().putString("toDo_attachment", "").apply();
        //noinspection ResultOfMethodCallIgnored
        newFile().delete();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Todo_DbAdapter db = new Todo_DbAdapter(Activity_todo.this);
            db.open();

            if (!sharedPref.getString("toDo_seqno", "").isEmpty()) {
                db.update(toDo_seqno, toDo_title, getText(), toDo_icon, todo_attachment, toDo_create);
            } else {
                if(db.isExist(toDo_title)){
                    Snackbar.make(lvItems, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                } else {
                    db.insert(toDo_title, getText(), toDo_icon, todo_attachment, toDo_create);
                }
            }

            sharedPref.edit().putString("toDo_title", "").apply();
            sharedPref.edit().putString("toDo_text", "").apply();
            sharedPref.edit().putString("toDo_seqno", "").apply();
            sharedPref.edit().putString("toDo_icon", "").apply();
            sharedPref.edit().putString("toDo_create", "").apply();
            sharedPref.edit().putString("toDo_attachment", "").apply();
            //noinspection ResultOfMethodCallIgnored
            newFile().delete();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}