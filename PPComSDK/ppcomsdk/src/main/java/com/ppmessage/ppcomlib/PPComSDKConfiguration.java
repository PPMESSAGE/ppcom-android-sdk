package com.ppmessage.ppcomlib;

import android.content.Context;

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

        public Builder() {
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
