package com.ppmessage.ppcomlib.services;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.api.OnAPIRequestCompleted;
import com.ppmessage.sdk.core.bean.common.App;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Example:
 *
 * <pre>
 *     1. -Async get app
 *     PPComSDK sdk = PPComSDK.getInstance();
 *     PPComApp comApp = new PPComApp(sdk);
 *     comApp.get(new OnGetAppCallback() {
 *         @Override
 *         public void onCompleted(App app) {
 *             // Success: app != null
 *             // Error: app == null
 *         }
 *     });
 *
 *     2. -get app
 *     App app = comApp.getApp();
 * </pre>
 *
 * Created by ppmessage on 5/16/16.
 */
public class PPComApp {

    interface OnGetAppCallback {
        void onCompleted(App app);
    }

    private PPComSDK sdk;
    private App app;

    public PPComApp(PPComSDK sdk) {
        this.sdk = sdk;
    }

    public App getApp() {
        return app;
    }

    public void get(final OnGetAppCallback event) {
        if (app != null) {
            if (event != null) {
                event.onCompleted(app);
            }
            return;
        }

        PPMessageSDK messageSDK = sdk.getConfiguration().getMessageSDK();
        String appUUID = sdk.getConfiguration().getAppUUID();

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("app_uuid", appUUID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        messageSDK.getAPI().getAppInfo(jsonObject, new OnAPIRequestCompleted() {
            @Override
            public void onResponse(JSONObject jsonResponse) {
                try {
                    if (jsonResponse.getInt("error_code") == 0) {
                        App app = App.parse(jsonResponse);
                        PPComApp.this.app = app; // Cache App

                        if (event != null) event.onCompleted(app);
                    } else {
                        if (event != null) event.onCompleted(PPComApp.this.app);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled() {
                if (event != null) event.onCompleted(PPComApp.this.app);
            }

            @Override
            public void onError(int errorCode) {
                if (event != null) event.onCompleted(PPComApp.this.app);
            }
        });
    }

}
