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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
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
import android.view.inputmethod.InputMethodManager;
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
import de.baumann.hhsmoodle.data_todo.Todo_helper;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.popup.Popup_courseList;

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
    private boolean isFABOpen=false;
    private ViewPager viewPager;

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
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);

        ImageButton ib_hideKeyboard =(ImageButton) rootView.findViewById(R.id.ib_hideKeyboard);
        ib_hideKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filter.getText().length() > 0) {
                    filter.setText("");
                } else {
                    setTitle();
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    imgHeader.setVisibility(View.VISIBLE);
                    filter_layout.setVisibility(View.GONE);
                    setNotesList();
                }
            }
        });

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
                sharedPref.edit().putString("handleTextCreate", helper_main.createDate()).apply();
                helper_main.switchToActivity(getActivity(), Activity_EditNote.class, false);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                helper_main.isOpened(getActivity());
                sharedPref.edit().putString("handleTextCreate", helper_main.createDate()).apply();
                Intent mainIntent = new Intent(getActivity(), Popup_courseList.class);
                mainIntent.setAction("courseList_note");
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
        if (!sharedPref.getString("search_byCourse", "").isEmpty() && viewPager.getCurrentItem() == 3) {
            String search = sharedPref.getString("search_byCourse", "");
            sharedPref.edit().putString("filter_noteBY", "note_title").apply();
            getActivity().setTitle(getString(R.string.todo_title) + " | " + search);
            setNotesList();
            filter_layout.setVisibility(View.VISIBLE);
            imgHeader.setVisibility(View.GONE);
            filter.setText(search);
            sharedPref.edit().putString("search_byCourse", "").apply();
        } else {
            setNotesList();
        }
    }

    public void doBack() {
        //BackPressed in activity will call this;
        if(isFABOpen){
            closeFABMenu();
        } if (filter_layout.getVisibility() == View.VISIBLE) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(filter_layout.getWindowToken(), 0);
            imgHeader.setVisibility(View.VISIBLE);
            filter_layout.setVisibility(View.GONE);
            setNotesList();
        } else {
            helper_main.onClose(getActivity());
        }
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

                switch (note_icon) {
                    case "3":
                        iv_icon.setImageResource(R.drawable.circle_green);
                        break;
                    case "2":
                        iv_icon.setImageResource(R.drawable.circle_yellow);
                        break;
                    case "1":
                        iv_icon.setImageResource(R.drawable.circle_red);
                        break;
                }

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

                        final Item[] items = {
                                new Item(getString(R.string.note_priority_0), R.drawable.circle_green),
                                new Item(getString(R.string.note_priority_1), R.drawable.circle_yellow),
                                new Item(getString(R.string.note_priority_2), R.drawable.circle_red),
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
                                            db.update(Integer.parseInt(_id),note_title, note_content, "3", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 1) {
                                            db.update(Integer.parseInt(_id),note_title, note_content, "2", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 2) {
                                            db.update(Integer.parseInt(_id),note_title, note_content, "1", note_attachment, note_creation);
                                            setNotesList();
                                        }
                                    }
                                }).show();
                    }
                });
                iv_attachment.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
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
        //onClick function
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {

                if(isFABOpen){
                    closeFABMenu();
                }

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

                switch (note_icon) {
                    case "3":
                        be.setImageResource(R.drawable.circle_green);
                        break;
                    case "2":
                        be.setImageResource(R.drawable.circle_yellow);
                        break;
                    case "1":
                        be.setImageResource(R.drawable.circle_red);
                        break;
                }

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

                                if (options[item].equals (getString(R.string.todo_menu))) {
                                    Todo_helper.newTodo(getActivity(), note_title, note_content, getActivity().getString(R.string.note_content));
                                }

                                if (options[item].equals (getString(R.string.count_create))) {
                                    Count_helper.newCount(getActivity(), note_title, note_content, getActivity().getString(R.string.note_content), false);
                                }

                                if (options[item].equals (getString(R.string.bookmark_createEvent))) {
                                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                                    calIntent.setType("vnd.android.cursor.item/event");
                                    calIntent.putExtra(CalendarContract.Events.TITLE, note_title);
                                    calIntent.putExtra(CalendarContract.Events.DESCRIPTION, note_content);
                                    startActivity(calIntent);
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
                            }
                        }).show();

                return true;
            }
        });
    }

    private static class Item{
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.sort_notification).setVisible(false);
        menu.findItem(R.id.sort_icon).setVisible(false);
        menu.findItem(R.id.sort_ext).setVisible(false);
        menu.findItem(R.id.filter_teacher).setVisible(false);
        menu.findItem(R.id.filter_room).setVisible(false);
        menu.findItem(R.id.filter_url).setVisible(false);
        menu.findItem(R.id.filter_ext).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_help:
                helper_main.switchToActivity(getActivity(), Notes_Help.class, false);
                return true;

            case R.id.filter_title:
                sharedPref.edit().putString("filter_noteBY", "note_title").apply();
                setNotesList();
                filter_layout.setVisibility(View.VISIBLE);
                imgHeader.setVisibility(View.GONE);
                filter.setText("");
                filter.setHint(R.string.action_filter_title);
                filter.requestFocus();
                helper_main.showKeyboard(getActivity(), filter);
                return true;
            case R.id.filter_content:
                sharedPref.edit().putString("filter_noteBY", "note_content").apply();
                setNotesList();
                filter_layout.setVisibility(View.VISIBLE);
                imgHeader.setVisibility(View.GONE);
                filter.setText("");
                filter.setHint(R.string.action_filter_cont);
                filter.requestFocus();
                helper_main.showKeyboard(getActivity(), filter);
                return true;
            case R.id.filter_course:
                helper_main.isOpened(getActivity());
                Intent mainIntent = new Intent(getActivity(), Popup_courseList.class);
                mainIntent.setAction("search_byCourse");
                startActivity(mainIntent);
                return true;

            case R.id.filter_today:
                getActivity().setTitle(getString(R.string.title_notes) + " | " + getString(R.string.filter_today));
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar cal = Calendar.getInstance();
                final String search = dateFormat.format(cal.getTime());
                sharedPref.edit().putString("filter_noteBY", "note_creation").apply();
                setNotesList();
                filter_layout.setVisibility(View.VISIBLE);
                imgHeader.setVisibility(View.GONE);
                filter.setText(search);
                return true;
            case R.id.filter_yesterday:
                getActivity().setTitle(getString(R.string.title_notes) + " | " + getString(R.string.filter_yesterday));
                DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar cal2 = Calendar.getInstance();
                cal2.add(Calendar.DATE, -1);
                final String search2 = dateFormat2.format(cal2.getTime());
                sharedPref.edit().putString("filter_noteBY", "note_creation").apply();
                setNotesList();
                filter_layout.setVisibility(View.VISIBLE);
                imgHeader.setVisibility(View.GONE);
                filter.setText(search2);
                return true;
            case R.id.filter_before:
                getActivity().setTitle(getString(R.string.title_notes) + " | " + getString(R.string.filter_before));
                DateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar cal3 = Calendar.getInstance();
                cal3.add(Calendar.DATE, -2);
                final String search3 = dateFormat3.format(cal3.getTime());
                sharedPref.edit().putString("filter_noteBY", "note_creation").apply();
                setNotesList();
                filter_layout.setVisibility(View.VISIBLE);
                imgHeader.setVisibility(View.GONE);
                filter.setText(search3);
                return true;
            case R.id.filter_month:
                getActivity().setTitle(getString(R.string.title_notes) + " | " + getString(R.string.filter_month));
                DateFormat dateFormat4 = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
                Calendar cal4 = Calendar.getInstance();
                final String search4 = dateFormat4.format(cal4.getTime());
                sharedPref.edit().putString("filter_noteBY", "note_creation").apply();
                setNotesList();
                filter_layout.setVisibility(View.VISIBLE);
                imgHeader.setVisibility(View.GONE);
                filter.setText(search4);
                return true;
            case R.id.filter_own:
                getActivity().setTitle(getString(R.string.title_notes) + " | " + getString(R.string.filter_own));
                sharedPref.edit().putString("filter_noteBY", "note_creation").apply();
                setNotesList();
                filter_layout.setVisibility(View.VISIBLE);
                imgHeader.setVisibility(View.GONE);
                filter.setText("");
                filter.setHint(R.string.action_filter_create);
                filter.requestFocus();
                helper_main.showKeyboard(getActivity(), filter);
                return true;
            case R.id.filter_clear:
                setTitle();
                filter.setText("");
                setNotesList();
                return true;

            case R.id.filter_att:
                sharedPref.edit().putString("filter_noteBY", "note_attachment").apply();
                setNotesList();
                filter_layout.setVisibility(View.VISIBLE);
                imgHeader.setVisibility(View.GONE);
                filter.setText("");
                filter.setHint(R.string.action_filter_att);
                filter.requestFocus();
                helper_main.showKeyboard(getActivity(), filter);
                return true;

            case R.id.sort_title:
                sharedPref.edit().putString("sortDB", "title").apply();
                setTitle();
                setNotesList();
                return true;
            case R.id.sort_pri:
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
            getActivity().setTitle(getString(R.string.title_notes) + " | " + getString(R.string.sort_pri));
        }  else if (sharedPref.getString("sortDB", "title").equals("create")) {
            getActivity().setTitle(getString(R.string.title_notes) + " | " + getString(R.string.sort_date));
        } else {
            getActivity().setTitle(getString(R.string.title_notes) + " | " + getString(R.string.sort_att));
        }
    }
}