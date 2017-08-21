package com.coincow.coinstart;

import android.app.Application;
import android.content.Intent;

import com.coincow.coinstart.myservice.FetchRealTimeDataService;


/**
 * Created by zhouyangzzu on 2017/8/19.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initApp();
    }

    private void initApp(){
        Intent intent = new Intent(this, FetchRealTimeDataService.class);
        intent.setAction("blabla");
        startService(intent);
    }
}
