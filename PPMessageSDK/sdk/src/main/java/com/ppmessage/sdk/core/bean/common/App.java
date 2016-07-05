package com.ppmessage.sdk.core.bean.common;

import android.text.TextUtils;

import com.ppmessage.sdk.core.L;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppmessage on 5/16/16.
 */
public class App {

    private String appUUID;
    private String appName;

    public String getAppUUID() {
        return appUUID;
    }

    public void setAppUUID(String appUUID) {
        this.appUUID = appUUID;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public String toString() {
        return "App{" +
                "appUUID='" + appUUID + '\'' +
                ", appName='" + appName + '\'' +
                '}';
    }

    public static App parse(JSONObject jsonObject) {
        App app = new App();

        if (jsonObject != null) {
            try {
                String appName = jsonObject.has("app_name") ? jsonObject.getString("app_name") : null;
                String appUUID = jsonObject.has("app_uuid") ? jsonObject.getString("app_uuid") :
                        (jsonObject.has("uuid") ? jsonObject.getString("uuid") : null);

                app.setAppName(appName);
                app.setAppUUID(appUUID);

            } catch (JSONException e) {
                L.e(e);
            }
        }

        return app;
    }
}
