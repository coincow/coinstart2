package com.coincow.coinstart;

import android.app.Application;
import android.content.Context;

import com.coincow.coinstart.service.AlarmService;
import com.coincow.coinstart.service.PersistService;
import com.tencent.bugly.crashreport.CrashReport;


/**
 * Created by zhouyangzzu on 2017/8/19.
 */

public class MyApplication extends Application {

    static Context mContext = null;

    @Override
    public void onCreate() {
        mContext = this;
        super.onCreate();
        initApp();

        CrashReport.initCrashReport(getApplicationContext(), "b256ceacc6", false);
    }

    private void initApp(){
        AlarmService.checService(this);
        PersistService.checkService(this);
    }

    public static Context getContext(){
        return mContext;
    }
}
