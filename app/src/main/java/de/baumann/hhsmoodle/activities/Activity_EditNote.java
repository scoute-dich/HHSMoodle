package de.baumann.hhsmoodle.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.mvc.imagepicker.ImagePicker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.data_notes.Notes_DbAdapter;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.popup.Popup_files;


public class Activity_EditNote extends AppCompatActivity {

    private Button attachment;
    private ImageButton attachmentRem;
    private ImageButton attachmentCam;
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

        String att = getString(R.string.note_attachment) + ": " + attName;

        if (attName.equals("")) {
            attachment.setText(R.string.choose_att);
            attachmentRem.setVisibility(View.GONE);
            attachmentCam.setVisibility(View.VISIBLE);
        } else {
            attachment.setText(att);
            attachmentRem.setVisibility(View.VISIBLE);
            attachmentCam.setVisibility(View.GONE);
        }
        File file2 = new File(file);
        if (!file2.exists()) {
            attachment.setText(R.string.choose_att);
            attachmentRem.setVisibility(View.GONE);
            attachmentCam.setVisibility(View.VISIBLE);
        }

        titleInput = (EditText) findViewById(R.id.note_title_input);
        textInput = (EditText) findViewById(R.id.note_text_input);

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
            }
        });

        attachmentCam.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onPickImage();
            }
        });

        final ImageButton be = (ImageButton) findViewById(R.id.imageButtonPri);
        helper_main.switchIcon(this, priority, "handleTextIcon", be);

        be.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final helper_main.Item[] items = {
                        new helper_main.Item(getString(R.string.text_tit_11), R.drawable.ic_school_grey600_48dp),
                        new helper_main.Item(getString(R.string.text_tit_1), R.drawable.ic_view_dashboard_grey600_48dp),
                        new helper_main.Item(getString(R.string.text_tit_2), R.drawable.ic_face_profile_grey600_48dp),
                        new helper_main.Item(getString(R.string.text_tit_8), R.drawable.ic_calendar_grey600_48dp),
                        new helper_main.Item(getString(R.string.text_tit_3), R.drawable.ic_chart_areaspline_grey600_48dp),
                        new helper_main.Item(getString(R.string.text_tit_4), R.drawable.ic_bell_grey600_48dp),
                        new helper_main.Item(getString(R.string.text_tit_5), R.drawable.ic_settings_grey600_48dp),
                        new helper_main.Item(getString(R.string.text_tit_6), R.drawable.ic_web_grey600_48dp),
                        new helper_main.Item(getString(R.string.text_tit_7), R.drawable.ic_magnify_grey600_48dp),
                        new helper_main.Item(getString(R.string.title_notes), R.drawable.ic_pencil_grey600_48dp),
                        new helper_main.Item(getString(R.string.text_tit_9), R.drawable.ic_check_grey600_48dp),
                        new helper_main.Item(getString(R.string.text_tit_10), R.drawable.ic_clock_grey600_48dp),
                        new helper_main.Item(getString(R.string.title_bookmarks), R.drawable.ic_bookmark_grey600_48dp),
                        new helper_main.Item(getString(R.string.subjects_color_red), R.drawable.circle_red),
                        new helper_main.Item(getString(R.string.subjects_color_pink), R.drawable.circle_pink),
                        new helper_main.Item(getString(R.string.subjects_color_purple), R.drawable.circle_purple),
                        new helper_main.Item(getString(R.string.subjects_color_blue), R.drawable.circle_blue),
                        new helper_main.Item(getString(R.string.subjects_color_teal), R.drawable.circle_teal),
                        new helper_main.Item(getString(R.string.subjects_color_green), R.drawable.circle_green),
                        new helper_main.Item(getString(R.string.subjects_color_lime), R.drawable.circle_lime),
                        new helper_main.Item(getString(R.string.subjects_color_yellow), R.drawable.circle_yellow),
                        new helper_main.Item(getString(R.string.subjects_color_orange), R.drawable.circle_orange),
                        new helper_main.Item(getString(R.string.subjects_color_brown), R.drawable.circle_brown),
                        new helper_main.Item(getString(R.string.subjects_color_grey), R.drawable.circle_grey),
                };

                ListAdapter adapter = new ArrayAdapter<helper_main.Item>(
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
                                helper_main.switchIconDialog(Activity_EditNote.this, item, "handleTextIcon", be);
                            }
                        }).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        InputStream inputStream = ImagePicker.getInputStreamFromResult(this, requestCode, resultCode, data);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);

        if (bitmap != null) {
            try {
                //create a file to write bitmap data
                File f = helper_main.newFile();
                //noinspection ResultOfMethodCallIgnored
                f.createNewFile();

                OutputStream outStream = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
                sharedPref.edit().putString("handleTextAttachment", f.getAbsolutePath()).apply();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onPickImage() {
        // Click on image button
        ImagePicker.pickImage(this, "Select your image:");
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.

        String filePath = sharedPref.getString("handleTextAttachment", "");
        final String attName = filePath.substring(filePath.lastIndexOf("/")+1);

        attachmentRem = (ImageButton) findViewById(R.id.button_rem);
        attachment = (Button) findViewById(R.id.button_att);
        attachmentCam = (ImageButton) findViewById(R.id.button_cam);

        String att = getString(R.string.note_attachment) + ": " + attName;

        if (attName.equals("")) {
            attachment.setText(R.string.choose_att);
            attachmentRem.setVisibility(View.GONE);
            attachmentCam.setVisibility(View.VISIBLE);
        } else {
            attachment.setText(att);
            attachmentRem.setVisibility(View.VISIBLE);
            attachmentCam.setVisibility(View.GONE);
        }
        File file = new File(filePath);
        if (!file.exists()) {
            attachment.setText(R.string.choose_att);
            attachmentRem.setVisibility(View.GONE);
            attachmentCam.setVisibility(View.VISIBLE);
        }
    }

    public void onBackPressed() {
        closeActivity();
    }

    private void closeActivity () {
        Notes_DbAdapter db = new Notes_DbAdapter(Activity_EditNote.this);
        db.open();

        String inputTitle = titleInput.getText().toString().trim();
        String inputContent = textInput.getText().toString().trim();
        String attachment = sharedPref.getString("handleTextAttachment", "");
        String create = sharedPref.getString("handleTextCreate", "");
        String seqno = sharedPref.getString("handleTextSeqno", "");

        if (seqno.isEmpty()) {
            try {
                if(db.isExist(helper_main.secString(inputTitle))){
                    Snackbar.make(titleInput, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                }else{
                    db.insert(helper_main.secString(inputTitle), helper_main.secString(inputContent), sharedPref.getString("handleTextIcon", ""), attachment, create);
                    closeActivity();
                }
            } catch (Exception e) {
                Log.w("HHS_Moodle", "Error Package name not found ", e);
                Snackbar snackbar = Snackbar.make(titleInput, R.string.toast_notSave, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        } else {
            try {
                db.update(Integer.parseInt(seqno), helper_main.secString(inputTitle), helper_main.secString(inputContent), sharedPref.getString("handleTextIcon", ""), attachment, create);
            } catch (Exception e) {
                Log.w("HHS_Moodle", "Error Package name not found ", e);
                Snackbar snackbar = Snackbar.make(titleInput, R.string.toast_notSave, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }

        sharedPref.edit()
                .putString("handleTextTitle", "")
                .putString("handleTextText", "")
                .putString("handleTextIcon", "19")
                .putString("handleTextAttachment", "")
                .putString("handleTextCreate", "")
                .putString("editTextFocus", "")
                .putString("handleTextSeqno", "")
                .apply();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.action_alert).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            closeActivity();
        }

        if (id == R.id.action_enterTime) {
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
                            Date date = new Date();
                            if (options[item].equals(getString(R.string.paste_date))) {
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                String dateNow = format.format(date);

                                if(sharedPref.getString("editTextFocus", "").equals("text")) {
                                    textInput.getText().insert(textInput.getSelectionStart(), dateNow);
                                } else {
                                    titleInput.getText().insert(titleInput.getSelectionStart(), dateNow);
                                }
                            }

                            if (options[item].equals (getString(R.string.paste_time))) {
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

        return super.onOptionsItemSelected(item);
    }
}
