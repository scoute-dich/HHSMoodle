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

package de.baumann.hhsmoodle.data_todo;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import de.baumann.hhsmoodle.activities.Activity_todo;
import de.baumann.hhsmoodle.data_count.Count_helper;
import de.baumann.hhsmoodle.data_notes.Notes_helper;
import de.baumann.hhsmoodle.data_random.Random_helper;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.popup.Popup_courseList;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Todo_Fragment extends Fragment {

    //calling variables
    private Todo_DbAdapter db;
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

    private int top;
    private int index;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_screen_notes, container, false);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        imgHeader = rootView.findViewById(R.id.imageView_header);
        helper_main.setImageHeader(getActivity(), imgHeader);

        filter_layout = rootView.findViewById(R.id.filter_layout);
        filter_layout.setVisibility(View.GONE);
        lv = rootView.findViewById(R.id.listNotes);
        filter = rootView.findViewById(R.id.myFilter);
        viewPager = getActivity().findViewById(R.id.viewpager);

        ImageButton ib_hideKeyboard = rootView.findViewById(R.id.ib_hideKeyboard);
        ib_hideKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filter.getText().length() > 0) {
                    filter.setText("");
                } else {
                    setTitle();
                    helper_main.hideFilter(getActivity(), filter_layout, imgHeader);
                    setTodoList();
                }
            }
        });

        fabLayout1= rootView.findViewById(R.id.fabLayout1);
        fabLayout2= rootView.findViewById(R.id.fabLayout2);
        fab = rootView.findViewById(R.id.fab);
        FloatingActionButton fab1 = rootView.findViewById(R.id.fab1);
        FloatingActionButton fab2 = rootView.findViewById(R.id.fab2);

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
                Todo_helper.newTodo(getActivity(), "", "", "19","", false);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                Intent mainIntent = new Intent(getActivity(), Popup_courseList.class);
                mainIntent.setAction("courseList_todo");
                startActivity(mainIntent);
            }
        });

        //calling Notes_DbAdapter
        db = new Todo_DbAdapter(getActivity());
        db.open();

        setTodoList();
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
        if (!sharedPref.getString("search_byCourse", "").isEmpty() && viewPager.getCurrentItem() == 2) {
            String search = sharedPref.getString("search_byCourse", "");
            helper_main.changeFilter(getActivity(), "filter_todoBY", "todo_title");
            setTodoList();
            helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                    search, getString(R.string.action_filter_course), false);
            sharedPref.edit().putString("search_byCourse", "").apply();
        }  else if (!sharedPref.getString("search_bySubject", "").isEmpty() && viewPager.getCurrentItem() == 2) {
            String search = sharedPref.getString("search_bySubject", "");
            helper_main.changeFilter(getActivity(), "filter_todoBY", "todo_title");
            setTodoList();
            helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                    search, getString(R.string.action_filter_subject), false);
            sharedPref.edit().putString("search_bySubject", "").apply();
        } else {
            if (filter_layout.getVisibility() == View.GONE) {
                setTodoList();
            }
        }
    }

    public void doBack() {
        //BackPressed in activity will call this;
        if(isFABOpen){
            closeFABMenu();
        } else if (filter_layout.getVisibility() == View.VISIBLE) {
            helper_main.hideFilter(getActivity(), filter_layout, imgHeader);
            setTodoList();
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

    public void setTodoList() {

        if(isFABOpen){
            closeFABMenu();
        }

        NotificationManager nMgr = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();

        //display data
        final int layoutstyle=R.layout.list_item_notes;
        int[] xml_id = new int[] {
                R.id.textView_title_notes,
                R.id.textView_des_notes,
                R.id.textView_create_notes
        };
        String[] column = new String[] {
                "todo_title",
                "todo_content",
                "todo_creation"
        };
        final Cursor row = db.fetchAllData(getActivity());
        adapter = new SimpleCursorAdapter(getActivity(), layoutstyle,row,column, xml_id, 0) {
            @Override
            public View getView (final int position, View convertView, ViewGroup parent) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String todo_title = row2.getString(row2.getColumnIndexOrThrow("todo_title"));
                final String todo_content = row2.getString(row2.getColumnIndexOrThrow("todo_content"));
                final String todo_icon = row2.getString(row2.getColumnIndexOrThrow("todo_icon"));
                final String todo_attachment = row2.getString(row2.getColumnIndexOrThrow("todo_attachment"));
                final String todo_creation = row2.getString(row2.getColumnIndexOrThrow("todo_creation"));

                View v = super.getView(position, convertView, parent);
                ImageView iv_icon = v.findViewById(R.id.icon_notes);
                ImageView iv_attachment = v.findViewById(R.id.att_notes);
                helper_main.switchIcon(getActivity(), todo_icon, "todo_icon", iv_icon);

                switch (todo_attachment) {
                    case "true":
                        iv_attachment.setVisibility(View.VISIBLE);
                        iv_attachment.setImageResource(R.drawable.alert_circle_dark);
                        break;
                    default:
                        iv_attachment.setVisibility(View.VISIBLE);
                        iv_attachment.setImageResource(R.drawable.alert_circle_red);

                        int n = Integer.valueOf(_id);

                        android.content.Intent iMain = new android.content.Intent();
                        iMain.setAction("shortcutToDo");
                        iMain.setClassName(getActivity(), "de.baumann.hhsmoodle.activities.Activity_splash");
                        PendingIntent piMain = PendingIntent.getActivity(getActivity(), n, iMain, 0);

                        NotificationCompat.Builder builder;

                        NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            String CHANNEL_ID = "hhs_not";// The id of the channel.
                            CharSequence name = getActivity().getString(R.string.app_name);// The user-visible name of the channel.
                            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
                            mNotificationManager.createNotificationChannel(mChannel);
                            builder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID);
                        } else {
                            //noinspection deprecation
                            builder = new NotificationCompat.Builder(getActivity());
                        }

                        @SuppressWarnings("deprecation")
                        Notification notification  = builder
                                .setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                                .setSmallIcon(R.drawable.school)
                                .setContentTitle(todo_title)
                                .setContentText(todo_content)
                                .setContentIntent(piMain)
                                .setAutoCancel(true)
                                .setGroupSummary(true)
                                .setGroup("HHS_Moodle")
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(todo_content))
                                .setPriority(Notification.PRIORITY_DEFAULT)
                                .setVibrate(new long[0])
                                .build();

                        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(n, notification);

                        break;
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
                                TextView tv = v.findViewById(android.R.id.text1);
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
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "01", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 1) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "02", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 2) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "03", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 3) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "04", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 4) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "05", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 5) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "06", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 6) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "07", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 7) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "08", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 8) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "09", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 9) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "10", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 10) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "11", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 11) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "12", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 12) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "13", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 13) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "14", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 14) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "15", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 15) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "16", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 16) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "17", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 17) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "18", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 18) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "19", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 19) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "20", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 20) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "21", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 21) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "22", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 22) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "23", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 23) {
                                            db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), "24", todo_attachment, todo_creation);
                                            setTodoList();
                                        }
                                    }
                                }).show();
                    }
                });
                iv_attachment.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        isEdited();
                        switch (todo_attachment) {
                            case "true":
                                db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), todo_icon, "", todo_creation);
                                setTodoList();
                                break;
                            default:
                                db.update(Integer.parseInt(_id), helper_main.secString(todo_title), helper_main.secString(todo_content), todo_icon, "true", todo_creation);
                                setTodoList();
                                break;
                        }
                    }
                });
                return v;
            }
        };

        //display data by filter
        final String note_search = sharedPref.getString("filter_todoBY", "note_title");
        sharedPref.edit().putString("filter_todoBY", "note_title").apply();
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
                final String todo_title = row2.getString(row2.getColumnIndexOrThrow("todo_title"));
                final String todo_content = row2.getString(row2.getColumnIndexOrThrow("todo_content"));
                final String todo_icon = row2.getString(row2.getColumnIndexOrThrow("todo_icon"));
                final String todo_attachment = row2.getString(row2.getColumnIndexOrThrow("todo_attachment"));
                final String todo_creation = row2.getString(row2.getColumnIndexOrThrow("todo_creation"));

                sharedPref.edit().putString("toDo_title", todo_title).apply();
                sharedPref.edit().putString("toDo_text", todo_content).apply();
                sharedPref.edit().putString("toDo_seqno", _id).apply();
                sharedPref.edit().putString("toDo_icon", todo_icon).apply();
                sharedPref.edit().putString("toDo_create", todo_creation).apply();
                sharedPref.edit().putString("toDo_attachment", todo_attachment).apply();

                helper_main.switchToActivity(getActivity(), Activity_todo.class, false);
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
                final String todo_title = row2.getString(row2.getColumnIndexOrThrow("todo_title"));
                final String todo_content = row2.getString(row2.getColumnIndexOrThrow("todo_content"));
                final String todo_icon = row2.getString(row2.getColumnIndexOrThrow("todo_icon"));
                final String todo_attachment = row2.getString(row2.getColumnIndexOrThrow("todo_attachment"));
                final String todo_creation = row2.getString(row2.getColumnIndexOrThrow("todo_creation"));

                final CharSequence[] options = {
                        getString(R.string.number_edit_entry),
                        getString(R.string.bookmark_remove_bookmark),
                        getString(R.string.todo_share),
                        getString(R.string.bookmark_createNote),
                        getString(R.string.number_create),
                        getString(R.string.count_create)};
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

                                    final EditText edit_title = dialogView.findViewById(R.id.pass_title);
                                    edit_title.setHint(R.string.bookmark_edit_title);
                                    edit_title.setText(todo_title);

                                    builder.setView(dialogView);
                                    builder.setTitle(R.string.bookmark_edit_title);
                                    builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            String inputTag = edit_title.getText().toString().trim();
                                            db.update(Integer.parseInt(_id), helper_main.secString(inputTag), helper_main.secString(todo_content), todo_icon, todo_attachment, todo_creation);
                                            setTodoList();
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
                                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, todo_title);
                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, todo_content);
                                    startActivity(Intent.createChooser(sharingIntent, (getString(R.string.note_share_2))));
                                }

                                if (options[item].equals(getString(R.string.bookmark_remove_bookmark))) {
                                    Snackbar snackbar = Snackbar
                                            .make(lv, R.string.note_remove_confirmation, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.toast_yes, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    db.delete(Integer.parseInt(_id));
                                                    setTodoList();
                                                }
                                            });
                                    snackbar.show();
                                }

                                if (options[item].equals (getString(R.string.number_create))) {
                                    Random_helper.newRandom(getActivity(), todo_title, todo_content, todo_icon, getActivity().getString(R.string.note_content), false);
                                }

                                if (options[item].equals (getString(R.string.count_create))) {
                                    Count_helper.newCount(getActivity(), todo_title, todo_content, todo_icon,getActivity().getString(R.string.note_content), false);
                                }

                                if (options[item].equals (getString(R.string.bookmark_createNote))) {
                                    Notes_helper.newNote(getActivity(),todo_title,todo_content,todo_icon,getString(R.string.note_content), false);
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
        menu.findItem(R.id.sort_attachment).setVisible(false);
        menu.findItem(R.id.filter_att).setVisible(false);
        menu.findItem(R.id.filter_url).setVisible(false);
        menu.findItem(R.id.sort_ext).setVisible(false);
        menu.findItem(R.id.filter_ext).setVisible(false);
        setTitle();
        setTodoList();
        helper_main.hideKeyboard(getActivity());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_help:
                helper_main.switchToActivity(getActivity(), Todo_Help.class, false);
                return true;

            case R.id.filter_title_own:
                helper_main.changeFilter(getActivity(), "filter_todoBY", "todo_title");
                setTodoList();
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        "", getString(R.string.action_filter_title), true);
                return true;
            case R.id.filter_content:
                helper_main.changeFilter(getActivity(), "filter_todoBY", "todo_content");
                setTodoList();
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        "", getString(R.string.action_filter_cont), true);
                return true;
            case R.id.filter_course:
                Intent mainIntent = new Intent(getActivity(), Popup_courseList.class);
                mainIntent.setAction("search_byCourse");
                startActivity(mainIntent);
                return true;

            case R.id.sort_title:
                sharedPref.edit().putString("sortDBT", "title").apply();
                setTitle();
                setTodoList();
                return true;
            case R.id.sort_icon:
                sharedPref.edit().putString("sortDBT", "icon").apply();
                setTitle();
                setTodoList();
                return true;
            case R.id.sort_notification:
                sharedPref.edit().putString("sortDBT", "attachment").apply();
                setTitle();
                setTodoList();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setTitle () {
        if (sharedPref.getString("sortDBT", "title").equals("title")) {
            getActivity().setTitle(getString(R.string.todo_title) + " | " + getString(R.string.sort_title));
        } else if (sharedPref.getString("sortDBT", "title").equals("icon")) {
            getActivity().setTitle(getString(R.string.todo_title) + " | " + getString(R.string.sort_icon));
        } else if (sharedPref.getString("sortDBT", "title").equals("create")) {
            getActivity().setTitle(getString(R.string.todo_title) + " | " + getString(R.string.sort_date));
        } else {
            getActivity().setTitle(getString(R.string.todo_title) + " | " + getString(R.string.sort_not));
        }
    }
}