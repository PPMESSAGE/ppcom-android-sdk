package com.ppmessage.sdk.core.bean.common;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppmessage on 5/6/16.
 */
public class User {

    private String uuid;
    private String name;
    private String icon;
    private String deviceUUID;
    private String sha1Password;
    private boolean serviceUser;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDeviceUUID() {
        return deviceUUID;
    }

    public void setDeviceUUID(String deviceUUID) {
        this.deviceUUID = deviceUUID;
    }

    public String getSha1Password() {
        return sha1Password;
    }

    public void setSha1Password(String sha1Password) {
        this.sha1Password = sha1Password;
    }

    public boolean isServiceUser() {
        return serviceUser;
    }

    public void setServiceUser(boolean serviceUser) {
        this.serviceUser = serviceUser;
    }

    @Override
    public String toString() {
        return "User{" +
                "deviceUUID='" + deviceUUID + '\'' +
                ", uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }

    public static User parse(JSONObject userJsonObject) {
        if (userJsonObject.has("error_code")) {
            try {
                if (userJsonObject.getInt("error_code") != 0) return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        User user = new User();
        try {
            String userName = userJsonObject.has("user_name") ? userJsonObject.getString("user_name") :
                    (userJsonObject.has("user_fullname") ? userJsonObject.getString("user_fullname") :
                            (userJsonObject.has("fullname") ? userJsonObject.getString("fullname") : null));
            String userIcon = Utils.getFileDownloadUrl(userJsonObject.has("user_icon") ? userJsonObject.getString("user_icon") :
                    (userJsonObject.has("icon") ? userJsonObject.getString("icon") : null));
            String userUUID = userJsonObject.has("uuid") ? userJsonObject.getString("uuid") :
                    (userJsonObject.has("user_uuid") ? userJsonObject.getString("user_uuid") : null);

            user.setName(userName);
            user.setIcon(userIcon);
            user.setUuid(userUUID);
        } catch (JSONException e) {
            L.e(e);
        }

        return user;
    }

}
