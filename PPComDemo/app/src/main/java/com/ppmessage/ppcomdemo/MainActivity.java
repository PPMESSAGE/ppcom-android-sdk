package com.ppmessage.ppcomdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ppmessage.ppcomlib.ui.ConversationsActivity;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.ui.adapter.ConversationsAdapter;

/**
 * Created by ppmessage on 5/13/16.
 */
public class MainActivity extends ConversationsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //make sure the init success
//        PPComSDK sdk = PPComSDK.getInstance();
//        PPComSDKConfiguration.Builder builder = new PPComSDKConfiguration.Builder();
//
//        sdk.update(builder.setUserFullName("Test User Name")
//                .setEntUserData("Test User Data")
//                .setJpushRegistrationId("Test JPush Registration ID")
//                .setUserIcon("Test User Icon URL")
//                .build());
//
        startUp();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // NOTE: ONLY FOR TEST
        conversationFragment.setOnItemClickListener(new ConversationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View container, Conversation conversation) {
                Intent intent = new Intent(MainActivity.this, TestMessageActivity.class);
                startActivity(intent);
            }
        });
    }
}
