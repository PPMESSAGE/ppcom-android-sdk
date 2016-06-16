package com.ppmessage.sdk.ws;

import android.support.test.runner.AndroidJUnit4;

import com.ppmessage.sdk.Global;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.message.IPPMessageMediaItem;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.bean.message.PPMessageFileMediaItem;
import com.ppmessage.sdk.core.bean.message.PPMessageImageMediaItem;
import com.ppmessage.sdk.core.bean.message.PPMessageTxtMediaItem;
import com.ppmessage.sdk.core.notification.INotificationHandler;
import com.ppmessage.sdk.core.notification.NotificationHandlerFactory;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Time;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by ppmessage on 5/10/16.
 */
@RunWith(AndroidJUnit4.class)
public class NotificationMessageTest {

    final CountDownLatch signal = new CountDownLatch(4);
    final PPMessageSDK sdk = Global.getPPMessageSDK();
    NotificationHandlerFactory factory = new NotificationHandlerFactory(sdk);

    @Before
    public void setup() {
        try {
            signal.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testWSTextMessageArrived() {
        factory.handle(getWSMessageJsonObject(Global.Message.getWSTextMessageJsonObject()).toString(), new INotificationHandler.OnNotificationHandleEvent() {
            @Override
            public void onCompleted(int eventType, Object obj) {
                Assert.assertThat(obj, Matchers.notNullValue());
                Assert.assertThat((obj instanceof PPMessage), Matchers.is(true));

                PPMessage message = (PPMessage) obj;
                Assert.assertThat(message.isError(), Matchers.is(false));
                Assert.assertThat(message.getDirection(), Matchers.is(PPMessage.DIRECTION_INCOMING));
                Assert.assertThat(message.getMediaItem(), Matchers.nullValue());

                signal.countDown();
            }
        });
    }

    @Test
    public void testWSTxtMessageArrived() {
        factory.handle(getWSMessageJsonObject(Global.Message.getWSTxtMessageJsonObject()).toString(), new INotificationHandler.OnNotificationHandleEvent() {
            @Override
            public void onCompleted(int eventType, Object obj) {
                Assert.assertThat(obj, Matchers.notNullValue());
                Assert.assertThat(obj instanceof PPMessage, Matchers.is(true));

                PPMessage message = (PPMessage) obj;
                Assert.assertThat(message.isError(), Matchers.is(false));
                Assert.assertThat(message.getMediaItem(), Matchers.notNullValue());
                Assert.assertThat(message.getMediaItem() instanceof PPMessageTxtMediaItem, Matchers.is(true));
                Assert.assertThat(((PPMessageTxtMediaItem)message.getMediaItem()).getTextContent(), Matchers.notNullValue());

                signal.countDown();
            }
        });
    }

    @Test
    public void testWSImageMessageArrived() {
        factory.handle(getWSMessageJsonObject(Global.Message.getWSImageMessageJsonObject()).toString(), new INotificationHandler.OnNotificationHandleEvent() {
            @Override
            public void onCompleted(int eventType, Object obj) {
                Assert.assertThat(obj, Matchers.notNullValue());

                PPMessage message = (PPMessage) obj;
                IPPMessageMediaItem mediaItem = message.getMediaItem();
                Assert.assertThat(message.isError(), Matchers.is(false));
                Assert.assertThat(mediaItem, Matchers.notNullValue());
                Assert.assertThat(mediaItem instanceof PPMessageImageMediaItem, Matchers.is(true));

                signal.countDown();
            }
        });
    }

    @Test
    public void testWSFileMessageArrived() {
        factory.handle(getWSMessageJsonObject(Global.Message.getWSFileMessageJsonObject()).toString(), new INotificationHandler.OnNotificationHandleEvent() {
            @Override
            public void onCompleted(int eventType, Object obj) {
                Assert.assertThat(obj, Matchers.notNullValue());

                PPMessage message = (PPMessage) obj;
                IPPMessageMediaItem mediaItem = message.getMediaItem();
                Assert.assertThat(message.isError(), Matchers.is(false));
                Assert.assertThat(mediaItem, Matchers.notNullValue());
                Assert.assertThat(mediaItem instanceof PPMessageFileMediaItem, Matchers.is(true));

                signal.countDown();
            }
        });
    }

    private JSONObject getWSMessageJsonObject(JSONObject messageJsonObject) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg", messageJsonObject);
            jsonObject.put("type", "MSG");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
