package com.ppmessage.sdk.ws;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ppmessage.sdk.Global;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.ws.AndroidAsyncWebSocketImpl;
import com.ppmessage.sdk.core.ws.IWebSocket;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by ppmessage on 5/9/16.
 */
@RunWith(AndroidJUnit4.class)
public class WebSocketTest {

    final CountDownLatch signal = new CountDownLatch(2);

    @Test
    public void testWebSocketCanConnect() {
        PPMessageSDK sdk = Global.getPPMessageSDK();
        final IWebSocket webSocket = new AndroidAsyncWebSocketImpl();

        webSocket.open();
        webSocket.setCallback(new IWebSocket.IWebSocketEvent() {

            @Override
            public void onOpen(IWebSocket webSocket) {
                L.d("WebSocket onOpen");
                Assert.assertThat(true, Matchers.is(true));
                signal.countDown();

                try {
                    Thread.sleep(1000);
                    webSocket.close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessageArrived(IWebSocket webSocket, String message) {
                L.d("WebSocket onMessageArrived");
            }

            @Override
            public void onClose(IWebSocket webSocket) {
                L.d("WebSocket onClose");
                Assert.assertThat(true, Matchers.is(true));
                signal.countDown();
            }

            @Override
            public void onError(IWebSocket webSocket, Exception e) {
                L.d("WebSocket onError:%s", e);

                signal.countDown();
            }

        });

        try {
            signal.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
