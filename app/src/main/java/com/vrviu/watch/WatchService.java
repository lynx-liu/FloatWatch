package com.vrviu.watch;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WatchService extends Service {

    private Binder mBinder = null;
    private FloatWindow floatWindow = null;
    private static final int MSG_UPDATE_TIME = 0x01;

    @Override
    public void onCreate() {
        super.onCreate();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			NotificationChannel channel = new NotificationChannel(getPackageName(), getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
			notificationManager.createNotificationChannel(channel);
			Notification notification = new Notification.Builder(getApplicationContext(), getPackageName()).build();
			startForeground(1, notification);
		}

        mBinder = new Binder();
        if(floatWindow==null) {
            try {
                floatWindow = new FloatWindow(WatchService.this);   //new出一个悬浮窗类
                floatWindow.createWindowManger();                   //创建一个窗口类
                floatWindow.createDesktopLayout();                  //创建布局参数
                floatWindow.showDesk();
                mhandler.sendEmptyMessage(MSG_UPDATE_TIME);
            } catch (Exception e) {
                Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        if(floatWindow==null) {
            try {
                floatWindow = new FloatWindow(WatchService.this);   //new出一个悬浮窗类
                floatWindow.createWindowManger();                   //创建一个窗口类
                floatWindow.createDesktopLayout();                  //创建布局参数
                floatWindow.showDesk();
                mhandler.sendEmptyMessage(MSG_UPDATE_TIME);
            } catch (Exception e) {
                Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
            }
        }
        return mBinder;
    }

    //消息处理 改变现实内容
    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_UPDATE_TIME:
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss SSS");
                    floatWindow.mDesktopLayout.setTextTime(sdf.format(new Date()));
                    floatWindow.mWindowManager.updateViewLayout(floatWindow.mDesktopLayout,floatWindow.mLayout);
                    mhandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME,5);
                    break;
            }
        }
    };
}