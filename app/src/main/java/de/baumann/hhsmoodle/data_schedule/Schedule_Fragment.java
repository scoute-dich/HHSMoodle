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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import de.baumann.hhsmoodle.data_count.Count_helper;
import de.baumann.hhsmoodle.data_notes.Notes_helper;
import de.baumann.hhsmoodle.data_random.Random_helper;
import de.baumann.hhsmoodle.data_todo.Todo_helper;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.popup.Popup_courseList;
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
    private ViewPager viewPager;

    private int top;
    private int index;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_screen_notes, container, false);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPref.edit().putString("edit_yes", "").apply();

        imgHeader = (ImageView) rootView.findViewById(R.id.imageView_header);
        helper_main.setImageHeader(getActivity(), imgHeader);

        filter_layout = (RelativeLayout) rootView.findViewById(R.id.filter_layout);
        filter_layout.setVisibility(View.GONE);
        lv = (ListView) rootView.findViewById(R.id.listNotes);
        filter = (EditText) rootView.findViewById(R.id.myFilter);
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);

        ImageButton ib_hideKeyboard =(ImageButton) rootView.findViewById(R.id.ib_hideKeyboard);
        ib_hideKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filter.getText().length() > 0) {
                    filter.setText("");
                } else {
                    helper_main.hideFilter(getActivity(), filter_layout, imgHeader);
                    setScheduleList();
                }
            }
        });

        //calling Notes_DbAdapter
        db = new Schedule_DbAdapter(getActivity());
        db.open();

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.format_vertical_align_center);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollToNow();
            }
        });

        setScheduleList();
        scrollToNow();
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!sharedPref.getString("search_byCourse", "").isEmpty() && viewPager.getCurrentItem() == 4) {
            String search = sharedPref.getString("search_byCourse", "");
            helper_main.changeFilter(getActivity(), "filter_scheduleBY", "schedule_title");
            setScheduleList();
            helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                    search, getString(R.string.action_filter_course), false);
            sharedPref.edit().putString("search_byCourse", "").apply();
        } else if (!sharedPref.getString("search_bySubject", "").isEmpty() && viewPager.getCurrentItem() == 4) {
            String search = sharedPref.getString("search_bySubject", "");
            helper_main.changeFilter(getActivity(), "filter_scheduleBY", "schedule_title");
            setScheduleList();
            helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                    search, getString(R.string.action_filter_subject), false);
            sharedPref.edit().putString("search_bySubject", "").apply();
        } else if (viewPager.getCurrentItem() == 4) {
             setScheduleList();
        }
    }

    public void doBack() {
        if (filter_layout.getVisibility() == View.VISIBLE) {
            helper_main.hideFilter(getActivity(), filter_layout, imgHeader);
            setScheduleList();
        } else {
            helper_main.onClose(getActivity());
        }
    }

    private void isEdited () {
        sharedPref.edit().putString("edit_yes", "true").apply();
        index = lv.getFirstVisiblePosition();
        View v = lv.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - lv.getPaddingTop());
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

                helper_main.switchIcon(getActivity(), schedule_icon,"schedule_color", iv_icon);

                iv_icon.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        isEdited();
                        final helper_main.Item[] items = {
                                new helper_main.Item(getString(R.string.text_tit_11), R.drawable.ic_school_grey600_48dp),
                                new helper_main.Item(getString(R.string.text_tit_1), R.drawable.ic_view_dashboard_grey600_48dp),
                                new helper_main.Item(getString(R.string.text_tit_2), R.drawable.ic_face_profile_grey600_48dp),
                                new helper_main.Item(getString(R.string.text_tit_8), R.drawable.ic_calendar_grey600_48dp),
                                new helper_main.Item(getString(R.string.text_tit_3), R.drawable.ic_chart_areaspline_grey600_48dp),
                                new helper_main.Item(getString(R.string.text_tit_4), R.drawable.ic_bell_grey600_48dp),
                                new helper_main.Item(getString(R.string.text_tit_5), R.drawable.ic_settings_grey600_48dp),
                                new helper_main.Item(getString(R.string.text_tit_6), R.drawable.ic_web_grey600_48dp),
                                new helper_main.Item(getString(R.string.text_tit_7), R.drawable.ic_magnify_grey600_48dp),
                                new helper_main.Item(getString(R.string.title_notes), R.drawable.ic_pencil_grey600_48dp),
                                new helper_main.Item(getString(R.string.text_tit_9), R.drawable.ic_check_grey600_48dp),
                                new helper_main.Item(getString(R.string.text_tit_10), R.drawable.ic_clock_grey600_48dp),
                                new helper_main.Item(getString(R.string.title_bookmarks), R.drawable.ic_bookmark_grey600_48dp),
                                new helper_main.Item(getString(R.string.subjects_color_red), R.drawable.circle_red),
                                new helper_main.Item(getString(R.string.subjects_color_pink), R.drawable.circle_pink),
                                new helper_main.Item(getString(R.string.subjects_color_purple), R.drawable.circle_purple),
                                new helper_main.Item(getString(R.string.subjects_color_blue), R.drawable.circle_blue),
                                new helper_main.Item(getString(R.string.subjects_color_teal), R.drawable.circle_teal),
                                new helper_main.Item(getString(R.string.subjects_color_green), R.drawable.circle_green),
                                new helper_main.Item(getString(R.string.subjects_color_lime), R.drawable.circle_lime),
                                new helper_main.Item(getString(R.string.subjects_color_yellow), R.drawable.circle_yellow),
                                new helper_main.Item(getString(R.string.subjects_color_orange), R.drawable.circle_orange),
                                new helper_main.Item(getString(R.string.subjects_color_brown), R.drawable.circle_brown),
                                new helper_main.Item(getString(R.string.subjects_color_grey), R.drawable.circle_grey),
                        };

                        ListAdapter adapter = new ArrayAdapter<helper_main.Item>(
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
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "01", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 1) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "02", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 2) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "03", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 3) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "04", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 4) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "05", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 5) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "06", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 6) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "07", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 7) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "08", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 8) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "09", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 9) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "10", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 10) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "11", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 11) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "12", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 12) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "13", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 13) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "14", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 14) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "15", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 15) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "16", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 16) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "17", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 17) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "18", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 18) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "19", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 19) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "20", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 20) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "21", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 21) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "22", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 22) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "23", schedule_attachment, schedule_creation, schedule_id);
                                            setScheduleList();
                                        } else if (item == 23) {
                                            db.update(Integer.parseInt(_id),schedule_title, schedule_content, "24", schedule_attachment, schedule_creation, schedule_id);
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
        if (sharedPref.getString("edit_yes", "").equals("true")) {
            sharedPref.edit().putString("edit_yes", "").apply();
            lv.setSelectionFromTop(index, top);
        } else {
            scrollToNow();
        }
        //onClick function
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {

                isEdited();
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
                                            helper_main.switchToActivity(getActivity(), Popup_todo.class, false);
                                        }
                                    }, 200);
                                }
                                if (options[item].equals (getString(R.string.schedule_notes))) {
                                    sharedPref.edit().putString("filter_note_subject", schedule_title).apply();
                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            helper_main.switchToActivity(getActivity(), Popup_note.class, false);
                                        }
                                    }, 200);
                                }
                            }
                        }).show();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                isEdited();
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
                        getString(R.string.bookmark_remove_bookmark),
                        getString(R.string.todo_share),
                        getString(R.string.todo_menu),
                        getString(R.string.bookmark_createNote),
                        getString(R.string.number_create),
                        getString(R.string.count_create),
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
                                                                .putInt("scroll", Integer.parseInt(schedule_id))
                                                                .putString("handleSubjectCreation", schedule_creation)
                                                                .putString("handleSubject_id", schedule_id)
                                                                .putString("handle_id", _id)
                                                                .apply();
                                                        helper_main.switchToActivity(getActivity(), Popup_subjects.class, false);
                                                    }

                                                    if (options[item].equals (getString(R.string.todo_from_new))) {

                                                        LayoutInflater inflater = getActivity().getLayoutInflater();

                                                        final ViewGroup nullParent = null;
                                                        View dialogView = inflater.inflate(R.layout.dialog_edit_subject, nullParent);

                                                        final EditText titleInput = (EditText) dialogView.findViewById(R.id.subject_title_);
                                                        titleInput.setSelection(titleInput.getText().length());
                                                        titleInput.setText(schedule_title);
                                                        final EditText teacherInput = (EditText) dialogView.findViewById(R.id.subject_teacher);
                                                        teacherInput.setText(schedule_content);
                                                        final EditText roomInput = (EditText) dialogView.findViewById(R.id.subject_room);
                                                        roomInput.setText(schedule_attachment);

                                                        helper_main.showKeyboard(getActivity(),titleInput);

                                                        final ImageButton be = (ImageButton) dialogView.findViewById(R.id.imageButtonPri);
                                                        assert be != null;

                                                        helper_main.switchIcon(getActivity(), schedule_icon,"schedule_color", be);

                                                        be.setOnClickListener(new View.OnClickListener() {

                                                            @Override
                                                            public void onClick(View arg0) {

                                                                final helper_main.Item[] items = {
                                                                        new helper_main.Item(getString(R.string.text_tit_11), R.drawable.ic_school_grey600_48dp),
                                                                        new helper_main.Item(getString(R.string.text_tit_1), R.drawable.ic_view_dashboard_grey600_48dp),
                                                                        new helper_main.Item(getString(R.string.text_tit_2), R.drawable.ic_face_profile_grey600_48dp),
                                                                        new helper_main.Item(getString(R.string.text_tit_8), R.drawable.ic_calendar_grey600_48dp),
                                                                        new helper_main.Item(getString(R.string.text_tit_3), R.drawable.ic_chart_areaspline_grey600_48dp),
                                                                        new helper_main.Item(getString(R.string.text_tit_4), R.drawable.ic_bell_grey600_48dp),
                                                                        new helper_main.Item(getString(R.string.text_tit_5), R.drawable.ic_settings_grey600_48dp),
                                                                        new helper_main.Item(getString(R.string.text_tit_6), R.drawable.ic_web_grey600_48dp),
                                                                        new helper_main.Item(getString(R.string.text_tit_7), R.drawable.ic_magnify_grey600_48dp),
                                                                        new helper_main.Item(getString(R.string.title_notes), R.drawable.ic_pencil_grey600_48dp),
                                                                        new helper_main.Item(getString(R.string.text_tit_9), R.drawable.ic_check_grey600_48dp),
                                                                        new helper_main.Item(getString(R.string.text_tit_10), R.drawable.ic_clock_grey600_48dp),
                                                                        new helper_main.Item(getString(R.string.title_bookmarks), R.drawable.ic_bookmark_grey600_48dp),
                                                                        new helper_main.Item(getString(R.string.subjects_color_red), R.drawable.circle_red),
                                                                        new helper_main.Item(getString(R.string.subjects_color_pink), R.drawable.circle_pink),
                                                                        new helper_main.Item(getString(R.string.subjects_color_purple), R.drawable.circle_purple),
                                                                        new helper_main.Item(getString(R.string.subjects_color_blue), R.drawable.circle_blue),
                                                                        new helper_main.Item(getString(R.string.subjects_color_teal), R.drawable.circle_teal),
                                                                        new helper_main.Item(getString(R.string.subjects_color_green), R.drawable.circle_green),
                                                                        new helper_main.Item(getString(R.string.subjects_color_lime), R.drawable.circle_lime),
                                                                        new helper_main.Item(getString(R.string.subjects_color_yellow), R.drawable.circle_yellow),
                                                                        new helper_main.Item(getString(R.string.subjects_color_orange), R.drawable.circle_orange),
                                                                        new helper_main.Item(getString(R.string.subjects_color_brown), R.drawable.circle_brown),
                                                                        new helper_main.Item(getString(R.string.subjects_color_grey), R.drawable.circle_grey),
                                                                };

                                                                ListAdapter adapter = new ArrayAdapter<helper_main.Item>(
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
                                                                                helper_main.switchIconDialog(getActivity(), item, "schedule_color", be);
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

                                if (options[item].equals (getString(R.string.todo_share))) {
                                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, schedule_title);
                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, schedule_content);
                                    startActivity(Intent.createChooser(sharingIntent, (getString(R.string.note_share_2))));
                                }

                                if (options[item].equals (getString(R.string.bookmark_remove_bookmark))) {
                                    Snackbar snackbar = Snackbar
                                            .make(lv, R.string.note_remove_confirmation, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.toast_yes, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    sharedPref.edit().putString("edit_yes", "true").apply();
                                                    db.update(Integer.parseInt(_id), getString(R.string.schedule_def_teacher), getString(R.string.schedule_def_teacher), "24", getString(R.string.schedule_def_teacher), schedule_creation, schedule_id);
                                                    setScheduleList();
                                                }
                                            });
                                    snackbar.show();
                                }

                                if (options[item].equals (getString(R.string.number_create))) {
                                    Random_helper.newRandom(getActivity(), schedule_title, schedule_attachment, schedule_icon, getActivity().getString(R.string.note_content), false);
                                }

                                if (options[item].equals (getString(R.string.todo_menu))) {
                                    Todo_helper.newTodo(getActivity(), schedule_title, schedule_content, schedule_icon,getActivity().getString(R.string.note_content), false);
                                }

                                if (options[item].equals (getString(R.string.count_create))) {
                                    Count_helper.newCount(getActivity(), schedule_title, schedule_content, schedule_icon,getActivity().getString(R.string.note_content), false);
                                }

                                if (options[item].equals (getString(R.string.bookmark_createEvent))) {
                                    helper_main.createCalendarEvent(getActivity(), schedule_title, schedule_content);
                                }

                                if (options[item].equals (getString(R.string.bookmark_createNote))) {
                                    Notes_helper.newNote(getActivity(),schedule_title,schedule_content, schedule_icon, getString(R.string.note_content), false);
                                }

                            }
                        }).show();
                return true;
            }
        });
        Schedule_helper.setAlarm(getActivity());
    }

    private void scrollToNow() {
        final int line = sharedPref.getInt("getLine", 1);
        lv.setSelection(line -1);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_schedule, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_sort).setVisible(false);
        menu.findItem(R.id.filter_content).setVisible(false);
        menu.findItem(R.id.filter_creation).setVisible(false);
        menu.findItem(R.id.filter_url).setVisible(false);
        menu.findItem(R.id.filter_att).setVisible(false);
        menu.findItem(R.id.filter_ext).setVisible(false);

        if (sharedPref.getBoolean ("silent_mode", false)){
            menu.findItem(R.id.action_silent).setIcon(R.drawable.bell_off_light);
        } else {
            menu.findItem(R.id.action_silent).setIcon(R.drawable.bell_ring_light);
        }

        getActivity().setTitle(R.string.schedule_title);
        setScheduleList();
        helper_main.hideKeyboard(getActivity());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_help:
                helper_main.switchToActivity(getActivity(), Schedule_Help.class, false);
                return true;

            case R.id.filter_title_own:
                helper_main.changeFilter(getActivity(), "filter_scheduleBY", "schedule_title");
                setScheduleList();
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        "", getString(R.string.action_filter_title), true);
                return true;
            case R.id.filter_teacher:
                helper_main.changeFilter(getActivity(), "filter_scheduleBY", "schedule_content");
                setScheduleList();
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        "", getString(R.string.schedule_search_teacher), true);
                return true;
            case R.id.filter_room:
                helper_main.changeFilter(getActivity(), "filter_scheduleBY", "schedule_attachment");
                setScheduleList();
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        "", getString(R.string.schedule_search_room), true);
                return true;
            case R.id.filter_course:
                Intent mainIntent = new Intent(getActivity(), Popup_courseList.class);
                mainIntent.setAction("search_byCourse");
                startActivity(mainIntent);
                return true;
            case R.id.filter_subject:
                Intent mainIntent2 = new Intent(getActivity(), Popup_subjects.class);
                mainIntent2.setAction("search_bySubject");
                startActivity(mainIntent2);
                return true;

            case R.id.action_silent:
                if (sharedPref.getBoolean ("silent_mode", false)){
                    sharedPref.edit().putBoolean("silent_mode", false).apply();
                    getActivity().invalidateOptionsMenu();
                } else {
                    sharedPref.edit().putBoolean("silent_mode", true).apply();
                    getActivity().invalidateOptionsMenu();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}