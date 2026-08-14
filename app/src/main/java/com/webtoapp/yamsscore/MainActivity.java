package com.webtoapp.yamsscore;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private LinearLayout loadingView;
    private ProgressBar progressBar;

    private static final String START_URL = "https://www.yams-bysvg.base44.app";
    private static final int SPLASH_DURATION = 2000;
    private static final boolean SPLASH_ENABLED = true;
    private static final String BACK_BEHAVIOR = "confirm";
    private static final String EXT_LINKS = "ask";
    private static final boolean SPECIAL_LINKS = true;
    private static final boolean KEEP_SCREEN_ON = true;
    private static final boolean FULLSCREEN = false;
    private static final List<String> ALLOWED_DOMAINS = Arrays.asList();

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FULLSCREEN) getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
        if (KEEP_SCREEN_ON) getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        webView = new WebView(this);
        loadingView = new LinearLayout(this);
        loadingView.setOrientation(LinearLayout.VERTICAL);
        loadingView.setBackgroundColor(Color.parseColor("#ffffff"));
        loadingView.setGravity(android.view.Gravity.CENTER);

        progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(120, 120);
        loadingView.addView(progressBar, lp);
        if (true) {
          TextView tv = new TextView(this);
          tv.setText("Chargement...");
          tv.setTextColor(Color.parseColor("#6366f1"));
          loadingView.addView(tv);
        }

        setContentView(true ? loadingView : webView);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setGeolocationEnabled(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setJavaScriptCanOpenWindowsAutomatically(true);
        s.setSupportMultipleWindows(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setAllowFileAccess(true);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        if (true) {
            CookieManager.getInstance().setAcceptCookie(true);
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest req) {
                String url = req.getUrl().toString();
                if (SPECIAL_LINKS && (url.startsWith("tel:") || url.startsWith("mailto:")
                        || url.startsWith("sms:") || url.startsWith("whatsapp:") || url.startsWith("https://maps.")
                        || url.startsWith("intent:"))) {
                    try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); } catch (Exception e) {}
                    return true;
                }
                String host = req.getUrl().getHost() == null ? "" : req.getUrl().getHost();
                boolean allowed = ALLOWED_DOMAINS.isEmpty() || ALLOWED_DOMAINS.contains(host);
                if (allowed) return false;
                if ("in_app".equals(EXT_LINKS)) return false;
                if ("browser".equals(EXT_LINKS)) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Lien externe")
                    .setMessage("Ouvrir ce lien dans le navigateur ?\n" + url)
                    .setPositiveButton("Ouvrir", (d, w) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))))
                    .setNegativeButton("Annuler", null).show();
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (true) setContentView(webView);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, false, false);
            }
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                runOnUiThread(() -> request.grant(request.getResources()));
            }
        });

        if (SPLASH_ENABLED) {
            webView.postDelayed(() -> { if (true) setContentView(webView); }, SPLASH_DURATION);
        }
        webView.loadUrl(START_URL);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if ("home".equals(BACK_BEHAVIOR) && webView.canGoBack()) { webView.loadUrl(START_URL); return; }
                if ("confirm".equals(BACK_BEHAVIOR) && !webView.canGoBack()) {
                    new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Quitter")
                        .setMessage("Voulez-vous vraiment quitter l'application ?")
                        .setPositiveButton("Quitter", (d, w) -> finish())
                        .setNegativeButton("Rester", null).show();
                    return;
                }
                if ("exit".equals(BACK_BEHAVIOR) && !webView.canGoBack()) { finish(); return; }
                if (webView.canGoBack()) webView.goBack(); else finish();
            }
        });
    }
}
