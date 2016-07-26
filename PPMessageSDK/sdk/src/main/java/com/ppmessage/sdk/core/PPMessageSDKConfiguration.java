package com.ppmessage.sdk.core;

import android.content.Context;
import android.hardware.camera2.params.StreamConfigurationMap;

import com.ppmessage.sdk.core.utils.IImageLoader;
import com.ppmessage.sdk.core.utils.PicassoImageLoader;
import com.ppmessage.sdk.core.utils.UILImageLoader;
import com.ppmessage.sdk.core.ws.AndroidAsyncWebSocketImpl;
import com.ppmessage.sdk.core.ws.IWebSocket;

/**
 * Created by ppmessage on 5/12/16.
 */
public final class PPMessageSDKConfiguration {

    private final Builder builder;

    final Context context;

    final String appUUID;

    final String entUserType;
    final String entUserUUID;

    private String entUserData;

    private String jpushRegistrationId;

    private String userFullName;
    private String userIcon;

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

        this.entUserType = builder.entUserType;
        this.entUserUUID = builder.entUserUUID;
        this.entUserData = builder.entUserData;

        this.jpushRegistrationId = builder.jpushRegistrationId;

        this.userFullName = builder.userFullName;
        this.userIcon = builder.userIcon;

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

    public void update(PPMessageSDKConfiguration configuration) {
        Builder builder = configuration.getBuilder();

        if (builder.getEntUserData() != null) {
            this.entUserData = builder.getEntUserData();
        }

        if (builder.getJpushRegistrationId() != null) {
            this.jpushRegistrationId = builder.getJpushRegistrationId();
        }

        if (builder.getUserIcon() != null) {
            this.userIcon = builder.getUserIcon();
        }

        if (builder.getUserFullName() != null) {
            this.userFullName = builder.getUserFullName();
        }
    }

    public Builder getBuilder() {
        return builder;
    }

    public static class Builder {

        private Context context;

        private String appUUID;

        private String userIcon;
        private String userFullName;

        private String entUserUUID;
        private String entUserData;
        private String entUserType;

        private String jpushRegistrationId;

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

        public Builder() {
            this.enableLogging = true;
            this.enableDebugLogging = false;
        }

        public Builder setContext(Context context) {
            this.context = context;
            this.imageLoader = new UILImageLoader(context);
            return this;
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

        public Builder setServerUrl(String url) {
            this.ssl = false;
            if (url.startsWith("https")) {
                this.ssl = true;
            }
            this.host = url.substring(url.indexOf("//") + "//".length());
            L.d(this.host);
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

        public Builder setUserIcon(String userIcon) {
            this.userIcon = userIcon;
            return this;
        }

        public Builder setUserFullName(String userFullName) {
            this.userFullName = userFullName;
            return this;
        }

        public Builder setUserEmail(String userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        public Builder setEntUserUUID(String entUserUUID) {
            this.entUserUUID = entUserUUID;
            return this;
        }

        public Builder setEntUserData(String entUserData) {
            this.entUserData = entUserData;
            return this;
        }

        public Builder setEntUserType(String entUserType) {
            this.entUserType = entUserType;
            return this;
        }

        public Builder setJpushRegistrationId(String jpushRegistrationId) {
            this.jpushRegistrationId = jpushRegistrationId;
            return this;
        }

        public String getUserIcon() {
            return userIcon;
        }

        public String getUserFullName() {
            return userFullName;
        }

        public String getEntUserData() {
            return entUserData;
        }

        public String getJpushRegistrationId() {
            return jpushRegistrationId;
        }

        public PPMessageSDKConfiguration build() {
            return new PPMessageSDKConfiguration(this);
        }

    }

}
