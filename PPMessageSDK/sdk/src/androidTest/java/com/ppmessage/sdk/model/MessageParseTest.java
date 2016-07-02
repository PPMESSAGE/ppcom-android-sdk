package com.ppmessage.sdk.model;

import android.support.test.runner.AndroidJUnit4;

import com.ppmessage.sdk.Global;
import com.ppmessage.sdk.core.bean.message.PPMessage;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by ppmessage on 6/23/16.
 */
@RunWith(AndroidJUnit4.class)
public class MessageParseTest {

    private final CountDownLatch signal = new CountDownLatch(1);
    private JSONObject messageJSONObject;

    @Before
    public void buildMessage() {
        messageJSONObject = new JSONObject();
        try {
            messageJSONObject.put("ci", "445ffdec-3914-11e6-811e-02287b8c0ebf");
            messageJSONObject.put("ft", "DU");
            messageJSONObject.put("tt", "AP");
            messageJSONObject.put("bo", "TEST BODY");
            messageJSONObject.put("ts", 1466667053.489856);
            messageJSONObject.put("mt", "NOTI");
            messageJSONObject.put("tl", "null");
            messageJSONObject.put("ms", "TEXT");
            messageJSONObject.put("ti", "445ffdec-3914-11e6-811e-02287b8c0ebf");
            messageJSONObject.put("fi", "9477b0fa-3913-11e6-b4b8-02287b8c0ebf");
            messageJSONObject.put("id", "79155943-28b5-45b2-dcd3-ab279ac8e906");
            messageJSONObject.put("ct", "P2S");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMessageWithoutFromUserParse() {
        PPMessage.asyncParse(Global.getPPMessageSDK(), messageJSONObject, new PPMessage.onParseListener() {
            @Override
            public void onCompleted(PPMessage message) {
                Assert.assertNotNull(message);
                Assert.assertNotNull(message.getFromUser());
                Assert.assertNotNull(message.getFromUser().getUuid());
                Assert.assertNotNull(message.getFromUser().getName());
                Assert.assertNotNull(message.getFromUser().getIcon());
                Assert.assertEquals(message.getFromUser().getUuid(), messageJSONObject.optString("fi"));

                signal.countDown();
            }
        });

        try {
            signal.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
