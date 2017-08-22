package com.coincow.coinstart.myservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by zhouyangzzu on 2017/8/19.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //check service
        AlarmService.checService(context);
        PersistService.checkService(context);
    }

}
