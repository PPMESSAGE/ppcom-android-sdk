package com.ppmessage.ppcomlib;

import android.support.test.runner.AndroidJUnit4;

import com.ppmessage.ppcomlib.model.ConversationsModel;
import com.ppmessage.ppcomlib.services.PPComStartupHelper;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.bean.common.Conversation;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by ppmessage on 5/16/16.
 */
@RunWith(AndroidJUnit4.class)
public class ConversationsModelTest {

    final CountDownLatch signal = new CountDownLatch(1);

    @Test
    public void testConversations() {
        final PPComSDK sdk = Global.getPPComSDK();
        sdk.getStartupHelper().startUp(new PPComStartupHelper.OnStartupCallback() {

            @Override
            public void onSuccess() {

                ConversationsModel conversationsModel = new ConversationsModel(sdk);
                conversationsModel.asyncGetConversations(new ConversationsModel.OnGetConversationsEvent() {
                    @Override
                    public void onCompleted(List<Conversation> conversationList) {
                        Assert.assertThat(conversationList, Matchers.notNullValue());
                        Assert.assertThat(conversationList.isEmpty(), Matchers.is(false));
                        L.d("Conversation's size:%d", conversationList.size());

                        signal.countDown();
                    }
                });

            }

            @Override
            public void onError(PPComSDKException exception) {
                Assert.assertThat(false, Matchers.is(true)); // shouldn't reach here

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
