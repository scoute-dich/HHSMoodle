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

package de.baumann.hhsmoodle.data_schedule;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.data_notes.Notes_helper;
import de.baumann.hhsmoodle.data_todo.Todo_DbAdapter;
import de.baumann.hhsmoodle.helper.class_CustomViewPager;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.popup.Popup_note;
import de.baumann.hhsmoodle.popup.Popup_subjects;
import de.baumann.hhsmoodle.popup.Popup_todo;

public class Schedule_Fragment extends Fragment {

    //calling variables
    private Schedule_DbAdapter db;
    private SimpleCursorAdapter adapter;

    private ListView lv = null;
    private EditText filter;
    private SharedPreferences sharedPref;
    private ImageView imgHeader;
    private RelativeLayout filter_layout;

    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_screen_notes, container, false);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        imgHeader = (ImageView) rootView.findViewById(R.id.imageView_header);
        if(imgHeader != null) {
            TypedArray images = getResources().obtainTypedArray(R.array.splash_images);
            int choice = (int) (Math.random() * images.length());
            imgHeader.setImageResource(images.getResourceId(choice, R.drawable.splash1));
            images.recycle();
        }

        filter_layout = (RelativeLayout) rootView.findViewById(R.id.filter_layout);
        filter_layout.setVisibility(View.GONE);
        lv = (ListView) rootView.findViewById(R.id.listNotes);
        filter = (EditText) rootView.findViewById(R.id.myFilter);

        ImageButton ib_hideKeyboard =(ImageButton) rootView.findViewById(R.id.ib_hideKeyboard);
        ib_hideKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                imgHeader.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                filter_layout.setVisibility(View.GONE);
                setScheduleList();
            }
        });

        //calling Notes_DbAdapter
        db = new Schedule_DbAdapter(getActivity());
        db.open();

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.refresh);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollToNow();
            }
        });

        setScheduleList();
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            getActivity().setTitle(R.string.schedule_title);
            setScheduleList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setScheduleList();
    }

    private void setScheduleList() {

        final int line = sharedPref.getInt("getLine", 1);
        //display data
        final int layoutstyle=R.layout.list_item_schedule;
        int[] xml_id = new int[] {
                R.id.textView_title_notes,
                R.id.textView_des_notes,
                R.id.att_notes,
                R.id.textView_create_notes
        };
        String[] column = new String[] {
                "schedule_title",
                "schedule_content",
                "schedule_attachment",
                "schedule_creation"
        };
        final Cursor row = db.fetchAllData();
        adapter = new SimpleCursorAdapter(getActivity(), layoutstyle,row,column, xml_id, 0) {
            @Override
            public View getView (final int position, View convertView, ViewGroup parent) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String schedule_title = row2.getString(row2.getColumnIndexOrThrow("schedule_title"));
                final String schedule_content = row2.getString(row2.getColumnIndexOrThrow("schedule_content"));
                final String schedule_icon = row2.getString(row2.getColumnIndexOrThrow("schedule_icon"));
                final String schedule_attachment = row2.getString(row2.getColumnIndexOrThrow("schedule_attachment"));
                final String schedule_creation = row2.getString(row2.getColumnIndexOrThrow("schedule_creation"));
                final String schedule_id = row2.getString(row2.getColumnIndexOrThrow("schedule_id"));

                View v = super.getView(position, convertView, parent);
                ImageView iv_icon = (ImageView) v.findViewById(R.id.icon_notes);

                if (position == line) {
                    v.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorAccent_trans));
                } else {
                    v.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.color_trans));
                }

                if (schedule_title.equals(getActivity().getString(R.string.schedule_weekend)) || schedule_title.equals(getActivity().getString(R.string.schedule_def_title))) {
                    sharedPref.edit().putString("hour_" + schedule_id, "false").apply();
                } else {
                    sharedPref.edit().putString("hour_" + schedule_id, "true").apply();
                }

                switch (schedule_icon) {
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
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "1", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 1) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "2", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 2) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "3", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 3) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "4", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 4) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "5", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 5) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "6", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 6) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "7", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 7) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "8", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 8) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "9", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 9) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "10", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 10) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "11", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        }
                                    }
                                }).show();
                    }
                });

                return v;
            }
        };

        //display data by filter
        final String note_search = sharedPref.getString("filter_scheduleBY", "schedule_title");
        sharedPref.edit().putString("filter_scheduleBY", "schedule_title").apply();
        filter.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }
        });
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                return db.fetchDataByFilter(constraint.toString(),note_search);
            }
        });

        lv.setAdapter(adapter);
        //onClick function
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String schedule_title = row2.getString(row2.getColumnIndexOrThrow("schedule_title"));

                final CharSequence[] options = {
                        getString(R.string.schedule_todo),
                        getString(R.string.schedule_notes)};
                new AlertDialog.Builder(getActivity())
                        .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals(getString(R.string.schedule_todo))) {
                                    sharedPref.edit().putString("filter_todo_subject", schedule_title).apply();
                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            Intent mainIntent = new Intent(getActivity(), Popup_todo.class);
                                            startActivity(mainIntent);
                                        }
                                    }, 200);
                                }
                                if (options[item].equals (getString(R.string.schedule_notes))) {
                                    sharedPref.edit().putString("filter_note_subject", schedule_title).apply();
                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            Intent mainIntent = new Intent(getActivity(), Popup_note.class);
                                            startActivity(mainIntent);
                                        }
                                    }, 200);
                                }
                            }
                        }).show();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String schedule_title = row2.getString(row2.getColumnIndexOrThrow("schedule_title"));
                final String schedule_content = row2.getString(row2.getColumnIndexOrThrow("schedule_content"));
                final String schedule_icon = row2.getString(row2.getColumnIndexOrThrow("schedule_icon"));
                final String schedule_attachment = row2.getString(row2.getColumnIndexOrThrow("schedule_attachment"));
                final String schedule_creation = row2.getString(row2.getColumnIndexOrThrow("schedule_creation"));
                final String schedule_id = row2.getString(row2.getColumnIndexOrThrow("schedule_id"));

                final CharSequence[] options = {
                        getString(R.string.number_edit_entry),
                        getString(R.string.todo_menu),
                        getString(R.string.bookmark_createNote),
                        getString(R.string.bookmark_createEvent)};
                new AlertDialog.Builder(getActivity())
                        .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @SuppressWarnings("ConstantConditions")
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals(getString(R.string.number_edit_entry))) {

                                    final CharSequence[] options = {
                                            getString(R.string.schedule_fromSubjectList),
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
                                                    if (options[item].equals(getString(R.string.schedule_fromSubjectList))) {
                                                        sharedPref.edit()
                                                                .putString("handleSubjectCreation", schedule_creation)
                                                                .putString("handleSubject_id", schedule_id)
                                                                .putString("handle_id", _id)
                                                                .apply();
                                                        helper_main.isOpened(getActivity());
                                                        Intent mainIntent = new Intent(getActivity(), Popup_subjects.class);
                                                        startActivity(mainIntent);
                                                    }

                                                    if (options[item].equals (getString(R.string.todo_from_new))) {

                                                        LayoutInflater inflater = getActivity().getLayoutInflater();

                                                        final ViewGroup nullParent = null;
                                                        View dialogView = inflater.inflate(R.layout.dialog_editsubject, nullParent);

                                                        final EditText titleInput = (EditText) dialogView.findViewById(R.id.subject_title_);
                                                        titleInput.setSelection(titleInput.getText().length());
                                                        titleInput.setText(schedule_title);
                                                        final EditText teacherInput = (EditText) dialogView.findViewById(R.id.subject_teacher);
                                                        teacherInput.setText(schedule_content);
                                                        final EditText roomInput = (EditText) dialogView.findViewById(R.id.subject_room);
                                                        roomInput.setText(schedule_attachment);

                                                        new Handler().postDelayed(new Runnable() {
                                                            public void run() {
                                                                helper_main.showKeyboard(getActivity(),titleInput);
                                                            }
                                                        }, 200);


                                                        final ImageButton be = (ImageButton) dialogView.findViewById(R.id.imageButtonPri);
                                                        assert be != null;

                                                        switch (schedule_icon) {
                                                            case "1":be.setImageResource(R.drawable.circle_red);sharedPref.edit().putString("schedule_color", "1").apply();break;
                                                            case "2":be.setImageResource(R.drawable.circle_pink);sharedPref.edit().putString("schedule_color", "2").apply();break;
                                                            case "3":be.setImageResource(R.drawable.circle_purple);sharedPref.edit().putString("schedule_color", "3").apply();break;
                                                            case "4":be.setImageResource(R.drawable.circle_blue);sharedPref.edit().putString("schedule_color", "4").apply();break;
                                                            case "5":be.setImageResource(R.drawable.circle_teal);sharedPref.edit().putString("schedule_color", "5").apply();break;
                                                            case "6":be.setImageResource(R.drawable.circle_green);sharedPref.edit().putString("schedule_color", "6").apply();break;
                                                            case "7":be.setImageResource(R.drawable.circle_lime);sharedPref.edit().putString("schedule_color", "7").apply();break;
                                                            case "8":be.setImageResource(R.drawable.circle_yellow);sharedPref.edit().putString("schedule_color", "8").apply();break;
                                                            case "9":be.setImageResource(R.drawable.circle_orange);sharedPref.edit().putString("schedule_color", "9").apply();break;
                                                            case "10":be.setImageResource(R.drawable.circle_brown);sharedPref.edit().putString("schedule_color", "10").apply();break;
                                                            case "11":be.setImageResource(R.drawable.circle_grey);sharedPref.edit().putString("schedule_color", "11").apply();break;
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
                                                                                    be.setImageResource(R.drawable.circle_red);sharedPref.edit().putString("schedule_color", "1").apply();
                                                                                } else if (item == 1) {
                                                                                    be.setImageResource(R.drawable.circle_pink);sharedPref.edit().putString("schedule_color", "2").apply();
                                                                                } else if (item == 2) {
                                                                                    be.setImageResource(R.drawable.circle_purple);sharedPref.edit().putString("schedule_color", "3").apply();
                                                                                } else if (item == 3) {
                                                                                    be.setImageResource(R.drawable.circle_blue);sharedPref.edit().putString("schedule_color", "4").apply();
                                                                                } else if (item == 4) {
                                                                                    be.setImageResource(R.drawable.circle_teal);sharedPref.edit().putString("schedule_color", "5").apply();
                                                                                } else if (item == 5) {
                                                                                    be.setImageResource(R.drawable.circle_green);sharedPref.edit().putString("schedule_color", "6").apply();
                                                                                } else if (item == 6) {
                                                                                    be.setImageResource(R.drawable.circle_lime);sharedPref.edit().putString("schedule_color", "7").apply();
                                                                                } else if (item == 7) {
                                                                                    be.setImageResource(R.drawable.circle_yellow);sharedPref.edit().putString("schedule_color", "8").apply();
                                                                                } else if (item == 8) {
                                                                                    be.setImageResource(R.drawable.circle_orange);sharedPref.edit().putString("schedule_color", "9").apply();
                                                                                } else if (item == 9) {
                                                                                    be.setImageResource(R.drawable.circle_brown);sharedPref.edit().putString("schedule_color", "10").apply();
                                                                                } else if (item == 10) {
                                                                                    be.setImageResource(R.drawable.circle_grey);sharedPref.edit().putString("schedule_color", "11").apply();
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

                                                                String inputTitle = titleInput.getText().toString().trim();
                                                                String inputTeacher = teacherInput.getText().toString().trim();
                                                                String inputRoom = roomInput.getText().toString().trim();

                                                                db.update(Integer.parseInt(_id), inputTitle, inputTeacher, sharedPref.getString("schedule_color", ""), inputRoom, schedule_creation, schedule_id);
                                                                dialog.dismiss();
                                                                setScheduleList();
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

                                                }
                                            }).show();
                                }

                                if (options[item].equals (getString(R.string.todo_menu))) {

                                    Todo_DbAdapter db = new Todo_DbAdapter(getActivity());
                                    db.open();

                                    if(db.isExist(schedule_title)){
                                        Snackbar.make(lv, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                                    } else {
                                        db.insert(schedule_title, "", "3", "true", helper_main.createDate());
                                        ViewPager viewPager = (class_CustomViewPager) getActivity().findViewById(R.id.viewpager);
                                        viewPager.setCurrentItem(2);
                                        getActivity().setTitle(R.string.todo_title);
                                        dialog.dismiss();
                                    }
                                }

                                if (options[item].equals (getString(R.string.bookmark_createEvent))) {
                                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                                    calIntent.setType("vnd.android.cursor.item/event");
                                    calIntent.putExtra(CalendarContract.Events.TITLE, schedule_title);
                                    calIntent.putExtra(CalendarContract.Events.DESCRIPTION, schedule_content);
                                    startActivity(calIntent);
                                }

                                if (options[item].equals (getString(R.string.bookmark_createNote))) {

                                    sharedPref.edit()
                                            .putString("handleTextTitle", schedule_title)
                                            .putString("handleTextText", "")
                                            .putString("handleTextCreate", helper_main.createDate())
                                            .putString("handleTextIcon", "")
                                            .putString("handleTextAttachment", "")
                                            .putString("handleTextSeqno", "")
                                            .apply();
                                    Notes_helper.newNote(getActivity());
                                }

                            }
                        }).show();
                return true;
            }
        });

        scrollToNow();
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

    private void scrollToNow() {
        final int line = sharedPref.getInt("getLine", 1);
        lv.setSelection(line);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_sort).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_help:
                helper_main.switchToActivity(getActivity(), Schedule_Help.class, false);
                return true;

            case R.id.action_filter:
                final CharSequence[] options = {
                        getActivity().getString(R.string.action_filter_title),
                        getActivity().getString(R.string.schedule_search_teacher),
                        getActivity().getString(R.string.schedule_search_room)};
                new AlertDialog.Builder(getActivity())
                        .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {

                                if (options[item].equals (getActivity().getString(R.string.action_filter_title))) {
                                    sharedPref.edit().putString("filter_scheduleBY", "schedule_title").apply();
                                    setScheduleList();
                                    filter_layout.setVisibility(View.VISIBLE);
                                    imgHeader.setVisibility(View.GONE);
                                    fab.setVisibility(View.GONE);
                                    filter.setText("");
                                    filter.setHint(R.string.action_filter_title);
                                    filter.requestFocus();
                                    helper_main.showKeyboard(getActivity(), filter);
                                }

                                if (options[item].equals(getActivity().getString(R.string.schedule_search_teacher))) {
                                    sharedPref.edit().putString("filter_scheduleBY", "schedule_content").apply();
                                    setScheduleList();
                                    filter_layout.setVisibility(View.VISIBLE);
                                    imgHeader.setVisibility(View.GONE);
                                    fab.setVisibility(View.GONE);
                                    filter.setText("");
                                    filter.setHint(R.string.schedule_search_teacher);
                                    filter.requestFocus();
                                    helper_main.showKeyboard(getActivity(), filter);
                                }

                                if (options[item].equals(getActivity().getString(R.string.schedule_search_room))) {
                                    sharedPref.edit().putString("filter_scheduleBY", "schedule_attachment").apply();
                                    setScheduleList();
                                    filter_layout.setVisibility(View.VISIBLE);
                                    imgHeader.setVisibility(View.GONE);
                                    fab.setVisibility(View.GONE);
                                    filter.setText("");
                                    filter.setHint(R.string.schedule_search_room);
                                    filter.requestFocus();
                                    helper_main.showKeyboard(getActivity(), filter);
                                }
                            }
                        }).show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}