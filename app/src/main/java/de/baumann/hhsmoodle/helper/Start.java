package de.baumann.hhsmoodle.helper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import de.baumann.hhsmoodle.Browser;

public class Start extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String startURL = sharedPref.getString("favoriteURL", "http://m.wetterdienst.de/");

        Intent intent = new Intent(Start.this, Browser.class);
        intent.putExtra("url", startURL);
        startActivityForResult(intent, 100);
        finish();
    }
}