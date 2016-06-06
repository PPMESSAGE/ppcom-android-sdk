package com.ppmessage.ppcomdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ppmessage.ppcomlib.ui.ConversationsActivity;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.notification.INotification;
import com.ppmessage.sdk.core.ui.ConversationFragment;
import com.ppmessage.sdk.core.ui.adapter.ConversationsAdapter;
import com.ppmessage.sdk.core.ui.adapter.MessageAdapter;

import java.util.List;

/**
 * Created by ppmessage on 5/13/16.
 */
public class MainActivity extends ConversationsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startUp();
    }

}
