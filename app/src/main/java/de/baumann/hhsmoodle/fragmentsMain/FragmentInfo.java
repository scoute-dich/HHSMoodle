package de.baumann.hhsmoodle.fragmentsMain;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import de.baumann.hhsmoodle.HHS_Browser;
import de.baumann.hhsmoodle.Notes_MainActivity;
import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.CustomListAdapter;
import de.baumann.hhsmoodle.helper.Database_Notes;


public class FragmentInfo extends Fragment {

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final String[] itemTITLE ={
                getString(R.string.text_tit_1),
                getString(R.string.text_tit_2),
                getString(R.string.text_tit_3),
                getString(R.string.text_tit_4),
                getString(R.string.text_tit_5),
                getString(R.string.text_tit_6),
                getString(R.string.text_tit_7),
        };

        final String[] itemURL ={
                "https://moodle.huebsch.ka.schule-bw.de/moodle/my/",
                "https://moodle.huebsch.ka.schule-bw.de/moodle/user/profile.php?id=4",
                "https://moodle.huebsch.ka.schule-bw.de/moodle/grade/report/overview/index.php",
                "https://moodle.huebsch.ka.schule-bw.de/moodle/message/index.php",
                "https://moodle.huebsch.ka.schule-bw.de/moodle/user/preferences.php",
                "http://www.huebsch-ka.de/",
                "https://startpage.com/",
        };

            final String[] itemDES ={
                getString(R.string.text_des_1),
                getString(R.string.text_des_2),
                getString(R.string.text_des_3),
                getString(R.string.text_des_4),
                getString(R.string.text_des_5),
                getString(R.string.text_des_6),
                getString(R.string.text_des_7),
        };

        Integer[] imgid={
                R.drawable.ic_view_dashboard_grey600_48dp,
                R.drawable.ic_face_profile_grey600_48dp,
                R.drawable.ic_chart_areaspline_grey600_48dp,
                R.drawable.ic_bell_grey600_48dp,
                R.drawable.ic_settings_grey600_48dp,
                R.drawable.ic_web_grey600_48dp,
                R.drawable.ic_magnify_grey600_48dp,
        };

        View rootView = inflater.inflate(R.layout.fragment_screen_main, container, false);

        ImageView imgHeader = (ImageView) rootView.findViewById(R.id.imageView_header);
        if(imgHeader != null) {
            TypedArray images = getResources().obtainTypedArray(R.array.splash_images);
            int choice = (int) (Math.random() * images.length());
            imgHeader.setImageResource(images.getResourceId(choice, R.drawable.splash1));
            images.recycle();
        }

        CustomListAdapter adapter=new CustomListAdapter(getActivity(), itemTITLE, itemURL, itemDES, imgid);
        listView = (ListView)rootView.findViewById(R.id.bookmarks);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String Selecteditem= itemURL[+position];
                Intent intent = new Intent(getActivity(), HHS_Browser.class);
                intent.putExtra("url", Selecteditem);
                startActivityForResult(intent, 100);
                getActivity().finish();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final String title = itemTITLE[+position];
                final String url = itemURL[+position];

                final CharSequence[] options = {getString(R.string.bookmark_edit_fav),
                        getString(R.string.bookmark_createNote),
                        getString(R.string.bookmark_CreateNotification)};
                new AlertDialog.Builder(getActivity())
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {

                                if (options[item].equals (getString(R.string.bookmark_edit_fav))) {
                                    final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    sharedPref.edit()
                                            .putString("favoriteURL", url)
                                            .putString("favoriteTitle", title)
                                            .apply();
                                    Snackbar.make(listView, R.string.bookmark_setFav, Snackbar.LENGTH_LONG).show();
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

        return rootView;
    }
}
