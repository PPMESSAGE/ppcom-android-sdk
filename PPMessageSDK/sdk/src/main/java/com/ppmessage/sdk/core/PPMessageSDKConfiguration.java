package com.ppmessage.sdk.core;

import android.content.Context;
import android.hardware.camera2.params.StreamConfigurationMap;

import com.ppmessage.sdk.core.utils.IImageLoader;
import com.ppmessage.sdk.core.utils.PicassoImageLoader;
import com.ppmessage.sdk.core.ws.AndroidAsyncWebSocketImpl;
import com.ppmessage.sdk.core.ws.IWebSocket;

/**
 * Created by ppmessage on 5/12/16.
 */
public final class PPMessageSDKConfiguration {

    public static final String DEFAULT_HOST = "ppmessage.com";
    public static final String DEFAULT_PPCOM_API_KEY = "M2E2OTRjZTQ5Mzk4ZWUxYzRjM2FlZDM2NmE4MjA4MzkzZjFjYWQyOA==";
    public static final String DEFAULT_PPCOM_API_SECRET = "ZThmMTM1ZDM4ZmI2NjE1YWE0NWEwMGM3OGNkMzY5MzVjOTQ2MGU0NQ==";
    public static final String DEFAULT_PPKEFU_API_KEY = "MWJkZWI3NDZhZmRiN2NjNDYzZDVmZGI3YTk2YjI5NzhhOWJhNzIyZA==";
    public static final boolean DEFAULT_SSL = true;

    private final Builder builder;

    final Context context;

    final String appUUID;
    final String userEmail;
    final String userSha1Password;

    final boolean ssl;
    final String host;
    final String ppcomApiKey;
    final String ppcomApiSecret;
    final String ppkefuApiKey;

    final boolean enableLogging;
    final boolean enableDebugLogging;

    final IImageLoader imageLoader;

    public PPMessageSDKConfiguration(Builder builder) {
        this.builder = builder;

        this.context = builder.context;

        this.appUUID = builder.appUUID;
        this.userEmail = builder.userEmail;
        this.userSha1Password = builder.userSha1Password;

        this.ssl = builder.ssl;
        this.host = builder.host;
        this.ppcomApiKey = builder.ppcomApiKey;
        this.ppcomApiSecret = builder.ppcomApiSecret;
        this.ppkefuApiKey = builder.ppkefuApiKey;

        this.enableDebugLogging = builder.enableDebugLogging;
        this.enableLogging = builder.enableLogging;

        this.imageLoader = builder.imageLoader;
    }

    public static class Builder {

        private Context context;

        private String appUUID;
        private String userEmail;
        private String userSha1Password;

        private boolean ssl;
        private String host;
        private String ppcomApiKey;
        private String ppcomApiSecret;
        private String ppkefuApiKey;

        private boolean enableLogging;
        private boolean enableDebugLogging;

        private IImageLoader imageLoader;

        public Builder(Context context) {
            this.context = context;

            this.enableLogging = true;
            this.enableDebugLogging = false;

            this.ssl = DEFAULT_SSL;
            this.host = DEFAULT_HOST;
            this.ppcomApiKey = DEFAULT_PPCOM_API_KEY;
            this.ppcomApiSecret = DEFAULT_PPCOM_API_SECRET;
            this.ppkefuApiKey = DEFAULT_PPKEFU_API_KEY;

            this.imageLoader = new PicassoImageLoader(context);
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

        public Builder setSsl(boolean ssl) {
            this.ssl = ssl;
            return this;
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPpcomApiKey(String ppcomApiKey) {
            this.ppcomApiKey = ppcomApiKey;
            return this;
        }

        public Builder setPpcomApiSecret(String ppcomApiSecret) {
            this.ppcomApiSecret = ppcomApiSecret;
            return this;
        }

        public Builder setPpkefuApiKey(String ppkefuApiKey) {
            this.ppkefuApiKey = ppkefuApiKey;
            return this;
        }

        public Builder setImageLoader(IImageLoader imageLoader) {
            this.imageLoader = imageLoader;
            return this;
        }

        public PPMessageSDKConfiguration build() {
            return new PPMessageSDKConfiguration(this);
        }

    }

}
