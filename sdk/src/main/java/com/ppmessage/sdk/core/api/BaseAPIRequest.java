package com.ppmessage.sdk.core.api;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Locale;

/**
 * Created by ppmessage on 5/6/16.
 */
public class BaseAPIRequest extends BaseHttpRequest {

    private static final String GET_API_ACCESS_TOKEN_FORMAT = "get access_token %s";
    private static final String USE_APP_UUID_GET_ACCESS_TOKEN = "use app_uuid to get access_token: %s";

    private PPMessageSDK sdk;

    private IToken token;
    private boolean useAppUUIDGetAccessToken;
    protected String cachedAccessToken;

    public BaseAPIRequest(PPMessageSDK sdk) {
        this.sdk = sdk;
        token = sdk.getToken();

        useAppUUIDGetAccessToken = this.sdk.getAppUUID() != null;
        L.d(USE_APP_UUID_GET_ACCESS_TOKEN, useAppUUIDGetAccessToken);
    }

    public void post(String urlSegment, JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        post(urlSegment, requestParam.toString(), completedCallback);
    }

    public void post(String urlSegment, String requestString, OnAPIRequestCompleted completedCallback) {
        if (cachedAccessToken != null) {
            finalPost(urlSegment, requestString, completedCallback);
        } else {
            if (useAppUUIDGetAccessToken) {
                token.getApiToken(sdk.getAppUUID(), onGetAccessToken(urlSegment, requestString, completedCallback));
            } else {
                token.getApiToken(sdk.getUserEmail(), sdk.getUserPassword(), onGetAccessToken(urlSegment, requestString, completedCallback));
            }
        }
    }

    @Override
    protected void setup(HttpURLConnection conn) {
        super.setup(conn);

        if (cachedAccessToken != null) {
            conn.addRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.addRequestProperty("Authorization", String.format(Locale.getDefault(), "OAuth %s", cachedAccessToken));
        }

    }

    private IToken.OnRequestTokenEvent onGetAccessToken(final String urlSegment, final String requestString, final OnAPIRequestCompleted completedCallback) {
        return new IToken.OnRequestTokenEvent() {
            @Override
            public void onGetToken(String accessToken) {
                cachedAccessToken = accessToken;
                L.d(GET_API_ACCESS_TOKEN_FORMAT, accessToken);
                finalPost(urlSegment, requestString, completedCallback);
            }
        };
    }

    private void finalPost(String urlSegment, String requestString, final OnAPIRequestCompleted completedCallback) {
        String url = String.format(Locale.getDefault(), "%s/api%s", HostConstants.HTTP_HOST, urlSegment);
        super.post(url, requestString, new OnHttpRequestCompleted() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (completedCallback != null) completedCallback.onResponse(jsonResponse);
            }

            @Override
            public void onCancelled() {
                if (completedCallback != null) completedCallback.onCancelled();
            }

            @Override
            public void onError(int errorCode) {
                if (completedCallback != null) completedCallback.onError(errorCode);
            }

        });
    }
}
