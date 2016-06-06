package com.ppmessage.sdk.core.bean.message;

import com.ppmessage.sdk.core.L;

import org.json.JSONObject;

/**
 * Created by ppmessage on 5/10/16.
 */
public class PPMessageMediaItemFactory {

    private static final String LOG_MESSAGE_SUBTYPE_UNKNOWN = "[PPMessageMediaItemFactory] unknown message subtype:%s";

    public static IPPMessageMediaItem getMediaItem(String messageSubType, JSONObject mediaItemJsonObject) {
        if (messageSubType.equals(PPMessage.TYPE_TXT)) {
            return PPMessageTxtMediaItem.parse(mediaItemJsonObject);
        } else if (messageSubType.equals(PPMessage.TYPE_FILE)) {
            return PPMessageFileMediaItem.parse(mediaItemJsonObject);
        } else if (messageSubType.equals(PPMessage.TYPE_IMAGE)) {
            return PPMessageImageMediaItem.parse(mediaItemJsonObject);
        }

        L.w(LOG_MESSAGE_SUBTYPE_UNKNOWN, messageSubType);
        return null;
    }

}
