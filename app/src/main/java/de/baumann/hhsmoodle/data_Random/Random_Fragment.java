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

package de.baumann.hhsmoodle.data_Random;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.Random;

import de.baumann.hhsmoodle.HHS_MainScreen;
import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.helper_encryption;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.popup.Popup_courseList;

public class Random_Fragment extends Fragment implements HHS_MainScreen.OnBackPressedListener {

    //calling variables
    private Random_DbAdapter db;

    private ListView lv = null;
    private SharedPreferences sharedPref;

    private TextView textFile;
    private ScrollView scrollView;
    private FloatingActionButton fab_add;
    private FloatingActionButton fab;
    private int number;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((HHS_MainScreen) getActivity()).setOnBackPressedListener(this);
        View rootView = inflater.inflate(R.layout.fragment_screen_dice, container, false);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        ImageView imgHeader = (ImageView) rootView.findViewById(R.id.imageView_header);
        if(imgHeader != null) {
            TypedArray images = getResources().obtainTypedArray(R.array.splash_images);
            int choice = (int) (Math.random() * images.length());
            imgHeader.setImageResource(images.getResourceId(choice, R.drawable.splash1));
            images.recycle();
        }

        fab_add = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        textFile = (TextView) rootView.findViewById(R.id.textFile);
        lv = (ListView) rootView.findViewById(R.id.list);

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CharSequence[] options = {
                        getString(R.string.todo_from_courseList),
                        getString(R.string.todo_from_new)};
                new AlertDialog.Builder(getActivity())
                        .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals(getString(R.string.todo_from_courseList))) {
                                    helper_main.isOpened(getActivity());
                                    Intent mainIntent = new Intent(getActivity(), Popup_courseList.class);
                                    mainIntent.setAction("courseList_random");
                                    startActivity(mainIntent);
                                }

                                if (options[item].equals (getString(R.string.todo_from_new))) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    View dialogView = View.inflate(getActivity(), R.layout.dialog_edit_title, null);
                                    final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                                    edit_title.setHint(R.string.bookmark_edit_title);
                                    builder.setTitle(R.string.todo_from_new);
                                    builder.setView(dialogView);
                                    builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                        }
                                    })
                                            .setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    dialog.cancel();
                                                }
                                            });

                                    final AlertDialog dialog2 = builder.create();
                                    dialog2.show();

                                    dialog2.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //Do stuff, possibly set wantToCloseDialog to true then...

                                            String inputTitle = edit_title.getText().toString().trim();

                                            if(db.isExist(inputTitle)){
                                                Snackbar.make(lv, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                                            }else{
                                                db.insert(inputTitle, "", "", "", helper_main.createDate());
                                                dialog2.dismiss();
                                                setRandomList();
                                                Snackbar.make(lv, R.string.bookmark_added, Snackbar.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            helper_main.showKeyboard(getActivity(),edit_title);
                                        }
                                    }, 200);

                                }

                            }
                        }).show();
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
                    highlight.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.colorAccent)), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

        //calling Notes_DbAdapter
        db = new Random_DbAdapter(getActivity());
        db.open();

        setRandomList();
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            getActivity().setTitle(R.string.number_title);
            setRandomList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setRandomList();
    }

    @Override
    public void doBack() {
        //BackPressed in activity will call this;
        if (scrollView.getVisibility() == View.VISIBLE) {
            lv.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            fab_add.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
        } else {
            PreferenceManager.setDefaultValues(getActivity(), R.xml.user_settings, false);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

            if (sharedPref.getBoolean ("backup_aut", false)){
                try {helper_encryption.encryptBackup(getActivity(),"/bookmarks_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                try {helper_encryption.encryptBackup(getActivity(),"/courses_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                try {helper_encryption.encryptBackup(getActivity(),"/notes_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                try {helper_encryption.encryptBackup(getActivity(),"/random_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                try {helper_encryption.encryptBackup(getActivity(),"/todo_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
            }

            sharedPref.edit().putString("loadURL", "").apply();
            helper_main.isClosed(getActivity());
            Snackbar.make(lv, getString(R.string.app_encrypt) , Snackbar.LENGTH_LONG).show();
            helper_encryption.encryptDatabases(getActivity());
            getActivity().finish();
        }
    }

    private void setRandomList() {

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
        final Cursor row = db.fetchAllData(getActivity());
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

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String random_content = row2.getString(row2.getColumnIndexOrThrow("random_content"));

                if (random_content.isEmpty()) {
                    Snackbar.make(lv, getActivity().getString(R.string.number_enterData), Snackbar.LENGTH_LONG).show();
                } else {
                    lv.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    textFile.setText(String.valueOf(random_content));
                    fab_add.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                }
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String random_title = row2.getString(row2.getColumnIndexOrThrow("random_title"));
                final String random_content = row2.getString(row2.getColumnIndexOrThrow("random_content"));
                final String random_icon = row2.getString(row2.getColumnIndexOrThrow("random_icon"));
                final String random_attachment = row2.getString(row2.getColumnIndexOrThrow("random_attachment"));
                final String random_creation = row2.getString(row2.getColumnIndexOrThrow("random_creation"));

                final CharSequence[] options = {
                        getString(R.string.bookmark_edit_title),
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
                                if (options[item].equals(getString(R.string.bookmark_edit_title))) {

                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                                    View dialogView = View.inflate(getActivity(), R.layout.dialog_edit_title, null);

                                    final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                                    edit_title.setHint(R.string.bookmark_edit_title);
                                    edit_title.setText(random_title);

                                    builder.setView(dialogView);
                                    builder.setTitle(R.string.bookmark_edit_title);
                                    builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            String inputTag = edit_title.getText().toString().trim();
                                            db.update(Integer.parseInt(_id),inputTag, random_content, random_icon, random_attachment, random_creation);
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

                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            helper_main.showKeyboard(getActivity(),edit_title);
                                        }
                                    }, 200);
                                }

                                if (options[item].equals(getString(R.string.number_edit_entry))) {

                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                                    View dialogView = View.inflate(getActivity(), R.layout.dialog_edit_text, null);

                                    final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                                    edit_title.setHint(R.string.bookmark_edit_title);
                                    edit_title.setText(random_content);

                                    builder.setView(dialogView);
                                    builder.setTitle(R.string.number_edit_entry);
                                    builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            String inputTag = edit_title.getText().toString().trim();
                                            db.update(Integer.parseInt(_id),random_title, inputTag, random_icon, random_attachment, random_creation);
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

                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            helper_main.showKeyboard(getActivity(),edit_title);
                                        }
                                    }, 200);


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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dice, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_filter).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_help:
                final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.courseList_title)
                        .setMessage(helper_main.textSpannable(getString(R.string.helpCourse_text)))
                        .setPositiveButton(getString(R.string.toast_yes), null);
                dialog.show();
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

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        helper_main.showKeyboard(getActivity(),editNumber2);
                    }
                }, 200);
                return true;




            case R.id.action_sort:

                AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                View dialogView2 = View.inflate(getActivity(), R.layout.dialog_sorting, null);

                final CheckBox ch_title = (CheckBox) dialogView2.findViewById(R.id.checkBoxTitle);
                final CheckBox ch_create = (CheckBox) dialogView2.findViewById(R.id.checkBoxCreate);

                RelativeLayout layout_sortByAtt = (RelativeLayout) dialogView2.findViewById(R.id.layout_sortByAtt);
                layout_sortByAtt.setVisibility(View.GONE);
                RelativeLayout layout_sortByIcon = (RelativeLayout) dialogView2.findViewById(R.id.layout_sortByIcon);
                layout_sortByIcon.setVisibility(View.GONE);

                if (sharedPref.getString("sortDBR", "title").equals("title")) {
                    ch_title.setChecked(true);
                } else {
                    ch_title.setChecked(false);
                }
                if (sharedPref.getString("sortDBR", "title").equals("create")) {
                    ch_create.setChecked(true);
                } else {
                    ch_create.setChecked(false);
                }

                ch_title.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if(isChecked){
                            ch_create.setChecked(false);
                            sharedPref.edit().putString("sortDBR", "title").apply();
                            setRandomList();
                        }
                    }
                });
                ch_create.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if(isChecked){
                            ch_title.setChecked(false);
                            sharedPref.edit().putString("sortDBR", "create").apply();
                            setRandomList();
                        }
                    }
                });

                builder2.setView(dialogView2);
                builder2.setTitle(R.string.action_sort);
                builder2.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                final AlertDialog dialog3 = builder2.create();
                // Display the custom alert dialog on interface
                dialog3.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}