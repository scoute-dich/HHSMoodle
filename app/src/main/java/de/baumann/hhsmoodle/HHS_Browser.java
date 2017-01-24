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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.EditText;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.baumann.hhsmoodle.activities.Activity_courseList;
import de.baumann.hhsmoodle.activities.Activity_dice;
import de.baumann.hhsmoodle.activities.Activity_grades;
import de.baumann.hhsmoodle.databases.Database_Browser;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.helper.helper_notes;
import de.baumann.hhsmoodle.helper.helper_webView;
import de.baumann.hhsmoodle.popup.Popup_bookmarks;

import static de.baumann.hhsmoodle.helper.helper_main.newFileDest;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class HHS_Browser extends AppCompatActivity {

    private WebView mWebView;
    private ProgressBar progressBar;
    private Bitmap bitmap;

    private ImageButton imageButton_left;
    private ImageButton imageButton_right;

    private final static int RESULT_CODE_ICE_CREAM = 2;

    private ValueCallback<Uri> mUploadMessage;

    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private View mCustomView;
    private myWebChromeClient mWebChromeClient;
    private SharedPreferences sharedPref;
    private File shareFile;
    private ValueCallback<Uri[]> mFilePathCallback;

    private String shareString;
    private String mCameraPhotoPath;
    private final String TAG = HHS_Browser.class.getSimpleName();

    private static final int REQUEST_CODE_LOLLIPOP = 1;

    private void hideCustomView() {
        mWebChromeClient.onHideCustomView();
    }
    private boolean inCustomView() {
        return (mCustomView != null);
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.edit().putString("browserStarted", "true").apply();

        setContentView(R.layout.activity_browser);

        customViewContainer = (FrameLayout) findViewById(R.id.customViewContainer);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        helper_main.onStart(HHS_Browser.this);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SwipeRefreshLayout swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);
        assert swipeView != null;
        swipeView.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });

        mWebView = (WebView) findViewById(R.id.webView);
        mWebChromeClient = new myWebChromeClient();
        mWebView.setWebChromeClient(mWebChromeClient);

        imageButton_left = (ImageButton) findViewById(R.id.imageButton_left);
        imageButton_right = (ImageButton) findViewById(R.id.imageButton_right);

        if (sharedPref.getBoolean ("arrow", false)){
            imageButton_left.setVisibility(View.VISIBLE);
            imageButton_right.setVisibility(View.VISIBLE);
        } else {
            imageButton_left.setVisibility(View.INVISIBLE);
            imageButton_right.setVisibility(View.INVISIBLE);
        }

        helper_webView.webView_Settings(HHS_Browser.this, mWebView);
        helper_webView.webView_WebViewClient(HHS_Browser.this, swipeView, mWebView);

        mWebView.setDownloadListener(new DownloadListener() {

            public void onDownloadStart(final String url, String userAgent,
                                        final String contentDisposition, final String mimetype,
                                        long contentLength) {

                final String filename= URLUtil.guessFileName(url, contentDisposition, mimetype);
                Snackbar snackbar = Snackbar
                        .make(mWebView, getString(R.string.toast_download_1) + " " + filename, Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.toast_yes), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                                request.addRequestHeader("Cookie", CookieManager.getInstance().getCookie(url));
                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                                request.setDestinationInExternalPublicDir(newFileDest(), filename);
                                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                dm.enqueue(request);

                                Snackbar.make(mWebView, getString(R.string.toast_download) + " " + filename , Snackbar.LENGTH_LONG).show();
                                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                            }
                        });
                snackbar.show();
            }
        });

        onNewIntent(getIntent());

        helper_main.grantPermissions(HHS_Browser.this);

        File directory = new File(Environment.getExternalStorageDirectory() + "/HHS_Moodle/backup/");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    protected void onNewIntent(Intent intent) {
        mWebView.loadUrl(intent.getStringExtra("url"));
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("dd-MM-yy_HH-mm", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_CODE_ICE_CREAM:
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                }
                mUploadMessage.onReceiveValue(uri);
                mUploadMessage = null;
                break;
            case REQUEST_CODE_LOLLIPOP:
                Uri[] results = null;
                // Check that the response is a good one
                if (resultCode == Activity.RESULT_OK) {
                    if (data == null) {
                        // If there is not data, then we may have taken a photo
                        if (mCameraPhotoPath != null) {
                            results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                        }
                    } else {
                        String dataString = data.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }

                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;
                break;
        }
    }

    private final BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {

            Snackbar snackbar = Snackbar
                    .make(mWebView, getString(R.string.toast_download_2), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.toast_yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            helper_main.openFilePicker(HHS_Browser.this,mWebView, Environment.getExternalStorageDirectory() + newFileDest());
                        }
                    });
            snackbar.show();
            unregisterReceiver(onComplete);
        }
    };

    private final BroadcastReceiver onComplete2 = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {

            File destinationFile = new File(Environment.getExternalStorageDirectory() + "/HHS_Moodle/" +
                    shareString);

            Uri myUri= Uri.fromFile(destinationFile);
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("image/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, myUri);
            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            HHS_Browser.this.startActivity(Intent.createChooser(sharingIntent, (getString(R.string.app_share_image))));
            unregisterReceiver(onComplete2);
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        final WebView.HitTestResult result = mWebView.getHitTestResult();
        final String url = result.getExtra();

        if(result.getType() == WebView.HitTestResult.IMAGE_TYPE){

            final CharSequence[] options = {
                    getString(R.string.context_saveImage),
                    getString(R.string.context_shareImage),
                    getString(R.string.context_externalBrowser)};
            new AlertDialog.Builder(HHS_Browser.this)
                    .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    })
                    .setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals(getString(R.string.context_saveImage))) {
                                if(url != null) {
                                    try {
                                        Uri source = Uri.parse(url);
                                        DownloadManager.Request request = new DownloadManager.Request(source);
                                        request.addRequestHeader("Cookie", CookieManager.getInstance().getCookie(url));
                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                                        request.setDestinationInExternalPublicDir(newFileDest(), helper_main.newFileName());
                                        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                        dm.enqueue(request);
                                        Snackbar.make(mWebView, getString(R.string.context_saveImage_toast) + " " + helper_main.newFileName(), Snackbar.LENGTH_SHORT).show();
                                        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Snackbar.make(mWebView, R.string.toast_perm , Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            if (options[item].equals(getString(R.string.context_shareImage))) {
                                if(url != null) {

                                    shareString = helper_main.newFileName();
                                    shareFile = helper_main.newFile();

                                    try {
                                        Uri source = Uri.parse(url);
                                        DownloadManager.Request request = new DownloadManager.Request(source);
                                        request.addRequestHeader("Cookie", CookieManager.getInstance().getCookie(url));
                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                                        request.setDestinationInExternalPublicDir(newFileDest(), helper_main.newFileName());
                                        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                        dm.enqueue(request);

                                        Snackbar.make(mWebView, getString(R.string.context_saveImage_toast) + " " + helper_main.newFileName(), Snackbar.LENGTH_SHORT).show();
                                        registerReceiver(onComplete2, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Snackbar.make(mWebView, R.string.toast_perm , Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            if (options[item].equals(getString(R.string.context_externalBrowser))) {
                                if (url != null) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    HHS_Browser.this.startActivity(intent);
                                }
                            }
                        }
                    }).show();

        } else if (result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {

            final CharSequence[] options = {
                    getString(R.string.menu_share_link_copy),
                    getString(R.string.menu_share_link),
                    getString(R.string.context_externalBrowser)};
            new AlertDialog.Builder(HHS_Browser.this)
                    .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    })
                    .setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals(getString(R.string.menu_share_link_copy))) {
                                if (url != null) {
                                    ClipboardManager clipboard = (ClipboardManager) HHS_Browser.this.getSystemService(Context.CLIPBOARD_SERVICE);
                                    clipboard.setPrimaryClip(ClipData.newPlainText("text", url));
                                    Snackbar.make(mWebView, R.string.context_linkCopy_toast, Snackbar.LENGTH_SHORT).show();
                                }
                            }
                            if (options[item].equals(getString(R.string.menu_share_link))) {
                                if (url != null) {
                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, url);
                                    sendIntent.setType("text/plain");
                                    HHS_Browser.this.startActivity(Intent.createChooser(sendIntent, getResources()
                                            .getString(R.string.app_share_link)));
                                }
                            }
                            if (options[item].equals(getString(R.string.context_externalBrowser))) {
                                if (url != null) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    HHS_Browser.this.startActivity(intent);
                                }
                            }
                        }
                    }).show();
        }
    }

    @Override
    public void onBackPressed() {

        if (inCustomView()) {
            hideCustomView();
        } else if ((mCustomView == null) && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            mWebView.stopLoading();
            helper_main.isClosed(HHS_Browser.this);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(HHS_Browser.this);
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(HHS_Browser.this);
    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        if (inCustomView()) {
            hideCustomView();
        }
        helper_main.isClosed(HHS_Browser.this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (sharedPref.getBoolean ("help", false)){
            menu.getItem(6).setVisible(false); // here pass the index of save menu item
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_saveBookmark) {
            try {

                final Database_Browser db = new Database_Browser(HHS_Browser.this);

                AlertDialog.Builder builder = new AlertDialog.Builder(HHS_Browser.this);
                View dialogView = View.inflate(HHS_Browser.this, R.layout.dialog_edit_title, null);

                final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                edit_title.setHint(R.string.bookmark_edit_title);
                edit_title.setText(mWebView.getTitle());

                builder.setView(dialogView);
                builder.setTitle(R.string.bookmark_edit_title);
                builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        String inputTag = edit_title.getText().toString().trim();
                        db.addBookmark(inputTag, mWebView.getUrl(), "4");
                        db.close();
                        Snackbar.make(mWebView, R.string.bookmark_added, Snackbar.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                final AlertDialog dialog2 = builder.create();
                // Display the custom alert dialog on interface
                dialog2.show();

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        helper_main.showKeyboard(HHS_Browser.this,edit_title);
                    }
                }, 200);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (id == R.id.action_folder) {
            String startDir = Environment.getExternalStorageDirectory() + "/HHS_Moodle/";
            helper_main.openFilePicker(HHS_Browser.this, mWebView, startDir);
        }

        if (id == R.id.action_tools) {
            final CharSequence[] options = {
                    getString(R.string.action_grades),
                    getString(R.string.number_title),
                    getString(R.string.courseList_title)};

            new AlertDialog.Builder(HHS_Browser.this)
                    .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    })
                    .setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {

                            if (options[item].equals (getString(R.string.action_grades))) {
                                helper_main.isOpened(HHS_Browser.this);
                                helper_main.switchToActivity(HHS_Browser.this, Activity_grades.class, "", false);
                            }

                            if (options[item].equals (getString(R.string.number_title))) {
                                helper_main.isOpened(HHS_Browser.this);
                                helper_main.switchToActivity(HHS_Browser.this, Activity_dice.class, "", false);
                            }

                            if (options[item].equals (getString(R.string.courseList_title))) {
                                helper_main.isOpened(HHS_Browser.this);
                                helper_main.switchToActivity(HHS_Browser.this, Activity_courseList.class, "", false);
                            }

                        }
                    }).show();
        }

        if (id == android.R.id.home) {
            helper_main.isOpened(HHS_Browser.this);
            helper_main.switchToActivity(HHS_Browser.this, HHS_MainScreen.class, "", true);
        }

        if (id == R.id.action_help) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(HHS_Browser.this)
                    .setTitle(R.string.helpBrowser_title)
                    .setMessage(helper_main.textSpannable(getString(R.string.helpBrowser_text)))
                    .setPositiveButton(getString(R.string.toast_yes), null);
            dialog.show();
        }

        if (id == R.id.action_share) {
            final CharSequence[] options = {
                    getString(R.string.menu_share_screenshot),
                    getString(R.string.menu_save_screenshot),
                    getString(R.string.menu_share_link),
                    getString(R.string.menu_share_link_browser),
                    getString(R.string.menu_share_link_copy)};
            new AlertDialog.Builder(HHS_Browser.this)
                    .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    })
                    .setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals(getString(R.string.menu_share_link))) {
                                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                sharingIntent.setType("text/plain");
                                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, mWebView.getTitle());
                                sharingIntent.putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
                                startActivity(Intent.createChooser(sharingIntent, (getString(R.string.app_share_link))));
                            }
                            if (options[item].equals(getString(R.string.menu_share_screenshot))) {
                                screenshot();

                                if (shareFile.exists()) {
                                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                    sharingIntent.setType("image/png");
                                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, mWebView.getTitle());
                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
                                    Uri bmpUri = Uri.fromFile(shareFile);
                                    sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                    startActivity(Intent.createChooser(sharingIntent, (getString(R.string.app_share_screenshot))));
                                }
                            }
                            if (options[item].equals(getString(R.string.menu_save_screenshot))) {
                                screenshot();
                            }
                            if (options[item].equals(getString(R.string.menu_share_link_browser))) {
                                String  url = mWebView.getUrl();
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                HHS_Browser.this.startActivity(intent);
                            }
                            if (options[item].equals(getString(R.string.menu_share_link_copy))) {
                                String  url = mWebView.getUrl();
                                ClipboardManager clipboard = (ClipboardManager) HHS_Browser.this.getSystemService(Context.CLIPBOARD_SERVICE);
                                clipboard.setPrimaryClip(ClipData.newPlainText("text", url));
                                Snackbar.make(mWebView, R.string.context_linkCopy_toast, Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }).show();
        }

        if (id == R.id.action_not) {

            final String title = mWebView.getTitle();
            final String url = mWebView.getUrl();
            final String text = url + "";

            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String dateCreate = format.format(date);

            sharedPref.edit()
                    .putString("handleTextTitle", title)
                    .putString("handleTextText", text)
                    .putString("handleTextIcon", "")
                    .putString("handleTextAttachment", "")
                    .putString("handleTextCreate", dateCreate)
                    .putString("handleTextSeqno", "")
                    .apply();
            helper_notes.editNote(HHS_Browser.this);
        }

        if (id == R.id.action_bookmark) {
            helper_main.isOpened(HHS_Browser.this);
            helper_main.switchToActivity(HHS_Browser.this, Popup_bookmarks.class, "", false);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setNavArrows() {
        if (sharedPref.getString ("nav", "2").equals("2") || sharedPref.getString ("nav", "2").equals("3")){
            if (mWebView.canGoBack()) {
                imageButton_left.setVisibility(View.VISIBLE);
            } else {
                imageButton_left.setVisibility(View.INVISIBLE);
            }
            imageButton_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mWebView.goBack();
                }
            });

            if (mWebView.canGoForward()) {
                imageButton_right.setVisibility(View.VISIBLE);
            } else {
                imageButton_right.setVisibility(View.INVISIBLE);
            }
            imageButton_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mWebView.goForward();
                }
            });
        }
    }

    class myWebChromeClient extends WebChromeClient {

        public void onProgressChanged(WebView view, int progress) {
            String url = mWebView.getUrl();

            progressBar.setProgress(progress);
            setNavArrows();
            if (url != null && url.contains("moodle.huebsch.ka.schule-bw.de")) {
                mWebView.loadUrl("javascript:(function() { " +
                        "var head = document.getElementsByClassName('navbar navbar-fixed-top moodle-has-zindex')[0];" +
                        "head.parentNode.removeChild(head);" +
                        "})()");
            }

            if (url != null) {
                setTitle(mWebView.getTitle());
            }

            progressBar.setVisibility(progress == 100 ? View.GONE : View.VISIBLE);
        }

        //For Android 4.1
        @SuppressWarnings({"UnusedParameters", "unused"})
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(Intent.createChooser(i, getString(R.string.app_share_file)),
                    RESULT_CODE_ICE_CREAM);
        }

        //For Android5.0+
        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback,
                FileChooserParams fileChooserParams) {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePathCallback;

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e(TAG, "Unable to create Image File", ex);
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("*/*");

            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.app_share_file));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

            startActivityForResult(chooserIntent, REQUEST_CODE_LOLLIPOP);

            return true;
        }

        @Override
        public void onShowCustomView(View view,CustomViewCallback callback) {

            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;
            mWebView.setVisibility(View.GONE);
            customViewContainer.setVisibility(View.VISIBLE);
            customViewContainer.addView(view);
            customViewCallback = callback;
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();    //To change body of overridden methods use File | Settings | File Templates.
            if (mCustomView == null)
                return;

            mWebView.setVisibility(View.VISIBLE);
            customViewContainer.setVisibility(View.GONE);

            // Hide the custom view.
            mCustomView.setVisibility(View.GONE);

            // Remove the custom view from its container.
            customViewContainer.removeView(mCustomView);
            customViewCallback.onCustomViewHidden();

            mCustomView = null;
        }
    }

    private void screenshot() {

        shareFile = helper_main.newFile();

        try{
            mWebView.measure(View.MeasureSpec.makeMeasureSpec(
                    View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            mWebView.layout(0, 0, mWebView.getMeasuredWidth(), mWebView.getMeasuredHeight());
            mWebView.setDrawingCacheEnabled(true);
            mWebView.buildDrawingCache();

            bitmap = Bitmap.createBitmap(mWebView.getMeasuredWidth(),
                    mWebView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            int iHeight = bitmap.getHeight();
            canvas.drawBitmap(bitmap, 0, iHeight, paint);
            mWebView.draw(canvas);

        }catch (OutOfMemoryError e) {
            e.printStackTrace();
            Snackbar.make(mWebView, R.string.toast_screenshot_failed, Snackbar.LENGTH_SHORT).show();
        }

        if (bitmap != null) {
            try {
                OutputStream fOut;
                fOut = new FileOutputStream(shareFile);

                bitmap.compress(Bitmap.CompressFormat.PNG, 50, fOut);
                fOut.flush();
                fOut.close();
                bitmap.recycle();

                Snackbar.make(mWebView, getString(R.string.context_saveImage_toast) + " " + helper_main.newFileName() , Snackbar.LENGTH_SHORT).show();

                Uri uri = Uri.fromFile(shareFile);
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                sendBroadcast(intent);

            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(mWebView, R.string.toast_perm, Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}