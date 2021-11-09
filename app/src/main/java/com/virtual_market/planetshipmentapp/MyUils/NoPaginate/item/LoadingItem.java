package com.virtual_market.planetshipmentapp.MyUils.NoPaginate.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.virtual_market.planetshipmentapp.R;


public interface LoadingItem extends BaseLinearLayoutManagerItem {


    LoadingItem DEFAULT = new LoadingItem() {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }
    };


}
