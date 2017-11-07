package com.example.dailyarticle.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by ZZP on 2017/7/6.
 */



public abstract class EndlessRecyclerOnScrollListener extends
        RecyclerView.OnScrollListener {

    private int previousTotal = 0;
    private boolean loading = true;

    private int currentPage = 1;

    //private GridLayoutManager mgridLayoutManager;
    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(
            LinearLayoutManager LinearLayoutManager) {
        this.mLinearLayoutManager = LinearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int firstVisibleItem, visibleItemCount, totalItemCount;

        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading
                && (totalItemCount - visibleItemCount) <= firstVisibleItem) {
            currentPage++;
            onLoadMore(currentPage);
            loading = true;
        }
    }

    public abstract void onLoadMore(int currentPage);
}