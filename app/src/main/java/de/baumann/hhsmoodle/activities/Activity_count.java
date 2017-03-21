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
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.data_count.Count_DbAdapter;
import de.baumann.hhsmoodle.helper.CustomListAdapter;
import de.baumann.hhsmoodle.helper.helper_main;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Activity_count extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private ArrayList<String> itemsTitle;
    private ArrayList<String> itemsCount;
    private ListView lvItems;

    private CustomListAdapter adapter;

    private String toDo_title;
    private String toDo_icon;
    private String toDo_create;
    private int toDo_seqno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        toDo_title = sharedPref.getString("count_title", "");
        String count_title = sharedPref.getString("count_content", "");
        toDo_icon = sharedPref.getString("count_icon", "");
        toDo_create = sharedPref.getString("count_create", "");
        String todo_attachment = sharedPref.getString("count_attachment", "");
        if (!sharedPref.getString("count_seqno", "").isEmpty()) {
            toDo_seqno = Integer.parseInt(sharedPref.getString("count_seqno", ""));
        }

        setContentView(R.layout.activity_count);
        setTitle(toDo_title);

        final EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String itemText = etNewItem.getText().toString();

                if (itemText.isEmpty()) {
                    Snackbar.make(lvItems, R.string.todo_enter, Snackbar.LENGTH_LONG).show();
                } else {
                    itemsTitle.add(0, itemText);
                    itemsCount.add(0, "0");
                    etNewItem.setText("");
                    writeItemsTitle();
                    writeItemsCount();
                    lvItems.post(new Runnable(){
                        public void run() {
                            lvItems.setSelection(lvItems.getCount() - 1);
                        }});
                    adapter.notifyDataSetChanged();
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        helper_main.onStart(Activity_count.this);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        try {
            FileOutputStream fOut = new FileOutputStream(newFileTitle());
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(count_title);
            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        try {
            FileOutputStream fOut = new FileOutputStream(newFileCount());
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(todo_attachment);
            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        lvItems = (ListView) findViewById(R.id.lvItems);
        itemsTitle = new ArrayList<>();
        readItemsTitle();
        readItemsCount();

        adapter=new CustomListAdapter(Activity_count.this, itemsTitle, itemsCount){
            @NonNull
            @Override
            public View getView (final int position, View convertView, @NonNull ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                ImageButton ib_plus = (ImageButton) v.findViewById(R.id.but_plus);
                ImageButton ib_minus = (ImageButton) v.findViewById(R.id.but_minus);
                TextView tv = (TextView) v.findViewById(R.id.count_count);

                int count = Integer.parseInt(itemsCount.get(position));

                if (count < 0) {
                    tv.setTextColor(ContextCompat.getColor(Activity_count.this,R.color.color_red));
                } else if (count > 0) {
                    tv.setTextColor(ContextCompat.getColor(Activity_count.this,R.color.color_green));
                } else if (count == 0) {
                    tv.setTextColor(ContextCompat.getColor(Activity_count.this,R.color.color_grey));
                }

                ib_plus.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        int a = Integer.parseInt(itemsCount.get(position)) + 1;
                        String plus = String.valueOf(a);

                        itemsCount.remove(position);
                        itemsCount.add(position, plus);
                        // Refresh the adapter
                        adapter.notifyDataSetChanged();
                        // Return true consumes the long click event (marks it handled)
                        writeItemsTitle();
                        writeItemsCount();

                    }
                });

                ib_minus.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        int a = Integer.parseInt(itemsCount.get(position)) - 1;
                        String minus = String.valueOf(a);

                        itemsCount.remove(position);
                        itemsCount.add(position, minus);
                        // Refresh the adapter
                        adapter.notifyDataSetChanged();
                        // Return true consumes the long click event (marks it handled)
                        writeItemsTitle();
                        writeItemsCount();
                    }
                });

                return v;
            }
        };

        lvItems.setAdapter(adapter);

        lvItems.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView<?> parent,
                                    View view, final int position, long id) {

                final String title = itemsTitle.get(position);
                final String count = itemsCount.get(position);

                // Remove the item within array at position
                itemsTitle.remove(position);
                itemsCount.remove(position);
                // Refresh the adapter
                adapter.notifyDataSetChanged();
                // Return true consumes the long click event (marks it handled)
                writeItemsTitle();
                writeItemsCount();

                Snackbar snackbar = Snackbar
                        .make(lvItems, R.string.todo_removed, Snackbar.LENGTH_LONG)
                        .setAction(R.string.todo_removed_back, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                itemsTitle.add(position, title);
                                itemsCount.add(position, count);
                                // Refresh the adapter
                                adapter.notifyDataSetChanged();
                                // Return true consumes the long click event (marks it handled)
                                writeItemsTitle();
                                writeItemsCount();
                            }
                        });
                snackbar.show();
            }
        });

        lvItems.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(android.widget.AdapterView<?> parent,
                                    View view, final int position, long id) {

                final String title = itemsTitle.get(position);
                final String count = itemsCount.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_count.this);
                View dialogView = View.inflate(Activity_count.this, R.layout.dialog_edit_text_singleline_count, null);

                final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                edit_title.setText(title);

                builder.setView(dialogView);
                builder.setTitle(R.string.number_edit_entry);
                builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        String inputTag = edit_title.getText().toString().trim();
                        // Remove the item within array at position
                        itemsTitle.remove(position);
                        itemsCount.remove(position);

                        itemsTitle.add(position, inputTag);
                        itemsCount.add(position, count);

                        // Refresh the adapter
                        adapter.notifyDataSetChanged();
                        // Return true consumes the long click event (marks it handled)
                        writeItemsTitle();
                        writeItemsCount();
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
                helper_main.showKeyboard(Activity_count.this,edit_title);

                return true;
            }
        });
    }

    private void readItemsTitle() {
        try {
            //noinspection unchecked
            itemsTitle = new ArrayList<>(FileUtils.readLines(newFileTitle()));
        } catch (IOException e) {
            itemsTitle = new ArrayList<>();
        }
    }

    private void readItemsCount() {
        try {
            //noinspection unchecked
            itemsCount = new ArrayList<>(FileUtils.readLines(newFileCount()));
        } catch (IOException e) {
            itemsCount = new ArrayList<>();
        }
    }

    private void writeItemsCount() {
        try {
            FileUtils.writeLines(newFileCount(), itemsCount);
        } catch (IOException e) {
            e.printStackTrace();
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

    private String getTextCount() {
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(newFileCount()));
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
        return  new File(Activity_count.this.getFilesDir() + "title.txt");
    }
    private File newFileCount() {
        return  new File(Activity_count.this.getFilesDir() + "count.txt");
    }




    @Override
    public void onBackPressed() {

        Count_DbAdapter db = new Count_DbAdapter(Activity_count.this);
        db.open();

        if (!sharedPref.getString("count_seqno", "").isEmpty()) {
            db.update(toDo_seqno, toDo_title, getTextTitle(), toDo_icon, getTextCount(), toDo_create);
        } else {
            if(db.isExist(toDo_title)){
                Snackbar.make(lvItems, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
            } else {
                db.insert(toDo_title, getTextTitle(), toDo_icon, getTextCount(), toDo_create);
            }
        }

        sharedPref.edit().putString("count_title", "").apply();
        sharedPref.edit().putString("count_text", "").apply();
        sharedPref.edit().putString("count_seqno", "").apply();
        sharedPref.edit().putString("count_icon", "").apply();
        sharedPref.edit().putString("count_create", "").apply();
        sharedPref.edit().putString("count_attachment", "").apply();
        newFileTitle().delete();
        newFileCount().delete();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Activity_count.this);
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Activity_count.this);
    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isClosed(Activity_count.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Count_DbAdapter db = new Count_DbAdapter(Activity_count.this);
            db.open();

            if (!sharedPref.getString("count_seqno", "").isEmpty()) {
                db.update(toDo_seqno, toDo_title, getTextTitle(), toDo_icon, getTextCount(), toDo_create);
            } else {
                if(db.isExist(toDo_title)){
                    Snackbar.make(lvItems, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                } else {
                    db.insert(toDo_title, getTextTitle(), toDo_icon, getTextCount(), toDo_create);
                }
            }

            sharedPref.edit().putString("count_title", "").apply();
            sharedPref.edit().putString("count_text", "").apply();
            sharedPref.edit().putString("count_seqno", "").apply();
            sharedPref.edit().putString("count_icon", "").apply();
            sharedPref.edit().putString("count_create", "").apply();
            sharedPref.edit().putString("count_attachment", "").apply();
            newFileTitle().delete();
            newFileCount().delete();
            helper_main.isOpened(Activity_count.this);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}