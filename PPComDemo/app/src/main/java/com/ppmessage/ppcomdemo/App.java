package com.ppmessage.ppcomdemo;

import android.app.Application;
import android.graphics.Color;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.ppcomlib.PPComSDKConfiguration;

/**
 * Created by ppmessage on 5/13/16.
 */
public class App extends Application {

//    private static final String TEST_APP_UUID = "914feafd-42a1-11e6-bbfe-58b035f16bf4";
//    private static final String TEST_API_KEY = "MGEzNzFhOTNjMGI2ZjgyMGViYzAzNmI5NTFlYWY2M2IyMTUyODVlZg==";
//    private static final String TEST_API_SECRET = "NjM1MTY4MWU0ZDlmYWEzMzhhZWU4ZjhjNzM5YTJkZDY2N2M0YTRjNA==";
//    private static final String TEST_SERVER_URL = "http://192.168.0.51:8945";

    private static final String TEST_APP_UUID = "28d059d1-53bd-11e6-a2a6-acbc327f19e9";
    private static final String TEST_API_KEY = "ZWZiZDZlMWZjYThiMDI4ODUxMjdkMGQyMTE0ZDE2NzE3MDg1NzUxYg==";
    private static final String TEST_API_SECRET = "Y2VhMmNhMDY2YjdmN2U3OTQ4MWIyNmMyMjE1MTdlZjlmMzUzYWUwZg==";
    private static final String TEST_SERVER_URL = "http://192.168.0.204:8945";

    @Override
    public void onCreate() {

        PPComSDK sdk = PPComSDK.getInstance();
        PPComSDKConfiguration.Builder builder = new PPComSDKConfiguration.Builder();
        sdk.init(builder.setContext(this)
                .setAppUUID(TEST_APP_UUID)
                .setApiKey(TEST_API_KEY)
                .setApiSecret(TEST_API_SECRET)
                .setServerUrl(TEST_SERVER_URL)
                .setInputHint("Any questions")
                .setActionbarBackgroundColor(getResources().getColor(
                        android.R.color.holo_blue_dark))
                .setActionbarTitleColor(Color.WHITE)
                .setEnableLog(true)
                .setEnableEnterKeyToSendText(true)
                .build());

        super.onCreate();
    }
}
