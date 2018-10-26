/*
    This file is part of the Browser WebApp.

    Browser WebApp is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Browser WebApp is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Browser webview app.

    If not, see <http://www.gnu.org/licenses/>.
 */

package de.baumann.hhsmoodle.browser;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import java.net.URISyntaxException;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.Activity_settings;
import de.baumann.hhsmoodle.helper.Class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.helper.helper_security;

import static android.content.ContentValues.TAG;

class browser_helper {




    @SuppressLint("SetJavaScriptEnabled")
    public static void webView_Settings(final Activity from, final WebView webView) {

        webView.getSettings().setAppCachePath(from.getApplicationContext().getCacheDir().getAbsolutePath());
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setGeolocationEnabled(false);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default
    }

    public static void webView_WebViewClient (final Activity from, final WebView webView,
                                              final RelativeLayout relativeLayout) {

        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, final String url) {
                super.onPageFinished(view, url);

                ViewPager viewPager = from.findViewById(R.id.viewpager);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(from);
                sharedPref.edit().putString("loadURL", webView.getUrl()).apply();

                if (viewPager.getCurrentItem() == 0) {
                    if (url != null) {
                        from.setTitle(webView.getTitle());
                    }
                }

                if (url != null
                        && url.contains("moodle.huebsch.ka.schule-bw.de/moodle/")
                        && url.contains("/login/")) {
                    relativeLayout.setVisibility(View.INVISIBLE);
                } else {
                    relativeLayout.setVisibility(View.VISIBLE);
                }

                Class_SecurePreferences sharedPrefSec = new Class_SecurePreferences(from, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);
                String username = sharedPrefSec.getString("username");
                String password = sharedPrefSec.getString("password");

                final String js = "javascript:" +
                        "document.getElementById('password').value = '" + password + "';"  +
                        "document.getElementById('username').value = '" + username + "';"  +
                        "var ans = document.getElementsByName('answer');"                  +
                        "document.getElementById('loginbtn').click()";

                view.evaluateJavascript(js, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {

                    }
                });
            }

            @SuppressWarnings("deprecation")
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

            private boolean handleUri(final Uri uri) {
                final String url = uri.toString();
                // Based on some condition you need to determine if you are going to load the url
                // in your web view itself or in a browser.
                // You can use `host` or `scheme` or any part of the `uri` to decide.

                if(url.contains("moodle.huebsch.ka.schule-bw.de/moodle/")) {
                    webView.loadUrl(url);
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                webView.getContext().startActivity(intent);
                return true;
            }
        });
    }
}