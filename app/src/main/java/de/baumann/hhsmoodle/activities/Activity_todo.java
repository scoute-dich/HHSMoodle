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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

import de.baumann.hhsmoodle.HHS_MainScreen;
import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.databases.Database_Todo;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.helper.helper_notes;
import filechooser.ChooserDialog;

import static de.baumann.hhsmoodle.helper.helper_main.newFileDest;

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
        toDo_seqno = Integer.parseInt(sharedPref.getString("toDo_seqno", ""));

        setContentView(R.layout.activity_todo);
        setTitle(toDo_title);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        helper_main.onStart(Activity_todo.this);

        if(toolbar != null) {
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String startURL = sharedPref.getString("favoriteURL", "https://moodle.huebsch.ka.schule-bw.de/moodle/");
                    final String startType = sharedPref.getString("startType", "1");

                    if (startType.equals("2")) {
                        helper_main.isOpened(Activity_todo.this);
                        helper_main.switchToActivity(Activity_todo.this, Activity_grades.class, startURL, false);
                    } else if (startType.equals("1")){
                        helper_main.isOpened(Activity_todo.this);
                        helper_main.switchToActivity(Activity_todo.this, HHS_MainScreen.class, "", true);
                    }
                }
            });

            if (sharedPref.getBoolean ("longPress", false)){
                toolbar.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        helper_main.isClosed(Activity_todo.this);
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
                    String startDir = Environment.getExternalStorageDirectory() + "/HHS_Moodle/";

                    new ChooserDialog().with(Activity_todo.this)
                            .withFilter(false, false, "txt")
                            .withStartFile(startDir)
                            .withChosenListener(new ChooserDialog.Result() {
                                @Override
                                public void onChoosePath(final String path, final File pathFile) {
                                    StringBuilder text = new StringBuilder();

                                    try {
                                        BufferedReader br = new BufferedReader(new FileReader(pathFile));
                                        String line;

                                        while ((line = br.readLine()) != null) {
                                            text.append(line);
                                            text.append('\n');
                                        }
                                        br.close();

                                    } catch (IOException e) {
                                        Snackbar.make(lvItems, R.string.number_error_read, Snackbar.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }

                                    try {

                                        if (text.length() > 0) {

                                            String text2 = text.toString();
                                            String textAdd = text2.substring(0, text2.length()-1);
                                            itemsAdapter.add(textAdd);
                                            writeItems();
                                            items = new ArrayList<>();
                                            readItems();
                                            itemsAdapter = new ArrayAdapter<>(Activity_todo.this,
                                                    android.R.layout.simple_list_item_1, items);
                                            lvItems.setAdapter(itemsAdapter);
                                            lvItems.post(new Runnable(){
                                                public void run() {
                                                    lvItems.setSelection(lvItems.getCount() - 1);
                                                }});
                                        }
                                    } catch (Exception e) {
                                        Snackbar.make(lvItems, R.string.number_error_read, Snackbar.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .build()
                            .show();
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

    private static String getText() {
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

    private static File newFile() {
        return  new File(Environment.getExternalStorageDirectory() + newFileDest() + "todo.txt");
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

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        helper_main.showKeyboard(Activity_todo.this,edit_title);
                    }
                }, 200);
            }
        });
    }

    @Override
    public void onBackPressed() {

        try {
            final Database_Todo db = new Database_Todo(Activity_todo.this);

            db.deleteNote(toDo_seqno);
            db.addBookmark(toDo_title, getText(), toDo_icon, todo_attachment, toDo_create);
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
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
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Activity_todo.this);
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Activity_todo.this);
    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isClosed(Activity_todo.this);
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
        getMenuInflater().inflate(R.menu.menu_todo, menu);
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
                    .putString("handleTextTitle", toDo_title)
                    .putString("handleTextText", getText())
                    .putString("handleTextIcon", "")
                    .putString("handleTextAttachment", "")
                    .putString("handleTextCreate", helper_main.createDate())
                    .putString("handleTextSeqno", "")
                    .apply();
            helper_notes.editNote(Activity_todo.this);
        }

        if (id == R.id.action_share) {

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, toDo_title);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, getText());
            startActivity(Intent.createChooser(sharingIntent, (getString(R.string.note_share_2))));
        }

        if (id == R.id.action_folder) {
            String startDir = Environment.getExternalStorageDirectory() + "/HHS_Moodle/";
            helper_main.openFilePicker(Activity_todo.this, lvItems, startDir);
        }

        if (id == android.R.id.home) {
            try {
                final Database_Todo db = new Database_Todo(Activity_todo.this);
                db.deleteNote(toDo_seqno);
                db.addBookmark(toDo_title, getText(), toDo_icon, todo_attachment, toDo_create);
                db.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            sharedPref.edit().putString("toDo_title", "").apply();
            sharedPref.edit().putString("toDo_text", "").apply();
            sharedPref.edit().putString("toDo_seqno", "").apply();
            sharedPref.edit().putString("toDo_icon", "").apply();
            sharedPref.edit().putString("toDo_create", "").apply();
            sharedPref.edit().putString("toDo_attachment", "").apply();
            //noinspection ResultOfMethodCallIgnored
            newFile().delete();
            helper_main.isOpened(Activity_todo.this);
            helper_main.switchToActivity(Activity_todo.this, HHS_MainScreen.class, "", true);
        }

        if (id == R.id.action_help) {
            final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Activity_todo.this)
                    .setTitle(R.string.todo_title)
                    .setMessage(helper_main.textSpannable(getString(R.string.helpToDo_activity_text)))
                    .setPositiveButton(getString(R.string.toast_yes), null);
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }
}