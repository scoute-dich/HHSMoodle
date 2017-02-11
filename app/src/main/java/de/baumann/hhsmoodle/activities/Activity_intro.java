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
        OnboarderPage onboarderPage1 = new OnboarderPage(getString(R.string.intro1_title), getString(R.string.intro1_text), R.drawable.screenshot_0);
        OnboarderPage onboarderPage2 = new OnboarderPage(getString(R.string.intro2_title), getString(R.string.intro2_text), R.drawable.screenshot_1);
        OnboarderPage onboarderPage3 = new OnboarderPage(getString(R.string.intro3_title), getString(R.string.intro3_text), R.drawable.screenshot_2);
        OnboarderPage onboarderPage4 = new OnboarderPage(getString(R.string.intro4_title), getString(R.string.intro4_text), R.drawable.screenshot_3);
        OnboarderPage onboarderPage5 = new OnboarderPage(getString(R.string.intro10_title), getString(R.string.intro10_text), R.drawable.screenshot_9);
        OnboarderPage onboarderPage6 = new OnboarderPage(getString(R.string.intro5_title), getString(R.string.intro5_text), R.drawable.screenshot_4);
        OnboarderPage onboarderPage7 = new OnboarderPage(getString(R.string.intro6_title), getString(R.string.intro6_text), R.drawable.screenshot_5);
        OnboarderPage onboarderPage8 = new OnboarderPage(getString(R.string.intro7_title), getString(R.string.intro7_text), R.drawable.screenshot_6);
        OnboarderPage onboarderPage9 = new OnboarderPage(getString(R.string.intro8_title), getString(R.string.intro8_text), R.drawable.screenshot_7);
        OnboarderPage onboarderPage10 = new OnboarderPage(getString(R.string.intro9_title), getString(R.string.intro9_text), R.drawable.screenshot_8);

        // You can define title and description colors (by default white)
        // Don't forget to set background color for your page

        onboarderPage1.setBackgroundColor(R.color.colorPrimaryDark);
        onboarderPage1.setTitleColor(R.color.colorAccent);
        onboarderPage2.setBackgroundColor(R.color.colorPrimaryDark);
        onboarderPage2.setTitleColor(R.color.colorAccent);
        onboarderPage3.setBackgroundColor(R.color.colorPrimaryDark);
        onboarderPage3.setTitleColor(R.color.colorAccent);
        onboarderPage4.setBackgroundColor(R.color.colorPrimaryDark);
        onboarderPage4.setTitleColor(R.color.colorAccent);
        onboarderPage5.setBackgroundColor(R.color.colorPrimaryDark);
        onboarderPage5.setTitleColor(R.color.colorAccent);
        onboarderPage6.setBackgroundColor(R.color.colorPrimaryDark);
        onboarderPage6.setTitleColor(R.color.colorAccent);
        onboarderPage7.setBackgroundColor(R.color.colorPrimaryDark);
        onboarderPage7.setTitleColor(R.color.colorAccent);
        onboarderPage8.setBackgroundColor(R.color.colorPrimaryDark);
        onboarderPage8.setTitleColor(R.color.colorAccent);
        onboarderPage9.setBackgroundColor(R.color.colorPrimaryDark);
        onboarderPage9.setTitleColor(R.color.colorAccent);
        onboarderPage10.setBackgroundColor(R.color.colorPrimaryDark);
        onboarderPage10.setTitleColor(R.color.colorAccent);

        // Add your pages to the list
        onboarderPages.add(onboarderPage1);
        onboarderPages.add(onboarderPage2);
        onboarderPages.add(onboarderPage3);
        onboarderPages.add(onboarderPage4);
        onboarderPages.add(onboarderPage5);
        onboarderPages.add(onboarderPage6);
        onboarderPages.add(onboarderPage7);
        onboarderPages.add(onboarderPage8);
        onboarderPages.add(onboarderPage9);
        onboarderPages.add(onboarderPage10);

        // And pass your pages to 'setOnboardPagesReady' method
        setActiveIndicatorColor(R.color.colorAccent);
        setInactiveIndicatorColor(R.color.colorPrimary);
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
        sharedPref.edit().putBoolean("showIntroScreen_notShow", false).apply();
        Intent mainIntent = new Intent(Activity_intro.this, Activity_splash.class);
        startActivity(mainIntent);
        Activity_intro.this.finish();
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
    }
}
