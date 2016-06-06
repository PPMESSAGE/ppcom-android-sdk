package com.ppmessage.sdk.bean;

import android.support.test.runner.AndroidJUnit4;

import com.ppmessage.sdk.Global;
import com.ppmessage.sdk.core.bean.common.Conversation;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by ppmessage on 5/10/16.
 */
@RunWith(AndroidJUnit4.class)
public class ConversationTest {

    @Test
    public void testConversationParse() {
        Conversation conversation = Conversation.parse(Global.getPPMessageSDK(), getConversationJSONObject());

        Assert.assertThat(conversation, Matchers.notNullValue());
        Assert.assertThat(conversation.getConversationUUID(), Matchers.is(Global.TEST_PPCOM_CONVERSATION_UUID));
        Assert.assertThat(conversation.getConversationIcon(), Matchers.notNullValue());
        Assert.assertThat(conversation.getConversationName(), Matchers.notNullValue());
        Assert.assertThat(conversation.getConversationType(), Matchers.notNullValue());

    }

    public JSONObject getConversationJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uuid", Global.TEST_PPCOM_CONVERSATION_UUID);

            JSONObject conversationData = new JSONObject();
            conversationData.put("conversation_uuid", Global.TEST_PPCOM_CONVERSATION_UUID);
            conversationData.put("conversation_icon", "875977d1-10e6-11e6-b932-acbc327f19e9");
            conversationData.put("conversation_status", "OPEN");
            conversationData.put("conversation_name", "Guijin Ding");
            conversationData.put("user_uuid", "a30ebb35-111d-11e6-80fb-acbc327f19e9");
            jsonObject.put("conversation_data", conversationData);

            jsonObject.put("latest_task", "ec89188e-f0a3-48e6-b63e-cbbd0015dbb8");
            jsonObject.put("group_uuid", "657d168c-0c48-11e6-b25a-acbc327f19e9");
            jsonObject.put("assigned_uuid", "b5fd6b63-06b6-11e6-a042-acbc327f19e9");
            jsonObject.put("conversation_type", "P2S");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
