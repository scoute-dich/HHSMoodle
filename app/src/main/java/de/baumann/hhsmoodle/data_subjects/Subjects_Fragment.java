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

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.LinearLayout;
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
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.popup.Popup_courseList;

public class Subjects_Fragment extends Fragment {

    //calling variables
    private Subject_DbAdapter db;
    private ListView lv = null;
    private SharedPreferences sharedPref;

    private EditText titleInput;
    private EditText teacherInput;
    private EditText roomInput;

    private  String icon;

    private FloatingActionButton fab;
    private LinearLayout fabLayout1;
    private LinearLayout fabLayout2;
    private boolean isFABOpen=false;
    private ViewPager viewPager;

    private int top;
    private int index;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_screen_notes, container, false);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPref.edit().putString("subject_title", "").apply();
        sharedPref.edit().putString("subject_text", "").apply();
        sharedPref.edit().putString("subject_seqno", "").apply();
        sharedPref.edit().putString("subject_icon", "").apply();
        sharedPref.edit().putString("subject_create", "").apply();
        sharedPref.edit().putString("subject_attachment", "").apply();

        ImageView imgHeader = (ImageView) rootView.findViewById(R.id.imageView_header);
        helper_main.setImageHeader(getActivity(), imgHeader);

        RelativeLayout filter_layout = (RelativeLayout) rootView.findViewById(R.id.filter_layout);
        filter_layout.setVisibility(View.GONE);
        lv = (ListView) rootView.findViewById(R.id.listNotes);
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);

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
                editSubject();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                Intent mainIntent = new Intent(getActivity(), Popup_courseList.class);
                mainIntent.setAction("courseList_subject");
                startActivity(mainIntent);
            }
        });

        //calling Notes_DbAdapter
        db = new Subject_DbAdapter(getActivity());
        db.open();

        setSubjectsList();
        setHasOptionsMenu(true);

        return rootView;
    }

    private void editSubject() {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final ViewGroup nullParent = null;
        View dialogView = inflater.inflate(R.layout.dialog_edit_subject, nullParent);

        titleInput = (EditText) dialogView.findViewById(R.id.subject_title_);
        titleInput.setSelection(titleInput.getText().length());
        titleInput.setText("");
        teacherInput = (EditText) dialogView.findViewById(R.id.subject_teacher);
        teacherInput.setText(sharedPref.getString("subject_title", ""));
        roomInput = (EditText) dialogView.findViewById(R.id.subject_room);
        roomInput.setText("");

        icon = "19";

        helper_main.showKeyboard(getActivity(),titleInput);

        final ImageButton be = (ImageButton) dialogView.findViewById(R.id.imageButtonPri);
        assert be != null;
        helper_main.switchIcon (getActivity(), icon, "subject_icon", be);

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
                                helper_main.switchIconDialog(getActivity(), item, "subject_icon", be);
                                icon = sharedPref.getString("subject_icon", "19");
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

                if(db.isExist(creation)){
                    Snackbar.make(titleInput, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                }else{
                    db.insert(helper_main.secString(inputTitle), helper_main.secString(inputTeacher), icon, helper_main.secString(inputRoom), creation);
                    dialog.dismiss();
                    setSubjectsList();
                    sharedPref.edit().putString("subject_title", "").apply();
                    sharedPref.edit().putString("subject_text", "").apply();
                    sharedPref.edit().putString("subject_seqno", "").apply();
                    sharedPref.edit().putString("subject_icon", "").apply();
                    sharedPref.edit().putString("subject_create", "").apply();
                    sharedPref.edit().putString("subject_attachment", "").apply();
                }
            }
        });
        builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                sharedPref.edit().putString("subject_title", "").apply();
                sharedPref.edit().putString("subject_text", "").apply();
                sharedPref.edit().putString("subject_seqno", "").apply();
                sharedPref.edit().putString("subject_icon", "").apply();
                sharedPref.edit().putString("subject_create", "").apply();
                sharedPref.edit().putString("subject_attachment", "").apply();
                dialog.cancel();
            }
        });

        final android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
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

    @Override
    public void onResume() {
        super.onResume();

        if (!sharedPref.getString("subject_title", "").isEmpty()) {
            editSubject();
            sharedPref.edit().putString("subject_title", "").apply();
        } else if (viewPager.getCurrentItem() == 10) {
            setSubjectsList();
        }
    }

    private void isEdited () {
        sharedPref.edit().putString("edit_yes", "true").apply();
        index = lv.getFirstVisiblePosition();
        View v = lv.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - lv.getPaddingTop());
    }

    private void setSubjectsList() {

        if(isFABOpen){
            closeFABMenu();
        }

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

                Cursor row = (Cursor) lv.getItemAtPosition(position);
                final String _id = row.getString(row.getColumnIndexOrThrow("_id"));
                final String subject_title = row.getString(row.getColumnIndexOrThrow("subject_title"));
                final String subject_content = row.getString(row.getColumnIndexOrThrow("subject_content"));
                final String subject_icon = row.getString(row.getColumnIndexOrThrow("subject_icon"));
                final String subject_attachment = row.getString(row.getColumnIndexOrThrow("subject_attachment"));
                final String subject_creation = row.getString(row.getColumnIndexOrThrow("subject_creation"));

                View v = super.getView(position, convertView, parent);
                ImageView iv_icon = (ImageView) v.findViewById(R.id.icon_notes);
                helper_main.switchIcon (getActivity(), subject_icon, "subject_icon", iv_icon);
                iv_icon.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        isEdited();
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
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "01", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 1) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "02", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 2) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "03", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 3) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "04", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 4) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "05", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 5) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "06", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 6) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "07", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 7) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "08", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 8) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "09", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 9) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "10", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 10) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "11", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 11) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "12", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 12) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "13", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 13) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "14", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 14) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "15", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 15) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "16", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 16) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "17", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 17) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "18", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 18) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "19", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 19) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "20", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 20) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "21", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 21) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "22", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 22) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "23", helper_main.secString(subject_attachment), subject_creation);
                                            setSubjectsList();
                                        } else if (item == 23) {
                                            db.update(Integer.parseInt(_id),helper_main.secString(subject_title), helper_main.secString(subject_content), "24", helper_main.secString(subject_attachment), subject_creation);
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
        lv.setSelectionFromTop(index, top);
        //onClick function
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {

                if(isFABOpen){
                    closeFABMenu();
                }

                isEdited();
                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String subject_title = row2.getString(row2.getColumnIndexOrThrow("subject_title"));
                final String subject_content = row2.getString(row2.getColumnIndexOrThrow("subject_content"));
                final String subject_icon = row2.getString(row2.getColumnIndexOrThrow("subject_icon"));
                final String subject_attachment = row2.getString(row2.getColumnIndexOrThrow("subject_attachment"));
                final String subject_creation = row2.getString(row2.getColumnIndexOrThrow("subject_creation"));

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
                helper_main.switchIcon (getActivity(), subject_icon, "subject_icon", be);

                be.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

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
                                        helper_main.switchIconDialog(getActivity(), item, "subject_icon", be);
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

                        db.update(Integer.parseInt(_id), helper_main.secString(inputTitle), helper_main.secString(inputTeacher), sharedPref.getString("subject_icon", ""), helper_main.secString(inputRoom), subject_creation);
                        dialog.dismiss();
                        setSubjectsList();
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

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if(isFABOpen){
                    closeFABMenu();
                }

                isEdited();
                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String subject_title = row2.getString(row2.getColumnIndexOrThrow("subject_title"));
                final String subject_content = row2.getString(row2.getColumnIndexOrThrow("subject_content"));
                final String subject_icon = row2.getString(row2.getColumnIndexOrThrow("subject_icon"));
                final String subject_attachment = row2.getString(row2.getColumnIndexOrThrow("subject_attachment"));
                final String subject_creation = row2.getString(row2.getColumnIndexOrThrow("subject_creation"));

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
                                    helper_main.switchIcon (getActivity(), subject_icon, "subject_icon", be);

                                    be.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {

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
                                                            helper_main.switchIconDialog(getActivity(), item, "subject_icon", be);
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

                                            db.update(Integer.parseInt(_id), helper_main.secString(inputTitle), helper_main.secString(inputTeacher), sharedPref.getString("subject_icon", ""), helper_main.secString(inputRoom), subject_creation);
                                            dialog.dismiss();
                                            setSubjectsList();
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
                                    helper_main.switchIcon (getActivity(), subject_icon, "subject_icon", be);

                                    be.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {

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
                                                            if (item == 0) {
                                                                be.setImageResource(R.drawable.school_grey_dark);sharedPref.edit().putString("subject_icon", "1").apply();
                                                            } else if (item == 1) {
                                                                be.setImageResource(R.drawable.ic_view_dashboard_grey600_48dp);sharedPref.edit().putString("subject_icon", "2").apply();
                                                            } else if (item == 2) {
                                                                be.setImageResource(R.drawable.ic_face_profile_grey600_48dp);sharedPref.edit().putString("subject_icon", "3").apply();
                                                            } else if (item == 3) {
                                                                be.setImageResource(R.drawable.ic_calendar_grey600_48dp);sharedPref.edit().putString("subject_icon", "4").apply();
                                                            } else if (item == 4) {
                                                                be.setImageResource(R.drawable.ic_chart_areaspline_grey600_48dp);sharedPref.edit().putString("subject_icon", "5").apply();
                                                            } else if (item == 5) {
                                                                be.setImageResource(R.drawable.ic_bell_grey600_48dp);sharedPref.edit().putString("subject_icon", "6").apply();
                                                            } else if (item == 6) {
                                                                be.setImageResource(R.drawable.ic_settings_grey600_48dp);sharedPref.edit().putString("subject_icon", "7").apply();
                                                            } else if (item == 7) {
                                                                be.setImageResource(R.drawable.ic_web_grey600_48dp);sharedPref.edit().putString("subject_icon", "8").apply();
                                                            } else if (item == 8) {
                                                                be.setImageResource(R.drawable.ic_magnify_grey600_48dp);sharedPref.edit().putString("subject_icon", "9").apply();
                                                            } else if (item == 9) {
                                                                be.setImageResource(R.drawable.ic_pencil_grey600_48dp);sharedPref.edit().putString("subject_icon", "10").apply();
                                                            } else if (item == 10) {
                                                                be.setImageResource(R.drawable.ic_check_grey600_48dp);sharedPref.edit().putString("subject_icon", "11").apply();
                                                            } else if (item == 11) {
                                                                be.setImageResource(R.drawable.ic_clock_grey600_48dp);sharedPref.edit().putString("subject_icon", "12").apply();
                                                            } else if (item == 12) {
                                                                be.setImageResource(R.drawable.ic_bookmark_grey600_48dp);sharedPref.edit().putString("subject_icon", "13").apply();
                                                            } else if (item == 13) {
                                                                be.setImageResource(R.drawable.circle_red);sharedPref.edit().putString("subject_icon", "14").apply();
                                                            } else if (item == 14) {
                                                                be.setImageResource(R.drawable.circle_pink);sharedPref.edit().putString("subject_icon", "15").apply();
                                                            } else if (item == 15) {
                                                                be.setImageResource(R.drawable.circle_purple);sharedPref.edit().putString("subject_icon", "16").apply();
                                                            } else if (item == 16) {
                                                                be.setImageResource(R.drawable.circle_blue);sharedPref.edit().putString("subject_icon", "17").apply();
                                                            } else if (item == 17) {
                                                                be.setImageResource(R.drawable.circle_teal);sharedPref.edit().putString("subject_icon", "18").apply();
                                                            } else if (item == 18) {
                                                                be.setImageResource(R.drawable.circle_green);sharedPref.edit().putString("subject_icon", "19").apply();
                                                            } else if (item == 19) {
                                                                be.setImageResource(R.drawable.circle_lime);sharedPref.edit().putString("subject_icon", "20").apply();
                                                            } else if (item == 20) {
                                                                be.setImageResource(R.drawable.circle_yellow);sharedPref.edit().putString("subject_icon", "21").apply();
                                                            } else if (item == 21) {
                                                                be.setImageResource(R.drawable.circle_orange);sharedPref.edit().putString("subject_icon", "22").apply();
                                                            } else if (item == 22) {
                                                                be.setImageResource(R.drawable.circle_brown);sharedPref.edit().putString("subject_icon", "23").apply();
                                                            } else if (item == 23) {
                                                                be.setImageResource(R.drawable.circle_grey);sharedPref.edit().putString("subject_icon", "24").apply();
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

                                            String creation =  helper_main.createDateSecond();

                                            if(db.isExist(creation)){
                                                Snackbar.make(titleInput, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                                            }else{
                                                try {
                                                    db.insert(inputTitle, inputTeacher, sharedPref.getString("subject_icon", "19"), inputRoom, creation);
                                                    setSubjectsList();
                                                }
                                                catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                dialog.dismiss();
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
        getActivity().setTitle(R.string.subjects_title);
        setSubjectsList();
        helper_main.hideKeyboard(getActivity());
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
}