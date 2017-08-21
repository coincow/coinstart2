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

    static TextToSpeech textToSpeech = null;
    static int count = 0;


    @Override
    public void onReceive(Context context, Intent intent) {

        //启动下一次任务
        FetchRealTimeDataService.addTask(context);

        try{
            if (null == textToSpeech){
                textToSpeech = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        textToSpeech.setLanguage(Locale.ENGLISH);
                    }
                });
            }else{
                count++;
                textToSpeech.speak(""+count, TextToSpeech.QUEUE_FLUSH, null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //check service
        Intent intent2 = new Intent(context, FetchRealTimeDataService.class);
        intent.setAction("blabla");
        context.startService(intent2);
    }



}
