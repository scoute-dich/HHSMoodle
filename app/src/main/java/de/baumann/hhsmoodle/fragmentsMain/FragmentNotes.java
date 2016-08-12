package de.baumann.hhsmoodle.fragmentsMain;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.Database_Notes;
import de.baumann.hhsmoodle.Notes_MainActivity;


public class FragmentNotes extends Fragment {

    private ListView listView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_screen_main, container, false);

        setHasOptionsMenu(true);
        checkFirstRun();

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

                final String title = map.get("title");
                final String cont = map.get("cont");

                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setGravity(Gravity.CENTER_HORIZONTAL);
                layout.setPadding(50, 0, 50, 0);

                final TextView textTitle = new TextView(getContext());
                textTitle.setText(title);
                textTitle.setTextSize(24);
                textTitle.setTypeface(null, Typeface.BOLD);
                textTitle.setPadding(5,50,0,0);
                layout.addView(textTitle);

                final TextView textContent = new TextView(getContext());
                textContent.setText(cont);
                textContent.setPadding(5,25,0,0);
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
                final String url = map.get("url");
                final String cont = map.get("cont");

                final LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setGravity(Gravity.CENTER_HORIZONTAL);

                final EditText inputTitle = new EditText(getActivity());
                inputTitle.setSingleLine(false);
                inputTitle.setSelection(inputTitle.getText().length());
                layout.setPadding(30, 0, 50, 0);
                layout.addView(inputTitle);

                final CharSequence[] options = {getString(R.string.note_edit_title),
                        getString(R.string.note_edit_content),
                        getString(R.string.note_set_not),
                        getString(R.string.note_share),
                        getString(R.string.note_remove_note)};
                new AlertDialog.Builder(getActivity())
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals(getString(R.string.note_edit_title))) {
                                    try {
                                        inputTitle.setText(title);
                                        final Database_Notes db = new Database_Notes(getActivity());
                                        db.deleteBookmark((Integer.parseInt(seqnoStr)));
                                        final AlertDialog.Builder dialog2 = new AlertDialog.Builder(getActivity())
                                                .setTitle(getString(R.string.note_edit_title))
                                                .setView(layout)
                                                .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        String textTitle = inputTitle.getText().toString().trim();
                                                        db.addBookmark(textTitle, url, cont);
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

                                if (options[item].equals (getString(R.string.note_edit_content))) {
                                    try {
                                        inputTitle.setText(cont);
                                        final Database_Notes db = new Database_Notes(getActivity());
                                        db.deleteBookmark((Integer.parseInt(seqnoStr)));
                                        final AlertDialog.Builder dialog2 = new AlertDialog.Builder(getActivity())
                                                .setTitle(getString(R.string.note_edit_content))
                                                .setView(layout)
                                                .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        String textContent = inputTitle.getText().toString().trim();
                                                        db.addBookmark(title, url, textContent);
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
                                            .apply();

                                    Intent intent_in = new Intent(getActivity(), Notes_MainActivity.class);
                                    startActivity(intent_in);
                                    getActivity().overridePendingTransition(0, 0);

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
    }

    private void checkFirstRun() {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (sharedPref.getBoolean ("first_note", false)){
            final SpannableString s = new SpannableString(Html.fromHtml(getString(R.string.firstNote_text)));
            Linkify.addLinks(s, Linkify.WEB_URLS);

            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.firstNote_title)
                    .setMessage(s)
                    .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                            sharedPref.edit()
                                    .putBoolean("first_note", false)
                                    .apply();
                        }
                    });
            dialog.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_folder:

                final File directory = new File(Environment.getExternalStorageDirectory() + "/HHS_Moodle/");
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(directory), "resource/folder");

                try {
                    startActivity (target);
                } catch (ActivityNotFoundException e) {
                    Snackbar.make(listView, R.string.toast_install_folder, Snackbar.LENGTH_LONG).show();
                }

            case R.id.action_not:

                final String url = "noURL";

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
                    titleEdit.setText("");
                    layout.addView(titleEdit);

                    final TextView contentText = new TextView(getActivity());
                    contentText.setText(R.string.note_edit_content);
                    contentText.setPadding(5,25,0,0);
                    layout.addView(contentText);

                    final EditText contentEdit = new EditText(getActivity());
                    contentEdit.setText("");
                    layout.addView(contentEdit);

                    ScrollView sv = new ScrollView(getActivity());
                    sv.pageScroll(0);
                    sv.setBackgroundColor(0);
                    sv.setScrollbarFadingEnabled(true);
                    sv.setVerticalFadingEdgeEnabled(false);
                    sv.addView(layout);

                    final Database_Notes db = new Database_Notes(getActivity());
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                            .setView(sv)
                            .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String inputTitle = titleEdit.getText().toString().trim();
                                    String inputContent = contentEdit.getText().toString().trim();
                                    db.addBookmark(inputTitle, url, inputContent);
                                    db.close();
                                    setBookmarkList();
                                }
                            })
                            .setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.cancel();
                                }
                            });
                    dialog.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return super.onOptionsItemSelected(item);
    }
}
