package de.baumann.hhsmoodle.fragmentsMain;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import de.baumann.hhsmoodle.HHS_Browser;
import de.baumann.hhsmoodle.Notes_MainActivity;
import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.Database_Browser;
import de.baumann.hhsmoodle.helper.Database_Notes;


public class FragmentBookmark extends Fragment {

    private ListView listView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_screen_main, container, false);

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
                @SuppressWarnings("unchecked")
                HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), HHS_Browser.class);
                intent.putExtra("url", map.get("url"));
                startActivityForResult(intent, 100);
                getActivity().finish();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                @SuppressWarnings("unchecked")
                HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);
                final String seqnoStr = map.get("seqno");
                final String title = map.get("title");
                final String url = map.get("url");

                final LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setGravity(Gravity.CENTER_HORIZONTAL);
                final EditText input = new EditText(getActivity());
                input.setSingleLine(true);
                layout.setPadding(30, 0, 50, 0);
                layout.addView(input);

                final CharSequence[] options = {getString(R.string.bookmark_edit_title),
                        getString(R.string.bookmark_edit_fav),
                        getString(R.string.bookmark_createNote),
                        getString(R.string.bookmark_CreateNotification),
                        getString(R.string.bookmark_remove_bookmark)};
                new AlertDialog.Builder(getActivity())
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals(getString(R.string.bookmark_edit_title))) {
                                    try {
                                        final Database_Browser db = new Database_Browser(getActivity());
                                        db.deleteBookmark((Integer.parseInt(seqnoStr)));
                                        input.setText(title);
                                        final AlertDialog.Builder dialog2 = new AlertDialog.Builder(getActivity())
                                                .setView(layout)
                                                .setMessage(R.string.bookmark_edit_title)
                                                .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        String inputTag = input.getText().toString().trim();
                                                        db.addBookmark(inputTag, url);
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

                                    try {

                                        final LinearLayout layout = new LinearLayout(getActivity());
                                        layout.setOrientation(LinearLayout.VERTICAL);
                                        layout.setGravity(Gravity.CENTER_HORIZONTAL);
                                        layout.setPadding(50, 0, 50, 0);

                                        final TextView titleText = new TextView(getActivity());
                                        titleText.setText(R.string.note_edit_title);
                                        titleText.setPadding(5,50,0,0);
                                        layout.addView(titleText);

                                        final EditText titleEdit = new EditText(getActivity());
                                        titleEdit.setText(title);
                                        layout.addView(titleEdit);

                                        final TextView contentText = new TextView(getActivity());
                                        contentText.setText(R.string.note_edit_content);
                                        contentText.setPadding(5,25,0,0);
                                        layout.addView(contentText);

                                        final EditText contentEdit = new EditText(getActivity());
                                        String text = url + " ";
                                        contentEdit.setText(text);
                                        layout.addView(contentEdit);

                                        ScrollView sv = new ScrollView(getActivity());
                                        sv.pageScroll(0);
                                        sv.setBackgroundColor(0);
                                        sv.setScrollbarFadingEnabled(true);
                                        sv.setVerticalFadingEdgeEnabled(false);
                                        sv.addView(layout);

                                        final Database_Notes db = new Database_Notes(getActivity());
                                        final AlertDialog.Builder dialog2 = new AlertDialog.Builder(getActivity())
                                                .setView(sv)
                                                .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        String inputTitle = titleEdit.getText().toString().trim();
                                                        String inputContent = contentEdit.getText().toString().trim();
                                                        db.addBookmark(inputTitle, url, inputContent);
                                                        db.close();
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

                                if (options[item].equals (getString(R.string.bookmark_CreateNotification))) {
                                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    sharedPref.edit()
                                            .putString("noteTitle", title)
                                            .putBoolean("click", true)
                                            .apply();

                                    Intent intent_in = new Intent(getActivity(), Notes_MainActivity.class);
                                    startActivity(intent_in);
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
                mapList.add(map);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(
                    getActivity(),
                    mapList,
                    R.layout.list_item,
                    new String[] {"title", "url"},
                    new int[] {R.id.textView_title, R.id.textView_des}
            );

            listView.setAdapter(simpleAdapter);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
