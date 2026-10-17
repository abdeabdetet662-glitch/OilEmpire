package com.oilempire.game;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.parseColor("#0A0A0A"));

        LinearLayout center = new LinearLayout(this);
        center.setOrientation(LinearLayout.VERTICAL);
        center.setGravity(Gravity.CENTER);

        FrameLayout.LayoutParams cp = new FrameLayout.LayoutParams(-1, -1);
        center.setLayoutParams(cp);

        // 🛢️
        TextView icon = new TextView(this);
        icon.setText("🛢️");
        icon.setTextSize(100);
        icon.setGravity(Gravity.CENTER);
        icon.setAlpha(0f);
        icon.animate().alpha(1f).setDuration(1000).start();
        center.addView(icon);

        // اسم اللعبة
        TextView title = new TextView(this);
        title.setText("بترول أُمّة");
        title.setTextColor(Color.parseColor("#FF6B35"));
        title.setTextSize(38);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 30, 0, 10);
        title.setAlpha(0f);
        title.animate().alpha(1f).setDuration(1000).setStartDelay(500).start();
        center.addView(title);

        // Subtitle
        TextView sub = new TextView(this);
        sub.setText("إمبراطورية النفط العربية");
        sub.setTextColor(Color.parseColor("#9E9E9E"));
        sub.setTextSize(14);
        sub.setGravity(Gravity.CENTER);
        sub.setAlpha(0f);
        sub.animate().alpha(1f).setDuration(1000).setStartDelay(1000).start();
        center.addView(sub);

        // Loading
        TextView loading = new TextView(this);
        loading.setText("⏳ جاري التحميل...");
        loading.setTextColor(Color.parseColor("#666666"));
        loading.setTextSize(11);
        loading.setGravity(Gravity.CENTER);
        loading.setPadding(0, 80, 0, 0);
        loading.setAlpha(0f);
        loading.animate().alpha(1f).setDuration(1000).setStartDelay(1500).start();
        center.addView(loading);

        root.addView(center);
        setContentView(root);

        // بعد 3 ثواني → GameActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, GameActivity.class));
            finish();
        }, 3000);
    }
}
