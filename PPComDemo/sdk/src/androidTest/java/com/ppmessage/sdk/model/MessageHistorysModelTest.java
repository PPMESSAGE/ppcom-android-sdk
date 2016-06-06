package com.ppmessage.sdk.model;

import android.support.test.runner.AndroidJUnit4;

import com.ppmessage.sdk.Global;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.message.IPPMessageMediaItem;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.bean.message.PPMessageTxtMediaItem;
import com.ppmessage.sdk.core.model.HistoryPageIndex;
import com.ppmessage.sdk.core.model.MessageHistorysModel;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by ppmessage on 5/17/16.
 */
@RunWith(AndroidJUnit4.class)
public class MessageHistorysModelTest {

    final CountDownLatch signal = new CountDownLatch(1);

    @Test
    public void testMessageHistorys() {
        PPMessageSDK sdk = Global.getPPMessageSDK();

        MessageHistorysModel historysModel = new MessageHistorysModel(sdk);
        MessageHistorysModel.MessageHistoryRequestParam requestParam = new MessageHistorysModel.MessageHistoryRequestParam(Global.TEST_PPCOM_CONVERSATION_UUID);

        historysModel.loadHistorys(requestParam, new MessageHistorysModel.OnLoadHistoryEvent() {

            @Override
            public void onCompleted(HistoryPageIndex pageIndex, List<PPMessage> messageList) {
                Assert.assertThat(messageList, Matchers.notNullValue());
                Assert.assertThat(messageList.isEmpty(), Matchers.is(false));

                for (PPMessage msg : messageList) {
                    Assert.assertThat(msg.isError(), Matchers.is(false));
                    Assert.assertThat(msg.getFromUser(), Matchers.notNullValue());

                    if (!msg.getMessageSubType().equals(PPMessage.TYPE_TEXT)) {
                        IPPMessageMediaItem mediaItem = msg.getMediaItem();
                        Assert.assertThat(mediaItem, Matchers.notNullValue());

                        if (msg.getMessageSubType().equals(PPMessage.TYPE_TXT)) {
                            PPMessageTxtMediaItem txtMediaItem = (PPMessageTxtMediaItem) mediaItem;

                            Assert.assertThat(txtMediaItem, Matchers.notNullValue());
                            Assert.assertThat(txtMediaItem.getTextContent(), Matchers.notNullValue());
                        }
                    }
                }

                signal.countDown();
            }

        });

        try {
            signal.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
