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

import android.os.Bundle;
import android.os.StrictMode;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import de.baumann.hhsmoodle.helper.Fragment_Browser;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.helper.helper_security;

public class HHS_MainScreen extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        helper_main.applyTheme(this);
        setContentView(R.layout.activity_screen_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        helper_security.checkPin(HHS_MainScreen.this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        onNewIntent(getIntent());
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Fragment_Browser(), getString(R.string.app_name));
        viewPager.setAdapter(adapter);
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

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBackPressed() {
        Fragment_Browser fragmentBrowser = (Fragment_Browser) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
        fragmentBrowser.doBack();
    }
}