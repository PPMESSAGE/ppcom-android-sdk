package com.ppmessage.sdk.core.notification;

import org.json.JSONObject;

/**
 * Created by ppmessage on 5/9/16.
 */
public class SysNotificationHandler implements INotificationHandler {

    @Override
    public void handle(JSONObject message, OnNotificationHandleEvent e) {
        if (e != null) {
            e.onCompleted(INotification.EVENT_SYS, message);
        }
    }

}
