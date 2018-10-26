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

package de.baumann.hhsmoodle.helper;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import de.baumann.hhsmoodle.HHS_MainScreen;
import de.baumann.hhsmoodle.R;

public class Activity_settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        setTitle(R.string.menu_more_settings);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Display the fragment as the activity_screen_main content
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment {



        private void addPermissionListener() {

            Preference reset = findPreference("settings_security_permission");
            reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {

                    if (android.os.Build.VERSION.SDK_INT >= 23) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        getActivity().startActivity(intent);
                    }
                    return true;
                }
            });
        }

        private void addShowLicenseListener() {
            Preference reset = findPreference("settings_license");
            reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    builder.setTitle(R.string.dialog_license_title);
                    builder.setMessage(helper_main.textSpannable(getActivity().getString(R.string.dialog_license_text)));

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    ((TextView)dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                    return true;
                }
            });
        }

        private void addProtectListener() {

            Preference reset = findPreference("settings_security_pin");
            reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {

                    final Activity activity = getActivity();
                    final Class_SecurePreferences sharedPrefSec = new Class_SecurePreferences(activity, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);
                    final String password = sharedPrefSec.getString("settings_security_pin");

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    View dialogView = View.inflate(activity, R.layout.dialog_edit_pin, null);

                    final EditText pass_userPW = dialogView.findViewById(R.id.pass_userPin);
                    pass_userPW.setText(password);

                    builder.setView(dialogView);
                    builder.setTitle(R.string.settings_security_pin);
                    builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            String inputTag = pass_userPW.getText().toString().trim();
                            sharedPrefSec.put("settings_security_pin", inputTag);

                        }
                    });
                    builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });

                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    helper_main.showKeyboard(activity, pass_userPW);

                    return true;
                }
            });
        }

        private void addUsernameListener() {

            Preference reset = findPreference("settings_security_moodle");
            reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {
                    helper_security.setLoginData (getActivity());
                    return true;
                }
            });
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.user_settings);
            addUsernameListener();
            addProtectListener();
            addPermissionListener();
            addShowLicenseListener();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            helper_main.switchToActivity(Activity_settings.this, HHS_MainScreen.class);
        }

        return super.onOptionsItemSelected(item);
    }
}