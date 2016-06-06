package com.ppmessage.sdk.core.api;

import org.json.JSONObject;

/**
 * Created by ppmessage on 5/5/16.
 */
public interface OnHttpRequestCompleted {

    void onResponse(String response);

    void onCancelled();

    void onError(int errorCode);

}
