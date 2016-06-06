package com.ppmessage.sdk.ws;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ppmessage.sdk.Global;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.api.IToken;
import com.ppmessage.sdk.core.api.Token;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.notification.INotification;

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
 * Created by ppmessage on 5/9/16.
 */
@RunWith(AndroidJUnit4.class)
public class NotificationTest {

    private PPMessageSDK sdk;
    private INotification notification;

    final CountDownLatch signal = new CountDownLatch(1);

    @Before
    public void setupSDK() {
        sdk = Global.getPPMessageSDK();

        notification = sdk.getNotification();
    }

    @Test
    public void testNotification() {
        notification.addListener(new INotification.SimpleNotificationEvent() {

            @Override
            public int getInterestedEvent() {
                return INotification.EVENT_AUTH | INotification.EVENT_UNKNOWN;
            }

            @Override
            public void onAuthInfoArrived(Object authInfo) {
                super.onAuthInfoArrived(authInfo);

                L.d("onAuthInfoArrived:%s", authInfo);
                Assert.assertThat(authInfo, Matchers.notNullValue());
                signal.countDown();
            }

            @Override
            public void onUnknownInfoArrived(Object unknownInfo) {
                super.onUnknownInfoArrived(unknownInfo);

                L.d("Should not be here -> onUnknownInfoArrived:%s", unknownInfo);
                Assert.assertThat(false, Matchers.is(true));
            }
        });

        sdk.getToken().getApiToken(Global.TEST_APP_UUID, new IToken.OnRequestTokenEvent() {

            @Override
            public void onGetToken(final String accessToken) {

                final User user = new User();
                user.setDeviceUUID(Global.TEST_PPCOM_USER_DEVICE_UUID);
                user.setUuid(Global.TEST_PPCOM_USER_UUID);
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

                notification.start();

            }
        });

        try {
            signal.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
