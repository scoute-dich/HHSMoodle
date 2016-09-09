package de.baumann.hhsmoodle;

import android.app.AlertDialog;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
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

        titleInput = (EditText) findViewById(R.id.note_title_input);
        textInput = (EditText) findViewById(R.id.note_text_input);

        titleInput.setText(sharedPref.getString("handleTextTitle", ""));
        titleInput.setSelection(titleInput.getText().length());
        textInput.setText(sharedPref.getString("handleTextText", ""));
        textInput.setSelection(textInput.getText().length());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(toolbar != null) {
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String title = titleInput.getText().toString();
                    String text = textInput.getText().toString();

                    if (title.isEmpty() && text.isEmpty() ) {
                        clearSharedPreferences();
                        Intent intent_in = new Intent(HHS_Note.this, HHS_MainScreen.class);
                        startActivity(intent_in);
                        finish();

                    } else {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(textInput.getWindowToken(),
                                InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        Snackbar snackbar = Snackbar
                                .make(textInput, getString(R.string.toast_save), Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.toast_no), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        clearSharedPreferences();
                                        Intent intent_in = new Intent(HHS_Note.this, HHS_MainScreen.class);
                                        startActivity(intent_in);
                                        finish();
                                    }
                                });
                        snackbar.show();
                    }
                }
            });

            if (sharedPref.getBoolean ("longPress", false)){
                toolbar.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        String title = titleInput.getText().toString();
                        String text = textInput.getText().toString();

                        if (title.isEmpty() && text.isEmpty() ) {
                            clearSharedPreferences();
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(HHS_Note.this);
                            sharedPref.edit()
                                    .putBoolean("isOpened", true)
                                    .apply();
                            finishAffinity();

                        } else {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(textInput.getWindowToken(),
                                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
                            Snackbar snackbar = Snackbar
                                    .make(textInput, getString(R.string.toast_save), Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.toast_no), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            clearSharedPreferences();
                                            finishAffinity();
                                        }
                                    });
                            snackbar.show();
                        }
                        return true;
                    }
                });
            }
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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
                clearSharedPreferences();
            }
        }
    }

    @Override
    public void onBackPressed() {

        String title = titleInput.getText().toString();
        String text = textInput.getText().toString();

        if (title.isEmpty() && text.isEmpty() ) {
            clearSharedPreferences();
            finish();

        } else {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(textInput.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
            Snackbar snackbar = Snackbar
                    .make(textInput, getString(R.string.toast_save), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.toast_no), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clearSharedPreferences();
                            finish();
                        }
                    });
            snackbar.show();
        }
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

            String title = titleInput.getText().toString();
            String text = textInput.getText().toString();

            if (title.isEmpty() && text.isEmpty() ) {
                clearSharedPreferences();
                Intent intent_in = new Intent(HHS_Note.this, HHS_MainScreen.class);
                startActivity(intent_in);
                finish();

            } else {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textInput.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
                Snackbar snackbar = Snackbar
                        .make(textInput, getString(R.string.toast_save), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.toast_no), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                clearSharedPreferences();
                                Intent intent_in = new Intent(HHS_Note.this, HHS_MainScreen.class);
                                startActivity(intent_in);
                                finish();
                            }
                        });
                snackbar.show();
            }
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
            clearSharedPreferences();
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

    private void clearSharedPreferences() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.edit()
                .putString("handleTextTitle", "")
                .putString("handleTextText", "")
                .apply();
    }
}