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

    final String appUUID;
    final String apiKey;
    final String apiSecret;
    final String serverUrl;
    final String userEmail;
    final String entUserUUID;
    final String entUserType;

    private String entUserData;
    private String userIcon;
    private String userFullName;
    private String jpushRegistrationId;

    // View Related
    final String inputHint;
    final int actionbarBackgroundColor;
    final int actionbarTitleColor;

    final boolean enableLog;
    final boolean enableEnterKeyToSendText;

    public PPComSDKConfiguration(PPComSDKConfiguration.Builder builder) {
        this.builder = builder;

        this.context = this.builder.context;
        this.appUUID = this.builder.appUUID;
        this.apiKey = this.builder.apiKey;
        this.apiSecret = this.builder.apiSecret;

        this.serverUrl = this.builder.serverUrl;

        this.userEmail = this.builder.userEmail;
        this.userIcon = this.builder.userIcon;
        this.userFullName = this.builder.userFullName;

        this.entUserType = this.builder.entUserType;
        this.entUserUUID = this.builder.entUserUUID;
        this.entUserData = this.builder.entUserData;

        this.jpushRegistrationId = this.builder.jpushRegistrationId;

        this.inputHint = builder.inputHint;
        this.actionbarBackgroundColor = builder.actionbarBackgroundColor;
        this.actionbarTitleColor = builder.actionbarTitleColor;

        this.enableLog = builder.enableLog;
        this.enableEnterKeyToSendText = builder.enableEnterKeyToSendText;
    }

    public void update(PPComSDKConfiguration configuration) {

        JSONObject jsonObject = new JSONObject();
        try {
            if (configuration.getUserFullName() != null) {
                this.userFullName = configuration.getUserFullName();
                jsonObject.put("user_fullname", this.userFullName);
            }

            if (configuration.getUserIcon() != null) {
                this.userIcon = configuration.getUserIcon();
                jsonObject.put("user_icon", this.userIcon);
            }

            if (configuration.getEntUserData() != null) {
                this.entUserData = configuration.getEntUserData();
                jsonObject.put("ent_user_data", this.entUserData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObject.length() > 0) {
            PPComSDK.getInstance().getStartupHelper().getComUser().updateUserInfo(jsonObject);
        }

        if (configuration.getJpushRegistrationId() != null) {
            this.jpushRegistrationId = configuration.getJpushRegistrationId();
            PPComSDK.getInstance().getStartupHelper().getComUser().updateDeviceJpushRegistrationId();
        }

    }

    public Context getContext() {
        return context;
    }

    public String getAppUUID() {
        return appUUID;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public String getServerUrl() {
        return serverUrl;
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

    public String getEntUserType() {
        return entUserType;
    }

    public String getEntUserData() {
        return entUserData;
    }

    public String getEntUserUUID() {
        return entUserUUID;
    }

    public String getJpushRegistrationId() {
        return jpushRegistrationId;
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

        private String appUUID;

        private String entUserUUID;
        private String entUserData;
        private String entUserType;

        private String jpushRegistrationId;

        private String userEmail;
        private String userIcon;
        private String userFullName;

        private String serverUrl;

        private String apiKey;
        private String apiSecret;

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

        public PPComSDKConfiguration.Builder setEntUserUUID(String entUserUUID) {
            this.entUserUUID = entUserUUID;
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

        /**
         * Set input hint, deault is null <br>
         * 设置聊天时的输入框提示信息,默认为 null
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
         * 设置 Activity ActionBar 标题颜色, 默认是白色
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
         * 设置 Activity ActionBar 背景颜色, 默认为蓝色
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
         * 是否使用回车键发送文本消息
         * @param enableEnterKeyToSendText
         * @return
         */
        public PPComSDKConfiguration.Builder setEnableEnterKeyToSendText(boolean enableEnterKeyToSendText) {
            this.enableEnterKeyToSendText = enableEnterKeyToSendText;
            return this;
        }

        public String getEntUserData() {
            return entUserData;
        }

        public String getJpushRegistrationId() {
            return jpushRegistrationId;
        }

        public String getUserIcon() {
            return userIcon;
        }

        public String getUserFullName() {
            return userFullName;
        }

        public PPComSDKConfiguration build() {
            return new PPComSDKConfiguration(this);
        }
    }

}
