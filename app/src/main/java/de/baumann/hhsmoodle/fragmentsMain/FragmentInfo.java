package de.baumann.hhsmoodle.fragmentsMain;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;

import de.baumann.hhsmoodle.Browser;
import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.CustomListAdapter;


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

        setHasOptionsMenu(true);

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
                Intent intent = new Intent(getActivity(), Browser.class);
                intent.putExtra("url", Selecteditem);
                startActivityForResult(intent, 100);
                getActivity().finish();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final String title = itemTITLE[+position];
                final String url = itemURL[+position];

                final CharSequence[] options = {getString(R.string.bookmark_edit_fav)};
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
                            }
                        }).show();

                return true;
            }
        });

        return rootView;
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
        }
        return super.onOptionsItemSelected(item);
    }
}
