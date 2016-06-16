package com.ppmessage.ppcomlib.model.conversations;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.ppcomlib.services.PPComStartupHelper;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.api.OnAPIRequestCompleted;
import com.ppmessage.sdk.core.bean.common.App;
import com.ppmessage.sdk.core.bean.common.Conversation;

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
 *     AppOrgGroupList appOrgGroupList = new AppOrgGroupList(sdk);
 *     appOrgGroupList.get(new OnGetAppOrgGroupListEvent() {
 *         @Override
 *         public void onCompleted(List<Conversation> groupConversations) {
 *
 *         }
 *     });
 * </pre>
 *
 * Created by ppmessage on 5/16/16.
 */
public class AppOrgGroupList {

    public interface OnGetAppOrgGroupListEvent {
        void onCompleted(List<Conversation> groupConversations);
    }

    private PPComSDK sdk;
    private PPMessageSDK messageSDK;
    private PPComStartupHelper startupHelper;

    private List<Conversation> conversationList;

    public AppOrgGroupList(PPComSDK sdk) {
        this.sdk = sdk;
        this.messageSDK = sdk.getConfiguration().getMessageSDK();
        this.startupHelper = sdk.getStartupHelper();

        conversationList = new ArrayList<>();
    }

    public List<Conversation> getConversationList() {
        return conversationList;
    }

    public void get(final OnGetAppOrgGroupListEvent event) {
        if (getConversationList() != null && !getConversationList().isEmpty()) {
            if (event != null) {
                event.onCompleted(getConversationList());
            }
            return;
        }

        if (!isGroupPolicy()) {
            if (event != null) {
                event.onCompleted(getConversationList());
            }
            return;
        }

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("app_uuid", sdk.getConfiguration().getAppUUID());
        } catch (JSONException e) {
            L.e(e);
        }

        messageSDK.getAPI().getAppOrgGroupList(jsonObject, new OnAPIRequestCompleted() {
            @Override
            public void onResponse(JSONObject jsonResponse) {
                AppOrgGroupList.this.onResponse(jsonResponse, event);
            }

            @Override
            public void onCancelled() {
                if (event != null) event.onCompleted(getConversationList());
            }

            @Override
            public void onError(int errorCode) {
                if (event != null) event.onCompleted(getConversationList());
            }
        });
    }

    private void onResponse(JSONObject jsonResponse, final OnGetAppOrgGroupListEvent event) {
        try {
            if (jsonResponse.getInt("error_code") == 0) {

                JSONArray list = jsonResponse.getJSONArray("list");
                for (int i = 0; i < list.length(); i++) {
                    getConversationList().add(Conversation.parse(messageSDK, list.getJSONObject(i)));
                }

                if (event != null) event.onCompleted(getConversationList());

            } else {
                if (event != null) event.onCompleted(getConversationList());
            }
        } catch (JSONException e) {
            L.e(e);
        }
    }

    private boolean isGroupPolicy() {
        App app = startupHelper.getComApp().getApp();
        if (app != null) {
            return app.getPolicy().equals(App.POLICY_GROUP);
        }
        return false;
    }

}
