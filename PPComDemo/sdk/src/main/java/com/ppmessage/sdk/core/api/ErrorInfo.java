package com.ppmessage.sdk.core.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ppmessage on 5/5/16.
 */
public final class ErrorInfo {

    private ErrorInfo() {}

    interface ErrorCode {

        /**
         * Http error
         */
        int HTTP_ERROR = 10001;

        /**
         * API error
         */
        int API_ERROR = 10002;

    }

    static Map<Integer, String> errorInfoMap = new HashMap<>();

    static {
        errorInfoMap.put(ErrorCode.HTTP_ERROR, "Http error");
        errorInfoMap.put(ErrorCode.API_ERROR, "Api error");
    }

    public static String getErrorString(int errorCode) {
        return errorInfoMap.get(errorCode);
    }

}
