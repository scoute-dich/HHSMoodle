package de.baumann.hhsmoodle.data_notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.chyrta.onboarder.OnboarderActivity;
import com.chyrta.onboarder.OnboarderPage;

import java.util.ArrayList;
import java.util.List;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.activities.Activity_splash;

public class Notes_Help extends OnboarderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<OnboarderPage> onboarderPages = new ArrayList<>();

        // Create your first page
        OnboarderPage onboarderPage1 = new OnboarderPage(getString(R.string.note_help1), getString(R.string.note_help_text1), R.drawable.help_note_1);
        OnboarderPage onboarderPage2 = new OnboarderPage(getString(R.string.note_help2), getString(R.string.note_help_text2), R.drawable.help_note_2);

        // You can define title and description colors (by default white)
        // Don't forget to set background color for your page

        onboarderPage1.setBackgroundColor(R.color.colorPrimaryDark);
        onboarderPage1.setTitleColor(R.color.colorAccent);
        onboarderPage2.setBackgroundColor(R.color.colorPrimaryDark);
        onboarderPage2.setTitleColor(R.color.colorAccent);

        // Add your pages to the list
        onboarderPages.add(onboarderPage1);
        onboarderPages.add(onboarderPage2);

        // And pass your pages to 'setOnboardPagesReady' method
        setActiveIndicatorColor(android.R.color.white);
        setInactiveIndicatorColor(android.R.color.darker_gray);
        shouldDarkenButtonsLayout(true);
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
        Intent mainIntent = new Intent(Notes_Help.this, Activity_splash.class);
        startActivity(mainIntent);
        Notes_Help.this.finish();
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
    }
}
