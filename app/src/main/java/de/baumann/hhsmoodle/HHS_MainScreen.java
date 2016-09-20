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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.baumann.hhsmoodle.fragmentsMain.FragmentBookmarks;
import de.baumann.hhsmoodle.fragmentsMain.FragmentInfo;
import de.baumann.hhsmoodle.fragmentsMain.FragmentNotes;
import de.baumann.hhsmoodle.helper.SplashActivity;
import de.baumann.hhsmoodle.popup.Popup_bookmarks;
import de.baumann.hhsmoodle.popup.Popup_info;
import de.baumann.hhsmoodle.popup.Popup_notes;

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
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPref.getString("protect_PW", "").length() > 0) {
            if (sharedPref.getBoolean("isOpened", true)) {
                Intent intent_in = new Intent(HHS_MainScreen.this, SplashActivity.class);
                startActivity(intent_in);
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(toolbar != null) {
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String startURL = sharedPref.getString("favoriteURL", "https://moodle.huebsch.ka.schule-bw.de/moodle/");
                    final String startType = sharedPref.getString("startType", "1");

                    if (startType.equals("2")) {
                        Intent mainIntent = new Intent(HHS_MainScreen.this, HHS_Browser.class);
                        mainIntent.putExtra("id", "1");
                        mainIntent.putExtra("url", startURL);
                        startActivity(mainIntent);
                    } else if (startType.equals("1")){
                        Intent mainIntent = new Intent(HHS_MainScreen.this, HHS_MainScreen.class);
                        mainIntent.putExtra("id", "1");
                        startActivity(mainIntent);
                    }
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
                    .setTitle(R.string.dialog_help_title)
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
    }

    private void setupViewPager(ViewPager viewPager) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final String startTab = sharedPref.getString("tabMain", "0");
        final int startTabInt = Integer.parseInt(startTab);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FragmentInfo(), String.valueOf(getString(R.string.title_info)));
        adapter.addFragment(new FragmentBookmarks(), String.valueOf(getString(R.string.title_bookmarks)));
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
    public boolean onPrepareOptionsMenu(Menu menu) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean ("help", false)){
            menu.getItem(4).setVisible(false); // here pass the index of save menu item
        }
        return super.onPrepareOptionsMenu(menu);
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

        if (id == R.id.action_shortcut) {
            final CharSequence[] options = {
                    getString(R.string.title_info),
                    getString(R.string.title_bookmarks),
                    getString(R.string.title_notes),
                    getString(R.string.bookmark_createNote)};

            new AlertDialog.Builder(HHS_MainScreen.this)
                    .setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {

                            if (options[item].equals (getString(R.string.title_info))) {
                                Intent i = new Intent(getApplicationContext(), Popup_info.class);
                                i.setAction(Intent.ACTION_MAIN);

                                Intent shortcut = new Intent();
                                shortcut.setAction(Intent.ACTION_MAIN);
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
                                shortcut.putExtra("android.intent.extra.shortcut.INTENT", i);
                                shortcut.putExtra("android.intent.extra.shortcut.NAME", "THE NAME OF SHORTCUT TO BE SHOWN");
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, (getString(R.string.title_info)));
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                                        Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));
                                shortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                sendBroadcast(shortcut);
                                Snackbar.make(viewPager, R.string.toast_shortcut, Snackbar.LENGTH_LONG).show();
                            }

                            if (options[item].equals (getString(R.string.title_bookmarks))) {
                                Intent i = new Intent(getApplicationContext(), Popup_bookmarks.class);
                                i.setAction(Intent.ACTION_MAIN);

                                Intent shortcut = new Intent();
                                shortcut.setAction(Intent.ACTION_MAIN);
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
                                shortcut.putExtra("android.intent.extra.shortcut.INTENT", i);
                                shortcut.putExtra("android.intent.extra.shortcut.NAME", "THE NAME OF SHORTCUT TO BE SHOWN");
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, (getString(R.string.title_bookmarks)));
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                                        Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_bookmark));
                                shortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                sendBroadcast(shortcut);
                                Snackbar.make(viewPager, R.string.toast_shortcut, Snackbar.LENGTH_LONG).show();
                            }

                            if (options[item].equals (getString(R.string.title_notes))) {
                                Intent i = new Intent(getApplicationContext(), Popup_notes.class);
                                i.setAction(Intent.ACTION_MAIN);

                                Intent shortcut = new Intent();
                                shortcut.setAction(Intent.ACTION_MAIN);
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
                                shortcut.putExtra("android.intent.extra.shortcut.INTENT", i);
                                shortcut.putExtra("android.intent.extra.shortcut.NAME", "THE NAME OF SHORTCUT TO BE SHOWN");
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, (getString(R.string.title_notes)));
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                                        Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_note));
                                shortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                sendBroadcast(shortcut);
                                Snackbar.make(viewPager, R.string.toast_shortcut, Snackbar.LENGTH_LONG).show();
                            }

                            if (options[item].equals (getString(R.string.bookmark_createNote))) {
                                Intent i = new Intent(getApplicationContext(), de.baumann.hhsmoodle.HHS_Note.class);

                                Intent shortcut = new Intent();
                                shortcut.setAction(Intent.ACTION_MAIN);
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
                                shortcut.putExtra("android.intent.extra.shortcut.INTENT", i);
                                shortcut.putExtra("android.intent.extra.shortcut.NAME", "THE NAME OF SHORTCUT TO BE SHOWN");
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, (getString(R.string.bookmark_createNote)));
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                                        Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_note_plus));
                                shortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                sendBroadcast(shortcut);
                                Snackbar.make(viewPager, R.string.toast_shortcut, Snackbar.LENGTH_LONG).show();
                            }

                        }
                    }).show();
        }

        return super.onOptionsItemSelected(item);
    }

}