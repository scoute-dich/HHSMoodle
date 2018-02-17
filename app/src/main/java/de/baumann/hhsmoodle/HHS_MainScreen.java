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

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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

import java.util.ArrayList;
import java.util.List;

import de.baumann.hhsmoodle.data_count.Count_Fragment;
import de.baumann.hhsmoodle.data_files.Files_Fragment;
import de.baumann.hhsmoodle.data_bookmarks.Bookmarks_Fragment;
import de.baumann.hhsmoodle.data_notes.Notes_helper;
import de.baumann.hhsmoodle.data_random.Random_Fragment;
import de.baumann.hhsmoodle.data_courses.Courses_Fragment;
import de.baumann.hhsmoodle.data_notes.Notes_Fragment;
import de.baumann.hhsmoodle.data_todo.Todo_Fragment;
import de.baumann.hhsmoodle.fragmentsMain.FragmentBrowser;
import de.baumann.hhsmoodle.fragmentsMain.FragmentGrades;
import de.baumann.hhsmoodle.activities.Activity_settings;
import de.baumann.hhsmoodle.helper.class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_security;
import de.baumann.hhsmoodle.helper.helper_main;

import static de.baumann.hhsmoodle.helper.helper_main.appDir;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class HHS_MainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ViewPager viewPager;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_screen_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        helper_security.checkPin(HHS_MainScreen.this);
        helper_security.grantPermissions(HHS_MainScreen.this);
        helper_main.onStart(HHS_MainScreen.this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.edit().putString("browserLoad", "").apply();
        class_SecurePreferences sharedPrefSec = new class_SecurePreferences(HHS_MainScreen.this, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView =  navigationView.getHeaderView(0);
        TextView nav_user = headerView.findViewById(R.id.usernameNav);
        nav_user.setText(sharedPrefSec.getString("username"));

        TypedArray images = getResources().obtainTypedArray(R.array.splash_images);
        int choice = (int) (Math.random() * images.length());
        headerView.setBackgroundResource(images.getResourceId(choice, R.drawable.splash1));
        images.recycle();

        if (!appDir().exists()) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                int hasWRITE_EXTERNAL_STORAGE = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasWRITE_EXTERNAL_STORAGE == PackageManager.PERMISSION_GRANTED) {
                    if (!appDir().exists()) {
                        appDir().mkdirs();
                    }  else {
                        helper_security.grantPermissions(HHS_MainScreen.this);
                    }
                }
            } else {
                appDir().mkdirs();
            }
        }

        onNewIntent(getIntent());
    }

    protected void onNewIntent(final Intent intent) {

        String action = intent.getAction();
        sharedPref.edit().putString("Intent", "yes").apply();

        if ("shortcutBookmarks_HS".equals(action)) {
            viewPager.setCurrentItem(1, true);
        } else if ("shortcutNotes_HS".equals(action)) {
            viewPager.setCurrentItem(4, true);
        }  else if ("shortcutNotesNew_HS".equals(action)) {
            viewPager.setCurrentItem(4, true);
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            String sharedTitle = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            Notes_helper.newNote(HHS_MainScreen.this, sharedTitle, sharedText, "19", getString(R.string.note_content), false);
        } else if ("shortcutToDo_HS".equals(action)) {
            viewPager.setCurrentItem(3, true);
        } else if ("shortcutBrowser_HS".equals(action)) {
            viewPager.setCurrentItem(0, true);
        } else if ("shortcutFiles_HS".equals(action)) {
            viewPager.setCurrentItem(2, true);
        }
    }

    private void setupViewPager(ViewPager viewPager) {

        final String startTab = sharedPref.getString("tabMain", "1");
        final int startTabInt = Integer.parseInt(startTab);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FragmentBrowser(), String.valueOf(getString(R.string.title_browser)));
        adapter.addFragment(new Bookmarks_Fragment(), String.valueOf(getString(R.string.title_bookmarks)));
        adapter.addFragment(new Files_Fragment(), String.valueOf(getString(R.string.choose_titleMain)));
        adapter.addFragment(new Todo_Fragment(), String.valueOf(getString(R.string.todo_title)));
        adapter.addFragment(new Notes_Fragment(), String.valueOf(getString(R.string.title_notes)));
        adapter.addFragment(new Random_Fragment(), String.valueOf(getString(R.string.number_title)));
        adapter.addFragment(new Count_Fragment(), String.valueOf(getString(R.string.count_title)));
        adapter.addFragment(new FragmentGrades(), String.valueOf(getString(R.string.action_grades)));
        adapter.addFragment(new Courses_Fragment(), String.valueOf(getString(R.string.courseList_title)));

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(startTabInt,true);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_finish) {
            helper_main.onClose(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(viewPager.getCurrentItem() == 0) {
            FragmentBrowser fragmentBrowser = (FragmentBrowser) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            fragmentBrowser.doBack();
        } else if(viewPager.getCurrentItem() == 1) {
            Bookmarks_Fragment bookmarks_Fragment = (Bookmarks_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            bookmarks_Fragment.doBack();
        } else if(viewPager.getCurrentItem() == 3) {
            Todo_Fragment todo_Fragment = (Todo_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            todo_Fragment.doBack();
        } else if(viewPager.getCurrentItem() == 4) {
            Notes_Fragment notes_Fragment = (Notes_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            notes_Fragment.doBack();
        } else if(viewPager.getCurrentItem() == 2) {
            Files_Fragment files_Fragment = (Files_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            files_Fragment.doBack();
        } else if(viewPager.getCurrentItem() == 5) {
            Random_Fragment random_Fragment = (Random_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            random_Fragment.doBack();
        } else if(viewPager.getCurrentItem() == 6) {
            Count_Fragment count_Fragment = (Count_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            count_Fragment.doBack();
        } else if(viewPager.getCurrentItem() == 8) {
            Courses_Fragment courses_Fragment = (Courses_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            courses_Fragment.doBack();
        } else {
            helper_main.onClose(HHS_MainScreen.this);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_browser) {
            viewPager.setCurrentItem(0, true);
            setTitle(R.string.title_browser);
        } else if (id == R.id.nav_bookmarks) {
            viewPager.setCurrentItem(1, true);
            Bookmarks_Fragment bookmarks_Fragment = (Bookmarks_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            bookmarks_Fragment.setTitle();
            bookmarks_Fragment.setBookmarksList();
        } else if (id == R.id.nav_todo) {
            viewPager.setCurrentItem(3, true);
            Todo_Fragment todo_Fragment = (Todo_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            todo_Fragment.setTitle();
            todo_Fragment.setTodoList();
        } else if (id == R.id.nav_notes) {
            viewPager.setCurrentItem(4, true);
            Notes_Fragment notes_Fragment = (Notes_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            notes_Fragment.setTitle();
            notes_Fragment.setNotesList();
        } else if (id == R.id.nav_files) {
            viewPager.setCurrentItem(2, true);
            Files_Fragment files_Fragment = (Files_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            files_Fragment.setTitle();
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                int hasWRITE_EXTERNAL_STORAGE = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasWRITE_EXTERNAL_STORAGE == PackageManager.PERMISSION_GRANTED) {
                    files_Fragment.setFilesList();
                } else {
                    helper_security.grantPermissions(this);
                }
            } else {
                files_Fragment.setFilesList();
            }
        } else if (id == R.id.nav_random) {
            viewPager.setCurrentItem(5, true);
            setTitle(R.string.number_title);
        } else if (id == R.id.nav_count) {
            viewPager.setCurrentItem(6, true);
            Count_Fragment count_Fragment = (Count_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            count_Fragment.setTitle();
            count_Fragment.setCountList();
        } else if (id == R.id.nav_grades) {
            viewPager.setCurrentItem(7, true);
            setTitle(R.string.action_grades);
        } else if (id == R.id.nav_courseList) {
            viewPager.setCurrentItem(8, true);
            setTitle(R.string.courseList_title);
        } else if (id == R.id.nav_settings) {
            helper_main.switchToActivity(HHS_MainScreen.this, Activity_settings.class, true);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}