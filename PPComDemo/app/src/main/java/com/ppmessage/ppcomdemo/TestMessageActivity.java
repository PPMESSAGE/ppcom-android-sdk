package com.ppmessage.ppcomdemo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

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
        PPMessageSDK messageSDK = sdk.getConfiguration().getMessageSDK();

        List<PPMessage> messageList = TestData.getTestMessageList();
        MessageAdapter messageAdapter = new MessageAdapter(messageSDK, this, messageList);
        setAdapter(messageAdapter);

    }

}
