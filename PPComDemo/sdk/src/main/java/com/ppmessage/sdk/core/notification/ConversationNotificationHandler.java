package com.ppmessage.sdk.core.notification;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.query.IQuery;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppmessage on 5/9/16.
 */
public class ConversationNotificationHandler implements INotificationHandler {

    private PPMessageSDK sdk;

    public ConversationNotificationHandler(PPMessageSDK sdk) {
        this.sdk = sdk;
    }

    @Override
    public void handle(JSONObject message, OnNotificationHandleEvent e) {
        asyncFindConversation(findConversationUUID(message), e);
    }

    private String findConversationUUID(JSONObject message) {
        try {
            JSONObject extraObject = message.getJSONObject("extra");
            if (extraObject != null) {
                return extraObject.has("conversation_uuid") ? extraObject.getString("conversation_uuid") : null;
            }
        } catch (JSONException e) {
            L.e(e);
        }

        return null;
    }

    private void asyncFindConversation(String conversationUUID,
                                       final OnNotificationHandleEvent e) {
        if (conversationUUID == null) {
            if (e != null) e.onCompleted(INotification.EVENT_CONVERSATION, null);
            return;
        }

        sdk.getDataCenter().queryConversation(conversationUUID, new IQuery.OnQueryCallback() {
            @Override
            public void onCompleted(Object object) {
                if (e != null) e.onCompleted(INotification.EVENT_CONVERSATION, object);
            }
        });
    }

}
