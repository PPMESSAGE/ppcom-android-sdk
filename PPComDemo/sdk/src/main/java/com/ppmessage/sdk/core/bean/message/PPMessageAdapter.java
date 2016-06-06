package com.ppmessage.sdk.core.bean.message;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppmessage on 5/11/16.
 */
public class PPMessageAdapter {

    private interface OnGetMessageBodyEvent {
        void onCompleted(Object messageBody);
    }

    private PPMessage message;
    private PPMessageSDK sdk;

    public PPMessageAdapter(PPMessageSDK sdk, PPMessage message) {
        this.sdk = sdk;
        this.message = message;
    }

    public void asyncGetAPIJsonObject(final OnGetJsonObjectEvent event) {
        final JSONObject apiJSONObject = new JSONObject();
        try {
            apiJSONObject.put("conversation_uuid", message.getConversation().getConversationUUID());
            apiJSONObject.put("to_uuid", sdk.getNotification().getConfig().getAppUUID());
            apiJSONObject.put("to_type", "AP"); // AP or OG ?
            apiJSONObject.put("conversation_type", message.getConversation().getConversationType());
            apiJSONObject.put("message_type", "NOTI");
            apiJSONObject.put("message_subtype", message.getMessageSubType());
            apiJSONObject.put("from_uuid", message.getFromUser().getUuid());
            apiJSONObject.put("device_uuid", sdk.getNotification().getConfig().getActiveUser().getDeviceUUID());
            apiJSONObject.put("uuid", message.getMessageID());
            apiJSONObject.put("from_type", "DU");
            apiJSONObject.put("app_uuid", sdk.getNotification().getConfig().getAppUUID());
        } catch (JSONException e) {
            L.e(e);
        }

        asyncGetMessageBody(new OnGetMessageBodyEvent() {
            @Override
            public void onCompleted(Object messageBody) {
                if (messageBody == null) {
                    message.setError(true);
                } else {
                    if (messageBody instanceof String ||
                            messageBody instanceof JSONObject) {
                        try {
                            apiJSONObject.put("message_body", messageBody);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (event != null) {
                    if (event != null) event.onCompleted(apiJSONObject);
                }
            }
        });
    }

    public void asyncGetWSJsonObject(final OnGetJsonObjectEvent event) {
        final JSONObject wsJSONObject = new JSONObject();
        asyncGetAPIJsonObject(new OnGetJsonObjectEvent() {

            @Override
            public void onCompleted(JSONObject jsonObject) {
                try {
                    wsJSONObject.put("type", "send");
                    wsJSONObject.put("send", jsonObject);
                } catch (JSONException e) {
                    L.e(e);
                }
                if (event != null) event.onCompleted(wsJSONObject);
            }

            @Override
            public void onError(Exception e) {
                if (event != null) event.onCompleted(wsJSONObject);
            }

        });
    }

    private void asyncGetMessageBody(final OnGetMessageBodyEvent event) {
        String messageSubType = message.getMessageSubType();
        if (messageSubType.equals(PPMessage.TYPE_TEXT)) {
            if (event != null) {
                event.onCompleted(message.getMessageBody());
            }
        } else {
            IPPMessageMediaItem mediaItem = message.getMediaItem();
            mediaItem.asyncGetAPIJsonObject(new OnGetJsonObjectEvent() {
                @Override
                public void onCompleted(JSONObject jsonObject) {
                    if (event != null) {
                        event.onCompleted(jsonObject.toString());
                    }
                }

                @Override
                public void onError(Exception e) {
                    if (event != null) event.onCompleted(null);
                }
            });
        }
    }

}
