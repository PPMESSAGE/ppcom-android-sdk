package com.ppmessage.ppcomlib.model.conversations;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.ppcomlib.services.PPComStartupHelper;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.api.OnAPIRequestCompleted;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Example:
 *
 * <pre>
 *     PPComSDK sdk = PPComSDK.getInstance();
 *     DefaultConversation defaultConversation = new DefaultConversation(sdk);
 *
 *     defaultConversation.get(new OnGetDefaultConversationEvent() {
 *         @Override
 *         public void onCompleted(Conversation conversation) {
 *             //conversation == null: ==> please waiting ...
 *             //conversation != null: ==> It's OK ^_^ !
 *         }
 *     });
 * </pre>
 *
 * Created by ppmessage on 5/16/16.
 */
public class DefaultConversation {

    public interface OnGetDefaultConversationEvent {
        void onCompleted(Conversation conversation);
    }

    private PPComSDK sdk;
    private PPComStartupHelper startupHelper;
    private PPMessageSDK messageSDK;

    public DefaultConversation(PPComSDK sdk) {
        this.sdk = sdk;
        this.messageSDK = sdk.getConfiguration().getMessageSDK();
        this.startupHelper = sdk.getStartupHelper();
    }

    public void get(final OnGetDefaultConversationEvent event) {
        User ppcomUser = startupHelper.getComUser().getUser();
        if (ppcomUser == null) {
            if (event != null) {
                event.onCompleted(null);
            }
            return;
        }

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("app_uuid", sdk.getConfiguration().getAppUUID());
            jsonObject.put("user_uuid", ppcomUser.getUuid());
            jsonObject.put("device_uuid", ppcomUser.getDeviceUUID());
        } catch (JSONException e) {
            L.e(e);
        }

        messageSDK.getAPI().getPPComDefaultConversation(jsonObject, new OnAPIRequestCompleted() {
            @Override
            public void onResponse(JSONObject jsonResponse) {
                try {
                    if (jsonResponse.getInt("error_code") == 0) {
                        if (Utils.isJsonResponseEmpty(jsonResponse)) {
                            if (event != null) event.onCompleted(null);
                        } else {
                            Conversation conversation = Conversation.parse(messageSDK, jsonResponse);
                            if (event != null) event.onCompleted(conversation);
                        }
                    } else {
                        if (event != null) event.onCompleted(null);
                    }
                } catch (JSONException e) {
                    L.e(e);
                }
            }

            @Override
            public void onCancelled() {
                if (event != null) {
                    event.onCompleted(null);
                }
            }

            @Override
            public void onError(int errorCode) {
                if (event != null) {
                    event.onCompleted(null);
                }
            }
        });
    }

}
