package com.coincow.coinstart;

import java.util.LinkedList;
import java.util.TreeMap;

/**
 * Created by Administrator on 2017/8/23.
 */

public class PriceMonitor {

    //最大存储10分钟也就是10根柱子的数据，当第10根柱子填充进来后，pop出前面5根柱子的数据
    private TreeMap<String, LinkedList<CoinInfo>> mPriceMap = new TreeMap<>();

    public class CoinInfo
    {
        public long index; //第几分钟，也就是第几根柱子
        public int second; //第几秒，一共需要统计60秒的数据
        public double volumeOneMin; //一分钟的量能
        public double maxOneMin; //一分钟最低价
        public double minOneMin; //一分钟最高价
        public double lastOneMin; //一分钟定价
    }

    public boolean countCoinInfo(String coinId, MainActivity.Coin coin)
    {
        LinkedList<CoinInfo> coinInfoLinkedList = mPriceMap.get(coinId);
        if(coinInfoLinkedList == null){
            coinInfoLinkedList = new LinkedList<CoinInfo>();
            CoinInfo coinInfo = new CoinInfo();
            coinInfoLinkedList.addLast(coinInfo);
            mPriceMap.put(coinId, coinInfoLinkedList);
        }

        CoinInfo coinInfo = coinInfoLinkedList.getLast();

        boolean notifyOneMinute = false;
        boolean notifyFiveMinute = false;

        if(coinInfo.second + 1 < 60)
        {
            ++coinInfo.second;
            coinInfo.volumeOneMin = Double.parseDouble(coin.vol);
            double high = Double.parseDouble(coin.high);
            double low = Double.parseDouble(coin.low);
            double last = Double.parseDouble(coin.last);
            if(high > coinInfo.maxOneMin){
                coinInfo.maxOneMin = high;
            }
            if(coinInfo.minOneMin == 0 || low < coinInfo.minOneMin){
                coinInfo.minOneMin = low;
            }
            coinInfo.lastOneMin = last;
        }
        else
        {
            //计算1分钟的涨幅和量能是否满足预警条件
            if(coinInfoLinkedList.size() >= 2){
                CoinInfo curMinCoinInfo = coinInfoLinkedList.getLast();
                CoinInfo preMinCoinInfo = coinInfoLinkedList.get(coinInfoLinkedList.size()-2);
                curMinCoinInfo.volumeOneMin -= preMinCoinInfo.volumeOneMin;

                notifyOneMinute = isSatisfiedOneMinuteCondition(preMinCoinInfo, curMinCoinInfo);
            }

            //计算5分钟的涨幅和量能是否满足预警条件，然后pop出前面5分钟的数据
            if(coinInfoLinkedList.size() >= 10){
                notifyFiveMinute = isSatisfiedFiveMinuteCondition(coinInfoLinkedList);
                //pop出前面5分钟的数据
                coinInfoLinkedList.removeFirst();
                coinInfoLinkedList.removeFirst();
                coinInfoLinkedList.removeFirst();
                coinInfoLinkedList.removeFirst();
                coinInfoLinkedList.removeFirst();
            }

            coinInfo = new CoinInfo();
            coinInfo.index = coinInfoLinkedList.size();
            coinInfo.volumeOneMin =  Double.parseDouble(coin.vol);
            coinInfo.maxOneMin = Double.parseDouble(coin.high);
            coinInfo.minOneMin = Double.parseDouble(coin.low);
            coinInfo.lastOneMin = Double.parseDouble(coin.last);
            coinInfoLinkedList.addLast(coinInfo);
        }

        return (notifyOneMinute || notifyFiveMinute);
    }

    private boolean isSatisfiedOneMinuteCondition(CoinInfo pre, CoinInfo cur)
    {
        double priceDistance = cur.lastOneMin - pre.lastOneMin;
        double volumeDistance = cur.volumeOneMin - pre.volumeOneMin;

        if(priceDistance <= 0)
            return false;

        double priceRate = priceDistance / pre.lastOneMin;
        priceRate *= 100;
        if(priceRate > 1.0)
            return true;

        return false;
    }

    private boolean isSatisfiedFiveMinuteCondition(LinkedList<CoinInfo> list)
    {
        if(list.size() != 10)
            return false;

        double prePrice = 0;
        double curPrice = 0;
        double preVolume = 0;
        double curVolume = 0;

        int tick = 0;
        for(CoinInfo item : list)
        {
            tick++;
            if(tick <= 5){
                prePrice = item.lastOneMin;
                preVolume += item.volumeOneMin;
            }else{
                curPrice = item.lastOneMin;
                curVolume += item.volumeOneMin;
            }
        }

        double priceDistance = curPrice - prePrice;
        double volumeDistance = curVolume - preVolume;

        if(priceDistance <= 0/* || volumeDistance <= 0*/)
            return false;

        double priceRate = priceDistance / prePrice;
        priceRate *= 100;
        if(priceRate > 0.5)
            return true;

        return false;
    }

}
