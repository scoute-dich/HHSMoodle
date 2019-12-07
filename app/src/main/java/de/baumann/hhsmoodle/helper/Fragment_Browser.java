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

package de.baumann.hhsmoodle.helper;

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
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.bookmarks.bookmarks_database;
import de.baumann.hhsmoodle.bookmarks.bookmarks_helper;

import static de.baumann.hhsmoodle.helper.helper_main.createDate;

public class Fragment_Browser extends Fragment {

    private WebView mWebView;
    private ProgressBar progressBar;

    private SharedPreferences sharedPref;

    private Class_SecurePreferences sharedPrefSec;
    private ValueCallback<Uri[]> mFilePathCallback;

    private static final int INPUT_FILE_REQUEST_CODE = 1;

    private bookmarks_database db;
    private ListView lv;
    private BottomSheetDialog bottomSheetDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView favoriteTitleTV;

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_screen_browser, container, false);
        WebView.enableSlowWholeDocumentDraw();

        sharedPrefSec = new Class_SecurePreferences(Objects.requireNonNull(getActivity()), "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPref.edit().putString("browserStarted", "true").apply();

        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        mWebView = rootView.findViewById(R.id.webView);
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

        final RelativeLayout webViewLayout = rootView.findViewById(R.id.webViewLayout);
        mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, final String url) {
                super.onPageFinished(view, url);

                if (url != null && url.contains("moodle.huebsch.ka.schule-bw.de/moodle/") && url.contains("/login/")) {
                    webViewLayout.setVisibility(View.INVISIBLE);
                } else {
                    webViewLayout.setVisibility(View.VISIBLE);
                }

                Class_SecurePreferences sharedPrefSec = new Class_SecurePreferences(getActivity(), "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);
                String username = sharedPrefSec.getString("username");
                String password = sharedPrefSec.getString("password");

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
                if(url.contains("moodle.huebsch.ka.schule-bw.de/moodle/")) {
                    mWebView.loadUrl(url);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    mWebView.getContext().startActivity(intent);
                }
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

                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                                CookieManager cookieManager = CookieManager.getInstance();
                                String cookie = cookieManager.getCookie(url);
                                request.addRequestHeader("Cookie", cookie);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setTitle(filename);
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                                DownloadManager manager = (DownloadManager) Objects.requireNonNull(getActivity()).getSystemService(Context.DOWNLOAD_SERVICE);
                                assert manager != null;
                                getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                                if (android.os.Build.VERSION.SDK_INT >= 23) {
                                    int hasWRITE_EXTERNAL_STORAGE = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                    if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                                        helper_main.grantPermissionsStorage(getActivity());
                                    } else {
                                        manager.enqueue(request);
                                        Snackbar.make(mWebView, getString(R.string.toast_download) + " " + filename , Snackbar.LENGTH_LONG).show();
                                    }
                                } else {
                                    manager.enqueue(request);
                                    Snackbar.make(mWebView, getString(R.string.toast_download) + " " + filename , Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                snackbar.show();
            }
        });


        try {
            if (sharedPrefSec.getString("username").isEmpty() || sharedPrefSec.getString("password").isEmpty()) {
                helper_security.setLoginData (getActivity());
            } else {
                mWebView.loadUrl(sharedPref.getString("favoriteURL", "https://moodle.huebsch.ka.schule-bw.de/moodle/my/"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            helper_security.setLoginData (getActivity());
        }

        Toolbar ab = getActivity().findViewById(R.id.toolbar);
        ab.setOnTouchListener(new Class_SwipeTouchListener(getActivity()) {

            public void onSwipeTop() {
                mWebView.pageUp(true);
            }
            public void onSwipeBottom() {
                mWebView.pageDown(true);
            }

            public void onSwipeRight() {
                if (mWebView.canGoForward()) {
                    mWebView.goForward();
                } else {
                    Snackbar.make(mWebView, R.string.toast_webviewForward, Snackbar.LENGTH_LONG).show();
                }
            }

            public void onSwipeLeft() {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    Snackbar.make(mWebView, R.string.toast_webViewBack, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        Button editLogin = rootView.findViewById(R.id.bt_editLogin);
        editLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper_security.setLoginData(getActivity());
            }
        });

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mWebView.reload();
                    }
                }
        );

        ImageButton ib_bookmarks = Objects.requireNonNull(getActivity()).findViewById(R.id.ib_bookmarks);
        ib_bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = sharedPrefSec.getString("username");
                String favoriteTitle = sharedPref.getString("favoriteTitle", "Dashboard");

                db = new bookmarks_database(getActivity());
                db.open();
                bottomSheetDialog = new BottomSheetDialog(Objects.requireNonNull(getActivity()));
                View dialogView = View.inflate(getActivity(), R.layout.dialog_bookmark, null);
                ImageView imageView = dialogView.findViewById(R.id.bookmarkHeader);
                helper_main.setImageHeader(getActivity(), imageView);
                TextView userNameTV = dialogView.findViewById(R.id.userName);
                userNameTV.setText(userName);
                favoriteTitleTV = dialogView.findViewById(R.id.favoriteTitle);
                favoriteTitleTV.setText(favoriteTitle);

                ImageButton ib_bookmarkSort = dialogView.findViewById(R.id.ib_bookmarkSort);
                ib_bookmarkSort.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final CharSequence[] options = {
                                getString(R.string.dialog_sortName),
                                getString(R.string.dialog_sortIcon)};
                        new AlertDialog.Builder(getActivity())
                                .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }
                                })
                                .setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int item) {

                                        if (options[item].equals (getString(R.string.dialog_sortName))) {
                                            sharedPref.edit().putString("sortDBB", "title").apply();
                                            setBookmarksList();
                                        }

                                        if (options[item].equals (getString(R.string.dialog_sortIcon))) {
                                            sharedPref.edit().putString("sortDBB", "icon").apply();
                                            setBookmarksList();
                                        }
                                    }
                                }).show();

                    }
                });

                lv = dialogView.findViewById(R.id.dialogList);
                setBookmarksList();
                bottomSheetDialog.setContentView(dialogView);
                bottomSheetDialog.show();
                helper_main.setBottomSheetBehavior(bottomSheetDialog, dialogView, BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        ImageButton ib_menu = getActivity().findViewById(R.id.ib_menu);
        ib_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog = new BottomSheetDialog(Objects.requireNonNull(getActivity()));
                View dialogView = View.inflate(getActivity(), R.layout.grid_menu, null);

                GridView grid = dialogView.findViewById(R.id.grid_filter);
                GridItem_Menu itemAlbum_01 = new GridItem_Menu(getResources().getString(R.string.menu_more_files),
                        R.drawable.icon_download);
                GridItem_Menu itemAlbum_02 = new GridItem_Menu(getResources().getString(R.string.menu_more_settings),
                        R.drawable.icon_settings);
                GridItem_Menu itemAlbum_03 = new GridItem_Menu(getResources().getString(R.string.menu_save_bookmark),
                        R.drawable.icon_bookmark);
                GridItem_Menu itemAlbum_04 = new GridItem_Menu(getResources().getString(R.string.menu_save_pdf),
                        R.drawable.icon_document);
                GridItem_Menu itemAlbum_05 = new GridItem_Menu(getResources().getString(R.string.menu_share),
                        R.drawable.icon_share);
                GridItem_Menu itemAlbum_06 = new GridItem_Menu(getResources().getString(R.string.menu_finish),
                        R.drawable.icon_exit);

                final List<GridItem_Menu> gridList = new LinkedList<>();
                gridList.add(gridList.size(), itemAlbum_02);
                gridList.add(gridList.size(), itemAlbum_01);
                gridList.add(gridList.size(), itemAlbum_03);
                gridList.add(gridList.size(), itemAlbum_05);
                gridList.add(gridList.size(), itemAlbum_04);
                gridList.add(gridList.size(), itemAlbum_06);

                GridAdapter_Menu gridAdapter = new GridAdapter_Menu(getActivity(), gridList);
                grid.setAdapter(gridAdapter);
                gridAdapter.notifyDataSetChanged();

                grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        switch (position) {
                            case 0:
                                bottomSheetDialog.cancel();
                                startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                                break;
                            case 1:
                                bottomSheetDialog.cancel();
                                helper_main.switchToActivity(getActivity(), Activity_Settings.class);
                                break;
                            case 2:
                                bottomSheetDialog.cancel();
                                final bookmarks_database db = new bookmarks_database(getActivity());
                                db.open();

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                View dialogView2 = View.inflate(getActivity(), R.layout.dialog_edit_title, null);

                                final EditText edit_title = dialogView2.findViewById(R.id.pass_title);
                                edit_title.setHint(R.string.bookmark_edit_hint);
                                edit_title.setText(mWebView.getTitle());

                                builder.setView(dialogView2);
                                builder.setTitle(R.string.bookmark_edit);
                                builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {

                                    }
                                });
                                builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }
                                });

                                final AlertDialog dialog = builder.create();
                                // Display the custom alert dialog on interface
                                dialog.show();
                                dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //Do stuff, possibly set wantToCloseDialog to true then...
                                        String inputTag = edit_title.getText().toString().trim();
                                        if(db.isExist(helper_main.secString(mWebView.getUrl()))){
                                            Snackbar.make(edit_title, getString(R.string.bookmark_saved_not), Snackbar.LENGTH_LONG).show();
                                        }else{
                                            db.insert(helper_main.secString(inputTag), helper_main.secString(mWebView.getUrl()), "04", "", createDate());
                                            dialog.dismiss();
                                            Snackbar.make(mWebView, R.string.bookmark_saved, Snackbar.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                break;
                            case 3:
                                bottomSheetDialog.cancel();
                                String title = mWebView.getTitle() + ".pdf";
                                String pdfTitle = mWebView.getTitle();
                                PrintManager printManager = (PrintManager) Objects.requireNonNull(getActivity()).getSystemService(Context.PRINT_SERVICE);
                                PrintDocumentAdapter printAdapter = mWebView.createPrintDocumentAdapter(title);
                                Objects.requireNonNull(printManager).print(pdfTitle, printAdapter, new PrintAttributes.Builder().build());
                                break;
                            case 4:
                                bottomSheetDialog.cancel();
                                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                sharingIntent.setType("text/plain");
                                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, mWebView.getTitle());
                                sharingIntent.putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
                                startActivity(Intent.createChooser(sharingIntent, (getString(R.string.app_share))));
                                break;
                            case 5:
                                bottomSheetDialog.cancel();
                                mWebView.destroy();
                                Objects.requireNonNull(getActivity()).finish();
                                break;
                        }
                    }
                });

                bottomSheetDialog.setContentView(dialogView);
                bottomSheetDialog.show();
                helper_main.setBottomSheetBehavior(bottomSheetDialog, dialogView, BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        return rootView;
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
            Snackbar snackbar = Snackbar
                    .make(mWebView, getString(R.string.toast_download_2), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.toast_yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                        }
                    });
            snackbar.show();
            Objects.requireNonNull(getActivity()).unregisterReceiver(onComplete);
        }
    };

    public void doBack() {
        //BackPressed in activity will call this;
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            mWebView.destroy();
            Objects.requireNonNull(getActivity()).finish();
        }
    }


    private void setBookmarksList() {

        //display data
        final int layoutStyle = R.layout.dialog_bookmark_item;
        int[] xml_id = new int[] {
                R.id.textView_title_notes
        };
        String[] column = new String[] {
                "bookmarks_title"
        };
        final Cursor row = db.fetchAllData(getActivity());
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), layoutStyle, row, column, xml_id, 0) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                Cursor row = (Cursor) lv.getItemAtPosition(position);
                final String bookmarks_icon = row.getString(row.getColumnIndexOrThrow("bookmarks_icon"));
                final String bookmarks_attachment = row.getString(row.getColumnIndexOrThrow("bookmarks_attachment"));

                View v = super.getView(position, convertView, parent);
                ImageView iv_icon = v.findViewById(R.id.icon_notes);
                helper_main.switchIcon(getActivity(), bookmarks_icon, "bookmarks_icon", iv_icon);
                return v;
            }
        };

        lv.setAdapter(adapter);

        if (lv.getAdapter().getCount() == 0) {
            bookmarks_helper.insertDefaultBookmarks(getActivity());
            sharedPref.edit()
                    .putString("favoriteURL", "https://moodle.huebsch.ka.schule-bw.de/moodle/my/")
                    .putString("favoriteTitle", "Dashboard").apply();
            setBookmarksList();
        }
        //onClick function
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                bottomSheetDialog.cancel();
                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String bookmarks_content = row2.getString(row2.getColumnIndexOrThrow("bookmarks_content"));
                mWebView.loadUrl(bookmarks_content);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor row = (Cursor) lv.getItemAtPosition(position);
                final String _id = row.getString(row.getColumnIndexOrThrow("_id"));
                final String bookmarks_title = row.getString(row.getColumnIndexOrThrow("bookmarks_title"));
                final String bookmarks_url = row.getString(row.getColumnIndexOrThrow("bookmarks_content"));
                final String bookmarks_icon = row.getString(row.getColumnIndexOrThrow("bookmarks_icon"));
                final String bookmarks_fav = row.getString(row.getColumnIndexOrThrow("bookmarks_attachment"));

                final BottomSheetDialog bottomSheetDialog_context = new BottomSheetDialog(Objects.requireNonNull(getActivity()));
                View dialogView = View.inflate(getActivity(), R.layout.grid_menu, null);
                GridView grid = dialogView.findViewById(R.id.grid_filter);
                GridItem_Menu itemAlbum_01 = new GridItem_Menu(getResources().getString(R.string.bookmark_edit), R.drawable.icon_edit);
                GridItem_Menu itemAlbum_02 = new GridItem_Menu(getResources().getString(R.string.bookmark_icon), R.drawable.icon_ui);
                GridItem_Menu itemAlbum_03 = new GridItem_Menu(getResources().getString(R.string.bookmark_setFav), R.drawable.icon_fav);
                GridItem_Menu itemAlbum_04 = new GridItem_Menu(getResources().getString(R.string.bookmark_remove), R.drawable.icon_delete);

                final List<GridItem_Menu> gridList = new LinkedList<>();
                gridList.add(gridList.size(), itemAlbum_01);
                gridList.add(gridList.size(), itemAlbum_02);
                gridList.add(gridList.size(), itemAlbum_03);
                gridList.add(gridList.size(), itemAlbum_04);

                GridAdapter_Menu gridAdapter = new GridAdapter_Menu(getActivity(), gridList);
                grid.setAdapter(gridAdapter);
                gridAdapter.notifyDataSetChanged();

                grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        switch (position) {
                            case 0:
                                bottomSheetDialog_context.cancel();
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                View dialogView = View.inflate(getActivity(), R.layout.dialog_edit_title, null);

                                final EditText edit_title = dialogView.findViewById(R.id.pass_title);
                                edit_title.setHint(R.string.bookmark_edit_hint);
                                edit_title.setText(bookmarks_title);

                                builder.setView(dialogView);
                                builder.setTitle(R.string.bookmark_edit);
                                builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        String inputTag = edit_title.getText().toString().trim();
                                        db.update(Integer.parseInt(_id), helper_main.secString(inputTag), helper_main.secString(bookmarks_url), bookmarks_icon, bookmarks_fav, "");
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

                                final BottomSheetDialog bottomSheetDialog_icon = new BottomSheetDialog(Objects.requireNonNull(getActivity()));
                                View dialogView2 = View.inflate(getActivity(), R.layout.grid_menu, null);
                                GridView grid = dialogView2.findViewById(R.id.grid_filter);
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

                                GridAdapter_Menu gridAdapter = new GridAdapter_Menu(getActivity(), gridList);
                                grid.setAdapter(gridAdapter);
                                gridAdapter.notifyDataSetChanged();

                                grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        switch (position) {
                                            case 0:
                                                bottomSheetDialog_icon.cancel();
                                                db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_url), "14", bookmarks_fav, "");
                                                setBookmarksList();
                                                break;
                                            case 1:
                                                bottomSheetDialog_icon.cancel();
                                                db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_url), "15", bookmarks_fav, "");
                                                setBookmarksList();
                                                break;
                                            case 2:
                                                db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_url), "16", bookmarks_fav, "");
                                                setBookmarksList();
                                                break;
                                            case 3:
                                                bottomSheetDialog_icon.cancel();
                                                db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_url), "17", bookmarks_fav, "");
                                                setBookmarksList();
                                                break;
                                            case 4:
                                                bottomSheetDialog_icon.cancel();
                                                db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_url), "18", bookmarks_fav, "");
                                                setBookmarksList();
                                                break;
                                            case 5:
                                                bottomSheetDialog_icon.cancel();
                                                db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_url), "19", bookmarks_fav, "");
                                                setBookmarksList();
                                                break;
                                            case 6:
                                                db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_url), "20", bookmarks_fav, "");
                                                setBookmarksList();
                                                break;
                                            case 7:
                                                bottomSheetDialog_icon.cancel();
                                                db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_url), "21", bookmarks_fav, "");
                                                setBookmarksList();
                                                break;
                                            case 8:
                                                bottomSheetDialog_icon.cancel();
                                                db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_url), "22", bookmarks_fav, "");
                                                setBookmarksList();
                                                break;
                                            case 9:
                                                bottomSheetDialog_icon.cancel();
                                                db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_url), "23", bookmarks_fav, "");
                                                setBookmarksList();
                                                break;
                                        }
                                    }
                                });

                                bottomSheetDialog_icon.setContentView(dialogView2);
                                bottomSheetDialog_icon.show();
                                helper_main.setBottomSheetBehavior(bottomSheetDialog_icon, dialogView2, BottomSheetBehavior.STATE_EXPANDED);


                                break;
                            case 2:
                                bottomSheetDialog_context.cancel();
                                sharedPref.edit()
                                        .putString("favoriteURL", bookmarks_url)
                                        .putString("favoriteTitle", bookmarks_title).apply();
                                favoriteTitleTV.setText(bookmarks_title);
                                break;
                            case 3:
                                bottomSheetDialog_context.cancel();
                                bottomSheetDialog.cancel();
                                Snackbar snackbar = Snackbar
                                        .make(mWebView, R.string.bookmark_remove_confirm, Snackbar.LENGTH_LONG)
                                        .setAction(R.string.toast_yes, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                db.delete(Integer.parseInt(_id));
                                                setBookmarksList();
                                            }
                                        });
                                snackbar.show();
                                break;
                        }
                    }
                });

                bottomSheetDialog_context.setContentView(dialogView);
                bottomSheetDialog_context.show();
                helper_main.setBottomSheetBehavior(bottomSheetDialog_context, dialogView, BottomSheetBehavior.STATE_EXPANDED);
                return true;
            }
        });
    }

    private class myWebChromeClient extends WebChromeClient {

        public void onProgressChanged(WebView view, int progress) {
            String url = mWebView.getUrl();

            progressBar.setProgress(progress);
            Objects.requireNonNull(getActivity()).setTitle(mWebView.getTitle());
            if (url != null && url.contains("moodle.huebsch.ka.schule-bw.de")) {
                mWebView.loadUrl("javascript:(function() { " +
                        "var head = document.getElementsByClassName('navbar navbar-fixed-top moodle-has-zindex')[0];" +
                        "head.parentNode.removeChild(head);" +
                        "})()");
            }
            if (progress == 100) {
                swipeRefreshLayout.setRefreshing(false);
            }
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