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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by zhouyangzzu on 2017/8/19.
 */

public class AlarmService extends Service {

    private final long mTimeStart = System.currentTimeMillis();

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
        setSelfForegroud();
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

        String timeExtra = calculateTime(mTimeStart);

        Bitmap largeIcon = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap();
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder notifyBuilder = new Notification.Builder(AlarmService.this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(largeIcon)
                .setTicker("正在帮你监视币价波动。。。")
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getString(R.string.app_name))
                .setContentText("正在监视币价波动。。。"+timeExtra)
                .setSubText("牛币")
                .setAutoCancel(false)
                .setShowWhen(true)
                .setContentIntent(pendingIntent);


        Notification notification = notifyBuilder.build();
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE;
        notification.defaults = 0;

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


    /**
     * 由过去的某一时间,计算距离当前的时间
     * */
    public String calculateTime(long time){
        long nowTime=System.currentTimeMillis();  //获取当前时间的毫秒数
        String msg = "刚启动";

        Date setTime = new Date(time);  //指定时间

        long reset=setTime.getTime();   //获取指定时间的毫秒数
        long dateDiff=nowTime-reset;

        if(dateDiff<0){
            msg="输入的时间不对";
        }else{

            long dateTemp1=dateDiff/1000; //秒
            long dateTemp2=dateTemp1/60; //分钟
            long dateTemp3=dateTemp2/60; //小时
            long dateTemp4=dateTemp3/24; //天数
            long dateTemp5=dateTemp4/30; //月数
            long dateTemp6=dateTemp5/12; //年数

            if(dateTemp6>0){
                msg = dateTemp6+"年前";

            }else if(dateTemp5>0){
                msg = dateTemp5+"个月前";

            }else if(dateTemp4>0){
                msg = dateTemp4+"天前";

            }else if(dateTemp3>0){
                msg = dateTemp3+"小时前";

            }else if(dateTemp2>0){
                msg = dateTemp2+"分钟前";

            }else if(dateTemp1>0){
                msg = "刚刚";

            }
        }
        return msg;

    }

}
