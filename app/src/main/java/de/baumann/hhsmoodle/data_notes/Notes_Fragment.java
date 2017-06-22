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

package de.baumann.hhsmoodle.data_notes;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.activities.Activity_EditNote;
import de.baumann.hhsmoodle.data_count.Count_helper;
import de.baumann.hhsmoodle.data_random.Random_helper;
import de.baumann.hhsmoodle.data_todo.Todo_helper;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.popup.Popup_courseList;
import de.baumann.hhsmoodle.popup.Popup_subjects;

public class Notes_Fragment extends Fragment {

    //calling variables
    private Notes_DbAdapter db;
    private SimpleCursorAdapter adapter;

    private ListView lv = null;
    private EditText filter;
    private SharedPreferences sharedPref;
    private ImageView imgHeader;
    private RelativeLayout filter_layout;

    private FloatingActionButton fab;
    private LinearLayout fabLayout1;
    private LinearLayout fabLayout2;
    private LinearLayout fabLayout3;
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
                    setTitle();
                    helper_main.hideFilter(getActivity(), filter_layout, imgHeader);
                    setNotesList();
                }
            }
        });

        fabLayout1= (LinearLayout) rootView.findViewById(R.id.fabLayout1);
        fabLayout2= (LinearLayout) rootView.findViewById(R.id.fabLayout2);
        fabLayout3= (LinearLayout) rootView.findViewById(R.id.fabLayout3);
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
                sharedPref.edit().putString("handleTextCreate", helper_main.createDate()).apply();
                helper_main.switchToActivity(getActivity(), Activity_EditNote.class, false);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                sharedPref.edit().putString("handleTextCreate", helper_main.createDate()).apply();
                Intent mainIntent = new Intent(getActivity(), Popup_courseList.class);
                mainIntent.setAction("courseList_note");
                startActivity(mainIntent);
            }
        });

        FloatingActionButton fab3 = (FloatingActionButton) rootView.findViewById(R.id.fab3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                sharedPref.edit().putString("handleTextCreate", helper_main.createDate()).apply();
                Intent mainIntent = new Intent(getActivity(), Popup_subjects.class);
                mainIntent.setAction("subjectList_note");
                startActivity(mainIntent);
            }
        });

        //calling Notes_DbAdapter
        db = new Notes_DbAdapter(getActivity());
        db.open();

        setNotesList();
        setHasOptionsMenu(true);

        return rootView;
    }

    private void showFABMenu(){
        isFABOpen=true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fabLayout3.setVisibility(View.VISIBLE);

        fab.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        fabLayout3.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab.animate().rotationBy(-180);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0);
        fabLayout3.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                if(!isFABOpen){
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                    fabLayout3.setVisibility(View.GONE);
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
        if (!sharedPref.getString("search_byCourse", "").isEmpty() && viewPager.getCurrentItem() == 3) {
            String search = sharedPref.getString("search_byCourse", "");
            helper_main.changeFilter(getActivity(), "filter_noteBY", "note_title");
            setNotesList();
            helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                    search, getString(R.string.action_filter_course), false);
            sharedPref.edit().putString("search_byCourse", "").apply();
        } else if (!sharedPref.getString("search_bySubject", "").isEmpty() && viewPager.getCurrentItem() == 3) {
            String search = sharedPref.getString("search_bySubject", "");
            helper_main.changeFilter(getActivity(), "filter_noteBY", "note_title");
            setNotesList();
            helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                    search, getString(R.string.action_filter_subject), false);
            sharedPref.edit().putString("search_bySubject", "").apply();
        } else if (filter_layout.getVisibility() == View.GONE) {
                setNotesList();
        }
    }

    public void doBack() {
        //BackPressed in activity will call this;
        if(isFABOpen){
            closeFABMenu();
        } if (filter_layout.getVisibility() == View.VISIBLE) {
            helper_main.hideFilter(getActivity(), filter_layout, imgHeader);
            setNotesList();
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

    public void setNotesList() {

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
                "note_title",
                "note_content",
                "note_creation"
        };
        final Cursor row = db.fetchAllData(getActivity());
        adapter = new SimpleCursorAdapter(getActivity(), layoutstyle,row,column, xml_id, 0) {
            @Override
            public View getView (final int position, View convertView, ViewGroup parent) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String note_title = row2.getString(row2.getColumnIndexOrThrow("note_title"));
                final String note_content = row2.getString(row2.getColumnIndexOrThrow("note_content"));
                final String note_icon = row2.getString(row2.getColumnIndexOrThrow("note_icon"));
                final String note_attachment = row2.getString(row2.getColumnIndexOrThrow("note_attachment"));
                final String note_creation = row2.getString(row2.getColumnIndexOrThrow("note_creation"));

                View v = super.getView(position, convertView, parent);
                ImageView iv_icon = (ImageView) v.findViewById(R.id.icon_notes);
                ImageView iv_attachment = (ImageView) v.findViewById(R.id.att_notes);
                helper_main.switchIcon(getActivity(), note_icon, "note_icon", iv_icon);

                switch (note_attachment) {
                    case "":
                        iv_attachment.setVisibility(View.GONE);
                        break;
                    default:
                        iv_attachment.setVisibility(View.VISIBLE);
                        iv_attachment.setImageResource(R.drawable.ic_attachment);
                        break;
                }

                File file = new File(note_attachment);
                if (!file.exists()) {
                    iv_attachment.setVisibility(View.GONE);
                }

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
                                            db.update(Integer.parseInt(_id), note_title, note_content, "01", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 1) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "02", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 2) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "03", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 3) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "04", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 4) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "05", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 5) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "06", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 6) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "07", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 7) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "08", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 8) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "09", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 9) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "10", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 10) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "11", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 11) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "12", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 12) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "13", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 13) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "14", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 14) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "15", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 15) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "16", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 16) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "17", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 17) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "18", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 18) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "19", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 19) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "20", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 20) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "21", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 21) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "22", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 22) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "23", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 23) {
                                            db.update(Integer.parseInt(_id), note_title, note_content, "24", note_attachment, note_creation);
                                            setNotesList();
                                        }
                                    }
                                }).show();
                    }
                });
                iv_attachment.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        isEdited();
                        helper_main.openAtt(getActivity(), lv, note_attachment);
                    }
                });
                return v;
            }
        };

        //display data by filter
        final String note_search = sharedPref.getString("filter_noteBY", "note_title");
        sharedPref.edit().putString("filter_noteBY", "note_title").apply();
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
                final String note_title = row2.getString(row2.getColumnIndexOrThrow("note_title"));
                final String note_content = row2.getString(row2.getColumnIndexOrThrow("note_content"));
                final String note_icon = row2.getString(row2.getColumnIndexOrThrow("note_icon"));
                final String note_attachment = row2.getString(row2.getColumnIndexOrThrow("note_attachment"));
                final String note_creation = row2.getString(row2.getColumnIndexOrThrow("note_creation"));

                final Button attachment;
                final TextView textInput;

                LayoutInflater inflater = getActivity().getLayoutInflater();

                final ViewGroup nullParent = null;
                final View dialogView = inflater.inflate(R.layout.dialog_note_show, nullParent);

                final String attName = note_attachment.substring(note_attachment.lastIndexOf("/")+1);
                final String att = getString(R.string.app_att) + ": " + attName;

                attachment = (Button) dialogView.findViewById(R.id.button_att);
                if (attName.equals("")) {
                    attachment.setVisibility(View.GONE);
                } else {
                    attachment.setText(att);
                }

                textInput = (TextView) dialogView.findViewById(R.id.note_text_input);
                if (note_content.isEmpty()) {
                    textInput.setVisibility(View.GONE);
                } else {
                    textInput.setText(note_content);
                    Linkify.addLinks(textInput, Linkify.WEB_URLS);
                }

                attachment.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        helper_main.openAtt(getActivity(), textInput, note_attachment);
                    }
                });

                final ImageView be = (ImageView) dialogView.findViewById(R.id.imageButtonPri);
                final ImageView attImage = (ImageView) dialogView.findViewById(R.id.attImage);

                File file2 = new File(note_attachment);
                if (!file2.exists()) {
                    attachment.setVisibility(View.GONE);
                    attImage.setVisibility(View.GONE);
                } else if (note_attachment.contains(".gif") ||
                            note_attachment.contains(".bmp") ||
                            note_attachment.contains(".tiff") ||
                            note_attachment.contains(".png") ||
                            note_attachment.contains(".jpg") ||
                            note_attachment.contains(".JPG") ||
                            note_attachment.contains(".jpeg") ||
                            note_attachment.contains(".mpeg") ||
                            note_attachment.contains(".mp4") ||
                            note_attachment.contains(".3gp") ||
                            note_attachment.contains(".3g2") ||
                            note_attachment.contains(".avi") ||
                            note_attachment.contains(".flv") ||
                            note_attachment.contains(".h261") ||
                            note_attachment.contains(".h263") ||
                            note_attachment.contains(".h264") ||
                            note_attachment.contains(".asf") ||
                            note_attachment.contains(".wmv")) {
                        attImage.setVisibility(View.VISIBLE);

                        try {
                            Glide.with(getActivity())
                                    .load(note_attachment) // or URI/path
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(attImage); //imageView to set thumbnail to
                        } catch (Exception e) {
                            Log.w("HHS_Moodle", "Error load thumbnail", e);
                            attImage.setVisibility(View.GONE);
                        }

                        attImage.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                helper_main.openAtt(getActivity(), attImage, note_attachment);
                            }
                        });
                }

                helper_main.switchIcon(getActivity(), note_icon, "note_icon", be);

                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity())
                        .setTitle(note_title)
                        .setView(dialogView)
                        .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton(R.string.note_edit, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                sharedPref.edit()
                                        .putString("handleTextTitle", note_title)
                                        .putString("handleTextText", note_content)
                                        .putString("handleTextIcon", note_icon)
                                        .putString("handleTextSeqno", _id)
                                        .putString("handleTextAttachment", note_attachment)
                                        .putString("handleTextCreate", note_creation)
                                        .apply();
                                helper_main.switchToActivity(getActivity(), Activity_EditNote.class, false);
                            }
                        });
                dialog.show();
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
                final String note_title = row2.getString(row2.getColumnIndexOrThrow("note_title"));
                final String note_content = row2.getString(row2.getColumnIndexOrThrow("note_content"));
                final String note_icon = row2.getString(row2.getColumnIndexOrThrow("note_icon"));
                final String note_attachment = row2.getString(row2.getColumnIndexOrThrow("note_attachment"));
                final String note_creation = row2.getString(row2.getColumnIndexOrThrow("note_creation"));

                final CharSequence[] options = {
                        getString(R.string.number_edit_entry),
                        getString(R.string.bookmark_remove_bookmark),
                        getString(R.string.todo_share),
                        getString(R.string.todo_menu),
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
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals(getString(R.string.number_edit_entry))) {
                                    sharedPref.edit()
                                            .putString("handleTextTitle", note_title)
                                            .putString("handleTextText", note_content)
                                            .putString("handleTextIcon", note_icon)
                                            .putString("handleTextSeqno", _id)
                                            .putString("handleTextAttachment", note_attachment)
                                            .putString("handleTextCreate", note_creation)
                                            .apply();
                                    helper_main.switchToActivity(getActivity(), Activity_EditNote.class, false);
                                }

                                if (options[item].equals (getString(R.string.todo_share))) {
                                    File attachment = new File(note_attachment);
                                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, note_title);
                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, note_content);

                                    if (attachment.exists()) {
                                        Uri bmpUri = Uri.fromFile(attachment);
                                        sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                    }

                                    startActivity(Intent.createChooser(sharingIntent, (getString(R.string.note_share_2))));
                                }

                                if (options[item].equals(getString(R.string.bookmark_remove_bookmark))) {
                                    Snackbar snackbar = Snackbar
                                            .make(lv, R.string.note_remove_confirmation, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.toast_yes, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    db.delete(Integer.parseInt(_id));
                                                    setNotesList();
                                                }
                                            });
                                    snackbar.show();
                                }

                                if (options[item].equals (getString(R.string.todo_menu))) {
                                    Todo_helper.newTodo(getActivity(), note_title, note_content, note_icon, getString(R.string.note_content), false);
                                }

                                if (options[item].equals (getString(R.string.number_create))) {
                                    Random_helper.newRandom(getActivity(), note_title, note_content, note_icon, getActivity().getString(R.string.note_content), false);
                                }

                                if (options[item].equals (getString(R.string.count_create))) {
                                    Count_helper.newCount(getActivity(), note_title, note_content, note_icon,getActivity().getString(R.string.note_content), false);
                                }

                                if (options[item].equals (getString(R.string.bookmark_createEvent))) {
                                    helper_main.createCalendarEvent(getActivity(), note_title, note_content);
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
        menu.findItem(R.id.sort_notification).setVisible(false);
        menu.findItem(R.id.sort_ext).setVisible(false);
        menu.findItem(R.id.filter_teacher).setVisible(false);
        menu.findItem(R.id.filter_room).setVisible(false);
        menu.findItem(R.id.filter_url).setVisible(false);
        menu.findItem(R.id.filter_ext).setVisible(false);
        setTitle();
        setNotesList();
        helper_main.hideKeyboard(getActivity());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String search;

        switch (item.getItemId()) {

            case R.id.action_help:
                helper_main.switchToActivity(getActivity(), Notes_Help.class, false);
                return true;

            case R.id.filter_title_own:
                helper_main.changeFilter(getActivity(), "filter_noteBY", "note_title");
                setNotesList();
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        "", getString(R.string.action_filter_title), true);
                return true;
            case R.id.filter_content:
                helper_main.changeFilter(getActivity(), "filter_noteBY", "note_content");
                setNotesList();
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        "", getString(R.string.action_filter_cont), true);
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

            case R.id.filter_today:
                helper_main.changeFilter(getActivity(), "filter_noteBY", "note_creation");
                setNotesList();
                search = dateFormat.format(cal.getTime());
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        search, getString(R.string.action_filter_create), false);
                return true;
            case R.id.filter_yesterday:
                helper_main.changeFilter(getActivity(), "filter_noteBY", "note_creation");
                setNotesList();
                cal.add(Calendar.DATE, -1);
                search = dateFormat.format(cal.getTime());
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        search, getString(R.string.action_filter_create), false);
                return true;
            case R.id.filter_before:
                helper_main.changeFilter(getActivity(), "filter_noteBY", "note_creation");
                setNotesList();
                cal.add(Calendar.DATE, -2);
                search = dateFormat.format(cal.getTime());
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        search, getString(R.string.action_filter_create), false);
                return true;
            case R.id.filter_month:
                helper_main.changeFilter(getActivity(), "filter_noteBY", "note_creation");
                setNotesList();
                DateFormat dateFormatMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
                search = dateFormatMonth.format(cal.getTime());
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        search, getString(R.string.action_filter_create), false);
                return true;
            case R.id.filter_own:
                helper_main.changeFilter(getActivity(), "filter_noteBY", "note_creation");
                setNotesList();
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        "", getString(R.string.action_filter_create), false);
                return true;

            case R.id.filter_att:
                helper_main.changeFilter(getActivity(), "filter_noteBY", "note_attachment");
                setNotesList();
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        "", getString(R.string.action_filter_att), true);
                return true;

            case R.id.sort_title:
                sharedPref.edit().putString("sortDB", "title").apply();
                setTitle();
                setNotesList();
                return true;
            case R.id.sort_icon:
                sharedPref.edit().putString("sortDB", "icon").apply();
                setTitle();
                setNotesList();
                return true;
            case R.id.sort_creation:
                sharedPref.edit().putString("sortDB", "create").apply();
                setTitle();
                setNotesList();
                return true;
            case R.id.sort_attachment:
                sharedPref.edit().putString("sortDB", "attachment").apply();
                setTitle();
                setNotesList();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setTitle () {
        if (sharedPref.getString("sortDB", "title").equals("title")) {
            getActivity().setTitle(getString(R.string.title_notes) + " | " + getString(R.string.sort_title));
        } else if (sharedPref.getString("sortDB", "title").equals("icon")) {
            getActivity().setTitle(getString(R.string.title_notes) + " | " + getString(R.string.sort_icon));
        }  else if (sharedPref.getString("sortDB", "title").equals("create")) {
            getActivity().setTitle(getString(R.string.title_notes) + " | " + getString(R.string.sort_date));
        } else {
            getActivity().setTitle(getString(R.string.title_notes) + " | " + getString(R.string.sort_att));
        }
    }
}