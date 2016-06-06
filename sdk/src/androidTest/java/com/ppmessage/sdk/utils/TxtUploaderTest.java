package com.ppmessage.sdk.utils;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ppmessage.sdk.Global;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.utils.TxtUploader;
import com.ppmessage.sdk.core.utils.Uploader;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by ppmessage on 5/6/16.
 */
@RunWith(AndroidJUnit4.class)
public class TxtUploaderTest {

    final CountDownLatch signal = new CountDownLatch(1);

    @Test
    public void testUploadTxt() {
        Global.getPPMessageSDK();

        TxtUploader uploader = new TxtUploader();
        uploader.upload("123", new Uploader.OnUploadingListener() {
            @Override
            public void onError(Exception e) {
                signal.countDown();
            }

            @Override
            public void onComplected(JSONObject response) {
                Assert.assertThat(response, Matchers.notNullValue());
                Assert.assertThat(response.has("fuuid"), Matchers.is(true));
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
