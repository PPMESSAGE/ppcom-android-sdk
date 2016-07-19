package com.ppmessage.ppcomdemo;

import android.os.Bundle;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.ui.MessageActivity;
import com.ppmessage.sdk.core.ui.adapter.MessageAdapter;

import java.util.List;

public class TestMessageActivity extends MessageActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PPComSDK sdk = PPComSDK.getInstance();
        PPMessageSDK messageSDK = sdk.getPPMessageSDK();
        List<PPMessage> messageList = TestData.getTestMessageList();
        MessageAdapter messageAdapter = new MessageAdapter(messageSDK, this, messageList);
        setAdapter(messageAdapter);

    }

}
