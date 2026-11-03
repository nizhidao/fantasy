package com.example.galaxybatteryremapper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(28, 36, 28, 28);

        TextView title = new TextView(this);
        title.setText("Galaxy Battery Remapper\n\n将原始 20% 映射为 0%，100% 保持 100%。\n\n注意：普通应用不能修改三星 SystemUI。此工具在状态栏下方显示模拟百分比。");
        title.setTextSize(17);
        layout.addView(title);

        Button permission = new Button(this);
        permission.setText("① 授予悬浮窗权限");
        permission.setOnClickListener(v -> {
            Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(i);
        });
        layout.addView(permission);

        Button start = new Button(this);
        start.setText("② 启动电量映射显示");
        start.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                title.setText("请先授予悬浮窗权限，再点击启动。");
                return;
            }
            Intent i = new Intent(this, OverlayService.class);
            startForegroundService(i);
            title.setText("映射显示已启动。可返回桌面查看。");
        });
        layout.addView(start);

        Button stop = new Button(this);
        stop.setText("停止显示");
        stop.setOnClickListener(v -> stopService(new Intent(this, OverlayService.class)));
        layout.addView(stop);

        TextView note = new TextView(this);
        note.setText("\n请手动关闭三星原生“显示电池百分比”，保留电池图标。悬浮数字会出现在状态栏正下方，不会真正替换 SystemUI。");
        note.setTextSize(14);
        layout.addView(note);
        setContentView(layout);
    }
}
