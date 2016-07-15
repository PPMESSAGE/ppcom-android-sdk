package com.ppmessage.ppcomdemo;

import android.app.Application;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.ppcomlib.PPComSDKConfiguration;
import com.ppmessage.sdk.core.PPMessageSDK;

/**
 * Created by ppmessage on 5/13/16.
 */
public class App extends Application {

//    private static final String TEST_APP_UUID = "914feafd-42a1-11e6-bbfe-58b035f16bf4";
//    private static final String TEST_API_KEY = "MGEzNzFhOTNjMGI2ZjgyMGViYzAzNmI5NTFlYWY2M2IyMTUyODVlZg==";
//    private static final String TEST_API_SECRET = "NjM1MTY4MWU0ZDlmYWEzMzhhZWU4ZjhjNzM5YTJkZDY2N2M0YTRjNA==";
//    private static final String TEST_SERVER_URL = "http://192.168.0.51:8945";

    private static final String TEST_APP_UUID = "9c60acbd-44bb-11e6-94c0-acbc327f19e9";
    private static final String TEST_API_KEY = "YjRlZTYyNWY3ZThjMDJlNDg3YjRkYjNkZDQzNTk0NjdmODk1ZTMzNg==";
    private static final String TEST_API_SECRET = "MTU3ZWE3MWQ4MTc0NzgxNjRhNGViMTdhMWUyMDUxZTRlYzAzNjg2YQ==";
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
                .build());

        super.onCreate();
    }
}
