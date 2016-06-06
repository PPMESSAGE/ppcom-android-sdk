package com.ppmessage.sdk.core.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ppmessage.sdk.R;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.notification.INotification;
import com.ppmessage.sdk.core.ui.adapter.MessageAdapter;
import com.ppmessage.sdk.core.ui.view.MessageListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ppmessage on 5/11/16.
 */
public class MessageActivity extends AppCompatActivity {

    private static final String TEXT_EMPTY_LOG = "[Send] text == nil";
    private static final String SDK_EMPTY_LOG = "[Send] SDK == nil";
    private static final String CONVERSATION_EMTPY_LOG = "[Send] conversation == nil";
    private static final String FROMUSER_EMPTY_LOG = "[Send] FromUser == nil";

    protected MessageListView messageListView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected MessageAdapter messageAdapter;
    protected TextView sendButton;
    protected EditText inputEt;

    private PPMessageSDK sdk;

    private Conversation conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pp_chat);

        messageListView = (MessageListView) findViewById(R.id.pp_chat_lv);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.pp_chat_swipe_refresh_layout);
        sendButton = (TextView) findViewById(R.id.pp_chat_tools_send_btn);
        inputEt = (EditText) findViewById(R.id.pp_chat_tools_input_et);

        sendButton.setEnabled(false);
        swipeRefreshLayout.setEnabled(false);

        // Avoid keyboard auto popup
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initEvent();

    }

    public void setAdapter(MessageAdapter adapter) {
        if (this.messageAdapter != adapter) {
            this.messageAdapter = adapter;
            this.messageListView.setAdapter(this.messageAdapter);
            this.messageAdapter.notifyDataSetChanged();
        }
    }

    public void setMessageSDK(PPMessageSDK sdk) {
        this.sdk = sdk;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    protected void onTextMessageSendFinish(PPMessage message) {
        inputEt.setText("");
    }

    protected void onSwipeRefresh(SwipeRefreshLayout swipeRefreshLayout) {

    }

    // === MessageActivity Business Logic ===

    private void initEvent() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PPMessage message = sendText(inputEt.getText().toString());
                onTextMessageSendFinish(message);
            }
        });

        inputEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageListView.scrollToBottom();
            }
        });

        inputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sendButton.setEnabled(s.toString().length() > 0);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MessageActivity.this.onSwipeRefresh(swipeRefreshLayout);
            }
        });
    }

    private PPMessage sendText(String text) {
        PPMessage message = null;
        if (checkInfoBeforeSendMessage(text)) {
            message = new PPMessage.Builder()
                    .setFromUser(sdk.getNotification().getConfig().getActiveUser())
                    .setConversation(conversation)
                    .setMessageBody(text)
                    .build();
            if (!sdk.getNotification().canSendMessage()) {
                message.setError(true);
            } else {
                sdk.getNotification().sendMessage(message);
            }
        }
        return message;
    }

    private boolean checkInfoBeforeSendMessage(String text) {
        if (TextUtils.isEmpty(text)) {
            L.w(TEXT_EMPTY_LOG);
            return false;
        }
        if (sdk == null) {
            L.w(SDK_EMPTY_LOG);
            return false;
        }
        if (conversation == null) {
            L.w(CONVERSATION_EMTPY_LOG);
            return false;
        }
        if (sdk.getNotification().getConfig().getActiveUser() == null) {
            L.w(FROMUSER_EMPTY_LOG);
            return false;
        }
        return true;
    }

}
