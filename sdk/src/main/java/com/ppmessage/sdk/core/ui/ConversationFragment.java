package com.ppmessage.sdk.core.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ppmessage.sdk.R;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.ui.adapter.ConversationsAdapter;

import java.util.List;

/**
 * Created by ppmessage on 5/13/16.
 */
public class ConversationFragment extends Fragment {

    protected ListView conversationListView;

    private PPMessageSDK sdk;
    private List<Conversation> conversationList;
    private ConversationsAdapter conversationsAdapter;

    private ConversationsAdapter.OnItemClickListener itemClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pp_conversations, container, false);
        conversationListView = (ListView) v.findViewById(R.id.pp_conversations_content_lv);
        return v;
    }

    protected ConversationsAdapter createAdapter(PPMessageSDK sdk, List<Conversation> conversationList) {
        return new ConversationsAdapter(sdk, conversationList);
    }

    public void setMessageSDK(PPMessageSDK sdk) {
        this.sdk = sdk;
    }

    public void setConversationList(List<Conversation> conversationList) {
        this.conversationList = conversationList;

        if (conversationsAdapter != null) {
            conversationsAdapter.setConversationList(conversationList);
        } else {
            conversationsAdapter = createAdapter(sdk, conversationList);
            setOnItemClickListener(this.itemClickListener);
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

}
