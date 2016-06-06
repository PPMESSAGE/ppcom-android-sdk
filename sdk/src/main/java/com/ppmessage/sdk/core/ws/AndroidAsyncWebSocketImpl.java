package com.ppmessage.sdk.core.ws;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.api.BaseHttpRequest;
import com.ppmessage.sdk.core.api.HostConstants;

/**
 * Created by ppmessage on 5/9/16.
 */
public class AndroidAsyncWebSocketImpl implements IWebSocket, AsyncHttpClient.WebSocketConnectCallback {

    private static final String WEBSOCKET_HOST = HostConstants.WS_HOST;

    private static final int HANDLER_EVENT_STRING_ARRIVED = 1;
    private static final int HANDLER_EVENT_OPEN = 2;
    private static final int HANDLER_EVENT_CLOSED = 3;
    private static final int HANDLER_EVENT_EXCEPTION = 4;

    private static final String LOG_STRING_AVALIABLE = "[AndroidAsyncWebSocket] string arrived:%s";

    private IWebSocketEvent event;
    private WebSocket webSocket;

    private Handler handler;

    public AndroidAsyncWebSocketImpl() {

    }

    @Override
    public void open() {
        if (handler == null) {
            handler = new android.os.Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    switch (msg.what) {
                        case HANDLER_EVENT_CLOSED:
                            if (event != null) event.onClose(AndroidAsyncWebSocketImpl.this);
                            break;

                        case HANDLER_EVENT_EXCEPTION:
                            if (event != null) event.onError(AndroidAsyncWebSocketImpl.this, (Exception) msg.obj);
                            break;

                        case HANDLER_EVENT_OPEN:
                            if (event != null) event.onOpen(AndroidAsyncWebSocketImpl.this);
                            break;

                        case HANDLER_EVENT_STRING_ARRIVED:
                            if (event != null) event.onMessageArrived(AndroidAsyncWebSocketImpl.this, (String) msg.obj);
                            break;
                    }
                }
            };
        }
        AsyncHttpClient.getDefaultInstance().websocket(WEBSOCKET_HOST, null, this);
    }

    @Override
    public void close() {
        if (webSocket != null) webSocket.close();
    }

    @Override
    public void send(String data) {
        if (webSocket != null) webSocket.send(data);
    }

    @Override
    public void setCallback(IWebSocketEvent event) {
        this.event = event;
    }

    @Override
    public void onCompleted(Exception ex, WebSocket webSocket) {
        if (ex != null) {
            L.e(ex);
            dispatch(ex, HANDLER_EVENT_EXCEPTION);
            return;
        }

        webSocket.setStringCallback(new WebSocket.StringCallback() {
            @Override
            public void onStringAvailable(String s) {
                L.d(LOG_STRING_AVALIABLE, s);
                dispatch(s, HANDLER_EVENT_STRING_ARRIVED);
            }
        });

        webSocket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                L.e(ex);
                dispatch(HANDLER_EVENT_CLOSED);
            }
        });

        this.webSocket = webSocket;

        dispatch(HANDLER_EVENT_OPEN);
    }

    private void dispatch(int what) {
        dispatch(null, what);
    }

    private void dispatch(Object obj, int what) {
        if (handler != null) {
            Message message = handler.obtainMessage();
            message.what = what;
            message.obj = obj;
            message.sendToTarget();
        }
    }

}
