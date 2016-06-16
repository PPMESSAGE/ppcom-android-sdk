package com.ppmessage.sdk.core.notification;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ppmessage on 5/9/16.
 */
public class NotificationHandlerFactory {

    private final Map<Integer, INotificationHandler> handlerMap = new ConcurrentHashMap<>();

    public NotificationHandlerFactory(PPMessageSDK sdk) {
        handlerMap.put(INotification.EVENT_UNKNOWN, new UnknownNotificationHandler());
        handlerMap.put(INotification.EVENT_AUTH, new AuthNotificationHandler());
        handlerMap.put(INotification.EVENT_CONVERSATION, new ConversationNotificationHandler(sdk));
        handlerMap.put(INotification.EVENT_MESSAGE, new MessageNotificationHandler(sdk));
        handlerMap.put(INotification.EVENT_SYS, new SysNotificationHandler());
        handlerMap.put(INotification.EVENT_MSG_SEND_OK, new WSMessageAckNotificationHandler());
    }

    public void handle(String message, INotificationHandler.OnNotificationHandleEvent event) {

        JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(message);
            } catch (JSONException e) {
                L.e(e);
            }

            if (jsonObject != null) {
                INotificationHandler handler = handlerMap.get(findNotificationType(jsonObject));
                if (handler != null) {
                    handler.handle(jsonObject, event);
                }
        }

    }

    public static int findNotificationType(JSONObject message) {
        String what = null;
        String type = null;

        try {
            what = message.has("what") ? message.getString("what") : null;
            type = message.has("type") ? message.getString("type") : null;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (what != null) {
            if (what.equals("AUTH")) return INotification.EVENT_AUTH;
            if (what.equals("SEND")) return INotification.EVENT_MSG_SEND_OK;
            if (what.equals("CONVERSATION")) return INotification.EVENT_CONVERSATION;
        }

        if (type != null) {

            String mt = null;
            try {
                JSONObject innerMsgObject = message.has("msg") ? message.getJSONObject("msg") : null;
                if (innerMsgObject != null) {
                    mt = innerMsgObject.has("mt") ? innerMsgObject.getString("mt") : null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (mt != null) {
                if (mt.equals("SYS")) return INotification.EVENT_SYS;
            }

            if (type.equals("MSG")) return INotification.EVENT_MESSAGE;
        }

        return INotification.EVENT_UNKNOWN;
    }

}
