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

package de.baumann.hhsmoodle.data_Bookmarks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class Bookmarks_Fragment extends Fragment {

    //calling variables
    private Bookmarks_DbAdapter db;
    private SimpleCursorAdapter adapter;

    private ListView lv = null;
    private EditText filter;
    private SharedPreferences sharedPref;
    private ImageView imgHeader;
    private RelativeLayout filter_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_screen_notes, container, false);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (sharedPref.getString("default_bookmarks", "no").equals("no")) {
            Bookmarks_helper.insertDefaultBookmarks(getActivity());
            sharedPref.edit().putString("default_bookmarks", "yes").apply();
        }

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
                filter_layout.setVisibility(View.GONE);
                setBookmarksList();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        //calling Notes_DbAdapter
        db = new Bookmarks_DbAdapter(getActivity());
        db.open();

        setBookmarksList();
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            getActivity().setTitle(R.string.title_bookmarks);
            setBookmarksList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setBookmarksList();
    }

    private void setBookmarksList() {

        //display data
        final int layoutstyle=R.layout.list_item_notes;
        int[] xml_id = new int[] {
                R.id.textView_title_notes,
                R.id.textView_des_notes,
                R.id.textView_create_notes
        };
        String[] column = new String[] {
                "bookmarks_title",
                "bookmarks_content",
                "bookmarks_creation"
        };
        final Cursor row = db.fetchAllData(getActivity());
        adapter = new SimpleCursorAdapter(getActivity(), layoutstyle,row,column, xml_id, 0) {
            @Override
            public View getView (final int position, View convertView, ViewGroup parent) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String bookmarks_title = row2.getString(row2.getColumnIndexOrThrow("bookmarks_title"));
                final String bookmarks_content = row2.getString(row2.getColumnIndexOrThrow("bookmarks_content"));
                final String bookmarks_icon = row2.getString(row2.getColumnIndexOrThrow("bookmarks_icon"));
                final String bookmarks_attachment = row2.getString(row2.getColumnIndexOrThrow("bookmarks_attachment"));
                final String bookmarks_creation = row2.getString(row2.getColumnIndexOrThrow("bookmarks_creation"));

                View v = super.getView(position, convertView, parent);
                ImageView iv_icon = (ImageView) v.findViewById(R.id.icon_notes);
                final ImageView iv_attachment = (ImageView) v.findViewById(R.id.att_notes);

                switch (bookmarks_icon) {
                    case "01":
                        iv_icon.setImageResource(R.drawable.circle_red);
                        break;
                    case "02":
                        iv_icon.setImageResource(R.drawable.circle_yellow);
                        break;
                    case "03":
                        iv_icon.setImageResource(R.drawable.circle_green);
                        break;
                    case "04":
                        iv_icon.setImageResource(R.drawable.ic_school_grey600_48dp);
                        break;
                    case "05":
                        iv_icon.setImageResource(R.drawable.ic_view_dashboard_grey600_48dp);
                        break;
                    case "06":
                        iv_icon.setImageResource(R.drawable.ic_face_profile_grey600_48dp);
                        break;
                    case "07":
                        iv_icon.setImageResource(R.drawable.ic_calendar_grey600_48dp);
                        break;
                    case "08":
                        iv_icon.setImageResource(R.drawable.ic_chart_areaspline_grey600_48dp);
                        break;
                    case "09":
                        iv_icon.setImageResource(R.drawable.ic_bell_grey600_48dp);
                        break;
                    case "10":
                        iv_icon.setImageResource(R.drawable.ic_settings_grey600_48dp);
                        break;
                    case "11":
                        iv_icon.setImageResource(R.drawable.ic_web_grey600_48dp);
                        break;
                    case "12":
                        iv_icon.setImageResource(R.drawable.ic_magnify_grey600_48dp);
                        break;
                    case "13":
                        iv_icon.setImageResource(R.drawable.ic_pencil_grey600_48dp);
                        break;
                    case "14":
                        iv_icon.setImageResource(R.drawable.ic_check_grey600_48dp);
                        break;
                    case "15":
                        iv_icon.setImageResource(R.drawable.ic_clock_grey600_48dp);
                        break;
                    case "16":
                        iv_icon.setImageResource(R.drawable.ic_bookmark_grey600_48dp);
                        break;
                }

                switch (bookmarks_attachment) {
                    case "":
                        iv_attachment.setVisibility(View.VISIBLE);
                        iv_attachment.setImageResource(R.drawable.star_outline);
                        break;
                    default:
                        iv_attachment.setVisibility(View.VISIBLE);
                        iv_attachment.setImageResource(R.drawable.star_grey);
                        break;
                }

                iv_attachment.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (bookmarks_attachment.equals("")) {

                            if(db.isExistFav("true")){
                                Snackbar.make(lv, R.string.bookmark_setFav_not, Snackbar.LENGTH_LONG).show();
                            }else{
                                iv_attachment.setImageResource(R.drawable.star_grey);
                                db.update(Integer.parseInt(_id), bookmarks_title, bookmarks_content, bookmarks_icon, "true", bookmarks_creation);
                                setBookmarksList();
                                sharedPref.edit()
                                        .putString("favoriteURL", bookmarks_content)
                                        .putString("favoriteTitle", bookmarks_title)
                                        .apply();
                                Snackbar.make(lv, R.string.bookmark_setFav, Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            iv_attachment.setImageResource(R.drawable.star_outline);
                            db.update(Integer.parseInt(_id), bookmarks_title, bookmarks_content, bookmarks_icon, "", bookmarks_creation);
                            setBookmarksList();
                        }
                    }
                });

                iv_icon.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        final Item[] items = {
                                new Item(getString(R.string.note_priority_2), R.drawable.circle_red),
                                new Item(getString(R.string.note_priority_1), R.drawable.circle_yellow),
                                new Item(getString(R.string.note_priority_0), R.drawable.circle_green),
                                new Item(getString(R.string.text_tit_11), R.drawable.ic_school_grey600_48dp),
                                new Item(getString(R.string.text_tit_1), R.drawable.ic_view_dashboard_grey600_48dp),
                                new Item(getString(R.string.text_tit_2), R.drawable.ic_face_profile_grey600_48dp),
                                new Item(getString(R.string.text_tit_8), R.drawable.ic_calendar_grey600_48dp),
                                new Item(getString(R.string.text_tit_3), R.drawable.ic_chart_areaspline_grey600_48dp),
                                new Item(getString(R.string.text_tit_4), R.drawable.ic_bell_grey600_48dp),
                                new Item(getString(R.string.text_tit_5), R.drawable.ic_settings_grey600_48dp),
                                new Item(getString(R.string.text_tit_6), R.drawable.ic_web_grey600_48dp),
                                new Item(getString(R.string.text_tit_7), R.drawable.ic_magnify_grey600_48dp),
                                new Item(getString(R.string.title_notes), R.drawable.ic_pencil_grey600_48dp),
                                new Item(getString(R.string.text_tit_9), R.drawable.ic_check_grey600_48dp),
                                new Item(getString(R.string.text_tit_10), R.drawable.ic_clock_grey600_48dp),
                                new Item(getString(R.string.title_bookmarks), R.drawable.ic_bookmark_grey600_48dp),
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
                                            db.update(Integer.parseInt(_id), bookmarks_title, bookmarks_content, "01", bookmarks_attachment, bookmarks_creation);
                                            setBookmarksList();
                                        } else if (item == 1) {
                                            db.update(Integer.parseInt(_id), bookmarks_title, bookmarks_content, "02", bookmarks_attachment, bookmarks_creation);
                                            setBookmarksList();
                                        } else if (item == 2) {
                                            db.update(Integer.parseInt(_id), bookmarks_title, bookmarks_content, "03", bookmarks_attachment, bookmarks_creation);
                                            setBookmarksList();
                                        } else if (item == 3) {
                                            db.update(Integer.parseInt(_id), bookmarks_title, bookmarks_content, "04", bookmarks_attachment, bookmarks_creation);
                                            setBookmarksList();
                                        } else if (item == 4) {
                                            db.update(Integer.parseInt(_id), bookmarks_title, bookmarks_content, "05", bookmarks_attachment, bookmarks_creation);
                                            setBookmarksList();
                                        } else if (item == 5) {
                                            db.update(Integer.parseInt(_id), bookmarks_title, bookmarks_content, "06", bookmarks_attachment, bookmarks_creation);
                                            setBookmarksList();
                                        } else if (item == 6) {
                                            db.update(Integer.parseInt(_id), bookmarks_title, bookmarks_content, "07", bookmarks_attachment, bookmarks_creation);
                                            setBookmarksList();
                                        } else if (item == 7) {
                                            db.update(Integer.parseInt(_id), bookmarks_title, bookmarks_content, "08", bookmarks_attachment, bookmarks_creation);
                                            setBookmarksList();
                                        } else if (item == 8) {
                                            db.update(Integer.parseInt(_id), bookmarks_title, bookmarks_content, "09", bookmarks_attachment, bookmarks_creation);
                                            setBookmarksList();
                                        } else if (item == 9) {
                                            db.update(Integer.parseInt(_id), bookmarks_title, bookmarks_content, "10", bookmarks_attachment, bookmarks_creation);
                                            setBookmarksList();
                                        } else if (item == 10) {
                                            db.update(Integer.parseInt(_id), bookmarks_title, bookmarks_content, "11", bookmarks_attachment, bookmarks_creation);
                                            setBookmarksList();
                                        } else if (item == 11) {
                                            db.update(Integer.parseInt(_id), bookmarks_title, bookmarks_content, "12", bookmarks_attachment, bookmarks_creation);
                                            setBookmarksList();
                                        } else if (item == 12) {
                                            db.update(Integer.parseInt(_id), bookmarks_title, bookmarks_content, "13", bookmarks_attachment, bookmarks_creation);
                                            setBookmarksList();
                                        } else if (item == 13) {
                                            db.update(Integer.parseInt(_id), bookmarks_title, bookmarks_content, "14", bookmarks_attachment, bookmarks_creation);
                                            setBookmarksList();
                                        } else if (item == 14) {
                                            db.update(Integer.parseInt(_id), bookmarks_title, bookmarks_content, "15", bookmarks_attachment, bookmarks_creation);
                                            setBookmarksList();
                                        } else if (item == 15) {
                                            db.update(Integer.parseInt(_id), bookmarks_title, bookmarks_content, "16", bookmarks_attachment, bookmarks_creation);
                                            setBookmarksList();
                                        }
                                    }
                                }).show();
                    }
                });
                return v;
            }
        };

        //display data by filter
        final String note_search = sharedPref.getString("filter_bookmarksBY", "bookmarks_title");
        sharedPref.edit().putString("filter_bookmarksBY", "bookmarks_title").apply();
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
                final String bookmarks_content = row2.getString(row2.getColumnIndexOrThrow("bookmarks_content"));
                sharedPref.edit().putString("loadURL", bookmarks_content).apply();

                ViewPager viewPager = (class_CustomViewPager) getActivity().findViewById(R.id.viewpager);
                viewPager.setCurrentItem(0);

            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String bookmarks_title = row2.getString(row2.getColumnIndexOrThrow("bookmarks_title"));
                final String bookmarks_content = row2.getString(row2.getColumnIndexOrThrow("bookmarks_content"));
                final String bookmarks_icon = row2.getString(row2.getColumnIndexOrThrow("bookmarks_icon"));
                final String bookmarks_attachment = row2.getString(row2.getColumnIndexOrThrow("bookmarks_attachment"));
                final String bookmarks_creation = row2.getString(row2.getColumnIndexOrThrow("bookmarks_creation"));

                final CharSequence[] options = {
                        getString(R.string.bookmark_edit_title),
                        getString(R.string.bookmark_createNote),
                        getString(R.string.todo_menu),
                        getString(R.string.bookmark_createShortcut),
                        getString(R.string.bookmark_createEvent),
                        getString(R.string.bookmark_remove_bookmark)};
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
                                if (options[item].equals(getString(R.string.bookmark_edit_title))) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    View dialogView = View.inflate(getActivity(), R.layout.dialog_edit_title, null);

                                    final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                                    edit_title.setHint(R.string.bookmark_edit_title);
                                    edit_title.setText(bookmarks_title);

                                    builder.setView(dialogView);
                                    builder.setTitle(R.string.bookmark_edit_title);
                                    builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            String inputTag = edit_title.getText().toString().trim();
                                            db.update(Integer.parseInt(_id), inputTag, bookmarks_content, bookmarks_icon, bookmarks_attachment, bookmarks_creation);
                                            setBookmarksList();
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

                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            helper_main.showKeyboard(getActivity(),edit_title);
                                        }
                                    }, 200);
                                }

                                if (options[item].equals (getString(R.string.todo_menu))) {

                                    Todo_DbAdapter db = new Todo_DbAdapter(getActivity());
                                    db.open();

                                    if(db.isExist(bookmarks_title)){
                                        Snackbar.make(lv, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                                    } else {
                                        db.insert(bookmarks_title, "", "3", "true", helper_main.createDate());
                                        ViewPager viewPager = (class_CustomViewPager) getActivity().findViewById(R.id.viewpager);
                                        viewPager.setCurrentItem(2);
                                        dialog.dismiss();
                                    }
                                }

                                if (options[item].equals (getString(R.string.bookmark_createEvent))) {
                                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                                    calIntent.setType("vnd.android.cursor.item/event");
                                    calIntent.putExtra(CalendarContract.Events.TITLE, bookmarks_title);
                                    startActivity(calIntent);
                                }

                                if (options[item].equals(getString(R.string.bookmark_remove_bookmark))) {

                                    Snackbar snackbar = Snackbar
                                            .make(lv, R.string.note_remove_confirmation, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.toast_yes, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    db.delete(Integer.parseInt(_id));
                                                    setBookmarksList();
                                                }
                                            });
                                    snackbar.show();
                                }

                                if (options[item].equals (getString(R.string.bookmark_createNote))) {

                                    sharedPref.edit()
                                            .putString("handleTextTitle", bookmarks_title)
                                            .putString("handleTextText", bookmarks_content)
                                            .putString("handleTextCreate", helper_main.createDate())
                                            .putString("handleTextIcon", "")
                                            .putString("handleTextAttachment", "")
                                            .putString("handleTextSeqno", "")
                                            .apply();
                                    Notes_helper.newNote(getActivity());
                                }

                                if (options[item].equals (getString(R.string.bookmark_createShortcut))) {
                                    Intent i = new Intent();
                                    i.setAction(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(bookmarks_content));

                                    Intent shortcut = new Intent();
                                    shortcut.putExtra("android.intent.extra.shortcut.INTENT", i);
                                    shortcut.putExtra("android.intent.extra.shortcut.NAME", "THE NAME OF SHORTCUT TO BE SHOWN");
                                    shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, bookmarks_title);
                                    shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getActivity().getApplicationContext(), R.mipmap.ic_launcher));
                                    shortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                    getActivity().sendBroadcast(shortcut);
                                    Snackbar.make(lv, R.string.toast_shortcut, Snackbar.LENGTH_LONG).show();
                                }

                            }
                        }).show();

                return true;
            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_help:
                final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.todo_title)
                        .setMessage(helper_main.textSpannable(getString(R.string.helpToDo_text)))
                        .setPositiveButton(getString(R.string.toast_yes), null);
                dialog.show();
                return true;

            case R.id.action_filter:
                final CharSequence[] options = {
                        getActivity().getString(R.string.action_filter_title),
                        getActivity().getString(R.string.action_filter_url),
                        getActivity().getString(R.string.action_filter_create)};
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
                                    sharedPref.edit().putString("filter_bookmarksBY", "bookmarks_title").apply();
                                    setBookmarksList();
                                    filter_layout.setVisibility(View.VISIBLE);
                                    imgHeader.setVisibility(View.GONE);
                                    filter.setText("");
                                    filter.setHint(R.string.action_filter_title);
                                    filter.requestFocus();
                                    helper_main.showKeyboard(getActivity(), filter);
                                }

                                if (options[item].equals(getActivity().getString(R.string.action_filter_url))) {
                                    sharedPref.edit().putString("filter_bookmarksBY", "bookmarks_content").apply();
                                    setBookmarksList();
                                    filter_layout.setVisibility(View.VISIBLE);
                                    imgHeader.setVisibility(View.GONE);
                                    filter.setText("");
                                    filter.setHint(R.string.action_filter_url);
                                    filter.requestFocus();
                                    helper_main.showKeyboard(getActivity(), filter);
                                }

                                if (options[item].equals(getActivity().getString(R.string.action_filter_create))) {
                                    sharedPref.edit().putString("filter_bookmarksBY", "bookmarks_create").apply();
                                    setBookmarksList();
                                    filter_layout.setVisibility(View.VISIBLE);
                                    imgHeader.setVisibility(View.GONE);
                                    filter.setText("");
                                    filter.setHint(R.string.action_filter_create);
                                    filter.requestFocus();
                                    helper_main.showKeyboard(getActivity(), filter);
                                }
                            }
                        }).show();

                return true;

            case R.id.action_sort:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View dialogView = View.inflate(getActivity(), R.layout.dialog_sorting, null);

                final CheckBox ch_title = (CheckBox) dialogView.findViewById(R.id.checkBoxTitle);
                final CheckBox ch_create = (CheckBox) dialogView.findViewById(R.id.checkBoxCreate);
                final CheckBox ch_icon = (CheckBox) dialogView.findViewById(R.id.checkBoxIcon);

                RelativeLayout layout_sortByAtt = (RelativeLayout) dialogView.findViewById(R.id.layout_sortByAtt);
                layout_sortByAtt.setVisibility(View.GONE);

                if (sharedPref.getString("sortDBB", "title").equals("title")) {
                    ch_title.setChecked(true);
                } else {
                    ch_title.setChecked(false);
                }
                if (sharedPref.getString("sortDBB", "title").equals("create")) {
                    ch_create.setChecked(true);
                } else {
                    ch_create.setChecked(false);
                }
                if (sharedPref.getString("sortDBB", "title").equals("icon")) {
                    ch_icon.setChecked(true);
                } else {
                    ch_icon.setChecked(false);
                }

                ch_title.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if(isChecked){
                            ch_create.setChecked(false);
                            ch_icon.setChecked(false);
                            sharedPref.edit().putString("sortDBB", "title").apply();
                            setBookmarksList();
                        }
                    }
                });
                ch_create.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if(isChecked){
                            ch_icon.setChecked(false);
                            ch_title.setChecked(false);
                            sharedPref.edit().putString("sortDBB", "create").apply();
                            setBookmarksList();
                        }
                    }
                });
                ch_icon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if(isChecked){
                            ch_create.setChecked(false);
                            ch_title.setChecked(false);
                            sharedPref.edit().putString("sortDBB", "icon").apply();
                            setBookmarksList();
                        }
                    }
                });

                builder.setView(dialogView);
                builder.setTitle(R.string.action_sort);
                builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                final AlertDialog dialog2 = builder.create();
                // Display the custom alert dialog on interface
                dialog2.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}