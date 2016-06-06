package com.ppmessage.ppcomlib;

import android.support.test.InstrumentationRegistry;

/**
 * Created by ppmessage on 5/16/16.
 */
public class Global {

    public static final String TEST_APP_UUID = "c56adc0a-1b54-11e6-bc9f-acbc327f19e9";

    public static synchronized PPComSDK getPPComSDK(String email) {
        PPComSDK sdk = PPComSDK.getInstance();
        sdk.init(new PPComSDKConfiguration
                        .Builder(InstrumentationRegistry.getContext())
                        .setAppUUID(TEST_APP_UUID)
                        .setUserEmail(email)
                        .build()
        );
        return sdk;
    }

    public static synchronized PPComSDK getPPComSDK() {
        return getPPComSDK(null);
    }

}
