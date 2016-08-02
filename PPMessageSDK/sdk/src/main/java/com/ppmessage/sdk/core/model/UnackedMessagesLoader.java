package com.ppmessage.sdk.core.model;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.api.OnAPIRequestCompleted;
import com.ppmessage.sdk.core.bean.common.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Help you load all unacked messages
 *
 * Created by ppmessage on 5/19/16.
 */
public class UnackedMessagesLoader {

    private static final String TAG = UnackedMessagesLoader.class.getSimpleName();
    public static final String LOG_API_PAGE_UNACKED_MESSAGES_EMPTY = "[" + TAG + "]: call api to page unacked messages, empty result, cancel request";
    public static final String LOG_PROCESS_UNACKED_MESSAGES_COMPLETED = "[" + TAG + "]: process %d unacked messages completed, total unacked messages count %d";

    private static final int DEFAULT_PAGE_UNACKED_MESSAGES_COUNT = 30;

    private PPMessageSDK messageSDK;
    private UnackedMessagesProcessor messagesProcessor;
    private boolean stop;

    public UnackedMessagesLoader(PPMessageSDK messageSDK) {
        this.messageSDK = messageSDK;
        this.stop = false;
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

        messageSDK.getAPI().pageUnackedMessages(requestParam, new OnAPIRequestCompleted() {
            @Override
            public void onResponse(JSONObject jsonResponse) {
                List<String> unackedMessageJSONStringList = UnackedMessagesLoader.this.onResponse(jsonResponse);
                if (unackedMessageJSONStringList == null || unackedMessageJSONStringList.isEmpty()) {
                    L.d(LOG_API_PAGE_UNACKED_MESSAGES_EMPTY);
                    return;
                }

                UnackedMessagesLoader.this.stopLastMessagesProcessor();

                messagesProcessor = new UnackedMessagesProcessor(messageSDK, unackedMessageJSONStringList);
                messagesProcessor.setCompletedCallback(new UnackedMessagesProcessor.OnCompletedCallback() {
                    @Override
                    public void onCompleted(int messageProcessedCount, int messageTotalCount) {
                        L.d(LOG_PROCESS_UNACKED_MESSAGES_COMPLETED, messageProcessedCount, messageTotalCount);

                        if (!isStop()) {
                            loadUnackedMessages();
                        }
                    }
                });
                messagesProcessor.start();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onError(int errorCode) {

            }
        });

    }

    public void stop() {
        stopLastMessagesProcessor();
        stop = true;
    }

    public boolean inLoading() {
        return messagesProcessor != null && !messagesProcessor.isCompleted() && !isStop();
    }

    public boolean isStop() {
        return stop;
    }

    private void stopLastMessagesProcessor() {
        if (messagesProcessor != null) {
            messagesProcessor.stop();
            messagesProcessor = null;
        }
    }

    private JSONObject getRequestParam() {
        final User activeUser = messageSDK.getNotification().getConfig().getActiveUser();
        final String appUUID = messageSDK.getNotification().getConfig().getAppUUID();
        if (activeUser == null || appUUID == null) return null;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("page_offset", 0);
            jsonObject.put("page_size", DEFAULT_PAGE_UNACKED_MESSAGES_COUNT);
            jsonObject.put("app_uuid", appUUID);
            jsonObject.put("user_uuid", activeUser.getUuid());
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
    private List<String> onResponse(JSONObject jsonResponse) {
        try {
            if (jsonResponse.getInt("error_code") != 0) {
                return null;
            }

            JSONArray jsonArray = jsonResponse.getJSONArray("list");
            JSONObject messageDictionary = jsonResponse.getJSONObject("message");
            List<String> unackedMessages = new ArrayList<>(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                String pid = jsonArray.getString(i);
                String message = messageDictionary.has(pid) ? messageDictionary.getString(pid) : null;
                String messageWithPid = convertMessageToWSMessageStyle(message, pid);

                if (messageWithPid != null) {
                    unackedMessages.add(messageWithPid);
                }
            }
            return unackedMessages;

        } catch (JSONException e) {
            L.e(e);
        }

        return null;
    }

    // Add pid to message:
    //
    //"{\"ci\": \"88c2b34a-1ce2-11e6-b80a-acbc327f19e9\", \"ft\": \"DU\", \"tt\": \"AP\", \"bo\": \"123\", \"ts\": 1463642936.526738, \"mt\": \"NOTI\", \"tl\": null, \"ms\": \"TEXT\", \"ti\": \"c56adc0a-1b54-11e6-bc9f-acbc327f19e9\", \"fi\": \"c5657363-1b54-11e6-aaea-acbc327f19e9\", \"id\": \"e43c6a0a-4d92-49fa-f83d-5c367a4d6150\", \"ct\": \"S2P\"}"
    //
    //
    private String convertMessageToWSMessageStyle(String message, String pid) {
        try {
            JSONObject msgWrapper = new JSONObject();

            JSONObject jsonObject = new JSONObject(message);
            jsonObject.put("pid", pid);

            msgWrapper.put("type", "MSG");
            msgWrapper.put("msg", jsonObject);

            return msgWrapper.toString();
        } catch (JSONException e) {
            L.e(e);
        }
        return message;
    }

    // ===========================================
    // UnackedMessagesProcessor
    //
    // [unack-msg-1] --500ms--> [unack-msg-2] --500ms--> [unack-msg-3] --500ms--> ...
    //
    // ===========================================

    private static class UnackedMessagesProcessor {

        interface OnCompletedCallback {
            void onCompleted(int messageProcessedCount, int messageTotalCount);
        }

        private static final String LOG_START = "[UnackedMessagesProcessor] start process unacked messages, count: %d";
        private static final String LOG_NEXT = "[UnackedMessagesProcessor] next unacked message, index:%d, message:%s";
        private static final String LOG_COMPLETED = "[UnackedMessagesProcessor] index == size, finished, total unacked messages: %d";

        private static final int WHAT = 1;
        private static final long DELAY_MILLIS = 500; // 500ms

        private Handler handler;
        private PPMessageSDK messageSDK;
        private List<String> unackedMessages;
        private int index;
        private boolean completed;
        private OnCompletedCallback completedCallback;

        public UnackedMessagesProcessor(PPMessageSDK messageSDK, final List<String> unackedMessages) {
            this.handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    if (msg.what == WHAT) {
                        int index = msg.arg1;
                        notifyMessageArrived(unackedMessages.get(index));
                    }

                    next();
                }
            };
            this.messageSDK = messageSDK;
            this.unackedMessages = unackedMessages;
            this.index = -1;
            setCompleted(false);
        }

        public void setCompletedCallback(OnCompletedCallback completedCallback) {
            this.completedCallback = completedCallback;
        }

        public void start() {
            if (unackedMessages == null ||
                    unackedMessages.isEmpty()) {
                setCompleted(true);
                return;
            }

            L.d(LOG_START, unackedMessages.size());
            next();
        }

        public void stop() {
            handler.removeMessages(WHAT);
            setCompleted(true);
        }

        private void next() {
            index++;
            if (index < unackedMessages.size()) {
                L.d(LOG_NEXT, index, unackedMessages.get(index));
                Message message = handler.obtainMessage(WHAT);
                message.arg1 = index;
                handler.sendMessageDelayed(message, DELAY_MILLIS);
            } else {
                L.d(LOG_COMPLETED, unackedMessages.size());
                stop();
            }
        }

        private void notifyMessageArrived(String message) {
            messageSDK.getNotification().notify(message);
        }

        public boolean isCompleted() {
            return completed;
        }

        private void setCompleted(boolean completed) {
            this.completed = completed;

            if (completed) {
                if (completedCallback != null) {
                    completedCallback.onCompleted(index, unackedMessages != null ? unackedMessages.size() : 0);
                }
            }
        }
    }

}
