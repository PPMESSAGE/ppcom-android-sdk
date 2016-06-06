package com.ppmessage.sdk.core.notification;

import com.ppmessage.sdk.core.L;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppmessage on 5/9/16.
 */
public class WSMessageAckNotificationHandler implements INotificationHandler {

    public static class MessageSendResult {

        final static int DEFAULT_ERROR_CODE = -1;

        int errorCode;
        String reason;
        String conversationUUID;
        String messageUUID;

        public MessageSendResult(JSONObject jsonObject) {
            try {
                errorCode = jsonObject.getInt("code");
                reason = jsonObject.getString("reason");
                JSONObject extraJSONObject = jsonObject.getJSONObject("extra");
                conversationUUID = extraJSONObject.getString("conversation_uuid");
                messageUUID = extraJSONObject.getString("uuid");
            } catch (JSONException e) {
                L.e(e);
            }
        }

        public MessageSendResult(int errorCode, String reason, String conversationUUID, String messageUUID) {
            this.errorCode = errorCode;
            this.conversationUUID = conversationUUID;
            this.messageUUID = messageUUID;
            this.reason = reason;
        }

        public MessageSendResult(int errorCode, String conversationUUID, String messageUUID) {
            this(errorCode, null, conversationUUID, messageUUID);
        }

        public MessageSendResult(String conversationUUID, String messageUUID) {
            this(DEFAULT_ERROR_CODE, conversationUUID, messageUUID);
        }

        public int getErrorCode() {
            return errorCode;
        }

        public String getReason() {
            return reason;
        }

        public String getConversationUUID() {
            return conversationUUID;
        }

        public String getMessageUUID() {
            return messageUUID;
        }
    }

    @Override
    public void handle(JSONObject message, OnNotificationHandleEvent e) {
        if (e != null) {
            if (isWSMessageSendOK(message)) {
                e.onCompleted(INotification.EVENT_MSG_SEND_OK, new MessageSendResult(message));
            } else {
                e.onCompleted(INotification.EVENT_MSG_SEND_ERROR, new MessageSendResult(message));
            }
        }
    }

    private boolean isWSMessageSendOK(final JSONObject message) {
        if (message.has("code")) {
            try {
                return message.getInt("code") == 0;
            } catch (JSONException e) {
                L.e(e);
            }
        }
        return false;
    }

}
