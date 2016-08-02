package com.ppmessage.ppcomdemo;

import android.os.Bundle;

import com.ppmessage.ppcomlib.ui.ConversationsActivity;

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
}
