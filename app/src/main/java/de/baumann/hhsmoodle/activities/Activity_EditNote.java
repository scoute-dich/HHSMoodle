package de.baumann.hhsmoodle.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.data_notes.Notes_DbAdapter;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.popup.Popup_camera;
import de.baumann.hhsmoodle.popup.Popup_files;


public class Activity_EditNote extends AppCompatActivity {

    private Button attachment;
    private ImageButton attachmentRem;
    private ImageButton attachmentCam;
    private ImageButton attachmentPic;
    private EditText titleInput;
    private EditText textInput;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.note_edit);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        PreferenceManager.setDefaultValues(Activity_EditNote.this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(Activity_EditNote.this);

        final String priority = sharedPref.getString("handleTextIcon", "");

        String file = sharedPref.getString("handleTextAttachment", "");
        final String attName = file.substring(file.lastIndexOf("/")+1);

        attachmentRem = (ImageButton) findViewById(R.id.button_rem);
        attachment = (Button) findViewById(R.id.button_att);
        attachmentCam = (ImageButton) findViewById(R.id.button_cam);
        attachmentPic = (ImageButton) findViewById(R.id.button_pic);

        String att = getString(R.string.note_attachment) + ": " + attName;

        if (attName.equals("")) {
            attachment.setText(R.string.choose_att);
            attachmentRem.setVisibility(View.GONE);
            attachmentCam.setVisibility(View.VISIBLE);
            attachmentPic.setVisibility(View.VISIBLE);
        } else {
            attachment.setText(att);
            attachmentRem.setVisibility(View.VISIBLE);
            attachmentCam.setVisibility(View.GONE);
            attachmentPic.setVisibility(View.GONE);
        }
        File file2 = new File(file);
        if (!file2.exists()) {
            attachment.setText(R.string.choose_att);
            attachmentRem.setVisibility(View.GONE);
            attachmentCam.setVisibility(View.VISIBLE);
            attachmentPic.setVisibility(View.VISIBLE);
        }

        titleInput = (EditText) findViewById(R.id.note_title_input);
        textInput = (EditText) findViewById(R.id.note_text_input);
        helper_main.showKeyboard(Activity_EditNote.this, titleInput);

        titleInput.setText(sharedPref.getString("handleTextTitle", ""));
        titleInput.setSelection(titleInput.getText().length());
        textInput.setText(sharedPref.getString("handleTextText", ""));
        textInput.setSelection(textInput.getText().length());

        titleInput.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                sharedPref.edit().putString("editTextFocus", "title").apply();
                return false;
            }
        });

        textInput.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View arg0, MotionEvent arg1) {
                sharedPref.edit().putString("editTextFocus", "text").apply();
                return false;
            }
        });

        attachment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent mainIntent = new Intent(Activity_EditNote.this, Popup_files.class);
                mainIntent.setAction("file_chooseAttachment");
                startActivity(mainIntent);
            }
        });

        attachmentRem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sharedPref.edit().putString("handleTextAttachment", "").apply();
                attachment.setText(R.string.choose_att);
                attachmentRem.setVisibility(View.GONE);
                attachmentCam.setVisibility(View.VISIBLE);
                attachmentPic.setVisibility(View.VISIBLE);
            }
        });

        attachmentPic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                selectImage_1();
            }
        });

        attachmentCam.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                File f = helper_main.newFile();
                final String fileName = f.getAbsolutePath();
                sharedPref.edit().putString("handleTextAttachment", fileName).apply();

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(titleInput.getWindowToken(), 0);
                Intent intent = new Intent(Activity_EditNote.this, Popup_camera.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        final ImageButton be = (ImageButton) findViewById(R.id.imageButtonPri);
        ImageButton ib_paste = (ImageButton) findViewById(R.id.imageButtonPaste);
        assert be != null;

        switch (priority) {
            case "3":
                be.setImageResource(R.drawable.circle_green);
                sharedPref.edit()
                        .putString("handleTextIcon", "3")
                        .apply();
                break;
            case "2":
                be.setImageResource(R.drawable.circle_yellow);
                sharedPref.edit()
                        .putString("handleTextIcon", "2")
                        .apply();
                break;
            case "1":
                be.setImageResource(R.drawable.circle_red);
                sharedPref.edit()
                        .putString("handleTextIcon", "1")
                        .apply();
                break;

            default:
                be.setImageResource(R.drawable.circle_green);
                sharedPref.edit()
                        .putString("handleTextIcon", "3")
                        .apply();
                break;
        }

        be.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final Item[] items = {
                        new Item(getString(R.string.note_priority_0), R.drawable.circle_green),
                        new Item(getString(R.string.note_priority_1), R.drawable.circle_yellow),
                        new Item(getString(R.string.note_priority_2), R.drawable.circle_red),
                };

                ListAdapter adapter = new ArrayAdapter<Item>(
                        Activity_EditNote.this,
                        android.R.layout.select_dialog_item,
                        android.R.id.text1,
                        items){
                    @NonNull
                    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                        //Use super class to create the View
                        View v = super.getView(position, convertView, parent);
                        TextView tv = (TextView)v.findViewById(android.R.id.text1);
                        tv.setTextSize(18);
                        tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);
                        //Add margin between image and text (support various screen densities)
                        int dp5 = (int) (24 * getResources().getDisplayMetrics().density + 0.5f);
                        tv.setCompoundDrawablePadding(dp5);

                        return v;
                    }
                };

                new android.app.AlertDialog.Builder(Activity_EditNote.this)
                        .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (item == 0) {
                                    be.setImageResource(R.drawable.circle_green);
                                    sharedPref.edit()
                                            .putString("handleTextIcon", "3")
                                            .apply();
                                } else if (item == 1) {
                                    be.setImageResource(R.drawable.circle_yellow);
                                    sharedPref.edit()
                                            .putString("handleTextIcon", "2")
                                            .apply();
                                } else if (item == 2) {
                                    be.setImageResource(R.drawable.circle_red);
                                    sharedPref.edit()
                                            .putString("handleTextIcon", "1")
                                            .apply();
                                }
                            }
                        }).show();
            }
        });

        ib_paste.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final CharSequence[] options = {
                        getString(R.string.paste_date),
                        getString(R.string.paste_time)};
                new android.app.AlertDialog.Builder(Activity_EditNote.this)
                        .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        })
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals(getString(R.string.paste_date))) {
                                    String dateFormat = sharedPref.getString("dateFormat", "1");

                                    switch (dateFormat) {
                                        case "1":

                                            Date date = new Date();
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                            String dateNow = format.format(date);

                                            if(sharedPref.getString("editTextFocus", "").equals("text")) {
                                                textInput.getText().insert(textInput.getSelectionStart(), dateNow);
                                            } else {
                                                titleInput.getText().insert(titleInput.getSelectionStart(), dateNow);
                                            }
                                            break;

                                        case "2":

                                            Date date2 = new Date();
                                            SimpleDateFormat format2 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                                            String dateNow2 = format2.format(date2);

                                            if(sharedPref.getString("editTextFocus", "").equals("text")) {
                                                textInput.getText().insert(textInput.getSelectionStart(), dateNow2);
                                            } else {
                                                titleInput.getText().insert(titleInput.getSelectionStart(), dateNow2);
                                            }
                                            break;
                                    }
                                }

                                if (options[item].equals (getString(R.string.paste_time))) {
                                    Date date = new Date();
                                    SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                    String timeNow = format.format(date);
                                    if(sharedPref.getString("editTextFocus", "").equals("text")) {
                                        textInput.getText().insert(textInput.getSelectionStart(), timeNow);
                                    } else {
                                        titleInput.getText().insert(titleInput.getSelectionStart(), timeNow);
                                    }
                                }
                            }
                        }).show();
            }
        });
    }

    private static class Item{
        public final String text;
        public final int icon;
        Item(String text, Integer icon) {
            this.text = text;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Activity_EditNote.this);
    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isClosed(Activity_EditNote.this);
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.

        helper_main.isOpened(Activity_EditNote.this);

        String file = sharedPref.getString("handleTextAttachment", "");
        final String attName = file.substring(file.lastIndexOf("/")+1);

        attachmentRem = (ImageButton) findViewById(R.id.button_rem);
        attachmentRem.setImageResource(R.drawable.close_red);
        attachment = (Button) findViewById(R.id.button_att);
        attachmentCam = (ImageButton) findViewById(R.id.button_cam);
        attachmentCam.setImageResource(R.drawable.camera);

        String att = getString(R.string.note_attachment) + ": " + attName;

        if (attName.equals("")) {
            attachment.setText(R.string.choose_att);
            attachmentRem.setVisibility(View.GONE);
            attachmentCam.setVisibility(View.VISIBLE);
            attachmentPic.setVisibility(View.VISIBLE);
        } else {
            attachment.setText(att);
            attachmentRem.setVisibility(View.VISIBLE);
            attachmentCam.setVisibility(View.GONE);
            attachmentPic.setVisibility(View.GONE);
        }
        File file2 = new File(file);
        if (!file2.exists()) {
            attachment.setText(R.string.choose_att);
            attachmentRem.setVisibility(View.GONE);
            attachmentCam.setVisibility(View.VISIBLE);
        }
    }

    public void onBackPressed() {
        Snackbar snackbar = Snackbar
                .make(titleInput, R.string.toast_save, Snackbar.LENGTH_LONG)
                .setAction(R.string.toast_yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sharedPref.edit()
                                .putString("handleTextTitle", "")
                                .putString("handleTextText", "")
                                .putString("handleTextIcon", "")
                                .putString("handleTextAttachment", "")
                                .putString("handleTextCreate", "")
                                .putString("editTextFocus", "")
                                .putString("handleTextSeqno", "")
                                .apply();
                        helper_main.isClosed(Activity_EditNote.this);
                        finish();
                    }
                });
        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Snackbar snackbar = Snackbar
                    .make(titleInput, R.string.toast_save, Snackbar.LENGTH_LONG)
                    .setAction(R.string.toast_yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sharedPref.edit()
                                    .putString("handleTextTitle", "")
                                    .putString("handleTextText", "")
                                    .putString("handleTextIcon", "")
                                    .putString("handleTextAttachment", "")
                                    .putString("handleTextCreate", "")
                                    .putString("editTextFocus", "")
                                    .putString("handleTextSeqno", "")
                                    .apply();
                            finish();
                        }
                    });
            snackbar.show();
        }

        if (id == R.id.action_save) {

            Notes_DbAdapter db = new Notes_DbAdapter(Activity_EditNote.this);
            db.open();

            String inputTitle = titleInput.getText().toString().trim();
            String inputContent = textInput.getText().toString().trim();
            String attachment = sharedPref.getString("handleTextAttachment", "");
            String create = sharedPref.getString("handleTextCreate", "");
            String seqno = sharedPref.getString("handleTextSeqno", "");

            if (seqno.isEmpty()) {
                try {
                    if(db.isExist(inputTitle)){
                        Snackbar.make(titleInput, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                    }else{
                        db.insert(inputTitle, inputContent, sharedPref.getString("handleTextIcon", ""), attachment, create);
                        sharedPref.edit()
                                .putString("handleTextTitle", "")
                                .putString("handleTextText", "")
                                .putString("handleTextIcon", "")
                                .putString("handleTextAttachment", "")
                                .putString("handleTextCreate", "")
                                .putString("editTextFocus", "")
                                .putString("handleTextSeqno", "")
                                .apply();
                        finish();
                    }
                } catch (Exception e) {
                    Log.w("HHS_Moodle", "Error Package name not found ", e);
                    Snackbar snackbar = Snackbar
                            .make(titleInput, R.string.toast_notSave, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            } else {
                try {
                    db.update(Integer.parseInt(seqno), inputTitle, inputContent, sharedPref.getString("handleTextIcon", ""), attachment, create);
                    sharedPref.edit()
                            .putString("handleTextTitle", "")
                            .putString("handleTextText", "")
                            .putString("handleTextIcon", "")
                            .putString("handleTextAttachment", "")
                            .putString("handleTextCreate", "")
                            .putString("editTextFocus", "")
                            .putString("handleTextSeqno", "")
                            .apply();
                    finish();
                } catch (Exception e) {
                    Log.w("HHS_Moodle", "Error Package name not found ", e);
                    Snackbar snackbar = Snackbar
                            .make(titleInput, R.string.toast_notSave, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void selectImage_1() {

        final CharSequence[] options = {
                getString(R.string.note_pic1),
                getString(R.string.note_pic2)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(getString(R.string.note_pic1))) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals(getString(R.string.note_pic2))) {
                    Intent intent = new Intent(Activity_EditNote.this, Activity_images.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            }
        });
        builder.setPositiveButton(getString(R.string.toast_cancel), null);
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String path = getRealPathFromURI(selectedImage);

                if (!path.isEmpty()){
                    sharedPref.edit().putString("handleTextAttachment", path).apply();
                } else {
                    Snackbar.make(titleInput, getString(R.string.toast_errors), Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
