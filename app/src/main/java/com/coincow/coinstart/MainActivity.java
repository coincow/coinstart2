package com.coincow.coinstart;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.coincow.coinstart.service.SpeakTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private final static String LOGTAG = "log";
    private final static String CoinIdsKey = "CoinIds1";
    private final static String InterestCoinIds = "gxscny,btccny,bcccny,ethcny,eoscny,omgcny,anscny";
    private static HashSet<String> mCoinIdsSet = new HashSet<>();

    private RecyclerView mRecyclerView;
    private CoinAdapter mCoinAdapter;
    private List<Coin> mCoinDatas;

    private PriceMonitor mPriceMonitor = new PriceMonitor();
    private OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化数据
        initDatas();

        mRecyclerView = (RecyclerView)findViewById(R.id.coin_view);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mCoinAdapter = new CoinAdapter(this, mCoinDatas);
        mRecyclerView.setAdapter(mCoinAdapter);

        Timer timer = new Timer("RefreshCoins");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshCoins();
            }
        }, 0, 1000); //必须每1秒请求一次数据，否则PriceMonitor工作不准确
    }

    private void initDatas()
    {
        mCoinDatas = new ArrayList<Coin>();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String idsStr = sharedPref.getString(CoinIdsKey, InterestCoinIds);
        String[] ids = idsStr.split(",");
        mCoinIdsSet.clear();
        for(String id : ids){
            mCoinIdsSet.add(id);
            Coin coin = new Coin();
            coin.name = idToName(id);
            mCoinDatas.add(coin);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        saveCoinsToPreferences();
    }

    private void saveCoinsToPreferences()
    {
        String ids = "";
        for(String id : mCoinIdsSet)
        {
            ids += id;
            ids += ",";
        }

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(CoinIdsKey, ids);
        editor.commit();
    }

    public class Coin
    {
        public Long at;
        public String buy;
        public String sell;
        public String low;
        public String high;
        public String last;
        public String vol;

        public String name;
    }

    private String idToName(String id)
    {
        String name = id.substring(0, id.length()-3);
        return name.toUpperCase();
    }

    public TreeMap<String, Coin> yunbiResponseToCoins(String response)
    {
        TreeMap<String, Coin> coinMap = new TreeMap<>();
        try
        {
            JSONObject jsonObject = new JSONObject(response);

            for(String id : mCoinIdsSet)
            {
                JSONObject coinJsonObject = jsonObject.getJSONObject(id);
                Coin coin = new Coin();

                coin.at = coinJsonObject.getLong("at");

                JSONObject tickerJsonObject = coinJsonObject.getJSONObject("ticker");
                coin.buy = tickerJsonObject.getString("buy");
                coin.sell = tickerJsonObject.getString("sell");
                coin.low = tickerJsonObject.getString("low");
                coin.high = tickerJsonObject.getString("high");
                coin.last = tickerJsonObject.getString("last");
                coin.vol = tickerJsonObject.getString("vol");

                coin.name = idToName(id);

                coinMap.put(id, coin);
                //存储30秒价格数据，用于判断拉升
                if(mPriceMonitor.countCoinInfo(id, coin))
                {
                    //notify
                    String text = coin.name;
                    text += getResources().getString(R.string.price_raise);
                    sendNotifation(123, coin.name, text);

                    //speak
                    SpeakTask.speak(coin.name);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return coinMap;
    }

    public void queryYunbiCoins()
    {
        String url = "https://yunbi.com//api/v2/tickers.json";


        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call,  Response response) throws IOException {
                if (null != response.cacheResponse()) {
                    String str = response.cacheResponse().toString();
                    //Log.i(LOGTAG, "cache---" + str);
                } else {

                    final String str = response.body().string();//response.networkResponse().toString();
                    //Log.i(LOGTAG, "network---" + str);

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateStockListView(yunbiResponseToCoins(str));
                        }
                    });

                }
            }
        });
    }

    public void queryYunbiMarkets()
    {
        String url = "https://yunbi.com/";
        String shortApi = "/api/v2/trades.json";
        String param = "market=gxbcny";

        String sigUrl = YunbiApiHelper.getSignatureUrl(url, shortApi, param);

        Request request = new Request.Builder()
                .url(sigUrl)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call,  Response response) throws IOException {
                if (null != response.cacheResponse()) {

                } else {

                    final String str = response.body().string();//response.networkResponse().toString();
                    //Log.i(LOGTAG, "trades---" + str);

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //updateStockListView(yunbiResponseToCoins(str));
                        }
                    });

                }
            }
        });
    }

    private void refreshCoins()
    {
        queryYunbiCoins();
        //queryYunbiMarkets();
    }

    public void updateStockListView(TreeMap<String, Coin> coinMap)
    {
        mCoinDatas.clear();
        Collection<Coin> coins = coinMap.values();
        for(Coin coin : coins)
        {
            mCoinDatas.add(coin);
        }
        mCoinAdapter.notifyDataSetChanged();
    }

    public void sendNotifation(int id, String title, String text){
        NotificationCompat.Builder nBuilder =
                new NotificationCompat.Builder(this);
        nBuilder.setSmallIcon(R.mipmap.ic_launcher);
        nBuilder.setContentTitle(title);
        nBuilder.setContentText(text);
        nBuilder.setVibrate(new long[]{100, 100, 100});
        nBuilder.setLights(Color.RED, 1000, 1000);
        nBuilder.setDefaults(Notification.DEFAULT_SOUND);


        NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyMgr.notify(id, nBuilder.build());
    }
}
