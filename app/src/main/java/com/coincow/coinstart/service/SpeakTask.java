package com.coincow.coinstart.service;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.speech.tts.TextToSpeech;

import com.coincow.coinstart.MyApplication;

import java.util.Locale;

/**
 * Created by zhouyangzzu on 2017/8/25.
 */

public class SpeakTask extends HandlerThread{

    public static void speak(String text){
        getInstance().post2Speak(text);
    }

    private SpeakTask() {
        super("SpeakTask");
    }

    private Handler mHandler = null;
    private static SpeakTask mThread = null;
    private static SpeakTask getInstance(){
        if (mThread == null){
            mThread = new SpeakTask();
            mThread.start();
        }
        return mThread;
    }

    private void post2Speak(String text){
        if (mHandler == null){
            mHandler = new Handler(getLooper()){
                @Override
                public void handleMessage(Message msg) {
                    String text = (String)msg.obj;
                    speakIndeed(text);
                }
            };
        }

        Message message = mHandler.obtainMessage(1, text);
        mHandler.sendMessage(message);
    }


    private TextToSpeech textToSpeech = null;
    private void speakIndeed(String text){
        try{
            if (null == textToSpeech){
                textToSpeech = new TextToSpeech(MyApplication.getContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        textToSpeech.setLanguage(Locale.ENGLISH);
                        textToSpeech.setSpeechRate(0.7f);
                    }
                });
            }else{
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
