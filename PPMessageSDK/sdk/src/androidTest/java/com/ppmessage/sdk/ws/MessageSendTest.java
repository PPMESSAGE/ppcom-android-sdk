package com.ppmessage.sdk.ws;

import android.support.test.runner.AndroidJUnit4;

import com.ppmessage.sdk.Global;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.api.IToken;
import com.ppmessage.sdk.core.api.OnAPIRequestCompleted;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.bean.message.IPPMessageMediaItem;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.bean.message.PPMessageTxtMediaItem;
import com.ppmessage.sdk.core.notification.INotification;
import com.ppmessage.sdk.core.notification.WSMessageAckNotificationHandler;
import com.ppmessage.sdk.core.utils.Utils;

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
 * Created by ppmessage on 5/11/16.
 */
@RunWith(AndroidJUnit4.class)
public class MessageSendTest {

    final PPMessageSDK sdk = Global.getPPMessageSDK();
    final CountDownLatch signal = new CountDownLatch(2);

    @Before
    public void setup() {

    }

    @Test
    public void testMessageSend() {
        sdk.getToken().getApiToken(Global.TEST_APP_UUID, new IToken.OnRequestTokenEvent() {
            @Override
            public void onGetToken(final String accessToken) {
                configNotification(accessToken);
            }
        });
        try {
            signal.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void configNotification(final String accessToken) {
        final User user = new User();
        user.setUuid(Global.TEST_PPCOM_USER_UUID);
        user.setDeviceUUID(Global.TEST_PPCOM_USER_DEVICE_UUID);
        user.setServiceUser(false);

        sdk.getNotification().config(new INotification.Config() {
            @Override
            public String getAppUUID() {
                return Global.TEST_APP_UUID;
            }

            @Override
            public String getApiToken() {
                return accessToken;
            }

            @Override
            public User getActiveUser() {
                return user;
            }
        });
        sdk.getNotification().start();
        sdk.getNotification().addListener(new INotification.SimpleNotificationEvent() {

            @Override
            public int getInterestedEvent() {
                return INotification.EVENT_MSG_SEND_OK | INotification.EVENT_MSG_SEND_ERROR | INotification.EVENT_AUTH | INotification.EVENT_MESSAGE;
            }

            @Override
            public void onAuthInfoArrived(Object authInfo) {
                super.onAuthInfoArrived(authInfo);

//                sdk.getNotification().sendMessage(buildTextMessage("THIS IS SEND FROM ANDROID SDK"));
                sdk.getNotification().sendMessage(buildTextMessage(
                        " THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT" +
                        " THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT" +
                        " THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT" +
                        " THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT" +
                        " THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT" +
                        " THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT" +
                        " THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT" +
                        " THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT THIS IS LARGE TEXT"));

                L.d("onAuthInfoArrived");
            }

            @Override
            public void onMessageSendOk(WSMessageAckNotificationHandler.MessageSendResult messageSendResult) {
                super.onMessageSendOk(messageSendResult);

                signal.countDown();
                L.d("onMessageSendOK");
            }

            @Override
            public void onMessageSendError(WSMessageAckNotificationHandler.MessageSendResult messageSendResult) {
                super.onMessageSendError(messageSendResult);

                signal.countDown();
                L.d("onMessageSendError");
            }

            @Override
            public void onMessageInfoArrived(PPMessage message) {
                super.onMessageInfoArrived(message);

                L.d("onMessageInfoArrived: %s", message);
            }
        });
    }

    private PPMessage buildTextMessage(String text) {

        Conversation conversation = new Conversation();
        conversation.setConversationUUID(Global.TEST_PPCOM_CONVERSATION_UUID);
        conversation.setConversationType("P2S");

        User fromUser = new User();
        fromUser.setUuid(Global.TEST_PPCOM_USER_UUID);

        return new PPMessage.Builder()
                .setFromUser(fromUser)
                .setConversation(conversation)
                .setMessageBody(text)
                .build();
    }

}
