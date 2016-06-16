package com.ppmessage.sdk.core.bean.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppmessage on 5/6/16.
 */
public class User implements Parcelable {

    private String uuid;
    private String name;
    private String icon;
    private String deviceUUID;
    private String sha1Password;
    private boolean serviceUser;
    private String email;
    private String signature;

    public User() {

    }

    protected User(Parcel in) {
        uuid = in.readString();
        name = in.readString();
        icon = in.readString();
        deviceUUID = in.readString();
        sha1Password = in.readString();
        serviceUser = in.readByte() != 0;
        email = in.readString();
        signature = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", deviceUUID='" + deviceUUID + '\'' +
                ", email='" + email + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }

    public static User parse(JSONObject userJsonObject) {
        if (userJsonObject.has("error_code")) {
            try {
                if (userJsonObject.getInt("error_code") != 0) return null;
            } catch (JSONException e) {
                L.e(e);
            }
        }

        User user = new User();
        try {
            // Find userName
            String userName = null;
            if (userJsonObject.has("user_name")) {
                userName = Utils.safeNull(userJsonObject.getString("user_name"));
            }
            if (userName == null) {
                if (userJsonObject.has("user_fullname")) {
                    userName = Utils.safeNull(userJsonObject.getString("user_fullname"));
                }
            }
            if (userName == null) {
                if (userJsonObject.has("fullname")) {
                    userName = Utils.safeNull(userJsonObject.getString("fullname"));
                }
            }

            String userIcon = Utils.getFileDownloadUrl(userJsonObject.has("user_icon") ? userJsonObject.getString("user_icon") :
                    (userJsonObject.has("icon") ? userJsonObject.getString("icon") : null));
            String userUUID = userJsonObject.has("uuid") ? userJsonObject.getString("uuid") :
                    (userJsonObject.has("user_uuid") ? userJsonObject.getString("user_uuid") : null);
            String mobileDeviceUUID = userJsonObject.has("mobile_device_uuid") ? Utils.safeNull(userJsonObject.getString("mobile_device_uuid")) : null;
            String userEmail = userJsonObject.has("user_email") ? Utils.safeNull(userJsonObject.getString("user_email")) : null;
            String userSignature = userJsonObject.has("user_signature") ? Utils.safeNull(userJsonObject.getString("user_signature")) : null;

            user.setName(userName);
            user.setIcon(userIcon);
            user.setUuid(userUUID);
            user.setDeviceUUID(mobileDeviceUUID);
            user.setEmail(userEmail);
            user.setSignature(userSignature);

        } catch (JSONException e) {
            L.e(e);
        }

        return user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(name);
        dest.writeString(icon);
        dest.writeString(deviceUUID);
        dest.writeString(sha1Password);
        dest.writeByte((byte) (serviceUser ? 1 : 0));
        dest.writeString(email);
        dest.writeString(signature);
    }
}
