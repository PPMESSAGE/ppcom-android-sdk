package com.ppmessage.sdk.core.bean.common;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppmessage on 5/6/16.
 */
public class Conversation implements Comparable<Conversation>, Parcelable {

    public static final String TYPE_S2S = "S2S";
    public static final String TYPE_P2S = "P2S";

    public static final String STATUS_NEW = "NEW";
    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_CLOSE = "CLOSE";

    private String conversationUUID;
    private String conversationIcon;
    private String conversationName;
    private String assignedUUID;
    private String groupUUID;
    private String userUUID;
    private String conversationSummary;
    private long updateTimestamp;
    private String conversationType;
    private boolean isGroupType;
    private int unreadCount;
    private String status;

    public Conversation() {

    }

    protected Conversation(Parcel in) {
        conversationUUID = in.readString();
        conversationIcon = in.readString();
        conversationName = in.readString();
        assignedUUID = in.readString();
        groupUUID = in.readString();
        userUUID = in.readString();
        conversationSummary = in.readString();
        updateTimestamp = in.readLong();
        conversationType = in.readString();
        isGroupType = in.readByte() != 0;
        unreadCount = in.readInt();
        status = in.readString();
    }

    public static final Creator<Conversation> CREATOR = new Creator<Conversation>() {
        @Override
        public Conversation createFromParcel(Parcel in) {
            return new Conversation(in);
        }

        @Override
        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };

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

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public static Conversation parse(PPMessageSDK sdk, JSONObject jsonObject) {
        if (jsonObject.has("error_code")) {
            try {
                if (jsonObject.getInt("error_code") != 0) return null;
            } catch (JSONException e) {
                L.e(e);
                return null;
            }
        }

        Conversation conversation = new Conversation();
        String conversationUUID = null;
        String conversationIcon = null;
        String conversationName = null;
        String conversationUserUuid = null;
        long updatetime = 0;
        boolean isGroupType = false;
        String groupUUID = null;
        String assignedUUID = null;

        try {
            isGroupType = determineIsGroupType(jsonObject);

            if (jsonObject.has("conversation_data")) {
                JSONObject conversationData = jsonObject.getJSONObject("conversation_data");
                conversationUUID = conversationData.optString("conversation_uuid", null);
                conversationName = conversationData.optString("conversation_name", null);
                conversationUserUuid = conversationData.optString("user_uuid", null);
            }

            JSONArray conversationUsers = jsonObject.optJSONArray("conversation_users");
            if (conversationUsers != null) {
                for(int i = 0; i < conversationUsers.length(); i++) {
                    JSONObject user = (JSONObject) conversationUsers.get(i);
                    if (user.optString("uuid").equals(conversationUserUuid)) {
                        continue;
                    }
                    if (conversationIcon == null) {
                        conversationIcon = Utils.getFileDownloadUrl(user.optString("user_icon"));
                        break;
                    }
                }
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
            conversation.setIsGroupType(isGroupType);
            conversation.setGroupUUID(groupUUID);
            conversation.setAssignedUUID(assignedUUID);
            conversation.setUserUUID(Utils.safeNull(jsonObject.optString("user_uuid", null)));
            conversation.setStatus(Utils.safeNull(jsonObject.optString("status", null)));

            PPMessage latestMessage = tryParseConversationLatestMessage(sdk, conversation, jsonObject);
            conversation.setConversationSummary(tryParseConversationSummary(latestMessage, sdk.getContext(), jsonObject));
            if (latestMessage != null) {
                // Try fix conversation's timestamp with message's timestamp
                // Because conversation's timestamp not an unix timestamp
                updatetime = latestMessage.getTimestamp() > 0 ? latestMessage.getTimestamp() : updatetime;
            }
            conversation.setUpdateTimestamp(updatetime);

            // Sometimes, We can not get the conversation_name -_-||
            if (null == conversation.getConversationName()) {
                if (conversation.getConversationType() != null && conversation.getConversationType().equals("P2S")) {
                    if (jsonObject.has("from_user")) {
                        User fromUser = User.parse(jsonObject.getJSONObject("from_user"));
                        if (fromUser != null) {
                            conversation.setConversationName(fromUser.getName());
                        }
                    }
                }
            }

        } catch (JSONException e) {
            L.e(e);
        }

        return conversation;
    }

    private static PPMessage tryParseConversationLatestMessage(final PPMessageSDK messageSDK, final Conversation conversation, JSONObject jsonObject) {
        String jsonMessageBody = null;
        try {

            if (jsonObject.has("latest_message")) {
                JSONObject latest = jsonObject.getJSONObject('latest_message');
                if (latest == null) return null;

                jsonMessageBody = latest.getString("message_body");
            } else if (jsonObject.has("message_body")) {
                jsonMessageBody = jsonObject.getString("message_body");
            }

            if (jsonMessageBody != null) {
                PPMessage message = PPMessage.parse(messageSDK, new JSONObject(jsonMessageBody));
                return message;
            }

        } catch (JSONException e) {
            L.e(e);
        }

        return null;
    }

    private static String tryParseConversationSummary(final PPMessage latestMessage, Context context, JSONObject jsonObject) {
        if (latestMessage != null) {
            return PPMessage.summary(context, latestMessage);
        }
        return jsonObject.optString("group_desc", null);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(conversationUUID);
        dest.writeString(conversationIcon);
        dest.writeString(conversationName);
        dest.writeString(assignedUUID);
        dest.writeString(groupUUID);
        dest.writeString(userUUID);
        dest.writeString(conversationSummary);
        dest.writeLong(updateTimestamp);
        dest.writeString(conversationType);
        dest.writeByte((byte) (isGroupType ? 1 : 0));
        dest.writeInt(unreadCount);
        dest.writeString(status);
    }
}
