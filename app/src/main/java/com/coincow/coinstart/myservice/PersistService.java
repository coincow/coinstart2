package com.coincow.coinstart.myservice;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhouyangzzu on 2017/8/22.
 */

public class PersistService extends JobService {

    static TextToSpeech textToSpeech = null;
    static int count = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        scheduleJob();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        scheduleJob();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private void scheduleJob() {

        try {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(this, PersistService.class));  //指定哪个JobService执行操作
            builder.setMinimumLatency(TimeUnit.MILLISECONDS.toMillis(2*60*1000)); //执行的最小延迟时间
            builder.setOverrideDeadline(TimeUnit.MILLISECONDS.toMillis(10*60*1000));  //执行的最长延时时间
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NOT_ROAMING);  //非漫游网络状态
            builder.setBackoffCriteria(TimeUnit.MINUTES.toMillis(10), JobInfo.BACKOFF_POLICY_LINEAR);  //线性重试方案
            builder.setRequiresCharging(false); // 未充电状态
            jobScheduler.schedule(builder.build());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        doJob();
    }

    private void doJob(){
        AlarmService.checService(this);
        PersistService.checkService(this);
    }

    static public void checkService(Context context){
        Intent intent1 = new Intent(context, PersistService.class);
        intent1.setAction("blabla");
        context.startService(intent1);

        FetchDataThread.getInstance();
    }

}