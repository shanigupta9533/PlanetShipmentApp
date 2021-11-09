package com.virtual_market.planetshipmentapp.MyUils.NoPaginate.callback;

public interface PaginateView {

    void showPaginateLoading(boolean show);

    void showPaginateError(boolean show);

    void setPaginateNoMoreData(boolean show);
}
