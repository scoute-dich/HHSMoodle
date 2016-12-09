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

package de.baumann.hhsmoodle.fragmentsMain;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.baumann.hhsmoodle.HHS_Browser;
import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.Database_Browser;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.helper.helper_notes;

public class FragmentBookmarks extends Fragment {

    private ListView listView = null;
    private SharedPreferences sharedPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_screen_bookmarks, container, false);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setHasOptionsMenu(true);

        ImageView imgHeader = (ImageView) rootView.findViewById(R.id.imageView_header);
        if(imgHeader != null) {
            TypedArray images = getResources().obtainTypedArray(R.array.splash_images);
            int choice = (int) (Math.random() * images.length());
            imgHeader.setImageResource(images.getResourceId(choice, R.drawable.splash1));
            images.recycle();
        }

        listView = (ListView)rootView.findViewById(R.id.bookmarks);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String startTab = sharedPref.getString("tabMain", "0");
                sharedPref.edit()
                        .putString("tabPref", startTab)
                        .putString("tabMain", "1")
                        .apply();

                @SuppressWarnings("unchecked")
                HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);
                helper_main.isOpened(getActivity());
                helper_main.switchToActivity(getActivity(), HHS_Browser.class, map.get("url"), true);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                @SuppressWarnings("unchecked")
                HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);
                final String seqnoStr = map.get("seqno");
                final String title = map.get("title");
                final String url = map.get("url");
                final String icon = map.get("icon");

                final LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setGravity(Gravity.CENTER_HORIZONTAL);
                final EditText input = new EditText(getActivity());
                input.setSingleLine(true);
                layout.setPadding(30, 0, 50, 0);
                layout.addView(input);

                final CharSequence[] options = {
                        getString(R.string.bookmark_edit_title),
                        getString(R.string.bookmark_edit_fav),
                        getString(R.string.bookmark_createNote),
                        getString(R.string.bookmark_createShortcut),
                        getString(R.string.bookmark_createEvent),
                        getString(R.string.bookmark_remove_bookmark)};
                new AlertDialog.Builder(getActivity())
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals(getString(R.string.bookmark_edit_title))) {
                                    try {

                                        final Database_Browser db = new Database_Browser(getActivity());

                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        View dialogView = View.inflate(getActivity(), R.layout.dialog_edit, null);

                                        final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                                        edit_title.setHint(R.string.bookmark_edit_title);
                                        edit_title.setText(title);

                                        builder.setView(dialogView);
                                        builder.setTitle(R.string.bookmark_edit_title);
                                        builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {

                                                String inputTag = edit_title.getText().toString().trim();
                                                db.deleteBookmark((Integer.parseInt(seqnoStr)));
                                                db.addBookmark(inputTag, url, icon);
                                                db.close();
                                                setBookmarkList();
                                                Snackbar.make(listView, R.string.bookmark_added, Snackbar.LENGTH_SHORT).show();
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

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (options[item].equals (getString(R.string.bookmark_createEvent))) {
                                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                                    calIntent.setType("vnd.android.cursor.item/event");
                                    calIntent.putExtra(CalendarContract.Events.TITLE, title);
                                    startActivity(calIntent);
                                }

                                if (options[item].equals (getString(R.string.bookmark_edit_fav))) {
                                    sharedPref.edit()
                                            .putString("favoriteURL", url)
                                            .putString("favoriteTitle", title)
                                            .apply();
                                    Snackbar.make(listView, R.string.bookmark_setFav, Snackbar.LENGTH_LONG).show();
                                }

                                if (options[item].equals(getString(R.string.bookmark_remove_bookmark))) {
                                    try {
                                        Database_Browser db = new Database_Browser(getActivity());
                                        final int count = db.getRecordCount();
                                        db.close();

                                        if (count == 1) {
                                            Snackbar snackbar = Snackbar
                                                    .make(listView, R.string.bookmark_remove_cannot, Snackbar.LENGTH_LONG);
                                            snackbar.show();

                                        } else {
                                            Snackbar snackbar = Snackbar
                                                    .make(listView, R.string.bookmark_remove_confirmation, Snackbar.LENGTH_LONG)
                                                    .setAction(R.string.toast_yes, new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            try {
                                                                Database_Browser db = new Database_Browser(getActivity());
                                                                db.deleteBookmark(Integer.parseInt(seqnoStr));
                                                                db.close();
                                                                setBookmarkList();
                                                            } catch (PackageManager.NameNotFoundException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                            snackbar.show();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (options[item].equals (getString(R.string.bookmark_createNote))) {
                                    Date date = new Date();
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                    String dateCreate = format.format(date);

                                    sharedPref.edit()
                                            .putString("handleTextTitle", title)
                                            .putString("handleTextText", url)
                                            .putString("handleTextCreate", dateCreate)
                                            .apply();
                                    helper_notes.editNote(getActivity());
                                }

                                if (options[item].equals (getString(R.string.bookmark_createShortcut))) {
                                    Intent i = new Intent();
                                    i.setAction(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));

                                    Intent shortcut = new Intent();
                                    shortcut.putExtra("android.intent.extra.shortcut.INTENT", i);
                                    shortcut.putExtra("android.intent.extra.shortcut.NAME", "THE NAME OF SHORTCUT TO BE SHOWN");
                                    shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
                                    shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getActivity().getApplicationContext(), R.mipmap.ic_launcher));
                                    shortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                    getActivity().sendBroadcast(shortcut);
                                    Snackbar.make(listView, R.string.toast_shortcut, Snackbar.LENGTH_LONG).show();
                                }

                            }
                        }).show();

                return true;
            }
        });

        setBookmarkList();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.getIntExtra("updated", 0) == 1) {
                        setBookmarkList();
                    }
                }
        }
    }

    private void setBookmarkList() {

        ArrayList<HashMap<String,String>> mapList = new ArrayList<>();

        try {
            Database_Browser db = new Database_Browser(getActivity());
            ArrayList<String[]> bookmarkList = new ArrayList<>();
            db.getBookmarks(bookmarkList, getActivity());
            if (bookmarkList.size() == 0) {
                db.loadInitialData();
                db.getBookmarks(bookmarkList, getActivity());
            }
            db.close();

            for (String[] strAry : bookmarkList) {
                HashMap<String, String> map = new HashMap<>();
                map.put("seqno", strAry[0]);
                map.put("title", strAry[1]);
                map.put("url", strAry[2]);
                map.put("icon", strAry[3]);
                mapList.add(map);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(
                    getActivity(),
                    mapList,
                    R.layout.list_item_notes,
                    new String[] {"title", "url"},
                    new int[] {R.id.textView_title_notes, R.id.textView_des_notes}
            ){
                @Override
                public View getView (final int position, final View convertView, final ViewGroup parent) {

                    @SuppressWarnings("unchecked")
                    HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);
                    final String title = map.get("title");
                    final String url = map.get("url");
                    final String seqnoStr = map.get("seqno");
                    final String icon = map.get("icon");

                    View v = super.getView(position, convertView, parent);
                    ImageView i=(ImageView) v.findViewById(R.id.icon_notes);

                    switch (icon) {
                        case "1":
                            i.setImageResource(R.drawable.ic_school_grey600_48dp);
                            break;
                        case "2":
                            i.setImageResource(R.drawable.ic_view_dashboard_grey600_48dp);
                            break;
                        case "3":
                            i.setImageResource(R.drawable.ic_face_profile_grey600_48dp);
                            break;
                        case "4":
                            i.setImageResource(R.drawable.ic_calendar_grey600_48dp);
                            break;
                        case "5":
                            i.setImageResource(R.drawable.ic_chart_areaspline_grey600_48dp);
                            break;
                        case "6":
                            i.setImageResource(R.drawable.ic_bell_grey600_48dp);
                            break;
                        case "7":
                            i.setImageResource(R.drawable.ic_settings_grey600_48dp);
                            break;
                        case "8":
                            i.setImageResource(R.drawable.ic_web_grey600_48dp);
                            break;
                        case "9":
                            i.setImageResource(R.drawable.ic_magnify_grey600_48dp);
                            break;
                        case "10":
                            i.setImageResource(R.drawable.ic_pencil_grey600_48dp);
                            break;
                        case "11":
                            i.setImageResource(R.drawable.ic_check_grey600_48dp);
                            break;
                        case "12":
                            i.setImageResource(R.drawable.ic_clock_grey600_48dp);
                            break;
                        case "13":
                            i.setImageResource(R.drawable.ic_bookmark_grey600_48dp);
                            break;
                        case "14":
                            i.setImageResource(R.drawable.circle_green);
                            break;
                        case "15":
                            i.setImageResource(R.drawable.circle_yellow);
                            break;
                        case "16":
                            i.setImageResource(R.drawable.circle_red);
                            break;
                    }
                    i.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {

                            final FragmentNotes.Item[] items = {
                                    new FragmentNotes.Item(getString(R.string.text_tit_11), R.drawable.ic_school_grey600_48dp),
                                    new FragmentNotes.Item(getString(R.string.text_tit_1), R.drawable.ic_view_dashboard_grey600_48dp),
                                    new FragmentNotes.Item(getString(R.string.text_tit_2), R.drawable.ic_face_profile_grey600_48dp),
                                    new FragmentNotes.Item(getString(R.string.text_tit_8), R.drawable.ic_calendar_grey600_48dp),
                                    new FragmentNotes.Item(getString(R.string.text_tit_3), R.drawable.ic_chart_areaspline_grey600_48dp),
                                    new FragmentNotes.Item(getString(R.string.text_tit_4), R.drawable.ic_bell_grey600_48dp),
                                    new FragmentNotes.Item(getString(R.string.text_tit_5), R.drawable.ic_settings_grey600_48dp),
                                    new FragmentNotes.Item(getString(R.string.text_tit_6), R.drawable.ic_web_grey600_48dp),
                                    new FragmentNotes.Item(getString(R.string.text_tit_7), R.drawable.ic_magnify_grey600_48dp),
                                    new FragmentNotes.Item(getString(R.string.title_notes), R.drawable.ic_pencil_grey600_48dp),
                                    new FragmentNotes.Item(getString(R.string.text_tit_9), R.drawable.ic_check_grey600_48dp),
                                    new FragmentNotes.Item(getString(R.string.text_tit_10), R.drawable.ic_clock_grey600_48dp),
                                    new FragmentNotes.Item(getString(R.string.title_bookmarks), R.drawable.ic_bookmark_grey600_48dp),
                                    new FragmentNotes.Item(getString(R.string.note_priority_0), R.drawable.circle_green),
                                    new FragmentNotes.Item(getString(R.string.note_priority_1), R.drawable.circle_yellow),
                                    new FragmentNotes.Item(getString(R.string.note_priority_2), R.drawable.circle_red)
                            };

                            ListAdapter adapter = new ArrayAdapter<FragmentNotes.Item>(
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
                                    .setAdapter(adapter, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int item) {
                                            if (item == 0) {
                                                changeIcon(seqnoStr, title, url, "1");
                                            } else if (item == 1) {
                                                changeIcon(seqnoStr, title, url, "2");
                                            } else if (item == 2) {
                                                changeIcon(seqnoStr, title, url, "3");
                                            } else if (item == 3) {
                                                changeIcon(seqnoStr, title, url, "4");
                                            } else if (item == 4) {
                                                changeIcon(seqnoStr, title, url, "5");
                                            } else if (item == 5) {
                                                changeIcon(seqnoStr, title, url, "6");
                                            } else if (item == 6) {
                                                changeIcon(seqnoStr, title, url, "7");
                                            } else if (item == 7) {
                                                changeIcon(seqnoStr, title, url, "8");
                                            } else if (item == 8) {
                                                changeIcon(seqnoStr, title, url, "9");
                                            } else if (item == 9) {
                                                changeIcon(seqnoStr, title, url, "10");
                                            } else if (item == 10) {
                                                changeIcon(seqnoStr, title, url, "11");
                                            } else if (item == 11) {
                                                changeIcon(seqnoStr, title, url, "12");
                                            } else if (item == 12) {
                                                changeIcon(seqnoStr, title, url, "13");
                                            } else if (item == 13) {
                                                changeIcon(seqnoStr, title, url, "14");
                                            } else if (item == 14) {
                                                changeIcon(seqnoStr, title, url, "15");
                                            } else if (item == 15) {
                                                changeIcon(seqnoStr, title, url, "16");
                                            }
                                        }
                                    }).show();
                        }
                    });
                    return v;
                }
            };

            listView.setAdapter(simpleAdapter);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void changeIcon(String seqno, String title, String url, String icon) {
        try {

            final Database_Browser db = new Database_Browser(getActivity());
            db.deleteBookmark((Integer.parseInt(seqno)));
            db.addBookmark(title, url, icon);
            db.close();
            setBookmarkList();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_help:
                final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_bookmarks)
                        .setMessage(helper_main.textSpannable(getString(R.string.helpBookmarks_text)))
                        .setPositiveButton(getString(R.string.toast_yes), null);
                dialog.show();
                return true;

            case R.id.action_sort:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View dialogView = View.inflate(getActivity(), R.layout.dialog_sort_bookmarks, null);

                final CheckBox ch_title = (CheckBox) dialogView.findViewById(R.id.checkBoxTitle);
                final CheckBox ch_create = (CheckBox) dialogView.findViewById(R.id.checkBoxCreate);
                final CheckBox ch_edit = (CheckBox) dialogView.findViewById(R.id.checkBoxEdit);
                final CheckBox ch_icon = (CheckBox) dialogView.findViewById(R.id.checkBoxIcon);


                if (sharedPref.getString("sortDBB", "title").equals("title")) {
                    ch_title.setChecked(true);
                } else {
                    ch_title.setChecked(false);
                }
                if (sharedPref.getString("sortDBB", "title").equals("url")) {
                    ch_create.setChecked(true);
                } else {
                    ch_create.setChecked(false);
                }
                if (sharedPref.getString("sortDBB", "title").equals("seqno")) {
                    ch_edit.setChecked(true);
                } else {
                    ch_edit.setChecked(false);
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
                            ch_edit.setChecked(false);
                            ch_icon.setChecked(false);
                            sharedPref.edit().putString("sortDBB", "title").apply();
                            setBookmarkList();
                        }
                    }
                });
                ch_create.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if(isChecked){
                            ch_edit.setChecked(false);
                            ch_icon.setChecked(false);
                            ch_title.setChecked(false);
                            sharedPref.edit().putString("sortDBB", "url").apply();
                            setBookmarkList();
                        }
                    }
                });
                ch_edit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if(isChecked){
                            ch_create.setChecked(false);
                            ch_icon.setChecked(false);
                            ch_title.setChecked(false);
                            sharedPref.edit().putString("sortDBB", "seqno").apply();
                            setBookmarkList();
                        }
                    }
                });
                ch_icon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if(isChecked){
                            ch_create.setChecked(false);
                            ch_edit.setChecked(false);
                            ch_title.setChecked(false);
                            sharedPref.edit().putString("sortDBB", "icon").apply();
                            setBookmarkList();
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