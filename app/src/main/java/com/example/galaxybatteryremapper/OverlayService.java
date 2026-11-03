package com.example.galaxybatteryremapper;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.*;
import android.view.*;
import android.widget.TextView;

public class OverlayService extends Service {
    private WindowManager wm;
    private TextView label;
    private BroadcastReceiver batteryReceiver;
    private static final String CHANNEL = "battery_remapper";

    public static int remap(int original) {
        return Math.max(0, Math.min(100, Math.round((original - 20) * 100f / 80f)));
    }

    @Override public void onCreate() {
        super.onCreate();
        createChannel();
        Notification n = new Notification.Builder(this, CHANNEL)
                .setContentTitle("电量映射正在运行")
                .setContentText("正在显示重映射后的电量")
                .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
                .setOngoing(true).build();
        startForeground(1001, n);

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        label = new TextView(this);
        label.setTextColor(Color.WHITE);
        label.setTextSize(12);
        label.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        label.setShadowLayer(2f, 0f, 1f, Color.BLACK);
        label.setPadding(5, 1, 5, 1);
        label.setBackgroundColor(Color.TRANSPARENT);
        label.setGravity(Gravity.CENTER);

        WindowManager.LayoutParams p = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        p.gravity = Gravity.TOP | Gravity.RIGHT;
        p.x = dp(10);
        // Place immediately below the status bar; normal apps cannot draw over SystemUI.
        p.y = statusBarHeight() + dp(1);
        wm.addView(label, p);

        batteryReceiver = new BroadcastReceiver() {
            @Override public void onReceive(Context c, Intent i) {
                int level = i.getIntExtra("level", 0);
                int scale = i.getIntExtra("scale", 100);
                int pct = scale > 0 ? Math.round(level * 100f / scale) : level;
                label.setText(remap(pct) + "%");
            }
        };
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    private int statusBarHeight() {
        int id = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return id > 0 ? getResources().getDimensionPixelSize(id) : dp(24);
    }
    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(CHANNEL, "电量映射", NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
        }
    }
    @Override public int onStartCommand(Intent i, int flags, int startId) { return START_STICKY; }
    @Override public void onDestroy() {
        if (batteryReceiver != null) unregisterReceiver(batteryReceiver);
        if (label != null && wm != null) wm.removeView(label);
        super.onDestroy();
    }
    @Override public IBinder onBind(Intent i) { return null; }
}
