package com.ppmessage.ppcomlib;

import android.content.Context;

import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.PPMessageSDKConfiguration;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.notification.INotification;
import com.ppmessage.sdk.core.notification.WSMessageAckNotificationHandler;

/**
 * Created by ppmessage on 5/13/16.
 */
public final class PPComSDKConfiguration {

    private final Builder builder;

    final Context context;

    final String appUUID;

    final String entUserUuid;
    final String entUserData;
    final String entUserType;

    final String userEmail;
    final String userIcon;
    final String userFullName;

    final String jpushRegistrationId;

    final PPMessageSDK messageSDK;

    public PPComSDKConfiguration(PPComSDKConfiguration.Builder builder) {
        this.builder = builder;

        this.context = this.builder.context;
        this.appUUID = this.builder.appUUID;

        this.userEmail = this.builder.userEmail;
        this.userIcon = this.builder.userIcon;
        this.userFullName = this.builder.userFullName;

        this.entUserType = this.builder.entUserType;
        this.entUserUuid = this.builder.entUserUuid;
        this.entUserData = this.builder.entUserData;

        this.jpushRegistrationId = this.builder.jpushRegistrationId;

        this.messageSDK = PPMessageSDK.getInstance();
        this.messageSDK.init(new PPMessageSDKConfiguration.Builder(this.context)
                .setEnableLogging(true)
                .setEnableDebugLogging(true)
                .setAppUUID(builder.appUUID)
                .setServerUrl(builder.serverUrl)
                .setPpcomApiKey(builder.apiKey)
                .setPpcomApiSecret(builder.apiSecret)

                .setEntUserUuid(builder.entUserUuid)
                .setEntUserData(builder.entUserData)
                .setEntUserType(builder.entUserType)

                .setUserEmail(builder.userEmail)
                .setUserFullName(builder.userFullName)
                .setUserIcon(builder.userIcon)
                .setJpushRegistrationId(builder.jpushRegistrationId)

                .build());

    }

    public Context getContext() {
        return context;
    }

    public String getAppUUID() {
        return appUUID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public String getEntUserType() { return entUserType; }
    public String getEntUserData() { return entUserData; }
    public String getEntUserUuid() { return entUserUuid; }

    public String getJpushRegistrationId() { return jpushRegistrationId; }

    public PPMessageSDK getMessageSDK() {
        return messageSDK;
    }

    public static class Builder {

        private Context context;

        private String appUUID;

        private String entUserUuid;
        private String entUserData;
        private String entUserType;

        private String jpushRegistrationId;

        private String userEmail;
        private String userIcon;
        private String userFullName;

        private String serverUrl;

        private String apiKey;
        private String apiSecret;

        public Builder(Context context) {
            this.context = context;
        }

        public PPComSDKConfiguration.Builder setAppUUID(String appUUID) {
            this.appUUID = appUUID;
            return this;
        }

        public PPComSDKConfiguration.Builder setUserEmail(String userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        public PPComSDKConfiguration.Builder setUserIcon(String userIcon) {
            this.userIcon = userIcon;
            return this;
        }

        public PPComSDKConfiguration.Builder setUserFullName(String userFullName) {
            this.userFullName = userFullName;
            return this;
        }

        public PPComSDKConfiguration.Builder setServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
            return this;
        }

        public PPComSDKConfiguration.Builder setApiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public PPComSDKConfiguration.Builder setApiSecret(String apiSecret) {
            this.apiSecret = apiSecret;
            return this;
        }

        public PPComSDKConfiguration.Builder setEntUserType(String entUserType) {
            this.entUserType = entUserType;
            return this;
        }

        public PPComSDKConfiguration.Builder setEntUserUuid(String entUserUuid) {
            this.entUserUuid = entUserUuid;
            return this;
        }

        public PPComSDKConfiguration.Builder setEntUserData(String entUserData) {
            this.entUserData = entUserData;
            return this;
        }

        public PPComSDKConfiguration.Builder setJpushRegistrationId(String jpushRegistrationId) {
            this.jpushRegistrationId = jpushRegistrationId;
            return this;
        }

        public PPComSDKConfiguration build() {
            return new PPComSDKConfiguration(this);
        }
    }

}
