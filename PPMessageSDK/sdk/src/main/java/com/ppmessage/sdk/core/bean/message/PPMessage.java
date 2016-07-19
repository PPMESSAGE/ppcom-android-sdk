package com.ppmessage.sdk.core.bean.message;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.ppmessage.sdk.R;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.query.IQuery;
import com.ppmessage.sdk.core.utils.TxtLoader;
import com.ppmessage.sdk.core.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppmessage on 5/6/16.
 */
public class PPMessage implements Parcelable {

    private static final String LOG_FAILED_GET_CONVERSATION = "[PPMessage] can not get conversation: %s";
    private static final String LOG_FAILED_GET_FROM_USER = "[PPMessage] can not get from_user: %s";
    private static final String LOG_FAILED_GET_MEDIA_ITEM = "[PPMessage] can not get media item: %s";
    private static final String LOG_FAILED_GET_TXT_CONTENT = "[PPMessage] can not get txt content: %s";

    public static final String TYPE_UNKNOWN = "UNKNOWN";
    public static final String TYPE_TEXT = "TEXT";
    public static final String TYPE_TXT = "TXT";
    public static final String TYPE_IMAGE = "IMAGE";
    public static final String TYPE_FILE = "FILE";
    public static final String TYPE_AUDIO = "AUDIO";

    public static final int DIRECTION_OUTGOING = 0;
    public static final int DIRECTION_INCOMING = 1;

    public PPMessage() {

    }

    protected PPMessage(Parcel in) {
        direction = in.readInt();
        messageSubType = in.readString();
        messageID = in.readString();
        messageBody = in.readString();
        messagePushID = in.readString();
        error = in.readByte() != 0;
        mediaItem = in.readParcelable(IPPMessageMediaItem.class.getClassLoader());
        conversation = in.readParcelable(Conversation.class.getClassLoader());
        fromUser = in.readParcelable(User.class.getClassLoader());
        toUser = in.readParcelable(User.class.getClassLoader());
        timestamp = in.readLong();
    }

    public static final Creator<PPMessage> CREATOR = new Creator<PPMessage>() {
        @Override
        public PPMessage createFromParcel(Parcel in) {
            return new PPMessage(in);
        }

        @Override
        public PPMessage[] newArray(int size) {
            return new PPMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(direction);
        dest.writeString(messageSubType);
        dest.writeString(messageID);
        dest.writeString(messageBody);
        dest.writeString(messagePushID);
        dest.writeByte((byte) (error ? 1 : 0));
        dest.writeParcelable(mediaItem, flags);
        dest.writeParcelable(conversation, flags);
        dest.writeParcelable(fromUser, flags);
        dest.writeParcelable(toUser, flags);
        dest.writeLong(timestamp);
    }

    public interface onParseListener {

        void onCompleted(PPMessage message);

    }

    private int direction;
    private String messageSubType;
    private String messageID;
    private String messageBody;
    private String messagePushID;
    private boolean error;
    private IPPMessageMediaItem mediaItem;
    private Conversation conversation;
    private User fromUser;
    private User toUser;
    private long timestamp;

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getMessagePushID() {
        return messagePushID;
    }

    public void setMessagePushID(String messagePushID) {
        this.messagePushID = messagePushID;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public IPPMessageMediaItem getMediaItem() {
        return mediaItem;
    }

    public void setMediaItem(IPPMessageMediaItem mediaItem) {
        this.mediaItem = mediaItem;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public String getMessageSubType() {
        return messageSubType;
    }

    public void setMessageSubType(String messageSubType) {
        this.messageSubType = messageSubType;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * In millseconds
     * @return
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * In millseconds
     * @param timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // ==========================
    // Parse API
    // ==========================

    /**
     * Parse jsonObject to PPmessage
     *
     * [NOTE]:
     *
     * - Won't check `from_user`, Only set a simple User which userUUID is not empty
     * - Won't async load conversation info, Only set a simple Conversation which conversationUUID is not empty
     * - Won't async load large txt
     *
     * @param jsonObject
     * @return
     */
    public static PPMessage parse(PPMessageSDK sdk, JSONObject jsonObject) {
        final PPMessage message = new PPMessage();
        try {
            String body = jsonObject.getString("bo");
            String fromUUID = jsonObject.getString("fi");

            message.setMessageBody(body);
            message.setMessageSubType(jsonObject.getString("ms"));
            message.setMessagePushID(jsonObject.has("pid") ? jsonObject.getString("pid") : null);
            message.setMessageID(jsonObject.getString("id"));
            message.setTimestamp((long) (jsonObject.getDouble("ts") * 1000));
            message.setDirection(sdk.getNotification()
                    .getConfig().getActiveUser().getUuid().equals(fromUUID) ? DIRECTION_OUTGOING : DIRECTION_INCOMING);

            User fromUser = new User();
            fromUser.setUuid(fromUUID);

            Conversation conversation = new Conversation();
            conversation.setConversationUUID(jsonObject.has("ci") ? jsonObject.getString("ci") : null);
            message.setConversation(conversation);

            message.setFromUser(fromUser);
        } catch (JSONException e) {
            L.e(e);
            message.setError(true);
        }
        return message;
    }

    /**
     * Asyns parse jsonObject to PPMessage
     *
     * @param sdk
     * @param jsonObject
     * @param parseListener
     */
    public static void asyncParse(final PPMessageSDK sdk, JSONObject jsonObject, final onParseListener parseListener) {
        try {

            final JSONObject messageJsonObject = jsonObject.has("message_body") ?
                    new JSONObject(jsonObject.getString("message_body")) :
                    jsonObject;
            final PPMessage message = parse(sdk, messageJsonObject);

            // From user
            if (jsonObject.has("from_user")) {
                message.setFromUser(User.parse(jsonObject.getJSONObject("from_user")));
                // Async get conversation
                // Async get MediaItem
                asyncParseMessageConversation(sdk, messageJsonObject, message, parseListener);
            } else {
                // jsonObject not contains `from_user` fileds, this will happens when the message was get from
                // the api interface '/GET_UNACKED_MESSAGES', the message won't contain from_user, only contains
                // `fi` fileds, so we have to try our best effort to get from_user as possible as we can.
                asyncParseMessageFromUser(sdk, jsonObject, message, new onParseListener() {
                    @Override
                    public void onCompleted(PPMessage message) {
                        try {
                            asyncParseMessageConversation(sdk, messageJsonObject, message, parseListener);
                        } catch (JSONException e) {
                            L.e(e);
                            if (parseListener != null) parseListener.onCompleted(message);
                        }
                    }
                });
            }
        } catch (JSONException e) {
            L.e(e);
        }
    }

    // try get from user
    private static void asyncParseMessageFromUser(final PPMessageSDK sdk,
                                                  final JSONObject jsonObject,
                                                  final PPMessage message,
                                                  final onParseListener parseListener) {
        // fromUser only contains {ci: xxx}, not contains fromName and fromIcon and so on.
        final String userUUID = (message.getFromUser() != null ? message.getFromUser().getUuid() : null);
        // userUUID is null
        if (userUUID == null) {
            L.w(LOG_FAILED_GET_FROM_USER, jsonObject);
            if (parseListener != null) {
                parseListener.onCompleted(message);
            }
            return;
        }

        sdk.getDataCenter().queryUser(userUUID, new IQuery.OnQueryCallback() {
            @Override
            public void onCompleted(Object object) {
                if (object != null && object instanceof User) {
                    message.setFromUser((User) object);
                } else {
                    L.w(LOG_FAILED_GET_FROM_USER, jsonObject);
                }
                if (parseListener != null) {
                    parseListener.onCompleted(message);
                }
            }
        });
    }

    private static void asyncParseMessageConversation(final PPMessageSDK sdk,
                                                      final JSONObject jsonObject,
                                                      final PPMessage message,
                                                      final onParseListener parseListener) throws JSONException {
        String conversationUUID = jsonObject.getString("ci");
        sdk.getDataCenter().queryConversation(conversationUUID, new IQuery.OnQueryCallback() {
            @Override
            public void onCompleted(Object object) {
                if (object != null) {
                    Conversation conversation = (Conversation) object;
                    message.setConversation(conversation);
                } else {
                    L.w(LOG_FAILED_GET_CONVERSATION, jsonObject.toString());
                    message.setError(true);
                }

                // Continue to parse, even the message has error
                try {
                    asyncParseMessageMediaItem(sdk, jsonObject, message, parseListener);
                } catch (JSONException e) {
                    L.e(e);

                    if (parseListener != null) {
                        parseListener.onCompleted(message);
                    }
                }
            }
        });
    }

    private static void asyncParseMessageMediaItem(PPMessageSDK sdk,
                                                   final JSONObject jsonObject,
                                                   final PPMessage message,
                                                   final onParseListener parseListener) throws JSONException {
        String messageSubType = message.getMessageSubType();
        String messageBody = message.getMessageBody();
        if (messageSubType.equals(TYPE_TEXT) || messageSubType.equals(TYPE_UNKNOWN)) { // TEXT || UNKNOWN
            if (parseListener != null) {
                parseListener.onCompleted(message);
            }
            return;
        }

        IPPMessageMediaItem mediaItem = PPMessageMediaItemFactory.getMediaItem(messageSubType, new JSONObject(messageBody));
        message.setMediaItem(mediaItem);
        if (mediaItem != null) {
            if (mediaItem.getType().equals(TYPE_TXT)) {
                final PPMessageTxtMediaItem txtMediaItem = (PPMessageTxtMediaItem) mediaItem;
                Utils.getTxtLoader().loadTxt(txtMediaItem.getTxtUrl(), new TxtLoader.OnTxtLoadEvent() {
                    @Override
                    public void onCompleted(String text) {
                        txtMediaItem.setTextContent(text);
                        if (!message.isError()) message.setError(text == null);

                        if (text == null) L.w(LOG_FAILED_GET_TXT_CONTENT, jsonObject.toString());

                        if (parseListener != null) {
                            parseListener.onCompleted(message);
                        }
                    }
                });
            } else {
                if (parseListener != null) {
                    parseListener.onCompleted(message);
                }
            }
        } else {
            message.setError(true);
            L.w(LOG_FAILED_GET_MEDIA_ITEM, jsonObject.toString());
            if (parseListener != null) {
                parseListener.onCompleted(message);
            }
        }

    }

    /**
     * Get message summary
     *
     * @param message
     * @return
     */
    public static String summary(Context context, PPMessage message) {
        String messageSubType = message.getMessageSubType();
        if (messageSubType.equals(PPMessage.TYPE_TEXT)) {
            return message.messageBody;
        } else if (messageSubType.equals(PPMessage.TYPE_TXT)) {
            PPMessageTxtMediaItem txtMediaItem = (PPMessageTxtMediaItem) message.getMediaItem();
            if (txtMediaItem != null && txtMediaItem.getTextContent() != null) {
                return txtMediaItem.getTextContent();
            }
            return context.getString(R.string.pp_message_summary_txt);
        } else if (messageSubType.equals(PPMessage.TYPE_IMAGE)) {
            return context.getString(R.string.pp_message_summary_image);
        } else if (messageSubType.equals(PPMessage.TYPE_FILE)) {
            return context.getString(R.string.pp_message_summary_file);
        } else if (messageSubType.equals(PPMessage.TYPE_AUDIO)) {
            return context.getString(R.string.pp_message_summary_audio);
        }
        return context.getString(R.string.pp_message_summary_unknown);
    }

    // === Builder ===

    /**
     * Use this class to let us easy build a message to send to server
     *
     * Prepare:
     *
     * <pre>
     *     Conversation conversation = new Conversation();
     *     conversation.setConversationUUID("CONVERSATION_UUID");
     *     conversation.setConversationType("P2S");
     *
     *     User fromUser = new User();
     *     fromUser.setUuid("FROM_USER_UUID");
     * </pre>
     *
     * Build TEXT message:
     *
     * <pre>
     *     String messageToBeSend = "THIS IS SHORT TEXT";
     *
     *     new PPMessage.Builder()
     *          .setConversation(conversation)
     *          .setFromUser(fromUser)
     *          .setMessageBody(messageToBeSend)
     *          .build();
     * </pre>
     *
     * Build TXT message: same as build TEXT message, {@link com.ppmessage.sdk.core.bean.message.PPMessage.Builder} will recognize is this text a large TEXT
     *
     * Build FILE message:
     *
     * <pre>
     *     File f = new File("WAITING_UPLOADED_FILE");
     *
     *     PPMessageFileMediaItem fileMediaItem = new PPMessageFileMediaItem();
     *     fileMediaItem.setFile(f);
     *
     *     new PPMessage.Builder()
     *          .setConversation(conversation)
     *          .setFromUser(fromUser)
     *          .setMediaItem(fileMediaItem)
     *          .build();
     * </pre>
     *
     * Build IMAGE message:
     *
     * <pre>
     *     File imageFile = new Filie("WAITING_UPLOADED_IMAGE_FILE");
     *
     *     PPMessageImageMediaItem imageMediaItem = new PPMessageImageMediaItem();
     *     imageMediaItem.setFile(imageFile);
     *
     *     new PPmessage.Builder()
     *          .setConversation(conversation)
     *          .setFromUser(fromUser)
     *          .setMediaItem(imageMediaItem)
     *          .build();
     * </pre>
     *
     * Finally, through {@link com.ppmessage.sdk.core.notification.INotification#sendMessage(PPMessage)} to send it
     *
     * <pre>
     *     PPMessage message = THE MESSAGE YOU JUST BUILD;
     *     INotification notification = PPMessageSDK.getInstance(getContext()).getNotification();
     *     notification.sendMessage(message);
     * </pre>
     */
    public static class Builder {

        private Conversation conversation;
        private User fromUser;
        private IPPMessageMediaItem mediaItem;
        private String messageBody;

        public Builder setConversation(Conversation conversation) {
            this.conversation = conversation;
            return this;
        }

        public Builder setFromUser(User fromUser) {
            this.fromUser = fromUser;
            return this;
        }

        public Builder setMediaItem(IPPMessageMediaItem mediaItem) {
            this.mediaItem = mediaItem;
            return this;
        }

        public Builder setMessageBody(String messageBody) {
            this.messageBody = messageBody;
            return this;
        }

        public PPMessage build() {
            PPMessage message = new PPMessage();
            message.setMessageSubType(determineType());
            message.setTimestamp(Utils.getCurrentTimestamp());
            message.setMessageID(Utils.randomUUID());
            message.setMessageBody(this.messageBody);
            message.setConversation(this.conversation);
            message.setMediaItem(this.mediaItem);
            message.setDirection(DIRECTION_OUTGOING);
            message.setFromUser(this.fromUser);

            return message;
        }

        private String determineType() {
            if (mediaItem == null) {
                if (Utils.isTextLargeThan128(this.messageBody)) {
                    PPMessageTxtMediaItem txtMediaItem = new PPMessageTxtMediaItem();
                    txtMediaItem.setTextContent(this.messageBody);
                    this.mediaItem = txtMediaItem;
                    return TYPE_TXT;
                }
                return TYPE_TEXT;
            }
            if (mediaItem instanceof PPMessageTxtMediaItem) return TYPE_TXT;
            if (mediaItem instanceof PPMessageFileMediaItem) return TYPE_FILE;
            if (mediaItem instanceof PPMessageImageMediaItem) return TYPE_IMAGE;
            if (mediaItem instanceof PPMessageAudioMediaItem) return TYPE_AUDIO;
            return TYPE_TEXT;
        }

    }

}
