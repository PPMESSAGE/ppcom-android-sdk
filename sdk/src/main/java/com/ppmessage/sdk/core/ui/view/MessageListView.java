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
public class MessageListView extends CustomListView {

    public MessageListView(Context context) {
        super(context);
    }

    public MessageListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MessageListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
