package com.ppmessage.sdk.query;

import android.support.test.runner.AndroidJUnit4;

import com.ppmessage.sdk.Global;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.query.IQuery;
import com.ppmessage.sdk.core.query.IUpdate;

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
public class DBUpdateTest {

    final CountDownLatch signal = new CountDownLatch(2);
    final PPMessageSDK sdk = Global.getPPMessageSDK();

    @Before
    public void setup() {
        try {
            signal.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDBUpdate() {
        final User testUser = Global.getUser();
        sdk.getDataCenter().updateUser(testUser, new IUpdate.OnUpdateCallback() {

            @Override
            public void onCompleted(Object object) {

                sdk.getDataCenter().queryUser(testUser.getUuid(), new IQuery.OnQueryCallback() {
                    @Override
                    public void onCompleted(Object object) {
                        Assert.assertThat(object, Matchers.notNullValue());
                        Assert.assertThat(object instanceof User, Matchers.is(true));
                        Assert.assertThat(((User)object).getUuid(), Matchers.is(testUser.getUuid()));

                        signal.countDown();
                    }
                });

            }

        });
    }

    @Test
    public void testConversationDBUpdate() {
        // Build fake conversation
        final Conversation conversation = new Conversation();
        conversation.setConversationUUID(Global.TEST_PPCOM_CONVERSATION_UUID);
        conversation.setConversationName("NAME");
        conversation.setConversationIcon("ICON");

        sdk.getDataCenter().updateConversation(conversation, new IUpdate.OnUpdateCallback() {
            @Override
            public void onCompleted(Object object) {

                sdk.getDataCenter().queryConversation(conversation.getConversationUUID(), new IQuery.OnQueryCallback() {
                    @Override
                    public void onCompleted(Object object) {
                        Assert.assertThat(object, Matchers.notNullValue());
                        Assert.assertThat(object instanceof Conversation, Matchers.is(true));
                        Assert.assertThat(((Conversation)object).getConversationUUID(), Matchers.is(conversation.getConversationUUID()));

                        signal.countDown();
                    }
                });

            }
        });
    }

}
