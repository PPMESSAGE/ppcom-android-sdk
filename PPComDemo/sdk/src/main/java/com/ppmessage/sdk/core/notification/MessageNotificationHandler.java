package com.ppmessage.sdk.core.notification;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.message.PPMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppmessage on 5/9/16.
 */
public class MessageNotificationHandler implements INotificationHandler {

    private static final String MESSAGE_ERROR_LOG = "parse message error: %s";

    private PPMessageSDK sdk;

    public MessageNotificationHandler(PPMessageSDK sdk) {
        this.sdk = sdk;
    }

    @Override
    public void handle(JSONObject message, OnNotificationHandleEvent e) {
        asyncParseMessage(extractRealMessage(message), e);
    }

    private JSONObject extractRealMessage(JSONObject message) {
        try {
            return message.getJSONObject("msg");
        } catch (JSONException e) {
            L.e(e);
        }

        return message;
    }

    private void asyncParseMessage(JSONObject messageJsonObject, final OnNotificationHandleEvent e) {
        PPMessage.asyncParse(sdk, messageJsonObject, new PPMessage.onParseListener() {

            @Override
            public void onCompleted(PPMessage message) {
                if (message.isError()) {
                    L.w(MESSAGE_ERROR_LOG, message);
                } else if (message.getMessagePushID() != null) {
                    ackMessage(message);
                }
                if (e != null) {
                    e.onCompleted(INotification.EVENT_MESSAGE, message);
                }
            }

        });
    }

    private void ackMessage(PPMessage message) {
        JSONObject jsonObject = new JSONObject();
        try {

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(message.getMessagePushID());

            jsonObject.put("list", jsonArray );
        } catch (JSONException e) {
            L.e(e);
        }

        // Ignore ack error
        sdk.getAPI().ackMessage(jsonObject, null);
    }

}
