package com.oilempire.game;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {

    private static final String TAG = "OilEmpire";

    private WebView webView;
    private SharedPreferences prefs;
    private TextView debugView;
    private ScrollView debugScroll;
    private StringBuilder logBuffer = new StringBuilder();

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        prefs = getSharedPreferences("oil_empire_save", MODE_PRIVATE);

        FrameLayout root = new FrameLayout(this);

        // ═══ WebView ═══
        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.setBackgroundColor(0xFF0A0A0A);

        // ═══ JS Bridge ═══
        webView.addJavascriptInterface(new AndroidBridge(), "AndroidBridge");

        // ═══ WebView Client — نعرض أخطاء التحميل ═══
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {
                String msg = "WEB_ERR: " + err.getDescription() + " | " + req.getUrl();
                Log.e(TAG, msg);
                appendDebug(msg);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "Page loaded: " + url);
                appendDebug("✅ Page loaded");
            }
        });

        // ═══ WebChromeClient — نعرض console logs ═══
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage msg) {
                String line = "JS[" + msg.messageLevel() + "]: " +
                        msg.message() +
                        " @ " + msg.lineNumber();
                Log.d(TAG, line);
                appendDebug(line);
                return true;
            }
        });

        // ═══ تحميل HTML ═══
        webView.loadUrl("file:///android_asset/game/index.html");
        Log.d(TAG, "Loading game...");
        appendDebug("📥 Loading game...");

        root.addView(webView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        // ═══ Debug Overlay (شفاف، يبان الأخطاء فقط) ═══
        debugScroll = new ScrollView(this);
        debugScroll.setBackgroundColor(Color.parseColor("#CC000000"));
        debugScroll.setVisibility(android.view.View.GONE);

        debugView = new TextView(this);
        debugView.setTextColor(Color.parseColor("#FF6B6B"));
        debugView.setTextSize(10);
        debugView.setPadding(20, 60, 20, 20);
        debugView.setGravity(Gravity.START);
        debugView.setTypeface(android.graphics.Typeface.MONOSPACE);
        debugScroll.addView(debugView);

        FrameLayout.LayoutParams dbgLp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        root.addView(debugScroll, dbgLp);

        // ═══ زر Debug (شفاف فوق يسار) ═══
        TextView debugBtn = new TextView(this);
        debugBtn.setText("🐛");
        debugBtn.setTextSize(20);
        debugBtn.setPadding(20, 60, 20, 20);
        FrameLayout.LayoutParams btnLp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        btnLp.gravity = Gravity.TOP | Gravity.START;
        debugBtn.setLayoutParams(btnLp);
        debugBtn.setOnClickListener(v -> {
            if (debugScroll.getVisibility() == android.view.View.VISIBLE) {
                debugScroll.setVisibility(android.view.View.GONE);
            } else {
                debugScroll.setVisibility(android.view.View.VISIBLE);
            }
        });
        root.addView(debugBtn);

        setContentView(root);
    }

    private void appendDebug(String line) {
        runOnUiThread(() -> {
            logBuffer.append(line).append("\n");
            if (logBuffer.length() > 2000) {
                logBuffer = new StringBuilder(logBuffer.substring(logBuffer.length() - 2000));
            }
            if (debugView != null) {
                debugView.setText(logBuffer.toString());
                debugScroll.post(() -> debugScroll.fullScroll(android.view.View.FOCUS_DOWN));
            }
        });
    }

    @Override
    public void onBackPressed() {
        webView.evaluateJavascript("onBackPressed()", value -> {
            if ("false".equals(value)) finish();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) webView.evaluateJavascript("onGameSave()", null);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.evaluateJavascript("onGameSave()", null);
            webView.destroy();
        }
        super.onDestroy();
    }

    public class AndroidBridge {
        @JavascriptInterface
        public void saveGame(String json) {
            prefs.edit().putString("save_data", json).apply();
        }

        @JavascriptInterface
        public String loadGame() {
            return prefs.getString("save_data", "");
        }

        @JavascriptInterface
        public void onReady() {
            appendDebug("✅ Game ready");
            runOnUiThread(() -> Toast.makeText(GameActivity.this,
                    "🎮 اللعبة جاهزة!", Toast.LENGTH_SHORT).show());
        }

        @JavascriptInterface
        public void showToast(String msg) {
            appendDebug("TOAST: " + msg);
            runOnUiThread(() -> Toast.makeText(GameActivity.this, msg, Toast.LENGTH_SHORT).show());
        }

        @JavascriptInterface
        public void vibrate() {
            runOnUiThread(() -> {
                android.os.Vibrator v = (android.os.Vibrator) getSystemService(VIBRATOR_SERVICE);
                if (v != null) v.vibrate(30);
            });
        }

        @JavascriptInterface
        public void logError(String msg) {
            appendDebug("❌ " + msg);
        }
    }
}
