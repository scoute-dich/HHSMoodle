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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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

import de.baumann.hhsmoodle.activities.Activity_courseList;
import de.baumann.hhsmoodle.activities.Activity_dice;
import de.baumann.hhsmoodle.activities.Activity_splash;
import de.baumann.hhsmoodle.fragmentsMain.FragmentBookmarks;
import de.baumann.hhsmoodle.fragmentsMain.FragmentNotes;
import de.baumann.hhsmoodle.activities.Activity_grades;
import de.baumann.hhsmoodle.activities.Activity_password;
import de.baumann.hhsmoodle.activities.Activity_settings;
import de.baumann.hhsmoodle.fragmentsMain.FragmentTodo;
import de.baumann.hhsmoodle.helper.class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_encryption;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.helper.helper_notes;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class HHS_MainScreen extends AppCompatActivity {

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

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        helper_main.onStart(HHS_MainScreen.this);
        helper_main.grantPermissions(HHS_MainScreen.this);

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
            viewPager.setCurrentItem(0, true);
        } else if ("shortcutNotes_HS".equals(action)) {
            lockUI();
            viewPager.setCurrentItem(2, true);
        } else if ("shortcutToDo_HS".equals(action)) {
            lockUI();
            viewPager.setCurrentItem(1, true);
        } else if ("shortcutNotesNew_HS".equals(action)) {
            lockUI();
            viewPager.setCurrentItem(2, true);
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

        adapter.addFragment(new FragmentBookmarks(), String.valueOf(getString(R.string.title_bookmarks)));
        adapter.addFragment(new FragmentTodo(), String.valueOf(getString(R.string.todo_title)));
        adapter.addFragment(new FragmentNotes(), String.valueOf(getString(R.string.title_notes)));

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
            menu.getItem(6).setVisible(false); // here pass the index of save menu item
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
            helper_main.isOpened(HHS_MainScreen.this);
            helper_main.switchToActivity(HHS_MainScreen.this, Activity_settings.class, "", false);
        }

        if (id == R.id.action_folder) {
            String startDir = Environment.getExternalStorageDirectory() + "/HHS_Moodle/";
            helper_main.openFilePicker(HHS_MainScreen.this, viewPager, startDir);
        }

        if (id == R.id.action_tools) {
            final CharSequence[] options = {
                    getString(R.string.action_grades),
                    getString(R.string.number_title),
                    getString(R.string.courseList_title)};

            new AlertDialog.Builder(HHS_MainScreen.this)
                    .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    })
                    .setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {

                            if (options[item].equals (getString(R.string.action_grades))) {
                                helper_main.isOpened(HHS_MainScreen.this);
                                helper_main.switchToActivity(HHS_MainScreen.this, Activity_grades.class, "", false);
                            }

                            if (options[item].equals (getString(R.string.number_title))) {
                                helper_main.isOpened(HHS_MainScreen.this);
                                helper_main.switchToActivity(HHS_MainScreen.this, Activity_dice.class, "", false);
                            }

                            if (options[item].equals (getString(R.string.courseList_title))) {
                                helper_main.isOpened(HHS_MainScreen.this);
                                helper_main.switchToActivity(HHS_MainScreen.this, Activity_courseList.class, "", false);
                            }

                        }
                    }).show();
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

        if (id == R.id.action_shortcut) {
            final CharSequence[] options = {
                    getString(R.string.title_bookmarks),
                    getString(R.string.title_notes),
                    getString(R.string.todo_title),
                    getString(R.string.bookmark_createNote)};

            new AlertDialog.Builder(HHS_MainScreen.this)
                    .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    })
                    .setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {

                            if (options[item].equals (getString(R.string.title_bookmarks))) {
                                Intent i = new Intent(getApplicationContext(), Activity_splash.class);
                                i.setAction("shortcutBookmarks");

                                Intent shortcut = new Intent();
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, (getString(R.string.title_bookmarks)));
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                                        Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_bookmark));
                                shortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                sendBroadcast(shortcut);
                                Snackbar.make(viewPager, R.string.toast_shortcut, Snackbar.LENGTH_LONG).show();
                            }

                            if (options[item].equals (getString(R.string.title_notes))) {
                                Intent i = new Intent(getApplicationContext(), Activity_splash.class);
                                i.setAction("shortcutNotes");

                                Intent shortcut = new Intent();
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, (getString(R.string.title_notes)));
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                                        Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_note));
                                shortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                sendBroadcast(shortcut);
                                Snackbar.make(viewPager, R.string.toast_shortcut, Snackbar.LENGTH_LONG).show();
                            }

                            if (options[item].equals (getString(R.string.todo_title))) {
                                Intent i = new Intent(getApplicationContext(), Activity_splash.class);
                                i.setAction("shortcutToDo");

                                Intent shortcut = new Intent();
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, (getString(R.string.todo_title)));
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                                        Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_todo));
                                shortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                sendBroadcast(shortcut);
                                Snackbar.make(viewPager, R.string.toast_shortcut, Snackbar.LENGTH_LONG).show();
                            }

                            if (options[item].equals (getString(R.string.bookmark_createNote))) {
                                Intent i = new Intent(getApplicationContext(), Activity_splash.class);
                                i.setAction(Intent.ACTION_SEND);

                                Intent shortcut = new Intent();
                                shortcut.setAction(Intent.ACTION_MAIN);
                                shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
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

    @Override
    public void onBackPressed() {
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
}