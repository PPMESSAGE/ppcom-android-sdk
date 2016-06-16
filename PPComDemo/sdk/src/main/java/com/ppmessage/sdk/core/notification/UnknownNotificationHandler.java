package com.ppmessage.sdk.core.notification;

import com.ppmessage.sdk.core.L;

import org.json.JSONObject;

/**
 * Created by ppmessage on 5/9/16.
 */
public class UnknownNotificationHandler implements INotificationHandler {

    @Override
    public void handle(JSONObject message, OnNotificationHandleEvent e) {
        L.d("[Unknown Notification]: " + message);
        if (e != null) {
            e.onCompleted(INotification.EVENT_UNKNOWN, e);
        }
    }

}
