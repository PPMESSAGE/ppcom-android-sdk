package com.ppmessage.sdk.bean;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ppmessage.sdk.Global;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.message.PPMessage;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by ppmessage on 5/9/16.
 */
@RunWith(AndroidJUnit4.class)
public class PPMessageTest {

    private JSONObject testPPMessgaeWSJson;
    private final CountDownLatch signal = new CountDownLatch(1);
    private PPMessageSDK sdk;

    @Before
    public void setup() {
        sdk = Global.getPPMessageSDK();
        testPPMessgaeWSJson = Global.Message.getWSTextMessageJsonObject();
    }

    @Test
    public void testPPMessageParse() {

        PPMessage.asyncParse(sdk, testPPMessgaeWSJson, new PPMessage.onParseListener() {
            @Override
            public void onCompleted(PPMessage message) {

                Assert.assertThat(message.isError(), Matchers.is(false));
                Assert.assertThat(message.getConversation(), Matchers.notNullValue());
                Assert.assertThat(message.getFromUser(), Matchers.notNullValue());
                Assert.assertThat(message.getMediaItem(), Matchers.nullValue());
                Assert.assertThat(message.getMessageSubType(), Matchers.is(PPMessage.TYPE_TEXT));
                Assert.assertThat(message.getDirection(), Matchers.is(PPMessage.DIRECTION_INCOMING));

                signal.countDown();
            }
        });

        try {
            signal.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
