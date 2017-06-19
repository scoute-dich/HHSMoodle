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

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.activities.Activity_course;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.popup.Popup_files;

public class Courses_Fragment extends Fragment {

    //calling variables
    private Courses_DbAdapter db;
    private ListView lv = null;

    private FloatingActionButton fab;
    private LinearLayout fabLayout1;
    private LinearLayout fabLayout2;
    private boolean isFABOpen=false;
    private SharedPreferences sharedPref;

    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_screen_notes, container, false);

        ImageView imgHeader = (ImageView) rootView.findViewById(R.id.imageView_header);
        helper_main.setImageHeader(getActivity(), imgHeader);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        RelativeLayout filter_layout = (RelativeLayout) rootView.findViewById(R.id.filter_layout);
        filter_layout.setVisibility(View.GONE);
        lv = (ListView) rootView.findViewById(R.id.listNotes);
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);

        fabLayout1= (LinearLayout) rootView.findViewById(R.id.fabLayout1);
        fabLayout2= (LinearLayout) rootView.findViewById(R.id.fabLayout2);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        TextView fab2_text = (TextView)rootView.findViewById(R.id.text_fab2);
        fab2_text.setText(getString(R.string.courseList_fromText));
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
                            setCoursesList();
                            Snackbar.make(lv, R.string.bookmark_added, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                final android.app.AlertDialog dialog = builder.create();
                // Display the custom alert dialog on interface
                dialog.show();
                helper_main.showKeyboard(getActivity(),edit_title);
            }
        });

        fab2.setImageResource(R.drawable.file_document_light);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                Intent mainIntent = new Intent(getActivity(), Popup_files.class);
                mainIntent.setAction("file_chooseText");
                startActivity(mainIntent);
            }
        });

        //calling Notes_DbAdapter
        db = new Courses_DbAdapter(getActivity());
        db.open();

        setCoursesList();
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
        if (viewPager.getCurrentItem() == 9) {
            setCoursesList();
        }
    }

    public void doBack() {
        //BackPressed in activity will call this;
        if(isFABOpen){
            closeFABMenu();
        } else {
            helper_main.onClose(getActivity());
        }
    }

    private void setCoursesList() {

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

                if(isFABOpen){
                    closeFABMenu();
                }

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String courses_title = row2.getString(row2.getColumnIndexOrThrow("courses_title"));
                final String courses_content = row2.getString(row2.getColumnIndexOrThrow("courses_content"));
                final String courses_icon = row2.getString(row2.getColumnIndexOrThrow("courses_icon"));
                final String courses_attachment = row2.getString(row2.getColumnIndexOrThrow("courses_attachment"));
                final String courses_creation = row2.getString(row2.getColumnIndexOrThrow("courses_creation"));

                sharedPref.edit().putString("random_title", courses_title).apply();
                sharedPref.edit().putString("random_content", courses_content).apply();
                sharedPref.edit().putString("random_seqno", _id).apply();
                sharedPref.edit().putString("random_icon", courses_icon).apply();
                sharedPref.edit().putString("random_create", courses_creation).apply();
                sharedPref.edit().putString("random_attachment", courses_attachment).apply();

                helper_main.switchToActivity(getActivity(), Activity_course.class, false);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if(isFABOpen){
                    closeFABMenu();
                }

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String courses_title = row2.getString(row2.getColumnIndexOrThrow("courses_title"));
                final String courses_content = row2.getString(row2.getColumnIndexOrThrow("courses_content"));
                final String courses_icon = row2.getString(row2.getColumnIndexOrThrow("courses_icon"));
                final String courses_attachment = row2.getString(row2.getColumnIndexOrThrow("courses_attachment"));
                final String courses_creation = row2.getString(row2.getColumnIndexOrThrow("courses_creation"));

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

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    View dialogView = View.inflate(getActivity(), R.layout.dialog_edit_title, null);

                                    final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                                    edit_title.setHint(R.string.bookmark_edit_title);
                                    edit_title.setText(courses_title);

                                    builder.setView(dialogView);
                                    builder.setTitle(R.string.bookmark_edit_title);
                                    builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            String inputTitle = edit_title.getText().toString().trim();
                                            db.update(Integer.parseInt(_id),inputTitle, courses_content, courses_icon, courses_attachment, courses_creation);
                                            setCoursesList();
                                            Snackbar.make(lv, R.string.bookmark_added, Snackbar.LENGTH_SHORT).show();
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
                                    helper_main.showKeyboard(getActivity(),edit_title);
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
        getActivity().setTitle(R.string.courseList_title);
        setCoursesList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_help:
                helper_main.switchToActivity(getActivity(), Courses_Help.class, false);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}