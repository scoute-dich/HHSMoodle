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

package de.baumann.hhsmoodle.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.chyrta.onboarder.OnboarderActivity;
import com.chyrta.onboarder.OnboarderPage;

import java.util.ArrayList;
import java.util.List;

import de.baumann.hhsmoodle.R;

public class Activity_intro extends OnboarderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<OnboarderPage> onboarderPages = new ArrayList<>();

        // Create your first page
        OnboarderPage onboarderPage1 = new OnboarderPage(getString(R.string.intro1_title), getString(R.string.intro1_text), R.drawable.launcher_big);
        OnboarderPage onboarderPage7 = new OnboarderPage(getString(R.string.intro6_title), getString(R.string.intro6_text), R.drawable.screenshot_5);
        OnboarderPage onboarderPage9 = new OnboarderPage(getString(R.string.intro8_title), getString(R.string.intro8_text), R.drawable.screenshot_7);
        OnboarderPage onboarderPage10 = new OnboarderPage(getString(R.string.intro9_title), getString(R.string.intro9_text), R.drawable.screenshot_8);

        // You can define title and description colors (by default white)
        // Don't forget to set background color for your page

        onboarderPage1.setBackgroundColor(R.color.colorPrimaryDark);
        onboarderPage1.setTitleColor(R.color.colorAccent);
        onboarderPage1.setDescriptionColor(R.color.color_light);
        onboarderPage7.setBackgroundColor(R.color.colorPrimaryDark);
        onboarderPage7.setTitleColor(R.color.colorAccent);
        onboarderPage7.setDescriptionColor(R.color.color_light);
        onboarderPage9.setBackgroundColor(R.color.colorPrimaryDark);
        onboarderPage9.setTitleColor(R.color.colorAccent);
        onboarderPage9.setDescriptionColor(R.color.color_light);
        onboarderPage10.setBackgroundColor(R.color.colorPrimaryDark);
        onboarderPage10.setTitleColor(R.color.colorAccent);
        onboarderPage10.setDescriptionColor(R.color.color_light);

        // Add your pages to the list
        onboarderPages.add(onboarderPage1);
        onboarderPages.add(onboarderPage7);
        onboarderPages.add(onboarderPage9);
        onboarderPages.add(onboarderPage10);

        // And pass your pages to 'setOnboardPagesReady' method
        setActiveIndicatorColor(R.color.colorAccent);
        setInactiveIndicatorColor(R.color.color_light);
        shouldUseFloatingActionButton(true);
        setOnboardPagesReady(onboarderPages);
    }

    @Override
    public void onSkipButtonPressed() {
        // Optional: by default it skips onboarder to the end
        super.onSkipButtonPressed();
        // Define your actions when the user press 'Skip' button
    }

    @Override
    public void onFinishButtonPressed() {
        // Define your actions when the user press 'Finish' button
        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.edit().putBoolean("showIntroScreen_showIntro", false).apply();
        Intent mainIntent = new Intent(Activity_intro.this, Activity_splash.class);
        startActivity(mainIntent);
        Activity_intro.this.finish();
        overridePendingTransition(0,0);
    }
}
