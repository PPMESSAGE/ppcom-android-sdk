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

    private static final int ERROR_CODE_NO_TOKEN = 3;
    private static final int ERROR_CODE_INVALID_TOKEN = 4;

    private PPMessageSDK sdk;

    private IToken token;

    public BaseAPIRequest(PPMessageSDK sdk) {
        this.sdk = sdk;
        token = sdk.getToken();
    }

    public void post(String urlSegment, JSONObject requestParam, OnAPIRequestCompleted completedCallback) {
        String url = String.format(Locale.getDefault(), "%s/ppquery/PP_QUERY", sdk.getHostInfo().getHttpHost());
        final JSONObject param = new JSONObject();

        try {
            param.put("api_data", requestParam);
            param.put("api_url", urlSegment);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        post(url, param.toString(), completedCallback);
    }

    public void post(String urlSegment, String requestString, OnAPIRequestCompleted completedCallback) {

        if (token.getCachedToken() != null) {
            finalPost(urlSegment, requestString, completedCallback);
            return;
        }
        
        if (sdk.getHostInfo().getPpcomApiKey() != null && sdk.getHostInfo().getPpcomApiSecret() != null) {
            token.getApiToken(onGetAccessToken(urlSegment, requestString, completedCallback));
            return;
        }

        if (sdk.getHostInfo().getPpkefuApiKey() != null && sdk.getUserEmail() != null && sdk.getUserPassword() != null) {
            token.getApiToken(sdk.getUserEmail(), sdk.getUserPassword(), onGetAccessToken(urlSegment, requestString, completedCallback));
            return;
        }

        // directly return null token
        L.d("No api key, directly request: %s", requestString);
        finalPost(urlSegment, requestString, completedCallback);
    }

    @Override
    protected void setup(HttpURLConnection conn) {
        super.setup(conn);
        conn.addRequestProperty("Content-Type", "application/json;charset=utf-8");
        if (token.getCachedToken() != null) {
            conn.addRequestProperty("Authorization", String.format(Locale.getDefault(), "OAuth %s", token.getCachedToken()));
        }

    }

    private IToken.OnRequestTokenEvent onGetAccessToken(final String urlSegment, final String requestString, final OnAPIRequestCompleted completedCallback) {
        return new IToken.OnRequestTokenEvent() {
            @Override
            public void onGetToken(String accessToken) {
                L.d(GET_API_ACCESS_TOKEN_FORMAT, accessToken);
                finalPost(urlSegment, requestString, completedCallback);
            }
        };
    }

    private void finalPost(String url, String requestString, final OnAPIRequestCompleted completedCallback) {

        super.post(url, requestString, new OnHttpRequestCompleted() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response);
                } catch (JSONException e) {
                    L.e(e);
                }
                if (completedCallback != null) completedCallback.onResponse(jsonResponse);
            }

            @Override
            public void onCancelled() {
                if (completedCallback != null) completedCallback.onCancelled();
            }

            @Override
            public void onError(int errorCode) {
                if (errorCode == ERROR_CODE_INVALID_TOKEN || errorCode == ERROR_CODE_NO_TOKEN) {
                    token.clearCachedToken();
                }
                if (completedCallback != null) completedCallback.onError(errorCode);
            }

        });
    }
}
