package com.ppmessage.sdk.core.query;

import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.api.OnAPIRequestCompleted;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.bean.common.Conversation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppmessage on 5/9/16.
 */
class APIQuery implements IQuery {

    private PPMessageSDK sdk;

    public APIQuery(PPMessageSDK sdk) {
        this.sdk = sdk;
    }

    @Override
    public void queryConversation(String conversationUUID, final OnQueryCallback queryCallback) {

        JSONObject param = new JSONObject();
        try {
            param.put("app_uuid", this.sdk.getAppUUID());
            param.put("user_uuid", this.sdk.getNotification().getConfig().getActiveUser().getUuid());
            param.put("conversation_uuid", conversationUUID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sdk.getAPI().getConversationInfo(param, new OnAPIRequestCompleted() {

            @Override
            public void onResponse(JSONObject jsonResponse) {
                Conversation conversaiton = Conversation.parse(sdk, jsonResponse);
                if (conversaiton != null && queryCallback != null) {
                    queryCallback.onCompleted(conversaiton);
                } else {
                    if (queryCallback != null) queryCallback.onCompleted(null);
                }
            }

            @Override
            public void onCancelled() {
                if (queryCallback != null) queryCallback.onCompleted(null);
            }

            @Override
            public void onError(int errorCode) {
                if (queryCallback != null) queryCallback.onCompleted(null);
            }
        });

    }

    @Override
    public void queryUser(String userUUID, final OnQueryCallback queryCallback) {

        JSONObject param = new JSONObject();
        try {
            param.put("app_uuid", this.sdk.getNotification().getConfig().getAppUUID());
            param.put("user_uuid", this.sdk.getNotification().getConfig().getActiveUser().getUuid());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sdk.getAPI().getUserDetailInfo(param, new OnAPIRequestCompleted() {
            @Override
            public void onResponse(JSONObject jsonResponse) {
                User user = null;
                if (jsonResponse != null) {
                    user = User.parse(jsonResponse);
                }
                if (queryCallback != null) {
                    queryCallback.onCompleted(user);
                }
            }

            @Override
            public void onCancelled() {
                if (queryCallback != null) {
                    queryCallback.onCompleted(null);
                }
            }

            @Override
            public void onError(int errorCode) {
                if (queryCallback != null) {
                    queryCallback.onCompleted(null);
                }
            }
        });

    }

}
