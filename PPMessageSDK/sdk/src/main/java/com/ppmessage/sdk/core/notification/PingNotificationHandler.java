package com.ppmessage.sdk.core.notification;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dingguijin on 08/05/2017.
 */

public class PingNotificationHandler implements INotificationHandler {
    private PPMessageSDK sdk;

    public PingNotificationHandler(PPMessageSDK sdk) {
        this.sdk = sdk;
    }

    @Override
    public void handle(JSONObject message, OnNotificationHandleEvent e) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "PONG");
        } catch (JSONException je) {
            L.e(je);
        }
        sdk.getNotification().sendMessage(jsonObject.toString());

    }
}
