package de.baumann.hhsmoodle;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.baumann.hhsmoodle.fragmentsMain.FragmentBookmark;
import de.baumann.hhsmoodle.fragmentsMain.FragmentInfo;
import de.baumann.hhsmoodle.fragmentsMain.FragmentNotes;
import de.baumann.hhsmoodle.helper.Database_Notes;
import de.baumann.hhsmoodle.helper.Start;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class HHS_MainScreen extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private ViewPager viewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager);

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(toolbar != null) {
            final String startType = sharedPref.getString("startType", "1");
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (startType.equals("2")) {
                        Intent intent_in = new Intent(HHS_MainScreen.this, Start.class);
                        startActivity(intent_in);
                        finish();
                    } else if (startType.equals("1")) {
                        Intent intent_in = new Intent(HHS_MainScreen.this, HHS_MainScreen.class);
                        startActivity(intent_in);
                    }
                }
            });

            if (sharedPref.getBoolean ("longPress", false)){
                toolbar.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        finish();
                        return true;
                    }
                });
            }
        }

        boolean show = sharedPref.getBoolean("help_notShow", true);
        if (show){
            final SpannableString s = new SpannableString(Html.fromHtml(getString(R.string.dialog_help)));
            Linkify.addLinks(s, Linkify.WEB_URLS);

            final AlertDialog.Builder dialog = new AlertDialog.Builder(HHS_MainScreen.this)
                    .setTitle(R.string.app_name)
                    .setMessage(s)
                    .setPositiveButton(getString(R.string.toast_yes), null)
                    .setNegativeButton(getString(R.string.toast_notAgain), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplication());
                            dialog.cancel();
                            sharedPref.edit()
                                    .putBoolean("help_notShow", false)
                                    .apply();
                        }
                    });
            dialog.show();
        }

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (sharedPref.getBoolean ("perm_notShow", false)){
                int hasWRITE_EXTERNAL_STORAGE = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        new AlertDialog.Builder(HHS_MainScreen.this)
                                .setMessage(R.string.app_permissions)
                                .setNeutralButton(R.string.toast_notAgain, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplication());
                                        dialog.cancel();
                                        sharedPref.edit()
                                                .putBoolean("perm_notShow", false)
                                                .apply();
                                    }
                                })
                                .setPositiveButton(getString(R.string.toast_yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (android.os.Build.VERSION.SDK_INT >= 23)
                                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                    REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                })
                                .setNegativeButton(getString(R.string.toast_cancel), null)
                                .show();
                        return;
                    }
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_PERMISSIONS);
                }
            }
        }

        File directory = new File(Environment.getExternalStorageDirectory() + "/HHS_Moodle/");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Intent intent = HHS_MainScreen.this.getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("text/")) {
                viewPager.setCurrentItem(2,true);
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final String startTab = sharedPref.getString("tabMain", "0");
        final int startTabInt = Integer.parseInt(startTab);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FragmentInfo(), String.valueOf(getString(R.string.title_weatherInfo)));
        adapter.addFragment(new FragmentBookmark(), String.valueOf(getString(R.string.title_bookmarks)));
        adapter.addFragment(new FragmentNotes(), String.valueOf(getString(R.string.title_notes)));

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(startTabInt,true);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);// add return null; to display only icons
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent_in = new Intent(HHS_MainScreen.this, HHS_UserSettingsActivity.class);
            startActivity(intent_in);
        }

        if (id == R.id.action_notifications) {
            Intent intent_in = new Intent(HHS_MainScreen.this, Notes_MainActivity.class);
            startActivity(intent_in);
        }

        if (id == R.id.action_help) {
            final SpannableString s = new SpannableString(Html.fromHtml(getString(R.string.helpMain_text)));
            Linkify.addLinks(s, Linkify.WEB_URLS);

            final AlertDialog.Builder dialog = new AlertDialog.Builder(HHS_MainScreen.this)
                    .setMessage(s)
                    .setPositiveButton(getString(R.string.toast_yes), null);
            dialog.show();
        }

        if (id == R.id.action_folder) {
            final File directory = new File(Environment.getExternalStorageDirectory() + "/HHS_Moodle/");
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(directory), "resource/folder");

            try {
                startActivity (target);
            } catch (ActivityNotFoundException e) {
                Snackbar.make(viewPager, R.string.toast_install_folder, Snackbar.LENGTH_LONG).show();
            }
        }

        if (id == R.id.action_not) {
            final String url = "noURL";

            try {

                final LinearLayout layout = new LinearLayout(HHS_MainScreen.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setGravity(Gravity.CENTER_HORIZONTAL);
                layout.setPadding(50, 0, 50, 0);

                final TextView titleText = new TextView(HHS_MainScreen.this);
                titleText.setText(R.string.note_edit_title);
                titleText.setPadding(5,50,0,0);
                layout.addView(titleText);

                final EditText titleEdit = new EditText(HHS_MainScreen.this);
                titleEdit.setText("");
                layout.addView(titleEdit);

                final TextView contentText = new TextView(HHS_MainScreen.this);
                contentText.setText(R.string.note_edit_content);
                contentText.setPadding(5,25,0,0);
                layout.addView(contentText);

                final EditText contentEdit = new EditText(HHS_MainScreen.this);
                contentEdit.setText("");
                layout.addView(contentEdit);

                ScrollView sv = new ScrollView(HHS_MainScreen.this);
                sv.pageScroll(0);
                sv.setBackgroundColor(0);
                sv.setScrollbarFadingEnabled(true);
                sv.setVerticalFadingEdgeEnabled(false);
                sv.addView(layout);

                final Database_Notes db = new Database_Notes(HHS_MainScreen.this);
                final AlertDialog.Builder dialog = new AlertDialog.Builder(HHS_MainScreen.this)
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
                dialog.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }

}