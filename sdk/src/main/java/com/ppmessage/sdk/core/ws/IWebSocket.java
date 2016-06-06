package com.ppmessage.sdk.core.ws;

/**
 * Created by ppmessage on 5/6/16.
 */
public interface IWebSocket {

    interface IWebSocketEvent {

        /**
         * On WebSocket opened event
         *
         * @param webSocket
         */
        void onOpen(IWebSocket webSocket);

        /**
         * On message arrived event
         *
         * @param webSocket
         * @param message
         */
        void onMessageArrived(IWebSocket webSocket, String message);

        /**
         * On webSocket closed event
         *
         * @param webSocket
         */
        void onClose(IWebSocket webSocket);

        /**
         * On webSocket meet error
         *
         * @param e
         */
        void onError(IWebSocket webSocket, Exception e);

    }

    /**
     * Open WebSocket
     */
    void open();

    /**
     * Close WebSocket
     */
    void close();

    /**
     * Send data by IWebSocekt
     *
     * @param data
     */
    void send(String data);

    /**
     * Set IWebSocketEvent Listener
     *
     * @param event
     */
    void setCallback(IWebSocketEvent event);

}
