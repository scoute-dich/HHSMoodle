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

package de.baumann.hhsmoodle.data_courses;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.data_random.Random_DbAdapter;
import de.baumann.hhsmoodle.data_notes.Notes_DbAdapter;
import de.baumann.hhsmoodle.data_subjects.Subject_DbAdapter;
import de.baumann.hhsmoodle.data_todo.Todo_DbAdapter;
import de.baumann.hhsmoodle.helper.class_CustomViewPager;
import de.baumann.hhsmoodle.helper.helper_main;
import filechooser.ChooserDialog;

public class Courses_Fragment extends Fragment {

    //calling variables
    private Courses_DbAdapter db;
    private ListView lv = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_screen_notes, container, false);

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
        fab.setImageResource(R.drawable.account_multiple_plus);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CharSequence[] options = {
                        getString(R.string.courseList_fromText),
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
                                if (options[item].equals(getString(R.string.courseList_fromText))) {
                                    String startDir = Environment.getExternalStorageDirectory() + "/HHS_Moodle/";

                                    new ChooserDialog().with(getActivity())
                                            .withStartFile(startDir)
                                            .withFilter(false, false, "txt")
                                            .withChosenListener(new ChooserDialog.Result() {
                                                @Override
                                                public void onChoosePath(final String path, final File pathFile) {

                                                    StringBuilder text = new StringBuilder();
                                                    final String fileName = pathFile.getAbsolutePath().substring(pathFile.getAbsolutePath().lastIndexOf("/")+1);
                                                    final String fileNameWE = fileName.substring(0, fileName.lastIndexOf("."));

                                                    try {
                                                        BufferedReader br = new BufferedReader(new FileReader(pathFile));
                                                        String line;

                                                        while ((line = br.readLine()) != null) {
                                                            text.append(line);
                                                            text.append('\n');
                                                        }
                                                        br.close();
                                                    }
                                                    catch (IOException e) {
                                                        Snackbar.make(lv, R.string.number_error_read, Snackbar.LENGTH_LONG).show();
                                                        e.printStackTrace();
                                                    }

                                                    if(db.isExist(fileNameWE)){
                                                        Snackbar.make(lv, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                                                    }else{
                                                        String textAdd = text.substring(0, text.length()-1);
                                                        db.insert(fileNameWE, textAdd, "", "", helper_main.createDate());
                                                        setCoursesList();
                                                    }
                                                }
                                            })
                                            .build()
                                            .show();
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
                                                setCoursesList();
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

        //calling Notes_DbAdapter
        db = new Courses_DbAdapter(getActivity());
        db.open();

        setCoursesList();
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            getActivity().setTitle(R.string.courseList_title);
            setCoursesList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setCoursesList();
    }

    private void setCoursesList() {

        //display data
        final int layoutstyle=R.layout.list_item_notes;
        int[] xml_id = new int[] {
                R.id.textView_title_notes,
                R.id.textView_des_notes,
                R.id.textView_create_notes
        };
        String[] column = new String[] {
                "courses_title",
                "courses_content",
                "courses_creation"
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

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String courses_title = row2.getString(row2.getColumnIndexOrThrow("courses_title"));
                final String courses_content = row2.getString(row2.getColumnIndexOrThrow("courses_content"));

                final CharSequence[] options = {
                        getString(R.string.courseList_todo),
                        getString(R.string.courseList_note),
                        getString(R.string.courseList_random),
                        getString(R.string.courseList_subject)};

                new android.app.AlertDialog.Builder(getActivity())
                        .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals(getString(R.string.courseList_random))) {

                                    Random_DbAdapter db = new Random_DbAdapter(getActivity());
                                    db.open();

                                    if(db.isExist(courses_title)){
                                        Snackbar.make(lv, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                                    } else {
                                        db.insert(courses_title, courses_content, "", "", helper_main.createDate());
                                        ViewPager viewPager = (class_CustomViewPager) getActivity().findViewById(R.id.viewpager);
                                        viewPager.setCurrentItem(5);
                                        getActivity().setTitle(R.string.number_title);
                                    }
                                }

                                if (options[item].equals(getString(R.string.courseList_note))) {

                                    Notes_DbAdapter db = new Notes_DbAdapter(getActivity());
                                    db.open();

                                    if(db.isExist(courses_title)){
                                        Snackbar.make(lv, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                                    } else {
                                        db.insert(courses_title, courses_content, "1", "", helper_main.createDate());
                                        ViewPager viewPager = (class_CustomViewPager) getActivity().findViewById(R.id.viewpager);
                                        viewPager.setCurrentItem(3);
                                        getActivity().setTitle(R.string.title_notes);
                                    }
                                }

                                if (options[item].equals(getString(R.string.courseList_todo))) {

                                    Todo_DbAdapter db = new Todo_DbAdapter(getActivity());
                                    db.open();

                                    if(db.isExist(courses_title)){
                                        Snackbar.make(lv, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                                    } else {
                                        db.insert(courses_title, courses_content, "3", "true", helper_main.createDate());
                                        ViewPager viewPager = (class_CustomViewPager) getActivity().findViewById(R.id.viewpager);
                                        viewPager.setCurrentItem(2);
                                        getActivity().setTitle(R.string.todo_title);
                                    }
                                }

                                if (options[item].equals(getString(R.string.courseList_subject))) {

                                    Subject_DbAdapter db = new Subject_DbAdapter(getActivity());
                                    db.open();

                                    if(db.isExist(courses_title)){
                                        Snackbar.make(lv, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                                    } else {
                                        Date date = new Date();
                                        DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd_HH-mm-ss", Locale.getDefault());
                                        String creation =  dateFormat.format(date);
                                        db.insert(courses_title, "", "3", "", creation);
                                        ViewPager viewPager = (class_CustomViewPager) getActivity().findViewById(R.id.viewpager);
                                        viewPager.setCurrentItem(8);
                                        getActivity().setTitle(R.string.subjects_title);
                                    }
                                }

                            }
                        }).show();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String courses_title = row2.getString(row2.getColumnIndexOrThrow("courses_title"));
                final String courses_content = row2.getString(row2.getColumnIndexOrThrow("courses_content"));
                final String courses_icon = row2.getString(row2.getColumnIndexOrThrow("courses_icon"));
                final String courses_attachment = row2.getString(row2.getColumnIndexOrThrow("courses_attachment"));
                final String courses_creation = row2.getString(row2.getColumnIndexOrThrow("courses_creation"));

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
                                    edit_title.setText(courses_title);

                                    builder.setView(dialogView);
                                    builder.setTitle(R.string.bookmark_edit_title);
                                    builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            String inputTag = edit_title.getText().toString().trim();
                                            db.update(Integer.parseInt(_id),inputTag, courses_content, courses_icon, courses_attachment, courses_creation);
                                            setCoursesList();
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
                                    edit_title.setText(courses_content);

                                    builder.setView(dialogView);
                                    builder.setTitle(R.string.number_edit_entry);
                                    builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            String inputTag = edit_title.getText().toString().trim();
                                            db.update(Integer.parseInt(_id),courses_title, inputTag, courses_icon, courses_attachment, courses_creation);
                                            setCoursesList();
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
                                                    setCoursesList();
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
                final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity())
                        .setTitle(R.string.courseList_title)
                        .setMessage(helper_main.textSpannable(getString(R.string.helpCourse_text)))
                        .setPositiveButton(getString(R.string.toast_yes), null);
                dialog.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}