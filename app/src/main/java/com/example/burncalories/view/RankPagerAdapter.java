package com.example.burncalories.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.burncalories.Account;
import com.example.burncalories.AccountData;
import com.example.burncalories.R;
import com.example.burncalories.activity.RankActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RankPagerAdapter extends PagerAdapter implements CardAdapter {
    private List<CardView> mViews;
    private List<RankItem> mData;
    private RecyclerView mRankList;
    private float mBaseElevation;

    public RankPagerAdapter() {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addRankItem(RankItem item) {
        mViews.add(null);
        mData.add(item);
    }

    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {

        return mViews.get(position);
    }

    @Override
    public float getPageWidth(int position) {
        return 1.5f;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.rank_card, container, false);
        container.addView(view);
        RankItem item = mData.get(position);
        bind(item, view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        //Load RecycleView
        LinearLayoutManager layoutManager = new LinearLayoutManager(container.getContext());

        AccountAdapter accountAdapter = new AccountAdapter(item.getData());

        RecyclerView recyclerView =  cardView.findViewById(R.id.recycle_view);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(accountAdapter);


        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(RankItem item, View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        titleTextView.setText(item.getRankTitle());
    }


    //实现刷新
    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

        private List<AccountData> mAccountDataList;

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView data;
            RoundedImageView headshot;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                name =  itemView.findViewById(R.id.name);
                data =  itemView.findViewById(R.id.data);
                headshot =  itemView.findViewById(R.id.portrait);
            }
        }

        public AccountAdapter(List<AccountData> accountDataList) {
            mAccountDataList = accountDataList;
            Collections.sort(mAccountDataList, new Comparator<AccountData>(){

                @Override
                public int compare(AccountData o1, AccountData o2) {
                    return o2.compareTo(o1);
                }
            });
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.rank_item, viewGroup, false);
            final int pos = i;
            final ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull AccountAdapter.ViewHolder viewHolder, int i) {
            AccountData accountData = mAccountDataList.get(i);
            viewHolder.name.setText(accountData.getName());
            Log.e("RankPagerAdapter", accountData.getName());
            viewHolder.headshot.setImageBitmap(accountData.getImage());
            viewHolder.data.setText(String.valueOf(accountData.getData()));
        }

        @Override
        public int getItemCount() {
            return mAccountDataList.size();
        }

    }
}
