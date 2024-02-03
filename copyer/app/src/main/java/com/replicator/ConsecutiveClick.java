package com.replicator;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Service;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class ConsecutiveClick extends AccessibilityService  {

    private Handler handler = new Handler(Looper.getMainLooper());
    private volatile boolean  stopClick = false;

    int clicktime=0;
    Path path =new Path();

    @Override
    public synchronized void onAccessibilityEvent(AccessibilityEvent event) {
// 监听需要的界面事件，根据实际情况进行修改
        // 这里以“按钮”为例，你可能需要根据你的应用界面的实际情况进行修改
        initposition();




        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            AccessibilityNodeInfo nodeInfo = event.getSource();

            if (nodeInfo != null) {
                // 处理点击事件
                if (nodeInfo.getClassName().equals("android.widget.Button")){
                    if ((MainActivity.mainActivity.getResources().getIdentifier(nodeInfo.getViewIdResourceName(),"id",getPackageName())) ==(R.id.stop)) {



                        new Thread(new Runnable() {
                            @Override
                            public synchronized void run() {
                                stopClick=true;
                            }
                        }).start();

                    }else if ((MainActivity.mainActivity.getResources().getIdentifier(nodeInfo.getViewIdResourceName(),"id",getPackageName())) ==(R.id.start)) {
                        stopClick=false;

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                performConsecutiveClick(nodeInfo);
                            }
                        }).start();


                    }else if((MainActivity.mainActivity.getResources().getIdentifier(nodeInfo.getViewIdResourceName(),"id",getPackageName())) ==(R.id.init)) {

                        initposition();
                    }
                nodeInfo.recycle();
            }
        }
    }
    }

    @Override
    public void onInterrupt() {
        Log.e("======AccessibilityService", "Service interrupted");
    }
    private synchronized void  performConsecutiveClick(AccessibilityNodeInfo nodeInfo) {

        int x=MainActivity.mainActivity.getResources().getDisplayMetrics().widthPixels/2;
        int y=MainActivity.mainActivity.getResources().getDisplayMetrics().heightPixels/2;
        // 使用 Handler 进行延迟执行，模拟连续点击

//        if(clicktime>=100) {
//            try {
//                Thread.sleep(3000);
//                clicktime=0;
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        if(clicktime>=100){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {


                }
            },240*1000);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                // 在这里执行你的点击操作
                // 这里使用 GestureDescription 模拟点击，可以根据实际情况修改


                GestureDescription.Builder builder = new GestureDescription.Builder();
//                path= new Path();
                path.moveTo(x,y);
                builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 100));
                dispatchGesture(builder.build(), null, null);
                clicktime++;
                // 继续模拟下一次点击
                if (!stopClick) {
                    // 继续模拟下一次点击
                    performConsecutiveClick(nodeInfo);
                }


                System.out.println("=====click");
//                performContinuousClick(nodeInfo);

            }
        }, 100); // 1000毫秒延迟，可以根据实际需求调整
    }
    public void initposition(){
        int x=MainActivity.mainActivity.getResources().getDisplayMetrics().widthPixels/2;
        int y=MainActivity.mainActivity.getResources().getDisplayMetrics().heightPixels/2;
        path.moveTo(x,y);
    }
}
