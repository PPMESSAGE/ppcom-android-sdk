package com.ppmessage.sdk.core.model;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.api.OnAPIRequestCompleted;
import com.ppmessage.sdk.core.bean.common.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Help you load all unacked messages
 *
 * Created by ppmessage on 5/19/16.
 */
public class UnackedMessagesLoader {

    private PPMessageSDK messageSDK;

    public UnackedMessagesLoader(PPMessageSDK messageSDK) {
        this.messageSDK = messageSDK;
    }

    /**
     *
     * Get all unacked messages, and send them to {@link com.ppmessage.sdk.core.notification.INotification},
     * so, make it seems like this messages are arrived by ws channel, not by api channel
     *
     */
    public void loadUnackedMessages() {

        final JSONObject requestParam = getRequestParam();
        if (requestParam == null) return;

        messageSDK.getAPI().getUnackedMessages(requestParam, new OnAPIRequestCompleted() {
            @Override
            public void onResponse(JSONObject jsonResponse) {
                UnackedMessagesLoader.this.onResponse(jsonResponse);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onError(int errorCode) {

            }
        });

    }

    private JSONObject getRequestParam() {
        final User activeUser = messageSDK.getNotification().getConfig().getActiveUser();
        final String appUUID = messageSDK.getNotification().getConfig().getAppUUID();
        if (activeUser == null || appUUID == null) return null;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("app_uuid", appUUID);
            jsonObject.put("from_uuid", activeUser.getUuid());
            jsonObject.put("device_uuid", activeUser.getDeviceUUID());
        } catch (JSONException e) {
            L.e(e);
        }
        return jsonObject;
    }

//    {
//        "error_string":"success.",
//            "list":[
//        "589e0bc2-1d93-11e6-9523-acbc327f19e9"
//        ],
//        "uri":"/GET_UNACKED_MESSAGES",
//            "message":{
//        "589e0bc2-1d93-11e6-9523-acbc327f19e9":"{\"ci\": \"88c2b34a-1ce2-11e6-b80a-acbc327f19e9\", \"ft\": \"DU\", \"tt\": \"AP\", \"bo\": \"123\", \"ts\": 1463642936.526738, \"mt\": \"NOTI\", \"tl\": null, \"ms\": \"TEXT\", \"ti\": \"c56adc0a-1b54-11e6-bc9f-acbc327f19e9\", \"fi\": \"c5657363-1b54-11e6-aaea-acbc327f19e9\", \"id\": \"e43c6a0a-4d92-49fa-f83d-5c367a4d6150\", \"ct\": \"S2P\"}"
//    },
//        "error_code":0,
//            "size":1
//    }
    private void onResponse(JSONObject jsonResponse) {
        try {
            if (jsonResponse.getInt("error_code") != 0) {
                return;
            }

            JSONArray jsonArray = jsonResponse.getJSONArray("list");
            JSONObject messageDictionary = jsonResponse.getJSONObject("message");
            for (int i = 0; i < jsonArray.length(); i++) {
                String pid = jsonArray.getString(i);
                String message = messageDictionary.has(pid) ? messageDictionary.getString(pid) : null;
                String messageWithPid = convertMessageToWSMessageStyle(message, pid);

                if (messageWithPid != null) {
                    notifyMessageArrived(messageWithPid);
                }
            }

        } catch (JSONException e) {
            L.e(e);
        }
    }

    // Add pid to message:
    //
    //"{\"ci\": \"88c2b34a-1ce2-11e6-b80a-acbc327f19e9\", \"ft\": \"DU\", \"tt\": \"AP\", \"bo\": \"123\", \"ts\": 1463642936.526738, \"mt\": \"NOTI\", \"tl\": null, \"ms\": \"TEXT\", \"ti\": \"c56adc0a-1b54-11e6-bc9f-acbc327f19e9\", \"fi\": \"c5657363-1b54-11e6-aaea-acbc327f19e9\", \"id\": \"e43c6a0a-4d92-49fa-f83d-5c367a4d6150\", \"ct\": \"S2P\"}"
    //
    //
    private String convertMessageToWSMessageStyle(String message, String pid) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            jsonObject.put("pid", pid);
            return jsonObject.toString();
        } catch (JSONException e) {
            L.e(e);
        }
        return message;
    }

    private void notifyMessageArrived(String message) {
        messageSDK.getNotification().notify(message);
    }

}
