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

package de.baumann.hhsmoodle.data_subjects;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import de.baumann.hhsmoodle.R;

class Subjects_helper {

    static void switchIcon (Activity activity, String string, ImageView be) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        assert be != null;

        switch (string) {
            case "1":be.setImageResource(R.drawable.circle_red);sharedPref.edit().putString("subject_color", "1").apply();break;
            case "2":be.setImageResource(R.drawable.circle_pink);sharedPref.edit().putString("subject_color", "2").apply();break;
            case "3":be.setImageResource(R.drawable.circle_purple);sharedPref.edit().putString("subject_color", "3").apply();break;
            case "4":be.setImageResource(R.drawable.circle_blue);sharedPref.edit().putString("subject_color", "4").apply();break;
            case "5":be.setImageResource(R.drawable.circle_teal);sharedPref.edit().putString("subject_color", "5").apply();break;
            case "6":be.setImageResource(R.drawable.circle_green);sharedPref.edit().putString("subject_color", "6").apply();break;
            case "7":be.setImageResource(R.drawable.circle_lime);sharedPref.edit().putString("subject_color", "7").apply();break;
            case "8":be.setImageResource(R.drawable.circle_yellow);sharedPref.edit().putString("subject_color", "8").apply();break;
            case "9":be.setImageResource(R.drawable.circle_orange);sharedPref.edit().putString("subject_color", "9").apply();break;
            case "10":be.setImageResource(R.drawable.circle_brown);sharedPref.edit().putString("subject_color", "10").apply();break;
            case "11":be.setImageResource(R.drawable.circle_grey);sharedPref.edit().putString("subject_color", "11").apply();break;
        }
    }


}
