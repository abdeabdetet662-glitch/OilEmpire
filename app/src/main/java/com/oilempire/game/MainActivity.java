package com.oilempire.game;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String TAG = "OilEmpire_Main";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        Log.d(TAG, "MainActivity started");

        try {
            FrameLayout root = new FrameLayout(this);
            root.setBackgroundColor(Color.parseColor("#0A0A0A"));

            LinearLayout center = new LinearLayout(this);
            center.setOrientation(LinearLayout.VERTICAL);
            center.setGravity(Gravity.CENTER);
            FrameLayout.LayoutParams cp = new FrameLayout.LayoutParams(-1, -1);
            center.setLayoutParams(cp);

            TextView icon = new TextView(this);
            icon.setText("\uD83D\uDED2");
            icon.setTextSize(100);
            icon.setGravity(Gravity.CENTER);
            center.addView(icon);

            TextView title = new TextView(this);
            title.setText("\u0628\u062A\u0631\u0648\u0644 \u0623\u064F\u0645\u0651\u0629");
            title.setTextColor(Color.parseColor("#FF6B35"));
            title.setTextSize(38);
            title.setTypeface(null, Typeface.BOLD);
            title.setGravity(Gravity.CENTER);
            title.setPadding(0, 30, 0, 10);
            center.addView(title);

            TextView sub = new TextView(this);
            sub.setText("\u0625\u0645\u0628\u0631\u0627\u0637\u0648\u0631\u064A\u0629 \u0627\u0644\u0646\u0641\u0637 \u0627\u0644\u0639\u0631\u0628\u064A\u0629");
            sub.setTextColor(Color.parseColor("#9E9E9E"));
            sub.setTextSize(14);
            sub.setGravity(Gravity.CENTER);
            center.addView(sub);

            root.addView(center);
            setContentView(root);

            Log.d(TAG, "UI ready, will start GameActivity in 3s");

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    Log.d(TAG, "Starting GameActivity");
                    startActivity(new Intent(this, GameActivity.class));
                    finish();
                } catch (Throwable t) {
                    Log.e(TAG, "GameActivity failed", t);
                    Toast.makeText(this, "GameActivity: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }, 3000);

        } catch (Throwable t) {
            Log.e(TAG, "MainActivity crash", t);
            Toast.makeText(this, "Main: " + t.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
