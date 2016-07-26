package com.ppmessage.sdk.core.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.ppmessage.sdk.core.L;

/**
 * Created by ppmessage on 6/2/16.
 */
public class CustomListView extends ListView implements AbsListView.OnScrollListener {

    public interface OnLastItemVisibleListener {
        void onLastItemVisible();
    }

    private boolean mLastItemVisible;
    private OnScrollListener mOnScrollListener;
    private OnLastItemVisibleListener mOnLastItemVisibleListener;

    private int scrollIndex;
    private int top;

    public CustomListView(Context context) {
        super(context);
        innerInit();
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        innerInit();
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        innerInit();
    }

    private void innerInit() {
        super.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        /**
         * Check that the scrolling has stopped, and that the last item is
         * visible.
         */
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && null != mOnLastItemVisibleListener && mLastItemVisible) {
            mOnLastItemVisibleListener.onLastItemVisible();
        }

        if (null != mOnScrollListener) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (null != mOnLastItemVisibleListener) {
            mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1);
        }

        // Finally call OnScrollListener if we have one
        if (null != mOnScrollListener) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    /**
     * Maintain scroll position
     *
     * <pre>
     *     saveScrollPosition();
     *
     *     // add new items
     *     // ...
     *
     *     restoreScrollPosition();
     * </pre>
     *
     */
    public void saveScrollPosition() {
        scrollIndex = getFirstVisiblePosition();
        top = getChildAt(0) == null ? 0 : (getChildAt(0).getTop() - getPaddingTop());
    }

    public void restoreScrollPosition() {
        restoreScrollPosition(0);
    }

    public void restoreScrollPosition(final int scrollIndexOffset) {
        post(new Runnable() {
            @Override
            public void run() {
                setSelectionFromTop(scrollIndex, top + scrollIndexOffset);
            }
        });
    }

    public final void setOnLastItemVisibleListener(OnLastItemVisibleListener listener) {
        mOnLastItemVisibleListener = listener;
    }

    public final void setOnScrollListener(OnScrollListener listener) {
        mOnScrollListener = listener;
    }

}
