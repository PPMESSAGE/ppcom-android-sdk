package com.ppmessage.ppcomlib.services;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.ppcomlib.PPComSDKException;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.api.IToken;
import com.ppmessage.sdk.core.bean.common.App;
import com.ppmessage.sdk.core.bean.common.User;
import com.ppmessage.sdk.core.notification.INotification;

/**
 * Help ppcom to startup
 *
 * 1. get app info
 * 2. create ppcom user
 * 3. build WebSocket connection
 *
 * Example:
 *
 * <pre>
 *     PPComSDK sdk = PPComSDK.getInstance();
 *
 *     PPComStartupHelper startUp = new PPComStartupHelper(sdk);
 *     startUp.startUp(new OnStartupCallback() {
 *         @Override
 *         void onSuccess() {
 *              // on startUp success
 *         }
 *         @Override
 *         void onError(PPComSDKException exception) {
 *             // on startup exception
 *         }
 *     });
 * </pre>
 *
 * Created by ppmessage on 5/16/16.
 */
public class PPComStartupHelper {

    public interface OnStartupCallback {
        void onSuccess();
        void onError(PPComSDKException exception);
    }

    enum StartupState {
        NULL,
        STARTUPING,
        STARTUP_ERROR,
        STARTUP_SUCCESS
    }

    private static final String LOG_GET_USER_ERROR = "[StartUp] can not get ppcom user";
    private static final String LOG_GET_APP_ACCESS_TOKEN_ERROR = "[StartUp] can not get access token";
    private static final String LOG_IN_STARTUP = "[StartUp] In startup";
    private static final String LOG_CAN_NOT_GET_APP_INFO = "[StartUp] can not get app info";

    private PPComSDK sdk;
    private PPComUser comUser;
    private PPComApp comApp;
    private StartupState state;

    public PPComStartupHelper(PPComSDK sdk) {
        this.sdk = sdk;
        this.comUser = new PPComUser(sdk);
        this.comApp = new PPComApp(sdk);
        state = StartupState.NULL;
    }

    public void startUp(final OnStartupCallback event) {
        if (state == StartupState.STARTUPING) {
            if (event != null) {
                event.onError(new PPComSDKException(LOG_IN_STARTUP));
            }
            return;
        }

        if (state == StartupState.STARTUP_SUCCESS && isInnerStateOk()) {
            if (event != null) {
                event.onSuccess();
            }
            return;
        }

        // Begin StartUp
        state = StartupState.STARTUPING;
        this.comApp.get(new PPComApp.OnGetAppCallback() {
            @Override
            public void onCompleted(App app) {
                if (app == null) {
                    L.w(LOG_CAN_NOT_GET_APP_INFO);
                    publishState(StartupState.STARTUP_ERROR);
                    if (event != null) {
                        event.onError(new PPComSDKException(LOG_CAN_NOT_GET_APP_INFO));
                    }
                } else {
                    getPPComUser(event);
                }
            }
        });
    }

    public PPComUser getComUser() {
        return comUser;
    }

    public PPComApp getComApp() {
        return comApp;
    }

    private void getPPComUser(final OnStartupCallback event) {
        this.comUser.getUser(new PPComUser.OnGetPPComUserEvent() {
            @Override
            public void onCompleted(User user) {
                if (user == null) {
                    publishState(StartupState.STARTUP_ERROR);
                    if (event != null) {
                        L.w(LOG_GET_USER_ERROR);
                        event.onError(new PPComSDKException(LOG_GET_USER_ERROR));
                    }
                    return;
                }

                buildWebSocketConnection(user, event);
            }
        });
    }

    private void buildWebSocketConnection(final User user, final OnStartupCallback event) {
        PPMessageSDK messageSDK = sdk.getConfiguration().getMessageSDK();
        final INotification notification = messageSDK.getNotification();

        messageSDK.getToken().getApiToken(sdk.getConfiguration().getAppUUID(), new IToken.OnRequestTokenEvent() {
            @Override
            public void onGetToken(String accessToken) {
                if (accessToken == null) {
                    L.w(LOG_GET_APP_ACCESS_TOKEN_ERROR);
                    publishState(StartupState.STARTUP_ERROR);
                    if (event != null) {
                        event.onError(new PPComSDKException(LOG_GET_APP_ACCESS_TOKEN_ERROR));
                    }
                } else {
                    if (event != null) {
                        configuraMessageSDKNotification(notification, accessToken, user);
                        event.onSuccess();
                    }
                }
            }
        });
    }

    private void configuraMessageSDKNotification(INotification notification, final String apiToken, final User user) {
        notification.config(new INotification.Config() {
            @Override
            public String getAppUUID() {
                return sdk.getConfiguration().getAppUUID();
            }

            @Override
            public User getActiveUser() {
                return user;
            }

            @Override
            public String getApiToken() {
                return apiToken;
            }
        });

        publishState(StartupState.STARTUP_SUCCESS);
        notification.start();
    }

    private boolean isInnerStateOk() {
        return sdk.getConfiguration().getMessageSDK().getNotification().canSendMessage();
    }

    private void publishState(StartupState state) {
        this.state = state;
    }

}
