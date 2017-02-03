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

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.databases.Database_Random;
import de.baumann.hhsmoodle.helper.class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_encryption;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.popup.Popup_courseList;

@SuppressWarnings("ConstantConditions")
public class FragmentDice extends Fragment {

    private TextView textFile;
    private ListView listView = null;
    private ScrollView scrollView;
    private FloatingActionButton fab_add;
    private FloatingActionButton fab;
    private int number;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_screen_dice, container, false);

        ImageView imgHeader = (ImageView) rootView.findViewById(R.id.imageView_header);
        if(imgHeader != null) {
            TypedArray images = getResources().obtainTypedArray(R.array.splash_images);
            int choice = (int) (Math.random() * images.length());
            imgHeader.setImageResource(images.getResourceId(choice, R.drawable.splash1));
            images.recycle();
        }

        fab_add = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        textFile = (TextView) rootView.findViewById(R.id.textFile);
        listView = (ListView)rootView.findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                @SuppressWarnings("unchecked")
                HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);
                final String text = map.get("text");

                listView.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                textFile.setText(String.valueOf(text));
                fab_add.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                @SuppressWarnings("unchecked")
                HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);
                final String seqnoStr = map.get("seqno");
                final String title = map.get("title");
                final String text = map.get("text");

                final CharSequence[] options = {
                        getString(R.string.bookmark_edit_title),
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
                                if (options[item].equals(getString(R.string.bookmark_edit_title))) {
                                    try {

                                        final Database_Random db = new Database_Random(getActivity());

                                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                                        View dialogView = View.inflate(getActivity(), R.layout.dialog_edit_title, null);

                                        final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                                        edit_title.setHint(R.string.bookmark_edit_title);
                                        edit_title.setText(title);

                                        builder.setView(dialogView);
                                        builder.setTitle(R.string.bookmark_edit_title);
                                        builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {

                                                String inputTag = edit_title.getText().toString().trim();
                                                db.deleteBookmark((Integer.parseInt(seqnoStr)));
                                                db.addBookmark(inputTag, text);
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

                                        final android.app.AlertDialog dialog2 = builder.create();
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

                                if (options[item].equals(getString(R.string.number_edit_entry))) {

                                    try {

                                        final Database_Random db = new Database_Random(getActivity());
                                        final class_SecurePreferences sharedPrefSec = new class_SecurePreferences(getActivity(), "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);

                                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                                        View dialogView = View.inflate(getActivity(), R.layout.dialog_edit_text, null);

                                        final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                                        edit_title.setHint(R.string.bookmark_edit_title);
                                        edit_title.setText(text);

                                        builder.setView(dialogView);
                                        builder.setTitle(R.string.number_edit_entry);
                                        builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {

                                                String inputTag = edit_title.getText().toString().trim();
                                                sharedPrefSec.put(title + "text", inputTag);
                                                db.deleteBookmark((Integer.parseInt(seqnoStr)));
                                                db.addBookmark(title, sharedPrefSec.getString(title + "text"));
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

                                        final android.app.AlertDialog dialog2 = builder.create();
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

                                if (options[item].equals(getString(R.string.bookmark_remove_bookmark))) {
                                    try {
                                        Database_Random db = new Database_Random(getActivity());
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
                                                                Database_Random db = new Database_Random(getActivity());
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

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper_main.isOpened(getActivity());
                Intent mainIntent = new Intent(getActivity(), Popup_courseList.class);
                mainIntent.setAction("courseList_random");
                startActivity(mainIntent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String origString = textFile.getText().toString();

                try {
                    Random rand = new Random();
                    final int n = rand.nextInt(textFile.getLineCount());

                    int startPos = textFile.getLayout().getLineStart(n);
                    int endPos = textFile.getLayout().getLineEnd(n);

                    String theLine = textFile.getText().toString().substring(startPos, endPos);
                    String theLine2 = theLine.substring(0, theLine.length()-1);
                    String text = getString(R.string.number_chosen) + " " + String.valueOf(n + 1) + " " + theLine2;

                    Spannable highlight = new SpannableString(origString);
                    highlight.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.colorAccent)), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    textFile.setText(highlight);
                    Layout layout = textFile.getLayout();
                    scrollView.scrollTo(0, layout.getLineTop(textFile.getTop() + layout.getLineForOffset(startPos)));
                    Snackbar.make(textFile, text, Snackbar.LENGTH_LONG).show();
                } catch(NumberFormatException nfe) {
                    Snackbar.make(textFile, R.string.number_error, Snackbar.LENGTH_LONG).show();
                    nfe.printStackTrace();
                }
            }
        });
        setHasOptionsMenu(true);
        setBookmarkList();
        return rootView;
    }

    private void setBookmarkList() {

        ArrayList<HashMap<String,String>> mapList = new ArrayList<>();

        try {
            Database_Random db = new Database_Random(getActivity());
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
                map.put("text", strAry[2]);
                mapList.add(map);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(
                    getActivity(),
                    mapList,
                    R.layout.list_item,
                    new String[] {"title", "text"},
                    new int[] {R.id.textView_title_notes, R.id.textView_des_notes}
            );

            listView.setAdapter(simpleAdapter);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            getActivity().setTitle(R.string.number_title);
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh () {
        setBookmarkList();
        if(getView() == null){
            return;
        }
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    // handle back button's click listener
                    if (scrollView.getVisibility() == View.VISIBLE) {
                        listView.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.GONE);
                        fab_add.setVisibility(View.VISIBLE);
                        fab.setVisibility(View.GONE);
                    } else {
                        Snackbar.make(listView, getString(R.string.app_encrypt) , Snackbar.LENGTH_LONG).show();
                        helper_main.isClosed(getActivity());

                        PreferenceManager.setDefaultValues(getActivity(), R.xml.user_settings, false);
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

                        if (sharedPref.getBoolean ("backup_aut", false)){
                            try {
                                LayoutInflater inflater = getActivity().getLayoutInflater();
                                View toastLayout = inflater.inflate(R.layout.toast,
                                        (ViewGroup) getActivity().findViewById(R.id.toast_root_view));
                                TextView header = (TextView) toastLayout.findViewById(R.id.toast_message);
                                header.setText(R.string.toast_backup);
                                Toast toast = new Toast(getActivity().getApplicationContext());
                                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(toastLayout);
                                toast.show();

                                helper_encryption.encryptBackup(getActivity(),"/browser_v2.db");
                                helper_encryption.encryptBackup(getActivity(),"/courseList_v2.db");
                                helper_encryption.encryptBackup(getActivity(),"/notes_v2.db");
                                helper_encryption.encryptBackup(getActivity(),"/random_v2.db");
                                helper_encryption.encryptBackup(getActivity(),"/todo_v2.db");

                            } catch (Exception e) {
                                e.printStackTrace();
                                LayoutInflater inflater = getActivity().getLayoutInflater();

                                View toastLayout = inflater.inflate(R.layout.toast,
                                        (ViewGroup) getActivity().findViewById(R.id.toast_root_view));
                                TextView header = (TextView) toastLayout.findViewById(R.id.toast_message);
                                header.setText(R.string.toast_backup_not);
                                Toast toast = new Toast(getActivity().getApplicationContext());
                                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(toastLayout);
                                toast.show();
                            }
                        }

                        helper_encryption.encryptDatabases(getActivity());
                        getActivity().finish();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_sort).setVisible(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dice, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.action_help:
                final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity())
                        .setTitle(R.string.number_title)
                        .setMessage(helper_main.textSpannable(getString(R.string.helpRandom_text)))
                        .setPositiveButton(getString(R.string.toast_yes), null);
                dialog.show();
                return true;

            case R.id.action_dice:
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                View dialogView = View.inflate(getActivity(), R.layout.dialog_dice, null);

                final TextView textChoose2 = (TextView) dialogView.findViewById(R.id.textChoose);
                final EditText editNumber2 = (EditText) dialogView.findViewById(R.id.editNumber);
                editNumber2.setHint(R.string.number_dice_hint);

                builder.setView(dialogView);
                builder.setTitle(R.string.number_dice);
                builder.setPositiveButton(R.string.toast_yes, null);
                builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                final android.support.v7.app.AlertDialog dialog2 = builder.create();
                // Display the custom alert dialog on interface
                dialog2.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = dialog2.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                try {
                                    number = Integer.parseInt(editNumber2.getText().toString());
                                    Random rand = new Random();
                                    int n = rand.nextInt(number);
                                    textChoose2.setText(String.valueOf(n +1));
                                } catch(NumberFormatException nfe) {
                                    Snackbar.make(textChoose2, R.string.number_dice_error, Snackbar.LENGTH_LONG).show();
                                    nfe.printStackTrace();
                                }
                            }
                        });
                    }
                });

                dialog2.show();

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        helper_main.showKeyboard(getActivity(),editNumber2);
                    }
                }, 200);
                return true;
        }

        return false;
    }
}