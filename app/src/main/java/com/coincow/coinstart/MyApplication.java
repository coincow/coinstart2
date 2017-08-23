package com.coincow.coinstart;

import android.app.Application;
import android.content.Context;

import com.coincow.coinstart.myservice.AlarmService;
import com.coincow.coinstart.myservice.PersistService;


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
    }

    private void initApp(){
        AlarmService.checService(this);
        PersistService.checkService(this);
    }

    public static Context getContext(){
        return mContext;
    }
}
