package com.virtual_market.planetshipmentapp.MyUils.NoPaginate.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.virtual_market.planetshipmentapp.MyUils.NoPaginate.callback.OnRepeatListener;
import com.virtual_market.planetshipmentapp.R;

public interface ErrorItem {

    RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    void onBindViewHolder(RecyclerView.ViewHolder holder, int position, OnRepeatListener onRepeatListener);

    ErrorItem DEFAULT = new ErrorItem() {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_error, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, final OnRepeatListener onRepeatListener) {

            Button btnRepeat = (Button) holder.itemView.findViewById(R.id.btnRepeat);
            btnRepeat.setBackgroundResource(R.drawable.no_pagination_button_ripple);
            btnRepeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (onRepeatListener != null) {
                        onRepeatListener.onClickRepeat();
                    }
                }
            });
        }


    };


}
