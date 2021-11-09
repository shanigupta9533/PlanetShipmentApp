package com.virtual_market.planetshipmentapp.MyUils.NoPaginate.paginate;

import androidx.recyclerview.widget.RecyclerView;

import com.virtual_market.planetshipmentapp.MyUils.NoPaginate.callback.OnLoadMore;
import com.virtual_market.planetshipmentapp.MyUils.NoPaginate.callback.OnLoadMoreListener;
import com.virtual_market.planetshipmentapp.MyUils.NoPaginate.item.ErrorItem;
import com.virtual_market.planetshipmentapp.MyUils.NoPaginate.item.LoadingItem;

@Deprecated
public class PaginateBuilder {


    private RecyclerView recyclerView;
    private OnLoadMore paginateCallback;
    private OnLoadMoreListener loadMoreListener;
    private LoadingItem loadingItem;
    private ErrorItem errorItem;
    private int loadingTriggerThreshold = 0;

    @Deprecated
    public PaginateBuilder() {
    }


    @Deprecated
    public PaginateBuilder with(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        return this;
    }

    @Deprecated
    public PaginateBuilder setCallback(OnLoadMore paginateCallback) {
        this.paginateCallback = paginateCallback;
        return this;
    }

    @Deprecated
    public PaginateBuilder setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
        return this;
    }


    @Deprecated
    public PaginateBuilder setLoadingTriggerThreshold(int loadingTriggerThreshold) {
        this.loadingTriggerThreshold = loadingTriggerThreshold;
        return this;
    }


    @Deprecated
    public PaginateBuilder setCustomLoadingItem(LoadingItem loadingItem) {
        this.loadingItem = loadingItem;
        return this;
    }


    @Deprecated
    public PaginateBuilder setCustomErrorItem(ErrorItem errorItem) {
        this.errorItem = errorItem;
        return this;
    }


    @Deprecated
    public Paginate build() {
        if (loadingItem == null) {
            loadingItem = LoadingItem.DEFAULT;
        }
        if (errorItem == null) {
            errorItem = ErrorItem.DEFAULT;
        }

        return new Paginate(recyclerView, paginateCallback, loadMoreListener, loadingTriggerThreshold, loadingItem, errorItem);
    }


}
