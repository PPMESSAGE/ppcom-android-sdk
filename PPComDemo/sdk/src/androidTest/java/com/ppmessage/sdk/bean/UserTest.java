package com.ppmessage.sdk.bean;

import android.support.test.runner.AndroidJUnit4;

import com.ppmessage.sdk.core.bean.common.User;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by ppmessage on 5/10/16.
 */
@RunWith(AndroidJUnit4.class)
public class UserTest {

    private JSONObject testUserJson;

    private static final String TEST_USER_UUID = "4cd2cd61-1117-11e6-b15a-acbc327f19e9";
    private static final String TEST_USER_ICON = "http://192.168.0.206:8080/identicon/4cd2cd61-1117-11e6-b15a-acbc327f19e9.png";
    private static final String TEST_USER_NAME = "Local Area.User";

    @Before
    public void setup() {
        testUserJson = getUserJsonObject();
    }

    @Test
    public void testUserParse() {
        User user = User.parse(testUserJson);
        Assert.assertThat(user, Matchers.notNullValue());
        Assert.assertThat(user.getUuid(), Matchers.is(TEST_USER_UUID));
        Assert.assertThat(user.getIcon(), Matchers.is(TEST_USER_ICON));
        Assert.assertThat(user.getName(), Matchers.is(TEST_USER_NAME));
    }

    public JSONObject getUserJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uuid", TEST_USER_UUID);
            jsonObject.put("user_icon", TEST_USER_ICON);
            jsonObject.put("user_name", TEST_USER_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
