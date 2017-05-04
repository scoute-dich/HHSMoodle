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

package de.baumann.hhsmoodle.data_random;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.popup.Popup_courseList;

public class Random_Fragment extends Fragment {

    //calling variables
    private Random_DbAdapter db;
    private ListView lv = null;
    private int number;

    private FloatingActionButton fab_dice;
    private FloatingActionButton fab;
    private LinearLayout fabLayout1;
    private LinearLayout fabLayout2;
    private boolean isFABOpen=false;

    private ArrayList<String> items;
    private ListView lvItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_screen_dice, container, false);

        ImageView imgHeader = (ImageView) rootView.findViewById(R.id.imageView_header);
        helper_main.setImageHeader(getActivity(), imgHeader);

        fab_dice = (FloatingActionButton) rootView.findViewById(R.id.fab_dice);
        lv = (ListView) rootView.findViewById(R.id.list);
        lvItems = (ListView) rootView.findViewById(R.id.lvItems);

        fabLayout1= (LinearLayout) rootView.findViewById(R.id.fabLayout1);
        fabLayout2= (LinearLayout) rootView.findViewById(R.id.fabLayout2);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        FloatingActionButton fab1 = (FloatingActionButton) rootView.findViewById(R.id.fab1);
        FloatingActionButton fab2 = (FloatingActionButton) rootView.findViewById(R.id.fab2);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                View dialogView = View.inflate(getActivity(), R.layout.dialog_edit_entry, null);

                final EditText edit_title = (EditText) dialogView.findViewById(R.id.note_title_input);
                edit_title.setHint(R.string.title_hint);

                final EditText edit_cont = (EditText) dialogView.findViewById(R.id.note_text_input);
                edit_cont.setHint(R.string.text_hint);

                builder.setView(dialogView);
                builder.setTitle(R.string.number_edit_entry);
                builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        String inputTitle = edit_title.getText().toString().trim();
                        String inputCont = edit_cont.getText().toString().trim();

                        if(db.isExist(inputTitle)){
                            Snackbar.make(lv, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                        }else{
                            db.insert(inputTitle, inputCont, "", "", helper_main.createDate());
                            dialog.dismiss();
                            setRandomList();
                            Snackbar.make(lv, R.string.bookmark_added, Snackbar.LENGTH_SHORT).show();
                        }
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
                helper_main.showKeyboard(getActivity(),edit_title);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                Intent mainIntent = new Intent(getActivity(), Popup_courseList.class);
                mainIntent.setAction("courseList_random");
                startActivity(mainIntent);
            }
        });


        fab_dice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Random rand = new Random();
                    final int n = rand.nextInt(lvItems.getCount());

                    setAdapter(n);
                    lvItems.setSelection(n-1);



                } catch(NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            }
        });

        //calling Notes_DbAdapter
        db = new Random_DbAdapter(getActivity());
        db.open();

        setRandomList();
        setHasOptionsMenu(true);

        return rootView;
    }

    private void showFABMenu(){
        isFABOpen=true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);

        fab.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab.animate().rotationBy(-180);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                if(!isFABOpen){
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
    }

    public void doBack() {
        //BackPressed in activity will call this;
        if(isFABOpen){
            closeFABMenu();
        }else if (lvItems.getVisibility() == View.VISIBLE) {
            //noinspection ResultOfMethodCallIgnored
            newFile().delete();
            lv.setVisibility(View.VISIBLE);
            lvItems.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            fab_dice.setVisibility(View.GONE);
            getActivity().setTitle(R.string.number_title);
        } else {
            helper_main.onClose(getActivity());
        }
    }

    private void setRandomList() {

        if(isFABOpen){
            closeFABMenu();
        }

        //display data
        final int layoutstyle=R.layout.list_item_notes;
        int[] xml_id = new int[] {
                R.id.textView_title_notes,
                R.id.textView_des_notes,
                R.id.textView_create_notes
        };
        String[] column = new String[] {
                "random_title",
                "random_content",
                "random_creation"
        };
        final Cursor row = db.fetchAllData();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), layoutstyle, row, column, xml_id, 0) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                ImageView iv_icon = (ImageView) v.findViewById(R.id.icon_notes);
                iv_icon.setVisibility(View.GONE);

                return v;
            }
        };

        lv.setAdapter(adapter);
        //onClick function
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {

                if(isFABOpen){
                    closeFABMenu();
                }

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String random_content = row2.getString(row2.getColumnIndexOrThrow("random_content"));
                final String random_title = row2.getString(row2.getColumnIndexOrThrow("random_title"));

                if (random_content.isEmpty()) {
                    Snackbar.make(lv, getActivity().getString(R.string.number_enterData), Snackbar.LENGTH_LONG).show();
                } else {
                    getActivity().setTitle(random_title);
                    lv.setVisibility(View.GONE);
                    lvItems.setVisibility(View.VISIBLE);

                    try {
                        FileOutputStream fOut = new FileOutputStream(newFile());
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                        myOutWriter.append(random_content);
                        myOutWriter.close();

                        fOut.flush();
                        fOut.close();
                    } catch (IOException e) {
                        Log.e("Exception", "File write failed: " + e.toString());
                    }

                    items = new ArrayList<>();
                    readItems();

                    setAdapter(1000);



                    fab.setVisibility(View.GONE);
                    fab_dice.setVisibility(View.VISIBLE);
                }
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if(isFABOpen){
                    closeFABMenu();
                }

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String random_title = row2.getString(row2.getColumnIndexOrThrow("random_title"));
                final String random_content = row2.getString(row2.getColumnIndexOrThrow("random_content"));
                final String random_icon = row2.getString(row2.getColumnIndexOrThrow("random_icon"));
                final String random_attachment = row2.getString(row2.getColumnIndexOrThrow("random_attachment"));
                final String random_creation = row2.getString(row2.getColumnIndexOrThrow("random_creation"));

                final CharSequence[] options = {
                        getString(R.string.number_edit_entry),
                        getString(R.string.bookmark_remove_bookmark)};
                new android.app.AlertDialog.Builder(getActivity())
                        .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {

                                if (options[item].equals(getString(R.string.number_edit_entry))) {

                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                                    View dialogView = View.inflate(getActivity(), R.layout.dialog_edit_entry, null);

                                    final EditText edit_title = (EditText) dialogView.findViewById(R.id.note_title_input);
                                    edit_title.setHint(R.string.title_hint);
                                    edit_title.setText(random_title);

                                    final EditText edit_cont = (EditText) dialogView.findViewById(R.id.note_text_input);
                                    edit_cont.setHint(R.string.text_hint);
                                    edit_cont.setText(random_content);

                                    builder.setView(dialogView);
                                    builder.setTitle(R.string.number_edit_entry);
                                    builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            String inputTitle = edit_title.getText().toString().trim();
                                            String inputCont = edit_cont.getText().toString().trim();
                                            db.update(Integer.parseInt(_id),inputTitle, inputCont, random_icon, random_attachment, random_creation);
                                            setRandomList();
                                            Snackbar.make(lv, R.string.bookmark_added, Snackbar.LENGTH_SHORT).show();
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
                                    helper_main.showKeyboard(getActivity(),edit_title);
                                }

                                if (options[item].equals(getString(R.string.bookmark_remove_bookmark))) {
                                    Snackbar snackbar = Snackbar
                                            .make(lv, R.string.note_remove_confirmation, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.toast_yes, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    db.delete(Integer.parseInt(_id));
                                                    setRandomList();
                                                }
                                            });
                                    snackbar.show();
                                }

                            }
                        }).show();

                return true;
            }
        });
    }

    private void setAdapter (final int count) {
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, items) {

            @NonNull
            @Override
            public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

                View v = super.getView(position, convertView, parent);

                if (position == count) {
                    v.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent_trans));
                } else {
                    v.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_trans));
                }
                return v;
            }
        };
        lvItems.setAdapter(itemsAdapter);
    }

    private File newFile() {
        return  new File(getActivity().getFilesDir() + "random.txt");
    }

    private void readItems() {
        try {
            //noinspection unchecked
            items = new ArrayList<>(FileUtils.readLines(newFile()));
        } catch (IOException e) {
            items = new ArrayList<>();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dice, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_sort).setVisible(false);
        getActivity().setTitle(R.string.number_title);
        setRandomList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_help:
                helper_main.switchToActivity(getActivity(), Random_Help.class, false);
                return true;

            case R.id.action_dice:
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                View dialogView = View.inflate(getActivity(), R.layout.dialog_dice, null);

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

                final android.support.v7.app.AlertDialog dialog2 = builder.create();
                // Display the custom alert dialog on interface
                dialog2.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = dialog2.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE);
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
                helper_main.showKeyboard(getActivity(),editNumber2);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}