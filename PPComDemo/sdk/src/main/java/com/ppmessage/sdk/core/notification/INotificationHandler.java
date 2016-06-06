package com.ppmessage.sdk.core.notification;

import org.json.JSONObject;

/**
 * Created by ppmessage on 5/9/16.
 */
public interface INotificationHandler {

    interface OnNotificationHandleEvent {

        void onCompleted(int eventType, Object obj);

    }

    /**
     * Handle string message
     *
     * @param message
     * @param e
     */
    void handle(JSONObject message, OnNotificationHandleEvent e);

}
