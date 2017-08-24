package com.coincow.coinstart.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.os.SystemClock;

import com.coincow.coinstart.MainActivity;
import com.coincow.coinstart.R;


/**
 * Created by zhouyangzzu on 2017/8/19.
 */

public class AlarmService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        setSelfForegroud();
        addTask(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    static public void addTask(Context context){

        long triggerAtTime = SystemClock.elapsedRealtime() + 4*60*1000;
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //间隔时间不准，不能精确到1s
        //manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, interval, pendingIntent);

        //换单次alarm，试试保活效果
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
    }


    private void setSelfForegroud(){

        Bitmap largeIcon = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap();
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder notifyBuilder = new Notification.Builder(AlarmService.this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(largeIcon)
                .setTicker("正在帮你监视币价波动。。。")
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getString(R.string.app_name))
                .setContentText("正在帮你监视币价波动。。。")
                .setSubText("牛币")
                .setAutoCancel(false)
                .setShowWhen(true)
                .setContentIntent(pendingIntent);


        Notification notification = notifyBuilder.build();
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE;
        notification.defaults = Notification.DEFAULT_ALL;

//        Notification notification1 = new Notification.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(largeIcon)
//                .setContentTitle("牛币")
//                .setSubText("ooooo")
//                .setTicker("正在帮你监视币价波动。。。")
//                .setContentTitle("正在帮你监视币价波动。。。")
//                .setContentIntent(pendingIntent)
//                .getNotification();
        startForeground(1111, notification);
    }

    static public void checService(Context context){
        Intent intent = new Intent(context, AlarmService.class);
        intent.setAction("blabla");
        context.startService(intent);
    }

}
