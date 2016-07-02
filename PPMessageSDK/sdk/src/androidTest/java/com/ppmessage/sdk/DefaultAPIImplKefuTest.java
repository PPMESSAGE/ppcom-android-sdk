package com.ppmessage.sdk;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.PPMessageSDKConfiguration;
import com.ppmessage.sdk.core.api.OnAPIRequestCompleted;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by ppmessage on 5/6/16.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class DefaultAPIImplKefuTest {

    private PPMessageSDK sdk;
    private static final String TEST_APP_UUID = Global.TEST_APP_UUID;

    private final CountDownLatch kefuAPISignal = new CountDownLatch(1);

    @Before
    public void createSDK() {
        // 40bd001563085fc35165329ea1ff5c5ecbdbbeef is sha1('123')
        sdk = Global.getPPMessageSDK();
        sdk.init(new PPMessageSDKConfiguration
                .Builder(InstrumentationRegistry.getContext())
                .setServiceUserInfo("dingguijin@gmail.com", "40bd001563085fc35165329ea1ff5c5ecbdbbeef")
                .setEnableLogging(true)
                .setEnableDebugLogging(true)
                .build());
    }

    @Test
    public void assertPPKefuCanCallAPI() {

        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("app_uuid", TEST_APP_UUID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sdk.getAPI().getAppInfo(requestParams, new OnAPIRequestCompleted() {
            @Override
            public void onResponse(JSONObject jsonResponse) {
                assertThat(jsonResponse, Matchers.notNullValue());
                try {
                    int errorCode = jsonResponse.getInt("error_code");
                    assertThat(errorCode, Matchers.is(0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                kefuAPISignal.countDown();
            }

            @Override
            public void onCancelled() {
                kefuAPISignal.countDown();
            }

            @Override
            public void onError(int errorCode) {
                kefuAPISignal.countDown();
            }
        });

        try {
            kefuAPISignal.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
