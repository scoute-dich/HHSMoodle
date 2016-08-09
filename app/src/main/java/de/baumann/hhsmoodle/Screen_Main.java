package de.baumann.hhsmoodle;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.baumann.hhsmoodle.fragmentsMain.FragmentBookmark;
import de.baumann.hhsmoodle.fragmentsMain.FragmentInfo;
import de.baumann.hhsmoodle.helper.SplashActivity;
import de.baumann.hhsmoodle.helper.Start;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Screen_Main extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
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
                        Intent intent_in = new Intent(Screen_Main.this, Start.class);
                        startActivity(intent_in);
                        overridePendingTransition(0, 0);
                        finish();
                    } else if (startType.equals("1")) {
                        Intent intent_in = new Intent(Screen_Main.this, Screen_Main.class);
                        startActivity(intent_in);
                        overridePendingTransition(0, 0);
                        finish();
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

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (sharedPref.getBoolean ("perm_notShow", false)){
                int hasWRITE_EXTERNAL_STORAGE = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        new AlertDialog.Builder(Screen_Main.this)
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

        adapter.addFragment(new FragmentInfo(), String.valueOf(getString(R.string.title_weatherInfo)));
        adapter.addFragment(new FragmentBookmark(), String.valueOf(getString(R.string.title_bookmarks)));

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
        getMenuInflater().inflate(R.menu.main_bookmarks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent_in = new Intent(Screen_Main.this, UserSettingsActivity.class);
            startActivity(intent_in);
            overridePendingTransition(0, 0);
            finish();
        }

        if (id == R.id.action_not) {

            final String title = getString(R.string.menu_rem);

            final LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setGravity(Gravity.CENTER_HORIZONTAL);
            final EditText input = new EditText(this);
            input.setSingleLine(false);
            layout.setPadding(30, 0, 50, 0);
            layout.addView(input);

            final AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setView(layout)
                    .setTitle(title)
                    .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            String longText = input.getText().toString().trim();
                            Notification.Builder mBuilder = new Notification.Builder(Screen_Main.this);

                            mBuilder.setSmallIcon(R.drawable.ic_school_white_24dp);
                            mBuilder.setContentTitle(title);
                            mBuilder.setContentText(longText);
                            mBuilder.setStyle(new Notification.BigTextStyle().bigText(longText));

                            Intent resultIntent = new Intent(Screen_Main.this, SplashActivity.class);
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(Screen_Main.this);
                            stackBuilder.addParentStack(Screen_Main.class);

                            // Adds the Intent that starts the Activity to the top of the stack
                            stackBuilder.addNextIntent(resultIntent);
                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(resultPendingIntent);

                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            Random rand = new Random();
                            int n = rand.nextInt(200);

                            // notificationID allows you to update the notification later on.
                            mNotificationManager.notify(n, mBuilder.build());
                        }
                    })
                    .setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

}