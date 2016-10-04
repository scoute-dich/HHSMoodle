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

package de.baumann.hhsmoodle.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.OnSwipeTouchListener;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Popup_calendar extends AppCompatActivity  {

    private WebView mWebView;
    private SwipeRefreshLayout swipeView;
    private ProgressBar progressBar;

    private boolean isNetworkUnAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo == null || !activeNetworkInfo.isConnected();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }

        setContentView(R.layout.activity_popup_calendar);

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String fontSizeST = sharedPref.getString("font", "100");
        int fontSize = Integer.parseInt(fontSizeST);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

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
        mWebView.getSettings().setTextZoom(fontSize);
        mWebView.loadUrl("https://moodle.huebsch.ka.schule-bw.de/moodle/calendar/view.php");

        if (sharedPref.getBoolean ("java", false)){
            mWebView.getSettings().setJavaScriptEnabled(true);
        }

        if (isNetworkUnAvailable()) { // loading offline
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            Snackbar.make(mWebView, R.string.toast_cache, Snackbar.LENGTH_LONG).show();
        }

        if (sharedPref.getBoolean ("swipe", false)){
            mWebView.setOnTouchListener(new OnSwipeTouchListener(Popup_calendar.this) {

                public void onSwipeRight() {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        Snackbar.make(mWebView, R.string.toast_back, Snackbar.LENGTH_LONG).show();
                    }
                }

                public void onSwipeLeft() {
                    if (mWebView.canGoForward()) {
                        mWebView.goForward();
                    } else {
                        Snackbar.make(mWebView, R.string.toast_forward, Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }

        mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Popup_calendar.this);
                final String username = sharedPref.getString("username", "");
                final String password = sharedPref.getString("password", "");

                swipeView.setRefreshing(false);

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

            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if(url.startsWith("mailto:")){
                    Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                    startActivity(i);
                } else if(url.contains("calendar")) {
                    view.loadUrl(url);
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                }
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {
                final String url = mWebView.getUrl();

                progressBar.setProgress(progress);

                if (progress > 0 && progress <= 60) {
                    if (url != null && url.contains("moodle.huebsch.ka.schule-bw.de")) {
                        mWebView.loadUrl("javascript:(function() { " +
                                "var head = document.getElementsByClassName('navbar navbar-fixed-top moodle-has-zindex')[0];" +
                                "head.parentNode.removeChild(head);" +
                                "var head2 = document.getElementsByClassName('clearfix')[0];" +
                                "head2.parentNode.removeChild(head2);" +
                                "})()");
                    }
                }

                if (progress > 60) {
                    if (url != null && url.contains("moodle.huebsch.ka.schule-bw.de")) {
                        mWebView.loadUrl("javascript:(function() { " +
                                "var head = document.getElementsByClassName('navbar navbar-fixed-top moodle-has-zindex')[0];" +
                                "head.parentNode.removeChild(head);" +
                                "var head2 = document.getElementsByClassName('clearfix')[0];" +
                                "head2.parentNode.removeChild(head2);" +
                                "})()");
                    }
                }

                progressBar.setVisibility(progress == 100 ? View.GONE : View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }
}