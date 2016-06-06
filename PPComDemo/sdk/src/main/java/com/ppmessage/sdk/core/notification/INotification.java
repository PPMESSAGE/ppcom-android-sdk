package com.ppmessage.sdk.core.notification;

import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.ws.IWebSocket;

import org.json.JSONObject;

/**
 * Send and receive message from {@link IWebSocket}
 *
 * Before calling {@link INotification#start()}, please make sure you have configed {@link INotification}:
 *
 * <pre>
 *     INotification notification = PPMessageSDK.getInstance().getNotification();
 *     notification.config("CURRENT_LOGINED_USER_UUID", "CURRENT_APP_UUID");
 * </pre>
 *
 * When configed finished, then you can addListener {@link INotification#addListener(OnNotificationEvent)} to receive message which from
 * {@link IWebSocket}:
 *
 * <pre>
 *     notification.addListener(new SimpleNotificationEvent() {
 *         @Override
 *         int getInterestedEvent() {
 *              return INotification.EVENT_MESSAGE;
 *         }
 *         @Override
 *         void onMessageInfoArrived(Object messageObj) {
 *             // messageObj
 *         }
 *     });
 * </pre>
 *
 * To start it , by calling {@link INotification#start()}
 *
 * <pre>
 *     JSONObject wsAuthObject = new JSONObject();
 *     wsAuthObject.put("api_token", "YOUR_API_TOKEN");
 *     wsAuthObject.put("user_uuid", "YOUR_USER_UUID");
 *     wsAuthObject.put("device_uuid", "YOUR_USER_DEVICE_UUID");
 *     wsAuthObject.put("is_service_user", "IS_SERVICE_USER");
 *     wsAuthObject.put("app_uuid", "YOUR_APP_UUID");
 *
 *     notification.start(wsAuthObject);
 * </pre>
 *
 * Created by ppmessage on 5/6/16.
 */
public interface INotification {

    int EVENT_AUTH = 0x0001;
    int EVENT_MESSAGE = 0x0002;
    int EVENT_CONVERSATION = 0x0004;
    int EVENT_SYS = 0x0008;
    int EVENT_TYPING = 0x0010;
    int EVENT_ONLINE = 0x0020;
    int EVENT_UNKNOWN = 0x0040;
    int EVENT_MSG_SEND_OK = 0x0080;
    int EVENT_MSG_SEND_ERROR = 0x100;

    /**
     * Config INotification
     */
    interface Config {

        /**
         * Let me known your APP_UUID
         * @return
         */
        String getAppUUID();

        /**
         * Let me known who you are
         *
         * - user_uuid can not be nil
         * - device_uuid can not be nil
         * - set is_service_user
         *
         * @return
         */
        User getActiveUser();

        /**
         * Let me known your API_TOKEN
         * @return
         */
        String getApiToken();

    }

    /**
     *
     * Consider to use {@link com.ppmessage.sdk.core.notification.INotification.SimpleNotificationEvent} rather than this
     *
     * <pre>
     *     INotification notification = PPMessage.getInstance().getNotification();
     *     notification.addListener(new SimpleNotificationEvent() {
     *          @Override
     *          int getInterestedEvent() {
     *              return INotification.EVENT_MESSAGE | INotification.EVENT_CONVERSATION;
     *          }
     *          @Override
     *          void onAuthInfoArrived(Object authInfo) {
     *              // Auth info arrived
     *          }
     *          @Override
     *          void onMessageInfoArrived(Object messageInfo) {
     *              // Message info arrived
     *          }
     *     });
     * </pre>
     */
    interface OnNotificationEvent {

        /**
         * Provied your interested Event, event list:
         *
         * <pre>
         *     - INotification.EVENT_AUTH
         *     - INotification.EVENT_MESSAGE
         *     - INotification.EVENT_CONVERSATION
         *     - INotification.EVENT_SYS
         *     - INotification.EVENT_TYPING
         *     - INotification.EVENT_ONLINE
         *     - INotification.EVENT_ONKNOWN
         *     - INotification.EVENT_MSG_SEND_OK
         *     - INotification.EVENT_MSG_SEND_ERROR
         * </pre>
         *
         * and you can interested on mutiple event at the same time like this:
         *
         * <b>INotification.EVENT_AUTH | INotification.EVENT_MESSAGE | INotification.EVENT_CONVERSATION</b>
         *
         * @return
         */
        int getInterestedEvent();

        /**
         * Can not identify this message belongs to which type
         *
         * @param unknownInfo
         */
        void onUnknownInfoArrived(Object unknownInfo);

        /**
         * On auth completed
         *
         * @param authInfo
         */
        void onAuthInfoArrived(Object authInfo);

        /**
         * On new message arrived
         *
         * @param message
         */
        void onMessageInfoArrived(PPMessage message);

        /**
         * On Conversation Info arrived
         *
         * @param conversation
         */
        void onConversationInfoArrived(Conversation conversation);

        /**
         * On sys info arrived
         *
         * @param jsonObject
         */
        void onSysInfoArrived(Object jsonObject);

        /**
         * On User Typing Info arrived [NOT SUPPORTED]
         *
         * @param jsonObject
         */
        void onTypingInfoArrived(Object jsonObject);

        /**
         * On PPKefu Service User Online Info arrived [NOT SUPPORTED]
         *
         * @param jsonObject
         */
        void onOnlineInfoArrived(Object jsonObject);

        /**
         * The ack message which send through webSocket channel
         *
         * @param messageSendResult
         */
        void onMessageSendOk(WSMessageAckNotificationHandler.MessageSendResult messageSendResult);

        /**
         * Message send error
         *
         * @param messageSendResult
         */
        void onMessageSendError(WSMessageAckNotificationHandler.MessageSendResult messageSendResult);

    }

    /**
     * YOU MUST config `INotification` to let me known who you are, the config info will be used to
     * auth WebSocket, send messsage, or call some api
     *
     * @param config
     * @return
     */
    INotification config(Config config);

    /**
     * Get config
     *
     * @return
     */
    Config getConfig();

    boolean canSendMessage();

    void start();

    void stop();

    void sendMessage(PPMessage message);

    void notify(String notifyInfo);

    void addListener(OnNotificationEvent event);

    void removeListener(OnNotificationEvent event);

    /**
     *
     * <pre>
     *     getInterestedEvent() return -1, Interested on nothing by default.
     * </pre>
     *
     */
    class SimpleNotificationEvent implements OnNotificationEvent {

        @Override
        public int getInterestedEvent() {
            return -1;
        }

        @Override
        public void onUnknownInfoArrived(Object unknownInfo) {

        }

        @Override
        public void onAuthInfoArrived(Object authInfo) {

        }

        @Override
        public void onMessageInfoArrived(PPMessage message) {

        }

        @Override
        public void onConversationInfoArrived(Conversation conversation) {

        }

        @Override
        public void onSysInfoArrived(Object jsonObject) {

        }

        @Override
        public void onTypingInfoArrived(Object jsonObject) {

        }

        @Override
        public void onOnlineInfoArrived(Object jsonObject) {

        }

        @Override
        public void onMessageSendOk(WSMessageAckNotificationHandler.MessageSendResult messageSendResult) {

        }

        @Override
        public void onMessageSendError(WSMessageAckNotificationHandler.MessageSendResult messageSendResult) {

        }
    }

}
