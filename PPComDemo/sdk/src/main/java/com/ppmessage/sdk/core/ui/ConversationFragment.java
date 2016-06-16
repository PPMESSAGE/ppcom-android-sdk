package com.ppmessage.sdk.core.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ppmessage.sdk.R;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.ui.adapter.ConversationsAdapter;
import com.ppmessage.sdk.core.ui.view.CustomListView;

import java.util.List;

/**
 * Created by ppmessage on 5/13/16.
 */
public class ConversationFragment extends Fragment {

    protected CustomListView conversationListView;
    private ViewGroup loadingView;
    private TextView loadingTextView;

    private PPMessageSDK sdk;
    private ConversationsAdapter conversationsAdapter;

    private ConversationsAdapter.OnItemClickListener itemClickListener;
    private ConversationsAdapter.OnItemLongClickListener itemLongClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pp_conversations, container, false);
        conversationListView = (CustomListView) v.findViewById(R.id.pp_conversations_content_lv);
        loadingView = (ViewGroup) v.findViewById(R.id.pp_conversations_loading_view);
        loadingTextView = (TextView) v.findViewById(R.id.loading_textview);
        showLoadingView(false);
        return v;
    }

    protected ConversationsAdapter createAdapter(PPMessageSDK sdk, List<Conversation> conversationList) {
        return new ConversationsAdapter(sdk, conversationList);
    }

    public void setMessageSDK(PPMessageSDK sdk) {
        this.sdk = sdk;
    }

    protected ConversationsAdapter getAdapter() {
        return conversationsAdapter;
    }

    protected void showLoadingView(boolean show) {
        showLoadingView(show, R.string.loading);
    }

    protected void showLoadingView(boolean show, @StringRes int strResId) {
        loadingTextView.setText(strResId);
        loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setConversationList(List<Conversation> conversationList) {
        if (conversationsAdapter != null) {
            conversationsAdapter.setConversationList(conversationList);
        } else {
            conversationsAdapter = createAdapter(sdk, conversationList);
            setOnItemClickListener(this.itemClickListener);
            setOnItemLongClickListener(this.itemLongClickListener);
        }

        if (conversationListView != null) {
            conversationListView.setAdapter(conversationsAdapter);
        }
    }

    public void setOnItemClickListener(ConversationsAdapter.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;

        if (conversationsAdapter != null) {
            conversationsAdapter.setOnItemClickedListener(itemClickListener);
        }
    }

    public void setOnItemLongClickListener(ConversationsAdapter.OnItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;

        if (conversationsAdapter != null) {
            conversationsAdapter.setItemOnLongClickListener(itemLongClickListener);
        }
    }

}
