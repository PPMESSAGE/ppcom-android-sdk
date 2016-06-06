package com.ppmessage.ppcomlib.services;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppmessage on 5/18/16.
 */
public class ConversationWaitingService {

    private PPComSDK sdk;
    private PPMessageSDK messageSDK;

    public ConversationWaitingService(PPComSDK sdk) {
        this.sdk = sdk;
        this.messageSDK = sdk.getConfiguration().getMessageSDK();
    }

    public void cancel() {
        cancel(null);
    }

    public void cancel(String groupUUID) {
        if (messageSDK.getNotification().getConfig().getActiveUser() == null) return;

        final User user = messageSDK.getNotification().getConfig().getActiveUser();
        if (user.getUuid() == null) return;
        if (user.getDeviceUUID() == null) return;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("app_uuid", sdk.getConfiguration().getAppUUID());
            jsonObject.put("user_uuid", user.getUuid());
            jsonObject.put("device_uuid", user.getDeviceUUID());
            if (groupUUID != null) {
                jsonObject.put("group_uuid", groupUUID);
            }
        } catch (JSONException e) {
            L.e(e);
        }

        // We don't care about the cancel result
        messageSDK.getAPI().cancelWaitingCreateConversation(jsonObject, null);
    }

}
