package com.virtual_market.planetshipmentapp.MyUils.NoPaginate.paginate;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.virtual_market.planetshipmentapp.MyUils.NoPaginate.callback.OnLoadMoreListener;
import com.virtual_market.planetshipmentapp.MyUils.NoPaginate.item.ErrorItem;
import com.virtual_market.planetshipmentapp.MyUils.NoPaginate.item.LoadingItem;


/**
 * Created by Alex Bykov on 11.08.2017.
 * You can contact me at: me@alexbykov.ru.
 */

public final class NoPaginateBuilder {


    private RecyclerView recyclerView;
    private OnLoadMoreListener loadMoreListener;
    private LoadingItem loadingItem;
    private ErrorItem errorItem;
    private int loadingTriggerThreshold = 0;


    NoPaginateBuilder(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public NoPaginateBuilder setOnLoadMoreListener(@NonNull OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
        return this;
    }

    public NoPaginateBuilder setLoadingTriggerThreshold(@IntRange(from = 0) int loadingTriggerThreshold) {
        this.loadingTriggerThreshold = loadingTriggerThreshold;
        return this;
    }
    public NoPaginateBuilder setCustomLoadingItem(@NonNull LoadingItem loadingItem) {
        this.loadingItem = loadingItem;
        return this;
    }

    public NoPaginateBuilder setCustomErrorItem(@NonNull ErrorItem errorItem) {
        this.errorItem = errorItem;
        return this;
    }

    public NoPaginate build() {
        if (loadingItem == null) {
            loadingItem = LoadingItem.DEFAULT;
        }
        if (errorItem == null) {
            errorItem = ErrorItem.DEFAULT;
        }

        return new NoPaginate(recyclerView, loadMoreListener, loadingTriggerThreshold, loadingItem, errorItem);
    }


}
