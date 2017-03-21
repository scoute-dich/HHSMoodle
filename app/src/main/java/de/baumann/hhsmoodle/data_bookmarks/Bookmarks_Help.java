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

package de.baumann.hhsmoodle.data_bookmarks;

import android.os.Bundle;

import com.chyrta.onboarder.OnboarderActivity;
import com.chyrta.onboarder.OnboarderPage;

import java.util.ArrayList;
import java.util.List;

import de.baumann.hhsmoodle.R;

public class Bookmarks_Help extends OnboarderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<OnboarderPage> onboarderPages = new ArrayList<>();

        // Create your first page
        OnboarderPage onboarderPage1 = new OnboarderPage(getString(R.string.bookmark_help1), getString(R.string.bookmark_help_text1), R.drawable.help_bookmarks_1);
        OnboarderPage onboarderPage2 = new OnboarderPage(getString(R.string.bookmark_help2), getString(R.string.bookmark_help_text2), R.drawable.help_bookmarks_2);

        // You can define title and description colors (by default white)
        // Don't forget to set background color for your page

        onboarderPage1.setBackgroundColor(R.color.colorPrimaryDark);
        onboarderPage1.setTitleColor(R.color.colorAccent);
        onboarderPage1.setDescriptionColor(R.color.color_light);
        onboarderPage2.setBackgroundColor(R.color.colorPrimaryDark);
        onboarderPage2.setTitleColor(R.color.colorAccent);
        onboarderPage2.setDescriptionColor(R.color.color_light);

        // Add your pages to the list
        onboarderPages.add(onboarderPage1);
        onboarderPages.add(onboarderPage2);

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
        finish();
    }
}
