package com.ppmessage.sdk.core.notification;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

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
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

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
    private static final String WS_BROADCST_EVENT = "[WebSocket] broadcast message, current listeners: %d, listener:%s";
    private static final String WS_TRY_TO_CLOSE_LOG = "[WebSocket] try to close it";
    private static final String WS_NOTIFICATION_ACTIVEUSER_LOG = "[Notification] set activeuser: %s";
    private static final String WS_STARTED_OR_STARTING = "[WebSocket] websocket started or starting, skip re-start";
    private static final String WS_AUTH_ACTIVE_USER_EMPTY = "[WebSocket] try auth websocket, but config.active_user == null";

    private static final String AUTORECONNECT_COUNT_ARRIVED = "[WebSocket] auto reconnect achieve max count:%d, current:%d";
    private static final String AUTORECONNECT_STOPED = "[WebSocket] auto reconnect stop manaually";
    private static final String AUTORECONNECT_BEGIN = "[WebSocket] begin auto reconnect, retry count: %d";
    private static final String AUTORECONNECT_STOP = "[WebSocket] stop auto reconnect";

    /** retry connect count **/
    private static final int MAX_TRY_AUTO_RECONNECT_COUNT = 3;
    /** what message send by Handler **/
    private static final int WHAT_RECONNECT = 1;
    /** delay in millseconds **/
    private static final int DELAY_AUTO_RECONNECT_IN_MILLISECONDS = 5000;
    private int currentRetryCount = 0;
    private boolean stop;
    private Handler autoReconnectHandler;
    private Runnable autoReconnectRunnable;

    public DefaultNotification(PPMessageSDK sdk) {
        this.sdk = sdk;
        this.stop = false;
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
    public boolean isConfigurationValid() {
        return this.config != null &&
                this.config.getActiveUser() != null &&
                this.config.getApiToken() != null &&
                this.config.getAppUUID() != null;
    }

    @Override
    public boolean canSendMessage() {
        checkConfig();
        return webSocket != null && webSocket.isOpen();
    }

    @Override
    public synchronized void start() {
        checkConfig();

        if (isConnectingOrConnected()) {
            L.w(WS_STARTED_OR_STARTING);
            return;
        }

        final JSONObject wsAuthObject = getWSAuthObject();
        getWebSocket().setCallback(new IWebSocket.IWebSocketEvent() {

            @Override
            public void onOpen(IWebSocket webSocket) {
                if (wsAuthObject != null) {
                    authWebSocket(wsAuthObject);
                }
                resetAutoReconnect();
            }

            @Override
            public void onMessageArrived(IWebSocket webSocket, String message) {
                L.d(WS_MESSAGE_ARRIVED_LOG, message);
                notificationHandlerFactory.handle(message, DefaultNotification.this);
            }

            @Override
            public void onClose(IWebSocket webSocket) {
                L.w(WS_CLOSE_LOG);
                onWebSocketClosedOrMeetError();
            }

            @Override
            public void onError(IWebSocket webSocket, Exception e) {
                L.e(WS_MEET_ERROR_LOG, e != null ? e.toString() : "null");
                onWebSocketClosedOrMeetError();
            }
        });

        L.d(WS_OPEN_LOG);
        getWebSocket().open();
    }

    @Override
    public synchronized void stop() {
        L.w(WS_TRY_TO_CLOSE_LOG);
        this.stop = true;
        if (webSocket == null) return;
        webSocket.close();
        webSocket = null;
        resetAutoReconnect();
    }

    @Override
    public void sendMessage(final PPMessage message) {
        if (webSocket != null && webSocket.isOpen()) {
            new PPMessageAdapter(sdk, message)
                    .asyncGetWSJsonObject(new OnGetJsonObjectEvent() {

                        @Override
                        public void onCompleted(JSONObject jsonObject) {
                            if (webSocket != null && webSocket.isOpen()) {
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

    @Override
    public List<OnNotificationEvent> listeners() {
        return listeners;
    }

    protected void sendDataByWebSocket(String string) {
        if (webSocket != null) {
            webSocket.send(string);
            L.d(WS_SEND_MESSAGE_LOG, string);
        }
    }

    @Override
    public void onCompleted(int eventType, Object obj) {
        L.d(WS_BROADCST_EVENT, listeners.size(), listeners);

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

    protected IWebSocket getWebSocket() {
        if (webSocket == null) {
            webSocket = new AndroidAsyncWebSocketImpl(sdk);
        }
        return webSocket;
    }

    private void checkConfig() {
        if (config == null) {
            throw new PPMessageException(CONFIG_ERROR_LOG);
        }
    }

    private JSONObject getWSAuthObject() {
        JSONObject wsAuthObject = new JSONObject();

        if (config.getActiveUser() == null) {
            L.w(WS_AUTH_ACTIVE_USER_EMPTY);
            return wsAuthObject;
        }

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

    private boolean isConnectingOrConnected() {
        return webSocket != null;
    }

    // =================================
    // WebSocket - Auto Reconnect
    // =================================
    protected boolean autoReconnectWhenMeetError() {
        return true;
    }

    private void onWebSocketClosedOrMeetError() {
        DefaultNotification.this.webSocket = null;

        if (autoReconnectWhenMeetError()) {
            startReconnect();
        }
    }

    private void startReconnect() {
        if (currentRetryCount > MAX_TRY_AUTO_RECONNECT_COUNT) {
            L.w(AUTORECONNECT_COUNT_ARRIVED, MAX_TRY_AUTO_RECONNECT_COUNT, currentRetryCount);
            resetAutoReconnect();
            return;
        }

        if (this.stop) {
            L.w(AUTORECONNECT_STOPED);
            resetAutoReconnect();
            return;
        }

        check();
        currentRetryCount++;
        L.d(AUTORECONNECT_BEGIN, currentRetryCount);

        if (autoReconnectHandler != null && autoReconnectRunnable != null) {
            autoReconnectHandler.postDelayed(autoReconnectRunnable, DELAY_AUTO_RECONNECT_IN_MILLISECONDS);
        }
    }

    private void check() {
        if (autoReconnectHandler == null) {
            autoReconnectHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    switch (msg.what) {
                        case WHAT_RECONNECT:
                            start();
                            break;
                    }
                }
            };
        }
        if (autoReconnectRunnable == null) {
            autoReconnectRunnable = new Runnable() {
                @Override
                public void run() {
                    if (autoReconnectHandler != null) {
                        autoReconnectHandler.sendEmptyMessage(WHAT_RECONNECT);
                    }
                }
            };
        }
    }

    private void resetAutoReconnect() {
        if (autoReconnectHandler != null) {
            autoReconnectHandler.removeCallbacks(autoReconnectRunnable);
            autoReconnectHandler = null;
        }
        if (autoReconnectRunnable != null) {
            autoReconnectRunnable = null;
        }
        currentRetryCount = 0;

        L.d(AUTORECONNECT_STOP);
    }

}
