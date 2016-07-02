package com.ppmessage.ppcomlib.services;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.ppcomlib.utils.PollingControl;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.api.OnAPIRequestCompleted;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.query.IQuery;
import com.ppmessage.sdk.core.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppmessage on 5/18/16.
 */

public class ConversationWaitingService {

    public interface OnConversationReadyCallback {
        void ready(Conversation conversation);
    }

    private PPComSDK sdk;
    private PPMessageSDK messageSDK;
    private PollingControl polling;

    public ConversationWaitingService(PPComSDK sdk) {
        this.sdk = sdk;
        this.messageSDK = sdk.getConfiguration().getMessageSDK();
        this.polling = new PollingControl();
    }

    public void cancel() {
        if (messageSDK.getNotification().getConfig().getActiveUser() == null) return;

        final User user = messageSDK.getNotification().getConfig().getActiveUser();
        if (user.getUuid() == null) return;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("app_uuid", sdk.getConfiguration().getAppUUID());
            jsonObject.put("user_uuid", user.getUuid());
        } catch (JSONException e) {
            L.e(e);
        }
        // We don't care about the cancel result
        messageSDK.getAPI().cancelWaitingCreateConversation(jsonObject, null);

        polling.cancel();
    }

    public void start(final OnConversationReadyCallback callback) {
        polling.run(new Runnable() {
            @Override
            public void run() {
                if (messageSDK.getNotification().getConfig().getActiveUser() == null) return;

                final User user = messageSDK.getNotification().getConfig().getActiveUser();
                if (user.getUuid() == null) return;

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("app_uuid", sdk.getConfiguration().getAppUUID());
                    jsonObject.put("user_uuid", user.getUuid());
                } catch (JSONException e) {
                    L.e(e);
                }

                // We don't care about the cancel result
                messageSDK.getAPI().getWaitingQueueLength(jsonObject, new OnAPIRequestCompleted() {
                    @Override
                    public void onResponse(JSONObject jsonResponse) {
                        if (jsonResponse.has("error_code")) {
                            try {
                                if (jsonResponse.getInt("error_code") != 0) return;
                            } catch (JSONException e) {
                                L.e(e);
                                return;
                            }
                        }

                        try {
                            if (jsonResponse.has("conversation_uuid")) {
                                String conversation_uuid = jsonResponse.getString("conversation_uuid");
                                if (conversation_uuid != null && conversation_uuid.length() != 0) {
                                    polling.cancel();
                                    messageSDK.getDataCenter().queryConversation(conversation_uuid, new IQuery.OnQueryCallback() {
                                        @Override
                                        public void onCompleted(Object object) {

                                            if (object != null) {
                                                Conversation conversation = (Conversation)object;
                                                if (callback != null) {
                                                    callback.ready(conversation);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                        catch (JSONException e) {
                            L.e(e);
                        }
                    }

                    @Override
                    public void onCancelled() {

                    }

                    @Override
                    public void onError(int errorCode) {

                    }
                });

            }
        });
    }
}
