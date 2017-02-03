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

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Environment;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.baumann.hhsmoodle.fragmentsMain.FragmentBookmarks;
import de.baumann.hhsmoodle.fragmentsMain.FragmentCourseList;
import de.baumann.hhsmoodle.fragmentsMain.FragmentDice;
import de.baumann.hhsmoodle.fragmentsMain.FragmentGrades;
import de.baumann.hhsmoodle.fragmentsMain.FragmentInfo;
import de.baumann.hhsmoodle.fragmentsMain.FragmentNotes;
import de.baumann.hhsmoodle.activities.Activity_password;
import de.baumann.hhsmoodle.activities.Activity_settings;
import de.baumann.hhsmoodle.fragmentsMain.FragmentTodo;
import de.baumann.hhsmoodle.helper.class_CustomViewPager;
import de.baumann.hhsmoodle.helper.class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_encryption;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.helper.helper_notes;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class HHS_MainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ViewPager viewPager;
    private SharedPreferences sharedPref;
    private class_SecurePreferences sharedPrefSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
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
        } else if ("shortcutNotesNew_HS".equals(action)) {
            lockUI();
            viewPager.setCurrentItem(3, true);
            sharedPref.edit()
                    .putString("handleTextTitle", intent.getStringExtra(Intent.EXTRA_SUBJECT))
                    .putString("handleTextText", intent.getStringExtra(Intent.EXTRA_TEXT))
                    .putString("handleTextIcon", "3")
                    .apply();
            helper_notes.editNote(HHS_MainScreen.this);
        } else {
            helper_main.isOpened(HHS_MainScreen.this);
        }
    }

    private void setupViewPager(ViewPager viewPager) {

        final String startTab = sharedPref.getString("tabMain", "0");
        final int startTabInt = Integer.parseInt(startTab);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FragmentInfo(), String.valueOf(getString(R.string.title_info)));
        adapter.addFragment(new FragmentBookmarks(), String.valueOf(getString(R.string.title_bookmarks)));
        adapter.addFragment(new FragmentTodo(), String.valueOf(getString(R.string.todo_title)));
        adapter.addFragment(new FragmentNotes(), String.valueOf(getString(R.string.title_notes)));
        adapter.addFragment(new FragmentDice(), String.valueOf(getString(R.string.number_title)));
        adapter.addFragment(new FragmentGrades(), String.valueOf(getString(R.string.action_grades)));
        adapter.addFragment(new FragmentCourseList(), String.valueOf(getString(R.string.courseList_title)));

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

        if (id == R.id.action_not) {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

            String dateCreate = format.format(date);

            sharedPref.edit()
                    .putString("handleTextTitle", "")
                    .putString("handleTextText", "")
                    .putString("handleTextCreate", dateCreate)
                    .putString("handleTextIcon", "")
                    .putString("handleTextAttachment", "")
                    .putString("handleTextSeqno", "")
                    .apply();
            helper_notes.editNote(HHS_MainScreen.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Snackbar.make(viewPager, getString(R.string.app_encrypt) , Snackbar.LENGTH_LONG).show();
            helper_main.isClosed(HHS_MainScreen.this);

            if (sharedPref.getBoolean ("backup_aut", false)){
                try {
                    LayoutInflater inflater = HHS_MainScreen.this.getLayoutInflater();
                    View toastLayout = inflater.inflate(R.layout.toast,
                            (ViewGroup) HHS_MainScreen.this.findViewById(R.id.toast_root_view));
                    TextView header = (TextView) toastLayout.findViewById(R.id.toast_message);
                    header.setText(R.string.toast_backup);
                    Toast toast = new Toast(HHS_MainScreen.this.getApplicationContext());
                    toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(toastLayout);
                    toast.show();

                    helper_encryption.encryptBackup(HHS_MainScreen.this,"/browser_v2.db");
                    helper_encryption.encryptBackup(HHS_MainScreen.this,"/courseList_v2.db");
                    helper_encryption.encryptBackup(HHS_MainScreen.this,"/notes_v2.db");
                    helper_encryption.encryptBackup(HHS_MainScreen.this,"/random_v2.db");
                    helper_encryption.encryptBackup(HHS_MainScreen.this,"/todo_v2.db");

                } catch (Exception e) {
                    e.printStackTrace();
                    LayoutInflater inflater = HHS_MainScreen.this.getLayoutInflater();

                    View toastLayout = inflater.inflate(R.layout.toast,
                            (ViewGroup) HHS_MainScreen.this.findViewById(R.id.toast_root_view));
                    TextView header = (TextView) toastLayout.findViewById(R.id.toast_message);
                    header.setText(R.string.toast_backup_not);
                    Toast toast = new Toast(HHS_MainScreen.this.getApplicationContext());
                    toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(toastLayout);
                    toast.show();
                }
            }

            helper_encryption.encryptDatabases(HHS_MainScreen.this);
            finish();
        }
    }

    private void lockUI() {

        String pw = sharedPrefSec.getString("protect_PW");
        helper_main.isClosed(HHS_MainScreen.this);
        if (pw != null && pw.length() > 0) {
            if (sharedPref.getBoolean("isOpened", true)) {
                helper_main.switchToActivity(HHS_MainScreen.this, Activity_password.class, "", false);
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

        if (id == R.id.nav_title_info) {
            viewPager.setCurrentItem(0, true);
            setTitle(R.string.title_info);
        } else if (id == R.id.nav_bookmarks) {
            viewPager.setCurrentItem(1, true);
            setTitle(R.string.title_bookmarks);
        } else if (id == R.id.nav_todo) {
            viewPager.setCurrentItem(2, true);
            setTitle(R.string.todo_title);
        } else if (id == R.id.nav_notes) {
            viewPager.setCurrentItem(3, true);
            setTitle(R.string.title_notes);
        } else if (id == R.id.nav_random) {
            viewPager.setCurrentItem(4, true);
            setTitle(R.string.number_title);
        } else if (id == R.id.nav_grades) {
            viewPager.setCurrentItem(5, true);
            setTitle(R.string.action_grades);
        } else if (id == R.id.nav_courseList) {
            viewPager.setCurrentItem(6, true);
            setTitle(R.string.courseList_title);
        } else if (id == R.id.nav_settings) {
            helper_main.isOpened(HHS_MainScreen.this);
            helper_main.switchToActivity(HHS_MainScreen.this, Activity_settings.class, "", false);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}