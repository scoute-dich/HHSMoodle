package de.baumann.hhsmoodle.fragmentsMain;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import de.baumann.hhsmoodle.HHS_Browser;
import de.baumann.hhsmoodle.HHS_Note;
import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.Database_Browser;


public class FragmentBookmarks extends Fragment {

    private ListView listView = null;
    private SwipeRefreshLayout swipeView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_screen_main_swipe, container, false);

        ImageView imgHeader = (ImageView) rootView.findViewById(R.id.imageView_header);
        if(imgHeader != null) {
            TypedArray images = getResources().obtainTypedArray(R.array.splash_images);
            int choice = (int) (Math.random() * images.length());
            imgHeader.setImageResource(images.getResourceId(choice, R.drawable.splash1));
            images.recycle();
        }

        setHasOptionsMenu(true);

        swipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);
        assert swipeView != null;
        swipeView.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setBookmarkList();
            }
        });

        listView = (ListView)rootView.findViewById(R.id.bookmarks);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                @SuppressWarnings("unchecked")
                HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), HHS_Browser.class);
                intent.putExtra("url", map.get("url"));
                startActivity(intent);
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
                                        input.setText(title);
                                        final AlertDialog.Builder dialog2 = new AlertDialog.Builder(getActivity())
                                                .setView(layout)
                                                .setMessage(R.string.bookmark_edit_title)
                                                .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        db.deleteBookmark((Integer.parseInt(seqnoStr)));
                                                        String inputTag = input.getText().toString().trim();
                                                        db.addBookmark(inputTag, url, icon);
                                                        db.close();
                                                        setBookmarkList();
                                                    }
                                                })
                                                .setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        dialog2.show();

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
                                    final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
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

                                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    sharedPref.edit()
                                            .putString("handleTextTitle", title)
                                            .putString("handleTextText", url)
                                            .apply();

                                    Intent intent_in = new Intent(getActivity(), HHS_Note.class);
                                    startActivity(intent_in);
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
            db.getBookmarks(bookmarkList);
            if (bookmarkList.size() == 0) {
                db.loadInitialData();
                db.getBookmarks(bookmarkList);
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
                    R.layout.list_item,
                    new String[] {"title", "url"},
                    new int[] {R.id.textView_title, R.id.textView_des}
            ){
                @Override
                public View getView (final int position, final View convertView, ViewGroup parent) {

                    @SuppressWarnings("unchecked")
                    HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);
                    final String title = map.get("title");
                    final String url = map.get("url");
                    final String seqnoStr = map.get("seqno");
                    final String icon = map.get("icon");

                    View v = super.getView(position, convertView, parent);
                    ImageView i=(ImageView) v.findViewById(R.id.icon);

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
                            i.setImageResource(R.drawable.pr_green);
                            break;
                        case "15":
                            i.setImageResource(R.drawable.pr_yellow);
                            break;
                        case "16":
                            i.setImageResource(R.drawable.pr_red);
                            break;
                    }
                    i.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {

                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                            // ...Irrelevant code for customizing the buttons and title

                            if (convertView == null) {
                                LayoutInflater inflater = getActivity().getLayoutInflater();
                                View dialogView = inflater.inflate(R.layout.dialog_icon, null);
                                dialogBuilder.setView(dialogView);

                                final AlertDialog alertDialog = dialogBuilder.create();
                                alertDialog.show();
                                alertDialog.getWindow().setLayout(425, 425);

                                ImageButton ib_1 = (ImageButton) dialogView.findViewById(R.id.imageButton5);
                                ib_1.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        changeIcon(seqnoStr, title, url, "1");
                                        alertDialog.cancel();
                                    }
                                });
                                ImageButton ib_2 = (ImageButton) dialogView.findViewById(R.id.imageButton6);
                                ib_2.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        changeIcon(seqnoStr, title, url, "2");
                                        alertDialog.cancel();
                                    }
                                });
                                ImageButton ib_3 = (ImageButton) dialogView.findViewById(R.id.imageButton7);
                                ib_3.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        changeIcon(seqnoStr, title, url, "3");
                                        alertDialog.cancel();
                                    }
                                });
                                ImageButton ib_4 = (ImageButton) dialogView.findViewById(R.id.imageButton8);
                                ib_4.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        changeIcon(seqnoStr, title, url, "4");
                                        alertDialog.cancel();
                                    }
                                });
                                ImageButton ib_5 = (ImageButton) dialogView.findViewById(R.id.imageButton9);
                                ib_5.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        changeIcon(seqnoStr, title, url, "5");
                                        alertDialog.cancel();
                                    }
                                });
                                ImageButton ib_6 = (ImageButton) dialogView.findViewById(R.id.imageButton10);
                                ib_6.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        changeIcon(seqnoStr, title, url, "6");
                                        alertDialog.cancel();
                                    }
                                });
                                ImageButton ib_7 = (ImageButton) dialogView.findViewById(R.id.imageButton11);
                                ib_7.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        changeIcon(seqnoStr, title, url, "7");
                                        alertDialog.cancel();
                                    }
                                });
                                ImageButton ib_8 = (ImageButton) dialogView.findViewById(R.id.imageButton12);
                                ib_8.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        changeIcon(seqnoStr, title, url, "8");
                                        alertDialog.cancel();
                                    }
                                });
                                ImageButton ib_9 = (ImageButton) dialogView.findViewById(R.id.imageButton13);
                                ib_9.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        changeIcon(seqnoStr, title, url, "9");
                                        alertDialog.cancel();
                                    }
                                });
                                ImageButton ib_10 = (ImageButton) dialogView.findViewById(R.id.imageButton14);
                                ib_10.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        changeIcon(seqnoStr, title, url, "10");
                                        alertDialog.cancel();
                                    }
                                });
                                ImageButton ib_11 = (ImageButton) dialogView.findViewById(R.id.imageButton15);
                                ib_11.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        changeIcon(seqnoStr, title, url, "11");
                                        alertDialog.cancel();
                                    }
                                });
                                ImageButton ib_12 = (ImageButton) dialogView.findViewById(R.id.imageButton16);
                                ib_12.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        changeIcon(seqnoStr, title, url, "12");
                                        alertDialog.cancel();
                                    }
                                });
                                ImageButton ib_13 = (ImageButton) dialogView.findViewById(R.id.imageButton17);
                                ib_13.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        changeIcon(seqnoStr, title, url, "13");
                                        alertDialog.cancel();
                                    }
                                });
                                ImageButton ib_14 = (ImageButton) dialogView.findViewById(R.id.imageButton18);
                                ib_14.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        changeIcon(seqnoStr, title, url, "14");
                                        alertDialog.cancel();
                                    }
                                });
                                ImageButton ib_15 = (ImageButton) dialogView.findViewById(R.id.imageButton19);
                                ib_15.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        changeIcon(seqnoStr, title, url, "15");
                                        alertDialog.cancel();
                                    }
                                });
                                ImageButton ib_16 = (ImageButton) dialogView.findViewById(R.id.imageButton20);
                                ib_16.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        changeIcon(seqnoStr, title, url, "16");
                                        alertDialog.cancel();
                                    }
                                });
                            }
                        }
                    });
                    return v;
                }
            };

            listView.setAdapter(simpleAdapter);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        swipeView.setRefreshing(false);
    }

    private void changeIcon(String segno, String title, String url, String icon) {
        try {

            final Database_Browser db = new Database_Browser(getActivity());
            db.deleteBookmark((Integer.parseInt(segno)));
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

                final SpannableString s = new SpannableString(Html.fromHtml(getString(R.string.helpBookmarks_text)));
                Linkify.addLinks(s, Linkify.WEB_URLS);

                final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_bookmarks)
                        .setMessage(s)
                        .setPositiveButton(getString(R.string.toast_yes), null);
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
