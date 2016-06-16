package com.ppmessage.sdk.core.notification;

import org.json.JSONObject;

/**
 * Created by ppmessage on 5/9/16.
 */
public class AuthNotificationHandler implements INotificationHandler {

    @Override
    public void handle(JSONObject message, OnNotificationHandleEvent event) {
        if (event != null) {
            event.onCompleted(INotification.EVENT_AUTH, message);
        }
    }

}
