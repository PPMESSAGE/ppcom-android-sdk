package com.ppmessage.sdk.core.ui.view;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ppmessage.sdk.core.L;

/**
 * Created by ppmessage on 5/12/16.
 */
public class MessageListView extends ListView implements AbsListView.OnScrollListener {

    private int top;
    private int index;

    public MessageListView(Context context) {
        super(context);
        init(context);
    }

    public MessageListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MessageListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.setOnScrollListener(this);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            index = getFirstVisiblePosition();
            View v = getChildAt(0);
            top = (v == null) ? 0 : (v.getTop() - getPaddingTop());
        }
    }

    /**
     * restore from saved position
     */
    public void restore() {
        L.d("index:" + index + ", top:" + top);
        setSelectionFromTop(index, top);
    }

    /**
     * Scroll to bottom
     */
    public void scrollToBottom() {
        post(new Runnable() {
            @Override
            public void run() {
                ListAdapter adapter = MessageListView.this.getAdapter();
                if (adapter != null) {
                    setSelection(adapter.getCount() - 1);
                }
            }
        });
    }

}
