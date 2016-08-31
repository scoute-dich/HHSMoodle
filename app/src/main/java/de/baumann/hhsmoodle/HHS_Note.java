package de.baumann.hhsmoodle;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import de.baumann.hhsmoodle.helper.Database_Notes;

public class HHS_Note extends AppCompatActivity {

    private EditText titleInput;
    private EditText textInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_activity_add_note);

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        setTitle(R.string.note_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(toolbar != null) {
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(HHS_Note.this);
                    sharedPref.edit()
                            .putString("handleTextTitle", "")
                            .putString("handleTextText", "")
                            .apply();

                    Intent intent_in = new Intent(HHS_Note.this, HHS_MainScreen.class);
                    startActivity(intent_in);
                }
            });

            if (sharedPref.getBoolean ("longPress", false)){
                toolbar.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(HHS_Note.this);
                        sharedPref.edit()
                                .putString("handleTextTitle", "")
                                .putString("handleTextText", "")
                                .apply();

                        finishAffinity();
                        return true;
                    }
                });
            }
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        titleInput = (EditText) findViewById(R.id.note_title_input);
        textInput = (EditText) findViewById(R.id.note_text_input);

        titleInput.setText(sharedPref.getString("handleTextTitle", ""));
        titleInput.setSelection(titleInput.getText().length());
        textInput.setText(sharedPref.getString("handleTextText", ""));
        textInput.setSelection(textInput.getText().length());

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action)) {

            if ("text/plain".equals(type)) {
                sharedPref.edit().putString("handleTextTitle", intent.getStringExtra(Intent.EXTRA_SUBJECT)).apply();
                sharedPref.edit().putString("handleTextText", intent.getStringExtra(Intent.EXTRA_TEXT)).apply();

                titleInput.setText(sharedPref.getString("handleTextTitle", ""));
                titleInput.setSelection(titleInput.getText().length());
                textInput.setText(sharedPref.getString("handleTextText", ""));
                textInput.setSelection(textInput.getText().length());

                sharedPref.edit()
                        .putString("handleTextTitle", "")
                        .putString("handleTextText", "")
                        .apply();
            }
        }
    }

    @Override
    public void onBackPressed() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.edit()
                .putString("handleTextTitle", "")
                .putString("handleTextText", "")
                .apply();

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPref.edit()
                    .putString("handleTextTitle", "")
                    .putString("handleTextText", "")
                    .apply();

            Intent intent_in = new Intent(HHS_Note.this, HHS_MainScreen.class);
            startActivity(intent_in);
        }

        if (id == R.id.save_note) {

            Snackbar.make(titleInput, R.string.note_saved, Snackbar.LENGTH_LONG).show();

            try {

                final Database_Notes db = new Database_Notes(HHS_Note.this);
                String inputTitle = titleInput.getText().toString().trim();
                String inputContent = textInput.getText().toString().trim();
                db.addBookmark(inputTitle, inputContent);
                db.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPref.edit()
                    .putString("handleTextTitle", "")
                    .putString("handleTextText", "")
                    .apply();

            finish();
        }

        if (id == R.id.action_help) {
            final SpannableString s = new SpannableString(Html.fromHtml(getString(R.string.helpNotes_text)));
            Linkify.addLinks(s, Linkify.WEB_URLS);

            final AlertDialog.Builder dialog = new AlertDialog.Builder(HHS_Note.this)
                    .setTitle(getString(R.string.note_edit))
                    .setMessage(s)
                    .setPositiveButton(getString(R.string.toast_yes), null);
            dialog.show();
        }
        return true;
    }
}
