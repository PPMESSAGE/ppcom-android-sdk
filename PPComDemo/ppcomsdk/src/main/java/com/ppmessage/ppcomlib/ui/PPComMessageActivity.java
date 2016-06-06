package com.ppmessage.ppcomlib.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.ppcomlib.R;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.model.HistoryPageIndex;
import com.ppmessage.sdk.core.model.MessageHistoryLoader;
import com.ppmessage.sdk.core.model.MessageHistorysModel;
import com.ppmessage.sdk.core.model.MessagesModel;
import com.ppmessage.sdk.core.notification.INotification;
import com.ppmessage.sdk.core.notification.WSMessageAckNotificationHandler;
import com.ppmessage.sdk.core.ui.MessageActivity;
import com.ppmessage.sdk.core.ui.adapter.MessageAdapter;
import com.ppmessage.sdk.core.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ppmessage on 5/13/16.
 */
public class PPComMessageActivity extends MessageActivity {

    public static final String EXTRA_KEY_CONVERSATION_UUID = "com.ppmessage.ppcomlib.ui.conversation_uuid";
    public static final String EXTRA_KEY_CONVERSATION_NAME = "com.ppmessage.ppcomlib.ui.conversation_name";

    private static final String LOG_GET_CONVERSATION_UUID_FROM_INTENT = "get conversation uuid from intent: %s";
    private static final String LOG_NOTIFY_DATASET_CHANGED = "notify dataset changed";

    private PPComSDK sdk;
    private PPMessageSDK messageSDK;

    // For load historys
    private MessageHistoryLoader historyLoader;
    private MessagesModel messagesModel;
    private boolean inRequestingHistory;

    // For WebSocket Notification
    private INotification.OnNotificationEvent notificationEvent;

    private String conversationUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        swipeRefreshLayout.setEnabled(true);

        sdk = PPComSDK.getInstance();
        messageSDK = sdk.getConfiguration().getMessageSDK();
        messagesModel = sdk.getMessageService().getMessagesModel();
        historyLoader = messagesModel.getHistoryLoader();

        setMessageSDK(messageSDK);

        conversationUUID = getIntent().getStringExtra(EXTRA_KEY_CONVERSATION_UUID);
        L.d(LOG_GET_CONVERSATION_UUID_FROM_INTENT, conversationUUID);

        setTitle(getIntent().getStringExtra(EXTRA_KEY_CONVERSATION_NAME));

        setConversation(findConversation(conversationUUID));

        List<PPMessage> messageList = messagesModel.getMessageList(conversationUUID);
        MessageAdapter messageAdapter = new MessageAdapter(messageSDK, this, messageList, false);
        setAdapter(messageAdapter);

        messageListView.scrollToBottom();

    }

    @Override
    protected void onResume() {
        super.onResume();

        addNotificationListener();

    }

    @Override
    protected void onStop() {
        super.onStop();

        removeNotificationListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ppcomlib_message_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.conversation_members) {
            if (conversationUUID != null) {
                Intent intent = new Intent(this, ConversationMemberActivity.class);
                intent.putExtra(ConversationMemberActivity.EXTRA_CONVERSATION_UUID, conversationUUID);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private Conversation findConversation(String conversationUUID) {
        return sdk.getMessageService().getConversationsModel().get(conversationUUID);
    }

    @Override
    protected void onTextMessageSendFinish(PPMessage message) {
        super.onTextMessageSendFinish(message);

        refreshListView(message);
        messageListView.scrollToBottom();

    }

    @Override
    protected void onSwipeRefresh(final SwipeRefreshLayout swipeRefreshLayout) {
        super.onSwipeRefresh(swipeRefreshLayout);

        if (!hasMoreHistory(conversationUUID)) {
            Utils.makeToast(this, R.string.pp_no_more_history);
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (inRequestingHistory) return;

        inRequestingHistory = true;
        final MessageHistorysModel.MessageHistoryRequestParam requestParam = getHistoryLoaderRequestParam(conversationUUID);
        historyLoader.loadHistorys(requestParam, new MessageHistorysModel.OnLoadHistoryEvent() {
            @Override
            public void onCompleted(HistoryPageIndex pageIndex, final List<PPMessage> messageList) {
                inRequestingHistory = false;
                swipeRefreshLayout.setRefreshing(false);

                if (messageList != null && !messageList.isEmpty()) {

                    refreshListView(messageList);

                }
            }
        });

    }

    private MessageHistorysModel.MessageHistoryRequestParam getHistoryLoaderRequestParam(String conversationUUID) {
        HistoryPageIndex pageIndex = historyLoader.getLastHistoryPageIndex(conversationUUID);
        MessageHistorysModel.MessageHistoryRequestParam requestParam = null;
        if (pageIndex == null) {
            requestParam = new MessageHistorysModel.MessageHistoryRequestParam(conversationUUID, null, 0);
        } else {
            requestParam = new MessageHistorysModel.MessageHistoryRequestParam(conversationUUID, pageIndex.getMaxUUID(),
                    pageIndex.getPageOffset() + 1); // Load the next Page
        }
        return requestParam;
    }

    private boolean hasMoreHistory(String conversationUUID) {
        HistoryPageIndex pageIndex = historyLoader.getLastHistoryPageIndex(conversationUUID);
        if (pageIndex != null) {
            return messagesModel.getMessageList(conversationUUID).size() < pageIndex.getTotalCount();
        }
        return true;
    }

    // === WebSocket Message Notification Listener ===
    private void addNotificationListener() {
        notificationEvent = new INotification.SimpleNotificationEvent() {
            @Override
            public int getInterestedEvent() {
                return INotification.EVENT_MESSAGE | INotification.EVENT_MSG_SEND_ERROR;
            }

            @Override
            public void onMessageInfoArrived(PPMessage message) {
                super.onMessageInfoArrived(message);

                refreshListView(message);
                messageListView.scrollToBottom();

            }

            @Override
            public void onMessageSendError(WSMessageAckNotificationHandler.MessageSendResult messageSendResult) {
                super.onMessageSendError(messageSendResult);

                PPMessage find = messagesModel.findMessage(messageSendResult.getConversationUUID(), messageSendResult.getMessageUUID());
                if (find != null) {
                    find.setError(true);
                    refreshListView();
                }

            }
        };
        messageSDK.getNotification().addListener(notificationEvent);
    }

    private void removeNotificationListener() {
        if (notificationEvent != null) {
            messageSDK.getNotification().removeListener(notificationEvent);
            notificationEvent = null;
        }
    }

    private void refreshListView() {
        refreshListView((PPMessage) null);
    }

    private void refreshListView(PPMessage message) {
        if (message != null) {
            sdk.getMessageService().updateModels(message);
        }

        L.d(LOG_NOTIFY_DATASET_CHANGED);
        messageAdapter.setMessages(messagesModel.getMessageList(conversationUUID));
    }

    /**
     * Maintain current position
     *
     * @param messageList
     */
    private void refreshListView(List<PPMessage> messageList) {
        List<PPMessage> afterFiteredMessages = messagesModel.addAllAtHead(messageList);
        final int index = messageListView.getFirstVisiblePosition() + afterFiteredMessages.size();
        messageListView.post(new Runnable() {
            @Override
            public void run() {
                int fixIndex = index > 0 ? index - 1 : index;
                messageListView.setSelection(fixIndex);
            }
        });
    }

}
