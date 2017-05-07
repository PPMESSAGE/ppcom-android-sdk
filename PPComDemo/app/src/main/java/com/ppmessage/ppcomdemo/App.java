package com.ppmessage.ppcomdemo;

import android.app.Application;
import android.graphics.Color;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.ppcomlib.PPComSDKConfiguration;

/**
 * Created by ppmessage on 5/13/16.
 */
public class App extends Application {

    @Override
    public void onCreate() {


        PPComSDK sdk = PPComSDK.getInstance();
        PPComSDKConfiguration.Builder builder = new PPComSDKConfiguration.Builder();
        sdk.init(builder
                .setContext(this.getApplicationContext())
                .setServerUrl("https://ppmessage.cn")
                .setAppUuid("a600998e-efff-11e5-9d9f-02287b8c0ebf")
                .setEnableLog(true)
                .build());

        super.onCreate();


    }
}
