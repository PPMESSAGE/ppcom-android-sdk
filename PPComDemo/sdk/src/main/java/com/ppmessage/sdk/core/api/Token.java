package com.ppmessage.sdk.core.api;

import com.ppmessage.sdk.core.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Locale;

/**
 * Created by ppmessage on 5/5/16.
 */
public class Token extends BaseHttpRequest implements IToken {

    private static final String API_KEY = HostConstants.PPCOM_API_KEY;
    private static final String API_SECRET = HostConstants.PPCOM_API_SECRET;
    private static final String PPKEFU_API_KEY = HostConstants.PPKEFU_API_KEY;

    private static final String PPAUTH_URL_SEGMENT = "/ppauth/token";

    private String cachedToken;

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
                String.format(Locale.getDefault(), "client_id=%s&client_secret=%s&grant_type=client_credentials", API_KEY, API_SECRET),
                makeResponse(completedCallback));
    }

    @Override
    public void getApiToken(String userEmail, String userPassword, OnRequestTokenEvent completedCallback) {
        if (cachedToken != null) {
            if (completedCallback != null) completedCallback.onGetToken(cachedToken);
            return;
        }

        post(getURL(),
                String.format(Locale.getDefault(), "grant_type=password&user_email=%s&user_password=%s&client_id=%s", userEmail, userPassword, PPKEFU_API_KEY),
                makeResponse(completedCallback));
    }

    private String getURL() {
        return String.format(Locale.getDefault(), "%s%s", HostConstants.HTTP_HOST, PPAUTH_URL_SEGMENT);
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
