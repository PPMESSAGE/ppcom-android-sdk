package com.ppmessage.sdk.core;

import android.content.Context;

import com.ppmessage.sdk.core.utils.IImageLoader;
import com.ppmessage.sdk.core.utils.PicassoImageLoader;
import com.ppmessage.sdk.core.ws.AndroidAsyncWebSocketImpl;
import com.ppmessage.sdk.core.ws.IWebSocket;

/**
 * Created by ppmessage on 5/12/16.
 */
public final class PPMessageSDKConfiguration {

    private final Builder builder;

    final Context context;

    final String appUUID;
    final String userEmail;
    final String userSha1Password;

    final boolean enableLogging;
    final boolean enableDebugLogging;

    final IImageLoader imageLoader;
    final IWebSocket webSocket;

    public PPMessageSDKConfiguration(Builder builder) {
        this.builder = builder;

        this.context = builder.context;

        this.appUUID = builder.appUUID;
        this.userEmail = builder.userEmail;
        this.userSha1Password = builder.userSha1Password;

        this.enableDebugLogging = builder.enableDebugLogging;
        this.enableLogging = builder.enableLogging;

        this.imageLoader = builder.imageLoader;
        this.webSocket = builder.webSocket;
    }

    public static class Builder {

        private Context context;

        private String appUUID;
        private String userEmail;
        private String userSha1Password;

        private boolean enableLogging;
        private boolean enableDebugLogging;

        private IImageLoader imageLoader;
        private IWebSocket webSocket;

        public Builder(Context context) {
            this.context = context;

            this.enableLogging = true;
            this.enableDebugLogging = false;

            this.imageLoader = new PicassoImageLoader(context);
            this.webSocket = new AndroidAsyncWebSocketImpl();
        }

        public Builder setAppUUID(String appUUID) {
            this.appUUID = appUUID;
            return this;
        }

        public Builder setServiceUserInfo(String userEmail, String userSha1Password) {
            this.userEmail = userEmail;
            this.userSha1Password = userSha1Password;
            return this;
        }

        public Builder setEnableLogging(boolean enableLogging) {
            this.enableLogging = enableLogging;
            return this;
        }

        public Builder setEnableDebugLogging(boolean enableDebugLogging) {
            this.enableDebugLogging = enableDebugLogging;
            return this;
        }

        public Builder setImageLoader(IImageLoader imageLoader) {
            this.imageLoader = imageLoader;
            return this;
        }

        public Builder setWebSocket(IWebSocket webSocket) {
            this.webSocket = webSocket;
            return this;
        }

        public PPMessageSDKConfiguration build() {
            return new PPMessageSDKConfiguration(this);
        }

    }

}
