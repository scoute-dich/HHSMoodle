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
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.baumann.hhsmoodle.helper.Activity_password;
import de.baumann.hhsmoodle.helper.class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.helper.helper_notes;

public class HHS_Grades extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private EditText maxPointsText;

    private TextView text_100_per;
    private TextView text_100_points;
    private TextView text_100_grade;

    private TextView text_99_per;
    private TextView text_99_points;
    private TextView text_99_grade;
    private TextView text_98_per;
    private TextView text_98_points;
    private TextView text_98_grade;
    private TextView text_97_per;
    private TextView text_97_points;
    private TextView text_97_grade;
    private TextView text_96_per;
    private TextView text_96_points;
    private TextView text_96_grade;
    private TextView text_95_per;
    private TextView text_95_points;
    private TextView text_95_grade;
    private TextView text_94_per;
    private TextView text_94_points;
    private TextView text_94_grade;
    private TextView text_93_per;
    private TextView text_93_points;
    private TextView text_93_grade;
    private TextView text_92_per;
    private TextView text_92_points;
    private TextView text_92_grade;
    private TextView text_91_per;
    private TextView text_91_points;
    private TextView text_91_grade;
    private TextView text_90_per;
    private TextView text_90_points;
    private TextView text_90_grade;

    private TextView text_89_per;
    private TextView text_89_points;
    private TextView text_89_grade;
    private TextView text_88_per;
    private TextView text_88_points;
    private TextView text_88_grade;
    private TextView text_87_per;
    private TextView text_87_points;
    private TextView text_87_grade;
    private TextView text_86_per;
    private TextView text_86_points;
    private TextView text_86_grade;
    private TextView text_85_per;
    private TextView text_85_points;
    private TextView text_85_grade;
    private TextView text_84_per;
    private TextView text_84_points;
    private TextView text_84_grade;
    private TextView text_83_per;
    private TextView text_83_points;
    private TextView text_83_grade;
    private TextView text_82_per;
    private TextView text_82_points;
    private TextView text_82_grade;
    private TextView text_81_per;
    private TextView text_81_points;
    private TextView text_81_grade;
    private TextView text_80_per;
    private TextView text_80_points;
    private TextView text_80_grade;

    private TextView text_79_per;
    private TextView text_79_points;
    private TextView text_79_grade;
    private TextView text_78_per;
    private TextView text_78_points;
    private TextView text_78_grade;
    private TextView text_77_per;
    private TextView text_77_points;
    private TextView text_77_grade;
    private TextView text_76_per;
    private TextView text_76_points;
    private TextView text_76_grade;
    private TextView text_75_per;
    private TextView text_75_points;
    private TextView text_75_grade;
    private TextView text_74_per;
    private TextView text_74_points;
    private TextView text_74_grade;
    private TextView text_73_per;
    private TextView text_73_points;
    private TextView text_73_grade;
    private TextView text_72_per;
    private TextView text_72_points;
    private TextView text_72_grade;
    private TextView text_71_per;
    private TextView text_71_points;
    private TextView text_71_grade;
    private TextView text_70_per;
    private TextView text_70_points;
    private TextView text_70_grade;

    private TextView text_69_per;
    private TextView text_69_points;
    private TextView text_69_grade;
    private TextView text_68_per;
    private TextView text_68_points;
    private TextView text_68_grade;
    private TextView text_67_per;
    private TextView text_67_points;
    private TextView text_67_grade;
    private TextView text_66_per;
    private TextView text_66_points;
    private TextView text_66_grade;
    private TextView text_65_per;
    private TextView text_65_points;
    private TextView text_65_grade;
    private TextView text_64_per;
    private TextView text_64_points;
    private TextView text_64_grade;
    private TextView text_63_per;
    private TextView text_63_points;
    private TextView text_63_grade;
    private TextView text_62_per;
    private TextView text_62_points;
    private TextView text_62_grade;
    private TextView text_61_per;
    private TextView text_61_points;
    private TextView text_61_grade;
    private TextView text_60_per;
    private TextView text_60_points;
    private TextView text_60_grade;

    private TextView text_59_per;
    private TextView text_59_points;
    private TextView text_59_grade;
    private TextView text_58_per;
    private TextView text_58_points;
    private TextView text_58_grade;
    private TextView text_57_per;
    private TextView text_57_points;
    private TextView text_57_grade;
    private TextView text_56_per;
    private TextView text_56_points;
    private TextView text_56_grade;
    private TextView text_55_per;
    private TextView text_55_points;
    private TextView text_55_grade;
    private TextView text_54_per;
    private TextView text_54_points;
    private TextView text_54_grade;
    private TextView text_53_per;
    private TextView text_53_points;
    private TextView text_53_grade;
    private TextView text_52_per;
    private TextView text_52_points;
    private TextView text_52_grade;
    private TextView text_51_per;
    private TextView text_51_points;
    private TextView text_51_grade;
    private TextView text_50_per;
    private TextView text_50_points;
    private TextView text_50_grade;

    private TextView text_49_per;
    private TextView text_49_points;
    private TextView text_49_grade;
    private TextView text_48_per;
    private TextView text_48_points;
    private TextView text_48_grade;
    private TextView text_47_per;
    private TextView text_47_points;
    private TextView text_47_grade;
    private TextView text_46_per;
    private TextView text_46_points;
    private TextView text_46_grade;
    private TextView text_45_per;
    private TextView text_45_points;
    private TextView text_45_grade;
    private TextView text_44_per;
    private TextView text_44_points;
    private TextView text_44_grade;
    private TextView text_43_per;
    private TextView text_43_points;
    private TextView text_43_grade;
    private TextView text_42_per;
    private TextView text_42_points;
    private TextView text_42_grade;
    private TextView text_41_per;
    private TextView text_41_points;
    private TextView text_41_grade;
    private TextView text_40_per;
    private TextView text_40_points;
    private TextView text_40_grade;

    private TextView text_39_per;
    private TextView text_39_points;
    private TextView text_39_grade;
    private TextView text_38_per;
    private TextView text_38_points;
    private TextView text_38_grade;
    private TextView text_37_per;
    private TextView text_37_points;
    private TextView text_37_grade;
    private TextView text_36_per;
    private TextView text_36_points;
    private TextView text_36_grade;
    private TextView text_35_per;
    private TextView text_35_points;
    private TextView text_35_grade;
    private TextView text_34_per;
    private TextView text_34_points;
    private TextView text_34_grade;
    private TextView text_33_per;
    private TextView text_33_points;
    private TextView text_33_grade;
    private TextView text_32_per;
    private TextView text_32_points;
    private TextView text_32_grade;
    private TextView text_31_per;
    private TextView text_31_points;
    private TextView text_31_grade;
    private TextView text_30_per;
    private TextView text_30_points;
    private TextView text_30_grade;

    private TextView text_29_per;
    private TextView text_29_points;
    private TextView text_29_grade;
    private TextView text_28_per;
    private TextView text_28_points;
    private TextView text_28_grade;
    private TextView text_27_per;
    private TextView text_27_points;
    private TextView text_27_grade;
    private TextView text_26_per;
    private TextView text_26_points;
    private TextView text_26_grade;
    private TextView text_25_per;
    private TextView text_25_points;
    private TextView text_25_grade;
    private TextView text_24_per;
    private TextView text_24_points;
    private TextView text_24_grade;
    private TextView text_23_per;
    private TextView text_23_points;
    private TextView text_23_grade;
    private TextView text_22_per;
    private TextView text_22_points;
    private TextView text_22_grade;
    private TextView text_21_per;
    private TextView text_21_points;
    private TextView text_21_grade;
    private TextView text_20_per;
    private TextView text_20_points;
    private TextView text_20_grade;

    private TextView text_19_per;
    private TextView text_19_points;
    private TextView text_19_grade;
    private TextView text_18_per;
    private TextView text_18_points;
    private TextView text_18_grade;
    private TextView text_17_per;
    private TextView text_17_points;
    private TextView text_17_grade;
    private TextView text_16_per;
    private TextView text_16_points;
    private TextView text_16_grade;
    private TextView text_15_per;
    private TextView text_15_points;
    private TextView text_15_grade;
    private TextView text_14_per;
    private TextView text_14_points;
    private TextView text_14_grade;
    private TextView text_13_per;
    private TextView text_13_points;
    private TextView text_13_grade;
    private TextView text_12_per;
    private TextView text_12_points;
    private TextView text_12_grade;
    private TextView text_11_per;
    private TextView text_11_points;
    private TextView text_11_grade;
    private TextView text_10_per;
    private TextView text_10_points;
    private TextView text_10_grade;

    private TextView text_09_per;
    private TextView text_09_points;
    private TextView text_09_grade;
    private TextView text_08_per;
    private TextView text_08_points;
    private TextView text_08_grade;
    private TextView text_07_per;
    private TextView text_07_points;
    private TextView text_07_grade;
    private TextView text_06_per;
    private TextView text_06_points;
    private TextView text_06_grade;
    private TextView text_05_per;
    private TextView text_05_points;
    private TextView text_05_grade;
    private TextView text_04_per;
    private TextView text_04_points;
    private TextView text_04_grade;
    private TextView text_03_per;
    private TextView text_03_points;
    private TextView text_03_grade;
    private TextView text_02_per;
    private TextView text_02_points;
    private TextView text_02_grade;
    private TextView text_01_per;
    private TextView text_01_points;
    private TextView text_01_grade;
    private TextView text_00_per;
    private TextView text_00_points;
    private TextView text_00_grade;

    private double maxPoints;
    private String maxPointsString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        class_SecurePreferences sharedPrefSec = new class_SecurePreferences(HHS_Grades.this, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);
        String pw = sharedPrefSec.getString("protect_PW");

        if (pw != null  && pw.length() > 0) {
            if (sharedPref.getBoolean("isOpened", true)) {
                helper_main.switchToActivity(HHS_Grades.this, Activity_password.class, "", false);
            }
        }

        setContentView(R.layout.activity_grade);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(toolbar != null) {
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String startURL = sharedPref.getString("favoriteURL", "https://moodle.huebsch.ka.schule-bw.de/moodle/");
                    final String startType = sharedPref.getString("startType", "1");

                    helper_main.resetStartTab(HHS_Grades.this);

                    if (startType.equals("2")) {
                        helper_main.isOpened(HHS_Grades.this);
                        helper_main.switchToActivity(HHS_Grades.this, HHS_Grades.class, startURL, false);
                    } else if (startType.equals("1")){
                        helper_main.isOpened(HHS_Grades.this);
                        helper_main.switchToActivity(HHS_Grades.this, HHS_MainScreen.class, "", true);
                    }
                }
            });

            if (sharedPref.getBoolean ("longPress", false)){
                toolbar.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        helper_main.resetStartTab(HHS_Grades.this);
                        helper_main.isClosed(HHS_Grades.this);
                        finishAffinity();
                        return true;
                    }
                });
            }
        }

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        maxPointsText = (EditText) findViewById(R.id.maxPoints);
        maxPointsText.setText(R.string.grade_100);

        text_100_per = (TextView) findViewById(R.id.text_100_per);
        text_100_points = (TextView) findViewById(R.id.text_100_points);
        text_100_grade = (TextView) findViewById(R.id.text_100_grade);

        text_99_per = (TextView) findViewById(R.id.text_99_per);
        text_99_points = (TextView) findViewById(R.id.text_99_points);
        text_99_grade = (TextView) findViewById(R.id.text_99_grade);
        text_98_per = (TextView) findViewById(R.id.text_98_per);
        text_98_points = (TextView) findViewById(R.id.text_98_points);
        text_98_grade = (TextView) findViewById(R.id.text_98_grade);
        text_97_per = (TextView) findViewById(R.id.text_97_per);
        text_97_points = (TextView) findViewById(R.id.text_97_points);
        text_97_grade = (TextView) findViewById(R.id.text_97_grade);
        text_96_per = (TextView) findViewById(R.id.text_96_per);
        text_96_points = (TextView) findViewById(R.id.text_96_points);
        text_96_grade = (TextView) findViewById(R.id.text_96_grade);
        text_95_per = (TextView) findViewById(R.id.text_95_per);
        text_95_points = (TextView) findViewById(R.id.text_95_points);
        text_95_grade = (TextView) findViewById(R.id.text_95_grade);
        text_94_per = (TextView) findViewById(R.id.text_94_per);
        text_94_points = (TextView) findViewById(R.id.text_94_points);
        text_94_grade = (TextView) findViewById(R.id.text_94_grade);
        text_93_per = (TextView) findViewById(R.id.text_93_per);
        text_93_points = (TextView) findViewById(R.id.text_93_points);
        text_93_grade = (TextView) findViewById(R.id.text_93_grade);
        text_92_per = (TextView) findViewById(R.id.text_92_per);
        text_92_points = (TextView) findViewById(R.id.text_92_points);
        text_92_grade = (TextView) findViewById(R.id.text_92_grade);
        text_91_per = (TextView) findViewById(R.id.text_91_per);
        text_91_points = (TextView) findViewById(R.id.text_91_points);
        text_91_grade = (TextView) findViewById(R.id.text_91_grade);
        text_90_per = (TextView) findViewById(R.id.text_90_per);
        text_90_points = (TextView) findViewById(R.id.text_90_points);
        text_90_grade = (TextView) findViewById(R.id.text_90_grade);

        text_89_per = (TextView) findViewById(R.id.text_89_per);
        text_89_points = (TextView) findViewById(R.id.text_89_points);
        text_89_grade = (TextView) findViewById(R.id.text_89_grade);
        text_88_per = (TextView) findViewById(R.id.text_88_per);
        text_88_points = (TextView) findViewById(R.id.text_88_points);
        text_88_grade = (TextView) findViewById(R.id.text_88_grade);
        text_87_per = (TextView) findViewById(R.id.text_87_per);
        text_87_points = (TextView) findViewById(R.id.text_87_points);
        text_87_grade = (TextView) findViewById(R.id.text_87_grade);
        text_86_per = (TextView) findViewById(R.id.text_86_per);
        text_86_points = (TextView) findViewById(R.id.text_86_points);
        text_86_grade = (TextView) findViewById(R.id.text_86_grade);
        text_85_per = (TextView) findViewById(R.id.text_85_per);
        text_85_points = (TextView) findViewById(R.id.text_85_points);
        text_85_grade = (TextView) findViewById(R.id.text_85_grade);
        text_84_per = (TextView) findViewById(R.id.text_84_per);
        text_84_points = (TextView) findViewById(R.id.text_84_points);
        text_84_grade = (TextView) findViewById(R.id.text_84_grade);
        text_83_per = (TextView) findViewById(R.id.text_83_per);
        text_83_points = (TextView) findViewById(R.id.text_83_points);
        text_83_grade = (TextView) findViewById(R.id.text_83_grade);
        text_82_per = (TextView) findViewById(R.id.text_82_per);
        text_82_points = (TextView) findViewById(R.id.text_82_points);
        text_82_grade = (TextView) findViewById(R.id.text_82_grade);
        text_81_per = (TextView) findViewById(R.id.text_81_per);
        text_81_points = (TextView) findViewById(R.id.text_81_points);
        text_81_grade = (TextView) findViewById(R.id.text_81_grade);
        text_80_per = (TextView) findViewById(R.id.text_80_per);
        text_80_points = (TextView) findViewById(R.id.text_80_points);
        text_80_grade = (TextView) findViewById(R.id.text_80_grade);

        text_79_points = (TextView) findViewById(R.id.text_79_points);
        text_79_grade = (TextView) findViewById(R.id.text_79_grade);
        text_78_points = (TextView) findViewById(R.id.text_78_points);
        text_78_grade = (TextView) findViewById(R.id.text_78_grade);
        text_77_points = (TextView) findViewById(R.id.text_77_points);
        text_77_grade = (TextView) findViewById(R.id.text_77_grade);
        text_76_points = (TextView) findViewById(R.id.text_76_points);
        text_76_grade = (TextView) findViewById(R.id.text_76_grade);
        text_75_points = (TextView) findViewById(R.id.text_75_points);
        text_75_grade = (TextView) findViewById(R.id.text_75_grade);
        text_74_points = (TextView) findViewById(R.id.text_74_points);
        text_74_grade = (TextView) findViewById(R.id.text_74_grade);
        text_73_points = (TextView) findViewById(R.id.text_73_points);
        text_73_grade = (TextView) findViewById(R.id.text_73_grade);
        text_72_points = (TextView) findViewById(R.id.text_72_points);
        text_72_grade = (TextView) findViewById(R.id.text_72_grade);
        text_71_points = (TextView) findViewById(R.id.text_71_points);
        text_71_grade = (TextView) findViewById(R.id.text_71_grade);
        text_70_points = (TextView) findViewById(R.id.text_70_points);
        text_70_grade = (TextView) findViewById(R.id.text_70_grade);

        text_69_points = (TextView) findViewById(R.id.text_69_points);
        text_69_grade = (TextView) findViewById(R.id.text_69_grade);
        text_68_points = (TextView) findViewById(R.id.text_68_points);
        text_68_grade = (TextView) findViewById(R.id.text_68_grade);
        text_67_points = (TextView) findViewById(R.id.text_67_points);
        text_67_grade = (TextView) findViewById(R.id.text_67_grade);
        text_66_points = (TextView) findViewById(R.id.text_66_points);
        text_66_grade = (TextView) findViewById(R.id.text_66_grade);
        text_65_points = (TextView) findViewById(R.id.text_65_points);
        text_65_grade = (TextView) findViewById(R.id.text_65_grade);
        text_64_points = (TextView) findViewById(R.id.text_64_points);
        text_64_grade = (TextView) findViewById(R.id.text_64_grade);
        text_63_points = (TextView) findViewById(R.id.text_63_points);
        text_63_grade = (TextView) findViewById(R.id.text_63_grade);
        text_62_points = (TextView) findViewById(R.id.text_62_points);
        text_62_grade = (TextView) findViewById(R.id.text_62_grade);
        text_61_points = (TextView) findViewById(R.id.text_61_points);
        text_61_grade = (TextView) findViewById(R.id.text_61_grade);
        text_60_points = (TextView) findViewById(R.id.text_60_points);
        text_60_grade = (TextView) findViewById(R.id.text_60_grade);

        text_59_points = (TextView) findViewById(R.id.text_59_points);
        text_59_grade = (TextView) findViewById(R.id.text_59_grade);
        text_58_points = (TextView) findViewById(R.id.text_58_points);
        text_58_grade = (TextView) findViewById(R.id.text_58_grade);
        text_57_points = (TextView) findViewById(R.id.text_57_points);
        text_57_grade = (TextView) findViewById(R.id.text_57_grade);
        text_56_points = (TextView) findViewById(R.id.text_56_points);
        text_56_grade = (TextView) findViewById(R.id.text_56_grade);
        text_55_points = (TextView) findViewById(R.id.text_55_points);
        text_55_grade = (TextView) findViewById(R.id.text_55_grade);
        text_54_points = (TextView) findViewById(R.id.text_54_points);
        text_54_grade = (TextView) findViewById(R.id.text_54_grade);
        text_53_points = (TextView) findViewById(R.id.text_53_points);
        text_53_grade = (TextView) findViewById(R.id.text_53_grade);
        text_52_points = (TextView) findViewById(R.id.text_52_points);
        text_52_grade = (TextView) findViewById(R.id.text_52_grade);
        text_51_points = (TextView) findViewById(R.id.text_51_points);
        text_51_grade = (TextView) findViewById(R.id.text_51_grade);
        text_50_points = (TextView) findViewById(R.id.text_50_points);
        text_50_grade = (TextView) findViewById(R.id.text_50_grade);

        text_49_points = (TextView) findViewById(R.id.text_49_points);
        text_49_grade = (TextView) findViewById(R.id.text_49_grade);
        text_48_points = (TextView) findViewById(R.id.text_48_points);
        text_48_grade = (TextView) findViewById(R.id.text_48_grade);
        text_47_points = (TextView) findViewById(R.id.text_47_points);
        text_47_grade = (TextView) findViewById(R.id.text_47_grade);
        text_46_points = (TextView) findViewById(R.id.text_46_points);
        text_46_grade = (TextView) findViewById(R.id.text_46_grade);
        text_45_points = (TextView) findViewById(R.id.text_45_points);
        text_45_grade = (TextView) findViewById(R.id.text_45_grade);
        text_44_points = (TextView) findViewById(R.id.text_44_points);
        text_44_grade = (TextView) findViewById(R.id.text_44_grade);
        text_43_points = (TextView) findViewById(R.id.text_43_points);
        text_43_grade = (TextView) findViewById(R.id.text_43_grade);
        text_42_points = (TextView) findViewById(R.id.text_42_points);
        text_42_grade = (TextView) findViewById(R.id.text_42_grade);
        text_41_points = (TextView) findViewById(R.id.text_41_points);
        text_41_grade = (TextView) findViewById(R.id.text_41_grade);
        text_40_points = (TextView) findViewById(R.id.text_40_points);
        text_40_grade = (TextView) findViewById(R.id.text_40_grade);

        text_39_points = (TextView) findViewById(R.id.text_39_points);
        text_39_grade = (TextView) findViewById(R.id.text_39_grade);
        text_38_points = (TextView) findViewById(R.id.text_38_points);
        text_38_grade = (TextView) findViewById(R.id.text_38_grade);
        text_37_points = (TextView) findViewById(R.id.text_37_points);
        text_37_grade = (TextView) findViewById(R.id.text_37_grade);
        text_36_points = (TextView) findViewById(R.id.text_36_points);
        text_36_grade = (TextView) findViewById(R.id.text_36_grade);
        text_35_points = (TextView) findViewById(R.id.text_35_points);
        text_35_grade = (TextView) findViewById(R.id.text_35_grade);
        text_34_points = (TextView) findViewById(R.id.text_34_points);
        text_34_grade = (TextView) findViewById(R.id.text_34_grade);
        text_33_points = (TextView) findViewById(R.id.text_33_points);
        text_33_grade = (TextView) findViewById(R.id.text_33_grade);
        text_32_points = (TextView) findViewById(R.id.text_32_points);
        text_32_grade = (TextView) findViewById(R.id.text_32_grade);
        text_31_points = (TextView) findViewById(R.id.text_31_points);
        text_31_grade = (TextView) findViewById(R.id.text_31_grade);
        text_30_points = (TextView) findViewById(R.id.text_30_points);
        text_30_grade = (TextView) findViewById(R.id.text_30_grade);

        text_29_points = (TextView) findViewById(R.id.text_29_points);
        text_29_grade = (TextView) findViewById(R.id.text_29_grade);
        text_28_points = (TextView) findViewById(R.id.text_28_points);
        text_28_grade = (TextView) findViewById(R.id.text_28_grade);
        text_27_points = (TextView) findViewById(R.id.text_27_points);
        text_27_grade = (TextView) findViewById(R.id.text_27_grade);
        text_26_points = (TextView) findViewById(R.id.text_26_points);
        text_26_grade = (TextView) findViewById(R.id.text_26_grade);
        text_25_points = (TextView) findViewById(R.id.text_25_points);
        text_25_grade = (TextView) findViewById(R.id.text_25_grade);
        text_24_points = (TextView) findViewById(R.id.text_24_points);
        text_24_grade = (TextView) findViewById(R.id.text_24_grade);
        text_23_points = (TextView) findViewById(R.id.text_23_points);
        text_23_grade = (TextView) findViewById(R.id.text_23_grade);
        text_22_points = (TextView) findViewById(R.id.text_22_points);
        text_22_grade = (TextView) findViewById(R.id.text_22_grade);
        text_21_points = (TextView) findViewById(R.id.text_21_points);
        text_21_grade = (TextView) findViewById(R.id.text_21_grade);
        text_20_points = (TextView) findViewById(R.id.text_20_points);
        text_20_grade = (TextView) findViewById(R.id.text_20_grade);

        text_19_points = (TextView) findViewById(R.id.text_19_points);
        text_19_grade = (TextView) findViewById(R.id.text_19_grade);
        text_18_points = (TextView) findViewById(R.id.text_18_points);
        text_18_grade = (TextView) findViewById(R.id.text_18_grade);
        text_17_points = (TextView) findViewById(R.id.text_17_points);
        text_17_grade = (TextView) findViewById(R.id.text_17_grade);
        text_16_points = (TextView) findViewById(R.id.text_16_points);
        text_16_grade = (TextView) findViewById(R.id.text_16_grade);
        text_15_points = (TextView) findViewById(R.id.text_15_points);
        text_15_grade = (TextView) findViewById(R.id.text_15_grade);
        text_14_points = (TextView) findViewById(R.id.text_14_points);
        text_14_grade = (TextView) findViewById(R.id.text_14_grade);
        text_13_points = (TextView) findViewById(R.id.text_13_points);
        text_13_grade = (TextView) findViewById(R.id.text_13_grade);
        text_12_points = (TextView) findViewById(R.id.text_12_points);
        text_12_grade = (TextView) findViewById(R.id.text_12_grade);
        text_11_points = (TextView) findViewById(R.id.text_11_points);
        text_11_grade = (TextView) findViewById(R.id.text_11_grade);
        text_10_points = (TextView) findViewById(R.id.text_10_points);
        text_10_grade = (TextView) findViewById(R.id.text_10_grade);

        text_09_points = (TextView) findViewById(R.id.text_09_points);
        text_09_grade = (TextView) findViewById(R.id.text_09_grade);
        text_08_points = (TextView) findViewById(R.id.text_08_points);
        text_08_grade = (TextView) findViewById(R.id.text_08_grade);
        text_07_points = (TextView) findViewById(R.id.text_07_points);
        text_07_grade = (TextView) findViewById(R.id.text_07_grade);
        text_06_points = (TextView) findViewById(R.id.text_06_points);
        text_06_grade = (TextView) findViewById(R.id.text_06_grade);
        text_05_points = (TextView) findViewById(R.id.text_05_points);
        text_05_grade = (TextView) findViewById(R.id.text_05_grade);
        text_04_points = (TextView) findViewById(R.id.text_04_points);
        text_04_grade = (TextView) findViewById(R.id.text_04_grade);
        text_03_points = (TextView) findViewById(R.id.text_03_points);
        text_03_grade = (TextView) findViewById(R.id.text_03_grade);
        text_02_points = (TextView) findViewById(R.id.text_02_points);
        text_02_grade = (TextView) findViewById(R.id.text_02_grade);
        text_01_points = (TextView) findViewById(R.id.text_01_points);
        text_01_grade = (TextView) findViewById(R.id.text_01_grade);
        text_00_points = (TextView) findViewById(R.id.text_00_points);
        text_00_grade = (TextView) findViewById(R.id.text_00_grade);

        text_79_per = (TextView) findViewById(R.id.text_79_per);
        text_78_per = (TextView) findViewById(R.id.text_78_per);
        text_77_per = (TextView) findViewById(R.id.text_77_per);
        text_76_per = (TextView) findViewById(R.id.text_76_per);
        text_75_per = (TextView) findViewById(R.id.text_75_per);
        text_74_per = (TextView) findViewById(R.id.text_74_per);
        text_73_per = (TextView) findViewById(R.id.text_73_per);
        text_72_per = (TextView) findViewById(R.id.text_72_per);
        text_71_per = (TextView) findViewById(R.id.text_71_per);
        text_70_per = (TextView) findViewById(R.id.text_70_per);

        text_69_per = (TextView) findViewById(R.id.text_69_per);
        text_68_per = (TextView) findViewById(R.id.text_68_per);
        text_67_per = (TextView) findViewById(R.id.text_67_per);
        text_66_per = (TextView) findViewById(R.id.text_66_per);
        text_65_per = (TextView) findViewById(R.id.text_65_per);
        text_64_per = (TextView) findViewById(R.id.text_64_per);
        text_63_per = (TextView) findViewById(R.id.text_63_per);
        text_62_per = (TextView) findViewById(R.id.text_62_per);
        text_61_per = (TextView) findViewById(R.id.text_61_per);
        text_60_per = (TextView) findViewById(R.id.text_60_per);

        text_59_per = (TextView) findViewById(R.id.text_59_per);
        text_58_per = (TextView) findViewById(R.id.text_58_per);
        text_57_per = (TextView) findViewById(R.id.text_57_per);
        text_56_per = (TextView) findViewById(R.id.text_56_per);
        text_55_per = (TextView) findViewById(R.id.text_55_per);
        text_54_per = (TextView) findViewById(R.id.text_54_per);
        text_53_per = (TextView) findViewById(R.id.text_53_per);
        text_52_per = (TextView) findViewById(R.id.text_52_per);
        text_51_per = (TextView) findViewById(R.id.text_51_per);
        text_50_per = (TextView) findViewById(R.id.text_50_per);

        text_49_per = (TextView) findViewById(R.id.text_49_per);
        text_48_per = (TextView) findViewById(R.id.text_48_per);
        text_47_per = (TextView) findViewById(R.id.text_47_per);
        text_46_per = (TextView) findViewById(R.id.text_46_per);
        text_45_per = (TextView) findViewById(R.id.text_45_per);
        text_44_per = (TextView) findViewById(R.id.text_44_per);
        text_43_per = (TextView) findViewById(R.id.text_43_per);
        text_42_per = (TextView) findViewById(R.id.text_42_per);
        text_41_per = (TextView) findViewById(R.id.text_41_per);
        text_40_per = (TextView) findViewById(R.id.text_40_per);

        text_39_per = (TextView) findViewById(R.id.text_39_per);
        text_38_per = (TextView) findViewById(R.id.text_38_per);
        text_37_per = (TextView) findViewById(R.id.text_37_per);
        text_36_per = (TextView) findViewById(R.id.text_36_per);
        text_35_per = (TextView) findViewById(R.id.text_35_per);
        text_34_per = (TextView) findViewById(R.id.text_34_per);
        text_33_per = (TextView) findViewById(R.id.text_33_per);
        text_32_per = (TextView) findViewById(R.id.text_32_per);
        text_31_per = (TextView) findViewById(R.id.text_31_per);
        text_30_per = (TextView) findViewById(R.id.text_30_per);

        text_29_per = (TextView) findViewById(R.id.text_29_per);
        text_28_per = (TextView) findViewById(R.id.text_28_per);
        text_27_per = (TextView) findViewById(R.id.text_27_per);
        text_26_per = (TextView) findViewById(R.id.text_26_per);
        text_25_per = (TextView) findViewById(R.id.text_25_per);
        text_24_per = (TextView) findViewById(R.id.text_24_per);
        text_23_per = (TextView) findViewById(R.id.text_23_per);
        text_22_per = (TextView) findViewById(R.id.text_22_per);
        text_21_per = (TextView) findViewById(R.id.text_21_per);
        text_20_per = (TextView) findViewById(R.id.text_20_per);

        text_19_per = (TextView) findViewById(R.id.text_19_per);
        text_18_per = (TextView) findViewById(R.id.text_18_per);
        text_17_per = (TextView) findViewById(R.id.text_17_per);
        text_16_per = (TextView) findViewById(R.id.text_16_per);
        text_15_per = (TextView) findViewById(R.id.text_15_per);
        text_14_per = (TextView) findViewById(R.id.text_14_per);
        text_13_per = (TextView) findViewById(R.id.text_13_per);
        text_12_per = (TextView) findViewById(R.id.text_12_per);
        text_11_per = (TextView) findViewById(R.id.text_11_per);
        text_10_per = (TextView) findViewById(R.id.text_10_per);

        text_09_per = (TextView) findViewById(R.id.text_09_per);
        text_08_per = (TextView) findViewById(R.id.text_08_per);
        text_07_per = (TextView) findViewById(R.id.text_07_per);
        text_06_per = (TextView) findViewById(R.id.text_06_per);
        text_05_per = (TextView) findViewById(R.id.text_05_per);
        text_04_per = (TextView) findViewById(R.id.text_04_per);
        text_03_per = (TextView) findViewById(R.id.text_03_per);
        text_02_per = (TextView) findViewById(R.id.text_02_per);
        text_01_per = (TextView) findViewById(R.id.text_01_per);
        text_00_per = (TextView) findViewById(R.id.text_00_per);

        maxPointsString = maxPointsText.getText().toString();

        if (maxPointsString.length() > 0) {
            calculate();
        } else {
            Snackbar.make(maxPointsText, R.string.grade_toast, Snackbar.LENGTH_LONG).show();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                maxPointsString = maxPointsText.getText().toString();

                if (maxPointsString.length() > 0) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(maxPointsText.getWindowToken(), 0);
                    calculate();
                } else {
                    Snackbar.make(maxPointsText, R.string.grade_toast, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void actualPoint(double percentText, TextView text, TextView textPercent, TextView textGrade) {

        double actPointsLong = (percentText/100) * maxPoints;
        String actPointsString = String.format(Locale.GERMANY, "%.1f", actPointsLong);
        text.setText(String.valueOf(String.valueOf(actPointsString)));

        int percent = Integer.parseInt(textPercent.getText().toString());

        if (percent >= 96) {
            textGrade.setText("1");
        } else if (percent >= 91) {
            textGrade.setText("1,2");
        } else if (percent >= 86) {
            textGrade.setText("1,5");
        } else if (percent >= 81) {
            textGrade.setText("1,7");
        } else if (percent >= 76) {
            textGrade.setText("2");
        } else if (percent >= 71) {
            textGrade.setText("2,2");
        } else if (percent >= 66) {
            textGrade.setText("2,5");
        } else if (percent >= 61) {
            textGrade.setText("2,7");
        } else if (percent >= 56) {
            textGrade.setText("3");
        } else if (percent >= 51) {
            textGrade.setText("3,2");
        } else if (percent >= 46) {
            textGrade.setText("3,5");
        } else if (percent >= 41) {
            textGrade.setText("3,7");
        } else if (percent >= 36) {
            textGrade.setText("4");
        } else if (percent >= 31) {
            textGrade.setText("4,2");
        } else if (percent >= 26) {
            textGrade.setText("4,5");
        } else if (percent >= 21) {
            textGrade.setText("4,7");
        } else if (percent >= 16) {
            textGrade.setText("5");
        } else if (percent >= 11) {
            textGrade.setText("5,2");
        } else if (percent >= 6) {
            textGrade.setText("5,5");
        } else if (percent >= 1) {
            textGrade.setText("5,7");
        } else if (percent >= 0) {
            textGrade.setText("6");
        }
    }

    private void calculate () {
        maxPoints = Double.parseDouble(maxPointsText.getText().toString());

        actualPoint(100, text_100_points, text_100_per, text_100_grade);
        actualPoint( 99,  text_99_points,  text_99_per,  text_99_grade);
        actualPoint( 98,  text_98_points,  text_98_per,  text_98_grade);
        actualPoint( 97,  text_97_points,  text_97_per,  text_97_grade);
        actualPoint( 96,  text_96_points,  text_96_per,  text_96_grade);
        actualPoint( 95,  text_95_points,  text_95_per,  text_95_grade);
        actualPoint( 94,  text_94_points,  text_94_per,  text_94_grade);
        actualPoint( 93,  text_93_points,  text_93_per,  text_93_grade);
        actualPoint( 92,  text_92_points,  text_92_per,  text_92_grade);
        actualPoint( 91,  text_91_points,  text_91_per,  text_91_grade);
        actualPoint( 90,  text_90_points,  text_90_per,  text_90_grade);

        actualPoint( 89,  text_89_points,  text_89_per,  text_89_grade);
        actualPoint( 88,  text_88_points,  text_88_per,  text_88_grade);
        actualPoint( 87,  text_87_points,  text_87_per,  text_87_grade);
        actualPoint( 86,  text_86_points,  text_86_per,  text_86_grade);
        actualPoint( 85,  text_85_points,  text_85_per,  text_85_grade);
        actualPoint( 84,  text_84_points,  text_84_per,  text_84_grade);
        actualPoint( 83,  text_83_points,  text_83_per,  text_83_grade);
        actualPoint( 82,  text_82_points,  text_82_per,  text_82_grade);
        actualPoint( 81,  text_81_points,  text_81_per,  text_81_grade);
        actualPoint( 80,  text_80_points,  text_80_per,  text_80_grade);

        actualPoint( 79,  text_79_points,  text_79_per,  text_79_grade);
        actualPoint( 78,  text_78_points,  text_78_per,  text_78_grade);
        actualPoint( 77,  text_77_points,  text_77_per,  text_77_grade);
        actualPoint( 76,  text_76_points,  text_76_per,  text_76_grade);
        actualPoint( 75,  text_75_points,  text_75_per,  text_75_grade);
        actualPoint( 74,  text_74_points,  text_74_per,  text_74_grade);
        actualPoint( 73,  text_73_points,  text_73_per,  text_73_grade);
        actualPoint( 72,  text_72_points,  text_72_per,  text_72_grade);
        actualPoint( 71,  text_71_points,  text_71_per,  text_71_grade);
        actualPoint( 70,  text_70_points,  text_70_per,  text_70_grade);

        actualPoint( 69,  text_69_points,  text_69_per,  text_69_grade);
        actualPoint( 68,  text_68_points,  text_68_per,  text_68_grade);
        actualPoint( 67,  text_67_points,  text_67_per,  text_67_grade);
        actualPoint( 66,  text_66_points,  text_66_per,  text_66_grade);
        actualPoint( 65,  text_65_points,  text_65_per,  text_65_grade);
        actualPoint( 64,  text_64_points,  text_64_per,  text_64_grade);
        actualPoint( 63,  text_63_points,  text_63_per,  text_63_grade);
        actualPoint( 62,  text_62_points,  text_62_per,  text_62_grade);
        actualPoint( 61,  text_61_points,  text_61_per,  text_61_grade);
        actualPoint( 60,  text_60_points,  text_60_per,  text_60_grade);

        actualPoint( 59,  text_59_points,  text_59_per,  text_59_grade);
        actualPoint( 58,  text_58_points,  text_58_per,  text_58_grade);
        actualPoint( 57,  text_57_points,  text_57_per,  text_57_grade);
        actualPoint( 56,  text_56_points,  text_56_per,  text_56_grade);
        actualPoint( 55,  text_55_points,  text_55_per,  text_55_grade);
        actualPoint( 54,  text_54_points,  text_54_per,  text_54_grade);
        actualPoint( 53,  text_53_points,  text_53_per,  text_53_grade);
        actualPoint( 52,  text_52_points,  text_52_per,  text_52_grade);
        actualPoint( 51,  text_51_points,  text_51_per,  text_51_grade);
        actualPoint( 50,  text_50_points,  text_50_per,  text_50_grade);

        actualPoint( 49,  text_49_points,  text_49_per,  text_49_grade);
        actualPoint( 48,  text_48_points,  text_48_per,  text_48_grade);
        actualPoint( 47,  text_47_points,  text_47_per,  text_47_grade);
        actualPoint( 46,  text_46_points,  text_46_per,  text_46_grade);
        actualPoint( 45,  text_45_points,  text_45_per,  text_45_grade);
        actualPoint( 44,  text_44_points,  text_44_per,  text_44_grade);
        actualPoint( 43,  text_43_points,  text_43_per,  text_43_grade);
        actualPoint( 42,  text_42_points,  text_42_per,  text_42_grade);
        actualPoint( 41,  text_41_points,  text_41_per,  text_41_grade);
        actualPoint( 40,  text_40_points,  text_40_per,  text_40_grade);

        actualPoint( 39,  text_39_points,  text_39_per,  text_39_grade);
        actualPoint( 38,  text_38_points,  text_38_per,  text_38_grade);
        actualPoint( 37,  text_37_points,  text_37_per,  text_37_grade);
        actualPoint( 36,  text_36_points,  text_36_per,  text_36_grade);
        actualPoint( 35,  text_35_points,  text_35_per,  text_35_grade);
        actualPoint( 34,  text_34_points,  text_34_per,  text_34_grade);
        actualPoint( 33,  text_33_points,  text_33_per,  text_33_grade);
        actualPoint( 32,  text_32_points,  text_32_per,  text_32_grade);
        actualPoint( 31,  text_31_points,  text_31_per,  text_31_grade);
        actualPoint( 30,  text_30_points,  text_30_per,  text_30_grade);

        actualPoint( 29,  text_29_points,  text_29_per,  text_29_grade);
        actualPoint( 28,  text_28_points,  text_28_per,  text_28_grade);
        actualPoint( 27,  text_27_points,  text_27_per,  text_27_grade);
        actualPoint( 26,  text_26_points,  text_26_per,  text_26_grade);
        actualPoint( 25,  text_25_points,  text_25_per,  text_25_grade);
        actualPoint( 24,  text_24_points,  text_24_per,  text_24_grade);
        actualPoint( 23,  text_23_points,  text_23_per,  text_23_grade);
        actualPoint( 22,  text_22_points,  text_22_per,  text_22_grade);
        actualPoint( 21,  text_21_points,  text_21_per,  text_21_grade);
        actualPoint( 20,  text_20_points,  text_20_per,  text_20_grade);

        actualPoint( 19,  text_19_points,  text_19_per,  text_19_grade);
        actualPoint( 18,  text_18_points,  text_18_per,  text_18_grade);
        actualPoint( 17,  text_17_points,  text_17_per,  text_17_grade);
        actualPoint( 16,  text_16_points,  text_16_per,  text_16_grade);
        actualPoint( 15,  text_15_points,  text_15_per,  text_15_grade);
        actualPoint( 14,  text_14_points,  text_14_per,  text_14_grade);
        actualPoint( 13,  text_13_points,  text_13_per,  text_13_grade);
        actualPoint( 12,  text_12_points,  text_12_per,  text_12_grade);
        actualPoint( 11,  text_11_points,  text_11_per,  text_11_grade);
        actualPoint( 10,  text_10_points,  text_10_per,  text_10_grade);

        actualPoint(  9,  text_09_points,  text_09_per,  text_09_grade);
        actualPoint(  8,  text_08_points,  text_08_per,  text_08_grade);
        actualPoint(  7,  text_07_points,  text_07_per,  text_07_grade);
        actualPoint(  6,  text_06_points,  text_06_per,  text_06_grade);
        actualPoint(  5,  text_05_points,  text_05_per,  text_05_grade);
        actualPoint(  4,  text_04_points,  text_04_per,  text_04_grade);
        actualPoint(  3,  text_03_points,  text_03_per,  text_03_grade);
        actualPoint(  2,  text_02_points,  text_02_per,  text_02_grade);
        actualPoint(  1,  text_01_points,  text_01_per,  text_01_grade);
        actualPoint(  0,  text_00_points,  text_00_per,  text_00_grade);
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(HHS_Grades.this);
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(HHS_Grades.this);
    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isClosed(HHS_Grades.this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (sharedPref.getBoolean ("help", false)){
            menu.getItem(2).setVisible(false); // here pass the index of save menu item
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_grade, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_folder) {
            String startDir = Environment.getExternalStorageDirectory() + "/HHS_Moodle/";
            helper_main.openFilePicker(HHS_Grades.this, maxPointsText, startDir);
        }

        if (id == android.R.id.home) {
            helper_main.resetStartTab(HHS_Grades.this);
            helper_main.isOpened(HHS_Grades.this);
            helper_main.switchToActivity(HHS_Grades.this, HHS_MainScreen.class, "", true);
        }

        if (id == R.id.action_help) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(HHS_Grades.this)
                    .setTitle(R.string.helpGrade_title)
                    .setMessage(helper_main.textSpannable(getString(R.string.helpGrade_text)))
                    .setPositiveButton(getString(R.string.toast_yes), null);
            dialog.show();
        }


        if (id == R.id.action_not) {

            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String dateCreate = format.format(date);

            sharedPref.edit()
                    .putString("handleTextTitle", "")
                    .putString("handleTextText", "")
                    .putString("handleTextIcon", "")
                    .putString("handleTextAttachment", "")
                    .putString("handleTextCreate", dateCreate)
                    .apply();
            helper_notes.editNote(HHS_Grades.this);
        }

        return super.onOptionsItemSelected(item);
    }
}