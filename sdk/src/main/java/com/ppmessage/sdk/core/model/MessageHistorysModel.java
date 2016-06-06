package com.ppmessage.sdk.core.model;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.api.OnAPIRequestCompleted;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.bean.message.PPMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Example:
 *
 * <pre>
 *     PPMessageSDK messageSDK = PPMessageSDK.getInstance();
 *     MessageHistorysModel historysModel = new MessageHistorysModel(messageSDK);
 *
 *     MessageHistoryRequestParam requestParam =
 *          new MessageHistoryRequestParam("CONVERSATION_UUID", "MAX_UUID", 20, 0);
 *
 *     historysModel.loadHistorys(requestParam, new MessageHistoryEvent.OnLoadHistoryEvent() {
 *         @Override
 *         public void onCompleted(List<PPMessage> messageList) {
 *              // if messageList == null: load error
 *         }
 *     });
 * </pre>
 *
 * Created by ppmessage on 5/17/16.
 */
public class MessageHistorysModel {

    public interface OnLoadHistoryEvent {
        /**
         *
         * messageList: [
         *              message-A,
         *              message-B,
         *              message-C,
         *              ...
         *              ]
         *
         * message's timestamp: => grow up from index [0] to [length - 1]
         *
         * @param pageIndex
         * @param messageList
         */
        void onCompleted(HistoryPageIndex pageIndex, List<PPMessage> messageList);
    }

    public static class MessageHistoryRequestParam {

        private static final int DEFAULT_PAGE_SIZE = 20;

        final int pageSize;
        final String conversationUUID;
        final int pageOffset;
        final String maxUUID;

        public MessageHistoryRequestParam(String conversationUUID, String maxUUID, int pageSize, int pageOffset) {
            this.conversationUUID = conversationUUID;
            this.maxUUID = maxUUID;
            this.pageOffset = pageOffset;
            this.pageSize = pageSize;
        }

        public MessageHistoryRequestParam(String conversationUUID, String maxUUID, int pageOffset) {
            this(conversationUUID, maxUUID, DEFAULT_PAGE_SIZE, pageOffset);
        }

        public MessageHistoryRequestParam(String conversationUUID) {
            this(conversationUUID, null, DEFAULT_PAGE_SIZE, 0);
        }
    }

    private PPMessageSDK messageSDK;

    public MessageHistorysModel(PPMessageSDK messageSDK) {
        this.messageSDK = messageSDK;
    }

    public void loadHistorys(final MessageHistoryRequestParam requestParam, final OnLoadHistoryEvent event) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("conversation_uuid", requestParam.conversationUUID);
            jsonObject.put("page_offset", requestParam.pageOffset);
            jsonObject.put("page_size", requestParam.pageSize);
            jsonObject.put("max_uuid", requestParam.maxUUID);
        } catch (JSONException e) {
            L.e(e);
        }

        messageSDK.getAPI().loadMessageHistorys(jsonObject, new OnAPIRequestCompleted() {
            @Override
            public void onResponse(JSONObject jsonResponse) {
                MessageHistorysModel.this.onResponse(requestParam, jsonResponse, event);
            }

            @Override
            public void onCancelled() {
                MessageHistorysModel.this.onCompleted(requestParam, null, event);
            }

            @Override
            public void onError(int errorCode) {
                MessageHistorysModel.this.onCompleted(requestParam, null, event);
            }
        });
    }

    // list: [{single-body}, {single-body}, {single-body}]
    // Single Body:
    // {"body": "{\"mime\": \"application/x-x509-ca-cert\", \"name\": \"com.yvertical.PPMessage_Apple_PUSH_Services_DIS.cer\", \"fid\": \"b1c85f07-1bde-11e6-8d8a-acbc327f19e9\", \"size\": 1612}", "conversation_type": "P2S", "updatetime": "2016-05-17 11:46:46 805539", "uuid": "35ee32d4-54ab-4c0f-d60d-d073c3424369", "from_uuid": "e8caf300-1be1-11e6-96ca-acbc327f19e9", "conversation_uuid": "eae07f02-1be1-11e6-922e-acbc327f19e9", "from_type": "DU", "createtime": "2016-05-17 11:46:46 768841", "to_type": "AP", "app_uuid": "c56adc0a-1b54-11e6-bc9f-acbc327f19e9", "from_user": {"user_fullname": "Local Area.User", "user_email": "e8caf3@c56adc", "user_icon": "http://192.168.0.204:8080/identicon/e8caf300-1be1-11e6-96ca-acbc327f19e9.png", "uuid": "e8caf300-1be1-11e6-96ca-acbc327f19e9"}, "to_uuid": "eae07f02-1be1-11e6-922e-acbc327f19e9", "message_body": "{\"ci\": \"eae07f02-1be1-11e6-922e-acbc327f19e9\", \"ft\": \"DU\", \"tt\": \"AP\", \"bo\": \"{\\\"mime\\\": \\\"application/x-x509-ca-cert\\\", \\\"name\\\": \\\"com.yvertical.PPMessage_Apple_PUSH_Services_DIS.cer\\\", \\\"fid\\\": \\\"b1c85f07-1bde-11e6-8d8a-acbc327f19e9\\\", \\\"size\\\": 1612}\", \"ts\": 1463456806.768841, \"mt\": \"NOTI\", \"tl\": null, \"ms\": \"FILE\", \"ti\": \"eae07f02-1be1-11e6-922e-acbc327f19e9\", \"fi\": \"e8caf300-1be1-11e6-96ca-acbc327f19e9\", \"id\": \"35ee32d4-54ab-4c0f-d60d-d073c3424369\", \"ct\": \"P2S\"}", "task_status": "PROCESSED", "message_type": "NOTI", "from_device_uuid": "e8fc6c30-1be1-11e6-9e06-acbc327f19e9", "message_subtype": "FILE"}
    private void onResponse(MessageHistoryRequestParam requestParam, JSONObject jsonResponse, final OnLoadHistoryEvent event) {
        try {
            if (jsonResponse.getInt("error_code") != 0) {
                MessageHistorysModel.this.onCompleted(requestParam, null, event);
                return;
            }

            if (!jsonResponse.has("list")) {
                MessageHistorysModel.this.onCompleted(requestParam, new ArrayList<PPMessage>(), event);
                return;
            }

            JSONArray jsonArray = jsonResponse.getJSONArray("list");
            List<PPMessage> messageLists = new ArrayList<>(jsonArray.length());

            asyncParseMessages(messageLists, jsonResponse, jsonArray, requestParam, 0, event);

        } catch (JSONException e) {
            L.e(e);
        }
    }

    private void asyncParseMessages(final List<PPMessage> messageList,
                                    final JSONObject jsonResponse,
                                    final JSONArray jsonArray,
                                    final MessageHistoryRequestParam requestParam,
                                    int startIndex,
                                    final OnLoadHistoryEvent event) {
        if (startIndex >= jsonArray.length()) {
            MessageHistorysModel.this.onCompleted(requestParam, jsonResponse, messageList, event);
            return;
        }

        final int nextIndex = startIndex + 1;
        try {
            final JSONObject messageJsonObject = jsonArray.getJSONObject(startIndex);
            final JSONObject messageBodyJsonObject = new JSONObject(messageJsonObject.getString("message_body"));
            PPMessage.asyncParse(messageSDK, messageBodyJsonObject, new PPMessage.onParseListener() {
                @Override
                public void onCompleted(PPMessage message) {

                    try {
                        User fromUser = User.parse(messageJsonObject.getJSONObject("from_user"));
                        message.setFromUser(fromUser);
                    } catch (JSONException e) {
                        L.e(e);
                    }

                    messageList.add(message);
                    asyncParseMessages(messageList, jsonResponse, jsonArray, requestParam, nextIndex, event);

                }
            });

        } catch (JSONException e) {
            L.e(e);
        }

    }

    private void onCompleted(final MessageHistoryRequestParam requestParam,
                             final List<PPMessage> messageList,
                             final OnLoadHistoryEvent event) {
        onCompleted(requestParam, new JSONObject(), messageList, event);
    }

    private void onCompleted(final MessageHistoryRequestParam requestParam,
                             final JSONObject response,
                             final List<PPMessage> messageList,
                             final OnLoadHistoryEvent event) {

        List<PPMessage> finalMessageList = finalMessageList(messageList);

        String maxUUID = (finalMessageList != null && !finalMessageList.isEmpty()) ?
                finalMessageList.get(0).getMessageID() : null;

        HistoryPageIndex pageIndex =
                null;
        try {
            pageIndex = new HistoryPageIndex(maxUUID,
                    requestParam.pageOffset,
                    requestParam.pageSize,
                    response.has("total_count") ? response.getInt("total_count") : 0);
        } catch (JSONException e) {
            L.e(e);
        }

        if (event != null) {
            event.onCompleted(pageIndex, finalMessageList);
        }
    }

    /**
     * Default response originMessageList's timestamp was [Big-timestamp, small-timestamp, ...],
     * We reverse it for more easily to use
     *
     * @param originMessageList
     * @return
     */
    private List<PPMessage> finalMessageList(List<PPMessage> originMessageList) {
        if (originMessageList == null || originMessageList.isEmpty()) return originMessageList;
        Collections.reverse(originMessageList);
        return originMessageList;
    }

}
