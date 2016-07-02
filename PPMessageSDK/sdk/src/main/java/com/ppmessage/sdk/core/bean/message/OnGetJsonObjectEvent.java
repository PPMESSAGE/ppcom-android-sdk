package com.ppmessage.sdk.core.bean.message;

import org.json.JSONObject;

/**
 * Created by ppmessage on 5/11/16.
 */
public interface OnGetJsonObjectEvent {

    void onCompleted(JSONObject jsonObject);

    void onError(Exception e);

}
