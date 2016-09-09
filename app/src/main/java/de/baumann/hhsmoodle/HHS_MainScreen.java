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
import android.support.design.widget.FloatingActionButton;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.baumann.hhsmoodle.fragmentsMain.FragmentBookmark;
import de.baumann.hhsmoodle.fragmentsMain.FragmentInfo;
import de.baumann.hhsmoodle.fragmentsMain.FragmentNotes;
import de.baumann.hhsmoodle.helper.SplashActivity;

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

        if (sharedPref.getString("protect_PW", "").length() > 0) {
            if (sharedPref.getBoolean("isOpened", true)) {
                Intent intent_in = new Intent(HHS_MainScreen.this, SplashActivity.class);
                startActivity(intent_in);
                finish();
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(toolbar != null) {
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_in = new Intent(HHS_MainScreen.this, HHS_MainScreen.class);
                    startActivity(intent_in);
                }
            });

            if (sharedPref.getBoolean ("longPress", false)){
                toolbar.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(HHS_MainScreen.this);
                        sharedPref.edit()
                                .putBoolean("isOpened", true)
                                .apply();
                        finishAffinity();
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(HHS_MainScreen.this);
                String startURL = sharedPref.getString("favoriteURL", "\"https://moodle.huebsch.ka.schule-bw.de/moodle/");

                Intent intent = new Intent(HHS_MainScreen.this, HHS_Browser.class);
                intent.putExtra("url", startURL);
                startActivity(intent);
            }
        });

        File directory = new File(Environment.getExternalStorageDirectory() + "/HHS_Moodle/");
        if (!directory.exists()) {
            directory.mkdirs();
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
    public void onBackPressed() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.edit()
                .putBoolean("isOpened", true)
                .apply();
        finish();
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
            Intent intent_in = new Intent(HHS_MainScreen.this, HHS_Note.class);
            startActivity(intent_in);
        }

        return super.onOptionsItemSelected(item);
    }

}