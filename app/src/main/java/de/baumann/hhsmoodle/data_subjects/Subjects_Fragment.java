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

package de.baumann.hhsmoodle.data_subjects;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.data_courses.Courses_DbAdapter;
import de.baumann.hhsmoodle.data_notes.Notes_helper;
import de.baumann.hhsmoodle.data_random.Random_DbAdapter;
import de.baumann.hhsmoodle.data_todo.Todo_helper;
import de.baumann.hhsmoodle.helper.helper_main;

public class Subjects_Fragment extends Fragment {

    //calling variables
    private Subject_DbAdapter db;
    private ListView lv = null;
    private SharedPreferences sharedPref;

    private EditText titleInput;
    private EditText teacherInput;
    private EditText roomInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_screen_notes, container, false);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        ImageView imgHeader = (ImageView) rootView.findViewById(R.id.imageView_header);
        if(imgHeader != null) {
            TypedArray images = getResources().obtainTypedArray(R.array.splash_images);
            int choice = (int) (Math.random() * images.length());
            imgHeader.setImageResource(images.getResourceId(choice, R.drawable.splash1));
            images.recycle();
        }

        RelativeLayout filter_layout = (RelativeLayout) rootView.findViewById(R.id.filter_layout);
        filter_layout.setVisibility(View.GONE);
        lv = (ListView) rootView.findViewById(R.id.listNotes);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getActivity().getLayoutInflater();

                final ViewGroup nullParent = null;
                View dialogView = inflater.inflate(R.layout.dialog_edit_subject, nullParent);

                titleInput = (EditText) dialogView.findViewById(R.id.subject_title_);
                titleInput.setSelection(titleInput.getText().length());
                titleInput.setText("");
                teacherInput = (EditText) dialogView.findViewById(R.id.subject_teacher);
                teacherInput.setText("");
                roomInput = (EditText) dialogView.findViewById(R.id.subject_room);
                roomInput.setText("");

                helper_main.showKeyboard(getActivity(),titleInput);

                final ImageButton be = (ImageButton) dialogView.findViewById(R.id.imageButtonPri);
                assert be != null;
                be.setImageResource(R.drawable.circle_grey);
                sharedPref.edit().putString("subject_color", "11").apply();

                be.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        final Item[] items = {
                                new Item(getString(R.string.subjects_color_red), R.drawable.circle_red),
                                new Item(getString(R.string.subjects_color_pink), R.drawable.circle_pink),
                                new Item(getString(R.string.subjects_color_purple), R.drawable.circle_purple),
                                new Item(getString(R.string.subjects_color_blue), R.drawable.circle_blue),
                                new Item(getString(R.string.subjects_color_teal), R.drawable.circle_teal),
                                new Item(getString(R.string.subjects_color_green), R.drawable.circle_green),
                                new Item(getString(R.string.subjects_color_lime), R.drawable.circle_lime),
                                new Item(getString(R.string.subjects_color_yellow), R.drawable.circle_yellow),
                                new Item(getString(R.string.subjects_color_orange), R.drawable.circle_orange),
                                new Item(getString(R.string.subjects_color_brown), R.drawable.circle_brown),
                                new Item(getString(R.string.subjects_color_grey), R.drawable.circle_grey),
                        };

                        ListAdapter adapter = new ArrayAdapter<Item>(
                                getActivity(),
                                android.R.layout.select_dialog_item,
                                android.R.id.text1,
                                items){
                            @NonNull
                            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                                //Use super class to create the View
                                View v = super.getView(position, convertView, parent);
                                TextView tv = (TextView)v.findViewById(android.R.id.text1);
                                tv.setTextSize(18);
                                tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);
                                //Add margin between image and text (support various screen densities)
                                int dp5 = (int) (24 * getActivity().getResources().getDisplayMetrics().density + 0.5f);
                                tv.setCompoundDrawablePadding(dp5);

                                return v;
                            }
                        };

                        new android.app.AlertDialog.Builder(getActivity())
                                .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }
                                })
                                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        if (item == 0) {
                                            be.setImageResource(R.drawable.circle_red);
                                            sharedPref.edit().putString("subject_color", "1").apply();
                                        } else if (item == 1) {
                                            be.setImageResource(R.drawable.circle_pink);
                                            sharedPref.edit().putString("subject_color", "2").apply();
                                        } else if (item == 2) {
                                            be.setImageResource(R.drawable.circle_purple);
                                            sharedPref.edit().putString("subject_color", "3").apply();
                                        } else if (item == 3) {
                                            be.setImageResource(R.drawable.circle_blue);
                                            sharedPref.edit().putString("subject_color", "4").apply();
                                        } else if (item == 4) {
                                            be.setImageResource(R.drawable.circle_teal);
                                            sharedPref.edit().putString("subject_color", "5").apply();
                                        } else if (item == 5) {
                                            be.setImageResource(R.drawable.circle_green);
                                            sharedPref.edit().putString("subject_color", "6").apply();
                                        } else if (item == 6) {
                                            be.setImageResource(R.drawable.circle_lime);
                                            sharedPref.edit().putString("subject_color", "7").apply();
                                        } else if (item == 7) {
                                            be.setImageResource(R.drawable.circle_yellow);
                                            sharedPref.edit().putString("subject_color", "8").apply();
                                        } else if (item == 8) {
                                            be.setImageResource(R.drawable.circle_orange);
                                            sharedPref.edit().putString("subject_color", "9").apply();
                                        } else if (item == 9) {
                                            be.setImageResource(R.drawable.circle_brown);
                                            sharedPref.edit().putString("subject_color", "10").apply();
                                        } else if (item == 10) {
                                            be.setImageResource(R.drawable.circle_grey);
                                            sharedPref.edit().putString("subject_color", "11").apply();
                                        }
                                    }
                                }).show();
                    }
                });

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.subjects_edit);
                builder.setView(dialogView);
                builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Subject_DbAdapter db = new Subject_DbAdapter(getActivity());
                        db.open();

                        String inputTitle = titleInput.getText().toString().trim();
                        String inputTeacher = teacherInput.getText().toString().trim();
                        String inputRoom = roomInput.getText().toString().trim();

                        Date date = new Date();
                        DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd_HH-mm-ss", Locale.getDefault());
                        String creation =  dateFormat.format(date);

                        if(db.isExist(inputTitle)){
                            Snackbar.make(titleInput, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                        }else{
                            db.insert(inputTitle, inputTeacher, sharedPref.getString("subject_color", ""), inputRoom, creation);
                            dialog.dismiss();
                            setSubjectsList();
                        }

                    }
                });
                builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                final android.support.v7.app.AlertDialog dialog2 = builder.create();
                dialog2.show();
            }
        });

        //calling Notes_DbAdapter
        db = new Subject_DbAdapter(getActivity());
        db.open();

        setSubjectsList();
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            getActivity().setTitle(R.string.subjects_title);
            setSubjectsList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setSubjectsList();
    }

    private void setSubjectsList() {

        //display data
        final int layoutstyle=R.layout.list_item_schedule;
        int[] xml_id = new int[] {
                R.id.textView_title_notes,
                R.id.textView_des_notes,
                R.id.att_notes
        };
        String[] column = new String[] {
                "subject_title",
                "subject_content",
                "subject_attachment"
        };
        final Cursor row = db.fetchAllData();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), layoutstyle, row, column, xml_id, 0) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String subject_title = row2.getString(row2.getColumnIndexOrThrow("subject_title"));
                final String subject_content = row2.getString(row2.getColumnIndexOrThrow("subject_content"));
                final String subject_icon = row2.getString(row2.getColumnIndexOrThrow("subject_icon"));
                final String subject_attachment = row2.getString(row2.getColumnIndexOrThrow("subject_attachment"));
                final String subject_creation = row2.getString(row2.getColumnIndexOrThrow("subject_creation"));

                View v = super.getView(position, convertView, parent);
                ImageView iv_icon = (ImageView) v.findViewById(R.id.icon_notes);

                switch (subject_icon) {
                    case "1":iv_icon.setImageResource(R.drawable.circle_red);break;
                    case "2":iv_icon.setImageResource(R.drawable.circle_pink);break;
                    case "3":iv_icon.setImageResource(R.drawable.circle_purple);break;
                    case "4":iv_icon.setImageResource(R.drawable.circle_blue);break;
                    case "5":iv_icon.setImageResource(R.drawable.circle_teal);break;
                    case "6":iv_icon.setImageResource(R.drawable.circle_green);break;
                    case "7":iv_icon.setImageResource(R.drawable.circle_lime);break;
                    case "8":iv_icon.setImageResource(R.drawable.circle_yellow);break;
                    case "9":iv_icon.setImageResource(R.drawable.circle_orange);break;
                    case "10":iv_icon.setImageResource(R.drawable.circle_brown);break;
                    case "11":iv_icon.setImageResource(R.drawable.circle_grey);break;
                }
                iv_icon.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        final Item[] items = {
                                new Item(getString(R.string.subjects_color_red), R.drawable.circle_red),
                                new Item(getString(R.string.subjects_color_pink), R.drawable.circle_pink),
                                new Item(getString(R.string.subjects_color_purple), R.drawable.circle_purple),
                                new Item(getString(R.string.subjects_color_blue), R.drawable.circle_blue),
                                new Item(getString(R.string.subjects_color_teal), R.drawable.circle_teal),
                                new Item(getString(R.string.subjects_color_green), R.drawable.circle_green),
                                new Item(getString(R.string.subjects_color_lime), R.drawable.circle_lime),
                                new Item(getString(R.string.subjects_color_yellow), R.drawable.circle_yellow),
                                new Item(getString(R.string.subjects_color_orange), R.drawable.circle_orange),
                                new Item(getString(R.string.subjects_color_brown), R.drawable.circle_brown),
                                new Item(getString(R.string.subjects_color_grey), R.drawable.circle_grey),
                        };

                        ListAdapter adapter = new ArrayAdapter<Item>(
                                getActivity(),
                                android.R.layout.select_dialog_item,
                                android.R.id.text1,
                                items){
                            @NonNull
                            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                                //Use super class to create the View
                                View v = super.getView(position, convertView, parent);
                                TextView tv = (TextView)v.findViewById(android.R.id.text1);
                                tv.setTextSize(18);
                                tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);
                                //Add margin between image and text (support various screen densities)
                                int dp5 = (int) (24 * getResources().getDisplayMetrics().density + 0.5f);
                                tv.setCompoundDrawablePadding(dp5);

                                return v;
                            }
                        };

                        new AlertDialog.Builder(getActivity())
                                .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }
                                })
                                .setAdapter(adapter, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int item) {
                                        if (item == 0) {
                                            db.update(Integer.parseInt(_id),subject_title, subject_content, "1", subject_attachment, subject_creation);
                                            setSubjectsList();
                                        } else if (item == 1) {
                                            db.update(Integer.parseInt(_id),subject_title, subject_content, "2", subject_attachment, subject_creation);
                                            setSubjectsList();
                                        } else if (item == 2) {
                                            db.update(Integer.parseInt(_id),subject_title, subject_content, "3", subject_attachment, subject_creation);
                                            setSubjectsList();
                                        } else if (item == 3) {
                                            db.update(Integer.parseInt(_id),subject_title, subject_content, "4", subject_attachment, subject_creation);
                                            setSubjectsList();
                                        } else if (item == 4) {
                                            db.update(Integer.parseInt(_id),subject_title, subject_content, "5", subject_attachment, subject_creation);
                                            setSubjectsList();
                                        } else if (item == 5) {
                                            db.update(Integer.parseInt(_id),subject_title, subject_content, "6", subject_attachment, subject_creation);
                                            setSubjectsList();
                                        } else if (item == 6) {
                                            db.update(Integer.parseInt(_id),subject_title, subject_content, "7", subject_attachment, subject_creation);
                                            setSubjectsList();
                                        } else if (item == 7) {
                                            db.update(Integer.parseInt(_id),subject_title, subject_content, "8", subject_attachment, subject_creation);
                                            setSubjectsList();
                                        } else if (item == 8) {
                                            db.update(Integer.parseInt(_id),subject_title, subject_content, "9", subject_attachment, subject_creation);
                                            setSubjectsList();
                                        } else if (item == 9) {
                                            db.update(Integer.parseInt(_id),subject_title, subject_content, "10", subject_attachment, subject_creation);
                                            setSubjectsList();
                                        } else if (item == 10) {
                                            db.update(Integer.parseInt(_id),subject_title, subject_content, "11", subject_attachment, subject_creation);
                                            setSubjectsList();
                                        }
                                    }
                                }).show();
                    }
                });

                return v;
            }
        };

        lv.setAdapter(adapter);
        //onClick function
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String subject_title = row2.getString(row2.getColumnIndexOrThrow("subject_title"));
                final String subject_content = row2.getString(row2.getColumnIndexOrThrow("subject_content"));
                final String subject_attachment = row2.getString(row2.getColumnIndexOrThrow("subject_attachment"));

                final CharSequence[] options = {
                        getString(R.string.courseList_todo),
                        getString(R.string.courseList_note),
                        getString(R.string.courseList_random),
                        getString(R.string.courseList_course),
                        getString(R.string.bookmark_createEvent)};

                new AlertDialog.Builder(getActivity())
                        .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals(getString(R.string.courseList_random))) {

                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                                    View dialogView = View.inflate(getActivity(), R.layout.dialog_edit_entry, null);

                                    final EditText edit_title = (EditText) dialogView.findViewById(R.id.note_title_input);
                                    edit_title.setHint(R.string.title_hint);
                                    edit_title.setText(subject_title);

                                    final EditText edit_cont = (EditText) dialogView.findViewById(R.id.note_text_input);
                                    edit_cont.setHint(R.string.text_hint);

                                    builder.setView(dialogView);
                                    builder.setTitle(R.string.number_edit_entry);
                                    builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {


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

                                    dialog2.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //Do stuff, possibly set wantToCloseDialog to true then...
                                            Random_DbAdapter db = new Random_DbAdapter(getActivity());
                                            db.open();

                                            if(db.isExist(subject_title)){
                                                Snackbar.make(edit_title, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                                            } else {
                                                String inputTitle = edit_title.getText().toString().trim();
                                                String inputCont = edit_cont.getText().toString().trim();

                                                db.insert(inputTitle, inputCont, "", "", helper_main.createDate());
                                                ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
                                                viewPager.setCurrentItem(5);
                                                getActivity().setTitle(R.string.number_title);
                                                dialog2.cancel();
                                            }
                                        }
                                    });
                                    helper_main.showKeyboard(getActivity(),edit_title);
                                }

                                if (options[item].equals(getString(R.string.courseList_note))) {
                                    Notes_helper.newNote(getActivity(), subject_title, subject_content);
                                }

                                if (options[item].equals(getString(R.string.courseList_todo))) {
                                    Todo_helper.newTodo(getActivity(), subject_title, subject_content, getActivity().getString(R.string.note_content));
                                }

                                if (options[item].equals(getString(R.string.courseList_course))) {

                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                                    View dialogView = View.inflate(getActivity(), R.layout.dialog_edit_entry, null);

                                    final EditText edit_title = (EditText) dialogView.findViewById(R.id.note_title_input);
                                    edit_title.setHint(R.string.title_hint);
                                    edit_title.setText(subject_title);

                                    final EditText edit_cont = (EditText) dialogView.findViewById(R.id.note_text_input);
                                    edit_cont.setHint(R.string.text_hint);

                                    builder.setView(dialogView);
                                    builder.setTitle(R.string.number_edit_entry);
                                    builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {


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

                                    dialog2.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //Do stuff, possibly set wantToCloseDialog to true then...
                                            Courses_DbAdapter db = new Courses_DbAdapter(getActivity());
                                            db.open();

                                            if(db.isExist(subject_title)){
                                                Snackbar.make(edit_title, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                                            } else {
                                                String inputTitle = edit_title.getText().toString().trim();
                                                String inputCont = edit_cont.getText().toString().trim();

                                                db.insert(inputTitle, inputCont, "", "", helper_main.createDate());
                                                ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
                                                viewPager.setCurrentItem(7);
                                                getActivity().setTitle(R.string.courseList_title);
                                                dialog2.cancel();
                                            }
                                        }
                                    });
                                    helper_main.showKeyboard(getActivity(),edit_title);
                                }

                                if (options[item].equals (getString(R.string.bookmark_createEvent))) {
                                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                                    calIntent.setType("vnd.android.cursor.item/event");
                                    calIntent.putExtra(CalendarContract.Events.TITLE, subject_title);
                                    calIntent.putExtra(CalendarContract.Events.DESCRIPTION, subject_content);
                                    calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, subject_attachment);
                                    startActivity(calIntent);
                                }

                            }
                        }).show();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String subject_title = row2.getString(row2.getColumnIndexOrThrow("subject_title"));
                final String subject_content = row2.getString(row2.getColumnIndexOrThrow("subject_content"));
                final String subject_icon = row2.getString(row2.getColumnIndexOrThrow("subject_icon"));
                final String subject_attachment = row2.getString(row2.getColumnIndexOrThrow("subject_attachment"));

                final CharSequence[] options = {
                        getString(R.string.number_edit_entry),
                        getString(R.string.subjects_copy),
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

                                    LayoutInflater inflater = getActivity().getLayoutInflater();

                                    final ViewGroup nullParent = null;
                                    View dialogView = inflater.inflate(R.layout.dialog_edit_subject, nullParent);

                                    titleInput = (EditText) dialogView.findViewById(R.id.subject_title_);
                                    titleInput.setSelection(titleInput.getText().length());
                                    titleInput.setText(subject_title);
                                    teacherInput = (EditText) dialogView.findViewById(R.id.subject_teacher);
                                    teacherInput.setText(subject_content);
                                    roomInput = (EditText) dialogView.findViewById(R.id.subject_room);
                                    roomInput.setText(subject_attachment);

                                    helper_main.showKeyboard(getActivity(),titleInput);

                                    final ImageButton be = (ImageButton) dialogView.findViewById(R.id.imageButtonPri);
                                    assert be != null;

                                    switch (subject_icon) {
                                        case "1":be.setImageResource(R.drawable.circle_red);sharedPref.edit().putString("subject_color", "1").apply();break;
                                        case "2":be.setImageResource(R.drawable.circle_pink);sharedPref.edit().putString("subject_color", "2").apply();break;
                                        case "3":be.setImageResource(R.drawable.circle_purple);sharedPref.edit().putString("subject_color", "3").apply();break;
                                        case "4":be.setImageResource(R.drawable.circle_blue);sharedPref.edit().putString("subject_color", "4").apply();break;
                                        case "5":be.setImageResource(R.drawable.circle_teal);sharedPref.edit().putString("subject_color", "5").apply();break;
                                        case "6":be.setImageResource(R.drawable.circle_green);sharedPref.edit().putString("subject_color", "6").apply();break;
                                        case "7":be.setImageResource(R.drawable.circle_lime);sharedPref.edit().putString("subject_color", "7").apply();break;
                                        case "8":be.setImageResource(R.drawable.circle_yellow);sharedPref.edit().putString("subject_color", "8").apply();break;
                                        case "9":be.setImageResource(R.drawable.circle_orange);sharedPref.edit().putString("subject_color", "9").apply();break;
                                        case "10":be.setImageResource(R.drawable.circle_brown);sharedPref.edit().putString("subject_color", "10").apply();break;
                                        case "11":be.setImageResource(R.drawable.circle_grey);sharedPref.edit().putString("subject_color", "11").apply();break;
                                    }

                                    be.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {

                                            final Item[] items = {
                                                    new Item(getString(R.string.subjects_color_red), R.drawable.circle_red),
                                                    new Item(getString(R.string.subjects_color_pink), R.drawable.circle_pink),
                                                    new Item(getString(R.string.subjects_color_purple), R.drawable.circle_purple),
                                                    new Item(getString(R.string.subjects_color_blue), R.drawable.circle_blue),
                                                    new Item(getString(R.string.subjects_color_teal), R.drawable.circle_teal),
                                                    new Item(getString(R.string.subjects_color_green), R.drawable.circle_green),
                                                    new Item(getString(R.string.subjects_color_lime), R.drawable.circle_lime),
                                                    new Item(getString(R.string.subjects_color_yellow), R.drawable.circle_yellow),
                                                    new Item(getString(R.string.subjects_color_orange), R.drawable.circle_orange),
                                                    new Item(getString(R.string.subjects_color_brown), R.drawable.circle_brown),
                                                    new Item(getString(R.string.subjects_color_grey), R.drawable.circle_grey),
                                            };

                                            ListAdapter adapter = new ArrayAdapter<Item>(
                                                    getActivity(),
                                                    android.R.layout.select_dialog_item,
                                                    android.R.id.text1,
                                                    items){
                                                @NonNull
                                                public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                                                    //Use super class to create the View
                                                    View v = super.getView(position, convertView, parent);
                                                    TextView tv = (TextView)v.findViewById(android.R.id.text1);
                                                    tv.setTextSize(18);
                                                    tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);
                                                    //Add margin between image and text (support various screen densities)
                                                    int dp5 = (int) (24 * getActivity().getResources().getDisplayMetrics().density + 0.5f);
                                                    tv.setCompoundDrawablePadding(dp5);

                                                    return v;
                                                }
                                            };

                                            new android.app.AlertDialog.Builder(getActivity())
                                                    .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                            dialog.cancel();
                                                        }
                                                    })
                                                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int item) {
                                                            if (item == 0) {
                                                                be.setImageResource(R.drawable.circle_green);sharedPref.edit().putString("subject_color", "1").apply();
                                                            } else if (item == 1) {
                                                                be.setImageResource(R.drawable.circle_yellow);sharedPref.edit().putString("subject_color", "2").apply();
                                                            } else if (item == 2) {
                                                                be.setImageResource(R.drawable.circle_red);sharedPref.edit().putString("subject_color", "3").apply();
                                                            } else if (item == 3) {
                                                                be.setImageResource(R.drawable.circle_yellow);sharedPref.edit().putString("subject_color", "4").apply();
                                                            } else if (item == 4) {
                                                                be.setImageResource(R.drawable.circle_red);sharedPref.edit().putString("subject_color", "5").apply();
                                                            } else if (item == 5) {
                                                                be.setImageResource(R.drawable.circle_yellow);sharedPref.edit().putString("subject_color", "6").apply();
                                                            } else if (item == 6) {
                                                                be.setImageResource(R.drawable.circle_red);sharedPref.edit().putString("subject_color", "7").apply();
                                                            } else if (item == 7) {
                                                                be.setImageResource(R.drawable.circle_yellow);sharedPref.edit().putString("subject_color", "8").apply();
                                                            } else if (item == 8) {
                                                                be.setImageResource(R.drawable.circle_red);sharedPref.edit().putString("subject_color", "9").apply();
                                                            } else if (item == 9) {
                                                                be.setImageResource(R.drawable.circle_yellow);sharedPref.edit().putString("subject_color", "10").apply();
                                                            } else if (item == 10) {
                                                                be.setImageResource(R.drawable.circle_red);sharedPref.edit().putString("subject_color", "11").apply();
                                                            }
                                                        }
                                                    }).show();
                                        }
                                    });

                                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                                    builder.setTitle(R.string.subjects_edit);
                                    builder.setView(dialogView);
                                    builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            Subject_DbAdapter db = new Subject_DbAdapter(getActivity());
                                            db.open();

                                            String inputTitle = titleInput.getText().toString().trim();
                                            String inputTeacher = teacherInput.getText().toString().trim();
                                            String inputRoom = roomInput.getText().toString().trim();

                                            db.update(Integer.parseInt(_id), inputTitle, inputTeacher, sharedPref.getString("subject_color", ""), inputRoom, "");
                                            dialog.dismiss();
                                            setSubjectsList();
                                            Snackbar.make(lv, R.string.bookmark_added, Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                                    builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.cancel();
                                        }
                                    });

                                    final android.support.v7.app.AlertDialog dialog2 = builder.create();
                                    dialog2.show();
                                }

                                if (options[item].equals(getString(R.string.subjects_copy))) {

                                    LayoutInflater inflater = getActivity().getLayoutInflater();

                                    final ViewGroup nullParent = null;
                                    View dialogView = inflater.inflate(R.layout.dialog_edit_subject, nullParent);

                                    titleInput = (EditText) dialogView.findViewById(R.id.subject_title_);
                                    titleInput.setSelection(titleInput.getText().length());
                                    titleInput.setText(subject_title);
                                    teacherInput = (EditText) dialogView.findViewById(R.id.subject_teacher);
                                    teacherInput.setText(subject_content);
                                    roomInput = (EditText) dialogView.findViewById(R.id.subject_room);
                                    roomInput.setText(subject_attachment);

                                    helper_main.showKeyboard(getActivity(),titleInput);

                                    final ImageButton be = (ImageButton) dialogView.findViewById(R.id.imageButtonPri);
                                    assert be != null;

                                    switch (subject_icon) {
                                        case "1":be.setImageResource(R.drawable.circle_red);sharedPref.edit().putString("subject_color", "1").apply();break;
                                        case "2":be.setImageResource(R.drawable.circle_pink);sharedPref.edit().putString("subject_color", "2").apply();break;
                                        case "3":be.setImageResource(R.drawable.circle_purple);sharedPref.edit().putString("subject_color", "3").apply();break;
                                        case "4":be.setImageResource(R.drawable.circle_blue);sharedPref.edit().putString("subject_color", "4").apply();break;
                                        case "5":be.setImageResource(R.drawable.circle_teal);sharedPref.edit().putString("subject_color", "5").apply();break;
                                        case "6":be.setImageResource(R.drawable.circle_green);sharedPref.edit().putString("subject_color", "6").apply();break;
                                        case "7":be.setImageResource(R.drawable.circle_lime);sharedPref.edit().putString("subject_color", "7").apply();break;
                                        case "8":be.setImageResource(R.drawable.circle_yellow);sharedPref.edit().putString("subject_color", "8").apply();break;
                                        case "9":be.setImageResource(R.drawable.circle_orange);sharedPref.edit().putString("subject_color", "9").apply();break;
                                        case "10":be.setImageResource(R.drawable.circle_brown);sharedPref.edit().putString("subject_color", "10").apply();break;
                                        case "11":be.setImageResource(R.drawable.circle_grey);sharedPref.edit().putString("subject_color", "11").apply();break;
                                    }

                                    be.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {

                                            final Item[] items = {
                                                    new Item(getString(R.string.subjects_color_red), R.drawable.circle_red),
                                                    new Item(getString(R.string.subjects_color_pink), R.drawable.circle_pink),
                                                    new Item(getString(R.string.subjects_color_purple), R.drawable.circle_purple),
                                                    new Item(getString(R.string.subjects_color_blue), R.drawable.circle_blue),
                                                    new Item(getString(R.string.subjects_color_teal), R.drawable.circle_teal),
                                                    new Item(getString(R.string.subjects_color_green), R.drawable.circle_green),
                                                    new Item(getString(R.string.subjects_color_lime), R.drawable.circle_lime),
                                                    new Item(getString(R.string.subjects_color_yellow), R.drawable.circle_yellow),
                                                    new Item(getString(R.string.subjects_color_orange), R.drawable.circle_orange),
                                                    new Item(getString(R.string.subjects_color_brown), R.drawable.circle_brown),
                                                    new Item(getString(R.string.subjects_color_grey), R.drawable.circle_grey),
                                            };

                                            ListAdapter adapter = new ArrayAdapter<Item>(
                                                    getActivity(),
                                                    android.R.layout.select_dialog_item,
                                                    android.R.id.text1,
                                                    items){
                                                @NonNull
                                                public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                                                    //Use super class to create the View
                                                    View v = super.getView(position, convertView, parent);
                                                    TextView tv = (TextView)v.findViewById(android.R.id.text1);
                                                    tv.setTextSize(18);
                                                    tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);
                                                    //Add margin between image and text (support various screen densities)
                                                    int dp5 = (int) (24 * getActivity().getResources().getDisplayMetrics().density + 0.5f);
                                                    tv.setCompoundDrawablePadding(dp5);

                                                    return v;
                                                }
                                            };

                                            new android.app.AlertDialog.Builder(getActivity())
                                                    .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                            dialog.cancel();
                                                        }
                                                    })
                                                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int item) {
                                                            if (item == 0) {
                                                                be.setImageResource(R.drawable.circle_green);sharedPref.edit().putString("subject_color", "1").apply();
                                                            } else if (item == 1) {
                                                                be.setImageResource(R.drawable.circle_yellow);sharedPref.edit().putString("subject_color", "2").apply();
                                                            } else if (item == 2) {
                                                                be.setImageResource(R.drawable.circle_red);sharedPref.edit().putString("subject_color", "3").apply();
                                                            } else if (item == 3) {
                                                                be.setImageResource(R.drawable.circle_yellow);sharedPref.edit().putString("subject_color", "4").apply();
                                                            } else if (item == 4) {
                                                                be.setImageResource(R.drawable.circle_red);sharedPref.edit().putString("subject_color", "5").apply();
                                                            } else if (item == 5) {
                                                                be.setImageResource(R.drawable.circle_yellow);sharedPref.edit().putString("subject_color", "6").apply();
                                                            } else if (item == 6) {
                                                                be.setImageResource(R.drawable.circle_red);sharedPref.edit().putString("subject_color", "7").apply();
                                                            } else if (item == 7) {
                                                                be.setImageResource(R.drawable.circle_yellow);sharedPref.edit().putString("subject_color", "8").apply();
                                                            } else if (item == 8) {
                                                                be.setImageResource(R.drawable.circle_red);sharedPref.edit().putString("subject_color", "9").apply();
                                                            } else if (item == 9) {
                                                                be.setImageResource(R.drawable.circle_yellow);sharedPref.edit().putString("subject_color", "10").apply();
                                                            } else if (item == 10) {
                                                                be.setImageResource(R.drawable.circle_red);sharedPref.edit().putString("subject_color", "11").apply();
                                                            }
                                                        }
                                                    }).show();
                                        }
                                    });

                                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                                    builder.setTitle(R.string.subjects_edit);
                                    builder.setView(dialogView);
                                    builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            Subject_DbAdapter db = new Subject_DbAdapter(getActivity());
                                            db.open();

                                            String inputTitle = titleInput.getText().toString().trim();
                                            String inputTeacher = teacherInput.getText().toString().trim();
                                            String inputRoom = roomInput.getText().toString().trim();

                                            if(db.isExist(inputTitle)){
                                                Snackbar.make(titleInput, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                                            }else{
                                                db.insert(inputTitle, inputTeacher, sharedPref.getString("subject_color", ""), inputRoom, helper_main.createDate());
                                                dialog.dismiss();
                                                setSubjectsList();
                                            }

                                        }
                                    });
                                    builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.cancel();
                                        }
                                    });

                                    final android.support.v7.app.AlertDialog dialog2 = builder.create();
                                    dialog2.show();
                                }

                                if (options[item].equals(getString(R.string.bookmark_remove_bookmark))) {

                                    Snackbar snackbar = Snackbar
                                            .make(lv, R.string.note_remove_confirmation, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.toast_yes, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    db.delete(Integer.parseInt(_id));
                                                    setSubjectsList();
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
    public void onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_sort).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_help:
                helper_main.switchToActivity(getActivity(), Subjects_Help.class, false);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class Item{
        public final String text;
        public final int icon;
        Item(String text, Integer icon) {
            this.text = text;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}