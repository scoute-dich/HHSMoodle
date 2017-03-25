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

package de.baumann.hhsmoodle.data_count;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.NotificationManager;
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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.activities.Activity_EditNote;
import de.baumann.hhsmoodle.activities.Activity_count;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.popup.Popup_courseList;

public class Count_Fragment extends Fragment {

    //calling variables
    private Count_DbAdapter db;
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
                    setCountList();
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
                final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                View dialogView = View.inflate(getActivity(), R.layout.dialog_edit_title, null);
                final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                edit_title.setHint(R.string.bookmark_edit_title);
                builder.setTitle(R.string.count_title);
                builder.setView(dialogView);
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
                dialog2.show();

                dialog2.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Do stuff, possibly set wantToCloseDialog to true then...

                        String inputTitle = edit_title.getText().toString().trim();

                        Count_DbAdapter db = new Count_DbAdapter(getActivity());
                        db.open();

                        if(db.isExist(inputTitle)){
                            Snackbar.make(edit_title, getActivity().getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                        }else{
                            dialog2.dismiss();
                            db.insert(inputTitle, sharedPref.getString("count_content", ""), "3", "", helper_main.createDate());
                            sharedPref.edit().putString("count_content", "").apply();
                            setCountList();
                        }
                    }
                });

                helper_main.showKeyboard(getActivity(),edit_title);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                helper_main.isOpened(getActivity());
                Intent mainIntent = new Intent(getActivity(), Popup_courseList.class);
                mainIntent.setAction("courseList_count");
                startActivity(mainIntent);
            }
        });

        //calling Notes_DbAdapter
        db = new Count_DbAdapter(getActivity());
        db.open();

        setCountList();
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
        if (!sharedPref.getString("search_byCourse", "").isEmpty() && viewPager.getCurrentItem() == 4) {
            String search = sharedPref.getString("search_byCourse", "");
            sharedPref.edit().putString("filter_countBY", "count_title").apply();
            getActivity().setTitle(getString(R.string.count_title) + " | " + search);
            setCountList();
            filter_layout.setVisibility(View.VISIBLE);
            imgHeader.setVisibility(View.GONE);
            filter.setText(search);
            sharedPref.edit().putString("search_byCourse", "").apply();
        } else {
            setCountList();
        }
    }

    public void doBack() {
        //BackPressed in activity will call this;
        if(isFABOpen){
            closeFABMenu();
        } else if (filter_layout.getVisibility() == View.VISIBLE) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(filter_layout.getWindowToken(), 0);
            imgHeader.setVisibility(View.VISIBLE);
            filter_layout.setVisibility(View.GONE);
            setCountList();
        } else {
            helper_main.onClose(getActivity());
        }
    }

    public void setCountList() {

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
                "count_title",
                "count_content",
                "count_creation"
        };
        final Cursor row = db.fetchAllData(getActivity());
        adapter = new SimpleCursorAdapter(getActivity(), layoutstyle,row,column, xml_id, 0) {
            @Override
            public View getView (final int position, View convertView, ViewGroup parent) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String count_title = row2.getString(row2.getColumnIndexOrThrow("count_title"));
                final String count_content = row2.getString(row2.getColumnIndexOrThrow("count_content"));
                final String count_icon = row2.getString(row2.getColumnIndexOrThrow("count_icon"));
                final String count_attachment = row2.getString(row2.getColumnIndexOrThrow("count_attachment"));
                final String count_creation = row2.getString(row2.getColumnIndexOrThrow("count_creation"));

                View v = super.getView(position, convertView, parent);
                final TextView tv = (TextView) v.findViewById(R.id.text);




                if (count_attachment.isEmpty()) {

                    tv.setText(count_content);

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            int n = tv.getLineCount();

                            StringBuilder sb = new StringBuilder(n);
                            for (int i = 0; i < n; ++i) {
                                sb.append("0" + '\n');
                            }
                            String result = sb.toString();

                            db.update(Integer.parseInt(_id), count_title, count_content, count_icon, result, count_creation);
                            setCountList();
                        }
                    }, 500);
                }


                ImageView iv_icon = (ImageView) v.findViewById(R.id.icon_notes);

                switch (count_icon) {
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
                                            db.update(Integer.parseInt(_id),count_title, count_content, "3", count_attachment, count_creation);
                                            setCountList();
                                        } else if (item == 1) {
                                            db.update(Integer.parseInt(_id),count_title, count_content, "2", count_attachment, count_creation);
                                            setCountList();
                                        } else if (item == 2) {
                                            db.update(Integer.parseInt(_id),count_title, count_content, "1", count_attachment, count_creation);
                                            setCountList();
                                        }
                                    }
                                }).show();
                    }
                });
                return v;
            }
        };

        //display data by filter
        final String note_search = sharedPref.getString("filter_countBY", "note_title");
        sharedPref.edit().putString("filter_countBY", "note_title").apply();
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
                final String count_title = row2.getString(row2.getColumnIndexOrThrow("count_title"));
                final String count_content = row2.getString(row2.getColumnIndexOrThrow("count_content"));
                final String count_icon = row2.getString(row2.getColumnIndexOrThrow("count_icon"));
                final String count_attachment = row2.getString(row2.getColumnIndexOrThrow("count_attachment"));
                final String count_creation = row2.getString(row2.getColumnIndexOrThrow("count_creation"));

                sharedPref.edit().putString("count_title", count_title).apply();
                sharedPref.edit().putString("count_content", count_content).apply();
                sharedPref.edit().putString("count_seqno", _id).apply();
                sharedPref.edit().putString("count_icon", count_icon).apply();
                sharedPref.edit().putString("count_create", count_creation).apply();
                sharedPref.edit().putString("count_attachment", count_attachment).apply();

                helper_main.switchToActivity(getActivity(), Activity_count.class, false);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if(isFABOpen){
                    closeFABMenu();
                }

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String count_title = row2.getString(row2.getColumnIndexOrThrow("count_title"));
                final String count_content = row2.getString(row2.getColumnIndexOrThrow("count_content"));
                final String count_icon = row2.getString(row2.getColumnIndexOrThrow("count_icon"));
                final String count_attachment = row2.getString(row2.getColumnIndexOrThrow("count_attachment"));
                final String count_creation = row2.getString(row2.getColumnIndexOrThrow("count_creation"));

                final CharSequence[] options = {
                        getString(R.string.number_edit_entry),
                        getString(R.string.bookmark_remove_bookmark),
                        getString(R.string.todo_share),
                        getString(R.string.bookmark_createNote),
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

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    View dialogView = View.inflate(getActivity(), R.layout.dialog_edit_title, null);

                                    final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                                    edit_title.setHint(R.string.bookmark_edit_title);
                                    edit_title.setText(count_title);

                                    builder.setView(dialogView);
                                    builder.setTitle(R.string.bookmark_edit_title);
                                    builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            String inputTag = edit_title.getText().toString().trim();
                                            db.update(Integer.parseInt(_id), inputTag, count_content, count_icon, count_attachment, count_creation);
                                            setCountList();
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

                                if (options[item].equals (getString(R.string.todo_share))) {
                                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, count_title);
                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, count_content);
                                    startActivity(Intent.createChooser(sharingIntent, (getString(R.string.note_share_2))));
                                }

                                if (options[item].equals (getString(R.string.bookmark_createEvent))) {
                                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                                    calIntent.setType("vnd.android.cursor.item/event");
                                    calIntent.putExtra(CalendarContract.Events.TITLE, count_title);
                                    calIntent.putExtra(CalendarContract.Events.DESCRIPTION, count_content);
                                    startActivity(calIntent);
                                }

                                if (options[item].equals(getString(R.string.bookmark_remove_bookmark))) {
                                    Snackbar snackbar = Snackbar
                                            .make(lv, R.string.note_remove_confirmation, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.toast_yes, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    db.delete(Integer.parseInt(_id));
                                                    setCountList();
                                                }
                                            });
                                    snackbar.show();
                                }

                                if (options[item].equals (getString(R.string.bookmark_createNote))) {
                                    sharedPref.edit()
                                            .putString("handleTextTitle", count_title)
                                            .putString("handleTextText", count_content)
                                            .apply();
                                    helper_main.switchToActivity(getActivity(), Activity_EditNote.class, false);
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
        menu.findItem(R.id.sort_attachment).setVisible(false);
        menu.findItem(R.id.sort_icon).setVisible(false);
        menu.findItem(R.id.filter_att).setVisible(false);
        menu.findItem(R.id.filter_url).setVisible(false);
        menu.findItem(R.id.filter_teacher).setVisible(false);
        menu.findItem(R.id.filter_room).setVisible(false);
        menu.findItem(R.id.sort_ext).setVisible(false);
        menu.findItem(R.id.sort_notification).setVisible(false);
        menu.findItem(R.id.filter_ext).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_help:

                return true;

            case R.id.filter_title:
                sharedPref.edit().putString("filter_countBY", "count_title").apply();
                setCountList();
                filter_layout.setVisibility(View.VISIBLE);
                imgHeader.setVisibility(View.GONE);
                filter.setText("");
                filter.setHint(R.string.action_filter_title);
                filter.requestFocus();
                helper_main.showKeyboard(getActivity(), filter);
                return true;
            case R.id.filter_course:
                helper_main.isOpened(getActivity());
                Intent mainIntent = new Intent(getActivity(), Popup_courseList.class);
                mainIntent.setAction("search_byCourse");
                startActivity(mainIntent);
                return true;

            case R.id.filter_content:
                sharedPref.edit().putString("filter_countBY", "count_content").apply();
                setCountList();
                filter_layout.setVisibility(View.VISIBLE);
                imgHeader.setVisibility(View.GONE);
                filter.setText("");
                filter.setHint(R.string.action_filter_cont);
                filter.requestFocus();
                helper_main.showKeyboard(getActivity(), filter);
                return true;

            case R.id.filter_today:
                getActivity().setTitle(getString(R.string.count_title) + " | " + getString(R.string.filter_today));
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar cal = Calendar.getInstance();
                final String search = dateFormat.format(cal.getTime());
                sharedPref.edit().putString("filter_countBY", "count_creation").apply();
                setCountList();
                filter_layout.setVisibility(View.VISIBLE);
                imgHeader.setVisibility(View.GONE);
                filter.setText(search);
                return true;
            case R.id.filter_yesterday:
                getActivity().setTitle(getString(R.string.count_title) + " | " + getString(R.string.filter_yesterday));
                DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar cal2 = Calendar.getInstance();
                cal2.add(Calendar.DATE, -1);
                final String search2 = dateFormat2.format(cal2.getTime());
                sharedPref.edit().putString("filter_countBY", "count_creation").apply();
                setCountList();
                filter_layout.setVisibility(View.VISIBLE);
                imgHeader.setVisibility(View.GONE);
                filter.setText(search2);
                return true;
            case R.id.filter_before:
                getActivity().setTitle(getString(R.string.count_title) + " | " + getString(R.string.filter_before));
                DateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar cal3 = Calendar.getInstance();
                cal3.add(Calendar.DATE, -2);
                final String search3 = dateFormat3.format(cal3.getTime());
                sharedPref.edit().putString("filter_countBY", "count_creation").apply();
                setCountList();
                filter.setText(search3);
                return true;
            case R.id.filter_month:
                getActivity().setTitle(getString(R.string.count_title) + " | " + getString(R.string.filter_month));
                DateFormat dateFormat4 = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
                Calendar cal4 = Calendar.getInstance();
                final String search4 = dateFormat4.format(cal4.getTime());
                sharedPref.edit().putString("filter_countBY", "count_creation").apply();
                setCountList();
                filter_layout.setVisibility(View.VISIBLE);
                imgHeader.setVisibility(View.GONE);
                filter.setText(search4);
                return true;
            case R.id.filter_own:
                getActivity().setTitle(getString(R.string.count_title) + " | " + getString(R.string.filter_own));
                sharedPref.edit().putString("filter_countBY", "count_creation").apply();
                setCountList();
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
                setCountList();
                return true;

            case R.id.sort_title:
                sharedPref.edit().putString("sortDBC", "title").apply();
                setTitle();
                setCountList();
                return true;
            case R.id.sort_pri:
                sharedPref.edit().putString("sortDBC", "icon").apply();
                setTitle();
                setCountList();
                return true;
            case R.id.sort_creation:
                sharedPref.edit().putString("sortDBC", "create").apply();
                setTitle();
                setCountList();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setTitle () {
        if (sharedPref.getString("sortDBC", "title").equals("title")) {
            getActivity().setTitle(getString(R.string.count_title) + " | " + getString(R.string.sort_title));
        } else if (sharedPref.getString("sortDBC", "title").equals("icon")) {
            getActivity().setTitle(getString(R.string.count_title) + " | " + getString(R.string.sort_pri));
        } else {
            getActivity().setTitle(getString(R.string.count_title) + " | " + getString(R.string.sort_date));
        }
    }
}