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
    final String userEmail;
    final String userIcon;
    final String userName;

    final PPMessageSDK messageSDK;

    public PPComSDKConfiguration(PPComSDKConfiguration.Builder builder) {
        this.builder = builder;

        this.context = this.builder.context;
        this.appUUID = this.builder.appUUID;
        this.userEmail = this.builder.userEmail;
        this.userIcon = this.builder.userIcon;
        this.userName = this.builder.userName;

        this.messageSDK = PPMessageSDK.getInstance();
        this.messageSDK.init(new PPMessageSDKConfiguration.Builder(this.context)
                             .setEnableLogging(true)
                             .setEnableDebugLogging(true)
                             .setAppUUID(builder.appUUID)
                             .setHost(builder.host)
                             .setPpcomApiKey(builder.apiKey)
                             .setPpcomApiSecret(builder.apiSecret)
                             .setSsl(builder.ssl)
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

    public String getUserName() {
        return userName;
    }

    public PPMessageSDK getMessageSDK() {
        return messageSDK;
    }

    public static class Builder {

        private Context context;

        private String appUUID;
        private String userEmail; // detail user_uuid
        private String userIcon;
        private String userName;

        private String host;
        private boolean ssl;
        private String apiKey;
        private String apiSecret;

        public Builder(Context context) {
            this.context = context;

            this.host = PPMessageSDKConfiguration.DEFAULT_HOST;
            this.ssl = PPMessageSDKConfiguration.DEFAULT_SSL;
            this.apiKey = PPMessageSDKConfiguration.DEFAULT_PPCOM_API_KEY;
            this.apiSecret = PPMessageSDKConfiguration.DEFAULT_PPCOM_API_SECRET;
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

        public PPComSDKConfiguration.Builder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public PPComSDKConfiguration.Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public PPComSDKConfiguration.Builder setSsl(boolean ssl) {
            this.ssl = ssl;
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

        public PPComSDKConfiguration build() {
            return new PPComSDKConfiguration(this);
        }
    }

}
