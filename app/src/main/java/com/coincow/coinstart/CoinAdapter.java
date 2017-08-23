package com.coincow.coinstart;

/**
 * Created by Administrator on 2017/8/22.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CoinAdapter extends
        RecyclerView.Adapter<CoinAdapter.ViewHolder>
{
    private LayoutInflater mInflater;
    private List<MainActivity.Coin> mDatas;

    public CoinAdapter(Context context, List<MainActivity.Coin> datas)
    {
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View v)
        {
            super(v);
        }

        TextView mName;
        TextView mPrice;
        TextView mIncrease;
    }

    @Override
    public int getItemCount()
    {
        return mDatas.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int pos)
    {
        View view = mInflater.inflate(R.layout.coin_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mName = (TextView)view.findViewById(R.id.coin_name);
        viewHolder.mPrice = (TextView)view.findViewById(R.id.coin_price);
        viewHolder.mIncrease = (TextView)view.findViewById(R.id.coin_increase);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewholder, final int pos)
    {
        MainActivity.Coin coin = mDatas.get(pos);
        viewholder.mName.setText(coin.name);
        viewholder.mPrice.setText(coin.last);
        viewholder.mIncrease.setText(R.string.coin_pending);
    }
}
