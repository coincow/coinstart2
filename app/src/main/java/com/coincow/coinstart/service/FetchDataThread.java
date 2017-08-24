package com.coincow.coinstart.service;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import com.coincow.coinstart.MyApplication;
import java.util.Locale;

/**
 * Created by zhouyangzzu on 2017/8/22.
 */

public class FetchDataThread {

    private static FetchDataThread mFetchDataThread = null;
    static TextToSpeech textToSpeech = null;
    static int count = 0;
    private  WorkerThread mWorkerThread = null;

    private Context mContext = null;


    private FetchDataThread() {
        mContext = MyApplication.getContext();
        mWorkerThread = new WorkerThread();
        mWorkerThread.start();
    }

    static FetchDataThread getInstance(){

        if (mFetchDataThread == null){
            mFetchDataThread = new FetchDataThread();
        }
        return mFetchDataThread;
    }


    class WorkerThread extends Thread{

        public WorkerThread() {
            super("FetchDataThread");
        }

        @Override
        public void run() {

            for(;;){
                //doWork();
                try {
                    Thread.sleep(5000);
                }catch (Exception e) {
                }
            }

        }
    }

    private void doWork(){

        try{
            if (null == textToSpeech){
                textToSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
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
    }

}
