package com.ppmessage.sdk.core.api;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Locale;

/**
 * Created by ppmessage on 5/5/16.
 */
public class Token extends BaseHttpRequest implements IToken {

    private static final String PPAUTH_URL_SEGMENT = "/ppauth/token";

    private String cachedToken;
    private PPMessageSDK messageSDK;

    public Token(PPMessageSDK messageSDK) {
        this.messageSDK = messageSDK;
    }

    @Override
    protected void setup(HttpURLConnection conn) {
        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    }

    @Override
    public void getApiToken(String appUUID, final OnRequestTokenEvent completedCallback) {
        if (cachedToken != null) {
            if (completedCallback != null) completedCallback.onGetToken(cachedToken);
            return;
        }

        post(getURL(),
                String.format(Locale.getDefault(), "client_id=%s&client_secret=%s&grant_type=client_credentials",
                        messageSDK.getHostInfo().getPpcomApiKey(), messageSDK.getHostInfo().getPpcomApiSecret()),
                makeResponse(completedCallback));
    }

    @Override
    public void getApiToken(String userEmail, String userPassword, OnRequestTokenEvent completedCallback) {
        if (cachedToken != null) {
            if (completedCallback != null) completedCallback.onGetToken(cachedToken);
            return;
        }



        post(getURL(),
                String.format(Locale.getDefault(), "grant_type=password&user_email=%s&user_password=%s&client_id=%s",
                        userEmail, userPassword, messageSDK.getHostInfo().getPpkefuApiKey()),
                makeResponse(completedCallback));
    }

    @Override
    public void clearCachedToken() {
        if (cachedToken != null) {
            cachedToken = null;
        }
    }

    @Override
    public String getCachedToken() {
        return cachedToken;
    }

    private String getURL() {
        return String.format(Locale.getDefault(), "%s%s", messageSDK.getHostInfo().getHttpHost(), PPAUTH_URL_SEGMENT);
    }

    private OnHttpRequestCompleted makeResponse(final OnRequestTokenEvent completedCallback) {
        return new OnHttpRequestCompleted() {
            @Override
            public void onResponse(String response) {
                String accessToken = null;
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    accessToken = jsonObj.getString("access_token");
                    Token.this.cachedToken = accessToken;
                } catch (JSONException e) {
                    L.e(e);
                }
                if (completedCallback != null) completedCallback.onGetToken(accessToken);
            }

            @Override
            public void onCancelled() {
                if (completedCallback != null) completedCallback.onGetToken(null);
            }

            @Override
            public void onError(int errorCode) {
                if (completedCallback != null) completedCallback.onGetToken(null);
            }
        };
    }

}
