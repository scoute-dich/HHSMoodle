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

package de.baumann.hhsmoodle.browser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.Activity_settings;
import de.baumann.hhsmoodle.bookmarks.bookmarks_database;
import de.baumann.hhsmoodle.bookmarks.bookmarks_helper;
import de.baumann.hhsmoodle.helper.Class_SwipeTouchListener;
import de.baumann.hhsmoodle.helper.Class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.helper.helper_security;

import static android.content.Context.DOWNLOAD_SERVICE;
import static de.baumann.hhsmoodle.helper.helper_main.createDate;

public class browser_fragment extends Fragment {

    private WebView mWebView;
    private ProgressBar progressBar;

    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private View mCustomView;
    private myWebChromeClient mWebChromeClient;
    private SharedPreferences sharedPref;

    private Class_SecurePreferences sharedPrefSec;
    private ValueCallback<Uri[]> mFilePathCallback;

    private String mCameraPhotoPath;

    private static final int REQUEST_CODE_LOLLIPOP = 1;

    private void hideCustomView() {
        mWebChromeClient.onHideCustomView();
    }
    private boolean inCustomView() {
        return (mCustomView != null);
    }


    private bookmarks_database db;
    private ListView lv;
    private BottomSheetDialog bottomSheetDialog;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_screen_browser, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }

        sharedPrefSec = new Class_SecurePreferences(Objects.requireNonNull(getActivity()), "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPref.edit().putString("browserStarted", "true").apply();

        customViewContainer = rootView.findViewById(R.id.customViewContainer);
        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        mWebView = rootView.findViewById(R.id.webView);
        mWebChromeClient = new myWebChromeClient();
        mWebView.setWebChromeClient(mWebChromeClient);

        RelativeLayout webViewLayout =rootView.findViewById(R.id.webViewLayout);
        browser_helper.webView_Settings(getActivity(), mWebView);
        browser_helper.webView_WebViewClient(getActivity(), mWebView, webViewLayout);

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
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                                DownloadManager dm = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                                Objects.requireNonNull(dm).enqueue(request);

                                Snackbar.make(mWebView, getString(R.string.toast_download) + " " + filename , Snackbar.LENGTH_LONG).show();
                                getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                            }
                        });
                snackbar.show();
            }
        });


        try {
            if (sharedPrefSec.getString("username").isEmpty() ||
                    sharedPrefSec.getString("password").isEmpty()) {
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

        setHasOptionsMenu(true);
        return rootView;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = createDate();
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
                            startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                        }
                    });
            snackbar.show();
            Objects.requireNonNull(getActivity()).unregisterReceiver(onComplete);
        }
    };

    public void doBack() {
        //BackPressed in activity will call this;
        if (inCustomView()) {
            hideCustomView();
        } else if ((mCustomView == null) && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            mWebView.destroy();
            Objects.requireNonNull(getActivity()).finish();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_browser, menu);
    }

    private void setBookmarksList() {

        //display data
        final int layoutstyle=R.layout.dialog_bookmark_item;
        int[] xml_id = new int[] {
                R.id.textView_title_notes
        };
        String[] column = new String[] {
                "bookmarks_title"
        };
        final Cursor row = db.fetchAllData(getActivity());
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), layoutstyle, row, column, xml_id, 0) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                Cursor row = (Cursor) lv.getItemAtPosition(position);
                final String bookmarks_icon = row.getString(row.getColumnIndexOrThrow("bookmarks_icon"));
                final String bookmarks_attachment = row.getString(row.getColumnIndexOrThrow("bookmarks_attachment"));

                View v = super.getView(position, convertView, parent);
                ImageView iv_icon = v.findViewById(R.id.icon_notes);
                final ImageView iv_attachment = v.findViewById(R.id.att_notes);
                helper_main.switchIcon(getActivity(), bookmarks_icon, "bookmarks_icon", iv_icon);

                switch (bookmarks_attachment) {
                    case "":
                        iv_attachment.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        iv_attachment.setVisibility(View.VISIBLE);
                        iv_attachment.setImageResource(R.drawable.star_grey);
                        break;
                }
                return v;
            }
        };

        lv.setAdapter(adapter);

        if (lv.getAdapter().getCount() == 0) {
            bookmarks_helper.insertDefaultBookmarks(getActivity());
            setBookmarksList();
        }
        //onClick function
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {
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

                final CharSequence[] options = {
                        getString(R.string.bookmark_edit),
                        getString(R.string.bookmark_remove),
                        getString(R.string.bookmark_share),
                        getString(R.string.bookmark_icon),
                        getString(R.string.bookmark_setFav)};
                new AlertDialog.Builder(getActivity())
                        .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @SuppressWarnings("ConstantConditions")
                            @Override
                            public void onClick(DialogInterface dialog, int item) {

                                if (options[item].equals (getString(R.string.bookmark_icon))) {
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
                                            new helper_main.Item(getString(R.string.text_tit_12), R.drawable.ic_pencil_grey600_48dp),
                                            new helper_main.Item(getString(R.string.text_tit_9), R.drawable.ic_check_grey600_48dp),
                                            new helper_main.Item(getString(R.string.text_tit_10), R.drawable.ic_clock_grey600_48dp),
                                            new helper_main.Item(getString(R.string.text_tit_13), R.drawable.ic_bookmark_grey600_48dp),
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
                                            getActivity(),
                                            android.R.layout.select_dialog_item,
                                            android.R.id.text1,
                                            items){
                                        @NonNull
                                        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                                            //Use super class to create the View
                                            View v = super.getView(position, convertView, parent);
                                            TextView tv = v.findViewById(android.R.id.text1);
                                            tv.setTextSize(18);
                                            tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);
                                            //Add margin between image and text (support various screen densities)
                                            int dp5 = (int) (24 * getResources().getDisplayMetrics().density + 0.5f);
                                            tv.setCompoundDrawablePadding(dp5);

                                            return v;
                                        }
                                    };

                                    new AlertDialog.Builder(getActivity())
                                            .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    dialog.cancel();
                                                }
                                            })
                                            .setAdapter(adapter, new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int item) {
                                                    switch (item) {
                                                        case 0:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "01", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 1:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "02", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 2:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "03", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 3:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "04", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 4:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "05", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 5:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "06", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 6:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "07", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 7:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "08", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 8:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "09", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 9:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "10", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 10:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "11", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 11:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "12", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 12:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "13", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 13:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "14", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 14:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "15", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 15:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "16", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 16:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "17", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 17:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "18", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 18:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "19", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 19:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "20", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 20:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "21", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 21:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "22", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 22:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "23", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                        case 23:
                                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_icon), "24", bookmarks_fav, "");
                                                            setBookmarksList();
                                                            break;
                                                    }
                                                }
                                            }).show();
                                }

                                if (options[item].equals (getString(R.string.bookmark_setFav))) {
                                    if (bookmarks_fav.equals("")) {

                                        if(db.isExistFav("true")){
                                            bottomSheetDialog.cancel();
                                            Snackbar.make(mWebView, R.string.bookmark_setFav_not, Snackbar.LENGTH_LONG).show();
                                        }else{
                                            db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_url), bookmarks_icon, "true", "");
                                            setBookmarksList();
                                            sharedPref.edit()
                                                    .putString("favoriteURL", bookmarks_url)
                                                    .putString("favoriteTitle", bookmarks_title)
                                                    .apply();
                                        }
                                    } else {
                                        db.update(Integer.parseInt(_id), helper_main.secString(bookmarks_title), helper_main.secString(bookmarks_url), bookmarks_icon, "", "");
                                        setBookmarksList();
                                    }
                                }


                                if (options[item].equals(getString(R.string.bookmark_edit))) {

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
                                    final AlertDialog dialog2 = builder.create();
                                    dialog2.show();
                                    helper_main.showKeyboard(getActivity(),edit_title);
                                }

                                if (options[item].equals (getString(R.string.bookmark_share))) {
                                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, bookmarks_title);
                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, bookmarks_url);
                                    startActivity(Intent.createChooser(sharingIntent, (getString(R.string.app_share))));
                                }

                                if (options[item].equals(getString(R.string.bookmark_remove))) {
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
                                }
                            }
                        }).show();

                return true;
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.menu_more_help:
                helper_main.showHelpDialog(getActivity());
                return true;

            case R.id.menu_more_settings:
                helper_main.switchToActivity(getActivity(), Activity_settings.class);
                return true;

            case R.id.menu_reload:
                if (mWebView.getUrl() == null) {
                    mWebView.loadUrl(sharedPref.getString("favoriteURL", "https://moodle.huebsch.ka.schule-bw.de/moodle/my/"));
                } else {
                    mWebView.reload();
                }
                return true;

            case R.id.menu_more_files:
                startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                return true;

            case R.id.menu_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, mWebView.getTitle());
                sharingIntent.putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
                startActivity(Intent.createChooser(sharingIntent, (getString(R.string.app_share))));
                return true;

            case R.id.menu_bookmark:
                String username = sharedPrefSec.getString("username");

                db = new bookmarks_database(getActivity());
                db.open();
                bottomSheetDialog = new BottomSheetDialog(Objects.requireNonNull(getActivity()));
                View dialogView = View.inflate(getActivity(), R.layout.dialog_bookmark, null);
                ImageView imageView = dialogView.findViewById(R.id.bookmarkHeader);
                helper_main.setImageHeader(getActivity(), imageView);
                TextView userName = dialogView.findViewById(R.id.userName);
                userName.setText(username);

                ImageButton ib_settings = dialogView.findViewById(R.id.ib_settings);
                ib_settings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        helper_main.switchToActivity(getActivity(), Activity_settings.class);
                    }
                });

                ImageButton ib_help = dialogView.findViewById(R.id.ib_help);
                ib_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        helper_main.showHelpDialog(getActivity());
                    }
                });

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
                                    @SuppressWarnings("ConstantConditions")
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
                bottomSheetDialog.setContentView(dialogView);
                bottomSheetDialog.show();
                setBookmarksList();
                return true;

            case R.id.menu_finish:
                mWebView.destroy();
                Objects.requireNonNull(getActivity()).finish();
                return true;

            case R.id.menu_save_pdf:
                String title = mWebView.getTitle() + ".pdf";
                String pdfTitle = mWebView.getTitle();
                PrintManager printManager = (PrintManager) Objects.requireNonNull(getActivity()).getSystemService(Context.PRINT_SERVICE);
                PrintDocumentAdapter printAdapter = mWebView.createPrintDocumentAdapter(title);
                Objects.requireNonNull(printManager).print(pdfTitle, printAdapter, new PrintAttributes.Builder().build());
                return true;

            case R.id.menu_save_bookmark:

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
                helper_main.showKeyboard(getActivity(),edit_title);

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

                return true;
        }
        return false;
    }

    private class myWebChromeClient extends WebChromeClient {

        public void onProgressChanged(WebView view, int progress) {
            String url = mWebView.getUrl();

            progressBar.setProgress(progress);

            if (url != null && url.contains("moodle.huebsch.ka.schule-bw.de")) {
                mWebView.loadUrl("javascript:(function() { " +
                        "var head = document.getElementsByClassName('navbar navbar-fixed-top moodle-has-zindex')[0];" +
                        "head.parentNode.removeChild(head);" +
                        "})()");
            }

            progressBar.setVisibility(progress == 100 ? View.GONE : View.VISIBLE);
        }

        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback,
                FileChooserParams fileChooserParams) {

            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePathCallback;

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    String TAG = "HHS_Moodle_Browser";
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
            chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.app_share));
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
}