package com.coincow.coinstart.myservice;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;

/**
 * Created by zhouyangzzu on 2017/8/22.
 */

public class PersistService extends JobService {

    static TextToSpeech textToSpeech = null;
    static int count = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scheduleJob();
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        scheduleJob();
        doJob();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private void scheduleJob() {

        try {
            int id = 1;
            JobInfo.Builder builder = new JobInfo.Builder(id, new ComponentName(getPackageName(), PersistService.class.getName() ));
            builder.setPeriodic(15000);  //60s执行一次
            JobScheduler jobScheduler = (JobScheduler)this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.cancel(id);
            int ret = jobScheduler.schedule(builder.build());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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