package com.ppmessage.ppcomdemo;

import android.app.Application;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.ppcomlib.PPComSDKConfiguration;
import com.ppmessage.sdk.core.PPMessageSDK;

/**
 * Created by ppmessage on 5/13/16.
 */
public class App extends Application {

    //private static final String TEST_APP_UUID = "77933ab0-f17c-11e5-8957-02287b8c0ebf";
    //private static final String TEST_APP_UUID = "a600998e-efff-11e5-9d9f-02287b8c0ebf";
    private static final String TEST_APP_UUID = "b60fcac7-3ff7-11e6-b4d7-acbc327f19e9";
    private static final String TEST_API_KEY = "NGQyMTdlYTBhMDg2YjhjN2JhYjQ4MzZmMGY0NmEzNTRkOGRjYTA5OA==";
    private static final String TEST_API_SECRET = "NzRkM2M3MWQ4NGY3NjgzZWExNDdlMWFkMmEwMzA3OTkzNDFkOTU5Yg==";
    private static final String TEST_HOST = "192.168.0.204:8945";
    private static final boolean TEST_SSL = false;
    
    @Override
    public void onCreate() {
        super.onCreate();

        PPComSDK sdk = PPComSDK.getInstance();
        sdk.init(new PPComSDKConfiguration.Builder(this)
                 .setAppUUID(TEST_APP_UUID)
                 .setApiKey(TEST_API_KEY)
                 .setApiSecret(TEST_API_SECRET)
                 .setHost(TEST_HOST)
                 .setSsl(TEST_SSL)
                 .build());
        
    }

}
