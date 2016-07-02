package com.ppmessage.sdk.model;

import android.support.test.runner.AndroidJUnit4;

import com.ppmessage.sdk.Global;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.model.UnackedMessagesLoader;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by ppmessage on 5/19/16.
 */
@RunWith(AndroidJUnit4.class)
public class UnackedMessagesLoaderTest {

    @Test
    public void testUnackedMessagesLoader() {
        PPMessageSDK messageSDK = Global.getPPMessageSDK();

        User activeUser = messageSDK.getNotification().getConfig().getActiveUser();
        Assert.assertThat(activeUser, Matchers.notNullValue());
        Assert.assertThat(activeUser.getUuid(), Matchers.notNullValue());
        Assert.assertThat(activeUser.getDeviceUUID(), Matchers.notNullValue());

        Assert.assertThat(messageSDK.getNotification().getConfig().getAppUUID(), Matchers.notNullValue());

        UnackedMessagesLoader messagesLoader = new UnackedMessagesLoader(messageSDK);
        messagesLoader.loadUnackedMessages();
    }

}
