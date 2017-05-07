package com.ppmessage.sdk.core;

import android.content.Context;

import com.ppmessage.sdk.core.utils.GlideImageLoader;
import com.ppmessage.sdk.core.utils.IImageLoader;

/**
 * Created by ppmessage on 5/12/16.
 */
public final class PPMessageSDKConfiguration {

    private final Builder builder;

    final Context context;

    final String appUuid;

    final String entUserId;
    final String entUserName;
    final String entUserIcon;


    private String gcmPushRegistrationId;
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
    final boolean enableEnterKeyToSendText;

    final IImageLoader imageLoader;

    final String inputHint;

    public PPMessageSDKConfiguration(Builder builder) {
        this.builder = builder;

        this.context = builder.context;

        this.appUuid = builder.appUuid;

        this.entUserId = builder.entUserId;
        this.entUserName = builder.entUserName;
        this.entUserIcon = builder.entUserIcon;

        this.jpushRegistrationId = builder.jpushRegistrationId;
        this.gcmPushRegistrationId = builder.gcmPushRegistrationId;

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
        this.enableEnterKeyToSendText = builder.enableEnterKeyToSendText;

        this.imageLoader = builder.imageLoader;
        this.inputHint = builder.inputHint;
    }

    public void update(PPMessageSDKConfiguration configuration) {
        Builder builder = configuration.getBuilder();
    }

    public Builder getBuilder() {
        return builder;
    }

    public String getInputHint() {
        return inputHint;
    }

    public boolean isEnableEnterKeyToSendText() {
        return enableEnterKeyToSendText;
    }

    public static class Builder {

        private Context context;

        private String appUuid;

        private String userIcon;
        private String userFullName;

        private String entUserId;
        private String entUserName;
        private String entUserIcon;

        private String jpushRegistrationId;
        private String gcmPushRegistrationId;

        private String userEmail;
        private String userSha1Password;

        private boolean ssl;
        private String host;

        private String ppcomApiKey;
        private String ppcomApiSecret;

        private String ppkefuApiKey;

        private boolean enableLogging;
        private boolean enableDebugLogging;
        private boolean enableEnterKeyToSendText;

        private IImageLoader imageLoader;

        private String inputHint;

        public Builder() {
            this.enableLogging = true;
            this.enableDebugLogging = false;
        }

        public Builder setContext(Context context) {
            this.context = context;
            this.imageLoader = new GlideImageLoader(context);
            return this;
        }

        public Builder setAppUuid(String appUUID) {
            this.appUuid = appUUID;
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

        public Builder setEntUserIcon(String userIcon) {
            this.entUserIcon = userIcon;
            return this;
        }

        public Builder setUserIcon(String userIcon) {
            this.userIcon = userIcon;
            return this;
        }

        public Builder setEntUserName(String userFullName) {
            this.entUserName = userFullName;
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

        public Builder setEntUserId(String entUserUUID) {
            this.entUserId = entUserUUID;
            return this;
        }

        public Builder setGcmPushRegistrationId(String id) {
            this.gcmPushRegistrationId = id;
            return this;
        }


        public Builder setJpushRegistrationId(String jpushRegistrationId) {
            this.jpushRegistrationId = jpushRegistrationId;
            return this;
        }

        public Builder setInputHint(String inputHint) {
            this.inputHint = inputHint;
            return this;
        }

        /**
         * Enable enterKey to send text message <br>
         * @param enableEnterKeyToSendText
         * @return
         */
        public Builder setEnableEnterKeyToSendText(boolean enableEnterKeyToSendText) {
            this.enableEnterKeyToSendText = enableEnterKeyToSendText;
            return this;
        }

        public PPMessageSDKConfiguration build() {
            return new PPMessageSDKConfiguration(this);
        }

    }

}
