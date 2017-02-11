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

package de.baumann.hhsmoodle;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.baumann.hhsmoodle.data_bookmarks.Bookmarks_Fragment;
import de.baumann.hhsmoodle.data_random.Random_Fragment;
import de.baumann.hhsmoodle.data_courses.Courses_Fragment;
import de.baumann.hhsmoodle.data_notes.Notes_Fragment;
import de.baumann.hhsmoodle.data_schedule.Schedule_Fragment;
import de.baumann.hhsmoodle.data_subjects.Subjects_Fragment;
import de.baumann.hhsmoodle.data_todo.Todo_Fragment;
import de.baumann.hhsmoodle.fragmentsMain.FragmentBrowser;
import de.baumann.hhsmoodle.fragmentsMain.FragmentGrades;
import de.baumann.hhsmoodle.activities.Activity_password;
import de.baumann.hhsmoodle.activities.Activity_settings;
import de.baumann.hhsmoodle.helper.class_CustomViewPager;
import de.baumann.hhsmoodle.helper.class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_encryption;
import de.baumann.hhsmoodle.helper.helper_main;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class HHS_MainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ViewPager viewPager;
    private SharedPreferences sharedPref;
    private class_SecurePreferences sharedPrefSec;
    private OnBackPressedListener onBackPressedListener;

    public interface OnBackPressedListener {
        void doBack();
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.edit().putString("browserLoad", "").apply();
        sharedPrefSec = new class_SecurePreferences(HHS_MainScreen.this, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);

        setContentView(R.layout.activity_screen_main);

        viewPager = (class_CustomViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        helper_main.onStart(HHS_MainScreen.this);
        helper_main.grantPermissions(HHS_MainScreen.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)headerView.findViewById(R.id.usernameNav);
        nav_user.setText(sharedPrefSec.getString("username"));

        TypedArray images = getResources().obtainTypedArray(R.array.splash_images);
        int choice = (int) (Math.random() * images.length());
        headerView.setBackgroundResource(images.getResourceId(choice, R.drawable.splash1));
        images.recycle();

        File directory = new File(Environment.getExternalStorageDirectory() + "/HHS_Moodle/backup/");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        onNewIntent(getIntent());

        final NotificationManager notificationManager =
                (NotificationManager) HHS_MainScreen.this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManager.isNotificationPolicyAccessGranted()) {
            if (sharedPref.getString ("show_permission_disturb", "true").equals("true")){
                new android.app.AlertDialog.Builder(this)
                        .setTitle(R.string.app_permissions_title_dist)
                        .setMessage(helper_main.textSpannable(getString(R.string.app_permissions_dist)))
                        .setNeutralButton(R.string.toast_notAgain, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                sharedPref.edit().putString("show_permission_disturb", "false").apply();
                            }
                        })
                        .setPositiveButton(getString(R.string.toast_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManager.isNotificationPolicyAccessGranted()) {
                                    Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                    startActivity(intent);
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.toast_cancel), null)
                        .show();
            }
        }
    }

    protected void onNewIntent(final Intent intent) {

        String action = intent.getAction();

        if ("shortcutBookmarks_HS".equals(action)) {
            lockUI();
            viewPager.setCurrentItem(1, true);
        } else if ("shortcutNotes_HS".equals(action)) {
            lockUI();
            viewPager.setCurrentItem(3, true);
        } else if ("shortcutToDo_HS".equals(action)) {
            lockUI();
            viewPager.setCurrentItem(2, true);
        } else if ("shortcutBrowser_HS".equals(action)) {
            lockUI();
            viewPager.setCurrentItem(0, true);
        } else if ("shortcutSchedule_HS".equals(action)) {
            lockUI();
            viewPager.setCurrentItem(4, true);
        } else {
            helper_main.isOpened(HHS_MainScreen.this);
        }
    }

    private void setupViewPager(ViewPager viewPager) {

        final String startTab = sharedPref.getString("tabMain", "1");
        final int startTabInt = Integer.parseInt(startTab);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FragmentBrowser(), String.valueOf(getString(R.string.app_name)));
        adapter.addFragment(new Bookmarks_Fragment(), String.valueOf(getString(R.string.title_bookmarks)));
        adapter.addFragment(new Todo_Fragment(), String.valueOf(getString(R.string.todo_title)));
        adapter.addFragment(new Notes_Fragment(), String.valueOf(getString(R.string.title_notes)));
        adapter.addFragment(new Schedule_Fragment(), String.valueOf(getString(R.string.schedule_title)));
        adapter.addFragment(new Random_Fragment(), String.valueOf(getString(R.string.number_title)));
        adapter.addFragment(new FragmentGrades(), String.valueOf(getString(R.string.action_grades)));
        adapter.addFragment(new Courses_Fragment(), String.valueOf(getString(R.string.courseList_title)));
        adapter.addFragment(new Subjects_Fragment(), String.valueOf(getString(R.string.subjects_title)));

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(startTabInt,true);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
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

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);// add return null; to display only icons
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (sharedPref.getBoolean ("help", false)){
            menu.getItem(0).setVisible(false); // here pass the index of save menu item
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

        if (id == R.id.action_folder) {
            String startDir = Environment.getExternalStorageDirectory() + "/HHS_Moodle/";
            helper_main.openFilePicker(HHS_MainScreen.this, viewPager, startDir);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (onBackPressedListener != null && (viewPager.getCurrentItem() == 0 || viewPager.getCurrentItem() == 5)) {
            onBackPressedListener.doBack();
        } else {
            if (sharedPref.getBoolean("backup_aut", false)) {

                Snackbar.make(viewPager, getString(R.string.app_close), Snackbar.LENGTH_INDEFINITE).show();
                try {helper_encryption.encryptBackup(HHS_MainScreen.this, "/bookmarks_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                try {helper_encryption.encryptBackup(HHS_MainScreen.this, "/courses_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                try {helper_encryption.encryptBackup(HHS_MainScreen.this, "/notes_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                try {helper_encryption.encryptBackup(HHS_MainScreen.this, "/random_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                try {helper_encryption.encryptBackup(HHS_MainScreen.this, "/subject_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                try {helper_encryption.encryptBackup(HHS_MainScreen.this, "/schedule_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                try {helper_encryption.encryptBackup(HHS_MainScreen.this, "/todo_DB_v01.db");} catch (Exception e) {e.printStackTrace();}

                Snackbar.make(viewPager, getString(R.string.app_close), Snackbar.LENGTH_INDEFINITE).show();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Snackbar.make(viewPager, getString(R.string.app_close), Snackbar.LENGTH_INDEFINITE).show();
                        sharedPref.edit().putString("loadURL", "").apply();
                        helper_main.isClosed(HHS_MainScreen.this);
                        helper_encryption.encryptDatabases(HHS_MainScreen.this);
                        finishAffinity();
                    }
                }, 1500);

            } else {
                sharedPref.edit().putString("loadURL", "").apply();
                helper_main.isClosed(HHS_MainScreen.this);
                helper_encryption.encryptDatabases(HHS_MainScreen.this);
                finishAffinity();
            }
        }
    }

    @Override
    protected void onDestroy() {
        onBackPressedListener = null;
        super.onDestroy();
    }

    private void lockUI() {

        String pw = sharedPrefSec.getString("protect_PW");
        helper_main.isClosed(HHS_MainScreen.this);
        if (pw != null && pw.length() > 0) {
            if (sharedPref.getBoolean("isOpened", true)) {
                helper_main.switchToActivity(HHS_MainScreen.this, Activity_password.class, false);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(HHS_MainScreen.this);
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(HHS_MainScreen.this);
    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isClosed(HHS_MainScreen.this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_browser) {
            viewPager.setCurrentItem(0, true);
            setTitle(R.string.app_name);
        } else if (id == R.id.nav_bookmarks) {
            viewPager.setCurrentItem(1, true);
            setTitle(R.string.title_bookmarks);
        } else if (id == R.id.nav_todo) {
            viewPager.setCurrentItem(2, true);
            setTitle(R.string.todo_title);
        } else if (id == R.id.nav_notes) {
            viewPager.setCurrentItem(3, true);
            setTitle(R.string.title_notes);
        }  else if (id == R.id.nav_schedule) {
            viewPager.setCurrentItem(4, true);
            setTitle(R.string.schedule_title);
        } else if (id == R.id.nav_random) {
            viewPager.setCurrentItem(5, true);
            setTitle(R.string.number_title);
        } else if (id == R.id.nav_grades) {
            viewPager.setCurrentItem(6, true);
            setTitle(R.string.action_grades);
        } else if (id == R.id.nav_courseList) {
            viewPager.setCurrentItem(7, true);
            setTitle(R.string.courseList_title);
        } else if (id == R.id.nav_subjectList) {
            viewPager.setCurrentItem(8, true);
            setTitle(R.string.subjects_title);
        } else if (id == R.id.nav_settings) {
            helper_main.isOpened(HHS_MainScreen.this);
            helper_main.switchToActivity(HHS_MainScreen.this, Activity_settings.class, false);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}