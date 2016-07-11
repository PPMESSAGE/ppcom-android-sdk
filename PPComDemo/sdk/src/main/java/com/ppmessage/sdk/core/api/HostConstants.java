package com.ppmessage.sdk.core.api;

/**
 * Created by ppmessage on 5/19/16.
 */
public final class HostConstants {

    private HostConstants() {}

    private static final boolean LOCAL_DEBUG = true;

    public static final String HOST;
    public static final String HTTP_HOST;
    public static final String WS_HOST;
    public static final String PPCOM_API_KEY;
    public static final String PPCOM_API_SECRET;
    public static final String PPKEFU_API_KEY;

    static {
        if (LOCAL_DEBUG) {
            HOST = "10.0.140.101:8945";
            HTTP_HOST = "http://" + HOST;
            WS_HOST = "ws://" + HOST + "/pcsocket/WS";

            PPCOM_API_KEY = "NDMxZjQ1NmZmMWZmY2I3NTZhZWMwZDliNjFmNTNkNWM1YjQ2YjZjNA==";
            PPCOM_API_SECRET = "YjA4YzIzNjMzOTkyMzdlNTE0ZDkyYzMyNjNmZWEyMzQwMDk2M2QyOA==";
            PPKEFU_API_KEY = "MTJkZDBmNDc0Yjg5NDIwY2RjM2M5ZjUyNGNiOTc3NGFhY2JlODllNA==";
        } else {
            HOST = "ppmessage.com";
            HTTP_HOST = "https://" + HOST;
            WS_HOST = "wss://" + HOST + "/pcsocket/WS";

            PPCOM_API_KEY = "M2E2OTRjZTQ5Mzk4ZWUxYzRjM2FlZDM2NmE4MjA4MzkzZjFjYWQyOA==";
            PPCOM_API_SECRET = "ZThmMTM1ZDM4ZmI2NjE1YWE0NWEwMGM3OGNkMzY5MzVjOTQ2MGU0NQ==";
            PPKEFU_API_KEY = "MWJkZWI3NDZhZmRiN2NjNDYzZDVmZGI3YTk2YjI5NzhhOWJhNzIyZA==";
        }
    }

}
