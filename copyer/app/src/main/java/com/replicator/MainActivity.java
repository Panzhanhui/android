package com.replicator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;

public class MainActivity extends AppCompatActivity {
    private int notificationId = 1;
    public static MainActivity mainActivity = null;
    private static boolean isExit = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, 1);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + this.getPackageName()));
            this.startActivity(intent);
        }

        if (!isAccessibilityServiceEnabled()) {
            // 如果未启用，跳转到无障碍设置界面
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (FloatingWindowManager.isback) onBackPressed();
                }

            }
        }).start();


//        registerInnerReceiver(this);
    }

//    public void registerInnerReceiver(Context context) {
//        FloatingWindowManager.VolumeKeyListener innerReceiver = new FloatingWindowManager.VolumeKeyListener();
//        IntentFilter intentFilter = new IntentFilter("com.replicator.BroadcastReceiver");
//        context.registerReceiver(innerReceiver, intentFilter);
//    }

    Handler mHandler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    public static void print(String string) {
        System.out.println("=========" + string);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//    print("666");
//        if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN)dispatchKeyEvent(event);
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        print("777");
        if (action == KeyEvent.ACTION_DOWN) {
            if (!isExit) {
                isExit = true;

                // 利用handler延迟发送更改状态信息，2000==2秒
                mHandler.sendEmptyMessageDelayed(0, 2000);
            } else {
//                finish();
                onDestroy();
            }
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    private boolean isAccessibilityServiceEnabled() {
        String serviceName = "com.replicator/.ConsecutiveClick"; // 替换成你的无障碍服务的完整路径

        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED
            );
        } catch (Settings.SettingNotFoundException e) {
            // 当找不到 ACCESSIBILITY_ENABLED 设置时的处理
        }

        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            );
            if (settingValue != null) {
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessibilityService = splitter.next();
                    if (accessibilityService.equalsIgnoreCase(serviceName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void registerActivityLifecycleCallbacks(@NonNull Application.ActivityLifecycleCallbacks callback) {
        super.registerActivityLifecycleCallbacks(callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStop() {
        super.onStop();
        sendNotification();
        FloatingWindowManager floatingWindowManager = new FloatingWindowManager(this);
        floatingWindowManager.showFloatingWindow();
        floatingWindowManager.showConsecutiveClick();
//        TcpServer serverTask = new TcpServer();
//        serverTask.execute();
        //连续启动Service
        Intent intentOne = new Intent(this, FloatingWindowManager.class);
        startService(intentOne);
    }

    // 在需要发送通知的地方调用
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification() {


        String channelId = "1"; // 通知渠道 ID，对应上面创建的通知渠道
        //创建通知渠道
        NotificationChannel notificationChannel = new NotificationChannel(channelId, "hello", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notification = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        notification.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher) // 设置通知图标
                .setContentTitle("Notification Title") // 设置通知标题
                .setContentText("Notification Text") // 设置通知内容
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); // 设置通知优先级

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.createNotificationChannel(builder.build());
        notificationManager.notify(notificationId, builder.build());
        System.out.println("==========notice=");
//        notificationManager.createNotificationChannel(builder);
        notificationId++;
    }
}
