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
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
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
import de.baumann.hhsmoodle.activities.Activity_todo;
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
                setTitle();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                imgHeader.setVisibility(View.VISIBLE);
                filter_layout.setVisibility(View.GONE);
                setTodoList();
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
                Todo_helper.newTodo(getActivity(), "", "", "");
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                helper_main.isOpened(getActivity());
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed() && viewPager.getCurrentItem() == 2) {
            setTitle();
            setTodoList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setTodoList();
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
            setTodoList();
        } else {
            helper_main.onClose(getActivity());
        }
    }

    private void setTodoList() {

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
                ImageView iv_icon = (ImageView) v.findViewById(R.id.icon_notes);
                ImageView iv_attachment = (ImageView) v.findViewById(R.id.att_notes);

                switch (todo_icon) {
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

                switch (todo_attachment) {
                    case "true":
                        iv_attachment.setVisibility(View.VISIBLE);
                        iv_attachment.setImageResource(R.drawable.alert_circle);
                        break;
                    default:
                        iv_attachment.setVisibility(View.VISIBLE);
                        iv_attachment.setImageResource(R.drawable.alert_circle_red);

                        int n = Integer.valueOf(_id);

                        android.content.Intent iMain = new android.content.Intent();
                        iMain.setAction("shortcutToDo");
                        iMain.setClassName(getActivity(), "de.baumann.hhsmoodle.activities.Activity_splash");
                        PendingIntent piMain = PendingIntent.getActivity(getActivity(), n, iMain, 0);

                        NotificationCompat.Builder builderSummary =
                                new NotificationCompat.Builder(getActivity())
                                        .setSmallIcon(R.drawable.school)
                                        .setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                                        .setGroup("HHS_Moodle")
                                        .setGroupSummary(true)
                                        .setContentIntent(piMain);

                        Notification notification = new NotificationCompat.Builder(getActivity())
                                .setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                                .setSmallIcon(R.drawable.school)
                                .setContentTitle(todo_title)
                                .setContentText(todo_content)
                                .setContentIntent(piMain)
                                .setAutoCancel(true)
                                .setGroup("HHS_Moodle")
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(todo_content))
                                .setPriority(Notification.PRIORITY_DEFAULT)
                                .setVibrate(new long[0])
                                .build();

                        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(n, notification);
                        notificationManager.notify(0, builderSummary.build());
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
                                            db.update(Integer.parseInt(_id),todo_title, todo_content, "3", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 1) {
                                            db.update(Integer.parseInt(_id),todo_title, todo_content, "2", todo_attachment, todo_creation);
                                            setTodoList();
                                        } else if (item == 2) {
                                            db.update(Integer.parseInt(_id),todo_title, todo_content, "1", todo_attachment, todo_creation);
                                            setTodoList();
                                        }
                                    }
                                }).show();
                    }
                });
                iv_attachment.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        switch (todo_attachment) {
                            case "true":
                                db.update(Integer.parseInt(_id), todo_title, todo_content, todo_icon, "", todo_creation);
                                setTodoList();
                                break;
                            default:
                                db.update(Integer.parseInt(_id), todo_title, todo_content, todo_icon, "true", todo_creation);
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
        //onClick function
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {

                if(isFABOpen){
                    closeFABMenu();
                }

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
                                    edit_title.setText(todo_title);

                                    builder.setView(dialogView);
                                    builder.setTitle(R.string.bookmark_edit_title);
                                    builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            String inputTag = edit_title.getText().toString().trim();
                                            db.update(Integer.parseInt(_id), inputTag, todo_content, todo_icon, todo_attachment, todo_creation);
                                            setTodoList();
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
                                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, todo_title);
                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, todo_content);
                                    startActivity(Intent.createChooser(sharingIntent, (getString(R.string.note_share_2))));
                                }

                                if (options[item].equals (getString(R.string.bookmark_createEvent))) {
                                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                                    calIntent.setType("vnd.android.cursor.item/event");
                                    calIntent.putExtra(CalendarContract.Events.TITLE, todo_title);
                                    calIntent.putExtra(CalendarContract.Events.DESCRIPTION, todo_content);
                                    startActivity(calIntent);
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

                                if (options[item].equals (getString(R.string.bookmark_createNote))) {
                                    sharedPref.edit()
                                            .putString("handleTextTitle", todo_title)
                                            .putString("handleTextText", todo_content)
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_help:
                helper_main.switchToActivity(getActivity(), Todo_Help.class, false);
                return true;

            case R.id.filter_title:
                sharedPref.edit().putString("filter_todoBY", "todo_title").apply();
                setTodoList();
                filter_layout.setVisibility(View.VISIBLE);
                imgHeader.setVisibility(View.GONE);
                filter.setText("");
                filter.setHint(R.string.action_filter_title);
                filter.requestFocus();
                helper_main.showKeyboard(getActivity(), filter);
                return true;
            case R.id.filter_content:
                sharedPref.edit().putString("filter_todoBY", "todo_content").apply();
                setTodoList();
                filter_layout.setVisibility(View.VISIBLE);
                imgHeader.setVisibility(View.GONE);
                filter.setText("");
                filter.setHint(R.string.action_filter_cont);
                filter.requestFocus();
                helper_main.showKeyboard(getActivity(), filter);
                return true;

            case R.id.filter_today:
                getActivity().setTitle(getString(R.string.todo_title) + " | " + getString(R.string.filter_today));
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar cal = Calendar.getInstance();
                final String search = dateFormat.format(cal.getTime());
                sharedPref.edit().putString("filter_todoBY", "todo_creation").apply();
                setTodoList();
                filter.setText(search);
                return true;
            case R.id.filter_yesterday:
                getActivity().setTitle(getString(R.string.todo_title) + " | " + getString(R.string.filter_yesterday));
                DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar cal2 = Calendar.getInstance();
                cal2.add(Calendar.DATE, -1);
                final String search2 = dateFormat2.format(cal2.getTime());
                sharedPref.edit().putString("filter_todoBY", "todo_creation").apply();
                setTodoList();
                filter.setText(search2);
                return true;
            case R.id.filter_before:
                getActivity().setTitle(getString(R.string.todo_title) + " | " + getString(R.string.filter_before));
                DateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar cal3 = Calendar.getInstance();
                cal3.add(Calendar.DATE, -2);
                final String search3 = dateFormat3.format(cal3.getTime());
                sharedPref.edit().putString("filter_todoBY", "todo_creation").apply();
                setTodoList();
                filter.setText(search3);
                return true;
            case R.id.filter_month:
                getActivity().setTitle(getString(R.string.todo_title) + " | " + getString(R.string.filter_month));
                DateFormat dateFormat4 = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
                Calendar cal4 = Calendar.getInstance();
                final String search4 = dateFormat4.format(cal4.getTime());
                sharedPref.edit().putString("filter_todoBY", "todo_creation").apply();
                setTodoList();
                filter.setText(search4);
                return true;
            case R.id.filter_own:
                getActivity().setTitle(getString(R.string.todo_title) + " | " + getString(R.string.filter_own));
                sharedPref.edit().putString("filter_todoBY", "todo_creation").apply();
                setTodoList();
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
                setTodoList();
                return true;

            case R.id.sort_title:
                sharedPref.edit().putString("sortDBT", "title").apply();
                setTitle();
                setTodoList();
                return true;
            case R.id.sort_pri:
                sharedPref.edit().putString("sortDBT", "icon").apply();
                setTitle();
                setTodoList();
                return true;
            case R.id.sort_creation:
                sharedPref.edit().putString("sortDBT", "create").apply();
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
            getActivity().setTitle(getString(R.string.todo_title) + " | " + getString(R.string.sort_pri));
        } else if (sharedPref.getString("sortDBT", "title").equals("create")) {
            getActivity().setTitle(getString(R.string.todo_title) + " | " + getString(R.string.sort_date));
        } else {
            getActivity().setTitle(getString(R.string.todo_title) + " | " + getString(R.string.sort_not));
        }
    }
}