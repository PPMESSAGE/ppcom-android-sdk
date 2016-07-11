package com.ppmessage.sdk.core.api;

import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Created by zoulinlin on 16/7/11.
 */
public interface PostObject {
    public void buildBody(HttpURLConnection connection);
    public boolean writeBody(HttpURLConnection connection, OutputStream bodyWriter);
}
