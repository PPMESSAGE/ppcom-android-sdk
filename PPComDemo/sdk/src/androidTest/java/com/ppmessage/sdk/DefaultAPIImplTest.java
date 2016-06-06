package com.ppmessage.sdk;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.api.IToken;
import com.ppmessage.sdk.core.api.OnAPIRequestCompleted;
import com.ppmessage.sdk.core.api.OnHttpRequestCompleted;
import com.ppmessage.sdk.core.api.Token;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertThat;

/**
 * Created by ppmessage on 5/6/16.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class DefaultAPIImplTest {

    private PPMessageSDK sdk;
    private static final String TEST_APP_UUID = "b6000733-06b6-11e6-8cc6-acbc327f19e9";

    private final CountDownLatch signal = new CountDownLatch(1);
    private final CountDownLatch accessTokenSignal = new CountDownLatch(1);

    @Before
    public void createAPI() {
        sdk = Global.getPPMessageSDK();
    }

    @Test
    public void assertWeCanGetAccessToken() {
        IToken token = new Token();
        token.getApiToken(sdk.getAppUUID(), new IToken.OnRequestTokenEvent() {
            @Override
            public void onGetToken(String accessToken) {
                assertThat(accessToken, Matchers.notNullValue());
                accessTokenSignal.countDown();
            }
        });
        try {
            accessTokenSignal.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void assertWeCanCalledAPI() {
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("app_uuid", sdk.getAppUUID());
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
                signal.countDown();
            }

            @Override
            public void onCancelled() {
                signal.countDown();
            }

            @Override
            public void onError(int errorCode) {
                signal.countDown();
            }
        });
        try {
            signal.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
