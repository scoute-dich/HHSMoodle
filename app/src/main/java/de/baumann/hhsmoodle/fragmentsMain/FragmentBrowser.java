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

package de.baumann.hhsmoodle.fragmentsMain;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.data_bookmarks.Bookmarks_DbAdapter;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.helper.helper_webView;
import de.baumann.hhsmoodle.popup.Popup_bookmarks;

import static android.content.Context.DOWNLOAD_SERVICE;
import static de.baumann.hhsmoodle.helper.helper_main.newFileDest;

public class FragmentBrowser extends Fragment {

    private WebView mWebView;
    private ProgressBar progressBar;
    private Bitmap bitmap;

    private ImageButton imageButton_left;
    private ImageButton imageButton_right;
    private ViewPager viewPager;

    private final static int RESULT_CODE_ICE_CREAM = 2;

    private ValueCallback<Uri> mUploadMessage;

    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private View mCustomView;
    private myWebChromeClient mWebChromeClient;
    private SharedPreferences sharedPref;
    private File shareFile;
    private ValueCallback<Uri[]> mFilePathCallback;

    private String mCameraPhotoPath;

    private static final int REQUEST_CODE_LOLLIPOP = 1;

    private void hideCustomView() {
        mWebChromeClient.onHideCustomView();
    }
    private boolean inCustomView() {
        return (mCustomView != null);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("SetJavaScriptEnabled")

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_screen_browser, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }

        PreferenceManager.setDefaultValues(getActivity(), R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPref.edit().putString("browserStarted", "true").apply();

        customViewContainer = (FrameLayout) rootView.findViewById(R.id.customViewContainer);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);

        SwipeRefreshLayout swipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);
        assert swipeView != null;
        swipeView.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });

        mWebView = (WebView) rootView.findViewById(R.id.webView);
        mWebChromeClient = new myWebChromeClient();
        mWebView.setWebChromeClient(mWebChromeClient);

        imageButton_left = (ImageButton) rootView.findViewById(R.id.imageButton_left);
        imageButton_right = (ImageButton) rootView.findViewById(R.id.imageButton_right);

        if (sharedPref.getBoolean ("arrow", false)){
            imageButton_left.setVisibility(View.VISIBLE);
            imageButton_right.setVisibility(View.VISIBLE);
        } else {
            imageButton_left.setVisibility(View.INVISIBLE);
            imageButton_right.setVisibility(View.INVISIBLE);
        }

        helper_webView.webView_Settings(getActivity(), mWebView);
        helper_webView.webView_WebViewClient(getActivity(), swipeView, mWebView);

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
                                DownloadManager dm = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                                dm.enqueue(request);

                                Snackbar.make(mWebView, getString(R.string.toast_download) + " " + filename , Snackbar.LENGTH_LONG).show();
                                getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                            }
                        });
                snackbar.show();
            }
        });

        String URLtoOpen  = sharedPref.getString("loadURL", "");
        if (URLtoOpen.isEmpty()) {
            mWebView.loadUrl(sharedPref.getString("favoriteURL", "https://moodle.huebsch.ka.schule-bw.de/moodle/my/"));
        } else {
            mWebView.loadUrl(URLtoOpen);
        }

        setHasOptionsMenu(true);
        return rootView;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(new Date());
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
                sharedPref.edit().putString("load_next", "false").apply();
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                }
                mUploadMessage.onReceiveValue(uri);
                mUploadMessage = null;
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        sharedPref.edit().putString("load_next", "true").apply();
                    }
                }, 1500);
                break;
            case REQUEST_CODE_LOLLIPOP:
                sharedPref.edit().putString("load_next", "false").apply();
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
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        sharedPref.edit().putString("load_next", "true").apply();
                    }
                }, 1500);
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
                            ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
                            viewPager.setCurrentItem(5, true);
                        }
                    });
            snackbar.show();
            getActivity().unregisterReceiver(onComplete);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (viewPager.getCurrentItem() == 0) {
            refresh();
        }
    }

    public void doBack() {
        //BackPressed in activity will call this;
        if (inCustomView()) {
            hideCustomView();
        } else if ((mCustomView == null) && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            mWebView.stopLoading();
            helper_main.onClose(getActivity());
        }
    }


    private void refresh () {

        if (sharedPref.getString ("load_next", "").equals("true")){

            final String URLtoOpen  = sharedPref.getString("loadURL", "");
            String FAVtoOpen  = sharedPref.getString("favoriteURL", "https://moodle.huebsch.ka.schule-bw.de/moodle/my/");

            if (URLtoOpen.isEmpty()) {
                mWebView.loadUrl(FAVtoOpen);
            } else {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mWebView.loadUrl(URLtoOpen);
                        sharedPref.edit().putString("loadURL", "").apply();
                    }
                }, 200);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_browser, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_sort).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.action_help:
                helper_main.switchToActivity(getActivity(), FragmentBrowser_Help.class, false);
                return true;

            case R.id.action_bookmark:
                helper_main.isOpened(getActivity());
                helper_main.switchToActivity(getActivity(), Popup_bookmarks.class, false);
                return true;

            case R.id.action_saveBookmark:

                final Bookmarks_DbAdapter db = new Bookmarks_DbAdapter(getActivity());
                db.open();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View dialogView = View.inflate(getActivity(), R.layout.dialog_edit_title, null);

                final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                edit_title.setHint(R.string.bookmark_edit_title);
                edit_title.setText(mWebView.getTitle());

                builder.setView(dialogView);
                builder.setTitle(R.string.bookmark_edit_title);
                builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

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
                helper_main.showKeyboard(getActivity(),edit_title);

                dialog2.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Do stuff, possibly set wantToCloseDialog to true then...
                        String inputTag = edit_title.getText().toString().trim();

                        if(db.isExist(mWebView.getUrl())){
                            Snackbar.make(edit_title, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                        }else{
                            db.insert(inputTag, mWebView.getUrl(), "04", "", helper_main.createDate());
                            dialog2.dismiss();
                            Snackbar.make(mWebView, R.string.bookmark_added, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

                return true;

            case R.id.menu_share_screenshot:
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
                return true;

            case R.id.menu_save_screenshot:
                screenshot();
                return true;

            case R.id.menu_share_link:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, mWebView.getTitle());
                sharingIntent.putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
                startActivity(Intent.createChooser(sharingIntent, (getString(R.string.app_share_link))));
                return true;

            case R.id.menu_share_link_browser:
                String  url = mWebView.getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                getActivity().startActivity(intent);
                return true;

            case R.id.menu_share_link_copy:
                String  url2 = mWebView.getUrl();
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(ClipData.newPlainText("text", url2));
                Snackbar.make(mWebView, R.string.context_linkCopy_toast, Snackbar.LENGTH_LONG).show();
                return true;
        }
        return false;
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
            if (progress == 100) {
                setNavArrows();
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
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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
                getActivity().sendBroadcast(intent);

            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(mWebView, R.string.toast_perm, Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}