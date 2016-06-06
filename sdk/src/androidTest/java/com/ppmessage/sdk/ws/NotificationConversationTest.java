package com.ppmessage.sdk.ws;

import android.support.test.runner.AndroidJUnit4;

import com.ppmessage.sdk.Global;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.notification.INotificationHandler;
import com.ppmessage.sdk.core.notification.NotificationHandlerFactory;

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
 * Created by ppmessage on 5/10/16.
 */
@RunWith(AndroidJUnit4.class)
public class NotificationConversationTest {

    final CountDownLatch signal = new CountDownLatch(1);

    @Test
    public void onConversationMsgArrived() {
        PPMessageSDK sdk = Global.getPPMessageSDK();
        NotificationHandlerFactory handlerFactory = new NotificationHandlerFactory(sdk);
        handlerFactory.handle(getConversationWSMessageJsonObject().toString(), new INotificationHandler.OnNotificationHandleEvent() {
            @Override
            public void onCompleted(int eventType, Object obj) {
                Assert.assertThat(obj, Matchers.notNullValue());
                Assert.assertThat((obj instanceof Conversation), Matchers.is(true));

                signal.countDown();
            }
        });

        try {
            signal.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private JSONObject getConversationWSMessageJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("what", "CONVERSATION");
            jsonObject.put("type", "ACK");
            jsonObject.put("code", 0);

            JSONObject extraObject = new JSONObject();
            extraObject.put("conversation_uuid", Global.TEST_PPCOM_CONVERSATION_UUID);

            jsonObject.put("extra", extraObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

}
