package de.baumann.hhsmoodle.fragmentsMain;

import android.os.Bundle;

import com.chyrta.onboarder.OnboarderActivity;
import com.chyrta.onboarder.OnboarderPage;

import java.util.ArrayList;
import java.util.List;

import de.baumann.hhsmoodle.R;

public class FragmentBrowser_Help extends OnboarderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<OnboarderPage> onboarderPages = new ArrayList<>();

        // Create your first page
        OnboarderPage onboarderPage1 = new OnboarderPage(getString(R.string.hhs_help1), getString(R.string.hhs_help_text1), R.drawable.help_hhs_1);
        OnboarderPage onboarderPage2 = new OnboarderPage(getString(R.string.hhs_help2), getString(R.string.hhs_help_text2), R.drawable.help_hhs_2);

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
        finish();
    }
}
