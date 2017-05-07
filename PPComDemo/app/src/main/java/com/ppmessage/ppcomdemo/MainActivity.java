package com.ppmessage.ppcomdemo;

import android.os.Bundle;

import com.ppmessage.ppcomlib.ui.ConversationsActivity;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.ppcomlib.PPComSDKConfiguration;

/**
 * Created by ppmessage on 5/13/16.
 */
public class MainActivity extends ConversationsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //make sure the init success
        super.onCreate(savedInstanceState);
        this.startUp();
    }
}
