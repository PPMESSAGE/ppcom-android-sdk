package com.ppmessage.sdk.utils;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ppmessage.sdk.Global;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.utils.TxtLoader;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by ppmessage on 5/6/16.
 */
@RunWith(AndroidJUnit4.class)
public class TxtLoaderTest {

    final CountDownLatch signal = new CountDownLatch(1);

    @Test
    public void testTextDownload() {
        Global.getPPMessageSDK();

        final TxtLoader txtLoader = new TxtLoader();
        txtLoader.loadTxt("b2cced63-06e0-11e6-b73b-acbc327f19e9", new TxtLoader.OnTxtLoadEvent() {
            @Override
            public void onCompleted(String text) {
                Assert.assertThat(text, Matchers.notNullValue());
                signal.countDown();
            }
        });

        try {
            signal.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
