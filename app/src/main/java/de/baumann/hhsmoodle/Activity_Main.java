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

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class Activity_Main extends AppCompatActivity {

    private WebView mWebView;
    private ProgressBar progressBar;
    private SharedPreferences sharedPref;
    private ValueCallback<Uri[]> mFilePathCallback;

    private static final int INPUT_FILE_REQUEST_CODE = 1;

    private Bookmarks_Database db;
    private GridView bookmarkList;
    private BottomSheetDialog bottomSheetDialog;
    private TextView favoriteTitleTV;
    private Activity activity;
    private TextView titleView;

    private BottomAppBar bottomAppBar;

    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Class_Helper.applyTheme(this);
        setContentView(R.layout.activity_screen_main);
        setUpBottomAppBar();

        Class_Helper.checkPin(Activity_Main.this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        WebView.enableSlowWholeDocumentDraw();

        activity = Activity_Main.this;

        PreferenceManager.setDefaultValues(activity, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        mWebView = findViewById(R.id.webView);
        myWebChromeClient mWebChromeClient = new myWebChromeClient();
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setGeolocationEnabled(false);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        final RelativeLayout loginScreen = findViewById(R.id.loginScreen);
        mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, final String url) {
                super.onPageFinished(view, url);


                if (url != null && url.contains(sharedPref.getString("link", "moodle.huebsch.ka.schule-bw.de/moodle/")) && url.contains("/login/")) {
                    loginScreen.setVisibility(View.VISIBLE);
                } else {
                    loginScreen.setVisibility(View.INVISIBLE);
                }

                String username = sharedPref.getString("username", "");
                String password = sharedPref.getString("password", "");

                final String js = "javascript:" +
                        "document.getElementById('password').value = '" + password + "';"  +
                        "document.getElementById('username').value = '" + username + "';"  +
                        "var ans = document.getElementsByName('answer');"                  +
                        "document.getElementById('loginbtn').click()";

                view.evaluateJavascript(js, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {}
                });
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                final Uri uri = Uri.parse(url);
                return handleUri(uri);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                final Uri uri = request.getUrl();
                return handleUri(uri);
            }

            @SuppressWarnings("SameReturnValue")
            private boolean handleUri(final Uri uri) {
                final String url = uri.toString();
                if(url.contains(sharedPref.getString("link", "moodle.huebsch.ka.schule-bw.de/moodle/"))) {
                    mWebView.loadUrl(url);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    mWebView.getContext().startActivity(intent);
                }
                return true;
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {

            public void onDownloadStart(final String url, String userAgent, final String contentDisposition, final String mimetype, long contentLength) {
                final String filename= URLUtil.guessFileName(url, contentDisposition, mimetype);
                String text = getString(R.string.toast_download_1) + " " + filename;

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
                View dialogView = View.inflate(activity, R.layout.dialog_action, null);
                TextView textView = dialogView.findViewById(R.id.dialog_text);
                textView.setText(text);
                Button action_ok = dialogView.findViewById(R.id.action_ok);
                action_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.cancel();
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        CookieManager cookieManager = CookieManager.getInstance();
                        String cookie = cookieManager.getCookie(url);
                        request.addRequestHeader("Cookie", cookie);
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setTitle(filename);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                        DownloadManager manager = (DownloadManager) Objects.requireNonNull(activity).getSystemService(Context.DOWNLOAD_SERVICE);
                        assert manager != null;
                        activity.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                        if (android.os.Build.VERSION.SDK_INT >= 23 && android.os.Build.VERSION.SDK_INT < 29) {
                            int hasWRITE_EXTERNAL_STORAGE = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                                Class_Helper.grantPermissionsStorage(activity);
                            } else {
                                manager.enqueue(request);
                                Toast.makeText(activity, getString(R.string.toast_download) + " " + filename, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            manager.enqueue(request);
                            Toast.makeText(activity, getString(R.string.toast_download) + " " + filename, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Button action_cancel = dialogView.findViewById(R.id.action_cancel);
                action_cancel.setText(R.string.toast_cancel);
                action_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.cancel();
                    }
                });
                bottomSheetDialog.setContentView(dialogView);
                bottomSheetDialog.show();
                Class_Helper.setBottomSheetBehavior(bottomSheetDialog, dialogView);
            }
        });

        try {
            if (sharedPref.getString("username", "").isEmpty() || sharedPref.getString("password", "").isEmpty()) {
                Class_Helper.setLoginData (activity);
            } else {
                mWebView.loadUrl(sharedPref.getString("favoriteURL", "https://moodle.huebsch.ka.schule-bw.de/moodle/my/"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Class_Helper.setLoginData (activity);
        }

        Button editLogin = findViewById(R.id.bt_editLogin);
        editLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class_Helper.setLoginData(activity);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpBottomAppBar() {

        bottomAppBar = findViewById(R.id.bar);
        bottomAppBar.setNavigationIcon(null);
        setSupportActionBar(bottomAppBar);

        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search_close:
                        mWebView.findAllAsync(null);
                        titleView.setText(mWebView.getTitle());
                        bottomAppBar.replaceMenu(R.menu.menu_main);
                        break;
                    case R.id.action_bookmark:
                        openBookmarkDialog();
                        break;
                    case R.id.action_menu:
                        openMenu();
                        break;
                }
                return false;
            }
        });

        bottomAppBar.setOnTouchListener(new SwipeTouchListener(activity) {
            public void onSwipeTop() {}
            public void onSwipeBottom() {}
            public void onSwipeRight() {
                if (mWebView.canGoForward()) {
                    mWebView.goForward();
                } else {
                    Toast.makeText(activity, getString(R.string.toast_notForward), Toast.LENGTH_SHORT).show();
                }
            }
            public void onSwipeLeft() {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    Toast.makeText(activity, getString(R.string.toast_notBack), Toast.LENGTH_SHORT).show();
                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenu();
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openBookmarkDialog();
                return false;
            }
        });

        fab.setOnTouchListener(new SwipeTouchListener(activity) {
            public void onSwipeTop() {}
            public void onSwipeBottom() {}
            public void onSwipeRight() {
                if (mWebView.canGoForward()) {
                    mWebView.goForward();
                } else {
                    Toast.makeText(activity, getString(R.string.toast_notForward), Toast.LENGTH_SHORT).show();
                }
            }
            public void onSwipeLeft() {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    Toast.makeText(activity, getString(R.string.toast_notBack), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openBookmarkDialog () {
        db = new Bookmarks_Database(activity);
        db.open();
        bottomSheetDialog = new BottomSheetDialog(Objects.requireNonNull(activity));
        View dialogView = View.inflate(activity, R.layout.grid_layout, null);
        String favoriteTitle = getString(R.string.bookmark_setFav) + ": " + sharedPref.getString("favoriteTitle", "Dashboard");
        favoriteTitleTV = dialogView.findViewById(R.id.grid_title);
        favoriteTitleTV.setText(favoriteTitle);
        bookmarkList = dialogView.findViewById(R.id.grid_item);
        setBookmarksList();
        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
    }

    private void openMenu () {
        bottomSheetDialog = new BottomSheetDialog(Objects.requireNonNull(activity));
        View dialogView = View.inflate(activity, R.layout.grid_layout, null);
        TextView menuTitle = dialogView.findViewById(R.id.grid_title);
        menuTitle.setText(mWebView.getTitle());
        GridView grid = dialogView.findViewById(R.id.grid_item);
        GridItem_Menu itemAlbum_01 = new GridItem_Menu(getResources().getString(R.string.menu_more_files), R.drawable.icon_download);
        GridItem_Menu itemAlbum_02 = new GridItem_Menu(getResources().getString(R.string.menu_more_settings), R.drawable.icon_settings);
        GridItem_Menu itemAlbum_03 = new GridItem_Menu(getResources().getString(R.string.menu_save_bookmark), R.drawable.icon_bookmark);
        GridItem_Menu itemAlbum_04 = new GridItem_Menu(getResources().getString(R.string.menu_save_pdf), R.drawable.icon_printer);
        GridItem_Menu itemAlbum_05 = new GridItem_Menu(getResources().getString(R.string.menu_share), R.drawable.icon_share);
        GridItem_Menu itemAlbum_06 = new GridItem_Menu(getResources().getString(R.string.menu_finish), R.drawable.icon_exit);
        GridItem_Menu itemAlbum_07 = new GridItem_Menu(getResources().getString(R.string.menu_search), R.drawable.icon_magnify);
        GridItem_Menu itemAlbum_08 = new GridItem_Menu(getResources().getString(R.string.menu_reload), R.drawable.icon_reload);

        final String url = mWebView.getUrl();
        final List<GridItem_Menu> gridList = new LinkedList<>();
        gridList.add(gridList.size(), itemAlbum_02);
        gridList.add(gridList.size(), itemAlbum_01);
        gridList.add(gridList.size(), itemAlbum_03);
        gridList.add(gridList.size(), itemAlbum_05);
        gridList.add(gridList.size(), itemAlbum_04);
        gridList.add(gridList.size(), itemAlbum_07);
        gridList.add(gridList.size(), itemAlbum_08);
        gridList.add(gridList.size(), itemAlbum_06);

        GridAdapter_Menu gridAdapter = new GridAdapter_Menu(activity, gridList);
        grid.setAdapter(gridAdapter);
        grid.setNumColumns(2);
        gridAdapter.notifyDataSetChanged();

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        bottomSheetDialog.cancel();
                        Class_Helper.switchToActivity(activity, Activity_Settings.class);
                        break;
                    case 1:
                        bottomSheetDialog.cancel();
                        startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                        break;
                    case 2:
                        if (url != null) {
                            bottomSheetDialog.cancel();
                            final Bookmarks_Database db = new Bookmarks_Database(activity);
                            db.open();
                            if(db.isExist(Class_Helper.secString(mWebView.getUrl()))){
                                Toast.makeText(activity, getString(R.string.bookmark_saved_not), Toast.LENGTH_SHORT).show();
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                View dialogView = View.inflate(activity, R.layout.dialog_edit_title, null);
                                final EditText edit_title = dialogView.findViewById(R.id.pass_title);
                                edit_title.setHint(R.string.bookmark_edit_hint);
                                edit_title.setText(mWebView.getTitle());
                                builder.setView(dialogView);
                                builder.setTitle(R.string.bookmark_edit);
                                builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        String inputTag = edit_title.getText().toString().trim();
                                        db.insert(Class_Helper.secString(inputTag), Class_Helper.secString(mWebView.getUrl()), "04", "");
                                        dialog.cancel();
                                        Toast.makeText(activity, getString(R.string.bookmark_saved), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }
                                });
                                final AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                        break;
                    case 3:
                        if (url != null) {
                            bottomSheetDialog.cancel();
                            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, mWebView.getTitle());
                            sharingIntent.putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
                            startActivity(Intent.createChooser(sharingIntent, (getString(R.string.app_share))));
                            break;
                        }
                    case 4:
                        if (url != null) {
                            bottomSheetDialog.cancel();
                            try {
                                String title = mWebView.getTitle() + ".pdf";
                                String pdfTitle = mWebView.getTitle();
                                PrintManager printManager = (PrintManager) Objects.requireNonNull(activity).getSystemService(Context.PRINT_SERVICE);
                                PrintDocumentAdapter printAdapter = mWebView.createPrintDocumentAdapter(title);
                                Objects.requireNonNull(printManager).print(pdfTitle, printAdapter, new PrintAttributes.Builder().build());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(activity, getString(R.string.toast_notPrint), Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case 7:
                        bottomSheetDialog.cancel();
                        mWebView.destroy();
                        Objects.requireNonNull(activity).finish();
                        break;
                    case 5:
                        bottomSheetDialog.cancel();
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        View dialogView2 = View.inflate(activity, R.layout.dialog_edit_title, null);
                        final EditText edit_title = dialogView2.findViewById(R.id.pass_title);
                        edit_title.setHint(R.string.dialog_searchHint);
                        builder.setView(dialogView2);
                        builder.setTitle(R.string.menu_search);
                        builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                String text = getResources().getString(R.string.menu_search) + ": " + edit_title.getText();
                                titleView.setText(text);
                                dialog.cancel();
                                mWebView.findAllAsync(edit_title.getText().toString());
                                bottomAppBar.replaceMenu(R.menu.menu_search);

                            }
                        });
                        builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                        break;
                    case 6:
                        bottomSheetDialog.cancel();
                        mWebView.reload();
                        break;
                }
            }
        });
        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
        Class_Helper.setBottomSheetBehavior(bottomSheetDialog, dialogView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            mWebView.destroy();
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        Uri[] results = null;
        // Check that the response is a good one
        if(resultCode == Activity.RESULT_OK) {
            if(data != null) {
                // If there is not data, then we may have taken a photo
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }
        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
    }

    private final BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
            View dialogView = View.inflate(activity, R.layout.dialog_action, null);
            TextView textView = dialogView.findViewById(R.id.dialog_text);
            textView.setText(Class_Helper.textSpannable(activity.getString(R.string.toast_download_2)));
            Button action_ok = dialogView.findViewById(R.id.action_ok);
            action_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.cancel();
                    startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                }
            });
            Button action_cancel = dialogView.findViewById(R.id.action_cancel);
            action_cancel.setText(R.string.toast_cancel);
            action_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.cancel();
                }
            });
            bottomSheetDialog.setContentView(dialogView);
            bottomSheetDialog.show();
            Class_Helper.setBottomSheetBehavior(bottomSheetDialog, dialogView);
            Objects.requireNonNull(activity).unregisterReceiver(onComplete);
        }
    };

    private void setBookmarksList() {

        //display data
        final int layoutStyle = R.layout.item;
        int[] xml_id = new int[] {
                R.id.item_title
        };
        String[] column = new String[] {
                "bookmarks_title"
        };
        final Cursor row = db.fetchAllData(activity);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(activity, layoutStyle, row, column, xml_id, 0) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                Cursor row = (Cursor) bookmarkList.getItemAtPosition(position);
                final String bookmarks_icon = row.getString(row.getColumnIndexOrThrow("bookmarks_icon"));
                View v = super.getView(position, convertView, parent);
                ImageView iv_icon = v.findViewById(R.id.item_icon);
                Class_Helper.switchIcon(activity, bookmarks_icon, iv_icon);
                return v;
            }
        };

        bookmarkList.setAdapter(adapter);
        bookmarkList.setNumColumns(1);

        if (bookmarkList.getAdapter().getCount() == 0) {
            db.insert("Moodle", sharedPref.getString("link", "https://moodle.huebsch.ka.schule-bw.de/moodle/"), "14", "");
            sharedPref.edit()
                    .putString("favoriteURL", "https://moodle.huebsch.ka.schule-bw.de/moodle/")
                    .putString("favoriteTitle", "Dashboard").apply();
            setBookmarksList();
        }
        //onClick function
        bookmarkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                bottomSheetDialog.cancel();
                Cursor row2 = (Cursor) bookmarkList.getItemAtPosition(position);
                final String bookmarks_content = row2.getString(row2.getColumnIndexOrThrow("bookmarks_content"));
                mWebView.loadUrl(bookmarks_content);
            }
        });

        bookmarkList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor row = (Cursor) bookmarkList.getItemAtPosition(position);
                final String _id = row.getString(row.getColumnIndexOrThrow("_id"));
                final String bookmarks_title = row.getString(row.getColumnIndexOrThrow("bookmarks_title"));
                final String bookmarks_url = row.getString(row.getColumnIndexOrThrow("bookmarks_content"));
                final String bookmarks_icon = row.getString(row.getColumnIndexOrThrow("bookmarks_icon"));
                final String bookmarks_fav = row.getString(row.getColumnIndexOrThrow("bookmarks_attachment"));

                final BottomSheetDialog bottomSheetDialog_context = new BottomSheetDialog(Objects.requireNonNull(activity));
                View dialogView = View.inflate(activity, R.layout.grid_layout, null);
                TextView menuTitle = dialogView.findViewById(R.id.grid_title);
                menuTitle.setText(getString(R.string.action_bookmark));
                GridView grid = dialogView.findViewById(R.id.grid_item);
                GridItem_Menu itemAlbum_01 = new GridItem_Menu(getResources().getString(R.string.bookmark_edit), R.drawable.icon_edit);
                GridItem_Menu itemAlbum_02 = new GridItem_Menu(getResources().getString(R.string.bookmark_icon), R.drawable.icon_ui);
                GridItem_Menu itemAlbum_03 = new GridItem_Menu(getResources().getString(R.string.bookmark_setFav), R.drawable.icon_fav);
                GridItem_Menu itemAlbum_04 = new GridItem_Menu(getResources().getString(R.string.bookmark_remove), R.drawable.icon_delete);

                final List<GridItem_Menu> gridList = new LinkedList<>();
                gridList.add(gridList.size(), itemAlbum_01);
                gridList.add(gridList.size(), itemAlbum_02);
                gridList.add(gridList.size(), itemAlbum_03);
                gridList.add(gridList.size(), itemAlbum_04);

                GridAdapter_Menu gridAdapter = new GridAdapter_Menu(activity, gridList);
                grid.setAdapter(gridAdapter);
                gridAdapter.notifyDataSetChanged();

                grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        View dialogView;

                        switch (position) {
                            case 0:
                                bottomSheetDialog_context.cancel();
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                dialogView = View.inflate(activity, R.layout.dialog_edit_title, null);

                                final EditText edit_title = dialogView.findViewById(R.id.pass_title);
                                edit_title.setHint(R.string.bookmark_edit_hint);
                                edit_title.setText(bookmarks_title);

                                builder.setView(dialogView);
                                builder.setTitle(R.string.bookmark_edit);
                                builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        String inputTag = edit_title.getText().toString().trim();
                                        db.update(Integer.parseInt(_id), Class_Helper.secString(inputTag), Class_Helper.secString(bookmarks_url), bookmarks_icon, bookmarks_fav);
                                        setBookmarksList();
                                    }
                                });
                                builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }
                                });
                                final AlertDialog dialog = builder.create();
                                dialog.show();
                                break;
                            case 1:
                                bottomSheetDialog_context.cancel();

                                final BottomSheetDialog bottomSheetDialog_icon = new BottomSheetDialog(Objects.requireNonNull(activity));
                                dialogView = View.inflate(activity, R.layout.grid_layout, null);
                                TextView menuTitle = dialogView.findViewById(R.id.grid_title);
                                menuTitle.setText(getString(R.string.bookmark_icon));
                                GridView grid = dialogView.findViewById(R.id.grid_item);
                                GridItem_Menu itemAlbum_01 = new GridItem_Menu(getResources().getString(R.string.subjects_color_red), R.drawable.circle_red);
                                GridItem_Menu itemAlbum_02 = new GridItem_Menu(getResources().getString(R.string.subjects_color_pink), R.drawable.circle_pink);
                                GridItem_Menu itemAlbum_03 = new GridItem_Menu(getResources().getString(R.string.subjects_color_purple), R.drawable.circle_purple);
                                GridItem_Menu itemAlbum_04 = new GridItem_Menu(getResources().getString(R.string.subjects_color_blue), R.drawable.circle_blue);
                                GridItem_Menu itemAlbum_05 = new GridItem_Menu(getResources().getString(R.string.subjects_color_teal), R.drawable.circle_teal);
                                GridItem_Menu itemAlbum_06 = new GridItem_Menu(getResources().getString(R.string.subjects_color_green), R.drawable.circle_green);
                                GridItem_Menu itemAlbum_07 = new GridItem_Menu(getResources().getString(R.string.subjects_color_lime), R.drawable.circle_lime);
                                GridItem_Menu itemAlbum_08 = new GridItem_Menu(getResources().getString(R.string.subjects_color_yellow), R.drawable.circle_yellow);
                                GridItem_Menu itemAlbum_09 = new GridItem_Menu(getResources().getString(R.string.subjects_color_orange), R.drawable.circle_orange);
                                GridItem_Menu itemAlbum_10 = new GridItem_Menu(getResources().getString(R.string.subjects_color_brown), R.drawable.circle_brown);

                                final List<GridItem_Menu> gridList = new LinkedList<>();
                                gridList.add(gridList.size(), itemAlbum_01);
                                gridList.add(gridList.size(), itemAlbum_02);
                                gridList.add(gridList.size(), itemAlbum_03);
                                gridList.add(gridList.size(), itemAlbum_04);
                                gridList.add(gridList.size(), itemAlbum_05);
                                gridList.add(gridList.size(), itemAlbum_06);
                                gridList.add(gridList.size(), itemAlbum_07);
                                gridList.add(gridList.size(), itemAlbum_08);
                                gridList.add(gridList.size(), itemAlbum_09);
                                gridList.add(gridList.size(), itemAlbum_10);

                                GridAdapter_Menu gridAdapter = new GridAdapter_Menu(activity, gridList);
                                grid.setAdapter(gridAdapter);
                                gridAdapter.notifyDataSetChanged();

                                grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        switch (position) {
                                            case 0:
                                                bottomSheetDialog_icon.cancel();
                                                db.update(Integer.parseInt(_id), Class_Helper.secString(bookmarks_title), Class_Helper.secString(bookmarks_url), "14", bookmarks_fav);
                                                setBookmarksList();
                                                break;
                                            case 1:
                                                bottomSheetDialog_icon.cancel();
                                                db.update(Integer.parseInt(_id), Class_Helper.secString(bookmarks_title), Class_Helper.secString(bookmarks_url), "15", bookmarks_fav);
                                                setBookmarksList();
                                                break;
                                            case 2:
                                                bottomSheetDialog_icon.cancel();
                                                db.update(Integer.parseInt(_id), Class_Helper.secString(bookmarks_title), Class_Helper.secString(bookmarks_url), "16", bookmarks_fav);
                                                setBookmarksList();
                                                break;
                                            case 3:
                                                bottomSheetDialog_icon.cancel();
                                                db.update(Integer.parseInt(_id), Class_Helper.secString(bookmarks_title), Class_Helper.secString(bookmarks_url), "17", bookmarks_fav);
                                                setBookmarksList();
                                                break;
                                            case 4:
                                                bottomSheetDialog_icon.cancel();
                                                db.update(Integer.parseInt(_id), Class_Helper.secString(bookmarks_title), Class_Helper.secString(bookmarks_url), "18", bookmarks_fav);
                                                setBookmarksList();
                                                break;
                                            case 5:
                                                bottomSheetDialog_icon.cancel();
                                                db.update(Integer.parseInt(_id), Class_Helper.secString(bookmarks_title), Class_Helper.secString(bookmarks_url), "19", bookmarks_fav);
                                                setBookmarksList();
                                                break;
                                            case 6:
                                                bottomSheetDialog_icon.cancel();
                                                db.update(Integer.parseInt(_id), Class_Helper.secString(bookmarks_title), Class_Helper.secString(bookmarks_url), "20", bookmarks_fav);
                                                setBookmarksList();
                                                break;
                                            case 7:
                                                bottomSheetDialog_icon.cancel();
                                                db.update(Integer.parseInt(_id), Class_Helper.secString(bookmarks_title), Class_Helper.secString(bookmarks_url), "21", bookmarks_fav);
                                                setBookmarksList();
                                                break;
                                            case 8:
                                                bottomSheetDialog_icon.cancel();
                                                db.update(Integer.parseInt(_id), Class_Helper.secString(bookmarks_title), Class_Helper.secString(bookmarks_url), "22", bookmarks_fav);
                                                setBookmarksList();
                                                break;
                                            case 9:
                                                bottomSheetDialog_icon.cancel();
                                                db.update(Integer.parseInt(_id), Class_Helper.secString(bookmarks_title), Class_Helper.secString(bookmarks_url), "23", bookmarks_fav);
                                                setBookmarksList();
                                                break;
                                        }
                                    }
                                });
                                bottomSheetDialog_icon.setContentView(dialogView);
                                bottomSheetDialog_icon.show();
                                Class_Helper.setBottomSheetBehavior(bottomSheetDialog_icon, dialogView);
                                break;
                            case 2:
                                bottomSheetDialog_context.cancel();
                                sharedPref.edit()
                                        .putString("favoriteURL", bookmarks_url)
                                        .putString("favoriteTitle", bookmarks_title).apply();

                                String favoriteTitle = getString(R.string.bookmark_setFav) + ": " + sharedPref.getString("favoriteTitle", "Dashboard");

                                favoriteTitleTV.setText(favoriteTitle);
                                break;
                            case 3:
                                bottomSheetDialog_context.cancel();
                                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
                                dialogView = View.inflate(activity, R.layout.dialog_action, null);
                                TextView textView = dialogView.findViewById(R.id.dialog_text);
                                textView.setText(Class_Helper.textSpannable(activity.getString(R.string.bookmark_remove_confirm)));
                                Button action_ok = dialogView.findViewById(R.id.action_ok);
                                action_ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        bottomSheetDialog.cancel();
                                        db.delete(Integer.parseInt(_id));
                                        setBookmarksList();
                                    }
                                });
                                Button action_cancel = dialogView.findViewById(R.id.action_cancel);
                                action_cancel.setText(R.string.toast_cancel);
                                action_cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        bottomSheetDialog.cancel();
                                    }
                                });
                                bottomSheetDialog.setContentView(dialogView);
                                bottomSheetDialog.show();
                                Class_Helper.setBottomSheetBehavior(bottomSheetDialog, dialogView);
                                break;
                        }
                    }
                });

                bottomSheetDialog_context.setContentView(dialogView);
                bottomSheetDialog_context.show();
                Class_Helper.setBottomSheetBehavior(bottomSheetDialog_context, dialogView);
                return true;
            }
        });
    }

    private class myWebChromeClient extends WebChromeClient {
        public void onProgressChanged(WebView view, int progress) {
            progressBar.setProgress(progress);
            titleView = findViewById(R.id.titleView);
            titleView.setText(mWebView.getTitle());
            mWebView.findAllAsync(null);
            bottomAppBar.replaceMenu(R.menu.menu_main);
            progressBar.setVisibility(progress == 100 ? View.GONE : View.VISIBLE);
        }

        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if(mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePathCallback;
            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("*/*");
            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
            return true;
        }
    }
}