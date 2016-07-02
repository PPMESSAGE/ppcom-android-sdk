package com.ppmessage.ppcomlib;

import android.support.test.runner.AndroidJUnit4;

import com.ppmessage.ppcomlib.services.PPComStartupHelper;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by ppmessage on 5/16/16.
 */
@RunWith(AndroidJUnit4.class)
public class PPComSDKEmailUserStartupTest {

    private static final String TEST_EMAIL = "test.email@google.com";

    final CountDownLatch signal = new CountDownLatch(1);

    @Test
    public void testStartup() {
        PPComSDK sdk = Global.getPPComSDK(TEST_EMAIL);

        sdk.getStartupHelper().startUp(new PPComStartupHelper.OnStartupCallback() {
            @Override
            public void onSuccess() {
                Assert.assertThat(true, Matchers.is(true));
                signal.countDown();
            }

            @Override
            public void onError(PPComSDKException exception) {
                Assert.assertThat(false, Matchers.is(true));
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
