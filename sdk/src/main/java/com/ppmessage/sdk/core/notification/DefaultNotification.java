package com.ppmessage.sdk.core.notification;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageException;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.bean.common.Conversation;
import com.ppmessage.sdk.core.bean.message.OnGetJsonObjectEvent;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.bean.message.PPMessageAdapter;
import com.ppmessage.sdk.core.ws.AndroidAsyncWebSocketImpl;
import com.ppmessage.sdk.core.ws.IWebSocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by ppmessage on 5/9/16.
 */
public class DefaultNotification implements INotification, INotificationHandler.OnNotificationHandleEvent {

    private IWebSocket webSocket;
    private List<OnNotificationEvent> listeners = new CopyOnWriteArrayList<>();
    private NotificationHandlerFactory notificationHandlerFactory;

    private Config config;
    private PPMessageSDK sdk;

    private static final String WS_MEET_ERROR_LOG = "[WebSocket] meet error: %s";
    private static final String CONFIG_ERROR_LOG = "Config can not be empty";
    private static final String WS_NOT_OPEN_LOG = "[WebSocket] not open";
    private static final String ASYNC_GET_MESSAGE_BODY_ERROR_LOG = "get message body error: %s";
    private static final String WS_SEND_MESSAGE_LOG = "[WebSocket] send string: %s";
    private static final String WS_MESSAGE_ARRIVED_LOG = "[WebSocket] message arrived: %s";
    private static final String WS_OPEN_LOG = "[WebSocket] Open";
    private static final String WS_CLOSE_LOG = "[WebSocket] Closed";
    private static final String WS_BROADCST_EVENT = "[WebSocket] broadcast message, current listeners: %d";
    private static final String WS_TRY_TO_CLOSE_LOG = "[WebSocket] try to close it";
    private static final String WS_NOTIFICATION_ACTIVEUSER_LOG = "[Notification] set activeuser: %s";

    public DefaultNotification(PPMessageSDK sdk, IWebSocket webSocket) {
        this.sdk = sdk;
        this.webSocket = webSocket;

        notificationHandlerFactory = new NotificationHandlerFactory(sdk);
    }

    @Override
    public INotification config(Config config) {
        this.config = config;
        if (this.config != null) {
            L.d(WS_NOTIFICATION_ACTIVEUSER_LOG, config.getActiveUser());
        }
        return this;
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    @Override
    public boolean canSendMessage() {
        checkConfig();
        return webSocket != null;
    }

    @Override
    public void start() {
        checkConfig();

        final JSONObject wsAuthObject = getWSAuthObject();
        webSocket.setCallback(new IWebSocket.IWebSocketEvent() {

            @Override
            public void onOpen(IWebSocket webSocket) {
                if (wsAuthObject != null) {
                    authWebSocket(wsAuthObject);
                }
            }

            @Override
            public void onMessageArrived(IWebSocket webSocket, String message) {
                L.d(WS_MESSAGE_ARRIVED_LOG, message);
                notificationHandlerFactory.handle(message, DefaultNotification.this);
            }

            @Override
            public void onClose(IWebSocket webSocket) {
                //TODO should't simply set webSocket = null here
                L.w(WS_CLOSE_LOG);
                webSocket = null;
            }

            @Override
            public void onError(IWebSocket webSocket, Exception e) {
                L.e(WS_MEET_ERROR_LOG, e != null ? e.toString() : "null");
                //TODO should't simply set webSocket = null here
                webSocket = null;
            }
        });

        L.d(WS_OPEN_LOG);
        webSocket.open();
    }

    @Override
    public void stop() {
        L.w(WS_TRY_TO_CLOSE_LOG);
        if (webSocket == null) return;
        webSocket.close();
    }

    @Override
    public void sendMessage(final PPMessage message) {
        if (webSocket != null) {
            new PPMessageAdapter(sdk, message)
                    .asyncGetWSJsonObject(new OnGetJsonObjectEvent() {

                        @Override
                        public void onCompleted(JSONObject jsonObject) {
                            if (webSocket != null) {
                                sendDataByWebSocket(jsonObject.toString());
                            } else {
                                L.d(WS_NOT_OPEN_LOG);
                                onMessageSendFailed(message);
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            L.d(ASYNC_GET_MESSAGE_BODY_ERROR_LOG, e != null ? e.toString() : "null");
                            onMessageSendFailed(message);
                        }

                    });
        } else {
            L.d(WS_NOT_OPEN_LOG);
            onMessageSendFailed(message);
        }
    }

    @Override
    public void notify(String notifyInfo) {
        if (notifyInfo != null) {
            notificationHandlerFactory.handle(notifyInfo, this);
        }
    }

    @Override
    public void addListener(OnNotificationEvent event) {
        listeners.add(event);
    }

    @Override
    public void removeListener(OnNotificationEvent event) {
        listeners.remove(event);
    }

    protected void sendDataByWebSocket(String string) {
        if (webSocket != null) {
            webSocket.send(string);
            L.d(WS_SEND_MESSAGE_LOG, string);
        }
    }

    private void checkConfig() {
        if (config == null) {
            throw new PPMessageException(CONFIG_ERROR_LOG);
        }
    }

    private JSONObject getWSAuthObject() {
        JSONObject wsAuthObject = new JSONObject();
        try {
            wsAuthObject.put("api_token", config.getApiToken());
            wsAuthObject.put("user_uuid", config.getActiveUser().getUuid());
            wsAuthObject.put("device_uuid", config.getActiveUser().getDeviceUUID());
            wsAuthObject.put("is_service_user", config.getActiveUser().isServiceUser());
            wsAuthObject.put("app_uuid", config.getAppUUID());
            wsAuthObject.put("extra_data", new JSONObject());
        } catch (JSONException e) {
            L.e(e);
        }
        return wsAuthObject;
    }

    private void onMessageSendFailed(PPMessage message) {
        DefaultNotification.this
                .onCompleted(
                        INotification.EVENT_MSG_SEND_ERROR,
                        new WSMessageAckNotificationHandler
                                .MessageSendResult(
                                message.getConversation().getConversationUUID(),
                                message.getMessageID()));
    }

    private void authWebSocket(JSONObject wsAuthObject) {
        if (wsAuthObject != null) {

            try {
                wsAuthObject.put("type", "auth");
            } catch (JSONException e) {
                L.e(e);
            }

            sendDataByWebSocket(wsAuthObject.toString());
        }
    }

    @Override
    public void onCompleted(int eventType, Object obj) {
        L.d(WS_BROADCST_EVENT, listeners.size());

        for (OnNotificationEvent e : listeners) {
            int interestedEvent = e.getInterestedEvent() & eventType;

            if ((interestedEvent & INotification.EVENT_AUTH) != 0) {
                e.onAuthInfoArrived(obj);
            } else if ((interestedEvent & INotification.EVENT_UNKNOWN) != 0) {
                e.onUnknownInfoArrived(obj);
            } else if ((interestedEvent & INotification.EVENT_CONVERSATION) != 0) {
                e.onConversationInfoArrived((Conversation)obj);
            } else if ((interestedEvent & INotification.EVENT_MESSAGE) != 0) {
                e.onMessageInfoArrived((PPMessage)obj);
            } else if ((interestedEvent & INotification.EVENT_ONLINE) != 0) {
                e.onOnlineInfoArrived(obj);
            } else if ((interestedEvent & INotification.EVENT_SYS) != 0) {
                e.onSysInfoArrived(obj);
            } else if ((interestedEvent & INotification.EVENT_TYPING) != 0) {
                e.onTypingInfoArrived(obj);
            } else if ((interestedEvent & INotification.EVENT_MSG_SEND_OK) != 0) {
                e.onMessageSendOk((WSMessageAckNotificationHandler.MessageSendResult)obj);
            } else if ((interestedEvent & INotification.EVENT_MSG_SEND_ERROR) != 0) {
                e.onMessageSendError((WSMessageAckNotificationHandler.MessageSendResult)obj);
            }
        }
    }
}
