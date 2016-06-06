package com.ppmessage.sdk.model;

import android.support.test.runner.AndroidJUnit4;

import com.ppmessage.sdk.Global;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.model.HistoryPageIndex;
import com.ppmessage.sdk.core.model.MessageHistorysModel;
import com.ppmessage.sdk.core.model.MessagesModel;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by ppmessage on 5/17/16.
 */
@RunWith(AndroidJUnit4.class)
public class MessageModelTest {

    final static int PAGE_SIZE = 3;
    final CountDownLatch signal = new CountDownLatch(2);

    @Test
    public void testMessagesModel() {
        PPMessageSDK sdk = Global.getPPMessageSDK();
        final MessagesModel messagesModel = new MessagesModel(sdk);

        messagesModel.getHistoryLoader().loadHistorys(buildHistoryRequestParam(0, null), new MessageHistorysModel.OnLoadHistoryEvent() {

            @Override
            public void onCompleted(HistoryPageIndex pageIndex, List<PPMessage> messageList) {
                Assert.assertThat(messageList, Matchers.notNullValue());
                testTimestampInAscendingOrder(messageList);

                List<PPMessage> afterAddedMessages = messagesModel.addAll(0, messageList);
                Assert.assertThat(afterAddedMessages.size(), Matchers.is(messageList.size())); // has the same size

                loadSecondPage(messagesModel, pageIndex);
            }

        });

        try {
            signal.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void loadSecondPage(final MessagesModel messagesModel, final HistoryPageIndex pageIndex) {
        // Load the second page
        messagesModel.getHistoryLoader().loadHistorys(buildHistoryRequestParam(1, pageIndex.getMaxUUID()), new MessageHistorysModel.OnLoadHistoryEvent() {
            @Override
            public void onCompleted(HistoryPageIndex pageIndex1, List<PPMessage> messageList) {

                Assert.assertThat(messageList, Matchers.notNullValue());
                Assert.assertThat(messageList.size() <= PAGE_SIZE, Matchers.is(true));
                Assert.assertThat(pageIndex1.getMaxUUID(), Matchers.is(pageIndex.getMaxUUID())); // Assert second page's maxUUId === first page's maxUUID
                testTimestampInAscendingOrder(messageList);
                List<PPMessage> afterAddedMessages = messagesModel.addAll(0, messageList);
                Assert.assertThat(afterAddedMessages.size(), Matchers.is(messageList.size()));

                Assert.assertThat(messagesModel.getMessageList(Global.TEST_PPCOM_CONVERSATION_UUID).size() <= PAGE_SIZE * 2, Matchers.is(true));
                testTimestampInAscendingOrder(messagesModel.getMessageList(Global.TEST_PPCOM_CONVERSATION_UUID));

                signal.countDown();

            }
        });
    }

    private void testTimestampInAscendingOrder(List<PPMessage> messageList) {
        if (messageList == null || messageList.isEmpty()) return;

        PPMessage last = null;
        for (int i = 0; i < messageList.size(); i++) {
            if (last == null) {
                last = messageList.get(i);
            } else {
                long timestampB = messageList.get(i).getTimestamp();
                long timestampA = last.getTimestamp();
                Assert.assertThat(timestampA <= timestampB, Matchers.is(true));
            }
        }
    }

    private MessageHistorysModel.MessageHistoryRequestParam buildHistoryRequestParam(int pageOffset, String maxUUID) {
        return new MessageHistorysModel.MessageHistoryRequestParam(
                Global.TEST_PPCOM_CONVERSATION_UUID,
                maxUUID,
                PAGE_SIZE,
                pageOffset
        );
    }

}
