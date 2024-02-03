package com.replicator;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;

public class FloatingWindowManager extends Service {
    private Context context;
    private WindowManager windowManager;
    private View floatingView, click;
    private WindowManager.LayoutParams params;
    boolean clickedstatus = false;
    public static boolean isExit = false;
    Instrumentation instrumentation = null;
    View view = null;
    public static boolean isback=false;

    public FloatingWindowManager(Context context) {
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public FloatingWindowManager() {

    }

   public  Handler mHandler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    public void onDestroy() {
        MainActivity.print("destroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        view = LayoutInflater.from(MainActivity.mainActivity).inflate(R.layout.floatingwin, null);
        view.setFocusableInTouchMode(true);
         boolean isfocus= view.requestFocus();
//        MainActivity.print((String) isfocus);

        System.out.println("============="+isfocus);

//        view.setOnKeyListener(new View.OnKeyListener() {
//
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                MainActivity.print("999");
//                if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
//                    // 处理音量减小事件
//                    // 在这里执行你的逻辑
//                    if (!isExit) {
//                        isExit = true;
//
//                        // 利用handler延迟发送更改状态信息，2000==2秒
//                        mHandler.sendEmptyMessageDelayed(0, 2000);
//                        return true;
//                    } else {
////                finish();
//                        Intent intentOne = new Intent(MainActivity.mainActivity, FloatingWindowManager.class);
//                        stopService(intentOne);
//                        onDestroy();
//
//                    }
//                }
//                MainActivity.print(view.toString());
//                return false;
//            }
//
//        });





        return super.onStartCommand(intent, flags, startId);
    }

    public void showFloatingWindow() {
        if (floatingView == null) {
            MainActivity.print("float");
            // 使用LayoutInflater从布局文件中创建悬浮窗视图
            floatingView = LayoutInflater.from(context).inflate(R.layout.floatingwin, null);

            // 设置悬浮窗的参数
            params = new WindowManager.LayoutParams(
                    1,
                    1,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // 悬浮窗类型（需要SYSTEM_ALERT_WINDOW权限）
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // 不获取焦点
                    PixelFormat.TRANSLUCENT); // 透明窗口

            // 设置悬浮窗的对齐方式和初始位置
            params.gravity = Gravity.START | Gravity.TOP;
            params.x = 0;
            params.y = 0;

            // 将悬浮窗视图添加到窗口管理器中
            windowManager.addView(floatingView, params);
        }
    }

    @SuppressLint("ResourceType")
    public void showConsecutiveClick() {
        if (click == null) {
            // 使用LayoutInflater从布局文件中创建悬浮窗视图
            click = LayoutInflater.from(context).inflate(R.layout.countinue_click, null);


            int x = context.getResources().getDisplayMetrics().widthPixels / 2;
            int y = context.getResources().getDisplayMetrics().heightPixels / 2;
            Button start = click.findViewById(R.id.start);
            Button init = click.findViewById(R.id.init);
            Button stop = click.findViewById(R.id.stop);
//
            start.setOnClickListener(view -> {
                Log.d("==========start", "start");
            });
//            stop.setOnClickListener(view -> {simulateScreenClick(false,context,x,y);});


            // 设置悬浮窗的参数
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,  // 宽高wrap_content使内容适应窗口大小
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // 悬浮窗类型（需要SYSTEM_ALERT_WINDOW权限）
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT); // 透明窗口


            // 设置悬浮窗的对齐方式和初始位置
            params.gravity = Gravity.START;
            params.x = 0;
            params.y = 0;

            // 将悬浮窗视图添加到窗口管理器中
            windowManager.addView(click, params);

        }
    }


    public void hideFloatingWindow() {
        if (floatingView != null) {
            // 从窗口管理器中移除悬浮窗视图
            windowManager.removeView(floatingView);
            floatingView = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public static boolean isback(boolean isback){
        return FloatingWindowManager.isback=isback;
    }




   public static class VolumeKeyListener extends BroadcastReceiver {

       public VolumeKeyListener(){}
        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity.print("5453");
            if (intent.getAction() != null && intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
                int keyCode = intent.getIntExtra("android.media.EXTRA_VOLUME_KEY", KeyEvent.KEYCODE_UNKNOWN);
                if (keyCode != KeyEvent.KEYCODE_UNKNOWN) {
                    // 处理音量键事件
                    if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

                        MainActivity.print("459438754");
                        if (!FloatingWindowManager.isExit) {
                            FloatingWindowManager.isExit = true;

                            // 利用handler延迟发送更改状态信息，2000==2秒
                            new FloatingWindowManager(). mHandler.sendEmptyMessageDelayed(0, 2000);

                        } else {
//                finish();
                            FloatingWindowManager.isback(true);

                        }

                    }
                }
            }
        }


    }

}
