package de.baumann.hhsmoodle.fragmentsMain;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import de.baumann.hhsmoodle.HHS_Note;
import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.Database_Notes;
import de.baumann.hhsmoodle.Notes_MainActivity;


public class FragmentNotes extends Fragment {

    private ListView listView = null;
    private SwipeRefreshLayout swipeView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_screen_main_swipe, container, false);

        setHasOptionsMenu(true);

        ImageView imgHeader = (ImageView) rootView.findViewById(R.id.imageView_header);
        if(imgHeader != null) {
            TypedArray images = getResources().obtainTypedArray(R.array.splash_images);
            int choice = (int) (Math.random() * images.length());
            imgHeader.setImageResource(images.getResourceId(choice, R.drawable.splash1));
            images.recycle();
        }

        swipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);
        assert swipeView != null;
        swipeView.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setNotesList();
            }
        });

        listView = (ListView)rootView.findViewById(R.id.bookmarks);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                @SuppressWarnings("unchecked")
                HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);

                final String title = map.get("title");
                final String text = map.get("cont");

                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setGravity(Gravity.CENTER_HORIZONTAL);
                layout.setPadding(50, 0, 50, 0);

                final TextView textTitle = new TextView(getContext());
                textTitle.setText(title);
                textTitle.setTextSize(24);
                textTitle.setTypeface(null, Typeface.BOLD);
                textTitle.setPadding(5,50,0,0);
                Linkify.addLinks(textTitle, Linkify.WEB_URLS);
                layout.addView(textTitle);

                final TextView textContent = new TextView(getContext());
                textContent.setText(text);
                textContent.setTextSize(16);
                textContent.setPadding(5,25,0,0);
                textContent.setMovementMethod(LinkMovementMethod.getInstance());
                Linkify.addLinks(textContent, Linkify.WEB_URLS);
                layout.addView(textContent);

                ScrollView sv = new ScrollView(getActivity());
                sv.pageScroll(0);
                sv.setBackgroundColor(0);
                sv.setScrollbarFadingEnabled(true);
                sv.setVerticalFadingEdgeEnabled(false);
                sv.addView(layout);

                final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                        .setView(sv)
                        .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                dialog.show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                @SuppressWarnings("unchecked")
                HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);

                final String seqnoStr = map.get("seqno");
                final String title = map.get("title");
                final String cont = map.get("cont");

                final CharSequence[] options = {
                        getString(R.string.note_edit),
                        getString(R.string.note_set_not),
                        getString(R.string.note_share),
                        getString(R.string.note_remove_note)};
                new AlertDialog.Builder(getActivity())
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals(getString(R.string.note_edit))) {
                                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    sharedPref.edit()
                                            .putString("handleTextTitle", title)
                                            .putString("handleTextText", cont)
                                            .apply();

                                    Intent intent_in = new Intent(getActivity(), HHS_Note.class);
                                    startActivity(intent_in);

                                    try {
                                        Database_Notes db = new Database_Notes(getActivity());
                                        db.deleteBookmark((Integer.parseInt(seqnoStr)));
                                        db.close();
                                        setNotesList();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (options[item].equals (getString(R.string.note_share))) {

                                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, cont);
                                    startActivity(Intent.createChooser(sharingIntent, (getString(R.string.note_share_2))));
                                }

                                if (options[item].equals(getString(R.string.note_set_not))) {
                                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    sharedPref.edit()
                                            .putString("noteTitle", title)
                                            .putString("noteContent", cont)
                                            .putBoolean("click", true)
                                            .apply();

                                    Intent intent_in = new Intent(getActivity(), Notes_MainActivity.class);
                                    startActivity(intent_in);
                                }

                                if (options[item].equals(getString(R.string.note_remove_note))) {

                                    try {
                                        Database_Notes db = new Database_Notes(getActivity());
                                        final int count = db.getRecordCount();
                                        db.close();

                                        if (count == 1) {
                                            Snackbar snackbar = Snackbar
                                                    .make(listView, R.string.note_remove_cannot, Snackbar.LENGTH_LONG);
                                            snackbar.show();

                                        } else {
                                            Snackbar snackbar = Snackbar
                                                    .make(listView, R.string.note_remove_confirmation, Snackbar.LENGTH_LONG)
                                                    .setAction(R.string.toast_yes, new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            try {
                                                                Database_Notes db = new Database_Notes(getActivity());
                                                                db.deleteBookmark(Integer.parseInt(seqnoStr));
                                                                db.close();
                                                                setNotesList();
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
                            }
                        }).show();
                return true;
            }
        });

        setNotesList();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.getIntExtra("updated", 0) == 1) {
                        setNotesList();
                    }
                }
        }
    }

    private void setNotesList() {

        ArrayList<HashMap<String,String>> mapList = new ArrayList<>();

        try {
            Database_Notes db = new Database_Notes(getActivity());
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
                map.put("cont", strAry[3]);
                mapList.add(map);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(
                    getActivity(),
                    mapList,
                    R.layout.list_item_note,
                    new String[] {"title", "cont"},
                    new int[] {R.id.textView_title, R.id.textView_des}
            );

            listView.setAdapter(simpleAdapter);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        swipeView.setRefreshing(false);
    }
}