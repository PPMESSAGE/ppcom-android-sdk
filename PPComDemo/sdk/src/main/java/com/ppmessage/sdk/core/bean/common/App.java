package com.ppmessage.sdk.core.bean.common;

import android.text.TextUtils;

import com.ppmessage.sdk.core.L;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppmessage on 5/16/16.
 */
public class App {

    public static final String POLICY_GROUP = "GROUP";
    public static final String POLICY_ALL = "ALL";
    public static final String POLICY_SMART = "SMART";

    private String appUUID;
    private String appName;
    private String policy;

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

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public static App parse(JSONObject jsonObject) {
        App app = new App();

        if (jsonObject != null) {
            try {
                String appName = jsonObject.has("app_name") ? jsonObject.getString("app_name") : null;
                String appUUID = jsonObject.has("app_uuid") ? jsonObject.getString("app_uuid") :
                        (jsonObject.has("uuid") ? jsonObject.getString("uuid") : null);
                String policy = jsonObject.has("app_route_policy") ? jsonObject.getString("app_route_policy") : POLICY_ALL;
                if (TextUtils.isEmpty(policy)) {
                    policy = POLICY_ALL;
                }

                app.setAppName(appName);
                app.setAppUUID(appUUID);
                app.setPolicy(policy);

            } catch (JSONException e) {
                L.e(e);
            }
        }

        return app;
    }
}
