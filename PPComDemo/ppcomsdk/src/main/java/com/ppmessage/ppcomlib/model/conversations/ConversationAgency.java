package com.ppmessage.ppcomlib.model.conversations;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.api.OnAPIRequestCompleted;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * 1. create conversation by group_uuid
 * 2. create conversation by user_uuid
 *
 * Created by ppmessage on 5/18/16.
 */
public class ConversationAgency {

    private static final String LOG_USER_EMPTY = "[ConversationAgency] can not create conversation with empty user";
    private static final String DEFAULT_CONVERSATION_TYPE = "P2S";

    public interface OnCreateConversationEvent {
        void onCompleted(Conversation conversation);
    }

    private PPComSDK sdk;

    public ConversationAgency(PPComSDK sdk) {
        this.sdk = sdk;
    }

    public void createGroupConversation(String groupUUID, final OnCreateConversationEvent event) {
        createConversation(groupUUID, null, event);
    }

    public void createUserConversation(String userUUID, final OnCreateConversationEvent event) {
        createConversation(null, userUUID, event);
    }

    private void createConversation(String groupUUID, String userUUID, final OnCreateConversationEvent event) {
        PPMessageSDK messageSDK = sdk.getConfiguration().getMessageSDK();

        if (messageSDK.getNotification().getConfig().getActiveUser() == null) {
            L.w(LOG_USER_EMPTY);
            if (event == null) {
                event.onCompleted(null);
            }
            return;
        }

        final User activeUser = messageSDK.getNotification().getConfig().getActiveUser();
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("app_uuid", sdk.getConfiguration().getAppUUID());
            jsonObject.put("user_uuid", activeUser.getUuid());
            jsonObject.put("device_uuid", activeUser.getDeviceUUID());
            jsonObject.put("conversation_type", DEFAULT_CONVERSATION_TYPE);
            if (groupUUID != null) {
                jsonObject.put("group_uuid", groupUUID);
            } else if (userUUID != null) {
                JSONArray memberList = new JSONArray();
                memberList.put(userUUID);
                jsonObject.put("member_list", memberList);
            }
        } catch (JSONException e) {
            L.e(e);
        }

        messageSDK.getAPI().createPPComConversation(jsonObject, new OnAPIRequestCompleted() {
            @Override
            public void onResponse(JSONObject jsonResponse) {
                if (Utils.isJsonResponseEmpty(jsonResponse)) {
                    if (event != null) event.onCompleted(null);
                } else {
                    Conversation conversation = Conversation.parse(sdk.getConfiguration().getMessageSDK(), jsonResponse);
                    if (event != null) event.onCompleted(conversation);
                }
            }

            @Override
            public void onCancelled() {
                if (event != null) event.onCompleted(null);
            }

            @Override
            public void onError(int errorCode) {
                if (event != null) event.onCompleted(null);
            }
        });

    }

}
