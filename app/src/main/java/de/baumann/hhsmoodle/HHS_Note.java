package de.baumann.hhsmoodle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import de.baumann.hhsmoodle.helper.Database_Notes;
import de.baumann.hhsmoodle.helper.PasswordActivity;

public class HHS_Note extends AppCompatActivity {

    private EditText titleInput;
    private EditText textInput;
    private String inputPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        setTitle(R.string.note_edit);

        if (sharedPref.getString("protect_PW", "").length() > 0) {
            if (sharedPref.getBoolean("isOpened", true)) {
                Intent intent_in = new Intent(HHS_Note.this, PasswordActivity.class);
                startActivity(intent_in);
            }
        }

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
                    final String startURL = sharedPref.getString("favoriteURL", "https://moodle.huebsch.ka.schule-bw.de/moodle/");
                    final String startType = sharedPref.getString("startType", "1");

                    if (title.isEmpty() && text.isEmpty() ) {
                        clearSharedPreferences();
                        if (startType.equals("2")) {
                            Intent mainIntent = new Intent(HHS_Note.this, HHS_Browser.class);
                            mainIntent.putExtra("id", "1");
                            mainIntent.putExtra("url", startURL);
                            startActivity(mainIntent);
                            finish();
                        } else if (startType.equals("1")){
                            Intent mainIntent = new Intent(HHS_Note.this, HHS_MainScreen.class);
                            mainIntent.putExtra("id", "1");
                            startActivity(mainIntent);
                            finish();
                        }

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
                                        if (startType.equals("2")) {
                                            Intent mainIntent = new Intent(HHS_Note.this, HHS_Browser.class);
                                            mainIntent.putExtra("id", "1");
                                            mainIntent.putExtra("url", startURL);
                                            startActivity(mainIntent);
                                            finish();
                                        } else if (startType.equals("1")){
                                            Intent mainIntent = new Intent(HHS_Note.this, HHS_MainScreen.class);
                                            mainIntent.putExtra("id", "1");
                                            startActivity(mainIntent);
                                            finish();
                                        }
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
                                            sharedPref.edit()
                                                    .putBoolean("isOpened", true)
                                                    .apply();
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

        String priority = sharedPref.getString("handleTextIcon", "");

        final ImageButton b = (ImageButton) findViewById(R.id.imageButtonPri);
        assert b != null;

        switch (priority) {
            case "":
                b.setImageResource(R.drawable.pr_green);
                inputPriority = "";
                break;
            case "!":
                b.setImageResource(R.drawable.pr_yellow);
                inputPriority = "!";
                break;
            case "!!":
                b.setImageResource(R.drawable.pr_red);
                inputPriority = "!!";
                break;
        }

        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final Item[] items = {
                        new Item(getString(R.string.note_priority_0), R.drawable.pr_green_1),
                        new Item(getString(R.string.note_priority_1), R.drawable.pr_yellow_1),
                        new Item(getString(R.string.note_priority_2), R.drawable.pr_red_1),
                };

                ListAdapter adapter = new ArrayAdapter<Item>(
                        HHS_Note.this,
                        android.R.layout.select_dialog_item,
                        android.R.id.text1,
                        items){
                    public View getView(int position, View convertView, ViewGroup parent) {
                        //Use super class to create the View
                        View v = super.getView(position, convertView, parent);
                        TextView tv = (TextView)v.findViewById(android.R.id.text1);
                        tv.setTextSize(18);
                        tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);
                        //Add margin between image and text (support various screen densities)
                        int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                        tv.setCompoundDrawablePadding(dp5);

                        return v;
                    }
                };

                new AlertDialog.Builder(HHS_Note.this)
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (item == 0) {
                                    b.setImageResource(R.drawable.pr_green);
                                    inputPriority = "";
                                } else if (item == 1) {
                                    b.setImageResource(R.drawable.pr_yellow);
                                    inputPriority = "!";
                                } else if (item == 2) {
                                    b.setImageResource(R.drawable.pr_red);
                                    inputPriority = "!!";
                                }
                            }
                        }).show();
            }
        });

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action)) {

            if ("text/plain".equals(type)) {

                if (sharedPref.getString("protect_PW", "").length() > 0) {
                    if (sharedPref.getBoolean("isOpened", true)) {
                        Intent intent_in = new Intent(HHS_Note.this, PasswordActivity.class);
                        startActivity(intent_in);
                    }
                }

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

    public static class Item{
        public final String text;
        public final int icon;
        public Item(String text, Integer icon) {
            this.text = text;
            this.icon = icon;
        }
        @Override
        public String toString() {
            return text;
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
    public boolean onPrepareOptionsMenu(Menu menu) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean ("help", false)){
            menu.getItem(1).setVisible(false); // here pass the index of save menu item
        }
        return super.onPrepareOptionsMenu(menu);
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

                db.addBookmark(inputTitle, inputContent, inputPriority);
                db.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            clearSharedPreferences();
            finish();
        }

        if (id == R.id.action_help) {

            SpannableString s;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                s = new SpannableString(Html.fromHtml(getString(R.string.helpAddNotes_text),Html.FROM_HTML_MODE_LEGACY));
            } else {
                //noinspection deprecation
                s = new SpannableString(Html.fromHtml(getString(R.string.helpAddNotes_text)));
            }

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
                .putString("handleTextIcon", "")
                .apply();
    }
}