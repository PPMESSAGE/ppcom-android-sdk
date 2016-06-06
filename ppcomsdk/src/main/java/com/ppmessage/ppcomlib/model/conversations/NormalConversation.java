package com.ppmessage.ppcomlib.model.conversations;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.ppcomlib.services.PPComStartupHelper;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.api.OnAPIRequestCompleted;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.bean.common.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Example:
 *
 * <pre>
 *     PPComSDK sdk = PPComSDK.getInstance();
 *
 *     NormalConversation normalConversation = new NormalConversation(sdk);
 *     normalConversation.get(new OnGetNormalConversationEvent() {
 *         @Override
 *         public void onCompleted(List<Conversation> conversationList) {
 *
 *         }
 *     });
 * </pre>
 *
 * Created by ppmessage on 5/16/16.
 */
public class NormalConversation {

    public interface OnGetNormalConversationEvent {
        void onCompleted(List<Conversation> conversationList);
    }

    private PPComSDK sdk;
    private PPComStartupHelper startupHelper;
    private PPMessageSDK messageSDK;

    private List<Conversation> conversationList;

    public NormalConversation(PPComSDK sdk) {
        this.sdk = sdk;
        this.startupHelper = sdk.getStartupHelper();
        this.messageSDK = sdk.getConfiguration().getMessageSDK();

        conversationList = new ArrayList<>();
    }

    public List<Conversation> getConversationList() {
        return conversationList;
    }

    public void get(final OnGetNormalConversationEvent event) {
        if (getConversationList() != null && !getConversationList().isEmpty()) {
            if (event != null) {
                event.onCompleted(getConversationList());
            }
            return;
        }

        User comUser = startupHelper.getComUser().getUser();
        if (comUser == null) {
            if (event != null) {
                event.onCompleted(getConversationList());
            }
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_uuid", comUser.getUuid());
            jsonObject.put("app_uuid", sdk.getConfiguration().getAppUUID());
        } catch (JSONException e) {
            L.e(e);
        }

        messageSDK.getAPI().getConversationList(jsonObject, new OnAPIRequestCompleted() {
            @Override
            public void onResponse(JSONObject jsonResponse) {
                NormalConversation.this.onResponse(jsonResponse, event);
            }

            @Override
            public void onCancelled() {
                if (event != null) {
                    event.onCompleted(getConversationList());
                }
            }

            @Override
            public void onError(int errorCode) {
                if (event != null) {
                    event.onCompleted(getConversationList());
                }
            }
        });
    }

    private void onResponse(JSONObject jsonResponse, final OnGetNormalConversationEvent event) {
        try {
            if (jsonResponse.getInt("error_code") == 0) {

                JSONArray jsonArray = jsonResponse.getJSONArray("list");
                for (int i = 0; i < jsonArray.length(); i++) {
                    getConversationList().add(Conversation.parse(messageSDK, jsonArray.getJSONObject(i)));
                }
                if (event != null) event.onCompleted(getConversationList());

            } else {
                if (event != null) event.onCompleted(getConversationList());
            }
        } catch (JSONException e) {
            L.e(e);
        }
    }

}
