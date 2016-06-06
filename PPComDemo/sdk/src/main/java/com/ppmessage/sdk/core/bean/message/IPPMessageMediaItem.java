package com.ppmessage.sdk.core.bean.message;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppmessage on 5/6/16.
 */
public interface IPPMessageMediaItem {

    /**
     * MediaItem type
     *
     * @return
     */
    String getType();

    /**
     * Used for send message
     *
     * @return
     */
    void asyncGetAPIJsonObject(OnGetJsonObjectEvent event);

}
