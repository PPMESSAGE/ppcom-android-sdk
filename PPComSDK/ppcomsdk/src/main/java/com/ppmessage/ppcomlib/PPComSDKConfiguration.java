package com.ppmessage.ppcomlib;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;

import com.ppmessage.sdk.core.PPMessageSDKConfiguration;
import com.ppmessage.sdk.core.bean.message.PPMessage;
import com.ppmessage.sdk.core.notification.INotification;
import com.ppmessage.sdk.core.notification.WSMessageAckNotificationHandler;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppmessage on 5/13/16.
 */
public final class PPComSDKConfiguration {

    final Builder builder;

    final Context context;

    final String serverUrl;

    final String appUuid;
    final String apiKey;
    final String apiSecret;

    final String userEmail;

    final String entUserId;
    final String entUserName;
    final String entUserIcon;
    final double entUserCreateTime;

    private String jpushRegistrationId;
    private String gcmPushRegistrationId;

    // View Related
    final String inputHint;
    final int actionbarBackgroundColor;
    final int actionbarTitleColor;

    final boolean enableLog;
    final boolean enableEnterKeyToSendText;

    public PPComSDKConfiguration(PPComSDKConfiguration.Builder builder) {
        this.builder = builder;

        this.context = this.builder.context;

        this.serverUrl = this.builder.serverUrl;

        this.appUuid = this.builder.appUuid;
        this.apiKey = this.builder.apiKey;
        this.apiSecret = this.builder.apiSecret;


        this.userEmail = this.builder.userEmail;
        this.entUserIcon = this.builder.entUserIcon;
        this.entUserName = this.builder.entUserName;
        this.entUserId = this.builder.entUserId;
        this.entUserCreateTime = this.builder.entUserCreateTime;

        this.jpushRegistrationId = this.builder.jpushRegistrationId;
        this.gcmPushRegistrationId = this.builder.gcmPushRegistrationId;

        this.inputHint = builder.inputHint;
        this.actionbarBackgroundColor = builder.actionbarBackgroundColor;
        this.actionbarTitleColor = builder.actionbarTitleColor;

        this.enableLog = builder.enableLog;
        this.enableEnterKeyToSendText = builder.enableEnterKeyToSendText;
    }

    public void update(PPComSDKConfiguration configuration) {
    }

    public Context getContext() {
        return context;
    }


    public String getServerUrl() {
        return serverUrl;
    }

    public String getAppUuid() {
        return appUuid;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getEntUserId() {
        return entUserId;
    }

    public String getEntUserIcon() {
        return entUserIcon;
    }

    public String getEntUserName() {
        return entUserName;
    }

    public double getEntUserCreateTime() {
        return entUserCreateTime;
    }


    public String getJpushRegistrationId() {
        return jpushRegistrationId;
    }


    public String getGcmPushRegistrationId() {
        return gcmPushRegistrationId;
    }

    public String getInputHint() {
        return inputHint;
    }

    public int getActionbarBackgroundColor() {
        return actionbarBackgroundColor;
    }

    public int getActionbarTitleColor() {
        return actionbarTitleColor;
    }

    public boolean isEnableEnterKeyToSendText() {
        return enableEnterKeyToSendText;
    }

    public static class Builder {

        private Context context;

        private String serverUrl;

        private String appUuid;
        private String apiSecret = "ZThmMTM1ZDM4ZmI2NjE1YWE0NWEwMGM3OGNkMzY5MzVjOTQ2MGU0NQ==";
        private String apiKey = "M2E2OTRjZTQ5Mzk4ZWUxYzRjM2FlZDM2NmE4MjA4MzkzZjFjYWQyOA==";

        private String userEmail;

        private String entUserId;
        private String entUserName;
        private String entUserIcon;

        private double entUserCreateTime;

        private String jpushRegistrationId;
        private String gcmPushRegistrationId;


        private String inputHint;
        private int actionbarBackgroundColor;
        private int actionbarTitleColor;

        private boolean enableLog;
        private boolean enableEnterKeyToSendText;

        public Builder() {
            this(null);
        }

        public Builder(Context context) {
            setContext(context);
            setActionbarBackgroundColor(Color.BLUE);
            setActionbarTitleColor(Color.WHITE);
            setEnableLog(true);
        }

        public PPComSDKConfiguration.Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public PPComSDKConfiguration.Builder setServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
            return this;
        }

        public PPComSDKConfiguration.Builder setAppUuid(String appUuid) {
            this.appUuid = appUuid;
            return this;
        }

        public PPComSDKConfiguration.Builder setUserEmail(String userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        public PPComSDKConfiguration.Builder setEntUserId(String entUserId) {
            this.entUserId = entUserId;
            return this;
        }

        public PPComSDKConfiguration.Builder setEntUserIcon(String entUserIcon) {
            this.entUserIcon = entUserIcon;
            return this;
        }

        public PPComSDKConfiguration.Builder setEntUserName(String entUserName) {
            this.entUserName = entUserName;
            return this;
        }

        public PPComSDKConfiguration.Builder setEntUserCreateTime(double entUserCreateTime) {
            this.entUserCreateTime = entUserCreateTime;
            return this;
        }

        public PPComSDKConfiguration.Builder setGcmPushRegistrationId(String gcmPushRegistrationId) {
            this.gcmPushRegistrationId = gcmPushRegistrationId;
            return this;
        }

        public PPComSDKConfiguration.Builder setJPushRegistrationId(String jPushRegistrationId) {
            this.jpushRegistrationId = jPushRegistrationId;
            return this;
        }

        /**
         * Set input hint, deault is null <br>
         *
         * @param inputHint
         * @return
         */
        public PPComSDKConfiguration.Builder setInputHint(String inputHint) {
            this.inputHint = inputHint;
            return this;
        }

        /**
         * Set Activity actionBar's title color. Default is white color <br>
         *
         * @param actionbarTitleColor
         * @return
         */
        public PPComSDKConfiguration.Builder setActionbarTitleColor(@ColorInt int actionbarTitleColor) {
            this.actionbarTitleColor = actionbarTitleColor;
            return this;
        }

        public PPComSDKConfiguration.Builder setEnableLog(boolean enableLog) {
            this.enableLog = enableLog;
            return this;
        }

        /**
         * Set Activity actionBar's background color. Default is BLUE<br>
         *
         * @param actionbarBackgroundColor
         * @return
         */
        public PPComSDKConfiguration.Builder setActionbarBackgroundColor(@ColorInt int actionbarBackgroundColor) {
            this.actionbarBackgroundColor = actionbarBackgroundColor;
            return this;
        }

        /**
         * Enable enterKey to send text message <br>
         *
         * @param enableEnterKeyToSendText
         * @return
         */
        public PPComSDKConfiguration.Builder setEnableEnterKeyToSendText(boolean enableEnterKeyToSendText) {
            this.enableEnterKeyToSendText = enableEnterKeyToSendText;
            return this;
        }


        public PPComSDKConfiguration build() {
            return new PPComSDKConfiguration(this);
        }
    }

}
