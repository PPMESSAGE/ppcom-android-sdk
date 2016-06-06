package com.ppmessage.ppcomlib.model;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.api.OnAPIRequestCompleted;
import com.ppmessage.sdk.core.bean.common.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ppmessage on 5/18/16.
 */
public class ConversationMemberModel {

    public interface OnGetConversationMembersEvent {
        void onCompleted(List<User> userList);
    }

    private PPComSDK sdk;
    private PPMessageSDK messageSDK;
    private Map<String, List<User>> membersMap;

    public ConversationMemberModel(PPComSDK sdk) {
        this.sdk = sdk;
        this.messageSDK = sdk.getConfiguration().getMessageSDK();
        membersMap = new HashMap<>();
    }

    public void getMembers(String conversationUUID, final OnGetConversationMembersEvent event) {
        if (membersMap.containsKey(conversationUUID)) {
            List<User> cachedUsers = membersMap.get(conversationUUID);
            if (event != null) {
                event.onCompleted(cachedUsers);
            }
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("app_uuid", sdk.getConfiguration().getAppUUID());
            jsonObject.put("conversation_uuid", conversationUUID);
        } catch (JSONException e) {
            L.e(e);
        }

        // get it from http
        messageSDK.getAPI().getConversationUserList(jsonObject, new OnAPIRequestCompleted() {
            @Override
            public void onResponse(JSONObject jsonResponse) {
                ConversationMemberModel.this.onResponse(jsonResponse, event);
            }

            @Override
            public void onCancelled() {
                if (event != null) event.onCompleted(new ArrayList<User>());
            }

            @Override
            public void onError(int errorCode) {
                if (event != null) event.onCompleted(new ArrayList<User>());
            }
        });

    }

    private void onResponse(JSONObject jsonResponse, final OnGetConversationMembersEvent event) {
        try {
            if (jsonResponse.getInt("error_code") != 0) {
                if (event != null) {
                    event.onCompleted(new ArrayList<User>());
                }
                return;
            }

            JSONArray jsonArray = jsonResponse.getJSONArray("list");
            final List<User> userList = new ArrayList<>(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userJsonObject = jsonArray.getJSONObject(i);
                userList.add(User.parse(userJsonObject));
            }

            if (event != null) {
                event.onCompleted(userList);
            }

        } catch (JSONException e) {
            L.e(e);

            if (event != null) {
                event.onCompleted(new ArrayList<User>());
            }
        }
    }

}
