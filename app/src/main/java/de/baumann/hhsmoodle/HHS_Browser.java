package de.baumann.hhsmoodle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.baumann.hhsmoodle.helper.Database_Browser;
import de.baumann.hhsmoodle.helper.Database_Notes;
import de.baumann.hhsmoodle.helper.OnSwipeTouchListener;
import de.baumann.hhsmoodle.helper.Start;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class HHS_Browser extends AppCompatActivity  {

    private WebView mWebView;
    private SwipeRefreshLayout swipeView;
    private ProgressBar progressBar;

    private static final int ID_SAVE_IMAGE = 10;
    private static final int ID_IMAGE_EXTERNAL_BROWSER = 11;
    private static final int ID_COPY_LINK = 12;
    private static final int ID_SHARE_LINK = 13;
    private static final int ID_SHARE_IMAGE = 14;

    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }

        setContentView(R.layout.activity_browser);

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(toolbar != null) {
            final String startType = sharedPref.getString("startType", "1");
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (startType.equals("2")) {
                        Intent intent_in = new Intent(HHS_Browser.this, Start.class);
                        startActivity(intent_in);
                        finish();
                    } else if (startType.equals("1")) {
                        Intent intent_in = new Intent(HHS_Browser.this, HHS_MainScreen.class);
                        startActivity(intent_in);
                        finish();
                    }
                }
            });

            if (sharedPref.getBoolean ("longPress", false)){
                toolbar.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        finish();
                        return true;
                    }
                });
            }
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);
        assert swipeView != null;
        swipeView.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });

        mWebView = (WebView) findViewById(R.id.webView);
        assert mWebView != null;
        mWebView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setGeolocationEnabled(false);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        registerForContextMenu(mWebView);

        if (sharedPref.getBoolean ("java", false)){
            mWebView.getSettings().setJavaScriptEnabled(true);
        }

        mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(HHS_Browser.this);
                final String username = sharedPref.getString("username", "");
                final String password = sharedPref.getString("password", "");
                // do your stuff here
                swipeView.setRefreshing(false);

                if (username.isEmpty() ) {
                    Snackbar snackbar = Snackbar
                            .make(mWebView, getString(R.string.toast_login), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getString(R.string.toast_yes), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent_in = new Intent(HHS_Browser.this, HHS_UserSettingsActivity.class);
                                    startActivity(intent_in);
                                }
                            });
                    snackbar.show();
                } else {
                    final String js = "javascript:" +
                            "document.getElementById('password').value = '" + password + "';"  +
                            "document.getElementById('username').value = '" + username + "';"  +
                            "var ans = document.getElementsByName('answer');"                  +
                            "document.getElementById('loginbtn').click()";

                    if (Build.VERSION.SDK_INT >= 19) {
                        view.evaluateJavascript(js, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {

                            }
                        });
                    } else {
                        view.loadUrl(js);
                    }
                }
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("mailto:")){
                    Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                    startActivity(i);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Snackbar.make(mWebView, R.string.toast_noInternet, Snackbar.LENGTH_LONG).show();
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);

                    String url = mWebView.getUrl();
                    if (url.contains("moodle")) {
                        mWebView.scrollTo(0, 80);
                        setTitle(mWebView.getTitle());
                    } else {
                        mWebView.scrollTo(0, 0);
                        setTitle(mWebView.getTitle());
                    }

                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) mFilePathCallback.onReceiveValue(null);

                mFilePathCallback = filePathCallback;

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        return false;
                    }
                    // Continue only if the File was successfully created
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");

                Intent[] intentArray;
                intentArray = new Intent[]{takePictureIntent};

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
                return true;
            }
        });

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
                                DownloadManager.Request request = new DownloadManager.Request(
                                        Uri.parse(url));

                                request.addRequestHeader("Cookie", CookieManager.getInstance().getCookie(url));
                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                                request.setDestinationInExternalPublicDir("/HHS_Moodle/", filename);
                                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                dm.enqueue(request);

                                Snackbar.make(mWebView, getString(R.string.toast_download) + " " +
                                        filename , Snackbar.LENGTH_LONG).show();
                            }
                        });
                snackbar.show();
            }
        });

        mWebView.setOnTouchListener(new OnSwipeTouchListener(HHS_Browser.this) {

            public void onSwipeRight() {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                }
            }

            public void onSwipeLeft() {
                if (mWebView.canGoForward()) {
                    mWebView.goForward();
                }
            }
        });

        onNewIntent(getIntent());
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (sharedPref.getBoolean ("perm_notShow", false)){
                int hasWRITE_EXTERNAL_STORAGE = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        new AlertDialog.Builder(HHS_Browser.this)
                                .setMessage(R.string.app_permissions)
                                .setNeutralButton(R.string.toast_notAgain, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplication());
                                        dialog.cancel();
                                        sharedPref.edit()
                                                .putBoolean("perm_notShow", false)
                                                .apply();
                                    }
                                })
                                .setPositiveButton(getString(R.string.toast_yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (android.os.Build.VERSION.SDK_INT >= 23)
                                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                    REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                })
                                .setNegativeButton(getString(R.string.toast_cancel), null)
                                .show();
                        return;
                    }
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_PERMISSIONS);
                }
            }
        }

        File directory = new File(Environment.getExternalStorageDirectory() + "/HHS_Moodle/");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_VIEW.equals(action)) {
            Uri data = intent.getData();
            String link = data.toString();
            mWebView.loadUrl(link);
        } else {
            mWebView.loadUrl(intent.getStringExtra("url"));
            setTitle(intent.getStringExtra("title"));
        }
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

    private final BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {

            final File directory = new File(Environment.getExternalStorageDirectory() + "/HHS_Moodle/");

            Snackbar snackbar = Snackbar
                    .make(mWebView, getString(R.string.toast_download_2), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.toast_yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent target = new Intent(Intent.ACTION_VIEW);
                            target.setDataAndType(Uri.fromFile(directory), "resource/folder");

                            try {
                                startActivity (target);
                            } catch (ActivityNotFoundException e) {
                                Snackbar.make(mWebView, R.string.toast_install_folder, Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
            snackbar.show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
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
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            Intent intent_in = new Intent(HHS_Browser.this, HHS_MainScreen.class);
            startActivity(intent_in);
            finish();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        WebView w = (WebView)v;
        WebView.HitTestResult result = w.getHitTestResult();

        MenuItem.OnMenuItemClickListener handler = new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                WebView.HitTestResult result = mWebView.getHitTestResult();
                String url = result.getExtra();
                switch (item.getItemId()) {
                    //Save image to external memory
                    case ID_SAVE_IMAGE: {

                        try {
                            if (url != null) {
                                Uri source = Uri.parse(url);
                                DownloadManager.Request request = new DownloadManager.Request(source);
                                File destinationFile = new File(Environment.getExternalStorageDirectory() + "/HHS_Moodle/"
                                        + source.getLastPathSegment());
                                request.addRequestHeader("Cookie", CookieManager.getInstance().getCookie(url));
                                request.setDestinationUri(Uri.fromFile(destinationFile));
                                ((DownloadManager) HHS_Browser.this.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
                                Snackbar.make(mWebView, getString(R.string.context_saveImage_toast) + " " +
                                        destinationFile.getAbsolutePath() , Snackbar.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Snackbar.make(mWebView, R.string.toast_perm , Snackbar.LENGTH_LONG).show();
                        }
                    }
                    break;

                    case ID_SHARE_IMAGE:
                        if(url != null) {
                            File directory = new File(Environment.getExternalStorageDirectory() + "/HHS_Moodle/");
                            if (!directory.exists()) {
                                //noinspection ResultOfMethodCallIgnored
                                directory.mkdirs();
                            }

                            try {
                                Uri source = Uri.parse(url);
                                DownloadManager.Request request = new DownloadManager.Request(source);
                                File destinationFile = new File(Environment.getExternalStorageDirectory() + "/HHS_Moodle/"
                                        + "1.jpg");
                                request.addRequestHeader("Cookie", CookieManager.getInstance().getCookie(url));
                                request.setDestinationUri(Uri.fromFile(destinationFile));
                                ((DownloadManager) HHS_Browser.this.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
                                Snackbar.make(mWebView, getString(R.string.context_saveImage_toast) + " " +
                                        destinationFile.getAbsolutePath() , Snackbar.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Snackbar.make(mWebView, R.string.toast_perm , Snackbar.LENGTH_LONG).show();
                            }

                            Uri myUri= Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/HHS_Moodle/"
                                    + "1.jpg"));
                            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                            sharingIntent.setType("image/*");
                            sharingIntent.putExtra(Intent.EXTRA_STREAM, myUri);
                            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            HHS_Browser.this.startActivity(Intent.createChooser(sharingIntent, (getString(R.string.app_share_image))));

                            File tempFile = new File(Environment.getExternalStorageDirectory() +  "/HHS_Moodle/" + "1.jpg");
                            if(tempFile.exists()){
                                tempFile.delete();
                            }
                        }
                        break;

                    case ID_IMAGE_EXTERNAL_BROWSER:
                        if (url != null) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            HHS_Browser.this.startActivity(intent);
                        }
                        break;

                    case ID_COPY_LINK:
                        if (url != null) {
                            ClipboardManager clipboard = (ClipboardManager) HHS_Browser.this.getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboard.setPrimaryClip(ClipData.newPlainText("text", url));
                            Snackbar.make(mWebView, R.string.context_linkCopy_toast, Snackbar.LENGTH_LONG).show();
                        }
                        break;

                    case ID_SHARE_LINK:
                        if (url != null) {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, url);
                            sendIntent.setType("text/plain");
                            HHS_Browser.this.startActivity(Intent.createChooser(sendIntent, getResources()
                                    .getString(R.string.app_share_link)));
                        }
                        break;
                }
                return true;
            }
        };

        if(result.getType() == WebView.HitTestResult.IMAGE_TYPE){
            menu.add(0, ID_SAVE_IMAGE, 0, getString(R.string.context_saveImage)).setOnMenuItemClickListener(handler);
            menu.add(0, ID_SHARE_IMAGE, 0, getString(R.string.context_shareImage)).setOnMenuItemClickListener(handler);
            menu.add(0, ID_IMAGE_EXTERNAL_BROWSER, 0, getString(R.string.context_externalBrowser)).setOnMenuItemClickListener(handler);
        } else if (result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
            menu.add(0, ID_COPY_LINK, 0, getString(R.string.context_linkCopy)).setOnMenuItemClickListener(handler);
            menu.add(0, ID_SHARE_LINK, 0, getString(R.string.menu_share_link)).setOnMenuItemClickListener(handler);
            menu.add(0, ID_IMAGE_EXTERNAL_BROWSER, 0, getString(R.string.context_externalBrowser)).setOnMenuItemClickListener(handler);
        }
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
                final LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setGravity(Gravity.CENTER_HORIZONTAL);
                final EditText input = new EditText(this);
                input.setSingleLine(true);
                layout.setPadding(30, 0, 50, 0);
                layout.addView(input);

                input.setText(mWebView.getTitle());
                final Database_Browser db = new Database_Browser(this);
                final AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                        .setView(layout)
                        .setMessage(R.string.bookmark_edit_title)
                        .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                String inputTag = input.getText().toString().trim();
                                db.addBookmark(inputTag, mWebView.getUrl());
                                db.close();
                                Snackbar.make(mWebView, R.string.bookmark_added, Snackbar.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                dialog.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (id == R.id.action_folder) {
            final File directory = new File(Environment.getExternalStorageDirectory() + "/HHS_Moodle/");
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(directory), "resource/folder");

            try {
                startActivity (target);
            } catch (ActivityNotFoundException e) {
                Snackbar.make(mWebView, R.string.toast_install_folder, Snackbar.LENGTH_LONG).show();
            }
        }

        if (id == android.R.id.home) {
            Intent intent_in = new Intent(HHS_Browser.this, HHS_MainScreen.class);
            startActivity(intent_in);
            finish();
        }

        if (id == R.id.action_help) {
            final SpannableString s = new SpannableString(Html.fromHtml(getString(R.string.helpBrowser_text)));
            Linkify.addLinks(s, Linkify.WEB_URLS);

            final AlertDialog.Builder dialog = new AlertDialog.Builder(HHS_Browser.this)
                    .setTitle(R.string.helpBrowser_title)
                    .setMessage(s)
                    .setPositiveButton(getString(R.string.toast_yes), null);
            dialog.show();
        }

        if (id == R.id.action_notifications) {

            final String title = mWebView.getTitle();

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPref.edit()
                    .putString("noteTitle", title)
                    .putBoolean("click", true)
                    .apply();

            Intent intent_in = new Intent(this, Notes_MainActivity.class);
            startActivity(intent_in);
        }

        if (id == R.id.action_share) {
            final CharSequence[] options = {getString(R.string.menu_share_screenshot),
                    getString(R.string.menu_save_screenshot),
                    getString(R.string.menu_share_link), getString(R.string.menu_share_link_browser),
                    getString(R.string.menu_share_link_copy)};
            new AlertDialog.Builder(HHS_Browser.this)
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

                                Date date = new Date();
                                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy_HH-mm", Locale.getDefault());
                                File file = new File(Environment.getExternalStorageDirectory() + "/HHS_Moodle/", dateFormat.format(date) + ".jpg");

                                if (file.exists()) {
                                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                    sharingIntent.setType("image/png");
                                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, mWebView.getTitle());
                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
                                    Uri bmpUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/HHS_Moodle/"
                                            + dateFormat.format(date) + ".jpg"));
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

            try {

                final LinearLayout layout = new LinearLayout(HHS_Browser.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setGravity(Gravity.CENTER_HORIZONTAL);
                layout.setPadding(50, 0, 50, 0);

                final TextView titleText = new TextView(HHS_Browser.this);
                titleText.setText(R.string.note_edit_title);
                titleText.setPadding(5,50,0,0);
                layout.addView(titleText);

                final EditText titleEdit = new EditText(HHS_Browser.this);
                titleEdit.setText(title);
                layout.addView(titleEdit);

                final TextView contentText = new TextView(HHS_Browser.this);
                contentText.setText(R.string.note_edit_content);
                contentText.setPadding(5,25,0,0);
                layout.addView(contentText);

                final EditText contentEdit = new EditText(HHS_Browser.this);
                contentEdit.setText(text);
                layout.addView(contentEdit);

                ScrollView sv = new ScrollView(HHS_Browser.this);
                sv.pageScroll(0);
                sv.setBackgroundColor(0);
                sv.setScrollbarFadingEnabled(true);
                sv.setVerticalFadingEdgeEnabled(false);
                sv.addView(layout);

                final Database_Notes db = new Database_Notes(HHS_Browser.this);
                final AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                        .setView(sv)
                        .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                String inputTitle = titleEdit.getText().toString().trim();
                                String inputContent = contentEdit.getText().toString().trim();
                                db.addBookmark(inputTitle, url, inputContent);
                                db.close();
                            }
                        })
                        .setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                dialog.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void screenshot() {

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy_HH-mm", Locale.getDefault());

        mWebView.measure(View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mWebView.layout(0, 0, mWebView.getMeasuredWidth(),
                mWebView.getMeasuredHeight());
        mWebView.setDrawingCacheEnabled(true);
        mWebView.buildDrawingCache();
        Bitmap bm = Bitmap.createBitmap(mWebView.getMeasuredWidth(),
                mWebView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas bigcanvas = new Canvas(bm);
        Paint paint = new Paint();
        int iHeight = bm.getHeight();
        bigcanvas.drawBitmap(bm, 0, iHeight, paint);
        mWebView.draw(bigcanvas);
        System.out.println("1111111111111111111111="
                + bigcanvas.getWidth());
        System.out.println("22222222222222222222222="
                + bigcanvas.getHeight());

        try {
            OutputStream fOut;
            File file = new File(Environment.getExternalStorageDirectory() + "/HHS_Moodle/" + dateFormat.format(date) + ".jpg");
            fOut = new FileOutputStream(file);

            bm.compress(Bitmap.CompressFormat.PNG, 50, fOut);
            fOut.flush();
            fOut.close();
            bm.recycle();

            String filename = getString(R.string.toast_screenshot) + " " + Environment.getExternalStorageDirectory() + "/HHS_Moodle/" + dateFormat.format(date) + ".jpg";
            Snackbar.make(swipeView, filename, Snackbar.LENGTH_LONG).show();

            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
            sendBroadcast(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(mWebView, R.string.toast_perm, Snackbar.LENGTH_LONG).show();
        }
    }
}
