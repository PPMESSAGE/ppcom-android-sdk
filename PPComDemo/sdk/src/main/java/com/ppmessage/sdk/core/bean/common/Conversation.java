package com.ppmessage.sdk.core.bean.common;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppmessage on 5/6/16.
 */
public class Conversation implements Comparable<Conversation> {

    private String conversationUUID;
    private String conversationIcon;
    private String conversationName;
    private String assignedUUID;
    private String groupUUID;
    private String conversationSummary;
    private long updateTimestamp;
    private String conversationType;
    private boolean isGroupType;

    public Conversation() {

    }

    public String getConversationUUID() {
        return conversationUUID;
    }

    public void setConversationUUID(String conversationUUID) {
        this.conversationUUID = conversationUUID;
    }

    public String getConversationIcon() {
        return conversationIcon;
    }

    public void setConversationIcon(String conversationIcon) {
        this.conversationIcon = conversationIcon;
    }

    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public String getAssignedUUID() {
        return assignedUUID;
    }

    public void setAssignedUUID(String assignedUUID) {
        this.assignedUUID = assignedUUID;
    }

    public String getGroupUUID() {
        return groupUUID;
    }

    public void setGroupUUID(String groupUUID) {
        this.groupUUID = groupUUID;
    }

    public long getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(long updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public String getConversationType() {
        return conversationType;
    }

    public void setConversationType(String conversationType) {
        this.conversationType = conversationType;
    }

    public String getConversationSummary() {
        return conversationSummary;
    }

    public void setConversationSummary(String conversationSummary) {
        this.conversationSummary = conversationSummary;
    }

    public boolean isGroupType() {
        return isGroupType;
    }

    public void setIsGroupType(boolean isGroupType) {
        this.isGroupType = isGroupType;
    }

    public static Conversation parse(PPMessageSDK sdk, JSONObject jsonObject) {
        if (jsonObject.has("error_code")) {
            try {
                if (jsonObject.getInt("error_code") != 0) return null;
            } catch (JSONException e) {
                L.e(e);
            }
        }

        Conversation conversation = new Conversation();
        String conversationUUID = null;
        String conversationIcon = null;
        String conversationName = null;
        long updatetime = 0;
        boolean isGroupType = false;
        String groupUUID = null;
        String assignedUUID = null;

        try {
            isGroupType = determineIsGroupType(jsonObject);

            if (jsonObject.has("conversation_data")) {
                JSONObject conversationData = jsonObject.getJSONObject("conversation_data");
                conversationUUID = conversationData.getString("conversation_uuid");
                conversationIcon = Utils.getFileDownloadUrl(conversationData.getString("conversation_icon"));
                conversationName = conversationData.getString("conversation_name");
            } else {

                conversationIcon = jsonObject.has("conversation_icon") ? Utils.getFileDownloadUrl(jsonObject.getString("conversation_icon")) :
                        (jsonObject.has("group_icon") ? Utils.getFileDownloadUrl(jsonObject.getString("group_icon")) : null);
                conversationName = jsonObject.has("conversation_name") ? jsonObject.getString("conversation_name") :
                        (jsonObject.has("group_name") ? jsonObject.getString("group_name") : null);
                conversationUUID = isGroupType ?
                        ( jsonObject.has("conversation_uuid") ? Utils.safeNull(jsonObject.getString("conversation_uuid")) : null ) :
                        ( jsonObject.has("uuid") ? jsonObject.getString("uuid") : null );
            }
        } catch (JSONException e) {
            L.e(e);
        }

        conversation.setConversationIcon(conversationIcon);
        conversation.setConversationUUID(conversationUUID);
        conversation.setConversationName(conversationName);

        try {

            if (isGroupType) {
                groupUUID = jsonObject.has("uuid") ? jsonObject.getString("uuid") : null;
            } else {
                groupUUID = jsonObject.has("group_uuid") ? Utils.safeNull(jsonObject.getString("group_uuid")) : null;
                assignedUUID = jsonObject.has("assigned_uuid") ? Utils.safeNull(jsonObject.getString("assigned_uuid")) : null;
            }

            updatetime = jsonObject.has("updatetime") ? Utils.getTimestamp(jsonObject.getString("updatetime")) : 0;

            conversation.setAssignedUUID(jsonObject.has("assigned_uuid") ? jsonObject.getString("assigned_uuid") : null);
            conversation.setGroupUUID(jsonObject.has("group_uuid") ? jsonObject.getString("group_uuid") : null);
            conversation.setConversationType(jsonObject.has("conversation_type") ? jsonObject.getString("conversation_type") : null);
            conversation.setUpdateTimestamp(updatetime);
            conversation.setIsGroupType(isGroupType);
            conversation.setGroupUUID(groupUUID);
            conversation.setAssignedUUID(assignedUUID);

            tryParseConversationSummary(sdk, conversation, jsonObject);

        } catch (JSONException e) {
            L.e(e);
        }

        return conversation;
    }

    private static void tryParseConversationSummary(final PPMessageSDK sdk, final Conversation conversation, JSONObject jsonObject) {
        String jsonMessageBody = null;
        try {

            if (jsonObject.has("latest_message")) {
                jsonMessageBody = jsonObject.getJSONObject("latest_message")
                        .getString("message_body");
            } else if (jsonObject.has("message_body")) {
                jsonMessageBody = jsonObject.getString("message_body");
            }

            if (jsonMessageBody != null) {
                PPMessage message = PPMessage.parse(sdk, new JSONObject(jsonMessageBody));
                    conversation.setConversationSummary(PPMessage.summary(sdk.getContext(), message));
            } else if (jsonObject.has("group_desc")) {
                conversation.setConversationSummary(jsonObject.getString("group_desc"));
            }

        } catch (JSONException e) {
            L.e(e);
        }

    }

//    {
//        "updatetime":"2016-05-18 16:42:17 923133",
//            "user_count":1,
//            "uuid":"673b8770-1cd4-11e6-813e-acbc327f19e9",
//            "conversation_uuid":null,
//            "createtime":"2016-05-18 16:42:07 213624",
//            "is_distributor":true,
//            "group_desc":"Group Description",
//            "group_name":"Main Group",
//            "app_uuid":"c56adc0a-1b54-11e6-bc9f-acbc327f19e9",
//            "group_icon":"http:\/\/192.168.0.204:8080\/identicon\/a72e82d80fa10fb0cbb746dd22a302781a93ea86.png"
//    }
    private static boolean determineIsGroupType(JSONObject jsonObject) {
        return jsonObject.has("group_name") &&
                jsonObject.has("group_desc") &&
                jsonObject.has("group_icon");
    }

    @Override
    public int compareTo(Conversation another) {
        int weightA = weight();
        int weightB = another.weight();

        if (weightA == weightB) {
            long timestampA = getUpdateTimestamp();
            long timestampB = another.getUpdateTimestamp();

            return timestampA > timestampB ? -1: 1;
        }

        return weightA > weightB ? -1 : 1;
    }

    private int weight() {
        return isGroupType() ? 10000 : 0;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "conversationUUID='" + conversationUUID + '\'' +
                ", conversationIcon='" + conversationIcon + '\'' +
                ", conversationName='" + conversationName + '\'' +
                ", assignedUUID='" + assignedUUID + '\'' +
                ", groupUUID='" + groupUUID + '\'' +
                ", conversationSummary='" + conversationSummary + '\'' +
                ", updateTimestamp=" + updateTimestamp +
                ", conversationType='" + conversationType + '\'' +
                ", isGroupType=" + isGroupType +
                '}';
    }
}
