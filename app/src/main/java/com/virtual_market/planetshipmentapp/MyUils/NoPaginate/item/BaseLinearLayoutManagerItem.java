package com.virtual_market.planetshipmentapp.MyUils.NoPaginate.item;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;


public interface BaseLinearLayoutManagerItem {

    RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);
    void onBindViewHolder(RecyclerView.ViewHolder holder, int position);
}
