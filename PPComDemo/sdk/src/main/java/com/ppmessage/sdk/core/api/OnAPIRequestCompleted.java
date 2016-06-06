package com.ppmessage.sdk.core.api;

import org.json.JSONObject;

/**
 * Created by ppmessage on 5/6/16.
 */
public interface OnAPIRequestCompleted {

    void onResponse(JSONObject jsonResponse);

    void onCancelled();

    void onError(int errorCode);

}
