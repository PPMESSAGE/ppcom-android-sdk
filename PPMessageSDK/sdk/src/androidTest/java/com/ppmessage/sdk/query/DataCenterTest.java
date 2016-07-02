package com.ppmessage.sdk.query;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ppmessage.sdk.Global;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.query.DataCenter;
import com.ppmessage.sdk.core.query.IQuery;

import org.hamcrest.Matchers;
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
public class DataCenterTest {

    final CountDownLatch signal = new CountDownLatch(1);
    final CountDownLatch conversationSignal = new CountDownLatch(1);
    private PPMessageSDK sdk;

    @Before
    public void setup() {
        sdk = Global.getPPMessageSDK();
    }

    @Test
    public void testDataCenter() {

        Context context = InstrumentationRegistry.getContext();
        DataCenter dataCenter = new DataCenter(sdk, context);
        dataCenter.queryUser(Global.TEST_PPCOM_USER_UUID, new IQuery.OnQueryCallback() {
            @Override
            public void onCompleted(Object object) {
                Assert.assertThat(object, Matchers.notNullValue());

                User user = (User) object;
                Assert.assertThat(user.getUuid(), Matchers.is(Global.TEST_PPCOM_USER_UUID));

                signal.countDown();
            }
        });

        try {
            signal.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testConversationData() {
        final Context context = InstrumentationRegistry.getContext();
        DataCenter dataCenter = new DataCenter(sdk, context);

        dataCenter.queryConversation(Global.TEST_PPCOM_CONVERSATION_UUID, new IQuery.OnQueryCallback() {
            @Override
            public void onCompleted(Object object) {
                Assert.assertThat(object, Matchers.notNullValue());

                Conversation conversation = (Conversation)object;
                Assert.assertThat(conversation.getConversationUUID(), Matchers.is(Global.TEST_PPCOM_CONVERSATION_UUID));

                signal.countDown();
            }
        });

        try {
            conversationSignal.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
